package com.foxconn.iisd.rcadsvc.util.plot;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;

import com.foxconn.iisd.rcadsvc.controller.CaseController;
import com.foxconn.iisd.rcadsvc.msg.PlotMsg;
import com.foxconn.iisd.rcadsvc.util.CaseFiledUtil;

public class LineChartUtil extends AbstractChartUtil {
	private static final Logger logger = LoggerFactory.getLogger(LineChartUtil.class);
	private Map<String, Object> utilResult = new HashMap<String, Object>();

	public PlotMsg plotSetting = null;
	public Date updateTime = null;
	private Long caseID ;

	@Autowired
	@Qualifier("mysqlJtl")
	private JdbcTemplate mysqlJtl;
	public LineChartUtil(PlotMsg chartSetting,Long caseID, JdbcTemplate mysqlJtl, Date updateTime) {
		this.updateTime = updateTime;
		this.plotSetting = chartSetting;
		this.caseID =caseID;
		this.mysqlJtl = mysqlJtl;
		System.out.println("GenUtil=>LineChartUtil");
	}

	
	@Override
	public JSONObject getChartJson() {
		JSONObject j = new JSONObject();
		j.put("chartType", "NDChart");// 圖的類型
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
		return 1;
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
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		NumberFormat nf = NumberFormat.getInstance();
	    nf.setMaximumFractionDigits(4);
	    
		String yfield = new CaseFiledUtil().getEncodeColumn(plotSetting.getyField(), this.caseID, mysqlJtl);

		Map<String, Object> dataMap = new HashMap<String, Object>();
		Map<String, Object> statisticMap = new HashMap<String, Object>();
		
		int sampleCount = 0;
		String dataTimeStart = "";
		String dataTimeEnd = "";
		
		String sql = " select count(*) as cnt"
	        	+ ", max(`scantime`) as dend "
	        	+ ", min(`scantime`) as dstart "
				+ " from `case_dataset_bigtable@"+this.caseID+"` "
				+ " where scantime is not null "
				+ " and `"+yfield+"` is not null";
		logger.info(sql);
		StringBuffer sqlCmd =  new StringBuffer("").append(sql);
		List<Map<String, Object>> resultList = mysqlJtl.queryForList(sqlCmd.toString());
	    if(resultList.size() > 0) {
	    	sampleCount 	= (resultList.get(0).get("cnt") 	== null? 0	:Integer.parseInt(String.valueOf(resultList.get(0).get("cnt"))));
			dataTimeStart 	= (resultList.get(0).get("dstart") 	== null? "":String.valueOf(resultList.get(0).get("dstart")));
			dataTimeEnd 	= (resultList.get(0).get("dend") 	== null? "":String.valueOf(resultList.get(0).get("dend")));
	    }
		
		//圖表基本信息
//		utilResult.put("caption", plotSetting.getCaption());
//		utilResult.put("updateTime", updateTime==null?"":sdf.format(updateTime));
//		utilResult.put("dataTimeStart", dataTimeStart);
//		utilResult.put("dataTimeEnd", dataTimeEnd);
		utilResult.put("updateTime", updateTime==null?"":sdf.format(updateTime));
		statisticMap.put("dataTimeStart", dataTimeStart);
		statisticMap.put("dataTimeEnd", dataTimeEnd);
		
		//圖表設定信息
//		utilResult.put("yField", yfield);
//		utilResult.put("yTitle", plotSetting.getxTitle());
//		utilResult.put("yObserve", plotSetting.getyObserve());
//		utilResult.put("groupField", plotSetting.getGroupField());
//		utilResult.put("groupFieldTag", plotSetting.getGroupFieldTag());
		
		//樣本信訊
		statisticMap.put("sampleCount", sampleCount);
	    
		if("byMonth".equalsIgnoreCase(plotSetting.getGroupField())){
			//by Month
			sql = " SELECT DATE_FORMAT(scantime,'%Y-%m') as scan_time "
					+ ", sum(if(`"+yfield+"@result` = 'fail', 1, 0))/count(*) as fail_rate "
					+ " FROM `case_dataset_bigtable@"+this.caseID+"` "
					+ " where scantime is not null "
					+ " GROUP BY YEAR(scantime), MONTH(scantime) "
					+ " order by YEAR(scantime), MONTH(scantime) ";
		}else{
			//by day
			sql = " SELECT DATE_FORMAT(scantime,'%Y-%m-%d') as scan_time "
					+ ", sum(if(`"+yfield+"@result` = 'fail', 1, 0))/count(*) as fail_rate "
					+ " FROM `case_dataset_bigtable@"+this.caseID+"` "
					+ " where scantime is not null "
					+ " GROUP BY YEAR(scantime), MONTH(scantime), Day(scantime) "
					+ " order by YEAR(scantime), MONTH(scantime), Day(scantime) ";
		}
		
	    //取得原始資料
		sqlCmd =  new StringBuffer("").append(sql);
		logger.info(sql);
		resultList = mysqlJtl.queryForList(sqlCmd.toString());
		List<String> scan_time_list = new ArrayList<String>();
		List<Double> fail_rate_list = new ArrayList<Double>();
	    for(Map<String, Object> result : resultList){
	    	//折線圖
	    	try {
	    		scan_time_list.add(result.get("scan_time") == null? "":String.valueOf(result.get("scan_time")));
				fail_rate_list.add(result.get("fail_rate") == null? null:nf.parse(nf.format(result.get("fail_rate"))).doubleValue());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }

	    List<Map<String, Object>> lineList = new ArrayList<Map<String, Object>>();
		Map<String, Object> lineMap = new HashMap<String, Object>();
	    try{
		    if("byMonth".equalsIgnoreCase(plotSetting.getGroupField())){
				//by Month
		    	DateFormat sdf2 = new SimpleDateFormat("yyyy-MM");
		    	if(scan_time_list.size()>0 && !"".equals(scan_time_list.get(0)) && !"".equals(scan_time_list.get(scan_time_list.size()-1))){
		    		Date beginDate = sdf2.parse(scan_time_list.get(0));
		    		Date endDate = sdf2.parse(scan_time_list.get(scan_time_list.size()-1));
		    		Calendar cal = Calendar.getInstance();  
		    		cal.setTime(beginDate);  
		    		boolean bContinue = true;  
		    		boolean isfound = false;
		    		while (bContinue) {  
		    			if (endDate.after(cal.getTime()) || endDate.compareTo(cal.getTime())==0) {  
		    				for(int i=0;i<scan_time_list.size();i++){
		    					String scan_time = scan_time_list.get(i);
		    					if(sdf2.format(cal.getTime()).equals(scan_time)){
		    						lineMap = new HashMap<String, Object>();
		    				    	lineMap.put("xValue", cal.getTimeInMillis());
		    				    	lineMap.put("yValue", fail_rate_list.get(i));
		    				    	lineList.add(lineMap);
		    				    	isfound = true;
		    				    	break;
		    					}
		    				}
		    				if(!isfound){
		    					lineMap = new HashMap<String, Object>();
	    				    	lineMap.put("xValue", cal.getTimeInMillis());
	    				    	lineMap.put("yValue", null);
	    				    	lineList.add(lineMap);
		    				}
		    				isfound = false;
		    			} else {  
		    				break;  
		    			} 
		    			cal.add(Calendar.MONTH, 1);  
		    		}
		    	}
			}else{
				//by day
				DateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		    	if(scan_time_list.size()>0 && !"".equals(scan_time_list.get(0)) && !"".equals(scan_time_list.get(scan_time_list.size()-1))){
		    		Date beginDate = sdf2.parse(scan_time_list.get(0));
		    		Date endDate = sdf2.parse(scan_time_list.get(scan_time_list.size()-1));
		    		Calendar cal = Calendar.getInstance();  
		    		cal.setTime(beginDate);  
		    		boolean bContinue = true;  
		    		boolean isfound = false;
		    		while (bContinue) {  
		    			if (endDate.after(cal.getTime()) || endDate.compareTo(cal.getTime())==0) {  
		    				for(int i=0;i<scan_time_list.size();i++){
		    					String scan_time = scan_time_list.get(i);
		    					if(sdf2.format(cal.getTime()).equals(scan_time)){
		    						lineMap = new HashMap<String, Object>();
		    				    	lineMap.put("xValue", cal.getTimeInMillis());
		    				    	lineMap.put("yValue", fail_rate_list.get(i));
		    				    	lineList.add(lineMap);
		    				    	isfound = true;
		    				    	break;
		    					}
		    				}
		    				if(!isfound){
		    					lineMap = new HashMap<String, Object>();
	    				    	lineMap.put("xValue", cal.getTimeInMillis());
	    				    	lineMap.put("yValue", null);
	    				    	lineList.add(lineMap);
		    				}
		    				isfound = false;
		    			} else {  
		    				break;  
		    			} 
		    			cal.add(Calendar.DAY_OF_MONTH, 1); 
		    		}
		    	}
			}
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
//	    
//	    for(Map<String, Object> result : resultList){
//	    	//折線圖
//	    	lineMap = new HashMap<String, Object>();
//	    	lineMap.put("xValue", result.get("scan_time") == null? "":String.valueOf(result.get("scan_time")));
//	    	lineMap.put("yValue", result.get("fail_rate") == null? null:Double.parseDouble(nf.format(result.get("fail_rate"))));
//	    	lineList.add(lineMap);
//	    }
		dataMap.put("line_data", lineList);
		utilResult.put("statistic", statisticMap);
	    utilResult.put("chart_data", dataMap);

		return null;
	}
}
