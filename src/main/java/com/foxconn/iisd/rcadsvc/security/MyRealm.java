package com.foxconn.iisd.rcadsvc.security;

import com.foxconn.iisd.rcadsvc.domain.auth.Role;
import com.foxconn.iisd.rcadsvc.service.PermissionService;
import com.foxconn.iisd.rcadsvc.service.RoleService;
import com.foxconn.iisd.rcadsvc.service.UserService;
import com.foxconn.iisd.rcadsvc.domain.auth.Permission;
import com.foxconn.iisd.rcadsvc.domain.auth.User;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class MyRealm extends AuthorizingRealm {

    private static final Logger log = LoggerFactory.getLogger(AuthorizingRealm.class);

    @Autowired
    private UserService userService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private RoleService roleService;

    /**
     *
     *
     * @author JasonLai
     * @date 2019/3/14 上午10:46
     * @param [principals]
     * @return org.apache.shiro.authz.AuthorizationInfo
     * @description 授權
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        log.info("===> doGetAuthorizationInfo method");
        String username = (String) principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo authorizationInfo = null;
        try {
            authorizationInfo = new SimpleAuthorizationInfo();
            User user = userService.findByUsername(username);
            if(user.getSuperUser() != null && user.getSuperUser().equals("1")){
                List<Role> roleList = roleService.findAll();
                List<Permission> permissionList = permissionService.findAll();
                for (Role r : roleList)
                    authorizationInfo.addRole(r.getCode());
                for(Permission p : permissionList)
                    authorizationInfo.addStringPermission(p.getCode());
            }else {
                List<Role> roleList = user.getRoleList();
                for (Role r : roleList) {
                    authorizationInfo.addRole(r.getCode());
                    List<Permission> list = r.getPermissionList();
                    for (Permission p : list) {
                        authorizationInfo.addStringPermission(p.getCode());
                    }
                }
            }


        } catch (Exception e) {
            log.error("授權錯誤{}", e.getMessage());
            e.printStackTrace();
        }
        return authorizationInfo;
    }

    /**
     *
     *
     * @author JasonLai
     * @date 2019/3/14 上午10:46
     * @param [token]
     * @return org.apache.shiro.authc.AuthenticationInfo
     * @description 登入認證
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        log.info("===> doGetAuthenticationInfo method");

        String username = (String) token.getPrincipal();
        User user = userService.findByUsername(username);
        if(user == null) {
            log.info("===> 資料庫查無此帳號");
            throw new UnknownAccountException(); // 沒找到帳號
        } else {
            log.info("===> 資料庫有此帳號");
        }

        /*if(Boolean.TRUE.equals(user.getLocked())) {
            throw new LockedAccountException(); //帳號鎖定
        }*/

        //交给AuthenticatingRealm使用CredentialsMatcher进行密码匹配，如果觉得人家的不好可以在此判断或自定义实现
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                user.getUsername(), //用户名
                user.getPassword(), //密码
                getName()  //realm name
        );

        return authenticationInfo;
    }

}
