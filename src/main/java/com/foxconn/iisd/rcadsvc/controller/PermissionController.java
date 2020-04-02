package com.foxconn.iisd.rcadsvc.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(description = "資源服務")
@RestController
@RequestMapping("/security/permissions/")
public class PermissionController {

    @ApiOperation("查詢资源")
    @GetMapping()
    @RequiresPermissions("wyj:**")
    public String get(){
        return "有permission:retrieve这个权限的用户才能访问，不然访问不了";
    }
}
