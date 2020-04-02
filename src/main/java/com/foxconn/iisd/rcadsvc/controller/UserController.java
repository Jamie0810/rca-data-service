package com.foxconn.iisd.rcadsvc.controller;

import com.foxconn.iisd.rcadsvc.msg.*;
import com.foxconn.iisd.rcadsvc.service.PermissionService;
import com.foxconn.iisd.rcadsvc.service.RoleService;
import com.foxconn.iisd.rcadsvc.domain.auth.Role;
import com.foxconn.iisd.rcadsvc.domain.auth.User;
import com.foxconn.iisd.rcadsvc.repo.UserRepository;
import com.foxconn.iisd.rcadsvc.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javafx.collections.transformation.TransformationList;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

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
    @Qualifier("mysqlJtl")
    private JdbcTemplate mysqlJtl;

    /**
     * @return com.foxconn.iisd.rcadsvc.msg.RestReturnMsg
     * @date 2019/3/14 下午1:27
     * @Description 建立使用者
     */
    @ApiOperation("建立使用者")
    @PostMapping("")
    @RequiresAuthentication
    public @ResponseBody
    RestReturnMsg create(@RequestBody UserUIMsg msg) {
        Subject subject = SecurityUtils.getSubject();
        User currentUser = userRepository
                .findByUsername((String) SecurityUtils.getSubject().getSession()
                        .getAttribute(UserController.USERNAME_SESSION_KEY));
        logger.info("===> user name : " + msg.getAccount());
        logger.info("===> user password : " + msg.getAccount());
        //檢查使用者名稱是否重複
        boolean isDuplicateUserName = checkDuplicateUserName(msg.getAccount());
        if (isDuplicateUserName) {
            logger.info("===> 使用者名稱已重複");
            HashMap dataMap = new HashMap();
            dataMap.put("errorCode", "404006");
            return new RestReturnMsg(200, "The specified user already exists on the database.", dataMap);
        } else {
            logger.info("===> 寫入Users table");
            User user = new User();
            user.setUsername(msg.getAccount());
            //TODO password需要進行加密作業
            //logger.info("===> after encrypt  : " + user.getPassword());
            user.setPassword(msg.getAccount());
            user.setName(msg.getName());
            user.setRemark(msg.getRemark());
            if (msg.getEnable() == 0)
                user.setDisableModifyTime(new Date());
            user.setEnable(msg.getEnable().toString());
            user.setOrg(msg.getOrg());
            user.setCreateTime(new Date());
            user.setModifyTime(new Date());
            user.setEmail(msg.getEmail());
            user.setCreateUser(currentUser.getUsername());
            user.setModifyUser(currentUser.getModifyUser());
            user = userRepository.save(user);
            if (msg.getRole() != null) {
                String[] roles = msg.getRole().split(",");
                if (roles.length > 0) {
                    for (int i = 0; i < roles.length; i++) {
                        String sql = "Insert into user_role Values(" + user.getId() + "," + Integer.parseInt(roles[i]) + ")";
                        mysqlJtl.execute(sql);
                    }
                }
            }
            Optional<User> userInfo = userRepository.findById(user.getId());


            return new RestReturnMsg(200, "create user successfully", userInfo.get());
        }
    }

    /**
     * @return com.foxconn.iisd.rcadsvc.msg.RestReturnMsg
     * @date 2019/3/14 下午1:27
     * @Description 更新使用者
     */
    @ApiOperation("更新使用者")
    @PutMapping("/{account}")
    @RequiresAuthentication
    public @ResponseBody
    RestReturnMsg update(@PathVariable String account, @RequestBody UserUIMsg msg) {
        Subject subject = SecurityUtils.getSubject();
        User currentUser = userRepository
                .findByUsername((String) SecurityUtils.getSubject().getSession()
                        .getAttribute(UserController.USERNAME_SESSION_KEY));

        logger.info("===> 寫入Users table");
        User user = new User();

        //TODO password需要進行加密作業
        //logger.info("===> after encrypt  : " + user.getPassword());
        user.setUsername(account);
        user.setName(msg.getName());
        user.setRemark(msg.getRemark());
        user.setEnable(msg.getEnable().toString());
        user.setEmail(msg.getEmail());
        user.setOrg(msg.getOrg());
        user = userService.updateUser(currentUser, user);
        if (msg.getRole() != null) {
            String[] roles = msg.getRole().split(",");
            String sql = "delete from user_role where user_id =" + user.getId();
            mysqlJtl.execute(sql);
            if (roles.length > 0) {
                for (int i = 0; i < roles.length; i++) {
                    sql = "Insert into user_role Values(" + user.getId() + "," + Integer.parseInt(roles[i]) + ")";
                    mysqlJtl.execute(sql);
                }
            }
        }
        Optional<User> userInfo = userRepository.findById(user.getId());

        return new RestReturnMsg(200, "create user successfully", userInfo);

    }



    /**
     * @param
     * @return
     * @date 2019/3/6 下午12:12
     * @description 檢查是否已登入
     */
    @ApiOperation("檢查是否已登入")
    @GetMapping("/checkUserAuthenticated")
    public RestReturnMsg status() {
        Subject subject = SecurityUtils.getSubject();
        // 檢查用戶是否已驗證 , 是將回傳true
        logger.info("===> subject.isAuthenticated() : " + subject.isAuthenticated());
        // 若無身份驗證，則達成進入條件，開始進行身份驗證
        if (!subject.isAuthenticated()) {
            return new RestReturnMsg(401, "User has not logged in yet.");
        } else {
            return new RestReturnMsg(200, "This user has already logged in.");
        }
    }



    /**
     * @param
     * @return
     * @date 2019/3/6 下午12:12
     * @description 查詢使用者
     */
    @ApiOperation("查詢使用者")
    @GetMapping("")
    public RestReturnMsg getUser(@RequestParam(name = "account", required = false) String account, @RequestParam(name = "name", required = false) String name, @RequestParam(name = "roleId", required = false) Integer roleId, @RequestParam(name = "enable", required = false) Integer enable) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        List<UserMsg> userMsgList = new ArrayList<>();
        String sql = "select u.*,GROUP_CONCAT(r.name) as rname from user as u ";
        sql += " left join user_role as ur on u.id = ur.user_id ";
        sql += " left join role as r on r.id = ur.role_id ";
        sql += "where 1=1 ";
        if (account != null)
            sql += " and u.username like '%" + account + "%' ";
        if (name != null)
            sql += " and u.name like '%" + name + "%' ";
        if (roleId != null)
            sql += " and r.id = " + roleId;
        if (enable != null)
            sql += " and u.enable = '" + enable + "' ";
        sql += " GROUP BY u.id ";
        List<Map<String, Object>> resultList = mysqlJtl.queryForList(sql);
        for (Map<String, Object> map : resultList) {
            UserMsg uMsg = new UserMsg();
            uMsg.setAccount(map.get("username").toString());
            if (map.get("name") != null) {
                uMsg.setName(map.get("name").toString());
            }
            if (map.get("enable") != null) {
                if (map.get("enable").toString().equals("1"))
                    uMsg.setEnable(1);
                else
                    uMsg.setEnable(0);
            }
            if (map.get("remark") != null) {
                uMsg.setRemark(map.get("remark").toString());
            }
            if (map.get("last_login_time") != null) {
                try {
                    Date date = dateFormat.parse(map.get("last_login_time").toString());
                    uMsg.setLastLoginTime(dateFormat.format(date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
            if (map.get("modify_time") != null) {
                try {
                    Date date = dateFormat.parse(map.get("modify_time").toString());
                    uMsg.setModifyTime(dateFormat.format(date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
            if (map.get("rname") != null) {
                uMsg.setRoleName(map.get("rname").toString());
            }
            userMsgList.add(uMsg);
        }
        return new RestReturnMsg(200, "Logout successfully", userMsgList);
    }

    /**
     * @param
     * @return com.foxconn.iisd.rcadsvc.msg.RestReturnMsg
     * @author JasonLai
     * @date 2019/3/14 上午11:53
     * @description 使用者內容
     */
    @ApiOperation("使用者內容")
    @GetMapping("/{account}")
    @RequiresAuthentication
    public RestReturnMsg getUser(@PathVariable String account) {
        User user = userRepository.findByUsername(account);

        UserMsg uMsg = new UserMsg();
        List<UserRoleMsg> urMsgList = new ArrayList<>();
        for (Role user_role : user.getRoleList()) {
            UserRoleMsg urMsg = new UserRoleMsg();
            urMsg.setId(user_role.getId());
            urMsg.setName(user_role.getName());
            urMsgList.add(urMsg);
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        uMsg.setRemark(user.getRemark());
        uMsg.setAccount(user.getUsername());
        uMsg.setName(user.getName());
        uMsg.setOrg(user.getOrg());
        uMsg.setCreateUser(user.getCreateUser());
        uMsg.setModifyUser(user.getModifyUser());
        uMsg.setModifyTime(user.getModifyTime() == null ? null : dateFormat.format(user.getModifyTime()));
        uMsg.setCreateTime(user.getCreateTime() == null ? null : dateFormat.format(user.getCreateTime()));
        uMsg.setEnable(user.getEnable() == null ? null : Integer.parseInt(user.getEnable()));
        uMsg.setLastLoginTime(user.getLastLoginTime() == null ? null : dateFormat.format(user.getLastLoginTime()));
        uMsg.setDisableModifyTime(user.getDisableModifyTime() == null ? null : dateFormat.format(user.getDisableModifyTime()));
        uMsg.setRoles(urMsgList);
        return new RestReturnMsg(200, "Logout successfully", uMsg);
    }

    /**
     * @param
     * @return com.foxconn.iisd.rcadsvc.msg.RestReturnMsg
     * @author JasonLai
     * @date 2019/3/14 上午11:53
     * @description 修改密碼
     */
    @ApiOperation("修改密碼")
    @PutMapping("/{account}/pwd")
    @RequiresAuthentication
    public RestReturnMsg modifyPwd(@PathVariable String account, @RequestBody PwdMsg pwdMsg) {
        Subject subject = SecurityUtils.getSubject();
        User currentUser = userRepository
                .findByUsername((String) SecurityUtils.getSubject().getSession()
                        .getAttribute(UserController.USERNAME_SESSION_KEY));
        String sql = "update user set password='" + pwdMsg.getPassword() + "',modify_time = now(),modify_user='" + currentUser.getUsername() + "' ";
        sql += " where username='" + account + "'";
        mysqlJtl.execute(sql);
        return new RestReturnMsg(200, "modify password successfully", "");
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

    /**
     * @param username
     * @return boolean
     * @author JasonLai
     * @date 2019/3/14 下午1:13
     * @description 檢查使用者名稱是否已重複，若有重複回傳true
     */
    private boolean checkDuplicateUserName(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return false;
        } else {
            return true;
        }
    }

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
}

