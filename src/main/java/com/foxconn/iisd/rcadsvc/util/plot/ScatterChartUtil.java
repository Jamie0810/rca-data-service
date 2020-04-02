package com.foxconn.iisd.rcadsvc.util.plot;

import java.util.*;

import com.foxconn.iisd.rcadsvc.msg.PlotMsg;
import com.foxconn.iisd.rcadsvc.util.CaseFiledUtil;
import org.json.JSONObject;

import org.apache.commons.math3.stat.correlation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

public class ScatterChartUtil extends AbstractChartUtil {
    private Map<String, Object> utilResult = new HashMap<String, Object>();

    //private List<String> colorList = new ArrayList<String>();
    public PlotMsg plotSetting = null;
    public Date updateTime = null;

    @Autowired
    @Qualifier("mysqlJtl")
    private JdbcTemplate mysqlJtl;

    private CaseFiledUtil casfiledUtil = new CaseFiledUtil();

    public ScatterChartUtil(PlotMsg chartSetting, Date updateTime, JdbcTemplate mysqlJtl) {
        this.updateTime = updateTime;
        plotSetting = chartSetting;
        System.out.println("GenUtil=>ScatterChartUtil");
        this.mysqlJtl = mysqlJtl;

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

    private Map<String, Object> genXYObject(Object x, Object y) {
        Map<String, Object> obj = new HashMap<>();
        obj.put("x", x);
        obj.put("y", y);
        return obj;
    }

    @Override
    public Object calculationData(List<Map<String, Object>> dataList) {
        //String[] xname = plotSetting.getxField().split(",");
        //String[] yname = plotSetting.getxField().split(",");
        String xfield = casfiledUtil.getEncodeColumn(plotSetting.getxField(), plotSetting.getCaseId(), mysqlJtl);
        String yfield = casfiledUtil.getEncodeColumn(plotSetting.getyField(), plotSetting.getCaseId(), mysqlJtl);
        String groupfield = plotSetting.getGroupField() == null ? null : casfiledUtil.getEncodeColumn(plotSetting.getGroupField(), plotSetting.getCaseId(), mysqlJtl);
        int sampleCount = 0;
        String dataTimeStart = null;
        String dataTimeEnd = null;
        sampleCount = dataList.size();

        //Value整理、計算
        List<Float> xAry = new ArrayList<Float>();
        List<Float> yAry = new ArrayList<Float>();
        List<Map<String, Object>> xyAry = new ArrayList<Map<String, Object>>();
        String sqldTime = "select max(`scantime`) as dend, min(`scantime`) as dstart   " +
                "FROM `case_dataset_bigtable@" + plotSetting.getCaseId() + "` " +
                "where `" + plotSetting.getyField() + "` is not null and `" + plotSetting.getyField() + "` != 'null'";
        List<Map<String, Object>> resultdTime = mysqlJtl.queryForList(sqldTime);
        dataTimeStart = (resultdTime.get(0).get("dstart") == null ? "" : String.valueOf(resultdTime.get(0).get("dstart")));
        dataTimeEnd = (resultdTime.get(0).get("dend") == null ? "" : String.valueOf(resultdTime.get(0).get("dend")));
        if (groupfield == null) {
            Map<String, Object> groupMap = new HashMap<String, Object>();
            for (Map<String, Object> field : dataList) {

                float x = Float.parseFloat(field.get(xfield).toString());
                float y = Float.parseFloat(field.get(yfield).toString());
                Map<String, Object> xyObj = genXYObject(x, y);
                xAry.add(x);
                yAry.add(y);
                xyAry.add(xyObj);
            }
            groupMap.put("", xyAry);
            utilResult.put("chart_data", groupMap);
        } else {
            Map<String, Object> groupMap = new HashMap<String, Object>();
            for (Map<String, Object> field : dataList) {
                String groupName = (String) field.get(groupfield);
                float x = Float.parseFloat(field.get(xfield).toString());
                float y = Float.parseFloat(field.get(yfield).toString());
                Map<String, Object> xyObj = genXYObject(x, y);
                if (groupMap.get(groupName) == null) {
                    xyAry = new ArrayList<Map<String, Object>>();
                } else {
                    xyAry = (List) groupMap.get(groupName);
                }
                xyAry.add(xyObj);
                groupMap.put(groupName, xyAry);
            }

			/*
			//test data
			List<Map<String,Object>> testAry = new ArrayList<Map<String,Object>>();
			for(int ti = 0; ti < 500 ; ++ti){
				Random ran = new Random();
				int x = ran.nextInt(100)+1;
				int y = ran.nextInt(100)+1;
				Map<String,Object> xyObj = genXYObject(x,y);
				testAry.add(xyObj);
			}
			groupMap.put("demo_data",testAry);
			*/
            utilResult.put("chart_data", groupMap);
        }

        double[] x = new double[xAry.size()];
        double[] y = new double[yAry.size()];
        for (int na = 0; na < xAry.size(); ++na) {
            x[na] = xAry.get(na);
            y[na] = yAry.get(na);
        }
        double corrValue = 0;
        if (x.length > 0 && y.length > 0) {
            try {
                corrValue = new PearsonsCorrelation().correlation(x, y);
            } catch (Exception e) {
                corrValue = -1;//error
            }
        }
		String[] xFieldArr = plotSetting.getxField().split("@");
		String xtest_lower = null;
		String xtest_upper = null;
		if (xFieldArr.length >= 2) {
			String sql = "select test_upper,test_lower from `case` c left join data_set_station_item dssi on dssi.dss_id = c.data_set_id" +
					" left join product_item_spec pis on dssi.product = pis.product COLLATE utf8mb4_unicode_ci " +
					"and dssi.station = pis.station_name COLLATE utf8mb4_unicode_ci and dssi.item = pis.test_item COLLATE utf8mb4_unicode_ci " +
					"where  c.id =" + plotSetting.getCaseId() + " and dssi.item ='" + xFieldArr[1] + "' and dssi.station = '" + xFieldArr[0] + "' order by pis.test_version desc limit 1 ";
			List<Map<String, Object>> resultList = mysqlJtl.queryForList(sql);
			if (resultList.size() > 0) {
				xtest_lower = (resultList.get(0).get("test_lower") == null ? null : (String) resultList.get(0).get("test_lower"));
				xtest_upper = (resultList.get(0).get("test_upper") == null ? null : (String) resultList.get(0).get("test_upper"));
			}
		}
        String[] yFieldArr = plotSetting.getyField().split("@");
        String ytest_lower = null;
        String ytest_upper = null;
        if (yFieldArr.length >= 2) {
            String sql = "select test_upper,test_lower from `case` c left join data_set_station_item dssi on dssi.dss_id = c.data_set_id" +
                    " left join product_item_spec pis on dssi.product = pis.product COLLATE utf8mb4_unicode_ci " +
                    "and dssi.station = pis.station_name COLLATE utf8mb4_unicode_ci and dssi.item = pis.test_item COLLATE utf8mb4_unicode_ci " +
                    "where  c.id =" + plotSetting.getCaseId() + " and dssi.item ='" + yFieldArr[1] + "' and dssi.station = '" + yFieldArr[0] + "' order by pis.test_version desc limit 1 ";
            List<Map<String, Object>> resultList = mysqlJtl.queryForList(sql);
            if (resultList.size() > 0) {
                ytest_lower = (resultList.get(0).get("test_lower") == null ? null : (String) resultList.get(0).get("test_lower"));
                ytest_upper = (resultList.get(0).get("test_upper") == null ? null : (String) resultList.get(0).get("test_upper"));
            }
        }
		Map<String,Object> boundaryValue =new HashMap<>();;
		if(xtest_upper != null || xtest_lower != null){
			boundaryValue.put("XtestUpper", xtest_upper);
			boundaryValue.put("XtestLower", xtest_lower);
		}
		if(ytest_lower != null || ytest_upper != null){
			boundaryValue.put("YtestUpper", ytest_upper);
			boundaryValue.put("YtestLower", ytest_lower);
		}

        Map<String, Object> statistic = new HashMap<>();
		statistic.put("boundary", boundaryValue);
        statistic.put("Rsquare", corrValue);
        statistic.put("dataTimeStart", dataTimeStart);
        statistic.put("dataTimeEnd", dataTimeEnd);
        statistic.put("sampleCount", sampleCount);
        utilResult.put("statistic", statistic);

        utilResult.put("updateTime", updateTime.toString());


//		// TODO Auto-generated method stub
//		String xStartRange = "";
//		String xStopRange = "";
//		String yStartRange = "";
//		String yStopRange = "";
//		for(Range r : chartSetting.getRangeList()){
//			if(r.getTypeR().equals("x")){
//				xStartRange = r.getStart();
//				xStopRange = r.getEnd();
//			}else{//y
//				yStartRange = r.getStart();
//				yStopRange = r.getEnd();
//			}
//		}
//
//		List<String[]> fieldList = new ArrayList<String[]>();
//		String xField = "";
//		String yField = "";
//		for(Data d : chartSetting.getDataList()){
//			if(d.getTypeD().equals("xy")){
//				String[] xyValue = d.getAttribute().substring(1, d.getAttribute().length()-1).split(",");
//				//xField = xyValue[0].trim();//xyValue[0].indexOf("@")>-1 ? xyValue[0].split("@")[1] : xyValue[0];
//				//yField = xyValue[1].trim();//xyValue[1].indexOf("@")>-1 ? xyValue[1].split("@")[1] : xyValue[1];
//				fieldList.add(new String[]{xyValue[0].trim(),xyValue[1].trim()});
//			}
//		}
//
//
//		//fieldList.add(xField);
//		//fieldList.add(yField);
//		List<Map<String,Object>> dataProvoid = new ArrayList<Map<String,Object>>();
//		List<Map> outSpecData = new ArrayList<Map>();
//		Map<String,Object> specData = new HashMap<String,Object>();
//		List<double[]> groupCntList = new ArrayList<double[]>();
//		List<Double> xupperList = new ArrayList<Double>();
//		List<Double> xlowerList = new ArrayList<Double>();
//		List<Double> yupperList = new ArrayList<Double>();
//		List<Double> ylowerList = new ArrayList<Double>();
//
//		double[] corrValueList = new double[fieldList.size()];
//
//		for(int i=0;i < fieldList.size();++i){
//			String[] f = fieldList.get(i);
//			xField = f[0];yField=f[1];
//
//			double[] groupCnt = new double[]{0,0,0,0,0,0,0,0,0,0};//A,B,C,D,E,F,G,H,I,EmptyCnt
//			//取Spec
//			double xupper = 999999999;
//			double xlower = -999999999;
//			double yupper = 999999999;
//			double ylower = -999999999;
//
//			PdcaItemSpec xSpec = super.specMap.get(xField);
//			if(xSpec != null){
//				specData.put(xField,xSpec);
//				//xupper = Double.parseDouble(xSpec.getUpper());
//				//xlower = Double.parseDouble(xSpec.getLower());
//				xupper = super.isNumeric(xSpec.getUpper()) ? Double.parseDouble(xSpec.getUpper()) : 999999999;
//				xlower = super.isNumeric(xSpec.getLower()) ? Double.parseDouble(xSpec.getLower()) :-999999999;
//			}else {
//				String[] fieldAry = xField.split("-");
//				if(fieldAry[0].equals("UMP")){
//					String[] fai = fieldAry[1].split("_");
//					Object[] faiInfo = super.getLVSpecData(String.valueOf(fai[0]));
//					if(faiInfo == null || faiInfo.length < 3)
//						faiInfo = new Object[3];
//					String up = faiInfo[1] == null ? "" : String.valueOf(faiInfo[1]);
//					String lw = faiInfo[2] == null ? "" : String.valueOf(faiInfo[2]);
//					xupper =  super.isNumeric(up) ? Double.parseDouble(up) : 999999999;
//					xlower =  super.isNumeric(lw) ? Double.parseDouble(lw) :-999999999;
//					xSpec = new PdcaItemSpec();//回傳頁面用的暫存物件
//					xSpec.setUpper(up);
//					xSpec.setLower(lw);
//					specData.put(xField,xSpec);
//				}
//
//			}
//
//			PdcaItemSpec ySpec = super.specMap.get(yField);
//			if(ySpec != null){
//				specData.put(yField,ySpec);
//				//yupper = Double.parseDouble(ySpec.getUpper());
//				//ylower = Double.parseDouble(ySpec.getLower());
//				yupper = super.isNumeric(ySpec.getUpper()) ? Double.parseDouble(ySpec.getUpper()) : 999999999;
//				ylower = super.isNumeric(ySpec.getLower()) ? Double.parseDouble(ySpec.getLower()) :-999999999;
//			}else {
//				String[] fieldAry = yField.split("-");
//				if(fieldAry[0].equals("UMP")){
//					String[] fai = fieldAry[1].split("_");
//					Object[] faiInfo = super.getLVSpecData(String.valueOf(fai[0]));
//					if(faiInfo == null || faiInfo.length < 3)
//						faiInfo = new Object[3];
//					String up = faiInfo[1] == null ? "" : String.valueOf(faiInfo[1]);
//					String lw = faiInfo[2] == null ? "" : String.valueOf(faiInfo[2]);
//					yupper =  super.isNumeric(up) ? Double.parseDouble(up) : 999999999;
//					ylower =  super.isNumeric(lw) ? Double.parseDouble(lw) :-999999999;
//					ySpec = new PdcaItemSpec();//回傳頁面用的暫存物件
//					ySpec.setUpper(up);
//					ySpec.setLower(lw);
//					specData.put(yField,ySpec);
//				}
//			}
//
//			//Value整理、計算
//			List<Float> xAry = new ArrayList<Float>();
//			List<Float> yAry = new ArrayList<Float>();
//
//			Map<String,Object> dataObj = new HashMap<String,Object>();
//			dataObj.put("name", xField + ";" + yField);
//			//dataObj.put("color", colorList.get(i%2));
//			List<Map> vlist = new ArrayList<Map>();
////			if(this.getChartLib().toLowerCase().equals("highcharts")){
//				for(Map<String,String> map :dataList){
//					String sn = map.get("SerialNumber");
//					if(map.get(xField) != null && map.get(yField) != null
//							&& !map.get(xField).equals("") && !map.get(yField).equals("")
//							&& map.get(xField).matches("-?\\d+(\\.\\d+)?") && map.get(yField).matches("-?\\d+(\\.\\d+)?")){
//						float x = Float.parseFloat(map.get(xField));
//						float y = Float.parseFloat(map.get(yField));
//						double onRange = groupCnt[0];
//
//						groupCnt = scatterCount(groupCnt, xupper, xlower, yupper, ylower, x, y);
//						xAry.add(x);
//						yAry.add(y);
//
//						Map<String,Object> snObj = new HashMap<String,Object>();
//						snObj.put("x",x);
//						snObj.put("xName",xField);
//						snObj.put("y",y);
//						snObj.put("yName",yField);
//						snObj.put("sn",sn);
//						if(groupCnt[0] == onRange){//表示正常值沒有加一,為異常值
//							outSpecData.add(snObj);
//							//System.out.println(sn);
//						}else{
//							vlist.add(snObj);
//						}
//
//					}else{
//						groupCnt[9] += 1;
//					}
//				}
////			}
////			else{//amchart
////				double[] maxAxis = {0,0};//X,Y
////				double[] minAxis = {0,0};//X,Y
////				for(Map<String,String> map :dataList){
////					if(map.get(xField) != null && map.get(yField) != null
////							&& !map.get(xField).equals("") && !map.get(yField).equals("")
////							&& map.get(xField).matches("-?\\d+(\\.\\d+)?") && map.get(yField).matches("-?\\d+(\\.\\d+)?")){
////						float x = Float.parseFloat(map.get(xField));
////						float y = Float.parseFloat(map.get(yField));
////						String sn = map.get("SerialNumber");
////						Map<String,Object> snObj = new HashMap<String,Object>();
////						double onRange = groupCnt[0];
////						groupCnt = scatterCount(groupCnt, xupper, xlower, yupper, ylower, x, y);
////						xAry.add(x);
////						yAry.add(y);
////						if(groupCnt[0] == onRange){//表示正常值沒有加一,為異常值,要變色
////							snObj.put("*" + xField.replace(" ","_"),x);
////							snObj.put("*" + yField.replace(" ","_"),y);
////						}else{
////							snObj.put(xField.replace(" ","_"),x);
////							snObj.put(yField.replace(" ","_"),y);
////						}
////						snObj.put("SN",sn);
////						vlist.add(snObj);
////					}else{
////						groupCnt[9] += 1;
////					}
////				}
////			}
//
//			dataObj.put("data",vlist);
//			dataProvoid.add(dataObj);
//			groupCntList.add(groupCnt);
//			xupperList.add(xupper);
//			xlowerList.add(xlower);
//			yupperList.add(yupper);
//			ylowerList.add(ylower);
//
//			double[] x = new double[xAry.size()];
//			double[] y = new double[yAry.size()];
//			for(int na = 0 ; na < xAry.size();++na){
//				x[na] = xAry.get(na);
//				y[na] = yAry.get(na);
//			}
//			double corrValue = 0;
//			if(x.length > 0 && y.length>0){
//				try{
//					corrValue = new PearsonsCorrelation().correlation(x, y);
//				}catch(Exception e){
//					corrValue = -1;//error
//				}
//			}
//			corrValueList[i] = corrValue;
//		}
//
////		if(this.getChartLib().toLowerCase().equals("highcharts")){
//			//Hichart Json資料
//			Map<String,Object> hiChartFieldTip = new HashMap<String,Object>();
//			//hiChartFieldTip.put("headerFormat","<b>{series.name}</b><br>");
//			hiChartFieldTip.put("pointFormat", "SN:<b>{point.sn}</b><br>{point.xName}:{point.x}<br>{point.yName}:{point.y}");
//
//			Map<String,Object> dataObj = new HashMap<String,Object>();
//			dataObj.put("name", "Out of Spec");
//			dataObj.put("color", "rgb(223, 83, 83)");
//			dataObj.put("data", outSpecData);
//
//			dataProvoid.add(dataObj);
//			dataObj = new HashMap<String,Object>();
//			dataObj.put("name", "Hight Light");
//			dataObj.put("color", "rgb(0, 153, 51)");
//			dataObj.put("data", new ArrayList<Map>());
//			dataProvoid.add(dataObj);
//
//			utilResult.put("hiChartFieldTip",hiChartFieldTip);
//			utilResult.put("hichartObj",dataProvoid);
////		}else{
////			utilResult.put("dataResult",dataProvoid);//資料結果(amchart所需要的)(不回傳是因為值接用前端暫存即可繪圖)
////		}
//		/*
//		for(String key : super.specMap.keySet()){
//			PdcaItemSpec spec = (PdcaItemSpec)super.specMap.get(key);
//			String keyName = spec.getStation() + "@" + spec.getItem();
//			if(fieldList.contains(keyName))
//				specData.put(keyName,spec);
//			if(xField.equals(keyName)){
//				specData.put(keyName,spec);
//				xupper = Double.parseDouble(spec.getUpper());
//				xlower = Double.parseDouble(spec.getLower());
//			}else if(yField.equals(keyName)){
//				specData.put(keyName,spec);
//				yupper = Double.parseDouble(spec.getUpper());
//				ylower = Double.parseDouble(spec.getLower());
//			}
//		}
//		*/

//		utilResult.put("specData",specData);
//		utilResult.put("dataCount",dataList.size());
//		utilResult.put("groupCntList",groupCntList);//分組資訊
//		utilResult.put("outSpecCount", "");//異常值數量
//		utilResult.put("chartTitle", "Scatter Diagram");//圖的標題
//		utilResult.put("XAxisName", xField);//X軸名稱
//		utilResult.put("YAxisName", yField);//Y軸名稱
//		utilResult.put("fieldList",fieldList);//多組XY名稱String[]{x,y}
//		utilResult.put("XUpper", xupperList);//X軸的Spec上限
//		utilResult.put("YUpper", yupperList);//Y軸的Spec上限
//		utilResult.put("XLower", xlowerList);//X軸的Spec下限
//		utilResult.put("YLower", ylowerList);//Y軸的Spec下限
//		utilResult.put("XStartRange", xStartRange == "" ? xlowerList.get(0) : xStartRange);//X軸的繪圖起始值
//		utilResult.put("YStartRange", yStartRange == "" ? ylowerList.get(0) : yStartRange);//Y軸的繪圖起始值
//		utilResult.put("XStopRange", xStopRange == "" ? xupperList.get(0) : xStopRange);//X軸的繪圖結束值
//		utilResult.put("YStopRange", yStopRange == "" ? yupperList.get(0) : yStopRange);//Y軸的繪圖結束值
////		utilResult.put("XStartRange", xStartRange);//X軸的繪圖起始值
////		utilResult.put("YStartRange", yStartRange);//Y軸的繪圖起始值
////		utilResult.put("XStopRange", xStopRange);//X軸的繪圖結束值
////		utilResult.put("YStopRange", yStopRange);//Y軸的繪圖結束值
//
//
//		//utilResult.put("max", "");//最大值
//		//utilResult.put("min", "");//最小值
//		//utilResult.put("mean", "");//平均值
//		utilResult.put("R", corrValueList);//相關係數
//		//utilResult.put("std", "");//標準差
//		utilResult.put("cpk", "");//CPK
//
        return null;
    }

    private double[] scatterCount(double[] list, double ux, double lx, double uy, double ly, double x, double y) {
        // Float ux;
        // if(uxStr.equals("")) ux = 9999999;
        // else ux = Float.parseFloat(uxStr);
        // if(lxStr.equals("")) lx = -9999999;
        // else lx = Float.parseFloat(lxStr);
        // if(uyStr.equals("")) uy = 9999999;
        // else uy = Float.parseFloat(uyStr);
        // if(lyStr.equals("")) ly = -9999999;
        // else ly = Float.parseFloat(lyStr);
        // x = Float.parseFloat(x);
        // y = Float.parseFloat(y);
        // if(ux != 9999999 && lx != -9999999 && uy != 9999999 && ly !=
        // -9999999){//四條線分九格
        if (lx <= x && x <= ux && ly <= y && y <= uy)// A
            list[0] = list[0] + 1;
        else if (x < lx && ly <= y && y <= uy)// B
            list[1] = list[1] + 1;
        else if (x > ux && ly <= y && y <= uy)// C
            list[2] = list[2] + 1;
        else if (lx <= x && x <= ux && y > uy)// D
            list[3] = list[3] + 1;
        else if (lx <= x && x <= ux && y < ly)// E
            list[4] = list[4] + 1;
        else if (x < lx && y > uy)// F
            list[5] = list[5] + 1;
        else if (x > ux && y > uy)// G
            list[6] = list[6] + 1;
        else if (x < lx && y < ly)// H
            list[7] = list[7] + 1;
        else if (x > ux && y < ly)// I
            list[8] = list[8] + 1;
        return list;
    }

}
