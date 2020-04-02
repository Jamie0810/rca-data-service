package com.foxconn.iisd.rcadsvc.controller;

import com.foxconn.iisd.rcadsvc.domain.*;
import com.foxconn.iisd.rcadsvc.domain.auth.User;
import com.foxconn.iisd.rcadsvc.msg.*;
import com.foxconn.iisd.rcadsvc.repo.*;
import com.foxconn.iisd.rcadsvc.service.CaseService;
import com.foxconn.iisd.rcadsvc.service.LogService;

import com.foxconn.iisd.rcadsvc.service.PageLogService;
import com.foxconn.iisd.rcadsvc.util.XWJUtil;
import com.foxconn.iisd.rcadsvc.util.plot.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;


import java.util.*;

@Configuration
@Api(description = "Log服務")
@RestController
@RequestMapping("/logger")
public class LogController {

    private static final Logger logger = LoggerFactory.getLogger(LogController.class);


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LogService logService;

    @Autowired
    private PageLogService pagelogService;

    @Autowired
    @Qualifier("mysqlJtl")
    private JdbcTemplate mysqlJtl;




    /**
     * @param logMsg
     * @return com.foxconn.iisd.rcadsvc.msg.RestReturnMsg
     * @date 2019/6/3  10:32
     * @description 新增 Case
     */
    @ApiOperation("新增Log")
    @RequiresAuthentication
    @PostMapping("/api_record")
    public @ResponseBody
    RestReturnMsg create(@RequestBody LogMsg logMsg) {
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
            msg = "Write log successfully!!!";
            Log dblog = logService.createLog(currentUser,logMsg.toNewDDS());

        } else {
            logger.info("==> This user has not logged in");
            msg = "This user has not logged in";
            dataMap.put("error", "error");
            code = 401;
        }
;
        return new RestReturnMsg(code, msg, dataMap);
    }

    /**
     * @param logMsg
     * @return com.foxconn.iisd.rcadsvc.msg.RestReturnMsg
     * @date 2019/6/3  10:32
     * @description 新增 Case
     */
    @ApiOperation("新增Log")
    @RequiresAuthentication
    @PostMapping("/page_record")
    public @ResponseBody
    RestReturnMsg createPageLog(@RequestBody PageLogMsg logMsg) {
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
            msg = "Write log successfully!!!";
            PageLog dblog = pagelogService.createLog(currentUser,logMsg.toNewDDS());

        } else {
            logger.info("==> This user has not logged in");
            msg = "This user has not logged in";
            dataMap.put("error", "error");
            code = 401;
        }
        ;
        return new RestReturnMsg(code, msg, dataMap);
    }

}