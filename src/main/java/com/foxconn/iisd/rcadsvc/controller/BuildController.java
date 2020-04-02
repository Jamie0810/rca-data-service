package com.foxconn.iisd.rcadsvc.controller;


import com.foxconn.iisd.rcadsvc.domain.auth.User;
import com.foxconn.iisd.rcadsvc.msg.RestReturnMsg;
import com.foxconn.iisd.rcadsvc.repo.UserRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.HashMap;
import java.util.Date;

@Api(description = "Build服務")
@RestController
@RequestMapping("/build")
public class BuildController {

    private static final Logger logger = LoggerFactory.getLogger(BuildController.class);



    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Qualifier("mysqlJtl")
    private JdbcTemplate mysqlJtl;


}

