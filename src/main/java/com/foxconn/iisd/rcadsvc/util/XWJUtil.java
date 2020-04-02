package com.foxconn.iisd.rcadsvc.util;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import java.math.BigDecimal;
import java.util.*;
import org.json.JSONObject;

public class XWJUtil {

    private static final char SQLCHAR = '`';


    public String convertJSONToCondition(JSONObject fieldObj) {
        String type = fieldObj.get("type").toString();
        String name = fieldObj.get("name").toString();
        String operator = fieldObj.get("operator").toString();
        String value = fieldObj.isNull("value") ? "" : fieldObj.get("value").toString();
        StringBuffer sb = new StringBuffer();

        if (type.equals("fixed")) {
            sb.append(addSQLCHAR(name));
        } else {
            sb.append(type).append("->>'$.\"").append(name.trim()).append("\"'");
        }

        switch (operator) {
            case "equals":
                sb.append(" = '").append(value).append("'");
                break;
            case "not_equals":
                sb.append(" <> '").append(value).append("'");
                break;
            case "contains":
                sb.append(" like '%").append(value).append("%'");
                break;
            case "over":
                sb.append(" > '").append(value).append("'");
                break;
            case "over_equals":
                sb.append(" >= '").append(value).append("'");
                break;
            case "less":
                sb.append(" < '").append(value).append("'");
                break;
            case "less_equals":
                sb.append(" <= '").append(value).append("'");
                break;
            case "starts_with":
                sb.append(" like '").append(value).append("%'");
                break;
            case "ends_with":
                sb.append(" like '%").append(value).append("'");
                break;
            case "missing":
                if(name.equals("scantime"))
                    sb.append(" is not null ");
                else
                    sb.append(" <> '' ");
                break;
            default:
                break;
        }
        return sb.toString();
    }
    public String convertJSONToFilterCondition(JSONObject fieldObj) {
        String dataType = fieldObj.get("dataType").toString();
        String type = fieldObj.get("type").toString();
        String name = fieldObj.get("name").toString();
        String operator = fieldObj.get("operator").toString();
        String value = fieldObj.isNull("value") ? "" : fieldObj.get("value").toString();
        StringBuffer sb = new StringBuffer();

        if (type.equals("fixed")) {
            sb.append(addSQLCHAR(name));
        } else {
            sb.append(type).append("->>'$.\"").append(name.trim()).append("\"'");
        }

        switch (operator) {
            case "equals":
                sb.append(" = '").append(value).append("'");
                break;
            case "not_equals":
                sb.append(" <> '").append(value).append("'");
                break;
            case "contains":
                sb.append(" like '%").append(value).append("%'");
                break;
            case "over":
                if( !dataType.equals("float") && !dataType.equals("int"))
                    sb.append(" > '").append(value).append("'");
                else
                    sb.append(" > ").append(value);
                break;
            case "over_equals":
                if( !dataType.equals("float") && !dataType.equals("int"))
                    sb.append(" >= '").append(value).append("'");
                else
                    sb.append(" >= ").append(value);
                break;
            case "less":
                if( !dataType.equals("float") && !dataType.equals("int"))
                    sb.append(" < '").append(value).append("'");
                else
                    sb.append(" < ").append(value);
                break;
            case "less_equals":
                if( !dataType.equals("float") && !dataType.equals("int"))
                    sb.append(" <= '").append(value).append("'");
                else
                    sb.append(" <= ").append(value);
                break;
            case "starts_with":
                sb.append(" like '").append(value).append("%'");
                break;
            case "ends_with":
                sb.append(" like '%").append(value).append("'");
                break;
            case "missing":
                if(name.equals("scantime"))
                    sb.append(" is not null ");
                else
                    sb.append(" <> '' ");
                break;
            default:
                break;
        }
        return sb.toString();
    }
    public String convertToCondition(Map<String, Object> fieldObj) {
        String type = fieldObj.get("type").toString();
        String name = fieldObj.get("name").toString();
        String operator = fieldObj.get("operator").toString();
        String value = fieldObj.get("value").toString();
        StringBuffer sb = new StringBuffer();

        if (type.equals("fixed")) {
            sb.append(addSQLCHAR(name));
        } else {
            sb.append(type).append("->>'$.\"").append(name.trim()).append("\"'");
        }

        switch (operator) {
            case "equals":
                sb.append(" = '").append(value).append("'");
                break;
            case "not_equals":
                sb.append(" <> '").append(value).append("'");
                break;
            case "contains":
                sb.append(" like '%").append(value).append("%'");
                break;
            case "over":
                sb.append(" > '").append(value).append("'");
                break;
            case "over_equals":
                sb.append(" >= '").append(value).append("'");
                break;
            case "less":
                sb.append(" < '").append(value).append("'");
                break;
            case "less_equals":
                sb.append(" <= '").append(value).append("'");
                break;
            case "starts_with":
                sb.append(" like '").append(value).append("%'");
                break;
            case "ends_with":
                sb.append(" like '%").append(value).append("'");
                break;
            case "missing":
                sb.append(" = '' ");
                break;
            default:
                break;
        }
        return sb.toString();
    }

    public  Map<String, List<String>> ListConvertMap(List<Map<String, Object>> list) {
        HashMap<String, List<String>> result = new HashMap<String, List<String>>();
        if (list.size() > 0) {
            Map<String, Object> firstMap = list.get(0);
            for (String key : firstMap.keySet()) {
                List<String> data = new ArrayList<String>();
                data.add(firstMap.get(key).toString());
                result.put(key, data);
            }

            for (int na = 1; na < list.size(); ++na) {
                Map<String, Object> map = list.get(na);
                for (String key : map.keySet()) {
                    List data = result.get(key);
                    data.add(map.get(key));
                    result.put(key, data);
                }
            }
        }
        return result;
    }

    public List<Map<String,Object>> jsonToListMap(JSONObject jo , String type){
        List<Map<String,Object>> resultList = new ArrayList<>();
        Map<String,Object> map = new HashMap<>();
        JSONObject listAry = jo.getJSONObject(type);
        for(String key : listAry.keySet()){
            map.put(key,listAry.get(key));
        }

        return resultList;
    }

    public Map<String, Map<String, String>> dataStatistic(List<Map<String, Object>> source, List<String> numericalFieldList) {
        Map<String, Map<String, String>> resultMsg = new HashMap<String, Map<String, String>>();

        Map<String, List<String>> dataMap = this.ListConvertMap(source);
        for (String field : dataMap.keySet()) {
            if (!field.contains("time") && !field.contains("sn")) {
                List<String> dataList = dataMap.get(field);
                Map<String, Integer> tmpMap = new HashMap<String, Integer>();
                boolean isNum = false;
                int missingCnt = 0;
                double[] d = new double[dataList.size()];
                //for(String value : dataList){
                for (int na = 0; na < dataList.size(); ++na) {
                    String value = dataList.get(na);
                    if (value == null || value.equals(""))
                        missingCnt++;
                    else {
                        //為數字且不為大表預設會產生的欄位,才進行數值收集(表示為測試數值)
                        if (value.startsWith(".")) value = "0" + value;//原始測項資料有的為.123,轉換會錯誤.所以要加0
                        else if (value.startsWith("-.")) value = "-0" + value.substring(1);//原始測項資料有的為-.123,轉換會錯誤.所以要加0
                        if (value.matches("-?\\d+(\\.\\d+)?") && numericalFieldList.contains(field)) {
                            d[na] = Double.parseDouble(value);
                            isNum = true;
                        }
                        if (tmpMap.get(value) == null)
                            tmpMap.put(value, new Integer(1));
                        else {
                            Integer v = tmpMap.get(value);
                            tmpMap.put(value, ++v);
                        }
                    }

                }

                Map<String, String> map = new HashMap<String, String>();
                if (isNum) {//min=0.167, max=80, average=29.881, deviation=14.431
                    double totalSum = 0;
                    Arrays.sort(d);
                    double[] temp = new double[dataList.size() - missingCnt];//temp為d 去掉missing value(null)
                    for (int na = 0, nb = 0; na < dataList.size(); na++) {
                        if (d[na] == 0 && nb < missingCnt)
                            nb++;
                        else {
                            totalSum += d[na];
                            temp[na - nb] = d[na];
                        }
                    }


                    double std = new StandardDeviation().evaluate(temp);
                    BigDecimal bigStd = new BigDecimal(std);
                    double mean = new Mean().evaluate(temp);
                    BigDecimal bigMean = new BigDecimal(mean);

                    map.put("type", "numerical");
                    map.put("temp_length", String.valueOf(temp.length));
                    map.put("total_Sum", String.valueOf(totalSum));
                    map.put("missing", String.valueOf(missingCnt));
                    map.put("total", String.valueOf(dataList.size()));
                    //map.put("min",String.valueOf(d[0-missingCnt]));//因為missCnt數量表示空值,double預設為0,所以需要offset(+missingCnt??)
                    //map.put("max",String.valueOf(d[d.length-1]));
                    map.put("min", String.valueOf(temp[0]));
                    map.put("max", String.valueOf(temp[temp.length - 1]));
                    map.put("average", bigMean.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                    map.put("deviation", bigStd.setScale(2, BigDecimal.ROUND_HALF_UP).toString());

                } else {//yes(3, 0.273), no(8, 0.727)
                    StringBuffer sb = new StringBuffer();
                    //sb.append("total count=").append(dataList.size()).append(", ");
                    for (String value : tmpMap.keySet()) {
                        int count = tmpMap.get(value);
                        double r = (double) count / dataList.size() * 100;
                        BigDecimal big = new BigDecimal(r);
                        String rate = big.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "%";
                        sb.append(value).append("( ").append(count).append(", ").append(rate).append(") ;");
                    }

                    map.put("type", "text");//polynominal
                    map.put("missing", String.valueOf(missingCnt));
                    map.put("total", String.valueOf(dataList.size()));
                    if (sb.length() > 0)
                        map.put("msg", sb.substring(0, sb.length() - 1));
                    else
                        map.put("msg", "");
                }
                resultMsg.put(field, map);
            }
        }

        return resultMsg;
    }

    public Object addSQLCHAR(Object input){
        if(input == null)
            return null;
        if(input instanceof String)
            if(input.equals(""))
                return input;
            else
                return new StringBuffer().append(SQLCHAR).append(input).append(SQLCHAR).toString();
        else{
            int index = 0;
            for(String s : (String[])input){
                if(!s.equals(""))
                    ((String[])input)[index] = new StringBuffer().append(SQLCHAR).append(s).append(SQLCHAR).toString();
                index++;
            }
            return input;
        }
    }

    public Map<String,String[]> convertToHeadAndSelectAry(List<String> selectList){
        int size = selectList.size();
        String[] headAry = new String[size];
        String[] selectAry = new String[size];

        int idx = 0;
        for (String f : selectList) {
            String str = f.split("as")[1].trim();
            if (str.indexOf('`') == -1)
                headAry[idx] = str;
            else
                headAry[idx] = str.replaceAll("`", "");
            selectAry[idx] = f;
            idx++;
        }

        Map<String,String[]> result = new HashMap<String,String[]>();
        result.put("select",selectAry);
        result.put("head",headAry);
        return result;
    }

    public Map<String, Object> newFieldObj(String type, String name, String... other) {
        Map<String, Object> fieldObj = new HashMap<>();
        fieldObj.put("type", type);
        fieldObj.put("name", name);
        for (String temp : other) {
            String[] str = temp.split(":");
            fieldObj.put(str[0], str[1]);
        }

        return fieldObj;
    }


}

