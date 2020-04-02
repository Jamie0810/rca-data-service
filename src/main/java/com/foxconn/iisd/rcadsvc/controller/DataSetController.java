package com.foxconn.iisd.rcadsvc.controller;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.foxconn.iisd.rcadsvc.domain.*;
import com.foxconn.iisd.rcadsvc.msg.*;
import com.foxconn.iisd.rcadsvc.repo.*;
import com.foxconn.iisd.rcadsvc.service.FileService;
import com.foxconn.iisd.rcadsvc.util.CaseFiledUtil;
import com.foxconn.iisd.rcadsvc.util.plot.*;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.web.bind.annotation.*;

import com.foxconn.iisd.rcadsvc.domain.auth.User;
import com.foxconn.iisd.rcadsvc.service.CaseService;
import com.foxconn.iisd.rcadsvc.service.DataSetService;
import com.foxconn.iisd.rcadsvc.util.XWJUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(description = "dataset服務")
@RestController
@RequestMapping("/dataset")
public class DataSetController {

    private static final Logger logger = LoggerFactory.getLogger(DataSetController.class);

    private XWJUtil xwjUtil = new XWJUtil();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DataSetRepository dataSetRepository;

    @Autowired
    private CasePlotRepository casePlotRepository;

    @Autowired
    private CasePlotNoteRepository casePlotNoteRepository;

    @Autowired
    private CaseMatrixRepository caseMatrixRepository;

    @Autowired
    private DataSetService dataSetService;

    @Autowired
    private CaseService caseService;

    @Autowired
    @Qualifier("mysqlJtl")
    private JdbcTemplate mysqlJtl;

    private static final char SQLCHAR = '`';

    private final String caseTableName = "case_dataset_bigtable@";

    @Value("${filePath}")
    private String filePath;

    private String notePath = "note";

    private CaseFiledUtil casefiledutil = new CaseFiledUtil();

    @Autowired
    private CaseRepository caseRepository;

    @Autowired
    @Qualifier("minioFileService")
    private FileService fileService;

    /**
     * @param dsSetting
     * @return com.foxconn.iisd.rcadsvc.msg.RestReturnMsg
     * @date 2019/6/3  10:32
     * @description 新增 DataSetSetting
     */
    @ApiOperation("新增DataSetSetting")
    @RequiresAuthentication
    @PostMapping("")
    @RequiresPermissions(value = {"xwj_dataset_create:Create", "xwj_dataset_create:Update", "xwj_dataset_create:Delete", "xwj_dataset_create:View"
    ,"xwj:Create", "xwj:Update", "xwj:Delete", "xwj:View"}, logical = Logical.OR)
    public @ResponseBody
    RestReturnMsg create(@RequestBody DataSetSettingMsg dsSetting) {
        logger.info("===> Create method");
        Integer code = null;
        String msg = null;

        Subject subject = SecurityUtils.getSubject();
        // 檢查用戶是否已驗證 , 是將回傳true
        logger.info("===> 檢查用戶是否已驗證 : " + subject.isAuthenticated());
        HashMap dataMap = new HashMap<String, Object>();
        code = 200;
        if (subject.isAuthenticated()) {
            User currentUser = userRepository
                    .findByUsername((String) SecurityUtils.getSubject().getSession()
                            .getAttribute(UserController.USERNAME_SESSION_KEY));
            DataSetSetting findOne = dataSetRepository.findByName(dsSetting.getBtName());
            if (findOne == null) {
                msg = "Create DataSetSetting successfully!!";
                DataSetSetting dbdss = dataSetService.createDataSetSetting(currentUser, dsSetting.toNewDDS());
                dataMap.put(dbdss.getId(), dbdss.toDSSMsg(true));
            } else {
                msg = "The name of DataSetSetting already exists!!";
                dataMap.put(findOne.getId(), findOne);
            }

        } else {
            logger.info("==> This user has not logged in");
            msg = "This user has not logged in";
            dataMap.put("error", "error");
            code = 401;
        }
        dataMap.put("code", code);
        return new RestReturnMsg(code, msg, dataMap);
    }

    /**
     * @param dsSetting
     * @return com.foxconn.iisd.rcadsvc.msg.RestReturnMsg
     * @date 2019/6/3  10:32
     * @description 更新 DataSetSetting
     */
    @ApiOperation("更新 DataSetSetting")
    @RequiresAuthentication
    @PutMapping("/{id}")
    public @ResponseBody
    RestReturnMsg update(@RequestBody DataSetSettingMsg dsSetting, @PathVariable Long id) {
        logger.info("===> Update method");
        Integer code = null;
        String msg = null;

        Subject subject = SecurityUtils.getSubject();
        // 檢查用戶是否已驗證 , 是將回傳true
        logger.info("===> 檢查用戶是否已驗證 : " + subject.isAuthenticated());
        HashMap dataMap = new HashMap<String, Object>();

        code = 200;
        if (subject.isAuthenticated()) {
            User currentUser = userRepository
                    .findByUsername((String) SecurityUtils.getSubject().getSession()
                            .getAttribute(UserController.USERNAME_SESSION_KEY));
            //User currentUser = userRepository.findByUsername("admin");
            DataSetSetting dbDss = dataSetService.updateDataSetSetting(currentUser, dsSetting.toNewDDS(), id);
            dataMap.put(dbDss.getId(), dbDss.toDSSMsg(true));
            msg = "Update DataSetSetting successfully!!";
        } else {
            logger.info("==> This user has not logged in");
            msg = "This user has not logged in";
            dataMap.put("error", "error");
            code = 401;
        }
        dataMap.put("code", code);
        return new RestReturnMsg(code, msg, dataMap);
    }

    /**
     * @param product
     * @return
     * @date 2019/5/26 下午14:12
     * @description 條列該產品所有DataSetSetting
     */
    @ApiOperation("條列該產品所有DataSetSetting ")
    @RequiresAuthentication
    @GetMapping("/listByProduct")
    public @ResponseBody
    RestReturnMsg listByProduct(@RequestParam(name = "product", required = false) String product) {
        List<DataSetSetting> dssList = new ArrayList<DataSetSetting>();
        if (product != null && !product.trim().equals(""))
            dssList = dataSetRepository.findByProduct(product);
        else
            dssList = dataSetRepository.findAll();
        List<DataSetSettingMsg> resultList = new ArrayList<DataSetSettingMsg>();
        for (DataSetSetting dss : dssList) {
            resultList.add(dss.toDSSMsg(true, caseService));
        }
        return new RestReturnMsg(200, "get DataSetSetting list successfully", resultList);
        //return new RestReturnMsg(200, "create build successfully", "");
    }

    /**
     * @param product
     * @param page
     * @param size
     * @return
     * @date 2019/5/26 下午14:12
     * @description 條列該產品所有DataSetSetting(分頁顯示)
     */
    @ApiOperation("條列該產品所有DataSetSetting(分頁顯示) ")
    @RequiresAuthentication
    @GetMapping("/pageByProduct")
    public @ResponseBody
    RestReturnMsg pageByProduct(
            @RequestParam(name = "product", required = false) String product,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size) {
        if (page == null || page == 1 || page == 0) page = 1;
        if (size == null || size == 0) size = 10;
        page = page - 1; //將前端的pageNum轉成後端使用的pageNum

        //PageRequest的物件建構函式有多個，page是頁數，初始值是0，size是查詢結果的條數，後兩個引數參考Sort物件的構造方法
        Pageable pageable = new PageRequest(page, size, Sort.Direction.DESC, "id");
        Map<String, Object> dataSetResult = new HashMap<String, Object>();
        List<DataSetSettingMsg> result = new ArrayList<DataSetSettingMsg>();
        if (product != null && !product.trim().equals("")) {
            Page<DataSetSetting> dssList = dataSetRepository.findByProduct(product, pageable);
            for (DataSetSetting dss : dssList) {
                result.add(dss.toDSSMsg(true, caseService));
            }
            dataSetResult.put("content", result);
            //dataSetResult.put("pageable",dssList.getPageable());
            dataSetResult.put("totalPages", dssList.getTotalPages());
            dataSetResult.put("totalElements", dssList.getTotalElements());
            return new RestReturnMsg(200, "get pageByProduct page successfully", dataSetResult);
        } else {
            Page<DataSetSetting> dssList = dataSetRepository.findAll(pageable);
            for (DataSetSetting dss : dssList) {
                result.add(dss.toDSSMsg(true, caseService));
            }
            dataSetResult.put("content", result);
            //dataSetResult.put("pageable",dssList.getPageable());
            dataSetResult.put("totalPages", dssList.getTotalPages());
            dataSetResult.put("totalElements", dssList.getTotalElements());
            return new RestReturnMsg(200, "get pageByProduct page successfully", dataSetResult);
        }


        //return new RestReturnMsg(200, "create build successfully", "");
    }


    /**
     * @param page
     * @param size
     * @param fulltext
     * @return
     * @date 2019/5/26 下午14:12
     * @description 搜尋DataSetSetting(分頁顯示)
     */
    @ApiOperation("搜尋DataSetSetting(分頁顯示)")
    @RequiresAuthentication
    @GetMapping("/queryPageDataSet")
    public @ResponseBody
    RestReturnMsg queryPageDataSet(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size,
            @RequestParam(name = "fulltext", required = false) String fulltext) {
        if (size == null || size == 0) size = 10;
        if (page == null || page == 1 || page == 0) page = 1;
        page = page - 1; //將前端的pageNum轉成後端使用的pageNum

        //PageRequest的物件建構函式有多個，page是頁數，初始值是0，size是查詢結果的條數，後兩個引數參考Sort物件的構造方法
        Pageable pageable = new PageRequest(page, size, Sort.Direction.DESC, "id");
        Map<String, Object> dataSetResult = new HashMap<String, Object>();
        List<DataSetSettingMsg> result = new ArrayList<DataSetSettingMsg>();
        if (fulltext != null && !fulltext.trim().equals("")) {
            Page<DataSetSetting> dssList = dataSetRepository.findByProductContainingOrNameContainingOrRemarkContainingOrCreateUserContaining(fulltext, fulltext, fulltext, fulltext, pageable);
            for (DataSetSetting dss : dssList) {
                result.add(dss.toDSSMsg(true, caseService));
            }
            dataSetResult.put("content", result);
            //dataSetResult.put("pageable",dssList.getPageable());
            dataSetResult.put("totalPages", dssList.getTotalPages());
            dataSetResult.put("totalElements", dssList.getTotalElements());
            return new RestReturnMsg(200, "queryPageDataSet successfully", dataSetResult);
        } else {
            Page<DataSetSetting> dssList = dataSetRepository.findAll(pageable);
            for (DataSetSetting dss : dssList) {
                result.add(dss.toDSSMsg(true, caseService));
            }
            dataSetResult.put("content", result);
            //dataSetResult.put("pageable",dssList.getPageable());
            dataSetResult.put("totalPages", dssList.getTotalPages());
            dataSetResult.put("totalElements", dssList.getTotalElements());
            return new RestReturnMsg(200, "queryPageDataSet successfully", dataSetResult);
        }


    }

    /**
     * @return
     * @date 2019/5/26 下午14:12
     * @description 搜尋DataSetSetting(全部顯示)
     */
    @ApiOperation("搜尋DataSetSetting(全部顯示)")
    @RequiresAuthentication
    @RequiresPermissions(value={"xwj_dataset_list:Create","xwj_dataset_list:Update","xwj_dataset_list:Delete","xwj_dataset_list:View"
    ,"xwj:Create", "xwj:Update", "xwj:Delete", "xwj:View"},logical= Logical.OR)
    @GetMapping("")
    public @ResponseBody
    RestReturnMsg queryDataSet() {
        List<DataSetSettingMsg> result = new ArrayList<DataSetSettingMsg>();
        List<DataSetSetting> dssList = dataSetRepository.findAll();
        for (DataSetSetting dss : dssList) {
            result.add(dss.toDSSMsg(false, caseService));
        }

        return new RestReturnMsg(200, "get pageByProduct page successfully", result);
    }

    /**
     * @param name
     * @return
     * @date 2019/5/26 下午14:12
     * @description 找出Name同的DataSetSetting
     */
    @ApiOperation("找出Name同的DataSetSetting")
    @RequiresAuthentication
    @GetMapping("/findByName")
    public @ResponseBody
    RestReturnMsg findByName(@RequestParam("name") String name) {
        DataSetSetting dss = dataSetRepository.findByName(name);
        return new RestReturnMsg(200, "get DataSetSetting by name successfully", dss.toDSSMsg(true, caseService));
    }

    /**
     * @param id
     * @return
     * @date 2019/5/26 下午14:12
     * @description 用ID找出DataSetSetting
     */
    @ApiOperation("用ID找出DataSetSetting")
    @RequiresAuthentication
    @GetMapping("/{id}")
    public @ResponseBody
    RestReturnMsg findById(@PathVariable Long id) {
        DataSetSetting dss = dataSetRepository.findById(id).get();
        return new RestReturnMsg(200, "get issue by code successfully", dss.toDSSMsg(true, caseService));
    }

    /**
     * @param name
     * @return
     * @date 2019/5/26 下午14:12
     * @description 找出包含Name的DataSetSetting
     */
    @ApiOperation("找出包含Name的DataSetSetting")
    @RequiresAuthentication
    @GetMapping("/queryByName")
    public @ResponseBody
    RestReturnMsg queryByName(@RequestParam("name") String name) {
        List<DataSetSetting> dssList = dataSetRepository.findByNameContaining(name);
        List<DataSetSettingMsg> dssMsgList = new ArrayList<DataSetSettingMsg>();
        for (DataSetSetting dss : dssList) {
            dssMsgList.add(dss.toDSSMsg(true, caseService));
        }
        return new RestReturnMsg(200, "query DataSetSetting list successfully", dssMsgList);
    }

    /**
     * @param id
     * @return
     * @date 2019/5/26 下午14:12
     * @description 用ID 刪除 DataSetSetting
     */
    @ApiOperation("用ID 刪除 DataSetSetting")
    @RequiresAuthentication
    @DeleteMapping("/{id}")
    public @ResponseBody
    RestReturnMsg deleteById(@PathVariable Long id) {
        User currentUser = userRepository
                .findByUsername((String) SecurityUtils.getSubject().getSession()
                        .getAttribute(UserController.USERNAME_SESSION_KEY));
        DataSetSetting dss = dataSetRepository.findById(id).get();
        dataSetService.deleteDataSetSetting(currentUser, dss);
        return new RestReturnMsg(200, "delete DataSetSetting by id successfully", id);
    }


    /**
     * @param product
     * @param type
     * @param component
     * @return
     * @date 2019/5/26 下午14:12
     * @description 取得該產品的某類型關鍵物料
     */
    @ApiOperation("取得該產品的某類型關鍵物料 ")
    @RequiresAuthentication
    @GetMapping("/getCompByProductType")
    public @ResponseBody
    RestReturnMsg getCompByProductType(
            @RequestParam(name = "product") String product,
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "component", required = false) String component,
            @RequestParam(name = "size", required = false) Integer size) {

        StringBuffer sqlCmd = new StringBuffer("SELECT DISTINCT component_type as partType ,component FROM config_component ");
        sqlCmd.append(String.format(" WHERE product = '%s'", product));
        if (type != null && !type.trim().equals(""))
            sqlCmd.append(String.format(" AND component_type = '%s'", type));
        if (component != null && !component.trim().equals(""))
            sqlCmd.append(String.format(" AND component like '%s'", '%' + component + '%'));
        sqlCmd.append(" order by component_type,component");
        if (size == null) size = 0;
        if (size > 0)
            sqlCmd.append(" LIMIT ").append(size);

        List<Map<String, Object>> resultList = mysqlJtl.queryForList(sqlCmd.toString());

        return new RestReturnMsg(200, "getComponentInfoByProductTypeItem successfully", resultList);
    }

    /**
     * @param product
     * @param type
     * @param component
     * @return
     * @date 2019/5/26 下午14:12
     * @description 取得該產品的某關鍵物料所有資訊
     */
    @ApiOperation("取得該產品的某關鍵物料所有資訊 ")
    @RequiresAuthentication
    @GetMapping("/getCompInfoByCompName")
    public @ResponseBody
    RestReturnMsg getCompInfoByCompName(
            @RequestParam(name = "product") String product,
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "component", required = false) String component,
            @RequestParam(name = "size", required = false) Integer size) {

        String headString = "product,build_name,file_name,config_name,component_type as partType ,component,version,hhpn,oempn,description,config,vendor,per,aoh,color,config_description,oem_notes,drp_notes,notes,etas,etds,input_qty,excel_row_id";
        String[] headAry = headString.split(",");
        StringBuffer sqlCmd = new StringBuffer("SELECT ").append(headString).append(" FROM config_component ");
        sqlCmd.append(String.format(" WHERE product = '%s'", product));

        if (type != null && !type.trim().equals(""))
            sqlCmd.append(String.format(" AND component_type = '%s'", type));
        if (component != null && !component.trim().equals(""))
            sqlCmd.append(String.format(" AND component like '%s'", '%' + component + '%'));

        sqlCmd.append(" order by component,version desc ");
        if (size == null) size = 0;
        if (size > 0)
            sqlCmd.append(" LIMIT ").append(size);

        List<String[]> rowList = new ArrayList<String[]>();
        List<Map<String, Object>> resultList = mysqlJtl.queryForList(sqlCmd.toString());

        return new RestReturnMsg(200, "query item By Product successfully", resultList);
    }


    /**
     * @param id
     * @param page
     * @param size
     * @return
     * @date 2019/5/26 下午14:12
     * @description 取DataSet大表資料(分頁顯示)
     */
    @ApiOperation("取DataSet大表資料(分頁顯示) ")
    @RequiresAuthentication
    @GetMapping(value = {"/{id}/pool/page/{page}/size/{size}", "/{id}/pool"})
    public @ResponseBody
    RestReturnMsg pageDataSetData(@PathVariable Long id, @PathVariable(required = false) Integer page, @PathVariable(required = false) Integer size) {

        if (page == null || page == 1 || page == 0) page = 1;
        if (size == null || size == 0) size = 10;
        page = page - 1; //將前端的pageNum轉成後端使用的pageNum

        //PageRequest的物件建構函式有多個，page是頁數，初始值是0，size是查詢結果的條數，後兩個引數參考Sort物件的構造方法
        Pageable pageable = new PageRequest(page, size, Sort.Direction.DESC, "id");
        DataSetSetting dbDss = dataSetRepository.getOne(id);
        String tbName = (String) xwjUtil.addSQLCHAR(dbDss.getBtName());
        String returnMsg = "";
        Map<String, Object> dataSetResult = new HashMap<String, Object>();
        System.out.println("Find Bigtable:" + tbName);
        if (tbName == null || tbName.equals("")) {
            dataSetResult.put("head_name", new String[0]);
            dataSetResult.put("row_data", new String[0]);
            //dataSetResult.put("pageable",pageable);
            dataSetResult.put("totalPages", 0);
            dataSetResult.put("totalElements", 0);
            returnMsg = "successfully, but bigtable not created....";
        } else {
            List<String> selectList = this.genTableSelectField(tbName);
            Map<String, String[]> fieldMap = xwjUtil.convertToHeadAndSelectAry(selectList);
            String[] headAry = fieldMap.get("head");
            String[] selectAry = fieldMap.get("select");
            //StringBuffer sbSQL = new StringBuffer();
            int idx = 0;

            Page<Map<String, Object>> resultList = this.pageDataSetData(tbName, pageable, "", selectAry);

            List<Object> rowList = new ArrayList<Object>();
            for (Map<String, Object> row : resultList) {
                Object[] rowary = new String[headAry.length];
                for (idx = 0; idx < headAry.length; ++idx) {
                    Object o = row.get(headAry[idx]);
                    if (o.getClass().equals(byte[].class)) {
                        rowary[idx] = new String((byte[]) o);
                    } else
                        rowary[idx] = o;
                }
                rowList.add(rowary);
            }

            dataSetResult.put("head_name", headAry);
            dataSetResult.put("row_data", rowList);
            //dataSetResult.put("pageable",resultList.getPageable());
            dataSetResult.put("totalPages", resultList.getTotalPages());
            dataSetResult.put("totalElements", resultList.getTotalElements());
            returnMsg = "pageDataSetData successfully";
        }

        return new RestReturnMsg(200, returnMsg, dataSetResult);

    }

    /**
     * @param id
     * @return
     * @date 2019/7/3  AM 04:04
     * @description 取DataSet有哪些欄位供選擇
     */
    @ApiOperation("取DataSet有哪些欄位供選擇[for Create Case用]")
    @RequiresAuthentication
    @GetMapping("/{id}/pool/properties")
    public @ResponseBody
    RestReturnMsg getDataSetField(@PathVariable Long id) {
        String returnMsg = "";


        Map<String, Object> dataSetResult = new HashMap<String, Object>();
        //System.out.println("Gen Field List from  Bigtable:" + tbName);

        List<String> jsonField = new ArrayList<String>();
        List<CaseFiledMsg> filedlist = new ArrayList<>();
        if (dataSetRepository.existsById(id)) {
            String datasetInfoSql = "Select ifnull(column_name,'') as name,ifnull(column_type,'') as dttype,ifnull(json_str,'') as srctype from data_set_bigtable_columns where data_set_id =" + id;
            List<Map<String, Object>> resultList = mysqlJtl.queryForList(datasetInfoSql);
            for (Map<String, Object> row : resultList) {
                if (!((String) row.get("name")).contains("@result") && !((String) row.get("name")).contains("@list_of_failure")) {
                    CaseFiledMsg msg = new CaseFiledMsg();
                    msg.setDataType((String) row.get("dttype"));
                    msg.setName((String) row.get("name"));
                    msg.setType((String) row.get("srctype"));
                    filedlist.add(msg);
                }
            }
//
//            HashMap dataMap = new HashMap<String, Object>();
//            dataMap.put("field",filedlist);
//            DataSetSetting dss = dataSetRepository.findById(dsId).get();
//            String tbName = (String)xwjUtil.addSQLCHAR(dss.getBtName());
//            if(tbName == null || tbName.equals("")){
//                returnMsg = "successfully, but bigtable not created....";
//            }else{
//                /**取出所有欄位,另外將json收集另外處理**/
//                List<Map<String, Object>> fieldList = mysqlJtl.queryForList("SHOW COLUMNS FROM "+ tbName);
//                for(Map<String, Object> row : fieldList){
//                    String field = row.get("Field").toString();
//                    String type = row.get("Type").toString();
//                    if(type.equals("json")){
//                        StringBuffer sqlsb  = new StringBuffer();
//                        jsonField.add(field);
//                    }else{
//                        resultList.add(xwjUtil.newFieldObj("fixed",field));
//                    }
//                }
//
//                /**取出Json類型的欄位,再將json內容轉出 **/
//                if(jsonField.size()>0){
//                    StringBuffer sqlSb = new StringBuffer();
//                    sqlSb.append("select 'a' as a ");
//                    for(String field : jsonField){
//                        sqlSb.append(",JSON_KEYS(").append(field).append(") as ").append(field);
//                    }
//                    sqlSb.append(" from ").append(tbName).append(" limit 1;");
//                    List<Map<String, Object>> jsonResult = mysqlJtl.queryForList(sqlSb.toString());
//                    if(jsonResult.size()>0) {
//                        for(String field : jsonField){
//                            String tmp = jsonResult.get(0).get(field).toString();
//                            String[] infoField = tmp.substring(1,tmp.length()-1).split(",");
//                            for(String name : infoField){
//                                name = name.trim().replaceAll("\"","");
//                                resultList.add(xwjUtil.newFieldObj(field,name));
//                            }
//                        }
//                    }
//                }
//                returnMsg = "get Data_Set_Field List successfully";
//            }
            returnMsg = "get Data_Set_Field List successfully";
        } else {
            returnMsg = "data_set_setting not exist!";
        }

        dataSetResult.put("data_set_id", id);
        dataSetResult.put("field", filedlist);

        return new RestReturnMsg(200, returnMsg, dataSetResult);
    }

    /**
     * @return
     * @date 2019/7/17 下午 15:00
     * @description 複製資料集
     */
    @ApiOperation("複製資料集 ")
    @PostMapping("/{id}/copy")
    public @ResponseBody
    RestReturnMsg copyById(
            @PathVariable Long id, @RequestBody DataSetSettingMsg dsSetting) {
//        User currentUser = userRepository
//                .findByUsername((String) SecurityUtils.getSubject().getSession()
//                        .getAttribute(UserController.USERNAME_SESSION_KEY));
//        int newID = 0 ;
//        String headString = "insert into `data_set_setting`(product, name, remark, bt_name, bt_create_time, bt_last_time, bt_next_time, create_user, create_time, modify_user, modify_time, effective_start_date, effective_end_date)";
//        headString+= " select product, ?, remark, bt_name, bt_create_time, bt_last_time, bt_next_time, ?, now(), ?, now(), effective_start_date, effective_end_date ";
//        headString+= " from `data_set_setting` where id = "+dsSetting.getId();
//
//        StringBuffer sqlCmd =  new StringBuffer("").append(headString);
//        mysqlJtl.execute(sqlCmd.toString(), new PreparedStatementCallback<Boolean>(){  
//	            @Override  
//	            public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {  
//		        	ps.setString(1, dsSetting.getName());  
//		        	ps.setString(2, currentUser.getUsername());  
//		        	ps.setString(3, currentUser.getUsername());  
//		        	return ps.execute();  
//            	}  
//            }
//        );
//        Map<String,Object> result = new HashMap<String,Object>();
//        headString = "select LAST_INSERT_ID()";
//        List<Map<String, Object>> resultList =mysqlJtl.queryForList(headString);
//        if(resultList.size() > 0) {
//            newID = ((BigInteger)resultList.get(0).get("LAST_INSERT_ID()")).intValue();
//            String sql = "Create table `data_set_bigtable@"+newID+"` like `data_set_bigtable@"+dsSetting.getId()+"`";
//            mysqlJtl.execute(sql);
//            sql = "insert into `data_set_bigtable@"+newID+"` select * from `data_set_bigtable@"+dsSetting.getId()+"`";
//            mysqlJtl.execute(sql);
//        }
//        headString = " id, product, name, remark, bt_name as btName, bt_create_time as btCreateTime, bt_last_time as btLastTime, bt_next_time as btNextTime, create_user as createUser, create_time as createTime, modify_user as modifyUser, modify_time as modifyTime, effective_start_date as effectiveStartDate, effective_end_date as effectiveEndDate ";
//        sqlCmd =  new StringBuffer("select ").append(headString).append(" from `data_set_setting` ").append(" where id =" +newID);
//        resultList =mysqlJtl.queryForList(sqlCmd.toString());
//        result = resultList.get(0);
//        return new RestReturnMsg(200, "copy By dataSetId successfully" , result);
        logger.info("===> copyById method");
        Integer code = null;
        String msg = null;
        DataSetSetting dbdss = null;

        Subject subject = SecurityUtils.getSubject();
        // 檢查用戶是否已驗證 , 是將回傳true
        logger.info("===> 檢查用戶是否已驗證 : " + subject.isAuthenticated());
        HashMap dataMap = new HashMap<String, Object>();

        code = 200;
        if (subject.isAuthenticated()) {
            User currentUser = userRepository
                    .findByUsername((String) SecurityUtils.getSubject().getSession()
                            .getAttribute(UserController.USERNAME_SESSION_KEY));
            DataSetSetting findOne = dataSetRepository.findById(id).isPresent() ? dataSetRepository.findById(id).get() : null;
            if (findOne == null) {
                code = 404;
                msg = "The DataSet " + id + " not exists!!";
                dataMap.put("404", "not found");
            } else {
                msg = "copy By dataSetId successfully";
                dbdss = dataSetService.copyDataSetSetting(currentUser, dsSetting.getName(), id);
//                dataMap.put(dbdss.getId(),dbdss.toDSSMsg(true));
            }

        } else {
            logger.info("==> This user has not logged in");
            msg = "This user has not logged in";
            dataMap.put("error", "error");
            code = 401;
        }
//        dataMap.put("code", code);
        return new RestReturnMsg(code, msg, dbdss.toDSSMsg(true));
    }

    private String escapeSingleQuote(String value) {
        String returnValue = value;
        if (value != null) {
            returnValue = returnValue.replaceAll("'", "''");
        }
        return returnValue;
    }

    private Page<Map<String, Object>> pageDataSetData(String tableName, Pageable pageable, String whereSQL, String[] selectAry) {
        String rowCountSql = "SELECT count(1) AS row_count FROM " + tableName + whereSQL;
        int total = mysqlJtl.queryForObject(rowCountSql, java.lang.Integer.class);

        String querySql = "SELECT " + String.join(",", selectAry) + " FROM " + tableName + whereSQL +
                " LIMIT " + pageable.getPageSize() + " " +
                " OFFSET " + pageable.getOffset();
        List<Map<String, Object>> resultList = mysqlJtl.queryForList(querySql.toString());
        return new PageImpl<>(resultList, pageable, total);
    }

    //產出大表select所需要的select欄位
    private List<String> genTableSelectField(String tbName) {
        List<String> jsonFieldList = new ArrayList<String>();
        List<Map<String, Object>> fieldList = mysqlJtl.queryForList("SHOW COLUMNS FROM " + tbName);
        List<String> selectField = new ArrayList<String>();
        for (Map<String, Object> row : fieldList) {
            String field = row.get("Field").toString();
            String type = row.get("Type").toString();
            if (!type.equals("json")) {
                StringBuffer sqlsb = new StringBuffer();
                sqlsb.append("ifnull(").append(xwjUtil.addSQLCHAR(field)).append(",''").append(") as ").append(xwjUtil.addSQLCHAR(field));
                selectField.add(sqlsb.toString());
            } else {
                jsonFieldList.add(field);
            }
        }
        if (tbName.indexOf("data_set_bigtable@") > 0) {
            List<String> jsonField = getJsonField(tbName, jsonFieldList);
            selectField.addAll(jsonField);
        }
        return selectField;
    }

    //將兩個JSON欄位的KEY取出並轉成mySQL的select語法
    private List<String> getJsonField(String tablename, List<String> fieldList) {
        List<String> resultList = new ArrayList<String>();

        StringBuffer sqlSb = new StringBuffer();
        sqlSb.append("select 'a' as a ");
        for (String field : fieldList) {
            sqlSb.append(",IFNULL(JSON_KEYS(").append(field).append("),'') as ").append(field);
        }
//        for(String field : fieldList){
//            sqlSb.append(",JSON_KEYS(").append(field).append(") as ").append(field);
//        }
        sqlSb.append(" from ").append(tablename).append(" LIMIT 1;");
        //String fieldSQL =" select JSON_KEYS(station_info) as station_info , JSON_KEYS(item_info) as item_info ,  JSON_KEYS(component_info) as component_info from " + tablename + " LIMIT 1;";

        List<Map<String, Object>> result = mysqlJtl.queryForList(sqlSb.toString());
        StringBuffer selectSQLSb = new StringBuffer();
        if (result.size() > 0) {
            for (String field : fieldList) {
                String tmp = result.get(0).get(field).toString();
                if (!tmp.trim().equals("")) {
                    String[] infoAry = tmp.substring(1, tmp.length() - 1).split(",");
                    for (String key : infoAry) {
                        StringBuffer tmpSb = new StringBuffer();
                        tmpSb.append("IFNULL( ").append(field).append("->>'$.").append(key.trim()).append("'").append(",'') as ").append(key.trim().replaceAll("\"", "`"));
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
        } else
            return resultList;
    }

    //以下為改版開始

    /****** 上方為假的測試條件 *****/

    /**
     * @return
     * @date 2019/7/3 下午15:12
     * @description 提供資料集清單
     */
    @ApiOperation("條列產品所有DataSetSetting ")
    @GetMapping("/less")
    @RequiresPermissions(value = {"xwj_analysis_create:Create", "xwj_analysis_create:Update", "xwj_analysis_create:Delete", "xwj_analysis_create:View"
            ,"xwj:Create", "xwj:Update", "xwj:Delete", "xwj:View"}, logical = Logical.OR)
    public @ResponseBody
    RestReturnMsg dataSetInfo() {
        List<DataSetSetting> dssList = new ArrayList<DataSetSetting>();
        dssList = dataSetRepository.findAll();
        List<DataSetInfoMsg> resultList = new ArrayList<DataSetInfoMsg>();
        for (DataSetSetting dss : dssList) {
            String sql = "show tables LIKE 'data_set_bigtable@" + dss.getId() + "'";
            List<Map<String, Object>> tablelist = mysqlJtl.queryForList(sql);
            if (tablelist.size() > 0)
                resultList.add(dss.toDSIMsg(false));
        }
        return new RestReturnMsg(200, "get DataSetSettingInfo list successfully", resultList);
    }

    /**
     * @param caseMsg
     * @return com.foxconn.iisd.rcadsvc.msg.RestReturnMsg
     * @date 2019/6/3  10:32
     * @description 新增 Case
     */
    @ApiOperation("新增Case")
    @RequiresAuthentication
    @RequiresPermissions(value={"xwj_analysis_create:Create","xwj_analysis_create:Update","xwj_analysis_create:Delete","xwj_analysis_create:View"
    ,"xwj:Create","xwj:Update","xwj:Delete","xwj:View"},logical= Logical.OR)
    @PostMapping("/analysis")
    public @ResponseBody
    RestReturnMsg create(@RequestBody CaseMsg caseMsg) {
        logger.info("===> Create method");
        Integer code = null;
        String msg = null;

        Subject subject = SecurityUtils.getSubject();
        // 檢查用戶是否已驗證 , 是將回傳true
        logger.info("===> 檢查用戶是否已驗證 : " + subject.isAuthenticated());
        HashMap dataMap = new HashMap<String, Object>();

        code = 200;
        if (subject.isAuthenticated()) {
            User currentUser = userRepository
                    .findByUsername((String) SecurityUtils.getSubject().getSession()
                            .getAttribute(UserController.USERNAME_SESSION_KEY));
            Case findOne = caseRepository.findByName(caseMsg.getName());
            if (findOne == null) {
                msg = "Create Case successfully!!";
                Case dbcase = caseService.createCase(currentUser, caseMsg.toNewDDS());
                dataMap.put(dbcase.getId(), dbcase.toCaseMsg(true, dataSetRepository));
            } else {
                msg = "The name of Case already exists!!";
                dataMap.put(findOne.getId(), findOne);
            }

        } else {
            logger.info("==> This user has not logged in");
            msg = "This user has not logged in";
            dataMap.put("error", "error");
            code = 401;

        }
        dataMap.put("code", code);
        return new RestReturnMsg(code, msg, dataMap);
    }

    /**
     * @param caseMsg
     * @return com.foxconn.iisd.rcadsvc.msg.RestReturnMsg
     * @date 2019/6/3  10:32
     * @description 更新 Case
     */
    @ApiOperation("更新 Case")
    @RequiresAuthentication
    @PutMapping("/analysis/{id}")
    public @ResponseBody
    RestReturnMsg update(@RequestBody CaseMsg caseMsg, @PathVariable Long id) {
        logger.info("===> Update method");
        Integer code = null;
        String msg = null;
        Case dbcase = null;
        Subject subject = SecurityUtils.getSubject();
        // 檢查用戶是否已驗證 , 是將回傳true
        logger.info("===> 檢查用戶是否已驗證 : " + subject.isAuthenticated());
        HashMap dataMap = new HashMap<String, Object>();

        code = 200;
        if (subject.isAuthenticated()) {
            User currentUser = userRepository
                    .findByUsername((String) SecurityUtils.getSubject().getSession()
                            .getAttribute(UserController.USERNAME_SESSION_KEY));
            //User currentUser = userRepository.findByUsername("admin");

            Case findOne = caseRepository.findById(id).isPresent() ? caseRepository.findById(id).get() : null;
            if (findOne == null) {
                code = 404;
                msg = "The Case not exists!!";
                dataMap.put("404", "not found");
            } else {
                msg = "Update Case successfully!!";
                dbcase = caseService.updateCase(currentUser, caseMsg.toNewDDS(), id);
//                dataMap.put(dbcase.getId(),dbcase.toCaseMsg(true, dataSetRepository));
            }
        } else {
            logger.info("==> This user has not logged in");
            msg = "This user has not logged in";
            dataMap.put("error", "error");
            code = 401;
        }
//        dataMap.put("code", code);
        return new RestReturnMsg(code, msg, dbcase == null ? null : dbcase.toCaseMsg(true, dataSetRepository));
    }

    /**
     * @return
     * @description 取Case分析表搜尋清單
     */
    @ApiOperation("取Case分析表搜尋清單")
    @RequiresAuthentication
    @RequiresPermissions(value={"xwj_analysis_list:Create","xwj_analysis_list:Update","xwj_analysis_list:Delete","xwj_analysis_list:View"
    ,"xwj:Create","xwj:Update","xwj:Delete","xwj:View"},logical= Logical.OR)
    @GetMapping("/analysis")
    public @ResponseBody
    RestReturnMsg caseSearchData() {

        String returnMsg = "";
        Map<String, Object> caseResult = new HashMap<String, Object>();
        String sql = "select distinct dss.product as product,dss.name as dataSetName,c.id as id,c.name as name ,c.remark as remark,c.create_user as createUser,c.create_time as createTime,c.modify_user as modifyUser,c.modify_time as modifyTime,c.data_last_date as dataLastDate " +
                "from data_set_setting as dss " +
                "left join `case` as c on c.data_set_id = dss.id " +
                "left join `case_plot`as cp on cp.case_id = c.id " +
                "left join `case_plot_note` as cpn on cpn.plot_id = cp.id " +
                "where c.name is not null ";

        List<Map<String, Object>> resultList = mysqlJtl.queryForList(sql);
        List<SearchCaseMsg> searchList = new ArrayList<>();
        for (Map<String, Object> row : resultList) {
            SearchCaseMsg msg = new SearchCaseMsg();
            msg.setId(Integer.parseInt(row.get("id").toString()));
            msg.setProduct((String) row.get("product"));
            msg.setName((String) row.get("name"));
            msg.setRemark((String) row.get("remark"));
            msg.setDataSetName((String) row.get("dataSetName"));
            msg.setCreateUser((String) row.get("createUser"));
            msg.setCreateTime((Date) row.get("createTime"));
            msg.setModifyUser((String) row.get("modifyUser"));
            msg.setModifyTime((Date) row.get("modifyTime"));
            msg.setDataLastDate((Date) row.get("dataLastDate"));
            searchList.add(msg);

        }
        returnMsg = "get Case Data successfully";
        return new RestReturnMsg(200, returnMsg, searchList);
    }

    /**
     * @param id
     * @return
     * @date 2019/5/26 下午14:12
     * @description 用ID找出Case
     */
    @ApiOperation("用ID找出Case")
    @GetMapping("/analysis/{id}")
    public @ResponseBody
    RestReturnMsg findByCaseId(@PathVariable Long id) {
        Case caseInfo = caseRepository.findById(id).get();
        return new RestReturnMsg(200, "get Case by code successfully", caseInfo.toCaseMsg(true, dataSetRepository));
    }

    /**
     * @return
     * @date 2019/7/4 下午 15:45
     * @description 複製分析集
     */
    @ApiOperation("複製分析集 ")
    @GetMapping("/analysis/{id}/copy")
    public @ResponseBody
    RestReturnMsg copyCaseById(
            @PathVariable Long id, @RequestBody CaseCopyMsg copyMsg) {
        User currentUser = userRepository
                .findByUsername((String) SecurityUtils.getSubject().getSession()
                        .getAttribute(UserController.USERNAME_SESSION_KEY));
        int newID = 0;
        String headString = "insert into `case`(data_set_id,name,remark,create_user,create_time,modify_user,modify_time,setting_json,data_last_date)";
        headString += " select data_set_id,'" + copyMsg.getName() + "',remark,'" + currentUser.getUsername() + "',now(),'" + currentUser.getUsername() + "',now(),setting_json,now() ";
        headString += " from  `case` where id = " + id;

        StringBuffer sqlCmd = new StringBuffer("").append(headString);
        mysqlJtl.execute(sqlCmd.toString());
        Map<String, Object> result = new HashMap<String, Object>();
        headString = "select LAST_INSERT_ID()";
        List<Map<String, Object>> resultList = mysqlJtl.queryForList(headString);
        if (resultList.size() > 0) {
            newID = ((BigInteger) resultList.get(0).get("LAST_INSERT_ID()")).intValue();
            String sql = "Create table `case_dataset_bigtable@" + newID + "` like `case_dataset_bigtable@" + id + "`";
            mysqlJtl.execute(sql);
            sql = "insert into `case_dataset_bigtable@" + newID + "` select * from `case_dataset_bigtable@" + id + "`";
            mysqlJtl.execute(sql);
            sql = "insert into `case_bigtable_column_mapping` select " + newID + ", source_name,encode_name,description " +
                    "FROM case_bigtable_column_mapping WHERE caseID = " + id;
            mysqlJtl.execute(sql);
        }

        headString = " id,name,remark,create_user as createUser,create_time as createTime,modify_user as modifyUser,modify_time as modifyTime,setting_json as settingJson,data_last_date as dataLastDate ";
        sqlCmd = new StringBuffer("select ").append(headString).append(" from `case` ").append(" where id =" + newID);
        resultList = mysqlJtl.queryForList(sqlCmd.toString());
        result = resultList.get(0);
        return new RestReturnMsg(200, "copy By caseId successfully", result);
    }

    /**
     * @param id
     * @return
     * @date 2019/5/26 下午14:12
     * @description 用ID 刪除 Case
     */
    @ApiOperation("用ID 刪除 Case")
    @RequiresAuthentication
    @DeleteMapping("/analysis/{id}")
    public @ResponseBody
    RestReturnMsg deleteCaseById(@PathVariable Long id) {
        User currentUser = userRepository
                .findByUsername((String) SecurityUtils.getSubject().getSession()
                        .getAttribute(UserController.USERNAME_SESSION_KEY));
        Case caseInfo = caseRepository.findById(id).get();
        caseService.deleteCase(currentUser, caseInfo);
        return new RestReturnMsg(200, "delete Case by id successfully", id);
    }

    /**
     * @param caseMsg
     * @return com.foxconn.iisd.rcadsvc.msg.RestReturnMsg
     * @date 2019/7/3  04:32
     * @description 更新Case Data分析集小表
     */
    @ApiOperation("更新Case Data分析集小表[Case用]")
    @RequiresAuthentication
    @PutMapping("/analysis/{id}/pool/define")
    public @ResponseBody
    RestReturnMsg updateCaseData(@RequestBody CaseMsg caseMsg, @PathVariable Long id) {
        Subject subject = SecurityUtils.getSubject();
        // 檢查用戶是否已驗證 , 是將回傳true
        logger.info("===> 檢查用戶是否已驗證 : " + subject.isAuthenticated());
        HashMap dataMap = new HashMap<String, Object>();
        Case dbCase = null;
        String msg = "";
        int code = 200;
        try {
            if (subject.isAuthenticated()) {
                User currentUser = userRepository
                        .findByUsername((String) SecurityUtils.getSubject().getSession()
                                .getAttribute(UserController.USERNAME_SESSION_KEY));

                dbCase = caseRepository.findById(id).isPresent() ? caseRepository.findById(id).get() : null;
                if (dbCase != null) {
                    Long caseId = dbCase.getId();
                    //String settingJson = caseMsg.getSettingJson();
                    JSONObject settingJson = new JSONObject(caseMsg.getSettingJson());
                    JSONArray selectField = (JSONArray) settingJson.get("selectField");
                    JSONArray createField = (JSONArray) settingJson.get("createField");
                    JSONArray conditionField = (JSONArray) settingJson.get("condition");
                    JSONArray filterField = (JSONArray) settingJson.get("filter");

//                List<Map<String, Object>> selectField = getTestField(dbCase.getDssId());
//                List<Map<String, Object>> createField = new ArrayList<>();
//                List<Map<String, Object>> conditionField = new ArrayList<>();
//                List<Map<String, Object>> filterField  = new ArrayList<>();
                    //List<Map<String, Object>> selectField = this.getTestGenCaseDataObj("selectField");
                    //List<Map<String, Object>> conditionField = this.getTestGenCaseDataObj("condition");
                    //List<Map<String, Object>> filterField = this.getTestGenCaseDataObj("filter");

                    /** 新建立case分析表**/
                    int maxvarchar = 100;
                    int minvarchar = 50;
                    String newTableName = xwjUtil.addSQLCHAR(caseTableName + caseId + "_new").toString();
                    String oldTableName = xwjUtil.addSQLCHAR(caseTableName + caseId).toString();
                    String dataSetTableName = "`data_set_bigtable@" + dbCase.getDssId() + "` ";
                    StringBuffer createSB = new StringBuffer();
                    StringBuffer insertSB = new StringBuffer();
                    createSB.append(" CREATE TABLE ").append(newTableName).append(" (");
                    insertSB.append(" INSERT INTO  ").append(newTableName).append("  SELECT ");
                    //for(Map<String,Object> fieldObj : selectField){
                    List<String> addField = new ArrayList<>();
                    List<String> testField = new ArrayList<>();
                    for (int idx = 0; idx < selectField.length(); ++idx) {
                        JSONObject fieldObj = selectField.getJSONObject(idx);
                        String type = fieldObj.get("type").toString();
                        String name = fieldObj.get("name").toString();
                        addField.add(name);
                        if (type.equals("fixed")) {
                            int size = minvarchar;
                            if (name.equals("data_set_name"))
                                size = 200;
                            if (name.equals("scantime")) {
                                createSB.append(" `").append(name).append("` varchar(").append(size).append(") , ");
                                insertSB.append(xwjUtil.addSQLCHAR(name)).append(",");
                            } else {
                                createSB.append(" `").append(name).append("` varchar(").append(size).append(") , ");
                                insertSB.append("ifnull(").append(xwjUtil.addSQLCHAR(name)).append(",null").append(") as ").append(xwjUtil.addSQLCHAR(name)).append(",");
                            }

                        } else {
                            if (type.equals("station_info")) {
                                if (!name.contains("@test_starttime")) {
                                    createSB.append(" `").append(name).append("` varchar(").append(maxvarchar).append(") , ");
                                    insertSB.append("ifnull(").append(type).append("->>'$.\"").append(name.trim()).append("\"'").append(",null) as ").append(xwjUtil.addSQLCHAR(name)).append(",");
                                    String station = name.split("@")[0];
                                    station = station + "@test_starttime";
                                    if (!testField.contains(station)) {
                                        createSB.append(" `").append(station).append("` DATETIME , ");
                                        insertSB.append("ifnull(").append(type).append("->>'$.\"").append(station).append("\"'").append(",null) as ").append(xwjUtil.addSQLCHAR(station)).append(",");
                                        testField.add(station);
                                    }
                                }
                            } else {
                                createSB.append(" `").append(casefiledutil.setEncodeColumn(name, caseId, mysqlJtl)).append("` varchar(").append(maxvarchar).append(") , ");
                                if (!type.equals("item_info")) {
                                    insertSB.append("ifnull(").append(type).append("->>'$.\"").append(name.trim()).append("\"'").append(",null) as ").append(xwjUtil.addSQLCHAR(name)).append(",");
                                } else {
                                    insertSB.append("if(").append(type).append("->>'$.\"").append(name.trim()).append("\"' ='null'").append(",null,").append(type).append("->>'$.\"").append(name.trim()).append("\"') as ").append(xwjUtil.addSQLCHAR(name)).append(",");
                                    String str = name + "@result";
                                    String sql = "select column_name from data_set_bigtable_columns where column_name = '" + str + "' and data_set_id = " + dbCase.getDssId();
                                    List<Map<String, Object>> resultList = mysqlJtl.queryForList(sql);
                                    if (resultList.size() > 0) {
                                        createSB.append(" `").append(casefiledutil.setEncodeColumn(name, caseId, mysqlJtl)).append("@result").append("` varchar(").append(25).append(") NOT NULL, ");
                                        insertSB.append("ifnull(").append(type).append("->>'$.\"").append(name.trim()).append("@result").append("\"'").append(",'') as ").append(xwjUtil.addSQLCHAR(name + "@result")).append(",");
                                    }
                                    String station = name.split("@")[0];
                                    station = station + "@test_starttime";
                                    if (!testField.contains(station)) {
                                        sql = "select column_name from data_set_bigtable_columns where column_name = '" + station + "' and data_set_id = " + dbCase.getDssId();
                                        resultList = mysqlJtl.queryForList(sql);
                                        if (resultList.size() > 0) {
                                            createSB.append(" `").append(station).append("` DATETIME , ");
                                            insertSB.append("ifnull(").append("station_info").append("->>'$.\"").append(station).append("\"'").append(",null) as ").append(xwjUtil.addSQLCHAR(station)).append(",");
                                            testField.add(station);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    //預設都要有的欄位(`data_set_id`,`product`,`sn`,`value_rank`),用來當PK Key
                    String[] pkField = new String[]{"data_set_id", "product", "sn", "value_rank", "scantime"};
                    for (String pkf : pkField) {
                        if (!addField.contains(pkf)) {
                            if (pkf.equals("scantime")) {
                                createSB.append(" `").append(pkf).append("` varchar(").append(50).append(") , ");
                                insertSB.append(xwjUtil.addSQLCHAR(pkf)).append(",");
                            } else {
                                createSB.append(" `").append(pkf).append("` varchar(").append(minvarchar).append(") NOT NULL, ");
                                insertSB.append("ifnull(").append(xwjUtil.addSQLCHAR(pkf)).append(",null").append(") as ").append(xwjUtil.addSQLCHAR(pkf)).append(",");
                            }
                        }
                    }
                    //for(Map<String,Object> fieldObj : createField){
                    for (int idx = 0; idx < createField.length(); ++idx) {
                        JSONObject fieldObj = createField.getJSONObject(idx);
                        String dtType = fieldObj.get("dataType").toString();
                        String type = fieldObj.get("type").toString();
                        String name = fieldObj.get("name").toString();
                        String newname = fieldObj.get("newname").toString();
                        if (dtType.equals("string")) {
                            int startIdx = Integer.parseInt(fieldObj.get("start").toString());
                            int len = Integer.parseInt(fieldObj.get("length").toString());
                            if (type.equals("fixed")) {
                                createSB.append(" `").append(newname).append("` varchar(").append(minvarchar).append(") , ");
                                insertSB.append("IFNULL(SUBSTRING(").append(xwjUtil.addSQLCHAR(name)).append(",").append(startIdx).append(",").append(len).append("),'') as ").append(xwjUtil.addSQLCHAR(newname)).append(",");
                            } else {
                                createSB.append(" `").append(newname).append("` varchar(").append(maxvarchar).append(") , ");
                                insertSB.append("IFNULL(SUBSTRING(").append(type).append("->>'$.\"").append(name).append("\"'").append(",").append(startIdx).append(",").append(len).append("),null) as ").append(xwjUtil.addSQLCHAR(newname)).append(",");
                            }
                        } else {
                            int times = Integer.parseInt(fieldObj.get("times").toString());
                            int precision = Integer.parseInt(fieldObj.get("precision").toString());
                            if (type.equals("fixed")) {
                                int size = minvarchar;
                                if (name.equals("data_set_name"))
                                    size = maxvarchar;
                                createSB.append(" `").append(newname).append("` varchar(").append(size).append(") , ");
                                //insertSB.append("ifnull(").append(xwjUtil.addSQLCHAR(name)).append(",''").append(") as ").append(xwjUtil.addSQLCHAR(name)).append(",");
                                //insertSB.append("ISNULL(SUBSTRING(").append(xwjUtil.addSQLCHAR(name)).append(",").append(startIdx).append(",").append(len).append("),'') as ").append(xwjUtil.addSQLCHAR(newname)).append(",");
                                insertSB.append("CAST((IFNULL(").append(xwjUtil.addSQLCHAR(name)).append(",0)*").append(times).append(") as DECIMAL(9,").append(precision).append(")) as ").append(xwjUtil.addSQLCHAR(newname)).append(",");

                            } else {
                                createSB.append(" `").append(newname).append("` varchar(").append(maxvarchar).append(") , ");
                                //insertSB.append("ifnull(").append(type).append("->>'$.\"").append(name.trim()).append("\"'").append(",'') as ").append(xwjUtil.addSQLCHAR(name)).append(",");
                                //insertSB.append("ISNULL(SUBSTRING(").append(type).append("->>'$.\"").append(name).append("\"'").append(",").append(startIdx).append(",").append(len).append("),'') as ").append(xwjUtil.addSQLCHAR(newname)).append(",");
                                insertSB.append("CAST((IFNULL(").append(type).append("->>'$.\"").append(name).append("\"'").append(",0)*").append(times).append(") as DECIMAL(9,").append(precision).append(")) as ").append(xwjUtil.addSQLCHAR(newname)).append(",");
                            }
                        }

                    }
                    createSB.append(" PRIMARY KEY (`data_set_id`,`product`,`sn`,`value_rank`)  )ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");
                    String insertSQL = insertSB.substring(0, insertSB.toString().length() - 1) + " FROM " + dataSetTableName;
                    StringBuffer whereSb = new StringBuffer();
                    whereSb.append(" WHERE 1=1 ");
                    //for(Map<String, Object> field : conditionField)
                    for (int idx = 0; idx < conditionField.length(); ++idx) {
                        JSONObject fieldObj = conditionField.getJSONObject(idx);
                        whereSb.append(" AND ").append(xwjUtil.convertJSONToCondition(fieldObj));
                    }
                    //for(Map<String, Object> field : filterField)
                    for (int idx = 0; idx < filterField.length(); ++idx) {
                        JSONObject fieldObj = filterField.getJSONObject(idx);
                        whereSb.append(" AND ").append(xwjUtil.convertJSONToFilterCondition(fieldObj));
                    }
                    mysqlJtl.execute(createSB.toString());

                    /** 將資料塞入新的case分析表**/
                    System.out.println(insertSQL + whereSb.toString());
                    mysqlJtl.execute(insertSQL + whereSb.toString());

                    /** 刪除 Case 舊分析表**/
                    mysqlJtl.execute(" DROP TABLE IF EXISTS " + oldTableName);

                    /** 更名新Case分析檔**/
                    mysqlJtl.execute("RENAME TABLE " + newTableName + " TO " + oldTableName);

                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    dbCase.setDataLastDate((new Date()));
                    dbCase.setSettingJson(caseMsg.getSettingJson());
                    //if(caseMsg.getName()==null || caseMsg.getName().equals("")) caseMsg.setName(dbCase.getName());
                    //if(caseMsg.getRemark()==null || caseMsg.getRemark().equals("")) caseMsg.setRemark(dbCase.getRemark());
                    dbCase = caseService.updateCase(currentUser, dbCase, id);
//                dataMap.put(dbCase.getId(), dbCase.toCaseMsg(false, dataSetRepository));
                    msg = "update case data successfully!!";
                } else {
                    msg = "case not exist!!";
                }

            } else {
                logger.info("==> This user has not logged in");
                msg = "This user has not logged in";
                dataMap.put("error", "error");
                code = 401;
            }
        } finally {
            System.out.println("最後刪暫存table");
            mysqlJtl.execute(" DROP TABLE IF EXISTS `case_dataset_bigtable@" + id + "_new`");
        }
//        dataMap.put("code", code);
        return new RestReturnMsg(code, msg, dbCase == null ? null : dbCase.toCaseMsg(false, dataSetRepository));
    }

    /**
     * @param id
     * @return
     * @date 2019/7/5 早上 09:04
     * @description 取得該Case的所有欄位資訊
     */
    @ApiOperation("取得該Case的所有欄位資訊 ")
    @GetMapping("/analysis/{id}/pool/properties")
    public @ResponseBody
    RestReturnMsg getCaseField(
            @PathVariable Long id) {
        List<CaseFiledMsg> filedlist = new ArrayList<>();
        String datasetId;
        String datasetIdSql = "Select data_set_id from `case` where id =" + id;
        List<Map<String, Object>> resultIdList = mysqlJtl.queryForList(datasetIdSql);
        datasetId = resultIdList.get(0).get("data_set_id").toString();
        String showCloumnSql = "SHOW  COLUMNS FROM `case_dataset_bigtable@" + id + "` where Field not like '%@result' and Field not like '%@test_starttime' " +
                " and Field != 'sn' and Field != 'scantime' ";
        List<Map<String, Object>> resultList = mysqlJtl.queryForList(showCloumnSql);
        List<String> fieldList = new ArrayList<>();
        for (Map<String, Object> row : resultList)
            fieldList.add("'" + new CaseFiledUtil().getDecodeColumn((String) row.get("Field"), id, mysqlJtl) + "'");
        String filed = String.join(",", fieldList);

        String datasetInfoSql = "Select column_name,column_type,json_str from data_set_bigtable_columns where data_set_id ='" + datasetId + "' and column_name in (" + filed + ")";
        resultList = mysqlJtl.queryForList(datasetInfoSql);
        for (Map<String, Object> row : resultList) {
            CaseFiledMsg msg = new CaseFiledMsg();
            msg.setDataType((String) row.get("column_type"));
            msg.setName((String) row.get("column_name"));
            msg.setType((String) row.get("json_str"));
            filedlist.add(msg);
            fieldList.remove("'" + (String) row.get("column_name") + "'");
        }
        if (fieldList.size() != 0) {
            for (String field : fieldList) {
                CaseFiledMsg msg = new CaseFiledMsg();
                msg.setDataType("string");
                msg.setName(field.replace("'", ""));
                msg.setType("fixed");
                filedlist.add(msg);
            }
        }

        HashMap dataMap = new HashMap<String, Object>();
        dataMap.put("field", filedlist);
        return new RestReturnMsg(200, "Export Case Filed successfully", dataMap);
    }

    /**
     * @param id
     * @return
     * @date 2019/7/5 早上 09:04
     * @description 取得該CaseMatrix的所有欄位資訊
     */
    @ApiOperation("取得該CaseMatrix的所有欄位資訊 ")
    @GetMapping("/analysis/{id}/pool/properties/type/numeric/test_items")
    public @ResponseBody
    RestReturnMsg getItemInfoNumericFields(
            @PathVariable Long id) {
        List<CaseMatrixFieldMsg> fieldlist = new ArrayList<CaseMatrixFieldMsg>();
        String datasetId;
        String datasetIdSql = "Select data_set_id from `case` where id =" + id;
        List<Map<String, Object>> resultIdList = mysqlJtl.queryForList(datasetIdSql);
        datasetId = resultIdList.get(0).get("data_set_id").toString();
        String showCloumnSql = "SHOW  COLUMNS FROM `case_dataset_bigtable@" + id + "` where Field not like '%@result' ";
        List<Map<String, Object>> resultList = mysqlJtl.queryForList(showCloumnSql);
        List<String> fieldList = new ArrayList<>();
        for (Map<String, Object> row : resultList)
            fieldList.add("'" + new CaseFiledUtil().getDecodeColumn((String) row.get("Field"), id, mysqlJtl) + "'");
        String field = String.join(",", fieldList);

        String datasetInfoSql = "Select column_name, "
                + " column_type, "
                + " json_str "
                + " from data_set_bigtable_columns "
                + " where data_set_id ='" + datasetId + "' "
                + " and column_type in ('float', 'int') "
                + " and json_str = 'item_info' "
                + " and column_name in (" + field + ") ";
        resultList = mysqlJtl.queryForList(datasetInfoSql);
        for (Map<String, Object> row : resultList) {
            CaseMatrixFieldMsg msg = new CaseMatrixFieldMsg();
            msg.setDataType((String) row.get("column_type"));
            msg.setName((String) row.get("column_name"));
            msg.setType((String) row.get("json_str"));
            List<Double> testLimit = getTestLimit(id, (String) row.get("column_name"));
            if (testLimit.size() == 2) {
                if (testLimit.get(0) != null) {
                    msg.setTestLower(testLimit.get(0));
                }
                if (testLimit.get(1) != null) {
                    msg.setTestUpper(testLimit.get(1));
                }
            }
            fieldlist.add(msg);
        }

        HashMap<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("field", fieldlist);
        return new RestReturnMsg(200, "get CaseMatrix Field successfully", dataMap);
    }

    private List<Double> getTestLimit(long caseId, String testItem) {
        List<Double> testLimit = new ArrayList<Double>();
        try {
            String[] xfield_array = testItem.split("@");
            String station = "";
            String item = "";
            if (xfield_array.length == 2) {
                station = xfield_array[0];
                item = xfield_array[1];
            }
            String sql = "select test_upper, test_lower from `case` c "
                    + " left join data_set_station_item dssi on dssi.dss_id = c.data_set_id "
                    + " left join product_item_spec pis on dssi.product = pis.product COLLATE utf8mb4_unicode_ci and dssi.station = pis.station_name COLLATE utf8mb4_unicode_ci and dssi.item = pis.test_item COLLATE utf8mb4_unicode_ci "
                    + " where  c.id =" + caseId + " "
                    + " and dssi.station ='" + station + "' "
                    + " and dssi.item ='" + item + "' "
                    + " order by pis.test_version desc "
                    + " limit 1 ";
            List<Map<String, Object>> resultList = mysqlJtl.queryForList(sql);

            Double test_lower = null;
            Double test_upper = null;

            if (resultList.size() > 0) {
                test_lower = (resultList.get(0).get("test_lower") == null ? null : Double.parseDouble(String.valueOf(resultList.get(0).get("test_lower"))));
                test_upper = (resultList.get(0).get("test_upper") == null ? null : Double.parseDouble(String.valueOf(resultList.get(0).get("test_upper"))));
            }

            testLimit.add(test_lower);
            testLimit.add(test_upper);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return testLimit;
    }

    /**
     * @param id
     * @return
     * @date 2019/5/26 下午14:12
     * @description 找出此 caseId 下所有的CasePlot
     */
    @ApiOperation("找出此 caseId 下所有的CasePlot")
    @GetMapping("/analysis/{id}/plots")
    public @ResponseBody
    RestReturnMsg getPlotByCaseId(@PathVariable Long id) {
        List<CasePlot> casePlotList = casePlotRepository.findByCaseId(id);
        List<CasePlotMsg> casePlotMsgList = new ArrayList<CasePlotMsg>();
        for (CasePlot casePlotInfo : casePlotList) {
            casePlotMsgList.add(casePlotInfo.toCasePlotMsg(true));
        }
        return new RestReturnMsg(200, "get CasePlot list successfully", casePlotMsgList);
    }

    /**
     * @param casePlotMsg
     * @return com.foxconn.iisd.rcadsvc.msg.RestReturnMsg
     * @date 2019/6/3  10:32
     * @description 新增 CasePlot
     */
    @ApiOperation("新增CasePlot")
    @RequiresAuthentication
    @PostMapping("/analysis/{id}/plot")
    public @ResponseBody
    RestReturnMsg create(@RequestBody CasePlotMsg casePlotMsg, @PathVariable Long id) {
        logger.info("===> Create method");
        Integer code = null;
        String msg = null;
        CasePlot dbcasePlot = new CasePlot();
        Subject subject = SecurityUtils.getSubject();
        // 檢查用戶是否已驗證 , 是將回傳true
        logger.info("===> 檢查用戶是否已驗證 : " + subject.isAuthenticated());
        HashMap dataMap = new HashMap<String, Object>();

        code = 200;
        if (subject.isAuthenticated()) {
            User currentUser = userRepository
                    .findByUsername((String) SecurityUtils.getSubject().getSession()
                            .getAttribute(UserController.USERNAME_SESSION_KEY));
            Case findOne = caseRepository.findById(id).isPresent() ? caseRepository.findById(id).get() : null;
//            CasePlot findOne = casePlotRepository.findById(casePlotMsg.getId()).isPresent()?casePlotRepository.findById(casePlotMsg.getId()).get():null;
            if (findOne == null) {
                code = 404;
                msg = "The Case " + id + " not exists!!";
//                dataMap.put("404", "not found");
            } else {
                msg = "Create CasePlot successfully!!";
                casePlotMsg.setCaseId(id);
                dbcasePlot = caseService.createCasePlot(currentUser, casePlotMsg.toNewDDS());
                dataMap.put("data", dbcasePlot.toCasePlotMsg(true));
            }

        } else {
            logger.info("==> This user has not logged in");
            msg = "This user has not logged in";
            dataMap.put("error", "error");
            code = 401;

        }
//        dataMap.put("code", code);
        return new RestReturnMsg(code, msg, dbcasePlot.toCasePlotMsg(true));
    }

    /**
     * @param casePlotMsg
     * @return com.foxconn.iisd.rcadsvc.msg.RestReturnMsg
     * @date 2019/6/3  10:32
     * @description 更新 CasePlot
     */
    @ApiOperation("更新 CasePlot")
    @RequiresAuthentication
    @PutMapping("/analysis/plots/{id}")
    public @ResponseBody
    RestReturnMsg update(@RequestBody CasePlotMsg casePlotMsg, @PathVariable Long id) {
        logger.info("===> Update method");
        Integer code = null;
        String msg = null;
        CasePlot dbcaseplot = null;

        Subject subject = SecurityUtils.getSubject();
        // 檢查用戶是否已驗證 , 是將回傳true
        logger.info("===> 檢查用戶是否已驗證 : " + subject.isAuthenticated());
        HashMap dataMap = new HashMap<String, Object>();

        code = 200;
        if (subject.isAuthenticated()) {
            User currentUser = userRepository
                    .findByUsername((String) SecurityUtils.getSubject().getSession()
                            .getAttribute(UserController.USERNAME_SESSION_KEY));
            //User currentUser = userRepository.findByUsername("admin");
            CasePlot findOne = casePlotRepository.findById(id).isPresent() ? casePlotRepository.findById(id).get() : null;
            if (findOne == null) {
                code = 404;
                msg = "The CasePlot " + casePlotMsg.getId() + " not exists!!";
                dataMap.put("404", "not found");
            } else {
                msg = "Update CasePlot successfully!!";
                dbcaseplot = caseService.updateCasePlot(currentUser, casePlotMsg.toNewDDS(), id);
//                dataMap.put(dbcaseplot.getId(),dbcaseplot.toCasePlotMsg(true));
            }
        } else {
            logger.info("==> This user has not logged in");
            msg = "This user has not logged in";
            dataMap.put("error", "error");
            code = 401;

        }
//        dataMap.put("code", code);
        return new RestReturnMsg(code, msg, dbcaseplot == null ? null : dbcaseplot.toCasePlotMsg(true));
    }

    /**
     * @param id
     * @return
     * @date 2019/5/26 下午14:12
     * @description 用ID 刪除 CasePlot
     */
    @ApiOperation("用ID 刪除 CasePlot")
    @RequiresAuthentication
    @DeleteMapping("/analysis/plots/{id}")
    public @ResponseBody
    RestReturnMsg deletePlotById(@PathVariable Long id) {
        User currentUser = userRepository
                .findByUsername((String) SecurityUtils.getSubject().getSession()
                        .getAttribute(UserController.USERNAME_SESSION_KEY));
        CasePlot casePlotInfo = casePlotRepository.findById(id).get();
        caseService.deleteCasePlot(currentUser, casePlotInfo);
        return new RestReturnMsg(200, "delete CasePlot by id successfully", id);
    }

    /**
     * @param id
     * @return
     * @description 回傳某plot繪圖所需要的(依據Plot設定)資料
     */
    @ApiOperation("回傳某plot繪圖所需要的(依據Plot設定)資料")
    @RequiresAuthentication
    @GetMapping("/analysis/plots/{id}/chart")
    public @ResponseBody
    RestReturnMsg getPlotDataById(@PathVariable Long id) {
        String returnMsg = "getPlotDataById Done!!";
        Object plotData = this.genPlotDataSetResult(id);
        return new RestReturnMsg(200, returnMsg, plotData);
    }

    private Object genPlotDataSetResult(Long plotId) {

        CasePlot plot = casePlotRepository.findById(plotId).get();

        JSONObject settingJson = new JSONObject(plot.getPlotJson());
        PlotMsg plotSetting = new PlotMsg(settingJson);
        plotSetting.setCaseId(plot.getCaseId());
        StringBuffer sb = new StringBuffer();

        String xField = plotSetting.getxField() == null ? null : casefiledutil.getEncodeColumn(plotSetting.getxField(), plot.getCaseId(), mysqlJtl);
        String yField = plotSetting.getyField() == null ? null : casefiledutil.getEncodeColumn(plotSetting.getyField(), plot.getCaseId(), mysqlJtl);
        String groupField = plotSetting.getGroupField() == null ? null : casefiledutil.getEncodeColumn(plotSetting.getGroupField(), plot.getCaseId(), mysqlJtl);
        String groupField2 = plotSetting.getGroupField2() == null ? null : casefiledutil.getEncodeColumn(plotSetting.getGroupField2(), plot.getCaseId(), mysqlJtl);

        if (plot.getPlotType() == 1) { //scatter
            sb.append("SELECT ").append(xwjUtil.addSQLCHAR(xField)).append(" , ").append(xwjUtil.addSQLCHAR(yField));
            if (plotSetting.getGroupField() != null)
                sb.append(" , ").append("if(" + xwjUtil.addSQLCHAR(groupField) + " is null,'空白'," + xwjUtil.addSQLCHAR(groupField) + ") as " + xwjUtil.addSQLCHAR(groupField));
            sb.append(" FROM ").append(xwjUtil.addSQLCHAR(caseTableName + plot.getCaseId()));
            sb.append(" WHERE ").append(xwjUtil.addSQLCHAR(xField)).append("is not null and ").append(xwjUtil.addSQLCHAR(xField)).append("!= 'null' and ").append(xwjUtil.addSQLCHAR(yField)).append(" is not null and ").append(xwjUtil.addSQLCHAR(yField)).append(" != 'null'");
            List<Map<String, Object>> fieldList = mysqlJtl.queryForList(sb.toString());
            AbstractChartUtil chart = new ScatterChartUtil(plotSetting, plot.getUpdateTime(), mysqlJtl);
            chart.calculationData(fieldList);
            return chart.getResultObject();
        } else if (plot.getPlotType() == 2) { //boxplot
            sb.append("SELECT ").append(xwjUtil.addSQLCHAR(yField));
            if (plotSetting.getGroupField() != null) {
                sb.append(" , ").append("if(" + xwjUtil.addSQLCHAR(groupField) + " is null,'空白'," + xwjUtil.addSQLCHAR(groupField) + ") as " + xwjUtil.addSQLCHAR(groupField));
            }
            sb.append(" FROM ").append(xwjUtil.addSQLCHAR(caseTableName + plot.getCaseId()));
            sb.append(" where ").append(xwjUtil.addSQLCHAR(yField)).append(" is not null and ").append(xwjUtil.addSQLCHAR(yField)).append(" != 'null' ");
            sb.append(" order by ").append("cast( ").append(xwjUtil.addSQLCHAR(yField)).append(" as DECIMAL)").append(" asc");
            List<Map<String, Object>> fieldList = mysqlJtl.queryForList(sb.toString());
            AbstractChartUtil chart = new BoxChartUtil(plotSetting, plot.getCaseId(), mysqlJtl, plot.getUpdateTime());
            chart.calculationData(fieldList);
            return chart.getResultObject();
        } else if (plot.getPlotType() == 3) { //Normal Distribution Plot
            sb.append("SELECT ").append(xwjUtil.addSQLCHAR(plotSetting.getxField()));
            sb.append(" FROM ").append(xwjUtil.addSQLCHAR(caseTableName + plot.getCaseId()));
            List<Map<String, Object>> fieldList = mysqlJtl.queryForList(sb.toString());
            AbstractChartUtil chart = new NDChartUtil(plotSetting, plot.getCaseId(), mysqlJtl, plot.getUpdateTime());
            chart.calculationData(fieldList);
            return chart.getResultObject();
        } else if (plot.getPlotType() == 4) { //barchart
            List<Map<String, Object>> fieldList = new ArrayList<>();
            String sql = "";
            if (plotSetting.getGroupField2() == null) {
                sql = "select sum(if(`" + yField + "@result` = 'fail', 1, 0))/count(*) as fail_rate ";
                sql += ", if(" + xwjUtil.addSQLCHAR(groupField) + " is null,'空白'," + xwjUtil.addSQLCHAR(groupField) + ")" +
                        " as " + xwjUtil.addSQLCHAR(groupField);
                sql += " from " + xwjUtil.addSQLCHAR(caseTableName + plot.getCaseId());
                sql += " group by " + xwjUtil.addSQLCHAR(groupField);
            } else {
                sql = "select sum(if(`" + yField + "@result` = 'fail', 1, 0))/count(*) as fail_rate ";
                sql += ", if(" + xwjUtil.addSQLCHAR(groupField) + " is null,'空白',"
                        + xwjUtil.addSQLCHAR(groupField) + ") as " + xwjUtil.addSQLCHAR(groupField);
                sql += ", if(" + xwjUtil.addSQLCHAR(groupField2) + " is null,'空白'," + xwjUtil.addSQLCHAR(groupField2) + ") as "
                        + xwjUtil.addSQLCHAR(groupField2);
                sql += " from " + xwjUtil.addSQLCHAR(caseTableName + plot.getCaseId());
                sql += " group by " + xwjUtil.addSQLCHAR(groupField);
                sql += " ," + xwjUtil.addSQLCHAR(groupField2);
            }
            System.out.println(sql);
            fieldList = mysqlJtl.queryForList(sql);
            AbstractChartUtil chart = new BarChartUtil(plotSetting, plot.getUpdateTime(), mysqlJtl);
            chart.calculationData(fieldList);
            return chart.getResultObject();
        } else if (plot.getPlotType() == 5) { //Line Chart
            sb.append("SELECT ").append(xwjUtil.addSQLCHAR(plotSetting.getxField()));
            sb.append(" FROM ").append(xwjUtil.addSQLCHAR(caseTableName + plot.getCaseId()));
            List<Map<String, Object>> fieldList = mysqlJtl.queryForList(sb.toString());
            AbstractChartUtil chart = new LineChartUtil(plotSetting, plot.getCaseId(), mysqlJtl, plot.getUpdateTime());
            chart.calculationData(fieldList);
            return chart.getResultObject();
        } else {
            AbstractChartUtil chart = new CommonChartUtil(plotSetting, plot.getCaseId(), plot.getUpdateTime());
            List<Map<String, Object>> fieldList = new ArrayList<>();
            chart.calculationData(fieldList);
            return chart.getResultObject();
        }
    }

    /**
     * @param casePlotNoteMsg
     * @return com.foxconn.iisd.rcadsvc.msg.RestReturnMsg
     * @date 2019/6/3  10:32
     * @description 新增 CasePlotNote
     */
    @ApiOperation("新增CasePlotNote")
    @RequiresAuthentication
    @PostMapping("/analysis/plots/{id}/capture")
    public @ResponseBody
    RestReturnMsg create(@RequestBody CasePlotNoteMsg casePlotNoteMsg, @PathVariable Long id) {
        logger.info("===> Create method");
        Integer code = null;
        String msg = null;
        String jsonContent = casePlotNoteMsg.getDataJson();
        casePlotNoteMsg.setDataJson(null);
        Subject subject = SecurityUtils.getSubject();
        // 檢查用戶是否已驗證 , 是將回傳true
        logger.info("===> 檢查用戶是否已驗證 : " + subject.isAuthenticated());
        HashMap dataMap = new HashMap<String, Object>();

        code = 200;
        if (subject.isAuthenticated()) {
            User currentUser = userRepository
                    .findByUsername((String) SecurityUtils.getSubject().getSession()
                            .getAttribute(UserController.USERNAME_SESSION_KEY));
            CasePlot findOne = casePlotRepository.findById(id).isPresent() ? casePlotRepository.findById(id).get() : null;
            if (findOne == null) {
                code = 404;
                msg = "The CasePlot " + casePlotNoteMsg.getPlotId() + " not exists!!";
                dataMap.put("404", "not found");
            } else {
                msg = "Create CasePlotNote successfully!!";
                casePlotNoteMsg.setPlotId(id);
                CasePlotNote dbcasePlotNote = caseService.createCasePlotNote(currentUser, casePlotNoteMsg.toNewDDS());
                if (jsonContent != null) {
                    String path = WriteNoteFile(jsonContent, dbcasePlotNote.getId());
                    casePlotNoteMsg.setFilePath(path);
                    caseService.updateCasePlotNote(currentUser, casePlotNoteMsg.toNewDDS(), dbcasePlotNote.getId());
                }
                dataMap.put("data", dbcasePlotNote.toCasePlotNoteMsg(true));
            }
        } else {
            logger.info("==> This user has not logged in");
            msg = "This user has not logged in";
            dataMap.put("error", "error");
            code = 401;
        }
        dataMap.put("code", code);
        return new RestReturnMsg(code, msg, dataMap);
    }

    public String WriteNoteFile(String dataJson, Long id) {
        File fw = null;
        try {
            Date now = new Date();
            int year = now.getYear() + 1900;
            int month = now.getMonth() + 1;
            int day = now.getDate();
            String monthStr = "";
            String dayStr = "";
            if (month < 10)
                monthStr = "0" + month;
            else
                monthStr = Integer.toString(month);
            if (day < 10)
                dayStr = "0" + day;
            else
                dayStr = Integer.toString(day);
            String path = filePath + notePath + "/" + year + "/" + monthStr + "/" + dayStr + "/";
            fw = new File(path);
            if (!fw.isDirectory()) {
                fw.mkdirs();
            }
            fw = new File(path + id + ".txt");
            System.out.println("SavePath:" + path + id + ".txt");
            fw.createNewFile(); // 建立新檔案
            BufferedWriter out = new BufferedWriter(new FileWriter(fw));
            out.write(dataJson);
            out.flush();
            out.close();
            path = year + "/" + monthStr + "/" + dayStr + "/" + id + ".txt";
            return path;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param id
     * @return
     * @date 2019/7/4 下午 13:45
     * @description 取得該Case的所有筆記
     */
    @ApiOperation("取得該Case的所有筆記 ")
    @GetMapping("/analysis/{id}/captures")
    public @ResponseBody
    RestReturnMsg exportAllNote(
            @PathVariable Long id) {

        List<CasePlotNote> casePlotNoteInfo = casePlotNoteRepository.findByCaseId(id);
        List<CasePlotNoteMsg> resultList = new ArrayList<>();
        for (CasePlotNote note : casePlotNoteInfo) {
            CasePlotNoteMsg temp = note.toCasePlotNoteMsg(false);
            if (temp.getFilePath() != null) {
                File fr = null;
                try {
                    fr = new File(filePath + notePath + "/" + temp.getFilePath());
                    System.out.println("Path:" + filePath + notePath + "/" + temp.getFilePath());
                    FileInputStream fis = new FileInputStream(fr);
                    BufferedInputStream bis = new BufferedInputStream(fis);
                    byte[] b = new byte[128];
                    int len;
                    String dataJson = "";
                    while ((len = bis.read(b)) != -1) {
                        dataJson += new String(b, 0, len);
                    }
                    temp.setDataJson(dataJson);
                    bis.close();
                    fis.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            resultList.add(temp);
        }
        return new RestReturnMsg(200, "Export AlltNote by caseId successfully", resultList);
    }

    /**
     * @param casePlotNoteMsg
     * @return com.foxconn.iisd.rcadsvc.msg.RestReturnMsg
     * @date 2019/6/3  10:32
     * @description 更新 CasePlotNote
     */
    @ApiOperation("更新 CasePlotNote")
    @RequiresAuthentication
    @PutMapping("/analysis/captures/{id}")
    public @ResponseBody
    RestReturnMsg update(@RequestBody CasePlotNoteMsg casePlotNoteMsg, @PathVariable Long id) {
        logger.info("===> Update method");
        Integer code = null;
        String msg = null;
        casePlotNoteMsg.setId(id);
        Subject subject = SecurityUtils.getSubject();
        // 檢查用戶是否已驗證 , 是將回傳true
        logger.info("===> 檢查用戶是否已驗證 : " + subject.isAuthenticated());
        HashMap dataMap = new HashMap<String, Object>();
        CasePlotNote dbcaseplotnote = null;
        code = 200;
        if (subject.isAuthenticated()) {
            User currentUser = userRepository
                    .findByUsername((String) SecurityUtils.getSubject().getSession()
                            .getAttribute(UserController.USERNAME_SESSION_KEY));
            //User currentUser = userRepository.findByUsername("admin");
            CasePlotNote findOne = casePlotNoteRepository.findById(casePlotNoteMsg.getId()).isPresent() ? casePlotNoteRepository.findById(casePlotNoteMsg.getId()).get() : null;
            if (findOne == null) {
                code = 404;
                msg = "The CasePlotNote " + casePlotNoteMsg.getId() + " not exists!!";
                dataMap.put("404", "not found");
            } else {
                msg = "Update CasePlotNote successfully!!";
                dbcaseplotnote = caseService.updateCasePlotNote(currentUser, casePlotNoteMsg.toNewDDS(), casePlotNoteMsg.getId());
//                dataMap.put(dbcaseplotnote.getId(),dbcaseplotnote.toCasePlotNoteMsg(true));
            }
        } else {
            logger.info("==> This user has not logged in");
            msg = "This user has not logged in";
            dataMap.put("error", "error");
            code = 401;
        }
//        dataMap.put("code", code);
        return new RestReturnMsg(code, msg, dbcaseplotnote == null ? null : dbcaseplotnote.toCasePlotNoteMsg(true));
    }

    /**
     * @param id
     * @return
     * @date 2019/5/26 下午14:12
     * @description 用ID找出CasePlotNote
     */
    @ApiOperation("用ID找出CasePlotNote")
    @GetMapping("/analysis/captures/{id}")
    public @ResponseBody
    RestReturnMsg findPlotNoteById(@PathVariable Long id) {
        String sql = "select cpn.id,note_title as noteTitle,note_text as noteText,note_remark as noteRemark ";
        sql += ",cpn.update_time as updateTime,cpn.create_user as createUser  ";
        sql += ",cpn.case_id as caseID,plot_id as plotId,plot_json as plotJson, data_json as dataJson,c.data_last_date as dataLastDate,c.setting_json as caseSettingJson";
        sql += " from `case_plot_note` cpn ";
        sql += " left join `case` c on c.id = cpn.case_id";
        sql += " where cpn.id = " + id;
        List<Map<String, Object>> resultList = mysqlJtl.queryForList(sql);
        Map<String, Object> result = new HashMap<String, Object>();
        if (resultList.size() > 0)
            result = resultList.get(0);
        return new RestReturnMsg(200, "get CasePlotNote by code successfully", result);
    }

    /**
     * @param id
     * @return
     * @date 2019/5/26 下午14:12
     * @description 用ID 刪除 CasePlotNote
     */
    @ApiOperation("用ID 刪除 CasePlotNote")
    @RequiresAuthentication
    @DeleteMapping("/analysis/captures/{id}")
    public @ResponseBody
    RestReturnMsg deletePlotNoteById(@PathVariable Long id) {
        User currentUser = userRepository
                .findByUsername((String) SecurityUtils.getSubject().getSession()
                        .getAttribute(UserController.USERNAME_SESSION_KEY));
        CasePlotNote casePlotNoteInfo = casePlotNoteRepository.findById(id).get();
        boolean isDelete = false;
        if (casePlotNoteInfo.getFilePath() != null) {
            File noteFile = new File(filePath + notePath + "/" + casePlotNoteInfo.getFilePath());
            if (noteFile.exists()) {
                try {
                    isDelete = noteFile.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (isDelete)
                caseService.deleteCasePlotNote(currentUser, casePlotNoteInfo);
            else
                return new RestReturnMsg(400, "delete CasePlotNote by id failure", id);
        } else
            caseService.deleteCasePlotNote(currentUser, casePlotNoteInfo);
        return new RestReturnMsg(200, "delete CasePlotNote by id successfully", id);
    }

    /**
     * @param id
     * @param page
     * @param size
     * @return
     * @description 取Case分析表資料(分頁顯示)
     */
    @ApiOperation("取Case分析表資料(分頁顯示)[Case用]")
    @RequiresAuthentication
    @GetMapping("/analysis/{id}/pool/page/{page}/size/{size}")
    public @ResponseBody
    RestReturnMsg pageCaseData(@PathVariable Long id, @PathVariable Integer page, @PathVariable Integer size) {

        if (page == null || page == 1 || page == 0) page = 1;
        if (size == null || size == 0) size = 10;
        page = page - 1; //將前端的pageNum轉成後端使用的pageNum

        //PageRequest的物件建構函式有多個，page是頁數，初始值是0，size是查詢結果的條數，後兩個引數參考Sort物件的構造方法
        Pageable pageable = new PageRequest(page, size, Sort.Direction.DESC, "sn");
        String tbName = xwjUtil.addSQLCHAR("case_dataset_bigtable@" + id).toString();
        String returnMsg = "";
        Map<String, Object> dataSetResult = new HashMap<String, Object>();
        System.out.println("Find Case Table:" + tbName);
        if (tbName == null || tbName.equals("")) {
            dataSetResult.put("head_name", new String[0]);
            dataSetResult.put("row_data", new String[0]);
            //dataSetResult.put("pageable",pageable);
            dataSetResult.put("totalPages", 0);
            dataSetResult.put("totalElements", 0);
            returnMsg = "successfully, but case data not created....";
        } else {
            List<String> selectList = this.genTableSelectField(tbName);
            Map<String, String[]> fieldMap = xwjUtil.convertToHeadAndSelectAry(selectList);
            String[] headAry = fieldMap.get("head");
            String[] selectAry = fieldMap.get("select");

            Page<Map<String, Object>> resultList = this.pageDataSetData(tbName, pageable, "", selectAry);

            List<Object> rowList = new ArrayList<Object>();
            for (Map<String, Object> row : resultList) {
                Object[] rowary = new String[headAry.length];
                for (int idx = 0; idx < headAry.length; ++idx) {
                    Object o = row.get(headAry[idx]);
                    if (o.getClass().equals(byte[].class)) {
                        rowary[idx] = new String((byte[]) o);
                    } else
                        rowary[idx] = o;
                }
                rowList.add(rowary);
            }
            for (int i = 0; i < headAry.length; i++) {
                headAry[i] = casefiledutil.getDecodeColumn(headAry[i], id, mysqlJtl);
            }
            dataSetResult.put("head_name", headAry);
            dataSetResult.put("row_data", rowList);
            dataSetResult.put("totalPages", resultList.getTotalPages());
            dataSetResult.put("totalElements", resultList.getTotalElements());
            returnMsg = "get Case Data successfully";
        }

        return new RestReturnMsg(200, returnMsg, dataSetResult);
    }

    /**
     * @param id
     * @return
     * @description 取Case分析表的欄位統計資訊
     */
    @ApiOperation("取Case分析表的欄位統計資訊[Case用]")
    @RequiresAuthentication
    @GetMapping("/analysis/{id}/pool/statistic")
    public @ResponseBody
    RestReturnMsg statisticsData(@PathVariable Long id) {

        String tbName = xwjUtil.addSQLCHAR("case_dataset_bigtable@" + id).toString();
        Map<String, Object> dataSetResult = new HashMap<String, Object>();

        Case caseObj = caseRepository.findById(id).isPresent() ? caseRepository.findById(id).get() : null;

        //將select指令區分欄位與一班指令
        List<String> selectList = this.genTableSelectField(tbName);
        Map<String, String[]> fieldMap = xwjUtil.convertToHeadAndSelectAry(selectList);
        String[] selectAry = fieldMap.get("select");

        List<String> numericalFieldList = new ArrayList<>();
        String datasetInfoSql = "Select column_name as name from data_set_bigtable_columns where data_set_id ='" + caseObj.getDssId() + "' and column_type in ('int','float')";
        List<Map<String, Object>> fieldResult = mysqlJtl.queryForList(datasetInfoSql);
        for (Map<String, Object> row : fieldResult) {
            numericalFieldList.add((String) row.get("name"));
        }

        String querySql = "SELECT " + String.join(",", selectAry) + " FROM " + tbName;
        List<Map<String, Object>> resultList = mysqlJtl.queryForList(querySql.toString());
        Map<String, Map<String, String>> dsResult = dataStatistic(resultList, numericalFieldList, id);

        dataSetResult.put("dataStatistic", dsResult);
        return new RestReturnMsg(200, "get dataStatistic table successfully", dataSetResult);
    }

    public Map<String, Map<String, String>> dataStatistic(List<Map<String, Object>> source, List<String> numericalFieldList, Long caseID) {
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
                field = casefiledutil.getDecodeColumn(field, caseID, mysqlJtl);
                resultMsg.put(field, map);
            }
        }

        return resultMsg;
    }

    public Map<String, List<String>> ListConvertMap(List<Map<String, Object>> list) {
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

    /**
     * @param id
     * @return
     * @date 2019/5/26 下午14:12
     * @description 用ID找出CaseMatrix
     */
    @ApiOperation("用ID找出CaseMatrix")
    @GetMapping("/analysis/matrices/{id}")
    public @ResponseBody
    RestReturnMsg findCaseMatrixById(@PathVariable Long id) {
        CaseMatrix caseMatrixInfo = caseMatrixRepository.findById(id).get();
        return new RestReturnMsg(200, "get CaseMatrix by code successfully", caseMatrixInfo.toCaseMatrixMsg(true, fileService));
    }

    /**
     * @return
     * @date 2019/7/3 下午15:12
     * @description 提供 case 下所有的 caseMatrix清單
     */
    @ApiOperation("條列Case所有caseMatrix ")
    @GetMapping("/analysis/{id}/matrices")
    public @ResponseBody
    RestReturnMsg caseMatrixList(@PathVariable Long id) {
        List<CaseMatrix> dssList = new ArrayList<CaseMatrix>();
        dssList = caseMatrixRepository.findByCaseId(id);
        List<CaseMatrixMsg> resultList = new ArrayList<CaseMatrixMsg>();
        for (CaseMatrix dss : dssList) {
            resultList.add(dss.toCaseMatrixMsg(false, fileService));
        }
        return new RestReturnMsg(200, "get caseMatrixList successfully", resultList);
    }

    /**
     * @param id
     * @return
     * @date 2019/5/26 下午14:12
     * @description 用ID 刪除 CaseMatrix
     */
    @ApiOperation("用ID 刪除 CaseMatrix")
    @RequiresAuthentication
    @DeleteMapping("/analysis/matrices/{id}")
    public @ResponseBody
    RestReturnMsg deleteCaseMatrixById(@PathVariable Long id) {
        User currentUser = userRepository
                .findByUsername((String) SecurityUtils.getSubject().getSession()
                        .getAttribute(UserController.USERNAME_SESSION_KEY));
        CaseMatrix caseMatrixInfo = caseMatrixRepository.findById(id).get();
        if (caseMatrixInfo != null) {
            try {
                fileService.delete(caseMatrixInfo.getFilePath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        caseService.deleteCaseMatrix(currentUser, caseMatrixInfo);
        return new RestReturnMsg(200, "delete CaseMatrix by id successfully", id);
    }

    /**
     * @param caseMatrixMsg
     * @return com.foxconn.iisd.rcadsvc.msg.RestReturnMsg
     * @date 2019/6/3  10:32
     * @description 新增 CaseMatrix
     */
    @ApiOperation("新增CaseMatrix")
    @RequiresAuthentication
    @PostMapping("/analysis/matrices")
    public @ResponseBody
    RestReturnMsg create(@RequestBody CaseMatrixMsg caseMatrixMsg) {
        logger.info("===> Create method");
        Integer code = null;
        String msg = null;

        Subject subject = SecurityUtils.getSubject();
        // 檢查用戶是否已驗證 , 是將回傳true
        logger.info("===> 檢查用戶是否已驗證 : " + subject.isAuthenticated());
        HashMap dataMap = new HashMap<String, Object>();

        code = 200;
        if (subject.isAuthenticated()) {
            User currentUser = userRepository
                    .findByUsername((String) SecurityUtils.getSubject().getSession()
                            .getAttribute(UserController.USERNAME_SESSION_KEY));
            CaseMatrix findOne = caseMatrixRepository.findByName(caseMatrixMsg.getName());
            if (findOne == null) {
                msg = "Create CaseMatrix successfully!!";
                CaseMatrix dbcaseMatrix = caseService.createCaseMatrix(currentUser, caseMatrixMsg.toNewDDS());
                dataMap.put(dbcaseMatrix.getId(), dbcaseMatrix.toCaseMatrixMsg(true, fileService));
            } else {
                msg = "The name of CaseMatrix already exists!!";
                dataMap.put(findOne.getId(), findOne);
            }

        } else {
            logger.info("==> This user has not logged in");
            msg = "This user has not logged in";
            dataMap.put("error", "error");
            code = 401;
        }
        dataMap.put("code", code);
        return new RestReturnMsg(code, msg, dataMap);
    }


}