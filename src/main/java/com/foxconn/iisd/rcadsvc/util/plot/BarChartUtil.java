package com.foxconn.iisd.rcadsvc.util.plot;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.foxconn.iisd.rcadsvc.util.CaseFiledUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;

import com.foxconn.iisd.rcadsvc.msg.BarFiledMsg;
import com.foxconn.iisd.rcadsvc.msg.PlotMsg;

public class BarChartUtil extends AbstractChartUtil {

	private Map<String, Object> utilResult = new HashMap<String, Object>();

	//private List<String> colorList = new ArrayList<String>();
	public PlotMsg plotSetting = null;
	private Date updateTime = null;
	private Long caseID ;

	@Autowired
	@Qualifier("mysqlJtl")
	private JdbcTemplate mysqlJtl;
	private CaseFiledUtil casfiledUtil = new CaseFiledUtil();
	public BarChartUtil(PlotMsg chartSetting, Date updateTime, JdbcTemplate mysqlJtl) {
		this.updateTime = updateTime;
		plotSetting = chartSetting;
		this.caseID = chartSetting.getCaseId();
		this.mysqlJtl = mysqlJtl;
		System.out.println("GenUtil=>BarChartUtil");

		// for(int na = 0 ; na < 10 ; na++){
		// Map<String,String> rowData = dataList.get(na);
		// StringBuffer sb = new StringBuffer();
		// for(String field : rowData.keySet()){
		// sb.append(field).append(":").append(rowData.get(field)).append(",");
		// }
		// System.out.println(na + "=>" + sb.toString());
		// }
		//colorList.add("rgba(119, 152, 191, .5)");
		//colorList.add("rgba(223, 83, 83, .5)");
	}

	
	@Override
	public JSONObject getChartJson() {
		JSONObject j = new JSONObject();
		j.put("chartType", "ScatterChart");// 圖的類型
		j.put("dataCount", "");// 資料數量
		j.put("dataResult", "");// 資料結果(amchart所需要的)
		j.put("outSpecCount", "");// 異常值數量
		j.put("chartTitle", "");// 圖的標題
		j.put("XAxisName", "");// X軸名稱
		j.put("YAxisName", "");// Y軸名稱
		j.put("XUpper", "");// X軸的Spec上限
		j.put("YUpper", "");// Y軸的Spec上限
		j.put("XLower", "");// X軸的Spec下限
		j.put("YLower", "");// Y軸的Spec下限
		j.put("XStartRange", "");// X軸的繪圖起始值
		j.put("YStartRange", "");// Y軸的繪圖起始值
		j.put("XStopRange", "");// X軸的繪圖結束值
		j.put("YStopRange", "");// Y軸的繪圖結束值
		j.put("max", "");// 最大值
		j.put("min", "");// 最小值
		j.put("mean", "");// 平均值
		j.put("R", "");// 相關係數
		j.put("std", "");// 標準差
		j.put("cpk", "");// CPK
		return j;
	}


	public Integer getChartType() {
		return 2;
	}

	@Override
	public List<Map<String, Object>> getListMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getResultObject() {
		return utilResult;
	}



	@Override
	public Object calculationData(List<Map<String, Object>> dataList) {
		List<BarFiledMsg> seriesList = new ArrayList<>();
		String yfield = casfiledUtil.getEncodeColumn(plotSetting.getyField(),plotSetting.getCaseId(),mysqlJtl);
		String groupfield = casfiledUtil.getEncodeColumn(plotSetting.getGroupField(),plotSetting.getCaseId(),mysqlJtl);;
		String groupfield2 = plotSetting.getGroupField2() == null? null: casfiledUtil.getEncodeColumn(plotSetting.getGroupField2(),plotSetting.getCaseId(),mysqlJtl);
		String yObserve = plotSetting.getyObserve();
		List<String> categories = new ArrayList<>();
		List<String> categories2 = new ArrayList<>();
		Map<String, Object> seriesdata = new HashMap<>();
		String dataTimeStart = "";
		String dataTimeEnd = "";
		int sampleCount = 0 ;
		String sqlSample = "select count(*) from `case_dataset_bigtable@"+this.caseID+"`";
		int total = mysqlJtl.queryForObject(sqlSample, java.lang.Integer.class);
		sampleCount = total;
		Map<String, Object> classCount = new HashMap<>();
		String sqldTime = "select max(`scantime`) as dend, min(`scantime`) as dstart   " +
				"FROM `case_dataset_bigtable@"+plotSetting.getCaseId()+"` " +
				"where `"+plotSetting.getyField()+"` is not null and `"+plotSetting.getyField()+"` != 'null'";
		List<Map<String, Object>> resultdTime = mysqlJtl.queryForList(sqldTime);
		dataTimeStart 	= (resultdTime.get(0).get("dstart") 	== null? "":String.valueOf(resultdTime.get(0).get("dstart")));
		dataTimeEnd 	= (resultdTime.get(0).get("dend") 	== null? "":String.valueOf(resultdTime.get(0).get("dend")));
		if(plotSetting.getGroupField2() == null){
			yfield ="fail_rate";
			Map<String,List<Double>> groupMap = new HashMap<String,List<Double>>();
			List<Double> yAry = new ArrayList<Double>();
			for(Map<String, Object> field : dataList){
				categories.add(field.get(groupfield).toString());
				yAry.add(Double.parseDouble(field.get(yfield).toString()));
			}
			classCount.put("",yAry.size());
			seriesdata.put("",yAry);
		}
		else{
			yfield ="fail_rate";
            List<String> group1List = new ArrayList<>();
            List<String> group2List = new ArrayList<>();
            Map<String,List<Double>> groupMap = new HashMap<String,List<Double>>();
            List<Double> yAry = new ArrayList<Double>();
            String preGroupfield2 = "" ;
            int groupSampleCount = 0 ;
            for(Map<String, Object> field : dataList) {
                if(!group1List.contains(field.get(groupfield).toString()))
                    group1List.add(field.get(groupfield).toString());
                if(!group2List.contains(field.get(groupfield2).toString())) {
					group2List.add(field.get(groupfield2).toString());
				}
            }
            String sql = "select if(`"+plotSetting.getGroupField2()+"` is null,'空白',`"+plotSetting.getGroupField2()+"`) as " +
					"`"+plotSetting.getGroupField2() +"`, count(*) as total";
            sql += " from `case_dataset_bigtable@"+this.caseID+"`";
            sql += " group by `"+ plotSetting.getGroupField2() +"`";
			List<Map<String, Object>> resultSampleCount = mysqlJtl.queryForList(sql);
			for(Map<String, Object> field : resultSampleCount)
				classCount.put(field.get(plotSetting.getGroupField2()).toString(),Integer.parseInt(field.get("total").toString()));

            boolean find = false ;
            for(String group1:group1List){
                for(String group2:group2List){
                    find = false ;
                    for(Map<String, Object> field : dataList){
                        if(!categories.contains(field.get(groupfield).toString()))
                            categories.add(field.get(groupfield).toString());
                        if(group2.equals(field.get(groupfield2).toString()) &&
                                group1.equals(field.get(groupfield).toString())) {
                            find = true;
                            if(groupMap.get(field.get(groupfield2).toString()) == null){
                                yAry = new ArrayList<Double>();
                                categories2.add(field.get(groupfield2).toString());
                            }
                            else{
                                yAry = groupMap.get(field.get(groupfield2).toString());
                            }
                            yAry.add(Double.parseDouble(field.get(yfield).toString()));
                            groupMap.put(field.get(groupfield2).toString(),yAry);
                        }
                    }
                    if(!find){
                        if(groupMap.get(group2) == null){
                            yAry = new ArrayList<Double>();
                            categories2.add(group2);
                        }
                        else{
                            yAry = groupMap.get(group2);
                        }
                        yAry.add(0.0);
                        groupMap.put(group2,yAry);
                    }
                }
            }
			for(String cat2: categories2){
				yAry = groupMap.get(cat2);
				seriesdata.put(cat2,yAry);
			}
		}
		String[] yFieldArr = plotSetting.getyField().split("@");
		String test_lower = null;
		String test_upper = null;
		if (yFieldArr.length >= 2) {

			String sql = "select test_upper,test_lower from `case` c left join data_set_station_item dssi on dssi.dss_id = c.data_set_id" +
					" left join product_item_spec pis on dssi.product = pis.product COLLATE utf8mb4_unicode_ci " +
					"and dssi.station = pis.station_name COLLATE utf8mb4_unicode_ci and dssi.item = pis.test_item COLLATE utf8mb4_unicode_ci " +
					"where  c.id =" + caseID + " and dssi.item ='" + yFieldArr[1] + "' and dssi.station = '"+yFieldArr[0]+"' order by pis.test_version desc limit 1 ";
			System.out.println(sql);
			List<Map<String, Object>> resultList = mysqlJtl.queryForList(sql);



			if (resultList.size() > 0) {
				test_lower = (resultList.get(0).get("test_lower") == null ? null : (String) resultList.get(0).get("test_lower"));
				test_upper = (resultList.get(0).get("test_upper") == null ? null : (String) resultList.get(0).get("test_upper"));
			}
		}
		yfield = plotSetting.getyField();
		Map<String,Object> boundaryValue =new HashMap<>();;
		if(test_upper != null || test_lower != null){
			boundaryValue.put("testUpper", test_upper);
			boundaryValue.put("testLower", test_lower);
		}
		Map<String, Object> data = new HashMap<>();
		data.put("categories",categories);
		data.put("series",seriesdata);
        Map<String,Object> statistic = new HashMap<>() ;
		statistic.put("boundary", boundaryValue);
		statistic.put("dataTimeStart", dataTimeStart);
		statistic.put("dataTimeEnd", dataTimeEnd);
		statistic.put("sampleCount", sampleCount);
		statistic.put("groupSampleCount",classCount);
        utilResult.put("statistic", statistic);
		utilResult.put("chart_data",data);
        utilResult.put("updateTime",updateTime.toString());


//
		return null;
	}




}
