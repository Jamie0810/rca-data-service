package com.foxconn.iisd.rcadsvc.util.plot;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;

import com.foxconn.iisd.rcadsvc.msg.PlotMsg;
import com.foxconn.iisd.rcadsvc.util.CaseFiledUtil;

public class NDChartUtil extends AbstractChartUtil {
	private static final Logger logger = LoggerFactory.getLogger(NDChartUtil.class);
	private Map<String, Object> utilResult = new HashMap<String, Object>();
	private NormalDistribution nd;

	public PlotMsg plotSetting = null;
	public Date updateTime = null;
	private Long caseID ;

	@Autowired
	@Qualifier("mysqlJtl")
	private JdbcTemplate mysqlJtl;
	public NDChartUtil(PlotMsg chartSetting,Long caseID, JdbcTemplate mysqlJtl, Date updateTime) {
		this.updateTime = updateTime;
		this.plotSetting = chartSetting;
		this.caseID =caseID;
		this.mysqlJtl = mysqlJtl;
		System.out.println("GenUtil=>NDChartUtil");
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
	    
		String xfield = new CaseFiledUtil().getEncodeColumn(plotSetting.getxField(), this.caseID, mysqlJtl);

		Map<String, Object> dataMap = new HashMap<String, Object>();
		Map<String, Object> statisticMap = new HashMap<String, Object>();
		
		//取得資料筆數(count), 平均值(mean), 標準誤(stdev), 最大值(max), 最小值(min)
		int sampleCount = 0;
		double mean = 0.0;
		double stdev = 1.0;
		double max = 0.0;
		double min = 0.0;
		String dataTimeStart = "";
		String dataTimeEnd = "";
		
		String sql = " select count(*) as cnt"
				+ ", avg(CAST(`"+xfield+"` as DECIMAL(10,4))) as dmean"
				+ ", STDDEV_SAMP(CAST(`"+xfield+"` as DECIMAL(10,4))) as dstddev"
	        	+ ", max(CAST(`"+xfield+"` as DECIMAL(10,4))) as dmax"
	        	+ ", min(CAST(`"+xfield+"` as DECIMAL(10,4))) as dmin "
	        	+ ", max(`scantime`) as dend "
	        	+ ", min(`scantime`) as dstart "
				+ " from `case_dataset_bigtable@"+this.caseID+"` "
				+ " where `"+xfield+"` is not null ";
		if(plotSetting.getxStart()!=null){
			sql = sql + " and `"+xfield+"` >= " + plotSetting.getxStart();
		}
		if(plotSetting.getxEnd()!=null){
			sql = sql + " and `"+xfield+"` <= " + plotSetting.getxEnd();
		}
//				+ " and `"+xfield+"` between '"+plotSetting.getxStart()+"' and '"+plotSetting.getxEnd()+"'";
		StringBuffer sqlCmd =  new StringBuffer("").append(sql);
		List<Map<String, Object>> resultList = mysqlJtl.queryForList(sqlCmd.toString());
	    if(resultList.size() > 0) {
	    	sampleCount 	= (resultList.get(0).get("cnt") 	== null? 0	:Integer.parseInt(String.valueOf(resultList.get(0).get("cnt"))));
			mean 			= (resultList.get(0).get("dmean") 	== null? 0.0:Double.parseDouble(String.valueOf(resultList.get(0).get("dmean"))));
			stdev 			= (resultList.get(0).get("dstddev") == null? 0.0:Double.parseDouble(String.valueOf(resultList.get(0).get("dstddev"))));
			max 			= (resultList.get(0).get("dmax") 	== null? 0.0:Double.parseDouble(String.valueOf(resultList.get(0).get("dmax"))));
			min 			= (resultList.get(0).get("dmin") 	== null? 0.0:Double.parseDouble(String.valueOf(resultList.get(0).get("dmin"))));
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
//		utilResult.put("xField", xfield);
//		utilResult.put("xTitle", plotSetting.getxTitle());
//		utilResult.put("xStart", plotSetting.getxStart());
//		utilResult.put("xEnd", plotSetting.getxEnd());
		
		//樣本信訊
		statisticMap.put("sampleCount", sampleCount);
	    
		//計算組數
		int class_n = (int)Math.ceil(Math.sqrt((double)sampleCount))==0?1:(int)Math.ceil(Math.sqrt(sampleCount));
		if(class_n < 6){
			class_n = 6;
		}
		double classInterval = (max-min)/class_n;
		
		//統計分析
		try {
			statisticMap.put("mean", nf.parse(nf.format(mean)).doubleValue());
			statisticMap.put("stdev", nf.parse(nf.format(stdev)).doubleValue());
			statisticMap.put("max", max);
			statisticMap.put("min", min);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	    if(stdev!=0.0){
		    //取得資料上限, 資料下限
	    	String[] xfield_array = plotSetting.getxField().split("@");
	    	String station = "";
	    	String item = "";
	    	if(xfield_array.length==2){
	    		station = xfield_array[0];
	    		item = xfield_array[1];
	    	}
	  		sql = "select test_upper, test_lower from `case` c "
	  				+ " left join data_set_station_item dssi on dssi.dss_id = c.data_set_id "
	  				+ " left join product_item_spec pis on dssi.product = pis.product COLLATE utf8mb4_unicode_ci and dssi.station = pis.station_name COLLATE utf8mb4_unicode_ci and dssi.item = pis.test_item COLLATE utf8mb4_unicode_ci "
	  				+ " where  c.id =" +this.caseID+ " "
	  				+ " and dssi.station ='"+station+"' "
	  				+ " and dssi.item ='"+item+"' "
	  				+ " order by pis.test_version desc "
	  				+ " limit 1 ";
	  		resultList = mysqlJtl.queryForList(sql);
	
	  		String test_lower = "0.0";
	  		String test_upper = "0.0";
	
	  		if(resultList.size() > 0){
	  			test_lower = (resultList.get(0).get("test_lower") == null? null:String.valueOf(resultList.get(0).get("test_lower")));
	  			test_upper = (resultList.get(0).get("test_upper") == null? null:String.valueOf(resultList.get(0).get("test_upper")));
	  		}
		    
		    //取得原始資料
		    sql = " select `"+xfield+"` from `case_dataset_bigtable@"+this.caseID+"`"
		    		+ " where `"+xfield+"` is not null ";
		    if(plotSetting.getxStart()!=null){
				sql = sql + " and `"+xfield+"` >= " + plotSetting.getxStart();
			}
			if(plotSetting.getxEnd()!=null){
				sql = sql + " and `"+xfield+"` <= " + plotSetting.getxEnd();
			}
//		    		+ " and `"+xfield+"` between '"+plotSetting.getxStart()+"' and '"+plotSetting.getxEnd()+"'";
			sqlCmd =  new StringBuffer("").append(sql);
			resultList = mysqlJtl.queryForList(sqlCmd.toString());
			List<Double> data_list = new ArrayList<Double>();
		    for(Map<String, Object> result : resultList){
		    	data_list.add(result.get(xfield) == null? 0.0:Double.parseDouble(String.valueOf(result.get(xfield))));
		    }
			
			// 分組數列
			List<Double> binList = new ArrayList<Double>();
			double y1 = min - (0.5 * classInterval); // yi = y1 +(i-1) ∆
			//double ym = y1 + ((class_n-1) * classInterval); // ym = y1 +(m-1) ∆
			double yi = 0;

			for(int i = 1 ; i <= class_n + 2 ; ++i){
				yi = y1 + (i - 1) * classInterval;
				binList.add(yi);
			}

			//原始資料排序
		    Collections.sort(data_list);
			
			// 開始統計分組次數
			int binIndex = 0;
			int counter = 0;
			List<Double> classCenterList = new ArrayList<Double>();
			List<double[]> histormData = new ArrayList<double[]>();
			for(;binIndex<binList.size()-1;binIndex++){
				double binLower = binList.get(binIndex);
				double binUpper = binList.get(binIndex+1);
				for (Double dta : data_list) {
					if (dta >= binLower && dta < binUpper) {
						counter = counter + 1;
					}else if(dta>=binUpper){
						break;
					}
				}
				double[] dataPoint = new double[2];
				double classCenter = (binLower + binUpper)/2;
				try {
					dataPoint[0] = nf.parse(nf.format(classCenter)).doubleValue();
					dataPoint[1] = nf.parse(nf.format(counter)).doubleValue();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				histormData.add(dataPoint);
				classCenterList.add(classCenter);
				counter = 0;
			}

			nd = new NormalDistribution(mean, stdev);

//			//統計分析
//			dataMap.put("mean", Double.parseDouble(nf.format(mean)));
//			dataMap.put("stdev", Double.parseDouble(nf.format(stdev)));
//			dataMap.put("max", max);
//			dataMap.put("min", min);
			try {
				if(test_upper!=null && test_lower!=null){
					double CPU = Math.abs(Double.valueOf(test_upper)-mean)/stdev*3;
					double CPL = Math.abs(mean-Double.valueOf(test_lower))/stdev*3;
					double CPK = Math.min(CPU, CPL);
					statisticMap.put("CPK", nf.parse(nf.format(CPK)).doubleValue());
				}else if(test_upper!=null && test_lower==null){
					double CPU = Math.abs(Double.valueOf(test_upper)-mean)/stdev*3;
					double CPK = CPU;
					statisticMap.put("CPK", nf.parse(nf.format(CPK)).doubleValue());
				}else if(test_upper==null && test_lower!=null){
					double CPL = Math.abs(mean-Double.valueOf(test_lower))/stdev*3;
					double CPK = CPL;
					statisticMap.put("CPK", nf.parse(nf.format(CPK)).doubleValue());
				}
				statisticMap.put("range", max-min);
				statisticMap.put("class_n", class_n+1);
				statisticMap.put("classInterval", nf.parse(nf.format(classInterval)).doubleValue());
				Map<String, Object> boundaryValue =new HashMap<String, Object>();
				if(test_upper != null || test_lower != null){
					boundaryValue.put("testUpper", Double.parseDouble(test_upper));
					boundaryValue.put("testLower", Double.parseDouble(test_lower));
				}
				statisticMap.put("boundary", boundaryValue);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//實際資料
			List<Map<String, Object>> ndList = new ArrayList<Map<String, Object>>();
			Map<String, Object> ndMap = new HashMap<String, Object>();
			List<Map<String, Object>> barList = new ArrayList<Map<String, Object>>();
			Map<String, Object> barMap = new HashMap<String, Object>();
			for(double x : classCenterList){
				//常態分佈機率值
				try {
					ndMap = new HashMap<String, Object>();
					ndMap.put("xValue", nf.parse(nf.format(x)).doubleValue());
					ndMap.put("yValue", nf.parse(nf.format(getY_hat(x))).doubleValue());
					ndList.add(ndMap);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			for(double[] x : histormData){
				//直方圖次數
				barMap = new HashMap<String, Object>();
				barMap.put("xValue", x[0]);
				barMap.put("yValue", x[1]);
				barList.add(barMap);
			}
			dataMap.put("nd_data", ndList);
			dataMap.put("bar_data", barList);
			
//			dataMap.put("data_sample", sampleMap);
//			dataMap.put("data_analysis", analysisMap);
//			utilResult.put("data", dataMap);
	    }
	    utilResult.put("statistic", statisticMap);
	    utilResult.put("chart_data", dataMap);
//		utilResult.put("plot_basic", basicMap);
//		utilResult.put("plot_setting", settingMap);
//		utilResult.put("data_sample", sampleMap);
//		utilResult.put("data_analysis", analysisMap);
//		utilResult.put("data", dataMap);

		return null;
	}

	private double getY_hat(double x_value){
		return nd.density(x_value);
	}
}
