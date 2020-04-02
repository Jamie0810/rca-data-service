package com.foxconn.iisd.rcadsvc.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.foxconn.iisd.rcadsvc.domain.ProductInfo;
import com.foxconn.iisd.rcadsvc.repo.ProductInfoRepository;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import com.foxconn.iisd.rcadsvc.domain.CodeTable;
import com.foxconn.iisd.rcadsvc.domain.auth.User;
import com.foxconn.iisd.rcadsvc.msg.CodeTableMsg;
import com.foxconn.iisd.rcadsvc.msg.RestReturnMsg;
import com.foxconn.iisd.rcadsvc.repo.CodeTableRepository;
import com.foxconn.iisd.rcadsvc.repo.UserRepository;
import com.foxconn.iisd.rcadsvc.service.CodeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
@Configuration
@Api(description = "Code服務")
@RestController
@RequestMapping("/codes")
public class CodeController {

    private static final Logger logger = LoggerFactory.getLogger(CodeController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CodeTableRepository codeTableRepository;

    @Autowired
    private ProductInfoRepository productInfoRepository;

    @Autowired
    private CodeService codeService;

    @Autowired
    @Qualifier("mysqlJtl")
    private JdbcTemplate mysqlJtl;

    private static String QUERY_PRODUCT_CODE = "SELECT DISTINCT product FROM product_floor_line;";
    
    /**
     * @param code
     * @return com.foxconn.iisd.rcadsvc.msg.RestReturnMsg
     * @date 2019/6/3  10:32
     * @description 新增 CodeTable
     */
    @ApiOperation("新增CodeTable")
    @RequiresAuthentication
    @PostMapping("")
    public @ResponseBody
    RestReturnMsg create(@RequestBody CodeTableMsg codeMsg) {
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
            CodeTable findOne = codeTableRepository.findByCodeProductAndCodeCategoryAndCode(codeMsg.getCodeProduct(), codeMsg.getCodeCategory(), codeMsg.getCode());
            if( findOne == null){
                msg = "Create Code successfully!!";
                CodeTable dbcode = codeService.createCode(currentUser,codeMsg.toNewDDS());
                dataMap.put(dbcode.getId(),dbcode.toCodeMsg(true));
            }else{
                msg = "The Code already exists!!";
                dataMap.put(findOne.getId(),findOne);
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
     * @param code
     * @return com.foxconn.iisd.rcadsvc.msg.RestReturnMsg
     * @date 2019/6/3  10:32
     * @description 更新 CodeTable
     */
    @ApiOperation("更新 CodeTable")
    @RequiresAuthentication
    @PostMapping("/update")
    public @ResponseBody
    RestReturnMsg update(@RequestBody CodeTableMsg codeMsg) {
        logger.info("===> Update method");
        Integer code = null;
        String msg = null;
        CodeTable dbcode = null;
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
            
            CodeTable findOne = codeTableRepository.findById(codeMsg.getId()).isPresent()?codeTableRepository.findById(codeMsg.getId()).get():null;
            if( findOne == null){
            	code = 404;
            	msg = "The Code not exists!!";
                dataMap.put("404", "not found");
            }else{
            	msg = "Update Code successfully!!";
            	dbcode = codeService.updateCode(currentUser,codeMsg.toNewDDS(),codeMsg.getId());
//                dataMap.put(dbcode.getId(),dbcode.toCodeTable(true, dataSetRepository));
            }
        } else {
            logger.info("==> This user has not logged in");
            msg = "This user has not logged in";
            dataMap.put("error", "error");
            code = 401;
        }
//        dataMap.put("code", code);
        return new RestReturnMsg(code, msg, dbcode==null?null:dbcode.toCodeMsg(true));
    }

    /**
     * @param name
     * @return
     * @date 2019/5/26 下午14:12
     * @description 找出Name同的CodeTable
     */
    @ApiOperation("找出Name同的CodeTable")
    @GetMapping("/findByCodeName")
    public @ResponseBody RestReturnMsg findByCodeName(@RequestParam("name") String name) {
        CodeTable codeInfo = codeTableRepository.findByCodeName(name);
        return new RestReturnMsg(200, "get Code by name successfully" , codeInfo.toCodeMsg(true));
    }

    /**
     * @param id
     * @return
     * @date 2019/5/26 下午14:12
     * @description 用ID找出CodeTable
     */
    @ApiOperation("用ID找出CodeTable")
    @GetMapping("/findById")
    public @ResponseBody RestReturnMsg findById(@RequestParam("codeId") Long id) {
        CodeTable codeInfo = codeTableRepository.findById(id).get();
        return new RestReturnMsg(200, "get Code by Id successfully" , codeInfo.toCodeMsg(true));
    }

    /**
     * @param name
     * @return
     * @date 2019/5/26 下午14:12
     * @description 找出包含CodeName的CodeTable
     */
    @ApiOperation("找出包含CodeName的CodeTable")
    @GetMapping("/queryByCodeName")
    public @ResponseBody RestReturnMsg queryByCodeName(@RequestParam("name") String name) {
        List<CodeTable> codeList = codeTableRepository.findByCodeNameContaining(name);
        List<CodeTableMsg> codeMsgList = new ArrayList<CodeTableMsg>();
        for(CodeTable codeInfo : codeList){
        	codeMsgList.add(codeInfo.toCodeMsg(true));
        }
        return new RestReturnMsg(200, "query Code list successfully" , codeMsgList);
    }

    /**
     * @param id
     * @return
     * @date 2019/5/26 下午14:12
     * @description 用ID 刪除 CodeTable
     */
    @ApiOperation("用ID 刪除 CodeTable")
    @RequiresAuthentication
    @DeleteMapping("/{id}")
    public @ResponseBody RestReturnMsg deleteById(@PathVariable Long id) {
        User currentUser = userRepository
                .findByUsername((String) SecurityUtils.getSubject().getSession()
                        .getAttribute(UserController.USERNAME_SESSION_KEY));
        CodeTable codeInfo = codeTableRepository.findById(id).get();
        codeService.deleteCode(currentUser, codeInfo);
        return new RestReturnMsg(200, "delete Code by id successfully" , id);
    }
    
    /**
     * @param id
     * @return
     * @date 2019/5/26 下午14:12
     * @description 條列全部代碼
     */
    @ApiOperation("條列全部代碼")
    @RequiresAuthentication
    @RequiresPermissions(value={"configuration_code:Create","configuration_code:Update","configuration_code:Delete","configuration_code:View",
            "configuration:Create","configuration:Update","configuration:Delete","configuration:View"},logical= Logical.OR)
    @GetMapping("")
    public @ResponseBody RestReturnMsg list() {
        List<CodeTable> codeList = codeTableRepository.findAll();
        return new RestReturnMsg(200, "list Code successfully" , codeList);
    }
    
    /**
     * @param id
     * @return
     * @date 2019/5/26 下午14:12
     * @description 條列全部代碼byPage
     */
    @ApiOperation("條列全部代碼ByPage")
    @RequiresAuthentication
    @GetMapping("/listByPage")
    public @ResponseBody RestReturnMsg listByPage(@RequestParam("pageNumber") Integer pageNumber, @RequestParam("pageSize") Integer pageSize) {
    	Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<CodeTable> codeList = codeTableRepository.findAll(pageable);
        return new RestReturnMsg(200, "list code ByPage successfully" , codeList);
    }

    /**
     * @return
     * @date 2019/5/26 下午14:12
     * @description 列出所有product
     */
    @ApiOperation("列出所有product")
    @RequiresAuthentication
    @GetMapping("/products/name")
    public @ResponseBody
    RestReturnMsg listProductName() {
        String sql = "select product from product_info order by product asc ";
        List<Map<String, Object>> tablelist = mysqlJtl.queryForList(sql);
        List<String> productList = new ArrayList<>();
        for (Map<String, Object> row : tablelist)
            productList.add((String) row.get("product"));
        return new RestReturnMsg(200, "get  product list successfully", productList);
    }

    /**
     * @param id
     * @return
     * @date 2019/5/26 下午14:12
     * @description 描述設定
     */
    @ApiOperation("描述設定")
    @RequiresAuthentication
    @GetMapping("/product/{id}/symptom")
    public @ResponseBody
    RestReturnMsg getSymptomCodeByProduct(@PathVariable Long id) {
        ProductInfo findOne = productInfoRepository.findById(id).isPresent() ? productInfoRepository.findById(id).get() : null;
        List<CodeTable> codeList = codeTableRepository.findByCodeProductAndCodeCategory(findOne.getProduct(), "1");
        List<CodeTableMsg> codeMsgList = new ArrayList<CodeTableMsg>();
        for (CodeTable codeInfo : codeList) {
            codeMsgList.add(codeInfo.toCodeMsg(true));
        }
        return new RestReturnMsg(200, "query Code list successfully", codeMsgList);
    }

    /**
     * @param id
     * @return
     * @date 2019/5/26 下午14:12
     * @description 描述設定
     */
    @ApiOperation("描述設定")
    @RequiresAuthentication
    @GetMapping("/product/{id}/component")
    public @ResponseBody
    RestReturnMsg getComponentCodeByProduct(@PathVariable Long id) {
        ProductInfo findOne = productInfoRepository.findById(id).isPresent() ? productInfoRepository.findById(id).get() : null;
        List<CodeTable> codeList = codeTableRepository.findByCodeProductAndCodeCategory(findOne.getProduct(), "4");
        List<CodeTableMsg> codeMsgList = new ArrayList<CodeTableMsg>();
        for (CodeTable codeInfo : codeList) {
            codeMsgList.add(codeInfo.toCodeMsg(true));
        }
        return new RestReturnMsg(200, "query Code list successfully", codeMsgList);
    }

    /**
     * @param id
     * @return
     * @date 2019/5/26 下午14:12
     * @description 描述設定
     */
    @ApiOperation("描述設定")
    @RequiresAuthentication
    @GetMapping("/product/{id}/vendor")
    public @ResponseBody
    RestReturnMsg getVendorCodeByProduct(@PathVariable Long id) {
        ProductInfo findOne = productInfoRepository.findById(id).isPresent() ? productInfoRepository.findById(id).get() : null;
        List<CodeTable> codeList = codeTableRepository.findByCodeProductAndCodeCategory(findOne.getProduct(), "2");
        List<CodeTableMsg> codeMsgList = new ArrayList<CodeTableMsg>();
        for (CodeTable codeInfo : codeList) {
            codeMsgList.add(codeInfo.toCodeMsg(true));
        }
        return new RestReturnMsg(200, "query Code list successfully", codeMsgList);
    }

    /**
     * @param id
     * @return
     * @date 2019/5/26 下午14:12
     * @description 描述設定
     */
    @ApiOperation("描述設定")
    @RequiresAuthentication
    @GetMapping("/product/{id}/date_code")
    public @ResponseBody
    RestReturnMsg getDate_codeCodeByProduct(@PathVariable Long id) {
        ProductInfo findOne = productInfoRepository.findById(id).isPresent() ? productInfoRepository.findById(id).get() : null;
        List<CodeTable> codeList = codeTableRepository.findByCodeProductAndCodeCategory(findOne.getProduct(), "3");
        List<CodeTableMsg> codeMsgList = new ArrayList<CodeTableMsg>();
        for (CodeTable codeInfo : codeList) {
            codeMsgList.add(codeInfo.toCodeMsg(true));
        }
        return new RestReturnMsg(200, "query Code list successfully", codeMsgList);
    }
}