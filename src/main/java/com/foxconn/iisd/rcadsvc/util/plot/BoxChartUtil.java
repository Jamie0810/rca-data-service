package com.foxconn.iisd.rcadsvc.util.plot;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.foxconn.iisd.rcadsvc.msg.PlotMsg;
import com.foxconn.iisd.rcadsvc.util.CaseFiledUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.*;
import java.util.concurrent.atomic.DoubleAccumulator;

public class BoxChartUtil extends AbstractChartUtil {

    private Map<String, Object> utilResult = new HashMap<String, Object>();

    //private List<String> colorList = new ArrayList<String>();
    public PlotMsg plotSetting = null;
    private double Q3;
    private double Q1;
    private Long caseID;
    private Date updateTime = null;

    @Autowired
    @Qualifier("mysqlJtl")
    private JdbcTemplate mysqlJtl;
    private CaseFiledUtil casfiledUtil = new CaseFiledUtil();

    public BoxChartUtil(PlotMsg chartSetting, Long caseID, JdbcTemplate mysqlJtl, Date updateTime) {
        this.updateTime = updateTime;
        this.caseID = caseID;
        plotSetting = chartSetting;
        this.mysqlJtl = mysqlJtl;
        System.out.println("GenUtil=>BoxChartUtil");

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
        String yfield = casfiledUtil.getEncodeColumn(plotSetting.getyField(), plotSetting.getCaseId(), mysqlJtl);
        String groupfield = plotSetting.getGroupField();
        if (groupfield != null)
            groupfield = casfiledUtil.getEncodeColumn(plotSetting.getGroupField(), plotSetting.getCaseId(), mysqlJtl);
        String dataTimeStart = "";
        String dataTimeEnd = "";
        int sampleCount = 0;
        List<String> categories = new ArrayList<>();
        Map<String, List<Double>> groupMap = new HashMap<String, List<Double>>();
        List<Double> yAry = new ArrayList<Double>();
        Collections.sort(dataList, new Comparator<Object>() {
            public int compare(Object l1, Object l2) {
                ObjectMapper oMapper = new ObjectMapper();
                Map<String, Object> map1 = oMapper.convertValue(l1, Map.class);
                Map<String, Object> map2 = oMapper.convertValue(l2, Map.class);
                // 回傳值: -1 前者比後者小, 0 前者與後者相同, 1 前者比後者大
                if (Double.parseDouble(map1.get(yfield).toString()) < Double.parseDouble(map2.get(yfield).toString()))
                    return -1;
                else if (Double.parseDouble(map1.get(yfield).toString()) > Double.parseDouble(map2.get(yfield).toString()))
                    return 1;
                else
                    return 0;
            }
        });
        for (Map<String, Object> field : dataList) {
            if (groupMap.get(field.get(groupfield).toString()) == null) {
                yAry = new ArrayList<Double>();
                categories.add(field.get(groupfield).toString());
            } else {
                yAry = groupMap.get(field.get(groupfield).toString());
            }
            yAry.add(Double.parseDouble(field.get(yfield).toString()));
            groupMap.put(field.get(groupfield).toString(), yAry);

        }
        String sqldTime = "select max(`scantime`) as dend, min(`scantime`) as dstart   " +
                "FROM `case_dataset_bigtable@" + plotSetting.getCaseId() + "` " +
                "where `" + plotSetting.getyField() + "` is not null and `" + plotSetting.getyField() + "` != 'null'";
        List<Map<String, Object>> resultdTime = mysqlJtl.queryForList(sqldTime);
        dataTimeStart = (resultdTime.get(0).get("dstart") == null ? "" : String.valueOf(resultdTime.get(0).get("dstart")));
        dataTimeEnd = (resultdTime.get(0).get("dend") == null ? "" : String.valueOf(resultdTime.get(0).get("dend")));
        Map<String, Object> xyValue = new HashMap<>();
        boolean hasData = false;
        Map<String, Object> classCount = new HashMap<>();
        Map<String, Object> valueMap = new HashMap<>();
        for (String groupName : categories) {
            valueMap = new HashMap<>();
            yAry = groupMap.get(groupName);
            if (yAry.size() >= 5) {
                List<Double> data = new ArrayList<>();
                List<Double> outlier = new ArrayList<>();
                classCount.put(groupName, yAry.size());
                Double Q1 = getQ1(yAry, yAry.toArray().length);
                Double Q3 = getQ3(yAry, yAry.toArray().length);
                Double IQR = Q3 - Q1;
                data.add(Q1 - 1.5 * IQR); //min
                data.add(getQ1(yAry, yAry.toArray().length));
                data.add(getMedian(yAry, yAry.toArray().length));
                data.add(getQ3(yAry, yAry.toArray().length));
                data.add(Q3 + 1.5 * IQR); //max
                valueMap.put("value", data);
                for (double i : yAry) {
                    if ((i < Q1 - 1.5 * IQR) || (i > Q3 + 1.5 * IQR)) {
                        if (outlier.size() > 0) {
                            if (!outlier.contains(i))
                                outlier.add(i);
                        } else
                            outlier.add(i);
                    }
                }
                valueMap.put("outlier", outlier);
                xyValue.put(groupName, valueMap);
                sampleCount += yAry.size();
            }

        }
        String[] yFieldArr = plotSetting.getyField().split("@");
        String test_lower = null;
        String test_upper = null;
        if (yFieldArr.length >= 2) {
            String sql = "select test_upper,test_lower from `case` c left join data_set_station_item dssi on dssi.dss_id = c.data_set_id" +
                    " left join product_item_spec pis on dssi.product = pis.product COLLATE utf8mb4_unicode_ci " +
                    "and dssi.station = pis.station_name COLLATE utf8mb4_unicode_ci and dssi.item = pis.test_item COLLATE utf8mb4_unicode_ci " +
                    "where  c.id =" + caseID + " and dssi.item ='" + yFieldArr[1] + "' and dssi.station = '" + yFieldArr[0] + "' order by pis.test_version desc limit 1 ";
            List<Map<String, Object>> resultList = mysqlJtl.queryForList(sql);


            if (resultList.size() > 0) {
                test_lower = (resultList.get(0).get("test_lower") == null ? null : (String) resultList.get(0).get("test_lower"));
                test_upper = (resultList.get(0).get("test_upper") == null ? null : (String) resultList.get(0).get("test_upper"));
            }
        }

        //測試用
        //test_lower = "1";
        //test_upper = "15";
        //測試用
        boolean hasBoundary = false;
        Map<String, Object> boundaryValue = new HashMap<>();
        ;
        if (test_upper != null || test_lower != null) {
            boundaryValue.put("YtestUpper", test_upper);
            boundaryValue.put("YtestLower", test_lower);
        }
        Map<String, Object> dataValue = new HashMap<>();
        dataValue.put("group", xyValue);
        Map<String, Object> statistic = new HashMap<>();
        statistic.put("boundary", boundaryValue);
        statistic.put("dataTimeStart", dataTimeStart);
        statistic.put("dataTimeEnd", dataTimeEnd);
        statistic.put("sampleCount", sampleCount);
        statistic.put("groupSampleCount", classCount);
        utilResult.put("statistic", statistic);
        utilResult.put("chart_data", dataValue);
        utilResult.put("updateTime", updateTime.toString());
        return null;
    }


    private int median(List<Double> a,
                       int l, int r) {
        int n = r - l + 1;
        n = (n + 1) / 2 - 1;
        return n + l;
    }

    // Function to
// calculate IQR
    private Double getIQR(List<Double> a, int n) {
        // Index of median
        // of entire data
        int mid_index = median(a, 0, n);

        // Median of first half
        Q1 = a.get(median(a, 0,
                mid_index));

        // Median of second half
        Q3 = a.get(median(a,
                mid_index + 1, n));

        // IQR calculation
        return (Q3 - Q1);
    }

    private Double getQ1(List<Double> a, int n) {

        // Index of median
        // of entire data
        int mid_index = median(a, 0, n);

        // Median of first half
        Q1 = a.get(median(a, 0,
                mid_index));


        // IQR calculation
        return Q1;
    }

    private Double getQ3(List<Double> a, int n) {

        // Index of median
        // of entire data
        int mid_index = median(a, 0, n);


        // Median of second half
        Q3 = a.get(median(a,
                mid_index + 1, n));

        // IQR calculation
        return Q3;
    }

    private Double getMedian(List<Double> a, int n) {


        // Index of median
        // of entire data
        int mid_index = median(a, 0, n);


        // IQR calculation
        return a.get(mid_index);
    }
}
