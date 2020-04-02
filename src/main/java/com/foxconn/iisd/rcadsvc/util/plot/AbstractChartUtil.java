package com.foxconn.iisd.rcadsvc.util.plot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.foxconn.iisd.rcadsvc.msg.PlotMsg;

import org.json.JSONObject;

public abstract class AbstractChartUtil {

	public abstract Object getResultObject();
	public abstract JSONObject getChartJson();

	public abstract Object calculationData(List<Map<String, Object>> dataList);//List<Map<String,String>>

	public abstract List<Map<String,Object>> getListMap();
	public abstract Map<String,Object> getMap();

	public String[] colorAry = {"#B0DE09","#FCD202","#FF6600","#0044BB","#0000FF","#FF0000","#4B0082","#CD5C5C","#006400","#00008B"};
	public String[] bulletTypeAry = {"round", "square", "triangleDown", "bubble"};


	/*
	1=scatter plot
	2=Box plot
	3=normail distribution
	4=bar plot
	5=line plot
	 */
	
	public static AbstractChartUtil getChartUtil(PlotMsg plotSetting)
	{
		Date date = new Date();

		Integer chartType = plotSetting.getPlotType();
		//if (chartType.equals(1) )
		    //	return new ScatterChartUtil(plotSetting,date);
//		else if(chartType.equals(2))
//	    	return new BoxplotChartUtil(plotSetting);
//		else if(chartType.equals(3))
//	    	return new NDUtil(plotSetting);
//	    else if(chartType.equals(4))
//	    	return new BarChartUtil(plotSetting);
//	    else if(chartType.equals(5))
//	    	return new LineplotUtil(plotSetting);
//	    else if(chartType.equals(6))
//	    	return new YieldReportUtil(plotSetting);
//	    else if(chartType.equals(8))
//	    	return new IQCBoxplotChartUtil(plotSetting);
//	    else if(chartType.equals(7))
//	    	return new MappingItemUtil(dataList,plotSetting);
	   // else if (chartType.equals(9) )
	    	//return new ScatterChartUtil(plotSetting,date);
	    return null;
  }
	
	public static boolean isNumeric(String str)
	{
	  return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}
	
	//public static String getChartLib(){
		//String libType = EnvProperty.getString("JS_CHART_LIBRARY");
		//System.out.println("ChartsLib=" + libType);
		//return libType;
	//}
}
