package com.foxconn.iisd.rcadsvc.controller;

import com.foxconn.iisd.rcadsvc.domain.FunctionList;
import com.foxconn.iisd.rcadsvc.domain.auth.Permission;
import com.foxconn.iisd.rcadsvc.domain.auth.Role;
import com.foxconn.iisd.rcadsvc.domain.auth.User;
import com.foxconn.iisd.rcadsvc.msg.PwdSelfMsg;
import com.foxconn.iisd.rcadsvc.msg.RestReturnMsg;
import com.foxconn.iisd.rcadsvc.msg.UserLoginMsg;
import com.foxconn.iisd.rcadsvc.repo.FunctionListRepository;
import com.foxconn.iisd.rcadsvc.repo.UserRepository;
import com.foxconn.iisd.rcadsvc.service.FunctionListService;
import com.foxconn.iisd.rcadsvc.util.menu.MenuAuth;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Configuration
@Api(description = "Log服務")
@RestController
@RequestMapping("/profile")
public class ProfileController {

    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FunctionListService functionListService;

    @Autowired
    private FunctionListRepository functionListRepository;




    @Autowired
    @Qualifier("mysqlJtl")
    private JdbcTemplate mysqlJtl;


    /**
     * @return com.foxconn.iisd.rcadsvc.msg.RestReturnMsg
     * @date 2019/6/3  10:32
     * @description 新增 Case
     */
    @ApiOperation("取得所有Menu")
    @RequiresAuthentication
    @GetMapping("")
    public @ResponseBody
    RestReturnMsg getMenu() {
        logger.info("===> Menu method");
        Integer code = null;
        String msg = null;

        Subject subject = SecurityUtils.getSubject();
        // 檢查用戶是否已驗證 , 是將回傳true
        logger.info("===> 檢查用戶是否已驗證 : " + subject.isAuthenticated());
        HashMap dataMap = new HashMap<String, Object>();
        UserLoginMsg returnMsg = new UserLoginMsg();
        code = 200;
        if (subject.isAuthenticated()) {
            User currentUser = userRepository
                    .findByUsername((String) SecurityUtils.getSubject().getSession()
                            .getAttribute(UserController.USERNAME_SESSION_KEY));
            msg = "get menu successfully!!!";
            returnMsg = getUserLoginMsg(currentUser);
        } else {
            logger.info("==> This user has not logged in");
            msg = "This user has not logged in";
            dataMap.put("error", "error");
            code = 401;
        }
        ;
        return new RestReturnMsg(code, msg, returnMsg);
    }

    /**
     * @param
     * @return com.foxconn.iisd.rcadsvc.msg.RestReturnMsg
     * @author JasonLai
     * @date 2019/3/14 上午11:53
     * @description 修改密碼
     */
    @ApiOperation("修改密碼")
    @PutMapping("/pwd")
    @RequiresAuthentication
    public ResponseEntity<RestReturnMsg> modifyPwdSelf(@RequestBody PwdSelfMsg pwdSelfMsg) {
        Subject subject = SecurityUtils.getSubject();
        User currentUser = userRepository
                .findByUsername((String) SecurityUtils.getSubject().getSession()
                        .getAttribute(UserController.USERNAME_SESSION_KEY));
        String sql = "select count(*) from user where username ='" + currentUser.getUsername() + "' and password = '" + pwdSelfMsg.getOriginalPassword() + "'";
        int count = mysqlJtl.queryForObject(sql, java.lang.Integer.class);
        if (count > 0) {
            sql = "update user set password='" + pwdSelfMsg.getNewPassword() + "',modify_time = now(),modify_user='" + currentUser.getUsername() + "' ";
            sql += " where username='" + currentUser.getUsername() + "'";
            mysqlJtl.execute(sql);

            return new ResponseEntity<RestReturnMsg>(new RestReturnMsg(200, "modify password successfully", ""), HttpStatus.valueOf(200));
        } else {
            return new ResponseEntity<RestReturnMsg>(new RestReturnMsg(301, "error Original Password", ""), HttpStatus.valueOf(301));
        }
    }

    public UserLoginMsg getUserLoginMsg(User user){
        UserLoginMsg msg = new UserLoginMsg();
        List<MenuAuth> menuList = new ArrayList<>();
        if(user.getSuperUser() != null && user.getSuperUser().equals("1")){
            List<FunctionList> functionList = functionListRepository.findAll();
            for(FunctionList f:functionList){
                MenuAuth m = new MenuAuth();
                m.setAuthority(15);
                m.setAuthBit("1111");
                m.setKey(f.getKey());
                menuList.add(m);
            }
            msg.setSuperuser(1);
        }
        else{
            for (Role r : user.getRoleList()) {
                for (Permission p : r.getPermissionList()) {
                    boolean find = false;
                    int index = 0;
                    for (int i = 0; i < menuList.size(); i++) {
                        if (menuList.get(i).getId() == p.getFunc_id()) {
                            find = true;
                            index = i;
                        }
                    }
                    if (find) {
                        StringBuilder authBit = new StringBuilder(menuList.get(index).getAuthBit());
                        if (p.getCode().contains("View"))
                            authBit.setCharAt(3, '1');
                        else if (p.getCode().contains("Create"))
                            authBit.setCharAt(2, '1');
                        else if (p.getCode().contains("Update"))
                            authBit.setCharAt(1, '1');
                        else if (p.getCode().contains("Delete"))
                            authBit.setCharAt(0, '1');
                        MenuAuth newMca = menuList.get(index);
                        newMca.setAuthBit(authBit.toString());
                        int auth = Integer.parseInt(authBit.toString(), 2);
                        newMca.setAuthority(auth);
                        menuList.set(index, newMca);
                    } else {
                        StringBuilder authBit = new StringBuilder("0000");
                        if (p.getCode().contains("View"))
                            authBit.setCharAt(3, '1');
                        else if (p.getCode().contains("Create"))
                            authBit.setCharAt(2, '1');
                        else if (p.getCode().contains("Update"))
                            authBit.setCharAt(1, '1');
                        else if (p.getCode().contains("Delete"))
                            authBit.setCharAt(0, '1');
                        MenuAuth newMca = new MenuAuth();
                        FunctionList func = functionListRepository.findById(p.getFunc_id());
                        newMca.setId(p.getFunc_id());
                        newMca.setKey(func.getKey());
                        newMca.setAuthBit(authBit.toString());
                        int auth = Integer.parseInt(authBit.toString(), 2);
                        newMca.setAuthority(auth);
                        menuList.add(newMca);
                    }
                }
            }
        }
        msg.setAccount(user.getUsername());
        msg.setName(user.getName());
        msg.setPermission(menuList);

        return msg;
    }
}