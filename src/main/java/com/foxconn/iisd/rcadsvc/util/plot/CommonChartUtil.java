package com.foxconn.iisd.rcadsvc.util.plot;


import com.foxconn.iisd.rcadsvc.msg.PlotMsg;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.*;

public class CommonChartUtil extends AbstractChartUtil {

	private Map<String, Object> utilResult = new HashMap<String, Object>();

	//private List<String> colorList = new ArrayList<String>();
	public PlotMsg plotSetting = null;
	private double Q3;
	private double Q1;
	private Long caseID ;
	private Date updateTime = null;

	public CommonChartUtil(PlotMsg chartSetting, Long caseID, Date updateTime) {
		this.updateTime = updateTime;
		this.caseID =caseID;
		plotSetting = chartSetting;

		System.out.println("GenUtil=>CommonChartUtil");

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
		utilResult.put("groupField", plotSetting.getGroupField());
		utilResult.put("groupField2",plotSetting.getGroupField2());
		utilResult.put("groupFieldTag",plotSetting.getGroupFieldTag());
		utilResult.put("xField", plotSetting.getxField());
		utilResult.put("xTitle", plotSetting.getxTitle());
		utilResult.put("yField", plotSetting.getyField());
		utilResult.put("yTitle", plotSetting.getyTitle());
		utilResult.put("xStart", plotSetting.getxStart());
		utilResult.put("xEnd", plotSetting.getxEnd());
		utilResult.put("yStart", plotSetting.getyStart());
		utilResult.put("yEnd", plotSetting.getyEnd());
		utilResult.put("caption",plotSetting.getCaption());
		utilResult.put("updateTime",updateTime.toString());


//
		return null;
	}


}
