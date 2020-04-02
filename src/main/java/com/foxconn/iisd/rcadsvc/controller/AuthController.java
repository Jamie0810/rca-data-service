package com.foxconn.iisd.rcadsvc.controller;

import com.foxconn.iisd.rcadsvc.domain.FunctionList;
import com.foxconn.iisd.rcadsvc.domain.auth.Permission;
import com.foxconn.iisd.rcadsvc.domain.auth.Role;
import com.foxconn.iisd.rcadsvc.domain.auth.User;
import com.foxconn.iisd.rcadsvc.msg.*;
import com.foxconn.iisd.rcadsvc.repo.FunctionListRepository;
import com.foxconn.iisd.rcadsvc.repo.UserRepository;
import com.foxconn.iisd.rcadsvc.service.PermissionService;
import com.foxconn.iisd.rcadsvc.service.RoleService;
import com.foxconn.iisd.rcadsvc.service.UserService;
import com.foxconn.iisd.rcadsvc.util.menu.MenuAuth;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Api(description = "使用者服務")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public static final String USERNAME_SESSION_KEY = "currentUserName";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    @Autowired
    private FunctionListRepository functionListRepository;


    @Autowired
    @Qualifier("mysqlJtl")
    private JdbcTemplate mysqlJtl;


    /**
     * @param user
     * @return com.foxconn.iisd.rcadsvc.msg.RestReturnMsg
     * @date 2019/3/5 下午4:13
     * @description 使用者登入
     */
    @ApiOperation("使用者登入")
    @PostMapping("/login")
    public @ResponseBody
    RestReturnMsg login(@RequestBody User user) {
        logger.info("===> login  method");
        Integer code = null;
        String msg = null;
        UserLoginMsg returnMsg = new UserLoginMsg();
        // 取得Subject及創建用戶名/密碼身份驗證Token（即用戶身份/憑證）
        Subject subject = SecurityUtils.getSubject();
        // 檢查用戶是否已驗證 , 是將回傳true
        logger.info("===> 檢查用戶是否已驗證 : " + subject.isAuthenticated());
        HashMap dataMap = new HashMap<String, String>();
        // 若無身份驗證，則達成進入條件，開始進行身份驗證
        if (!subject.isAuthenticated()) {
            UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername(), user.getPassword());
            try {
                logger.info("===> 開始進行身份驗證");
                // 登入，就是身份驗證
                subject.login(token);
                logger.info("Login successfully");
                logger.info("===> session id : " + subject.getSession().getId());
                subject.getSession().setAttribute(USERNAME_SESSION_KEY, user.getUsername());
                code = 200;
                msg = "Login successfully";
                String sql = "update user set last_login_time = now() where username='" + user.getUsername() + "'";
                mysqlJtl.execute(sql);
                User dbUser = userService.findByUsername(user.getUsername());
                if(dbUser != null && !dbUser.getEnable().equals("1")){
                    logger.info("==> Your account is locked");
                    code = 406;
                    msg = "Your account is locked";
                    subject.logout();
                }
                else
                    returnMsg = getUserLoginMsg(dbUser);

            } catch (UnknownAccountException use) {
                logger.info("==> User Not Found");
                code = 404;
                msg = "User Not Found";
            } catch (IncorrectCredentialsException ice) {
                logger.info("==> The password does not match for the user");
                code = 401;
                msg = "The password does not match for the user";
            } catch (LockedAccountException lae) {
                logger.info("==> Your account is locked");
                code = 406;
            }
        } else {
            logger.info("==> This user has already logged in");
            code = 200;
            msg = "This user has already logged in";
            dataMap.put("sessionId", subject.getSession().getId());
            dataMap.put("code", code);
        }
        return new RestReturnMsg(code, msg, returnMsg);
    }

    /**
     * @param
     * @return com.foxconn.iisd.rcadsvc.msg.RestReturnMsg
     * @author JasonLai
     * @date 2019/3/14 上午11:53
     * @description 使用者登出
     */
    @ApiOperation("使用者登出")
    @GetMapping("/logout")
    @RequiresAuthentication
    public RestReturnMsg logout() {
        Subject subject = SecurityUtils.getSubject();
        logger.info("===> session id : " + subject.getSession().getId().toString());

        subject.logout();
        logger.info("===> Logout successfully");
        return new RestReturnMsg(200, "Logout successfully", "");
    }

    /**
     * 對字符串md5加密
     *
     * @param str
     * @return
     */
    public String getMD5(String str) {
        String ret = null;
        try {
            // 生成一個MD5加密計算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 計算md5函數
            md.update(str.getBytes());
            // digest()最後確定返回md5 hash值，返回值為8為字符串。因為md5 hash值是16位的hex值，實際上就是8位的字符
            // BigInteger函數則將8位的字符串轉換成16位hex值，用字符串來表示；得到字符串形式的hash值
            ret = new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            //throw new SpeedException("MD5加密出現錯誤");
            e.printStackTrace();
        }
        return ret;
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

