package com.foxconn.iisd.rcadsvc.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(description = "測試API服務")
@RestController
@RequestMapping("/test")
public class TestController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    /**
     * @param
     * @return java.lang.String
     * @date 2019/3/14 上午11:54
     * @description 測試api服務是否正常
     */
    @ApiOperation("測試api服務是否正常")
    @GetMapping("/ok")
    public String ok() {
        return "ok";
    }
}