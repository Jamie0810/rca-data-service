package com.foxconn.iisd.rcadsvc.msg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import org.json.JSONObject;

public class PlotMsg{
	/*	
	mapIdx.put(1, "scatter plot");
	mapIdx.put(2, "box plot");
	mapIdx.put(3, "normail distribution");
	mapIdx.put(4, "bar plot");
	mapIdx.put(5, "line plot");
	mapIdx.put(6, "yield report");
	mapIdx.put(7, "mapping item name");
	mapIdx.put(8, "3d box plot");
	*/
	public PlotMsg(JSONObject j){
		//this.plotType = 1;//Integer.parseInt(j.get("plotType").toString());
		//this.caseId = Integer.parseInt(j.get("caseId").toString());
		//this.dataSetId = Integer.parseInt(j.get("dataSetId").toString());

		this.groupField = !j.has("groupField") ? null:  j.get("groupField").toString();
		this.groupFieldTag = !j.has("groupFieldTag") ? null :  j.get("groupFieldTag").toString();
		this.groupField2 = !j.has("groupField2") ? null :  j.get("groupField2").toString();
		this.xField = !j.has("xField") ? null :  j.get("xField").toString();
		this.yField = !j.has("yField") ? null :  j.get("yField").toString();
		this.xTitle = !j.has("xTitle") || j.get("xTitle").equals("") ? null :  j.get("xTitle").toString();
		this.yTitle = !j.has("yTitle") || j.get("yTitle").equals("") ? null :  j.get("yTitle").toString();
		this.yObserve = !j.has("yObserve") || j.get("yObserve").equals("") ? null :  j.get("yObserve").toString();

		this.xStart = !j.has("xStart") || j.get("xStart").equals("") ? null : Double.parseDouble(j.get("xStart").toString());
		this.xEnd = !j.has("xEnd") || j.get("xEnd").equals("") ? null : Double.parseDouble(j.get("xEnd").toString());
		this.yStart = !j.has("yStart") || j.get("yStart").equals("") ? null : Double.parseDouble(j.get("yStart").toString());
		this.yEnd = !j.has("yEnd") || j.get("yEnd").equals("") ? null :  Double.parseDouble(j.get("yEnd").toString());
		this.caption = !j.has("caption") ? "" :  j.get("caption").toString();
	}

	private Long caseId;
	private Integer dataSetId;
	private String operationUser="";
	private Integer plotType = 0;

	private String groupField = "";
	private String groupFieldTag ="";
	private String groupField2 ="";
	private String xField = "";
	private String yField = "";
	private String xTitle= "";
	private String yTitle = "";
	private String yObserve = "";
	private String caption = "";

	private Double xStart = new Double(0);
	private Double xEnd = new Double(0);
	private Double yStart = new Double(0);
	private Double yEnd = new Double(0);


	//private JSONObject json = new JSONObject();
	//private Map<Integer,String> mapIdx = new HashMap<Integer,String>();

	public Long getCaseId() {
		return caseId;
	}

	public void setCaseId(Long caseId) {
		this.caseId = caseId;
	}

	public Integer getDataSetId() {
		return dataSetId;
	}

	public void setDataSetId(Integer dataSetId) {
		this.dataSetId = dataSetId;
	}

	public String getOperationUser() {
		return operationUser;
	}

	public void setOperationUser(String operationUser) {
		this.operationUser = operationUser;
	}

	public Integer getPlotType() {
		return plotType;
	}

	public void setPlotType(Integer plotType) {
		this.plotType = plotType;
	}

	public String getxField() {
		return xField;
	}

	public void setxField(String xField) {
		this.xField = xField;
	}

	public String getyField() {
		return yField;
	}

	public void setyField(String yField) {
		this.yField = yField;
	}

	public String getGroupField() {
		return groupField;
	}

	public void setGroupField(String groupField) {
		this.groupField = groupField;
	}

	public String getxTitle() {
		return xTitle;
	}

	public void setxTitle(String xTitle) {
		this.xTitle = xTitle;
	}

	public String getyTitle() {
		return yTitle;
	}

	public void setyTitle(String yTitle) {
		this.yTitle = yTitle;
	}
	
	public String getyObserve() {
		return yObserve;
	}

	public void setyObserve(String yObserve) {
		this.yObserve = yObserve;
	}

	public Double getxStart() {
		return xStart;
	}

	public void setxStart(Double xStart) {
		this.xStart = xStart;
	}

	public Double getxEnd() {
		return xEnd;
	}

	public void setxEnd(Double xEnd) {
		this.xEnd = xEnd;
	}

	public Double getyStart() {
		return yStart;
	}

	public void setyStart(Double yStart) {
		this.yStart = yStart;
	}

	public Double getyEnd() {
		return yEnd;
	}

	public void setyEnd(Double yEnd) {
		this.yEnd = yEnd;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getGroupField2() {
		return groupField2;
	}

	public void setGroupField2(String groupField2) {
		this.groupField2 = groupField2;
	}

	public String getGroupFieldTag() {
		return groupFieldTag;
	}

	public void setGroupFieldTag(String groupFieldTag) {
		this.groupFieldTag = groupFieldTag;
	}
	//
//	public class Data{
//		private String typeD;
//		private String attribute;
//		public Data(String typeD,String attribute){
//			this.typeD = typeD;
//			this.attribute = attribute;
//		}
//		public String getTypeD() {
//			return typeD;
//		}
//		public void setTypeD(String typeD) {
//			this.typeD = typeD;
//		}
//		public String getAttribute() {
//			return attribute;
//		}
//		public void setAttribute(String attribute) {
//			this.attribute = attribute;
//		}
//
//	}
//
//	public class Spec{
//		private String typeS;
//		private String upper;
//		private String lower;
//		public Spec(String typeS,String upper,String lower){
//			this.typeS = typeS;
//			this.upper = upper;
//			this.lower = lower;
//		}
//		public String getTypeS() {
//			return typeS;
//		}
//		public void setTypeS(String typeS) {
//			this.typeS = typeS;
//		}
//		public String getUpper() {
//			return upper;
//		}
//		public void setUpper(String upper) {
//			this.upper = upper;
//		}
//		public String getLower() {
//			return lower;
//		}
//		public void setLower(String lower) {
//			this.lower = lower;
//		}
//
//	}
//
//	public class Range{
//		private String typeR;
//		private String start;
//		private String end;
//		public Range(String typeR,String start,String end){
//			this.typeR = typeR;
//			this.start = start;
//			this.end = end;
//		}
//		public String getTypeR() {
//			return typeR;
//		}
//		public void setTypeR(String typeR) {
//			this.typeR = typeR;
//		}
//		public String getStart() {
//			return start;
//		}
//		public void setStart(String start) {
//			this.start = start;
//		}
//		public String getEnd() {
//			return end;
//		}
//		public void setEnd(String end) {
//			this.end = end;
//		}
//
//	}
//
//	public class Check{
//		private String condition;
//		private String min;
//		private String equalMin;
//		private String max;
//		private String equalMax;
//		private List<String> compareList = new ArrayList<String>();
//		public Check(String condition,String equalMin,String min,String equalMax,String max,List<String> compareList){
//			this.condition = condition;
//			this.equalMin = equalMin;
//			this.min = min;
//			this.equalMax = equalMax;
//			this.max = max;
//			this.compareList = compareList;
//		}
//		public String getCondition() {
//			return condition;
//		}
//		public void setCondition(String condition) {
//			this.condition = condition;
//		}
//		public String getMin() {
//			return min;
//		}
//		public void setMin(String min) {
//			this.min = min;
//		}
//		public String getEqualMin() {
//			return equalMin;
//		}
//		public void setEqualMin(String equalMin) {
//			this.equalMin = equalMin;
//		}
//		public String getMax() {
//			return max;
//		}
//		public void setMax(String max) {
//			this.max = max;
//		}
//		public String getEqualMax() {
//			return equalMax;
//		}
//		public void setEqualMax(String equalMax) {
//			this.equalMax = equalMax;
//		}
//		public List<String> getCompareList() {
//			return compareList;
//		}
//		public void setCompareList(List<String> compareList) {
//			this.compareList = compareList;
//		}
//
//	}
//
//	List<PlotSetting> plotSettingList = new ArrayList<PlotSetting>();
//
//
//	public class PlotSetting{
//		private Long plotId;
//		private Integer plotType;
//		private String isDifferent;
//		private String plotJson;
//		private Integer isLog;
//		private List<Data> dataList = new ArrayList<Data>();
//		private List<Spec> specList = new ArrayList<Spec>();
//		private List<Range> rangeList = new ArrayList<Range>();
//		private List<Check> checkList = new ArrayList<Check>();
//		public PlotSetting(Long plotId,Integer plotType,String isDiff,String plotJson,Integer isLog,List data, List spec , List range,List check){
//			this.plotId = plotId;
//			this.plotType = plotType;
//			this.isDifferent = isDiff;
//			this.plotJson = plotJson;
//			this.isLog = isLog;
//			if(data!=null)this.setDataList(data);
//			if(spec!=null)this.setSpecList(spec);
//			if(range!=null)this.setRangeList(range);
//			if(check!=null)this.setCheckList(check);
//		}
//		public Long getPlotId(){
//			return plotId;
//		}
//		public void setPlotId(Long id){
//			this.plotId = id;
//		}
//		public Integer getPlotType() {
//			return plotType;
//		}
//		public void setPlotType(Integer plotType) {
//			this.plotType = plotType;
//		}
//		public List<Data> getDataList() {
//			return dataList;
//		}
//		public void setDataList(List<Map<String,Object>> dataList) {
//			for(Map<String,Object> map : dataList){
//				String typeD = map.get("typeD").toString();
//				String attribute = map.get("attribute").toString();
//				this.dataList.add(new Data(typeD,attribute));
//			}
//		}
//		public List<Spec> getSpecList() {
//			return specList;
//		}
//		public void setSpecList(List<Map<String,Object>> specList) {
//			for(Map<String,Object> map : specList){
//				String typeS = map.get("typeS").toString();
//				String upper = map.get("upper").toString();
//				String lower = map.get("lower").toString();
//				this.specList.add(new Spec(typeS,upper,lower));
//			}
//		}
//		public List<Range> getRangeList() {
//			return rangeList;
//		}
//		public void setRangeList(List<Map<String,Object>> rangeList) {
//			for(Map<String,Object> map : rangeList){
//				String typeR = map.get("typeR").toString();
//				String start = map.get("start").toString();
//				String end = map.get("end").toString();
//				this.rangeList.add(new Range(typeR,start,end));
//			}
//		}
//
//		public List<Check> getCheckList() {
//			return checkList;
//		}
//
//		public void setCheckList(List<Map<String,Object>> checkList) {
//			for(Map<String,Object> map : checkList){
//				String condition = map.get("condition").toString();
//				String min = map.get("min").toString();
//				String equalMin = map.get("equalMin").toString();
//				String max = map.get("max").toString();
//				String equalMax = map.get("equalMax").toString();
//				List compareList = (List)map.get("compareList");
//				this.checkList.add(new Check(condition,equalMin,min,equalMax,max,compareList));
//			}
//		}
//
//		public String getIsDifferent() {
//			return isDifferent;
//		}
//
//		public void setIsDifferent(String isDifferent) {
//			this.isDifferent = isDifferent;
//		}
//
//		public String getPlotJson() {
//			return this.plotJson;
//		}
//
//		public JSONObject getPlotJSONObject(){
//			return new JSONObject(plotJson);
//		}
//
//		public void setPlotJson(String plotJson) {
//			this.plotJson = plotJson;
//		}
//
//		public Integer getIsLog() {
//			return isLog;
//		}
//
//		public void setIsLog(Integer isLog) {
//			this.isLog = isLog;
//		}
//
//	}
//
//	public void setPlotSettingList(List<Map<String,Object>> list){
//		for(Map<String,Object> map : list){
//			Long id = map.get("id") == null ? 0 : Long.parseLong(map.get("id").toString());
//			Integer plotType = Integer.parseInt(map.get("plotType").toString());
//			String isDiff = map.get("isDiff").toString();
//			String plotJson = map.get("plotJson").toString();
//			Integer isLog = map.get("isLog") == null ? 0 : Integer.parseInt(map.get("isLog").toString());
//			List<Map<String,Object>> data = (List<Map<String,Object>>)map.get("data");
//			List<Map<String,Object>> spec = (List<Map<String,Object>>)map.get("spec");
//			List<Map<String,Object>> range = (List<Map<String,Object>>)map.get("range");
//			List<Map<String,Object>> check = (List<Map<String,Object>>)map.get("check");
//			this.plotSettingList.add(new PlotSetting(id,plotType,isDiff,plotJson,isLog,data,spec,range,check));
//		}
//	}
//	public List<PlotSetting> getPlotSettingList(){
//		return this.plotSettingList;
//	}
//
//
}
