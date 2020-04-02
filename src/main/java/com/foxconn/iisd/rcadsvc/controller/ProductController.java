package com.foxconn.iisd.rcadsvc.controller;

import com.foxconn.iisd.rcadsvc.domain.*;
import com.foxconn.iisd.rcadsvc.domain.auth.User;
import com.foxconn.iisd.rcadsvc.msg.*;
import com.foxconn.iisd.rcadsvc.repo.*;
import com.foxconn.iisd.rcadsvc.service.ProductBwListService;
import com.foxconn.iisd.rcadsvc.service.ProductInfoService;
import com.foxconn.iisd.rcadsvc.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Time;
import java.util.*;

@Configuration
@Api(description = "Product服務")
@RestController
@RequestMapping("/products")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CodeTableRepository codeTableRepository;

    @Autowired
    private ProductInfoService productInfoService;

    @Autowired
    private ProductInfoRepository productInfoRepository;

    @Autowired
    private ProductVerifyResultRepository productVerifyResultRepository;

    @Autowired
    private ProductBwListService productBwListService;

    @Autowired
    private ProductBwListRepository productBwListRepository;

    @Autowired
    @Qualifier("mysqlJtl")
    private JdbcTemplate mysqlJtl;

    @Value("${ftpPath}")
    private String ftpPath;

    @Value("${summaryPath}")
    private String summaryPath;

    @Autowired
    @Qualifier("minioFileService")
    private FileService fileService;

    /**
     * @param productCreateMsg
     * @return com.foxconn.iisd.rcadsvc.msg.RestReturnMsg
     * @date 2019/6/3  10:32
     * @description 新增 Case
     */
    @ApiOperation("新增Product")
    @RequiresAuthentication
    @PostMapping("")
    public @ResponseBody
    ResponseEntity<RestReturnMsg> create(@RequestBody ProductMsg productCreateMsg) {
        logger.info("===> Create method");
        Integer code = null;
        String msg = null;

        Subject subject = SecurityUtils.getSubject();
        // 檢查用戶是否已驗證 , 是將回傳true
        logger.info("===> 檢查用戶是否已驗證 : " + subject.isAuthenticated());
        HashMap dataMap = new HashMap<String, Object>();

        code = 200;
        ProductInfo productInfo = null;
        ProductInfo findProd = productInfoService.findByProduct(productCreateMsg.getProduct());
        if(findProd != null){
            return ResponseEntity.status(301).body(null);
        }
        else {
            if (subject.isAuthenticated()) {
                User currentUser = userRepository
                        .findByUsername((String) SecurityUtils.getSubject().getSession()
                                .getAttribute(UserController.USERNAME_SESSION_KEY));
                msg = "Create Product successfully!!";
                boolean bwStationCheck = false;
                boolean bwPartCheck = false;
                boolean ruleCheck = false;
                ProductInfo dbRefProd = productInfoService.findByProduct(productCreateMsg.getReferProduct());
                productCreateMsg.setTrueFailRule(dbRefProd.getTrueFailRule());
                productCreateMsg.setMdTolerateTime(dbRefProd.getMdTolerateTime());
                productCreateMsg.setTaTolerateTime(dbRefProd.getTaTolerateTime());
                productCreateMsg.setUploadFreq(dbRefProd.getUploadFreq());
                ProductInfo dbcase = productInfoService.createProductInfo(currentUser, productCreateMsg.toNewDDS());
                for (Integer i : productCreateMsg.getReferType()) {
                    String sql = "";
                    if (i == 1) {
                        sql = " delete from shift where product = '" + productCreateMsg.getProduct() + "'";
                        mysqlJtl.execute(sql);
                        sql = "insert into shift(product,start_time,stop_time,update_time,description)";
                        sql += " select '" + productCreateMsg.getProduct() + "', start_time,stop_time,update_time,description ";
                        sql += " from shift where product = '" + productCreateMsg.getReferProduct() + "'";
                        mysqlJtl.execute(sql);
                    }
                    if (i == 2) {
                        ProductBwList findOne = productBwListRepository.findByProductAndListType(productCreateMsg.getProduct(), "3");
                        if (findOne != null) {
                            String content = "";
                            content += "Product:" + findOne.getProduct() + ",ListType:" + findOne.getListType();
                            sql = "insert into product_log(log,create_date) Values('" + content + "',now())";
                            mysqlJtl.execute(sql);
                            productBwListRepository.delete(findOne);
                        }
                        sql = "insert into product_bw_list(product,list_type,black_white,black_list,white_list,setting_json,create_user,create_time)";
                        sql += " select '" + productCreateMsg.getProduct() + "', list_type,black_white,black_list,white_list,setting_json,'"
                                + currentUser.getUsername() + "',now()";
                        sql += " from product_bw_list where product = '" + productCreateMsg.getReferProduct() + "' and list_type ='3'";
                        mysqlJtl.execute(sql);
                        ruleCheck = true;
                    }
                    if (i == 3) {
                        ProductBwList findOne = productBwListRepository.findByProductAndListType(productCreateMsg.getProduct(), "1");
                        if (findOne != null) {
                            String content = "";
                            content += "Product:" + findOne.getProduct() + ",ListType:" + findOne.getListType();
                            sql = "insert into product_log(log,create_date) Values('" + content + "',now())";
                            mysqlJtl.execute(sql);
                            productBwListRepository.delete(findOne);
                        }
                        sql = "insert into product_bw_list(product,list_type,black_white,black_list,white_list,setting_json,create_user,create_time)";
                        sql += " select '" + productCreateMsg.getProduct() + "', list_type,black_white,black_list,white_list,setting_json,'"
                                + currentUser.getUsername() + "',now()";
                        sql += " from product_bw_list where product = '" + productCreateMsg.getReferProduct() + "' and list_type ='1'";
                        mysqlJtl.execute(sql);
                        bwStationCheck = true;
                    }
                    if (i == 4) {
                        ProductBwList findOne = productBwListRepository.findByProductAndListType(productCreateMsg.getProduct(), "2");
                        if (findOne != null) {
                            String content = "";
                            content += "Product:" + findOne.getProduct() + ",ListType:" + findOne.getListType();
                            sql = "insert into product_log(log,create_date) Values('" + content + "',now())";
                            mysqlJtl.execute(sql);
                            productBwListRepository.delete(findOne);
                        }
                        sql = "insert into product_bw_list(product,list_type,black_white,black_list,white_list,setting_json,create_user,create_time)";
                        sql += " select '" + productCreateMsg.getProduct() + "', list_type,black_white,black_list,white_list,setting_json,'"
                                + currentUser.getUsername() + "',now()";
                        sql += " from product_bw_list where product = '" + productCreateMsg.getReferProduct() + "' and list_type ='2'";
                        mysqlJtl.execute(sql);
                        bwPartCheck = true;
                    }
                    if (i == 5) {
                        sql = " delete from code_table where code_product = '" + productCreateMsg.getProduct() + "'";
                        mysqlJtl.execute(sql);
                        sql = "INSERT INTO `code_table` (`code_product`,`code_category`,`code`,`code_name`,`create_user`,`create_time`)";
                        sql += " select '" + productCreateMsg.getProduct() + "', code_category,`code`,code_name,'"
                                + currentUser.getUsername() + "',now()";
                        sql += " from code_table where code_product = '" + productCreateMsg.getReferProduct() + "'";
                        mysqlJtl.execute(sql);
                    }
                }
                if (!ruleCheck) {
                    String sql = "insert into product_bw_list(product,list_type,setting_json,create_user,create_time)";
                    sql += " Values( '" + productCreateMsg.getProduct() + "', 3,'{}','"
                            + currentUser.getUsername() + "',now())";
                    mysqlJtl.execute(sql);
                }
                if (!bwStationCheck) {
                    String sql = "insert into product_bw_list(product,list_type,black_white,black_list,create_user,create_time)";
                    sql += " Values('" + productCreateMsg.getProduct() + "', 1,'b','','"
                            + currentUser.getUsername() + "',now())";
                    mysqlJtl.execute(sql);
                }
                if (!bwPartCheck) {
                    String sql = "insert into product_bw_list(product,list_type,black_white,black_list,create_user,create_time)";
                    sql += " Values('" + productCreateMsg.getProduct() + "', 2,'b','','"
                            + currentUser.getUsername() + "',now())";
                    mysqlJtl.execute(sql);
                }
                productInfo = productInfoRepository.findByProduct(productCreateMsg.getProduct());

            } else {
                logger.info("==> This user has not logged in");
                msg = "This user has not logged in";
                dataMap.put("error", "error");
                code = 401;

            }
            return ResponseEntity.status(HttpStatus.OK).body(new RestReturnMsg(code, msg, productInfo.toProductMsg()));
        }

    }



    /**
     * @param productMsg
     * @return com.foxconn.iisd.rcadsvc.msg.RestReturnMsg
     * @date 2019/6/3  10:32
     * @description 更新 Product
     */
    @ApiOperation("更新Product")
    @RequiresAuthentication
    @PutMapping("/{id}")
    public @ResponseBody
    RestReturnMsg update(@RequestBody ProductMsg productMsg,@PathVariable Long id) {
        logger.info("===> Update method");
        productMsg.setId(id);
        Integer code = null;
        String msg = null;
        ProductInfo dbcase = null;
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

            ProductInfo findOne = productInfoRepository.findById(productMsg.getId()).isPresent() ? productInfoRepository.findById(productMsg.getId()).get() : null;
            if (findOne == null) {
                code = 404;
                msg = "The Case not exists!!";
                dataMap.put("404", "not found");
            } else {
                msg = "Update ProductInfo successfully!!";
                dbcase = productInfoService.updateProduct(currentUser, productMsg.toNewUpdateDDS(), productMsg.getId());
//                dataMap.put(dbcase.getId(),dbcase.toCaseMsg(true, dataSetRepository));
            }
        } else {
            logger.info("==> This user has not logged in");
            msg = "This user has not logged in";
            dataMap.put("error", "error");
            code = 401;
        }
//        dataMap.put("code", code);
        return new RestReturnMsg(code, msg, dbcase == null ? null : dbcase.toProductMsg());
    }

    /**
     * @return
     * @date 2019/7/3 下午15:12
     * @description 條列產品
     */
    @ApiOperation("條列產品 ")
    @RequiresAuthentication
    @GetMapping("")
    public @ResponseBody
    RestReturnMsg list() {
        List<ProductInfo> productList = new ArrayList<ProductInfo>();
        productList = productInfoRepository.findAll();
        List<ProductMsg> resultList = new ArrayList<ProductMsg>();
        for (ProductInfo dss : productList) {
            String sql = " SELECT count(verify_result) as verifyResult from product_verify_result where product = '" + dss.getProduct() + "' " +
                    "and verify_result = 'Fail'";
            int total = mysqlJtl.queryForObject(sql, java.lang.Integer.class);
            ProductMsg msg = dss.toProductMsg();
            msg.setVerifyResult(total);
            resultList.add(msg);
        }
        return new RestReturnMsg(200, "get ProductList successfully", resultList);
    }

    /**
     * @return
     * @date 2019/7/3 下午15:12
     * @description 詳細驗證內容
     */
    @ApiOperation("詳細驗證內容 ")
    @RequiresAuthentication
    @GetMapping("/{id}/validation/history")
    public @ResponseBody
    RestReturnMsg listFail(@PathVariable Long id) {
        ProductInfo findOne = productInfoRepository.findById(id).isPresent() ? productInfoRepository.findById(id).get() : null ;
        String sql = "SELECT verify_date as createTime,FTP,productSetup,SummaryFile FROM product_verify_result_view  where product ='" + findOne.getProduct() + "' order by verify_date asc ";
        List<Map<String, Object>> tablelist = mysqlJtl.queryForList(sql);

        return new RestReturnMsg(200, "get ProductList successfully", tablelist);
    }

    /**
     * @param id
     * @return com.foxconn.iisd.rcadsvc.msg.RestReturnMsg
     * @date 2019/6/3  10:32
     * @description ProductInfo
     */
    @ApiOperation("ProductInfo")
    @RequiresAuthentication
    @GetMapping("/{id}")
    public @ResponseBody
    RestReturnMsg getInfo(@PathVariable Long id) {
        Integer code = null;
        String msg = null;
        ProductInfo dbcase = null;
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

            ProductInfo findOne = productInfoRepository.findById(id).isPresent() ? productInfoRepository.findById(id).get() : null;
            if (findOne == null) {
                code = 404;
                msg = "The Case not exists!!";
                dataMap.put("404", "not found");
            } else {
                msg = "Update ProductInfo successfully!!";
                dbcase = findOne;
//                dataMap.put(dbcase.getId(),dbcase.toCaseMsg(true, dataSetRepository));
            }
        } else {
            logger.info("==> This user has not logged in");
            msg = "This user has not logged in";
            dataMap.put("error", "error");
            code = 401;
        }
//        dataMap.put("code", code);
        return new RestReturnMsg(code, msg, dbcase == null ? null : dbcase.toProductMsg());
    }

    /**
     * @param id
     * @return com.foxconn.iisd.rcadsvc.msg.RestReturnMsg
     * @date 2019/6/3  10:32
     * @description ProductInfo
     */
    @ApiOperation("數據邏輯")
    @RequiresAuthentication
    @GetMapping("/{id}/etl/rules")
    public @ResponseBody
    RestReturnMsg getLogic(@PathVariable Long id) {
        Integer code = null;
        String msg = null;
        ProductMsg pmsg = null;
        ProductInfo dbcase = null;
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

            ProductInfo findOne = productInfoRepository.findById(id).isPresent() ? productInfoRepository.findById(id).get() : null;
            if (findOne == null) {
                code = 404;
                msg = "The Case not exists!!";
                dataMap.put("404", "not found");
            } else {
                msg = "Update ProductInfo successfully!!";
                dbcase = findOne;
                String sql = "select start_time,stop_time,description from shift where product ='" + dbcase.getProduct() + "'";
                List<ShiftMsg> shiftMsgList = new ArrayList<>();
                List<Map<String, Object>> tablelist = mysqlJtl.queryForList(sql);
                for (Map<String, Object> row : tablelist) {
                    ShiftMsg smsg = new ShiftMsg();
                    smsg.setStartTime(((Time) row.get("start_time")).toString());
                    smsg.setStopTime(((Time) row.get("stop_time")).toString());
                    smsg.setDescription((String) row.get("description"));
                    shiftMsgList.add(smsg);
                }
                pmsg = new ProductMsg();
                pmsg = dbcase.toLogicMsg();
                pmsg.setShift(shiftMsgList);
//              dataMap.put(dbcase.getId(),dbcase.toCaseMsg(true, dataSetRepository));
            }
        } else {
            logger.info("==> This user has not logged in");
            msg = "This user has not logged in";
            dataMap.put("error", "error");
            code = 401;
        }
//        dataMap.put("code", code);
        return new RestReturnMsg(code, msg, pmsg == null ? null : pmsg);
    }

    /**
     * @param productMsg
     * @return com.foxconn.iisd.rcadsvc.msg.RestReturnMsg
     * @date 2019/6/3  10:32
     * @description 更新 ProductLogic
     */
    @ApiOperation("更新ProductLogic")
    @RequiresAuthentication
    @PutMapping("/{id}/etl/rules")
    public @ResponseBody
    RestReturnMsg updateLogic(@RequestBody ProductMsg productMsg,@PathVariable Long id) {
        logger.info("===> Update method");
        productMsg.setId(id);
        Integer code = null;
        String msg = null;
        ProductInfo dbcase = null;
        ProductMsg pmsg = null;
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

            ProductInfo findOne = productInfoRepository.findById(productMsg.getId()).isPresent() ? productInfoRepository.findById(productMsg.getId()).get() : null;
            if (findOne == null) {
                code = 404;
                msg = "The Case not exists!!";
                dataMap.put("404", "not found");
            } else {
                msg = "Update ProductInfo successfully!!";
                dbcase = productInfoService.updateProductLogic(currentUser, productMsg.toUpdateLogicDDS(), productMsg.getId());
                //先刪除原本的班表 再新增
                String sql = "delete from shift where product ='" + productMsg.getProduct() + "'";
                mysqlJtl.execute(sql);
                for (ShiftMsg smsg : productMsg.getShift()) {
                    sql = "Insert into shift Values('" + productMsg.getProduct() + "','" + smsg.getStartTime() + "','" + smsg.getStopTime() + "',now(),'" +
                            smsg.getDescription() + "')";
                    mysqlJtl.execute(sql);
                }
                pmsg = new ProductMsg();
                pmsg = dbcase.toLogicMsg();
                pmsg.setShift(productMsg.getShift());
            }
        } else {
            logger.info("==> This user has not logged in");
            msg = "This user has not logged in";
            dataMap.put("error", "error");
            code = 401;
        }
//        dataMap.put("code", code);
        return new RestReturnMsg(code, msg, pmsg == null ? null : pmsg);
    }

    /**
     * @param product,category
     * @return
     * @date 2019/5/26 下午14:12
     * @description 描述設定
     */
    @ApiOperation("描述設定")
    @RequiresAuthentication
    @GetMapping("/getCodeByProduct")
    public @ResponseBody
    RestReturnMsg getCodeByProduct(@RequestParam("product") String product, @RequestParam("category") String category) {
        List<CodeTable> codeList = codeTableRepository.findByCodeProductAndCodeCategory(product, category);
        List<CodeTableMsg> codeMsgList = new ArrayList<CodeTableMsg>();
        for (CodeTable codeInfo : codeList) {
            codeMsgList.add(codeInfo.toCodeMsg(true));
        }
        return new RestReturnMsg(200, "query Code list successfully", codeMsgList);
    }

    /**
     * @return
     * @date 2019/7/3 下午15:12
     * @description 驗證功能
     */
    @ApiOperation("驗證功能 ")
    @GetMapping("/{id}/validation/ftp")
    public @ResponseBody
    RestReturnMsg getFtpVerifyResult(@PathVariable Long id) {
        List<ProductVerifyResult> productVerifyResultList = new ArrayList<>();
        ProductInfo find = productInfoRepository.findById(id).isPresent() ? productInfoRepository.findById(id).get() : null;
        productVerifyResultList = productVerifyResultRepository.findByProductAndVerifyTypeOrderByVerifyDate(find.getProduct(), "1");
        List<ProductVerifyResultMsg> resultList = new ArrayList<ProductVerifyResultMsg>();
        for (ProductVerifyResult dss : productVerifyResultList) {
            ProductVerifyResultMsg msg = dss.toProductVerifyResultMsg(fileService);
            resultList.add(msg);
        }
        return new RestReturnMsg(200, "get ProductList successfully", resultList);
    }

    /**
     * @return
     * @date 2019/7/3 下午15:12
     * @description 驗證功能
     */
    @ApiOperation("驗證功能 ")
    @GetMapping("/{id}/validation/product")
    public @ResponseBody
    RestReturnMsg getProductVerifyResult(@PathVariable Long id) {
        List<ProductVerifyResult> productVerifyResultList = new ArrayList<>();
        ProductInfo find = productInfoRepository.findById(id).isPresent() ? productInfoRepository.findById(id).get() : null;
        productVerifyResultList = productVerifyResultRepository.findByProductAndVerifyTypeOrderByVerifyDate(find.getProduct(), "2");
        List<ProductVerifyResultMsg> resultList = new ArrayList<ProductVerifyResultMsg>();
        for (ProductVerifyResult dss : productVerifyResultList) {
            ProductVerifyResultMsg msg = dss.toProductVerifyResultMsg(fileService);
            resultList.add(msg);
        }
        return new RestReturnMsg(200, "get ProductList successfully", resultList);
    }

    /**
     * @return
     * @date 2019/7/3 下午15:12
     * @description 驗證功能
     */
    @ApiOperation("驗證功能 ")
    @GetMapping("/{id}/validation/summary")
    public @ResponseBody
    RestReturnMsg getSummaryVerifyResult(@PathVariable Long id) {
        List<ProductVerifyResult> productVerifyResultList = new ArrayList<>();
        ProductInfo find = productInfoRepository.findById(id).isPresent() ? productInfoRepository.findById(id).get() : null;
        productVerifyResultList = productVerifyResultRepository.findByProductAndVerifyTypeOrderByVerifyDate(find.getProduct(), "3");
        List<ProductVerifyResultMsg> resultList = new ArrayList<ProductVerifyResultMsg>();
        for (ProductVerifyResult dss : productVerifyResultList) {
            ProductVerifyResultMsg msg = dss.toProductVerifyResultMsg(fileService);
            resultList.add(msg);
        }
        return new RestReturnMsg(200, "get ProductList successfully", resultList);
    }
    /**
     * @param id
     * @return com.foxconn.iisd.rcadsvc.msg.RestReturnMsg
     * @date 2019/6/3  10:32
     * @description ProductInfo
     */
    @ApiOperation("getVerifyInfo")
    @RequiresAuthentication
    @GetMapping("/{id}/validation")
    public @ResponseBody
    RestReturnMsg getVerifyInfo(@PathVariable Long id) {
        Integer code = null;
        String msg = null;
        ProductInfo dbcase = null;
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

            ProductInfo findOne = productInfoRepository.findById(id).isPresent() ? productInfoRepository.findById(id).get() : null;
            if (findOne == null) {
                code = 404;
                msg = "The Case not exists!!";
                dataMap.put("404", "not found");
            } else {
                msg = "Update ProductInfo successfully!!";
                dbcase = findOne;
//                dataMap.put(dbcase.getId(),dbcase.toCaseMsg(true, dataSetRepository));
            }
        } else {
            logger.info("==> This user has not logged in");
            msg = "This user has not logged in";
            dataMap.put("error", "error");
            code = 401;
        }
//        dataMap.put("code", code);
        return new RestReturnMsg(code, msg, dbcase == null ? null : dbcase.toProducVerifytMsg(ftpPath, summaryPath));
    }

    @RequestMapping(value = "/{id}/etl/filters/station/upload", method = RequestMethod.POST, consumes = "multipart/form-data")
    @RequiresAuthentication
    public RestReturnMsg uploadStation(@ModelAttribute ProductBWMsg productBWMsg,@PathVariable Long id) {

        Integer code = null;
        String msg = null;
        try {
//            System.out.println((String) SecurityUtils.getSubject().getSession()
//                    .getAttribute(UserController.USERNAME_SESSION_KEY));

            // User currentUser = userRepository
            // .findByUsername((String) SecurityUtils.getSubject().getSession()
            //    .getAttribute(UserController.USERNAME_SESSION_KEY));
            ProductInfo findOne = productInfoRepository.findById(id).isPresent() ? productInfoRepository.findById(id).get() : null;
            byte[] bytes = productBWMsg.getFile().getBytes();
            String completeData = new String(bytes);
            InputStreamReader isrCheck = new InputStreamReader(new ByteArrayInputStream(completeData.getBytes(StandardCharsets.UTF_8)));//檔案讀取路徑
            BufferedReader readerCheck = new BufferedReader(isrCheck);
            String line = null;
            readerCheck.readLine();
            String type = "";
            boolean checkFormat = true;
            int index = 1;
            while ((line = readerCheck.readLine()) != null) {
                String item[] = line.split(",");
                if (item.length == 2) {
                    if (index == 1)
                        type = item[0];
                    else {
                        if (type.equals(item[0]) && (item[0].equals("b") || item[0].equals("w")))
                            checkFormat = true;
                        else
                            checkFormat = false;
                    }
                } else {
                    checkFormat = false;
                }
                index++;
            }
            if(checkFormat) {
                InputStreamReader isr = new InputStreamReader(new ByteArrayInputStream(completeData.getBytes(StandardCharsets.UTF_8)));//檔案讀取路徑
                BufferedReader reader = new BufferedReader(isr);

                int i = 1;
                String sql = "";
                int total = 0;
                String bw = "";
                String list = "";
                sql = "delete from product_bw_list where product = '" + findOne.getProduct() + "' " +
                        " and list_type ='1'";
                mysqlJtl.execute(sql);
                while ((line = reader.readLine()) != null) {
                    String item[] = line.split(",");
                    /** 讀取 **/
                    if (i == 2) {
                        String list_type = item[0].trim();
                        bw = list_type;
                    }
                    String csvList = item[1].trim();
                    if (i >= 2) {
                        list += csvList + ",";
                    }
                    i++;
                }
                list = list.substring(0, list.length() - 1);

                if (bw.equals("w")) {
                    sql = "Insert into product_bw_list (`product`,`list_type`,`black_white`,`white_list`,`create_time`) Values('" + findOne.getProduct() + "'," +
                            "'1','w','" + list + "',now())";
                    mysqlJtl.execute(sql);
                } else {
                    sql = "Insert into product_bw_list (`product`,`list_type`,`black_white`,`black_list`,`create_time`) Values('" + findOne.getProduct() + "'," +
                            "'1','b','" + list + "',now())";
                    mysqlJtl.execute(sql);
                }

                code = 200;
                msg = "Upload successfully!!!";
            }
            else{
                code = 422;
                msg = "csv格式有誤";
            }
        } catch (Exception e) {
            code = 500;
            msg = "Failing to Upload!!!";
            logger.error(msg, e);
        }
        return new RestReturnMsg(code, msg, null);
    }

    @RequestMapping(value = "/{id}/etl/filters/component/upload", method = RequestMethod.POST, consumes = "multipart/form-data")
    @RequiresAuthentication
    public RestReturnMsg uploadComponent(@ModelAttribute ProductBWMsg productBWMsg,@PathVariable Long id) {

        Integer code = null;
        String msg = null;
        try {
//            System.out.println((String) SecurityUtils.getSubject().getSession()
//                    .getAttribute(UserController.USERNAME_SESSION_KEY));

            // User currentUser = userRepository
            // .findByUsername((String) SecurityUtils.getSubject().getSession()
            //    .getAttribute(UserController.USERNAME_SESSION_KEY));
            ProductInfo findOne = productInfoRepository.findById(id).isPresent() ? productInfoRepository.findById(id).get() : null;
            byte[] bytes = productBWMsg.getFile().getBytes();
            String completeData = new String(bytes);
            InputStreamReader isrCheck = new InputStreamReader(new ByteArrayInputStream(completeData.getBytes(StandardCharsets.UTF_8)));//檔案讀取路徑
            BufferedReader readerCheck = new BufferedReader(isrCheck);
            String line = null;
            readerCheck.readLine();
            String type = "";
            boolean checkFormat = true;
            int index = 1;
            while ((line = readerCheck.readLine()) != null) {
                String item[] = line.split(",");
                if (item.length == 2) {
                    if (index == 1)
                        type = item[0];
                    else {
                        if (type.equals(item[0]) && (item[0].equals("b") || item[0].equals("w")))
                            checkFormat = true;
                        else
                            checkFormat = false;
                    }
                } else {
                    checkFormat = false;
                }
                index++;
            }
            if(checkFormat) {
                InputStreamReader isr = new InputStreamReader(new ByteArrayInputStream(completeData.getBytes(StandardCharsets.UTF_8)));//檔案讀取路徑
                BufferedReader reader = new BufferedReader(isr);

                int i = 1;
                String sql = "";
                int total = 0;
                String bw = "";
                String list = "";
                sql = "delete from product_bw_list where product = '" + findOne.getProduct() + "' " +
                        " and list_type ='2'";
                mysqlJtl.execute(sql);
                while ((line = reader.readLine()) != null) {
                    String item[] = line.split(",");
                    /** 讀取 **/
                    if (i == 2) {
                        String list_type = item[0].trim();
                        bw = list_type;
                    }
                    String csvList = item[1].trim();
                    if (i >= 2) {
                        list += csvList + ",";
                    }
                    i++;
                }
                list = list.substring(0, list.length() - 1);

                if (bw.equals("w")) {
                    sql = "Insert into product_bw_list (`product`,`list_type`,`black_white`,`white_list`,`create_time`) Values('" + findOne.getProduct() + "'," +
                            "'2','w','" + list + "',now())";
                    mysqlJtl.execute(sql);
                } else {
                    sql = "Insert into product_bw_list (`product`,`list_type`,`black_white`,`black_list`,`create_time`) Values('" + findOne.getProduct() + "'," +
                            "'2','b','" + list + "',now())";
                    mysqlJtl.execute(sql);
                }

                code = 200;
                msg = "Upload successfully!!!";
            }
            else{
                code = 422;
                msg = "csv格式有誤";
            }
        } catch (Exception e) {
            code = 500;
            msg = "Failing to Upload!!!";
            logger.error(msg, e);
        }
        return new RestReturnMsg(code, msg, null);
    }

    @RequiresAuthentication
    @GetMapping("/{id}/etl/filters/station/download")
    public @ResponseBody
    ResponseEntity<InputStreamResource> InputStreamResourceStation(@PathVariable Long id) {
        List<String> srcFilesList = new ArrayList<>();

        try {
            String content = "";
            content = "list_type,station\n";
            ProductInfo findOne = productInfoRepository.findById(id).isPresent() ? productInfoRepository.findById(id).get() : null;
            String sql = "select * from product_bw_list where product ='" + findOne.getProduct() + "' and list_type ='1'";
            List<Map<String, Object>> tablelist = mysqlJtl.queryForList(sql);
            String list = "";
            for (Map<String, Object> row : tablelist) {
                if (((String) row.get("list_type")).equals("b")) {
                    String[] arrayContent = ((String) row.get("black_list")).split(",");
                    for (int i = 0; i < arrayContent.length; i++) {
                        list += "b," + arrayContent[i] + "\n";
                    }
                } else if (((String) row.get("list_type")).equals("w")) {
                    String[] arrayContent = ((String) row.get("white_list")).split(",");
                    for (int i = 0; i < arrayContent.length; i++) {
                        list += "w," + arrayContent[i] + "\n";
                    }
                }
            }
            content += list;
            InputStream targetStream = new ByteArrayInputStream(content.getBytes());
            InputStreamResource resourcetest = new InputStreamResource(targetStream);
            HttpHeaders header = new HttpHeaders();
            header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; " +
                    "filename=key.csv" + "; " +
                    "filename*=utf-8''key.csv");
            header.add("Cache-Control", "no-cache, no-store, must-revalidate");
            header.add("Pragma", "no-cache");
            header.add("Expires", "0");

            return ResponseEntity.ok()
                    .headers(header)
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .body(resourcetest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequiresAuthentication
    @GetMapping("/{id}/etl/filters/component/download")
    public @ResponseBody
    ResponseEntity<InputStreamResource> InputStreamResourceComponent(@PathVariable Long id) {
        List<String> srcFilesList = new ArrayList<>();

        try {
            String content = "";
            ProductInfo findOne = productInfoRepository.findById(id).isPresent() ? productInfoRepository.findById(id).get() : null;
            content = "list_type,part_name\n";
            String sql = "select * from product_bw_list where product ='" + findOne.getProduct() + "' and list_type ='2'";
            List<Map<String, Object>> tablelist = mysqlJtl.queryForList(sql);
            String list = "";
            for (Map<String, Object> row : tablelist) {
                if (((String) row.get("list_type")).equals("b")) {
                    String[] arrayContent = ((String) row.get("black_list")).split(",");
                    for (int i = 0; i < arrayContent.length; i++) {
                        list += "b," + arrayContent[i] + "\n";
                    }
                } else if (((String) row.get("list_type")).equals("w")) {
                    String[] arrayContent = ((String) row.get("white_list")).split(",");
                    for (int i = 0; i < arrayContent.length; i++) {
                        list += "w," + arrayContent[i] + "\n";
                    }
                }
            }
            content += list;
            InputStream targetStream = new ByteArrayInputStream(content.getBytes());
            InputStreamResource resourcetest = new InputStreamResource(targetStream);
            HttpHeaders header = new HttpHeaders();
            header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; " +
                    "filename=key.csv" + "; " +
                    "filename*=utf-8''key.csv");
            header.add("Cache-Control", "no-cache, no-store, must-revalidate");
            header.add("Pragma", "no-cache");
            header.add("Expires", "0");

            return ResponseEntity.ok()
                    .headers(header)
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .body(resourcetest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * @param id
     * @return com.foxconn.iisd.rcadsvc.msg.RestReturnMsg
     * @date 2019/6/3  10:32
     * @description ProductInfo
     */
    @ApiOperation("getStationInfo")
    @RequiresAuthentication
    @GetMapping("/{id}/etl/filters/station")
    public @ResponseBody
    RestReturnMsg getStationInfo(@PathVariable Long id) {
        Integer code = null;
        String msg = null;
        ProductBwList dbcase = null;
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
            ProductInfo find = productInfoRepository.findById(id).isPresent() ? productInfoRepository.findById(id).get() : null;
            ProductBwList findOne = productBwListRepository.findByProductAndListType(find.getProduct(), "1") != null ? productBwListRepository.findByProductAndListType(find.getProduct(), "1") : null;
            if (findOne == null) {
                code = 404;
                msg = "The Product BwList not exists!!";
                dataMap.put("404", "not found");
            } else {
                msg = "Get BwList successfully!!";
                dbcase = findOne;
//                dataMap.put(dbcase.getId(),dbcase.toCaseMsg(true, dataSetRepository));
            }
        } else {
            logger.info("==> This user has not logged in");
            msg = "This user has not logged in";
            dataMap.put("error", "error");
            code = 401;
        }
//        dataMap.put("code", code);
        return new RestReturnMsg(code, msg, dbcase == null ? null : dbcase.toMsg());
    }

    /**
     * @param id
     * @return com.foxconn.iisd.rcadsvc.msg.RestReturnMsg
     * @date 2019/6/3  10:32
     * @description ProductInfo
     */
    @ApiOperation("getComponentInfo")
    @RequiresAuthentication
    @GetMapping("/{id}/etl/filters/component")
    public @ResponseBody
    RestReturnMsg getComponentInfo(@PathVariable Long id) {
        Integer code = null;
        String msg = null;
        ProductBwList dbcase = null;
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
            ProductInfo find = productInfoRepository.findById(id).isPresent() ? productInfoRepository.findById(id).get() : null;
            ProductBwList findOne = productBwListRepository.findByProductAndListType(find.getProduct(), "2") != null ? productBwListRepository.findByProductAndListType(find.getProduct(), "2") : null;
            if (findOne == null) {
                code = 404;
                msg = "The Product BwList not exists!!";
                dataMap.put("404", "not found");
            } else {
                msg = "Get BwList successfully!!";
                dbcase = findOne;
//                dataMap.put(dbcase.getId(),dbcase.toCaseMsg(true, dataSetRepository));
            }
        } else {
            logger.info("==> This user has not logged in");
            msg = "This user has not logged in";
            dataMap.put("error", "error");
            code = 401;
        }
//        dataMap.put("code", code);
        return new RestReturnMsg(code, msg, dbcase == null ? null : dbcase.toMsg());
    }

    /**
     * @param id
     * @return com.foxconn.iisd.rcadsvc.msg.RestReturnMsg
     * @date 2019/6/3  10:32
     * @description 取得物料編碼規則
     */
    @ApiOperation("取得物料編碼規則")
    @RequiresAuthentication
    @GetMapping("/{id}/etl/transformation/component")
    public RestReturnMsg getRule(@PathVariable Long id) {
        Integer code = null;
        String msg = null;
        ProductInfo dbcase = null;
        Subject subject = SecurityUtils.getSubject();
        // 檢查用戶是否已驗證 , 是將回傳true
        logger.info("===> 檢查用戶是否已驗證 : " + subject.isAuthenticated());
        List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();

        code = 200;
        if (subject.isAuthenticated()) {
            User currentUser = userRepository
                    .findByUsername((String) SecurityUtils.getSubject().getSession()
                            .getAttribute(UserController.USERNAME_SESSION_KEY));

            String sql = "select * from product_bw_list where id =" + id + " and list_type ='3'";
            List<Map<String, Object>> ruleList = mysqlJtl.queryForList(sql);
            if (ruleList.size() == 0) {
                code = 404;
                msg = "The Rule not exists!!";
            } else {
                msg = "getRule successfully!!";
                Map<String, Object> ruleItem = ruleList.get(0);
                String jsonString = (String) ruleItem.get("setting_json");
                JSONObject settingJson = new JSONObject(jsonString);
                JSONArray a = settingJson.getJSONArray("rule");
                for (Object oo : a) {
                    JSONObject o = (JSONObject) oo;
                    JSONArray part_name_whitelist = o.getJSONArray("part_name_whitelist");
                    JSONArray part_name_blacklist = o.getJSONArray("part_name_blacklist");
                    JSONArray vendor_constant = o.getJSONArray("vendor_constant");
                    JSONArray part_sn_lengthList = o.getJSONArray("part_sn_lengthList");
                    List<Object> partList = new ArrayList<Object>();
                    Map<String, String> vendor_condition = new HashMap<String, String>();
                    for (int i = 0; i < vendor_constant.length(); i++) {
                        JSONObject json = vendor_constant.getJSONObject(i);
                        Iterator<String> keys = json.keys();

                        while (keys.hasNext()) {
                            String key = keys.next();
                            String value = (String) json.get(key);
                            vendor_condition.put(key, value);
                        }
                    }
                    for (int i = 0; i < part_sn_lengthList.length(); i++) {
                        JSONObject json = part_sn_lengthList.getJSONObject(i);
                        Iterator<String> keys = json.keys();

                        while (keys.hasNext()) {
                            String key = keys.next();
                            if (vendor_condition.containsKey(key)) {
                                JSONObject part = (JSONObject) json.get(key);
                                try {
                                    part.put("part_sn_length", key);
                                    part.put("vendor_constant", vendor_condition.get(key));
                                    Map<String, Object> in_map = new HashMap<String, Object>();
                                    Iterator<String> keys_in = part.keys();
                                    while (keys_in.hasNext()) {
                                        String key_in = keys_in.next();
                                        if (!"match_vendor_name".equals(key_in)) {
                                            String o_in = String.valueOf(part.get(key_in));
                                            in_map.put(key_in, o_in);
                                        } else {
                                            JSONArray a_in = part.getJSONArray(key_in);
                                            in_map.put(key_in, a_in);
                                        }
                                    }
                                    partList.add(in_map);
                                } catch (Exception e) {
                                    //
                                }
                            } else {
                                JSONObject part = (JSONObject) json.get(key);
                                Map<String, Object> in_map = new HashMap<String, Object>();
                                Iterator<String> keys_in = part.keys();
                                while (keys_in.hasNext()) {
                                    String key_in = keys_in.next();
                                    if (!"match_vendor_name".equals(key_in)) {
                                        String o_in = String.valueOf(part.get(key_in));
                                        in_map.put(key_in, o_in);
                                    } else {
                                        JSONArray a_in = part.getJSONArray(key_in);
                                        in_map.put(key_in, a_in);
                                    }
                                }
                                in_map.put("part_sn_length", key);
                                partList.add(in_map);
                            }
                        }
                    }
                    Map<String, Object> dataMap = new HashMap<String, Object>();
                    dataMap.put("part_name_whitelist", part_name_whitelist);
                    dataMap.put("part_name_blacklist", part_name_blacklist);
                    dataMap.put("part_sn_lengthList", partList);
                    dataList.add(dataMap);
                }
            }
        } else {
            logger.info("==> This user has not logged in");
            msg = "This user has not logged in";
            code = 401;
        }

        return new RestReturnMsg(code, msg, dataList);
    }

    /**
     * @return
     * @date 2019/5/26 下午14:12
     * @description 列出所有product
     */
    @ApiOperation("列出所有product")
    @RequiresAuthentication
    @GetMapping("/name")
    public @ResponseBody
    RestReturnMsg listProductName() {
        String sql = "select product from product_info order by product asc ";
        List<Map<String, Object>> tablelist = mysqlJtl.queryForList(sql);
        List<String> productList = new ArrayList<>();
        for (Map<String, Object> row : tablelist)
            productList.add((String) row.get("product"));
        return new RestReturnMsg(200, "get  product list successfully", productList);
    }
    public static File convert(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }
}