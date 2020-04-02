package com.foxconn.iisd.rcadsvc.controller;

import com.foxconn.iisd.rcadsvc.domain.DataSetSetting;
import com.foxconn.iisd.rcadsvc.domain.auth.User;
import com.foxconn.iisd.rcadsvc.msg.*;
import com.foxconn.iisd.rcadsvc.repo.CodeTableRepository;
import com.foxconn.iisd.rcadsvc.repo.DataSetRepository;
import com.foxconn.iisd.rcadsvc.repo.UserRepository;
import com.foxconn.iisd.rcadsvc.service.CaseService;
import com.foxconn.iisd.rcadsvc.service.DataSetService;
import com.foxconn.iisd.rcadsvc.util.CodeUtils;
import com.foxconn.iisd.rcadsvc.util.FDJL2QueryResultComparator;
import com.foxconn.iisd.rcadsvc.util.FormatUtils;
import com.foxconn.iisd.rcadsvc.util.XWJUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import joinery.DataFrame;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Api(description = "DataSet服務")
@RestController
@RequestMapping("/qc")
public class QcController {

    private static final Logger logger = LoggerFactory.getLogger(QcController.class);

    private XWJUtil xwjUtil = new XWJUtil();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DataSetRepository dataSetRepository;

    @Autowired
    private DataSetService dataSetService;

    @Autowired
    private CaseService caseService;

    @Autowired
    @Qualifier("mysqlJtl")
    private JdbcTemplate mysqlJtl;

    @Autowired
    @Qualifier("mysqlDS")
    DataSource mysqlDS;

    private static final char SQLCHAR = '`';

    @Autowired
    private ExecutorService executorService;

    private static String QUERY_FACTORY = "select distinct factory "
            + " from ("
            + " SELECT factory FROM risk_test_station_sn "
            + " WHERE product = ? AND floor = ? and start_time BETWEEN ? AND ? "
            + " union all "
            + " SELECT factory FROM risk_test_station_sn_history "
            + " WHERE product = ? AND floor = ? and start_time BETWEEN ? AND ? "
            + " ) gg;";

    private static String QUERY_FACTORY_2_2_0 = "select distinct factory "
            + " from ("
            + " SELECT factory FROM risk_test_station_sn "
            + " WHERE product = ? AND floor = ? and line = ? and start_time BETWEEN ? AND ? "
            + " union all "
            + " SELECT factory FROM risk_test_station_sn_history "
            + " WHERE product = ? AND floor = ? and line = ? and start_time BETWEEN ? AND ? "
            + " ) gg;";

    private static String FAILURE_SYMPTON_ID_COLNAME = "symptom_id";

    private static String FAILURE_SYMPTON_COLNAME = "failure_sympton";

    private static String TEST_STATION_COLNAME = "teststation";

    private static String QUERY_PRODUCT_FLOOR = "SELECT product, floor FROM product_floor_line;";

    private static String QUERY_FDJ_PRODUCT_FLOOR_LINE = "select distinct product, floor, line FROM ("
            + " SELECT product, floor, line FROM risk_test_station_sn "
            + " WHERE start_time BETWEEN ? AND ? "
            + " union all "
            + " SELECT product, floor, line FROM risk_test_station_sn_history "
            + " WHERE start_time BETWEEN ? AND ? "
            + " ) gg "
            + " ORDER BY product, floor, line;";
    //private static String QUERY_FDJ_PRODUCT_FLOOR_LINE = "SELECT product, floor, line FROM product_floor_line order by product, floor, line;";

    private static String QUERY_WYJ_PRODUCT_FLOOR_LINE = "SELECT product, floor, line FROM product_floor_line;";

    private static String QUERY_WYJ_FLOOR_LINE = "SELECT distinct floor, line FROM product_floor_line;";

    private static String QUERY_WYJ_SHIFT_WITHIN_RANGE = "SELECT start_time,stop_time,description FROM shift" +
            " WHERE product = ? AND ((? between start_time and stop_time) or (start_time > stop_time and (? >= start_time or ? <= stop_time)));";

    private static String QUERY_WYJ_SHIFT_OUTSIDE_RANGE_FIRST = "SELECT start_time FROM shift WHERE product = ? AND ? > stop_time ORDER BY stop_time DESC;";

    private static String QUERY_WYJ_SHIFT_OUTSIDE_RANGE_SECOND = "SELECT start_time FROM shift WHERE product = ? ORDER BY stop_time DESC";

    private static String QUERY_TEST_DETAIL = "SELECT DISTINCT a.SN, a.riskcode as MACHINE, a.START_TIME, b.symptom as SYMPTOM, a.failure_desc as failureDesc, a.remark as remark "
            + " FROM ("
            + " select sn, riskcode, START_TIME, symptom_id, failure_desc, remark from risk_test_station_sn "
            + " WHERE product = ? AND FLOOR = ? AND IS_TRUE_FAIL= '1' AND STATION = ? AND symptom_id = ? AND START_TIME BETWEEN ? AND ? "
            + " union all "
            + " select sn, riskcode, START_TIME, symptom_id, failure_desc, remark from risk_test_station_sn_history "
            + " WHERE product = ? AND FLOOR = ? AND IS_TRUE_FAIL= '1' AND STATION = ? AND symptom_id = ? AND START_TIME BETWEEN ? AND ? "
            + " ) a "
            + " left join symptom b on a.symptom_id = b.symptom_id "
            + " ORDER BY a.START_TIME";

    private static String QUERY_TEST_DETAIL_2_2_0 = "SELECT DISTINCT a.SN, a.riskcode as MACHINE, a.START_TIME, b.symptom as SYMPTOM, a.failure_desc as failureDesc, a.remark as remark "
            + " FROM ("
            + " select sn, riskcode, START_TIME, symptom_id, failure_desc, remark from risk_test_station_sn "
            + " WHERE product = ? AND FLOOR = ? AND a.line = ? AND IS_TRUE_FAIL= '1' AND STATION = ? AND symptom_id = ? AND START_TIME BETWEEN ? AND ? "
            + " union all "
            + " select sn, riskcode, START_TIME, symptom_id, failure_desc, remark from risk_test_station_sn_history "
            + " WHERE product = ? AND FLOOR = ? AND a.line = ? AND IS_TRUE_FAIL= '1' AND STATION = ? AND symptom_id = ? AND START_TIME BETWEEN ? AND ? "
            + " ) a "
            + " left join symptom b on a.symptom_id = b.symptom_id "
            + " ORDER BY a.START_TIME";
    private static String QUERY_LEVEL_ONE_TEMPLATE = "call new_level_one('%s','%s','%s','%s',%s);";

    private static String QUERY_LEVEL_ONE_TEMPLATE_2_2_0 = "call new_level_one_2_2_0('%s','%s','%s','%s','%s',%s);";

    private static String QUERY_WYJ_LEVEL_ONE_TEMPLATE = "call wyj_level_one('%s','%s','%s','%s','%s',%s);";

    private static String QUERY_LEVEL_TWO_TEMPLATE = "call new_level_two('%s','%s','%s','%s','%s','%s');";

    private static String QUERY_LEVEL_TWO_TEMPLATE_2_2_0 = "call new_level_two_2_2_0('%s','%s','%s','%s','%s','%s','%s');";

    private static String QUERY_LEVEL_TWO_MAX_RANK_TEMPLATE = "call new_level_two_max_rank('%s','%s','%s','%s','%s','%s');";

    private static String QUERY_LEVEL_TWO_MAX_RANK_TEMPLATE_2_2_0 = "call new_level_two_max_rank_2_2_0('%s','%s','%s','%s','%s','%s','%s');";

    private static String QUERY_WYJ_LEVEL_TWO_MAX_RANK_TEMPLATE = "call wyj_level_two_max_rank('%s','%s','%s','%s','%s','%s','%s');";

    private static String QUERY_LEVEL_THREE_TEMPLATE = "call new_level_three('%s','%s','%s','%s','%s','%s','%s','%s');";

    private static String QUERY_LEVEL_THREE_TEMPLATE_2_2_0 = "call new_level_three_2_2_0('%s','%s','%s','%s','%s','%s','%s','%s','%s');";

    private static String QUERY_LEVEL_THREE_MAX_RANK_TEMPLATE = "call new_level_three_max_rank('%s','%s','%s','%s','%s','%s','%s','%s');";

    private static String QUERY_LEVEL_THREE_MAX_RANK_TEMPLATE_2_2_0 = "call new_level_three_max_rank_2_2_0('%s','%s','%s','%s','%s','%s','%s','%s','%s');";

    private static String QUERY_WYJ_LEVEL_THREE_MAX_RANK_TEMPLATE = "call wyj_level_three_max_rank('%s','%s','%s','%s','%s','%s','%s','%s','%s');";

    private static Long WAIT_REPAIR_COUNT_DEFAULT_VALUE = 0L;

    private static Integer WYJ_DEFAULT_RESULT_SIZE = 5;

    private static String WYJ_DEFAULT_TIMEZONE = "Asia/Shanghai";

    private static String COMMONALITY_ACCESS_COMMALITY_THRESHOLD = "0.5";

    private static String COMMONALITY_ACCESS_SIGNIFICANT_THRESHOLD = "2";

    private static String COMMONALITY_ACCESS_FAILQTY_THRESHOLD = "4";

    private static String TOTAL_FAILURE_COUNT = "SELECT count(DISTINCT SN) as totalFailureCount "
            + " FROM ("
            + " select sn, START_TIME from risk_test_station_sn "
            + " WHERE product = ? AND FLOOR = ? AND IS_TRUE_FAIL= '1' AND START_TIME BETWEEN ? AND ? "
            + " union all "
            + " select sn, START_TIME from risk_test_station_sn_history "
            + " WHERE product = ? AND FLOOR = ? AND IS_TRUE_FAIL= '1' AND START_TIME BETWEEN ? AND ? "
            + " ) gg "
            + " ORDER BY START_TIME;";

    private static String TOTAL_FAILURE_COUNT_2_2_0 = "SELECT count(DISTINCT SN) as totalFailureCount "
            + " FROM ("
            + " select sn, START_TIME from risk_test_station_sn "
            + " WHERE product = ? AND FLOOR = ? AND line = ? AND IS_TRUE_FAIL= '1' AND START_TIME BETWEEN ? AND ? "
            + " union all "
            + " select sn, START_TIME from risk_test_station_sn_history "
            + " WHERE product = ? AND FLOOR = ? AND line = ? AND IS_TRUE_FAIL= '1' AND START_TIME BETWEEN ? AND ? "
            + " ) gg "
            + " ORDER BY START_TIME;";

    private static String GET_PRODUCT_BY_LINE = "SELECT distinct product, floor, start_time "
            + " FROM risk_test_station_sn "
            + " WHERE line = ? "
            + " ORDER BY START_TIME DESC "
            + " limit 1;";
    private static String GET_NOW_SHIFT_DAY = "SELECT start_time, stop_time FROM shift "
            + "WHERE product = ? AND ? BETWEEN concat(?, start_time) AND concat(?, stop_time) "
            + "ORDER BY start_time DESC LIMIT 1;";

    private static String GET_NOW_SHIFT_NIGHT = "SELECT start_time, stop_time FROM shift "
            + "WHERE product = ? AND ? BETWEEN concat(?, start_time) AND concat(?, stop_time) "
            + "ORDER BY start_time DESC LIMIT 1;";

    private String QUERY_FA_TEST_TEMPLATE = "select * from ("
            + " SELECT id , sn, factory, product, floor, line, start_time, station, riskcode as machine, failure_desc,  "
            + " failure_sympton FROM risk_test_station_sn WHERE is_true_fail = '1'"
            + " union all "
            + " SELECT id , sn, factory, product, floor, line, start_time, station, riskcode as machine, failure_desc,  "
            + " failure_sympton FROM risk_test_station_sn_history WHERE is_true_fail = '1') gg"
            + " where 1=1 ";

    private static String QUERY_FA_TEST_ORDER = " ORDER BY start_time DESC";

    private static String QUERY_PRODUCT_LINE_STATION = "SELECT product, line, station FROM product_line_station;";

    private static String QUERY_TEST_SYMPTON = "select DISTINCT  failure_sympton from ("
            + " SELECT DISTINCT failure_sympton FROM risk_test_station_sn WHERE is_true_fail = '1' "
            + " union all "
            + " SELECT DISTINCT failure_sympton FROM risk_test_station_sn_history WHERE is_true_fail = '1' "
            + " ) gg "
            + " ORDER BY failure_sympton";

    private static String QUERY_PRODUCT_CODE = "SELECT DISTINCT product FROM product_floor_line;";

    @Autowired
    private CodeTableRepository codeTableRepository;

    private String escapeSingleQuote(String value){
    	String returnValue = value;
    	if(value != null){
    		returnValue = returnValue.replaceAll("'", "''");
    	}
    	return returnValue;
    }

    private Page<Map<String, Object>> pageDataSetData(String tableName, Pageable pageable, String whereSQL, String[] selectAry) {
        String rowCountSql = "SELECT count(1) AS row_count FROM " + tableName + whereSQL;
        int total = mysqlJtl.queryForObject(rowCountSql, Integer.class);

        String querySql = "SELECT " + String.join(",", selectAry) + " FROM " + tableName + whereSQL +
                " LIMIT " + pageable.getPageSize() + " " +
                " OFFSET " + pageable.getOffset();
        List<Map<String, Object>> resultList = mysqlJtl.queryForList(querySql.toString());
        return new PageImpl<>(resultList, pageable, total);
    }

    //產出大表select所需要的select欄位
    private List<String> genTableSelectField(String tbName){
        List<String> jsonFieldList = new ArrayList<String>();
        List<Map<String, Object>> fieldList = mysqlJtl.queryForList("SHOW COLUMNS FROM "+ tbName);
        List<String> selectField = new ArrayList<String>();
        for(Map<String, Object> row : fieldList){
            String field = row.get("Field").toString();
            String type = row.get("Type").toString();
            if(!type.equals("json")){
                StringBuffer sqlsb  = new StringBuffer();
                sqlsb.append("ifnull(").append(xwjUtil.addSQLCHAR(field)).append(",''").append(") as ").append(xwjUtil.addSQLCHAR(field));
                selectField.add(sqlsb.toString());
            }else{
                jsonFieldList.add(field);
            }
        }
        if(tbName.indexOf("data_set_bigtable@")> 0){
            List<String> jsonField = getJsonField(tbName,jsonFieldList);
            selectField.addAll(jsonField);
        }
        return selectField;
    }

    //將兩個JSON欄位的KEY取出並轉成mySQL的select語法
    private List<String> getJsonField(String tablename,List<String> fieldList){
        List<String> resultList = new ArrayList<String>();

        StringBuffer sqlSb = new StringBuffer();
        sqlSb.append("select 'a' as a ");
        for(String field : fieldList){
            sqlSb.append(",IFNULL(JSON_KEYS(").append(field).append("),'') as ").append(field);
        }
//        for(String field : fieldList){
//            sqlSb.append(",JSON_KEYS(").append(field).append(") as ").append(field);
//        }
        sqlSb.append(" from ").append(tablename).append(" LIMIT 1;");
        //String fieldSQL =" select JSON_KEYS(station_info) as station_info , JSON_KEYS(item_info) as item_info ,  JSON_KEYS(component_info) as component_info from " + tablename + " LIMIT 1;";

        List<Map<String, Object>> result = mysqlJtl.queryForList(sqlSb.toString());
        StringBuffer selectSQLSb = new StringBuffer();
        if(result.size()>0) {
            for(String field : fieldList){
                String tmp = result.get(0).get(field).toString();
                if(!tmp.trim().equals("")){
	                String[] infoAry = tmp.substring(1,tmp.length()-1).split(",");
	                for(String key : infoAry){
	                    StringBuffer tmpSb  = new StringBuffer();
	                    tmpSb.append("IFNULL( ").append(field).append("->>'$.").append(key.trim()).append("'").append(",'') as ").append(key.trim().replaceAll("\"","`"));
	                    resultList.add(tmpSb.toString());
	                }
                }
            }
//            //json_extract(station_info,  '$."TLEOL@station_id"' ) as "TLEOL@station_id"
//            String tmp = result.get(0).get("station_info").toString();
//            String[] stationinfo = tmp.substring(1,tmp.length()-1).split(",");
//            for(String station : stationinfo){
//                StringBuffer sqlsb  = new StringBuffer();
//                sqlsb.append("IFNULL( station_info->>'$.").append(station.trim()).append("'").append(",'') as ").append(station.trim().replaceAll("\"","`"));
//                //sqlsb.append("JSON_EXTRACT(station_info, '$.").append(station.trim()).append("'").append(") as ").append(station.trim().replaceAll("\"","`"));
//                resultList.add(sqlsb.toString());
//            }
//            //System.out.println("stationInfo=" + stationinfo);
//            tmp = result.get(0).get("item_info").toString();
//            String[] iteminfo = tmp.substring(1,tmp.length()-1).split(",");
//            for(String item : iteminfo){
//                StringBuffer sqlsb  = new StringBuffer();
//                //sqlsb.append("JSON_EXTRACT(item_info, '$.").append(item.trim()).append("'").append(") as ").append(item.trim().replaceAll("\"","`"));
//                sqlsb.append("IFNULL(  item_info->>'$.").append(item.trim()).append("'").append(",'') as ").append(item.trim().replaceAll("\"","`"));
//
//                resultList.add(sqlsb.toString());
//            }
//
//            tmp = result.get(0).get("component_info").toString();
//            String[] compinfo = tmp.substring(1,tmp.length()-1).split(",");
//            for(String component : compinfo){
//                StringBuffer sqlsb  = new StringBuffer();
//                //sqlsb.append("JSON_EXTRACT(item_info, '$.").append(item.trim()).append("'").append(") as ").append(item.trim().replaceAll("\"","`"));
//                sqlsb.append("IFNULL(  component_info->>'$.").append(component.trim()).append("'").append(",'') as ").append(component.trim().replaceAll("\"","`"));
//
//                resultList.add(sqlsb.toString());
//            }
            //System.out.println("iteminfo=" + iteminfo);
            return resultList;
        }else
            return resultList;
    }

    //以下為改版開始

    /**
     * @param
     * @return
     * @date 2019/5/26 下午14:12
     * @description 取所有Product資料
     */
    @ApiOperation("取所有Product資料")
    @GetMapping("/dataset/products")
    public @ResponseBody RestReturnMsg listProduct() {
        String sql = "select distinct product FROM product_floor_line";
        List<Map<String, Object>> fieldList = mysqlJtl.queryForList(sql);
        List<String> productList = new ArrayList<>();
        for( Map<String,Object> result:fieldList) {
            productList.add((String)result.get("product"));
        }
        return new RestReturnMsg(200, "get product list successfully" , productList);
    }

    /**
     * @param product
     * @param station
     * @return
     * @date 2019/5/26 下午14:12
     * @description  查詢該產品所有Station
     */
    @ApiOperation("查詢該產品所有Station ")
    @RequiresAuthentication
    @GetMapping("/dataset/test_items/stations")
    public @ResponseBody RestReturnMsg findStationByProduct(
            @RequestParam("product") String product,
            @RequestParam(name="station",required = false) String station) {
        List<String> stationList = new ArrayList<String>();
        StringBuffer sqlCmd =  new StringBuffer("SELECT product, station_name FROM product_station WHERE flag = 1 ");
        sqlCmd.append(String.format(" AND product = '%s'", product));
        if(station != null && !station.trim().equals(""))
            sqlCmd.append(String.format(" AND station_name like '%s'", '%'+station+'%'));
        sqlCmd.append(" order by station_name");

        List<Map<String, Object>> resultList = mysqlJtl.queryForList(sqlCmd.toString());
        for (Map<String, Object> row : resultList) {
            stationList.add((String) row.get("station_name"));
        }
        return new RestReturnMsg(200, "queryStationByProduct successfully" , stationList);
    }

    /**
     * @param product
     * @param station
     * @param item
     * @return
     * @date 2019/5/26 下午14:12
     * @description  查詢該產品所有Station下的item
     */
    @ApiOperation("查詢該產品的Station下的item ")
    @RequiresAuthentication
    @GetMapping("/dataset/test_items")
    public @ResponseBody RestReturnMsg findItemByProductStation(
            @RequestParam(name= "product") String product,
            @RequestParam(name="station",required = false) String station,
            @RequestParam(name="item",required = false) String item,
            @RequestParam(name="size",required = false) Integer size) {

        String headString = "product,station_name as station , test_item as item ,test_upper,test_lower,test_unit,test_version";
        String[] headAry = headString.split(",");
        StringBuffer sqlCmd =  new StringBuffer("SELECT ").append(headString).append(" FROM product_item_spec ");
        sqlCmd.append(String.format(" WHERE product = '%s'", product));
        if(station !=null && !station.trim().equals(""))
            sqlCmd.append(String.format(" AND station_name = '%s'", station));
        if(item!=null && !item.trim().equals(""))
            sqlCmd.append(String.format(" AND test_item like '%s'", '%'+item+'%'));
        sqlCmd.append(" order by station_name,test_item");

        if(size == null) size=0;
        if(size>0)
            sqlCmd.append(" LIMIT ").append(size);

        List<Map<String, Object>> resultList = mysqlJtl.queryForList(sqlCmd.toString());
        return new RestReturnMsg(200, "findItemByProductStation successfully" , resultList);
    }

    /**
     * @param product
     * @param component
     * @return
     * @date 2019/5/26 下午14:12
     * @description  查詢該產品所有關鍵物料的type資訊
     */
    @ApiOperation("查詢該產品所有關鍵物料的type資訊 ")
    @RequiresAuthentication
    @GetMapping("/dataset/components/types")
    public @ResponseBody RestReturnMsg findCompTypeByProduct(
            @RequestParam(name="product") String product,
            @RequestParam(name="component",required = false) String component) {
        List<String> stationList = new ArrayList<String>();
        StringBuffer sqlCmd =  new StringBuffer("SELECT DISTINCT component_type FROM config_component WHERE ")
                .append(String.format(" product = '%s'", product));
        if(component != null && !component.trim().equals(""))
            sqlCmd.append(String.format(" AND component like '%s'", '%'+component+'%'));
        sqlCmd.append(" order by component_type");
        List<Map<String, Object>> resultList = mysqlJtl.queryForList(sqlCmd.toString());
        for (Map<String, Object> row : resultList) {
            stationList.add((String) row.get("component_type"));
        }
        return new RestReturnMsg(200, "findComponentTypeByProduct successfully" , stationList);
    }

    /**
     * @param product
     * @param type
     * @param component
     * @return
     * @date 2019/5/26 下午14:12
     * @description  取得該產品的某類型關鍵物料
     */
    @ApiOperation("取得該產品的某類型關鍵物料 ")
    @RequiresAuthentication
    @GetMapping("/dataset/components")
    public @ResponseBody RestReturnMsg getCompByProductType(
            @RequestParam(name="product") String product,
            @RequestParam(name="type",required = false) String type,
            @RequestParam(name="component",required = false) String component,
            @RequestParam(name="size",required = false) Integer size) {

        StringBuffer sqlCmd =  new StringBuffer("SELECT DISTINCT component_type as partType ,component FROM config_component ");
        sqlCmd.append(String.format(" WHERE product = '%s'", product));
        if(type  != null && !type.trim().equals(""))
            sqlCmd.append(String.format(" AND component_type = '%s'", type));
        if(component != null && !component.trim().equals(""))
            sqlCmd.append(String.format(" AND component like '%s'", '%'+component+'%'));
        sqlCmd.append(" order by component_type,component");
        if(size == null) size=0;
        if(size>0)
            sqlCmd.append(" LIMIT ").append(size);

        List<Map<String, Object>> resultList = mysqlJtl.queryForList(sqlCmd.toString());

        return new RestReturnMsg(200, "getComponentInfoByProductTypeItem successfully" , resultList);
    }
    /**
     * @param
     * @return com.foxconn.iisd.rcadsvc.msg.RestReturnMsg
     * @date 2019/8/27 下午1:46
     * @description 產品, 樓層, 線體
     */

    @ApiOperation("取得產品, 樓層, 線體")
    @GetMapping("/defect/products/floors/lines/{start_timestamp}/{end_timestamp}")
    @RequiresAuthentication
    @RequiresPermissions(value = {"fdj_defect:Create", "fdj_defect:Update", "fdj_defect:Delete", "fdj_defect:View"
            ,"fdj:Create", "fdj:Update", "fdj:Delete", "fdj:View"}, logical = Logical.OR)
    public @ResponseBody
    RestReturnMsg getProdcutFloorLine_FDJ(
            @PathVariable long start_timestamp,
            @PathVariable long end_timestamp
    ) {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        String tsStr = "";
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String startTime = sdf.format(start_timestamp);
        String stopTime = sdf.format(end_timestamp);
        logger.info("==> getProdcutFloorLine_FDJ method");
        Map<String, Map<String, List<String>>> returnMap = new TreeMap<String, Map<String, List<String>>>();
        List<Map<String, Object>> resultList = mysqlJtl.queryForList(QUERY_FDJ_PRODUCT_FLOOR_LINE
                , startTime, stopTime
                , startTime, stopTime);
//        List<Map<String, Object>> resultList = mysqlJtl.queryForList(QUERY_FDJ_PRODUCT_FLOOR_LINE);
        for (Map<String, Object> row : resultList) {
            String product = (String) row.get("product");
            String floor = (String) row.get("floor");
            String line = (String) row.get("line");

            Map<String, List<String>> floorMap = null;
            List<String> lineList = null;
            if (returnMap.containsKey(product)) {
                floorMap = returnMap.get(product);
                if(floorMap.containsKey(floor)){
                    lineList = floorMap.get(floor);
                    if(!lineList.contains(line)){
                        lineList.add(line);
                    }
                }else{
                    lineList = new ArrayList<String>();
                    floorMap.put(floor, lineList);
                    lineList.add(line);
                }
            } else {
                floorMap = new TreeMap<String, List<String>>();
                returnMap.put(product, floorMap);
                if(floorMap.containsKey(floor)){
                    lineList = floorMap.get(floor);
                    if(!lineList.contains(line)){
                        lineList.add(line);
                    }
                }else{
                    lineList = new ArrayList<String>();
                    floorMap.put(floor, lineList);
                    lineList.add(line);
                }
            }

        }
        return new RestReturnMsg(200, "Query product_floor_line successfully!!!", returnMap);
    }

    /**
     * @param product
     * @param floor
     * @param startTime
     * @param stopTime
     * @param resultSize
     * @return com.foxconn.iisd.rcadsvc.msg.RestReturnMsg
     * @date 2019/3/12 下午4:50
     * @description 放大鏡(FDJ)第一層資訊, 不同層表示不同顆粒度大小,
     * 包含的資訊有: 排名 測試工站 不良徵狀 不良率 不良數量 異常持續時間 最高風險因子 風險來源 集中性 顯著性 產出比例
     */
    @ApiOperation("取得放大鏡(FDJ)第一層資訊")
    @GetMapping("/defect/dimension/symptoms")
    @RequiresAuthentication
    @RequiresPermissions(value={"fdj_defect:Create","fdj_defect:Update","fdj_defect:Delete","fdj_defect:View",
            "fdj:Create","fdj:Update","fdj:Delete","fdj:View"},logical= Logical.OR)
    public @ResponseBody
    RestReturnMsg levelOne(
            @RequestParam String product,
            @RequestParam String floor,
            String line,
            @RequestParam String startTime,
            @RequestParam String stopTime,
            @RequestParam Integer resultSize) {
        logger.info("==> product : " + product);
        logger.info("Running ExecutorService...");
        Integer returnCode = 200;
        String returnMsg = "Query Level one successfully!!!";

//        List<Map<String, Object>> resultList = mysqlJtl.queryForList(QUERY_PRODUCT_FLOOR);
        Map<String, Object> dataMap = new HashMap<String, Object>();
        List<Map<String, Object>> recordList = new ArrayList<Map<String, Object>>();
        Map<String, Object> infoMap = new HashMap<String, Object>();
        List<Map<String, Object>> queryFactoryList;
        if(line != null){
            queryFactoryList = mysqlJtl.queryForList(QUERY_FACTORY_2_2_0
                    , product, floor, line, startTime, stopTime
                    , product, floor, line, startTime, stopTime);
        }else{
            queryFactoryList = mysqlJtl.queryForList(QUERY_FACTORY
                    , product, floor, startTime, stopTime
                    , product, floor, startTime, stopTime);
        }

        String factory = null;
        for (Map<String, Object> row : queryFactoryList) {
            factory = (String) row.get("factory");
        }

        infoMap.put("product", product);
        infoMap.put("factory", factory);
        infoMap.put("floor", floor);
        infoMap.put("line", line);
        infoMap.put("startTime", startTime);
        infoMap.put("stopTime", stopTime);

        String dataStartTime = "";
        String dataEndTime = "";
        DateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        String sql = "select min(min_start_time) as dataStartTime, max(max_start_time) as dataEndTime "
                + " from ("
                + " select min(start_time) as min_start_time, max(start_time) as max_start_time from risk_test_station_sn "
                + " where product='"+product+"' "
                + " and floor='"+floor+"' "
                + " and start_time between '"+startTime+"' and '"+stopTime+"' "
                + " and 1=1 "
                + " union all "
                + " select min(start_time) as min_start_time, max(start_time) as max_start_time from risk_test_station_sn "
                + " where product='"+product+"' "
                + " and floor='"+floor+"' "
                + " and start_time between '"+startTime+"' and '"+stopTime+"' "
                + " and 1=1 "
                + " ) gg";
        if(line != null){
            sql = sql.replaceAll("1=1", "line='"+line+"'");
//    		sql += "and line='"+line+"'";
        }
        resultList = mysqlJtl.queryForList(sql);
        if(resultList.size()>0){
            dataStartTime = resultList.get(0).get("dataStartTime")==null?null:sdf.format(resultList.get(0).get("dataStartTime"));
            dataEndTime = resultList.get(0).get("dataEndTime")==null?null:sdf.format(resultList.get(0).get("dataEndTime"));
        }
        infoMap.put("dataStartTime", dataStartTime);
        infoMap.put("dataEndTime", dataEndTime);

        dataMap.put("infoData", infoMap);
        dataMap.put("recordList", recordList);

        Connection mysqlConn = null;
        try {
            mysqlConn = mysqlDS.getConnection();
            String lv1QryStr;
            if(line!=null){
                lv1QryStr = String.format(QUERY_LEVEL_ONE_TEMPLATE_2_2_0, product, floor, line, startTime, stopTime, resultSize);
            }else{
                lv1QryStr = String.format(QUERY_LEVEL_ONE_TEMPLATE, product, floor, startTime, stopTime, resultSize);
            }
            logger.info("\n" + lv1QryStr);
            if (lv1QryStr.contains("empty")) {
                logger.info("lv1QryStr is empty");
            } else {
                DataFrame<Object> df_lv1 = DataFrame.readSql(mysqlConn, lv1QryStr);
                List<Callable<Map<String, Object>>> callableTasks = new ArrayList<>();
                System.out.println(df_lv1);

                ListIterator<Map<Object, Object>> dfIter = df_lv1.itermap();
                while (dfIter.hasNext()) {
                    Map<String, Object> rowMap = new HashMap<String, Object>();
                    Map<Object, Object> lv1Row = dfIter.next();
                    Callable<Map<String, Object>> callableTask = () -> {
                        Connection conn = null;
                        try {
                            List<Map<String, Object>> queryFactoryList_count;
                            if(line!=null){
                                queryFactoryList_count = mysqlJtl.queryForList(TOTAL_FAILURE_COUNT_2_2_0
                                        , product, floor, line, startTime, stopTime
                                        , product, floor, line, startTime, stopTime);
                            }else{
                                queryFactoryList_count = mysqlJtl.queryForList(TOTAL_FAILURE_COUNT
                                        , product, floor, startTime, stopTime
                                        , product, floor, startTime, stopTime);
                            }
                            long totalFailureCount = 0l;
                            for (Map<String, Object> row : queryFactoryList_count) {
                                totalFailureCount = (long)row.get("totalFailureCount");
                            }
                            infoMap.put("totalFailureCount", totalFailureCount);

                            conn = mysqlDS.getConnection();
                            String failureSympton = (String) lv1Row.get(FAILURE_SYMPTON_COLNAME);
                            String Symptom_id = String.valueOf(lv1Row.get(FAILURE_SYMPTON_ID_COLNAME));
                            String testStation = (String) lv1Row.get(TEST_STATION_COLNAME);
                            String rank = (String) lv1Row.get("rank");
                            String failureRate = (String) lv1Row.get("failurerate");
                            String defectQty = (String) lv1Row.get("defectqty");
                            String failure_conthour = (String) lv1Row.get("failure_conthour");

                            rowMap.put("rank", rank);
                            rowMap.put("testStation", testStation);
                            rowMap.put("failureSymptom_display", CodeUtils.getCodeName(codeTableRepository, product, "1", failureSympton, ""));
                            rowMap.put("failureSymptom", Symptom_id);
                            rowMap.put("failRate", failureRate);
                            rowMap.put("defectQty", defectQty);
                            rowMap.put("failureContHour", failure_conthour);
                            rowMap.put("waitRepairCnt", WAIT_REPAIR_COUNT_DEFAULT_VALUE);

                            String lv2QryStr;
                            if(line!=null){
                                lv2QryStr = String.format(QUERY_LEVEL_TWO_MAX_RANK_TEMPLATE_2_2_0, product, floor, line, testStation, Symptom_id, startTime, stopTime);
                            }else{
                                lv2QryStr = String.format(QUERY_LEVEL_TWO_MAX_RANK_TEMPLATE, product, floor, testStation, Symptom_id, startTime, stopTime);
                            }
                            logger.info("\n" + lv2QryStr);
                            if (lv2QryStr.contains("empty")) {
                                logger.info("lv2QryStr is empty");
                            } else {
                                DataFrame<Object> df_lv2 = DataFrame.readSql(conn, lv2QryStr);
                                System.out.println(df_lv2);
                                if (df_lv2 == null || df_lv2.isEmpty()) {
                                    rowMap.put("commonalityAssess", CommonalityAccessType.NEGATIVE.getText());
                                    logger.info("df_lv2 is null or empty");
                                } else {
                                    rowMap.put("riskName_display", CodeUtils.getCodeName(codeTableRepository, product, "4", String.valueOf(df_lv2.col("riskname").get(0)), String.valueOf(df_lv2.col("risktype").get(0))));
                                    rowMap.put("riskName", df_lv2.col("riskname").get(0));
                                    rowMap.put("riskType", df_lv2.col("risktype").get(0));

                                    String lv3QryStr;
                                    if(line!=null){
                                        lv3QryStr = String.format(QUERY_LEVEL_THREE_MAX_RANK_TEMPLATE_2_2_0, product, floor, line, testStation, Symptom_id,
                                                df_lv2.col("riskname").get(0), df_lv2.col("risktype").get(0), startTime, stopTime);
                                    }else{
                                        lv3QryStr = String.format(QUERY_LEVEL_THREE_MAX_RANK_TEMPLATE, product, floor, testStation, Symptom_id,
                                                df_lv2.col("riskname").get(0), df_lv2.col("risktype").get(0), startTime, stopTime);
                                    }
                                    logger.info("\n" + "lv3QryStr : " + lv3QryStr);
                                    if (lv3QryStr.indexOf("empty") > -1) {
                                        logger.info("lv3QryStr is empty");
                                    } else {
                                        DataFrame<Object> df_lv3 = DataFrame.readSql(conn, lv3QryStr);
                                        System.out.println(df_lv3);
                                        if (df_lv3 == null || df_lv3.isEmpty()) {
                                            rowMap.put("commonalityAssess", CommonalityAccessType.NEGATIVE.getText());
                                            logger.info("df_lv3 is null or empty");
                                        } else {
                                            rowMap.put("commonalityAssess", CommonalityAccessType.NEGATIVE.getText());

                                            BigDecimal significant = new BigDecimal((String) df_lv3.col("significant").get(0));
                                            BigDecimal commonality = new BigDecimal((String) df_lv3.col("commonnality").get(0));
                                            BigInteger failQty = new BigInteger((String) df_lv3.col("failqty").get(0));
                                            Boolean common_lead = ((String) df_lv3.col("common_lead").get(0)).equals("1") ? true : false;

                                            if (significant != null) {
                                                if (commonality.compareTo(new BigDecimal(COMMONALITY_ACCESS_COMMALITY_THRESHOLD)) <= 0) {
                                                    logger.info(String.format("CommonalityAccess is negative because commonality %s is not greater than %s", commonality, COMMONALITY_ACCESS_COMMALITY_THRESHOLD));
                                                } else {
                                                    Boolean isSig = commonality.compareTo(new BigDecimal(COMMONALITY_ACCESS_SIGNIFICANT_THRESHOLD)) > 0;
                                                    if (!isSig && !common_lead) {
                                                        logger.info(String.format("Commonality: %s and CommonalityLeading: $s are both unqualified!", isSig, common_lead));
                                                    } else {
                                                        if (failQty.compareTo(new BigInteger(COMMONALITY_ACCESS_FAILQTY_THRESHOLD)) < 0) {
                                                            logger.info(String.format("failQty: %s is less than %s!", failQty, COMMONALITY_ACCESS_FAILQTY_THRESHOLD));
                                                            rowMap.put("commonalityAssess", CommonalityAccessType.DATALACK.getText());
                                                        } else {
                                                            rowMap.put("commonalityAssess", CommonalityAccessType.CONFIRMED.getText());
                                                        }
                                                    }
                                                }
                                            } else {
                                                logger.info("CommonalityAccess is negative because significant is null!");
                                            }

                                            rowMap.put("riskCode_display", CodeUtils.getCodeName(codeTableRepository, product, "2_3", String.valueOf(df_lv3.col("riskCode").get(0)), String.valueOf(df_lv2.col("risktype").get(0))));
                                            rowMap.put("riskCode", df_lv3.col("riskCode").get(0));
                                            rowMap.put("commonality", commonality);
                                            rowMap.put("throughputRatio", df_lv3.col("throughput_ratio").get(0));
                                            rowMap.put("significant", significant);
                                            rowMap.put("throughputQty", df_lv3.col("throughputqty").get(0));
                                            rowMap.put("outputQty", df_lv3.col("outputqty").get(0));
                                            rowMap.put("failQty", failQty);
                                            rowMap.put("accountFor", Double.parseDouble(defectQty)/totalFailureCount);

                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            logger.error("Error running query!", e);
                        } finally {
                            logger.info("run query finish");
                            if (conn != null) {
                                conn.close();
                            }
                        }
                        return rowMap;
                    };
                    callableTasks.add(callableTask);
                }
                List<Future<Map<String, Object>>> futureList = executorService.invokeAll(callableTasks);
                for (Future<Map<String, Object>> future : futureList) {
                    recordList.add(future.get());
                }
            }
        } catch (Exception e) {
            returnMsg = "Error!";
            logger.error(returnMsg, e);
            returnCode = 500;
        } finally {
            logger.info("run levelOne finish");
            if (mysqlConn != null) {
                try {
                    mysqlConn.close();
                } catch (Exception e) {
                    returnMsg = "Error!";
                    logger.error(returnMsg, e);
                    returnCode = 500;
                }
            }
        }

        return new RestReturnMsg(returnCode, returnMsg, dataMap);
    }
    /**
     * @param failureSymptom
     * @param testStation
     * @param startTime
     * @param stopTime
     * @return com.foxconn.iisd.rcadsvc.msg.RestReturnMsg
     * @date 2019/3/14 上午11:43
     * @description 測試細項
     */
    @ApiOperation("取得測試細項")
    @GetMapping("/defect/dimension/symptoms/records")
    @RequiresAuthentication
    public @ResponseBody
    RestReturnMsg testDetail(
            @RequestParam String product,
            @RequestParam String floor,
            String line,
            @RequestParam Integer failureSymptom,
            @RequestParam String testStation,
            @RequestParam String startTime,
            @RequestParam String stopTime) {
        Map<String, Object> dataMap = new HashMap<String, Object>();
        List<Map<String, Object>> defectSNList = new ArrayList<Map<String, Object>>();
        dataMap.put("defectSNList", defectSNList);

        List<Map<String, Object>> resultList;
        if(line!=null){
            resultList = mysqlJtl.queryForList(
                    QUERY_TEST_DETAIL_2_2_0
                    , product, floor, line, testStation, failureSymptom, Timestamp.valueOf(startTime), Timestamp.valueOf(stopTime)
                    , product, floor, line, testStation, failureSymptom, Timestamp.valueOf(startTime), Timestamp.valueOf(stopTime));
        }else{
            resultList = mysqlJtl.queryForList(
                    QUERY_TEST_DETAIL
                    , product, floor, testStation, failureSymptom, Timestamp.valueOf(startTime), Timestamp.valueOf(stopTime)
                    , product, floor, testStation, failureSymptom, Timestamp.valueOf(startTime), Timestamp.valueOf(stopTime));
        }
        for (Map<String, Object> row : resultList) {
            Map<String, Object> returnEntry = new HashMap<String, Object>();
            returnEntry.put("sn", row.get("sn"));
            returnEntry.put("machine", row.get("machine"));
            returnEntry.put("failureSymptom", row.get("symptom")==null?null:CodeUtils.getCodeName(codeTableRepository, product, "1", row.get("symptom").toString(), ""));
            returnEntry.put("testStartTime", FormatUtils.timestampToString((Timestamp) row.get("start_time")));
            returnEntry.put("failureDesc", row.get("failureDesc"));
            returnEntry.put("remark", row.get("remark"));
            defectSNList.add(returnEntry);
        }

        return new RestReturnMsg(200, "Query Test Detail successfully!!!", dataMap);
    }
    /**
     * @param product
     * @param floor
     * @param testStation
     * @param failureSymptom
     * @param startTime
     * @param stopTime
     * @return com.foxconn.iisd.rcadsvc.msg.RestReturnMsg
     * @date 2019/3/14 上午11:28
     * @description 放大鏡(FDJ)第二層資訊
     * 風險因子列表 包含的資訊有:
     * (1)製成 : 組裝工站 作業員	集中性 顯著性 產出比例 不良品連續性 風險評分
     * (2)設備 : 測試工站 設備編號 集中性 顯著性 產出比例 不良品連續性 風險評分
     * (3)物料 : 風險物料 物料廠商 集中性 顯著性 產出比例 不良品連續性 風險評分
     */
    @ApiOperation("取得放大鏡(FDJ)第二層資訊")
    @GetMapping("/defect/dimension/symptoms/risks")
    @RequiresAuthentication
    public @ResponseBody
    RestReturnMsg levelTwo(
            @RequestParam String product,
            @RequestParam String floor,
            String line,
            @RequestParam String testStation,
            @RequestParam String failureSymptom,
            @RequestParam String startTime,
            @RequestParam String stopTime) {
        Map<String, Object> dataMap = new HashMap<String, Object>();
        List<Map<String, Object>> assemblyList = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> partList = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> testList = new ArrayList<Map<String, Object>>();
        Connection mysqlConn = null;
        try {
            List<Map<String, Object>> queryFactoryList_count;
            if(line!=null){
                queryFactoryList_count = mysqlJtl.queryForList(TOTAL_FAILURE_COUNT_2_2_0
                        , product, floor, line, startTime, stopTime
                        , product, floor, line, startTime, stopTime);
            }else{
                queryFactoryList_count = mysqlJtl.queryForList(TOTAL_FAILURE_COUNT
                        , product, floor, startTime, stopTime
                        , product, floor, startTime, stopTime);
            }
            long totalFailureCount = 0l;
            for (Map<String, Object> row : queryFactoryList_count) {
                totalFailureCount = (long)row.get("totalFailureCount");
            }
            dataMap.put("totalFailureCount", totalFailureCount);

            mysqlConn = mysqlDS.getConnection();

            String lv2QryStr;
            if(line!=null){
                lv2QryStr = String.format(QUERY_LEVEL_TWO_TEMPLATE_2_2_0, product, floor, line, testStation, failureSymptom, startTime, stopTime);
            }else{
                lv2QryStr = String.format(QUERY_LEVEL_TWO_TEMPLATE, product, floor, testStation, failureSymptom, startTime, stopTime);
            }
            logger.info("\n" + lv2QryStr);
            DataFrame<Object> df_lv2 = DataFrame.readSql(mysqlConn, lv2QryStr);
            System.out.println(df_lv2);
            List<Callable<Map<String, Object>>> callableTasks = new ArrayList<>();

            ListIterator<Map<Object, Object>> dfIter = df_lv2.itermap();
            while (dfIter.hasNext()) {
                Map<String, Object> rowMap = new HashMap<String, Object>();
                Map<Object, Object> lv2Row = dfIter.next();
                Callable<Map<String, Object>> callableTask = () -> {
                    Connection conn = null;
                    try {
                        conn = mysqlDS.getConnection();
                        String riskType = (String) lv2Row.get("risktype");
                        String riskName = (String) lv2Row.get("riskname");

                        rowMap.put("riskName_display", CodeUtils.getCodeName(codeTableRepository, product, "4", riskName, riskType));
                        rowMap.put("riskName", riskName);
                        rowMap.put("riskType", riskType);

                        String lv3QryStr;
                        if(line!=null){
                            lv3QryStr = String.format(QUERY_LEVEL_THREE_MAX_RANK_TEMPLATE_2_2_0, product, floor, line, testStation, failureSymptom,
                                    riskName, riskType, startTime, stopTime);
                        }else{
                            lv3QryStr = String.format(QUERY_LEVEL_THREE_MAX_RANK_TEMPLATE, product, floor, testStation, failureSymptom,
                                    riskName, riskType, startTime, stopTime);
                        }
                        logger.info("\n" + lv3QryStr);
                        if (!lv3QryStr.contains("empty")) {
                            DataFrame<Object> df_lv3 = DataFrame.readSql(conn, lv3QryStr);
                            System.out.println(df_lv3);
                            if (df_lv3 == null || df_lv3.isEmpty()) {
                                logger.info("df_lv3 is null or empty");
                            } else {
                                rowMap.put("riskCode_display", CodeUtils.getCodeName(codeTableRepository, product, "2_3", String.valueOf(df_lv3.col("riskCode").get(0)), riskType));
                                rowMap.put("riskCode", df_lv3.col("riskCode").get(0));
                                rowMap.put("commonality", df_lv3.col("commonnality").get(0));
                                rowMap.put("throughputRatio", df_lv3.col("throughput_ratio").get(0));
                                rowMap.put("significant", df_lv3.col("significant").get(0));
                                rowMap.put("throughputQty", df_lv3.col("throughputqty").get(0));
                                rowMap.put("outputQty", df_lv3.col("outputqty").get(0));
                                rowMap.put("failureContRatio", df_lv3.col("failureContRation").get(0));
                                rowMap.put("riskPoint", df_lv3.col("riskpoint").get(0));
                                rowMap.put("failQty", df_lv3.col("failqty").get(0));
                                rowMap.put("defectQty", df_lv3.col("defectqty").get(0));
                            }

                        }
                    } catch (Exception e) {
                        logger.error("Error running query:" + QUERY_LEVEL_THREE_MAX_RANK_TEMPLATE, e);
                    } finally {
                        if (conn != null) {
                            conn.close();
                        }
                    }
                    return rowMap;
                };
                callableTasks.add(callableTask);
            }
            List<Future<Map<String, Object>>> futureList = executorService.invokeAll(callableTasks);
            for (Future<Map<String, Object>> future : futureList) {
                Map<String, Object> dataRowMap = future.get();
                String riskType = (String) dataRowMap.get("riskType");
                switch (RiskType.fromText(riskType)) {
                    case ASSEMBLY:
                        logger.info("riskType assembly");
                        assemblyList.add(dataRowMap);
                        break;
                    case PART:
                        logger.info("riskType part");
                        partList.add(dataRowMap);
                        break;
                    case TEST:
                        logger.info("riskType test");
                        testList.add(dataRowMap);
                        break;
                    default:
                        logger.info("riskType continue");
                        continue;
                }
            }

        } catch (SQLException e) {
            logger.error("SQLException");
            e.printStackTrace();
        } catch (Exception e) {
            logger.error("Exception");
            e.printStackTrace();
        } finally {
            logger.info("finally");
            if (mysqlConn != null) {
                try {
                    logger.info("connection close");
                    mysqlConn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        FDJL2QueryResultComparator comparator = new FDJL2QueryResultComparator();
        Collections.sort(assemblyList, comparator);
        Collections.sort(testList, comparator);
        Collections.sort(partList, comparator);

        dataMap.put("assemblyList", assemblyList);
        dataMap.put("stationList", testList);
        dataMap.put("dateCodeList", partList);

        return new RestReturnMsg(200, "Query Level two successfully!!!", dataMap);
    }
    /**
     * @param product
     * @param floor
     * @param testStation
     * @param failureSymptom
     * @param riskName
     * @param riskType
     * @param startTime
     * @param stopTime
     * @return com.foxconn.iisd.rcadsvc.msg.RestReturnMsg
     * @date 2019/3/14 上午11:42
     * @description 放大鏡(FDJ)第三層資訊
     * 包含的資訊有: 物料廠商 集中性 顯著性 產出比例 不良品連續性 風險評分
     */
    @ApiOperation("取得放大鏡(FDJ)第三層資訊")
    @GetMapping("/defect/dimension/symptoms/risks/source")
    @RequiresAuthentication
    public @ResponseBody
    RestReturnMsg levelThree(
            @RequestParam String product, String floor,
            String line,
            @RequestParam String testStation,
            @RequestParam String failureSymptom,
            @RequestParam String riskName,
            @RequestParam String riskType,
            @RequestParam String startTime,
            @RequestParam String stopTime) {
        Map<String, Object> dataMap = new HashMap<String, Object>();
        List<Map<String, Object>> recordList = new ArrayList<Map<String, Object>>();
        Connection mysqlConn = null;
        try {
            List<Map<String, Object>> queryFactoryList_count;
            if(line!=null){
                queryFactoryList_count = mysqlJtl.queryForList(TOTAL_FAILURE_COUNT_2_2_0
                        , product, floor, line, startTime, stopTime
                        , product, floor, line, startTime, stopTime);
            }else{
                queryFactoryList_count = mysqlJtl.queryForList(TOTAL_FAILURE_COUNT
                        , product, floor, startTime, stopTime
                        , product, floor, startTime, stopTime);
            }
            long totalFailureCount = 0l;
            for (Map<String, Object> row : queryFactoryList_count) {
                totalFailureCount = (long)row.get("totalFailureCount");
            }
            dataMap.put("totalFailureCount", totalFailureCount);

            mysqlConn = mysqlDS.getConnection();

            String lv3QryStr;
            if(line!=null){
                lv3QryStr = String.format(QUERY_LEVEL_THREE_TEMPLATE_2_2_0, product, floor, line, testStation, failureSymptom,
                        riskName, riskType, startTime, stopTime);
            }else{
                lv3QryStr = String.format(QUERY_LEVEL_THREE_TEMPLATE, product, floor, testStation, failureSymptom,
                        riskName, riskType, startTime, stopTime);
            }
            logger.info("\n" + lv3QryStr);
            if (lv3QryStr.contains("empty")) {
                logger.info("data is empty , must be skip");
                Map<String, Object> rowMap = new HashMap<String, Object>();
                recordList.add(rowMap);
            } else {
                DataFrame<Object> df_lv3 = DataFrame.readSql(mysqlConn, lv3QryStr);
                System.out.println(df_lv3);
                ListIterator<Map<Object, Object>> dfIter = df_lv3.itermap();
                while (dfIter.hasNext()) {
                    Map<String, Object> rowMap = new HashMap<String, Object>();
                    Map<Object, Object> lv3Row = dfIter.next();
                    rowMap.put("riskCode_display", CodeUtils.getCodeName(codeTableRepository, product, "2_3", String.valueOf(lv3Row.get("riskCode")), riskType));
                    rowMap.put("riskCode", lv3Row.get("riskCode"));
                    rowMap.put("commonality", lv3Row.get("commonnality"));
                    rowMap.put("throughputRatio", lv3Row.get("throughput_ratio"));
                    rowMap.put("significant", lv3Row.get("significant"));
                    rowMap.put("throughputQty", lv3Row.get("throughputqty"));
                    rowMap.put("outputQty", lv3Row.get("outputqty"));
                    rowMap.put("defectQty", lv3Row.get("defectqty"));
                    rowMap.put("failQty", lv3Row.get("failqty"));
                    rowMap.put("failureContRatio", lv3Row.get("failureContRation"));
                    rowMap.put("riskPoint", lv3Row.get("riskpoint"));

                    recordList.add(rowMap);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (mysqlConn != null) {
                try {
                    mysqlConn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        dataMap.put("recordList", recordList);

        return new RestReturnMsg(200, "Query Level three successfully!!!", dataMap);
    }
    /*
     * @author JasonLai
     * @date 2019/3/12 下午4:40
     * @description 取得望遠鏡(WYJ) (1)產品 (2)樓層 (3)線體 資訊
     */
    @ApiOperation("取得望遠鏡(WYJ) (1)產品 (2)樓層 (3)線體 資訊")
    @GetMapping("/defect/products/floors/lines")
    @RequiresAuthentication
   @RequiresPermissions(value={"wyj:Create","wyj:Update","wyj:Delete","wyj:View"},logical= Logical.OR)
    public RestReturnMsg getProdcutFloorLine() {

        logger.info("==> getProdcutFloorLine method");

        Map<String, Map<String, Set<Object>>> returnMap = new TreeMap<String, Map<String, Set<Object>>>();

        //查詢產品 樓層 線體 結果輸出只會有這三個欄位
        List<Map<String, Object>> resultList = mysqlJtl.queryForList(QUERY_WYJ_PRODUCT_FLOOR_LINE);

        logger.info("==> 產品 樓層 線體，總筆數有 : " + resultList);

        for (Map<String, Object> row : resultList) {
            String product = ((String) row.get("product")).trim();
            String floor = ((String) row.get("floor")).trim();
            String line = ((String) row.get("line")).trim();

            Map<String, Set<Object>> floorMap = null;
            Set<Object> lineSet = null;
            if (returnMap.containsKey(product)) {
                floorMap = returnMap.get(product);
                if (floorMap.containsKey(floor)) {
                    lineSet = floorMap.get(floor);
                } else {
                    lineSet = new TreeSet<Object>();
                    floorMap.put(floor, lineSet);
                }
                lineSet.add(line);
            } else {
                floorMap = new TreeMap<String, Set<Object>>();
                lineSet = new TreeSet<Object>();
                floorMap.put(floor, lineSet);
                returnMap.put(product, floorMap);
                lineSet.add(line);
            }
        }

        return new RestReturnMsg(200, "Query Test Detail successfully!!!", returnMap);
//        return returnMap;
    }

    /*
     * @description 取得望遠鏡(WYJ) 樓層 線體 資訊
     */
    @ApiOperation("取得望遠鏡(WYJ) 樓層 線體 資訊")
    @GetMapping("/wyj/api/v1/fetchFloorLine")
    @RequiresAuthentication
    public RestReturnMsg getFloorLine() {

        logger.info("==> getFloorLine method");

        //查詢樓層 線體 結果輸出只會有這兩個欄位
        Map<String, Set<Object>> returnMap = new TreeMap<String, Set<Object>>();
        List<Map<String, Object>> resultList = mysqlJtl.queryForList(QUERY_WYJ_FLOOR_LINE);

        logger.info("==> 樓層 線體，總筆數有 : " + resultList);

        for (Map<String, Object> row : resultList) {
            String floor = (String) row.get("floor");
            String line = (String) row.get("line");

            Set<Object> lineSet = null;
            if (returnMap.containsKey(floor)) {
                lineSet = returnMap.get(floor);
            } else {
                lineSet = new TreeSet<Object>();
                returnMap.put(floor, lineSet);
            }
            lineSet.add(line);
        }

        return new RestReturnMsg(200, "Query getFloorLine successfully!!!", returnMap);
//        return returnMap;
    }

    /**
     * @param product
     * @param floor
     * @param line
     * @param startTimeStr
     * @description 取得望遠鏡(WYJ) top 5 yield資訊
     */
    @ApiOperation("取得望遠鏡(WYJ) top 5 yield資訊")
    @GetMapping("/defect/statistic/yield")
    @RequiresAuthentication
    public RestReturnMsg fetchFakeYields(
            String product, String floor,
            @RequestParam String line, String startTimeStr, String stopTimeStr) {

        String startTime = null;
        String stopTime = null;
        String tmpFloor = "";
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of(WYJ_DEFAULT_TIMEZONE));

        //2.2.0 product 非必填
        if(line !=null && product == null){
            List<Map<String, Object>> queryFactoryList_count = mysqlJtl.queryForList(GET_PRODUCT_BY_LINE
                    , line);

            for (Map<String, Object> row : queryFactoryList_count) {
                product = (String) row.get("product");
                if(floor!=null && !"".equals(floor)){
                    tmpFloor = floor;
                }else {
                    tmpFloor = (String) row.get("floor");
                }

                DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                DateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd ");
                String nowStr = sdf.format(Date.from(now.toInstant()));
                String nowDateStr = sdf2.format(Date.from(now.toInstant()));
                String preDateStr = sdf2.format(Date.from(now.minusDays(1).toInstant()));
                List<Map<String, Object>> queryFactoryList_shift = mysqlJtl.queryForList(GET_NOW_SHIFT_DAY, product, nowStr, nowDateStr, nowDateStr);
                for (Map<String, Object> shift_row : queryFactoryList_shift) {
                    startTime = nowDateStr + String.valueOf(shift_row.get("start_time"));
                    stopTime = nowStr;
                }
                if(startTime==null){
                    queryFactoryList_shift = mysqlJtl.queryForList(GET_NOW_SHIFT_NIGHT, product, nowStr, preDateStr, nowDateStr);
                    for (Map<String, Object> shift_row : queryFactoryList_shift) {
                        startTime = preDateStr + String.valueOf(shift_row.get("start_time"));
                        stopTime = nowStr;
                    }
                }
            }
        }else{
            if (startTimeStr != null) {
                startTime = startTimeStr;
            } else {
                startTime = computeStartTimeForWYJ(product, now);
            }
            if (stopTimeStr != null) {
                stopTime = stopTimeStr;
            } else {
                stopTime = FormatUtils.dateTimeToString(now);
            }
            tmpFloor = floor;
        }
        final String finalProduct = product;
        final String finalFloor= tmpFloor;
        final String finalStartTime = startTime;
        final String finalStopTime = stopTime;

        Map<String, Object> topMap = new HashMap<String, Object>();
        List<Map<String, Object>> alertList = new ArrayList<Map<String, Object>>();
        topMap.put("startTime", finalStartTime);
        topMap.put("stopTime", finalStopTime);
        topMap.put("alertList", alertList);
        topMap.put("product", product);

        String dataStartTime = "";
        String dataEndTime = "";
        DateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        String sql = "select min(min_start_time) as dataStartTime, max(max_start_time) as dataEndTime "
                + " from ("
                + " select min(start_time) as min_start_time, max(start_time) as max_start_time from risk_test_station_sn "
                + " where product='"+product+"' "
                + " and floor='"+floor+"' "
                + " and start_time between '"+finalStartTime+"' and '"+finalStopTime+"' "
                + " and 1=1 "
                + " union all "
                + " select min(start_time) as min_start_time, max(start_time) as max_start_time from risk_test_station_sn "
                + " where product='"+product+"' "
                + " and floor='"+floor+"' "
                + " and start_time between '"+finalStartTime+"' and '"+finalStopTime+"' "
                + " and 1=1 "
                + " ) gg";
        if(line != null){
            sql = sql.replaceAll("1=1", "line='"+line+"'");
//    		sql += "and line='"+line+"'";
        }
        resultList = mysqlJtl.queryForList(sql);
        if(resultList.size()>0){
            dataStartTime = resultList.get(0).get("dataStartTime")==null?null:sdf.format(resultList.get(0).get("dataStartTime"));
            dataEndTime = resultList.get(0).get("dataEndTime")==null?null:sdf.format(resultList.get(0).get("dataEndTime"));
        }
        topMap.put("dataStartTime", dataStartTime);
        topMap.put("dataEndTime", dataEndTime);

        Connection mysqlConn = null;
        try {
            if(finalStartTime!=null){
                mysqlConn = mysqlDS.getConnection();
                String lv1QryStr = String.format(
                        QUERY_WYJ_LEVEL_ONE_TEMPLATE, finalProduct, finalFloor, line, finalStartTime, finalStopTime, WYJ_DEFAULT_RESULT_SIZE);
                logger.info("\n" + lv1QryStr);
                if (lv1QryStr.contains("empty")) {
                    logger.info("data is empty , must be skip");
                } else {
                    DataFrame<Object> df_lv1 = DataFrame.readSql(mysqlConn, lv1QryStr);
                    List<Callable<Map<String, Object>>> callableTasks = new ArrayList<>();

                    ListIterator<Map<Object, Object>> dfIter_lv1 = df_lv1.itermap();
                    while (dfIter_lv1.hasNext()) {
                        Map<String, Object> rowMap = new HashMap<String, Object>();
                        List<Map<String, Object>> riskList = new ArrayList<Map<String, Object>>();
                        rowMap.put("riskList", riskList);
                        Map<Object, Object> lv1Row = dfIter_lv1.next();

                        Callable<Map<String, Object>> callableTask = () -> {
                            Connection conn = null;
                            try {
                                conn = mysqlDS.getConnection();
                                String failureSympton = (String) lv1Row.get(FAILURE_SYMPTON_COLNAME);
                                String Symptom_id = String.valueOf(lv1Row.get(FAILURE_SYMPTON_ID_COLNAME));
                                String testStation = (String) lv1Row.get(TEST_STATION_COLNAME);
                                String failureRate = (String) lv1Row.get("failurerate");
                                String defectQty = (String) lv1Row.get("defectqty");

                                rowMap.put("testStation", testStation);
                                rowMap.put("failureSymptom_display", CodeUtils.getCodeName(codeTableRepository, finalProduct, "1", failureSympton, ""));
                                rowMap.put("failureSymptom", Symptom_id);
                                rowMap.put("failRate", failureRate);
                                rowMap.put("testStationFail", defectQty);
                                rowMap.put("waitRepairCnt", WAIT_REPAIR_COUNT_DEFAULT_VALUE);

                                String lv2QryStr = String.format(QUERY_WYJ_LEVEL_TWO_MAX_RANK_TEMPLATE, finalProduct, finalFloor, line, testStation, Symptom_id, finalStartTime, finalStopTime);
                                logger.info("\n" + lv2QryStr);
                                if (lv2QryStr.contains("empty")) {
                                    logger.info("data is empty , must be skip");
                                } else {
                                    DataFrame<Object> df_lv2 = DataFrame.readSql(conn, lv2QryStr);
                                    System.out.println(df_lv2);

                                    ListIterator<Map<Object, Object>> dfIter_lv2 = df_lv2.itermap();
                                    while (dfIter_lv2.hasNext()) {
                                        Map<String, Object> riskMap = new HashMap<String, Object>();
                                        Map<Object, Object> lv2Row = dfIter_lv2.next();

                                        String riskType = (String) lv2Row.get("risktype");
                                        String riskName = (String) lv2Row.get("riskname");

                                        riskMap.put("riskType", FormatUtils.wyjRiskType(riskType));
                                        riskMap.put("riskName_display", CodeUtils.getCodeName(codeTableRepository, finalProduct, "4", riskName, riskType));
                                        riskMap.put("riskName", riskName);

                                        String lv3QryStr = String.format(
                                                QUERY_WYJ_LEVEL_THREE_MAX_RANK_TEMPLATE,
                                                finalProduct, finalFloor, line, testStation, Symptom_id,
                                                riskName, riskType, finalStartTime, finalStopTime);
                                        logger.info("\n" + "lv3QryStr : " + lv3QryStr);
                                        DataFrame<Object> df_lv3 = DataFrame.readSql(conn, lv3QryStr);
                                        System.out.println(df_lv3);
                                        if (df_lv3 == null || df_lv3.isEmpty()) {
                                            logger.info("wyj df_lv3 is null or empty");
                                            continue;
                                        } else {
                                            riskMap.put("commonalityAssess", CommonalityAccessType.NEGATIVE.getText());

                                            BigDecimal significant = null;
                                            if (df_lv3.col("significant") != null && df_lv3.col("significant").get(0) != null) {
                                                significant = new BigDecimal((String) df_lv3.col("significant").get(0));
                                            }

                                            BigDecimal commonality = new BigDecimal((String) df_lv3.col("commonnality").get(0));
                                            BigInteger failQty = new BigInteger((String) df_lv3.col("failqty").get(0));
                                            Boolean common_lead = ((String) df_lv3.col("common_lead").get(0)).equals("1") ? true : false;

                                            if (significant != null) {
                                                if (commonality.compareTo(new BigDecimal(COMMONALITY_ACCESS_COMMALITY_THRESHOLD)) <= 0) {
                                                    logger.info(String.format("CommonalityAccess is negative because commonality %s is not greater than %s", commonality, COMMONALITY_ACCESS_COMMALITY_THRESHOLD));
                                                } else {
                                                    Boolean isSig = commonality.compareTo(new BigDecimal(COMMONALITY_ACCESS_SIGNIFICANT_THRESHOLD)) > 0;
                                                    if (!isSig && !common_lead) {
                                                        logger.info(String.format("Commonality: %s and CommonalityLeading: $s are both unqualified!", isSig, common_lead));
                                                    } else {
                                                        if (failQty.compareTo(new BigInteger(COMMONALITY_ACCESS_FAILQTY_THRESHOLD)) < 0) {
                                                            logger.info(String.format("failQty: %s is less than %s!", failQty, COMMONALITY_ACCESS_FAILQTY_THRESHOLD));
                                                            riskMap.put("commonalityAssess", CommonalityAccessType.DATALACK.getText());
                                                        } else {
                                                            riskMap.put("commonalityAssess", CommonalityAccessType.CONFIRMED.getText());
                                                        }
                                                    }
                                                }
                                            } else {
                                                logger.info("CommonalityAccess is negative because significant is null!");
                                            }

                                            riskMap.put("riskId_display", CodeUtils.getCodeName(codeTableRepository, finalProduct, "2_3", String.valueOf(df_lv3.col("riskCode").get(0)), riskType));
                                            riskMap.put("riskId", df_lv3.col("riskCode").get(0));
                                            riskMap.put("riskFailQty", df_lv3.col("failqty").get(0));
                                            riskMap.put("riskInput", df_lv3.col("defectqty").get(0));
                                            riskMap.put("outputQty", df_lv3.col("outputqty").get(0));
                                            riskMap.put("commonality", df_lv3.col("commonnality").get(0));
                                        }
                                        riskList.add(riskMap);
                                    }

                                }

                            } catch (Exception e) {
                                logger.error("Error running query!", e);
                            } finally {
                                if (conn != null) {
                                    conn.close();
                                }
                            }
                            return rowMap;
                        };
                        callableTasks.add(callableTask);
                    }
                    List<Future<Map<String, Object>>> futureList = executorService.invokeAll(callableTasks);
                    for (Future<Map<String, Object>> future : futureList) {
                        alertList.add(future.get());
                    }

                }
            }
            return new RestReturnMsg(200, "Query Test Detail successfully!!!", topMap);
        } catch (Exception e) {
            logger.error("Internal Error", e);
            return new RestReturnMsg(500, "Internal Error");
        } finally {
            if (mysqlConn != null) {
                try {
                    mysqlConn.close();
                } catch (Exception e) {
                    logger.error("Error!", e);
                }
            }
        }
//        return topMap;
    }

    private String computeStartTimeForWYJ(String product, ZonedDateTime now) {
        String nowStr = FormatUtils.dateTimeToTimeString(now);
        List<Map<String, Object>> resultList = mysqlJtl.queryForList(QUERY_WYJ_SHIFT_WITHIN_RANGE, product, nowStr, nowStr, nowStr);

        if (resultList.isEmpty()) {
//            resultList = mysqlJtl.queryForList(QUERY_WYJ_SHIFT_OUTSIDE_RANGE_FIRST, product, nowStr, nowStr, nowStr);
            resultList = mysqlJtl.queryForList(QUERY_WYJ_SHIFT_OUTSIDE_RANGE_FIRST, product, nowStr);
            if (resultList.isEmpty()) {
//            	resultList = mysqlJtl.queryForList(QUERY_WYJ_SHIFT_OUTSIDE_RANGE_SECOND, product, nowStr, nowStr, nowStr);
                resultList = mysqlJtl.queryForList(QUERY_WYJ_SHIFT_OUTSIDE_RANGE_SECOND, product);
            }
        }

        if(resultList.size()>0){
            LocalTime startLT = ((Time) resultList.get(0).get("start_time")).toLocalTime();
            ZonedDateTime startDT = ZonedDateTime.of(now.toLocalDate(), startLT, ZoneId.of(WYJ_DEFAULT_TIMEZONE));

            int nowHour = now.getHour();
            int nowMinute = now.getMinute();
            int startHour = startLT.getHour();
            int startMinute = startLT.getMinute();
            int startSecond = startLT.getSecond();

            if ((nowHour < startHour) || ((nowHour == startHour) && (nowMinute < startMinute))) {
                startDT = now
                        .minusDays(1L)
                        .withHour(startHour)
                        .withMinute(startMinute)
                        .withSecond(startSecond);
            }
            return FormatUtils.dateTimeToString(startDT);
        }else{
            return FormatUtils.dateTimeToString(now);
        }

    }

    @GetMapping("/defect")
    @RequiresAuthentication
    public RestReturnMsg queryTests(String sn, String symptom, String testStartDate, String testEndDate) {
        String queryFaTestSql = QUERY_FA_TEST_TEMPLATE;

        if (StringUtils.isNotBlank(sn)) {
            queryFaTestSql = queryFaTestSql + String.format(" AND sn = '%s'", sn);
        }
        if (StringUtils.isNotBlank(symptom)) {
            queryFaTestSql = queryFaTestSql + String.format(" AND failure_sympton = '%s'", symptom);
        }
        if (StringUtils.isNotBlank(testStartDate)) {
            queryFaTestSql = queryFaTestSql + String.format(" AND DATE(start_time) >= '%s'", testStartDate);
        }
        if (StringUtils.isNotBlank(testEndDate)) {
            queryFaTestSql = queryFaTestSql + String.format(" AND DATE(start_time) <= '%s'", testEndDate);
        }
        queryFaTestSql = queryFaTestSql + QUERY_FA_TEST_ORDER;
        logger.info(queryFaTestSql);

        List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();

        List<Map<String, Object>> resultList = mysqlJtl.queryForList(queryFaTestSql);
        for (Map<String, Object> row : resultList) {
            Map<String, Object> returnEntry = new HashMap<String, Object>();
            returnEntry.put("testing_id", row.get("id"));
            returnEntry.put("sn", row.get("sn"));
            returnEntry.put("factory", row.get("factory"));
            returnEntry.put("product", row.get("product"));
            returnEntry.put("floor", row.get("floor"));
            returnEntry.put("line", row.get("line"));
            returnEntry.put("station", row.get("station"));
            returnEntry.put("machine", row.get("machine"));
            returnEntry.put("failureSymptom", row.get("failure_sympton"));
            returnEntry.put("failureDesc", row.get("failure_desc"));
            returnEntry.put("testStartTime", FormatUtils.timestampToString((Timestamp) row.get("start_time")));
            dataList.add(returnEntry);
        }

        return new RestReturnMsg(200, "Query FA Tests successfully!!!", dataList);
    }

    @GetMapping("/defect/products/lines/stations")
    @RequiresAuthentication
    public RestReturnMsg getProdcutLineStation() {
        Connection conn = null;
        Integer code = 200;
        String msg = "Query Product_Line_Station successfully!!!";
        Map<String, Map<String, Set<String>>> productMap = new TreeMap<>();
        try {
            conn = mysqlJtl.getDataSource().getConnection();
            DataFrame<Object> df = DataFrame.readSql(conn, QUERY_PRODUCT_LINE_STATION);

            Set<Object> products = df.groupBy("product").groups().keys();
            for (Object product : products) {
                String productStr = (String) product;
                DataFrame<Object> productDf =
                        df.select(new DataFrame.Predicate<Object>() {
                            @Override
                            public Boolean apply(List<Object> values) {
                                return productStr.equals((String) values.get(0));

                            }
                        });
                Set<Object> lines = productDf.groupBy("line").groups().keys();

//                Map<String, Map<String, Set<String>>> lintMap = new TreeMap<>();
                Map<String, Set<String>> stationMap = new TreeMap<>();
                for (Object line : lines) {
                    String lineStr = (String) line;
                    DataFrame<Object> lineDf = df.select(new DataFrame.Predicate<Object>() {
                        @Override
                        public Boolean apply(List<Object> values) {
                            return productStr.equals((String) values.get(0)) && lineStr.equals((String) values.get(1));
                        }
                    });

                    Set<String> stationSet = new TreeSet<>();
                    for (Object station : lineDf.col("station")) {
                        stationSet.add((String) station);
                    }
                    stationMap.put(lineStr, stationSet);
//                    lintMap.put(lineStr, stationMap);
//                    lineList.add(stationMap);
                    productMap.put(productStr, stationMap);
                }
            }
        } catch (Exception e) {
            code = 500;
            msg = "Get exception when querying productLineStation...";
            logger.error(msg, e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                    code = 500;
                    msg = "Get exception when closing connection...";
                    logger.error(msg, e);
                }
            }
            return new RestReturnMsg(code, msg, productMap);
        }
    }
    @GetMapping("/defect/symptoms")
    @RequiresPermissions(value = {"fa:Create", "fa:Update", "fa:Delete", "fa:View"
            ,"fa_create:Create", "fa_create:Update", "fa_create:Delete", "fa_create:View",
    "fa_list:Create", "fa_list:Update", "fa_list:Delete", "fa_list:View"}, logical = Logical.OR)
    public RestReturnMsg queryTestSymptoms() {
        List<String> dataList = new ArrayList<>();
        List<Map<String, Object>> resultList = mysqlJtl.queryForList(QUERY_TEST_SYMPTON);
        for (Map<String, Object> row : resultList) {
            dataList.add((String) row.get("failure_sympton"));
        }
        return new RestReturnMsg(200, "Query Test Symptons successfully!!!",
                dataList);
    }



    /**
     * @return
     * @date 2019/5/26 下午14:12
     * @description 取得產品清單
     */
    @ApiOperation("取得產品清單")
    @RequiresAuthentication
    @GetMapping("/defect/products/name")
    public @ResponseBody RestReturnMsg getProduct() {
        logger.info("==> getProduct method");
        List<String> productList = new ArrayList<String>();
        List<Map<String, Object>> resultList = mysqlJtl.queryForList(QUERY_PRODUCT_CODE);
        for (Map<String, Object> row : resultList) {
            String product = (String) row.get("product");
            if (!productList.contains(product)) {
                productList.add(product);
            }
        }
        return new RestReturnMsg(200, "getProduct successfully!!!", productList);
    }

}
