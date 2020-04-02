package com.foxconn.iisd.rcadsvc.controller;

import com.foxconn.iisd.rcadsvc.domain.FunctionList;
import com.foxconn.iisd.rcadsvc.domain.auth.Permission;
import com.foxconn.iisd.rcadsvc.domain.auth.Role;
import com.foxconn.iisd.rcadsvc.domain.auth.User;
import com.foxconn.iisd.rcadsvc.msg.CaseMsg;
import com.foxconn.iisd.rcadsvc.msg.EntriesMsg;
import com.foxconn.iisd.rcadsvc.msg.RestReturnMsg;
import com.foxconn.iisd.rcadsvc.msg.RoleMsg;
import com.foxconn.iisd.rcadsvc.repo.UserRepository;
import com.foxconn.iisd.rcadsvc.repo.FunctionListRepository;
import com.foxconn.iisd.rcadsvc.service.PermissionService;
import com.foxconn.iisd.rcadsvc.service.RoleService;
import com.foxconn.iisd.rcadsvc.util.menu.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Api(description = "角色服務")
@RestController
@RequestMapping("/roles")
public class RolesController {

    private static final Logger logger = LoggerFactory.getLogger(RolesController.class);

    public static final String USERNAME_SESSION_KEY = "currentUserName";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private FunctionListRepository functionListRepository;

    @Autowired
    @Qualifier("mysqlJtl")
    private JdbcTemplate mysqlJtl;

    /**
     * @param
     * @return com.foxconn.iisd.rcadsvc.msg.RestReturnMsg
     * @author JasonLai
     * @date 2019/3/14 上午11:53
     * @description 使用者登出
     */
    @ApiOperation("所有角色權限")
    @GetMapping("/more")
    @RequiresAuthentication
    public RestReturnMsg logout() {
        List<Role> roles = roleService.findAll();
        List<RoleMenu> roleMore = new ArrayList<>();
        for (Role r : roles) {
            List<MenuAuth> mcaList = new ArrayList<>();
            RoleMenu rm = new RoleMenu();
            rm.setId(r.getId());
            rm.setName(r.getName());
            rm.setRemark(r.getRemark());
            for (Permission p : r.getPermissionList()) {
                boolean find = false;
                int index = 0;
                for (int i = 0; i < mcaList.size(); i++) {
                    if (mcaList.get(i).getId() == p.getFunc_id()) {
                        find = true;
                        index = i;
                    }
                }
                if (find) {
                    StringBuilder authBit = new StringBuilder(mcaList.get(index).getAuthBit());
                    if (p.getCode().contains("View"))
                        authBit.setCharAt(3, '1');
                    else if (p.getCode().contains("Create"))
                        authBit.setCharAt(2, '1');
                    else if (p.getCode().contains("Update"))
                        authBit.setCharAt(1, '1');
                    else if (p.getCode().contains("Delete"))
                        authBit.setCharAt(0, '1');
                    MenuAuth newMca = mcaList.get(index);
                    newMca.setAuthBit(authBit.toString());
                    int auth = Integer.parseInt(authBit.toString(), 2);
                    newMca.setAuthority(auth);
                    mcaList.set(index, newMca);
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
                    mcaList.add(newMca);
                }
            }

            rm.setPermission(mcaList);
            roleMore.add(rm);
        }

        logger.info("===> get all permissions successfully");
        return new RestReturnMsg(200, "Logout successfully", roleMore);
    }

    /**
     * @param
     * @return com.foxconn.iisd.rcadsvc.msg.RestReturnMsg
     * @author JasonLai
     * @date 2019/3/14 上午11:53
     * @description 使用者登出
     */
    /*
    @ApiOperation("角色對所有Menu權限")
    @GetMapping("/{id}/entries")
    @RequiresAuthentication
    public RestReturnMsg entries(@PathVariable Long id) {

        Optional<Role> r = roleService.findById(id);
        List<FunctionList> funcList = functionListRepository.findAllByOrderByPathAsc();
        List<MenuAuth> maList = new ArrayList<>();
        StringBuilder authBit = new StringBuilder("0000");
        for (Permission p : r.get().getPermissionList()) {
            if (p.getFunc_id() == maList.get(i).getId()) {
                if (p.getCode().contains("View"))
                    authBit.setCharAt(3, '1');
                else if (p.getCode().contains("Create"))
                    authBit.setCharAt(2, '1');
                else if (p.getCode().contains("Update"))
                    authBit.setCharAt(1, '1');
                else if (p.getCode().contains("Delete"))
                    authBit.setCharAt(0, '1');
                MenuAuth ma = new MenuAuth();
                ma.setId(maList.get(i).getId());
                ma.setKey(maList.get(i).getKey());
                ma.setAuthBit(authBit.toString());
                int auth = Integer.parseInt(authBit.toString(), 2);
                ma.setAuth(auth);
                maList.set(i, ma);
            }
        }


        logger.info("===> get all permissions successfully");
        return new RestReturnMsg(200, "Logout successfully", maList);
    }
*/

    /**
     * @param
     * @return com.foxconn.iisd.rcadsvc.msg.RestReturnMsg
     * @author JasonLai
     * @date 2019/3/14 上午11:53
     * @description 新增角色
     */
    @ApiOperation("新增角色")
    @PostMapping("")
    @RequiresAuthentication
    public RestReturnMsg create(@RequestBody RoleMsg roleMsg) {
        User currentUser = userRepository
                .findByUsername((String) SecurityUtils.getSubject().getSession()
                        .getAttribute(UserController.USERNAME_SESSION_KEY));
        Role r = new Role();
        r.setName(roleMsg.getName());
        r.setCode(roleMsg.getName());
        r.setRemark(roleMsg.getRemark());
        Role dbR = roleService.createRole(currentUser, r);
        String sql = "";
        for (EntriesMsg entries : roleMsg.getPermission()) {
            String authBit = Integer.toBinaryString(entries.getAuthority());
            if (authBit.length() < 4) {
                String str = "";
                int i = authBit.length();
                while (i < 4) {
                    str += "0";
                    i++;
                }
                authBit = str + authBit;
            }
            sql = "select id from func_list where `key` = '"+entries.getKey()+"'";
            List<Map<String, Object>> funcList = mysqlJtl.queryForList(sql);
            int funcId = 0 ;
            if(funcList.size() > 0 )
                funcId = Integer.parseInt(funcList.get(0).get("id").toString());
            else{
                FunctionList f = new FunctionList();
                f.setKey(entries.getKey());
                f.setCreateTime(new Date());
                f.setCreateUser(currentUser.getUsername());
                f = functionListRepository.save(f);
                funcId = f.getId();
                String code = f.getKey()+":Delete";
                sql = "insert into permission(`code`,`name`,`func_id`) Values('"+code+"','"+code+"',"+funcId+")";
                mysqlJtl.execute(sql);
                code = f.getKey()+":Update";
                sql = "insert into permission(`code`,`name`,`func_id`) Values('"+code+"','"+code+"',"+funcId+")";
                mysqlJtl.execute(sql);
                code = f.getKey()+":Create";
                sql = "insert into permission(`code`,`name`,`func_id`) Values('"+code+"','"+code+"',"+funcId+")";
                mysqlJtl.execute(sql);
                code = f.getKey()+":View";
                sql = "insert into permission(`code`,`name`,`func_id`) Values('"+code+"','"+code+"',"+funcId+")";
                mysqlJtl.execute(sql);

            }

            if (authBit.charAt(0) == '1') {
                sql = "select id from permission where func_id =" + funcId + " and code like '%Delete'";
                long permissionId = mysqlJtl.queryForObject(sql, java.lang.Long.class);
                sql = "Insert into role_permission Values(" + dbR.getId() + "," + permissionId + ") ";
                mysqlJtl.execute(sql);
            }
            if (authBit.charAt(1) == '1') {
                sql = "select id from permission where func_id =" + funcId + " and code like '%Update'";
                long permissionId = mysqlJtl.queryForObject(sql, java.lang.Long.class);
                sql = "Insert into role_permission Values(" + dbR.getId() + "," + permissionId + ") ";
                mysqlJtl.execute(sql);
            }
            if (authBit.charAt(2) == '1') {
                sql = "select id from permission where func_id =" + funcId + " and code like '%Create'";
                long permissionId = mysqlJtl.queryForObject(sql, java.lang.Long.class);
                sql = "Insert into role_permission Values(" + dbR.getId() + "," + permissionId + ") ";
                mysqlJtl.execute(sql);
            }
            if (authBit.charAt(3) == '1') {
                sql = "select id from permission where func_id =" + funcId + " and code like '%View'";
                long permissionId = mysqlJtl.queryForObject(sql, java.lang.Long.class);
                sql = "Insert into role_permission Values(" + dbR.getId() + "," + permissionId + ") ";
                mysqlJtl.execute(sql);
            }
        }
        logger.info("===> create role successfully");
        return new RestReturnMsg(200, "create role  successfully", dbR);
    }

    /**
     * @param
     * @return com.foxconn.iisd.rcadsvc.msg.RestReturnMsg
     * @author JasonLai
     * @date 2019/3/14 上午11:53
     * @description 新增角色
     */
    @ApiOperation("更新角色")
    @PutMapping("/{id}")
    @RequiresAuthentication
    public RestReturnMsg update(@PathVariable Long id, @RequestBody RoleMsg roleMsg) {
        User currentUser = userRepository
                .findByUsername((String) SecurityUtils.getSubject().getSession()
                        .getAttribute(UserController.USERNAME_SESSION_KEY));
        Role r = new Role();
        r.setId(id);
        r.setName(roleMsg.getName());
        r.setRemark(roleMsg.getRemark());
        Role dbR = roleService.updateRole(currentUser, r);
        String sql = "";
        sql = "delete from role_permission where role_id = "+id;
        mysqlJtl.execute(sql);
        for (EntriesMsg entries : roleMsg.getPermission()) {
            String authBit = Integer.toBinaryString(entries.getAuthority());
            if (authBit.length() < 4) {
                String str = "";
                int i = authBit.length();
                while (i < 4) {
                    str += "0";
                    i++;
                }
                authBit = str + authBit;
            }
            sql = "select id from func_list where `key` = '"+entries.getKey()+"'";
            List<Map<String, Object>> funcList = mysqlJtl.queryForList(sql);
            int funcId = 0 ;
            if(funcList.size() > 0 )
                funcId = Integer.parseInt(funcList.get(0).get("id").toString());
            else{
                FunctionList f = new FunctionList();
                f.setKey(entries.getKey());
                f.setCreateTime(new Date());
                f.setCreateUser(currentUser.getUsername());
                f = functionListRepository.save(f);
                funcId = f.getId();
                String code = f.getKey()+":Delete";
                sql = "insert into permission(`code`,`name`,`func_id`) Values('"+code+"','"+code+"',"+funcId+")";
                mysqlJtl.execute(sql);
                code = f.getKey()+":Update";
                sql = "insert into permission(`code`,`name`,`func_id`) Values('"+code+"','"+code+"',"+funcId+")";
                mysqlJtl.execute(sql);
                code = f.getKey()+":Create";
                sql = "insert into permission(`code`,`name`,`func_id`) Values('"+code+"','"+code+"',"+funcId+")";
                mysqlJtl.execute(sql);
                code = f.getKey()+":View";
                sql = "insert into permission(`code`,`name`,`func_id`) Values('"+code+"','"+code+"',"+funcId+")";
                mysqlJtl.execute(sql);

            }

            if (authBit.charAt(0) == '1') {
                sql = "select id from permission where func_id =" + funcId + " and code like '%Delete'";
                long permissionId = mysqlJtl.queryForObject(sql, java.lang.Long.class);
                sql = "Insert into role_permission Values(" + dbR.getId() + "," + permissionId + ") ";
                mysqlJtl.execute(sql);
            }
            if (authBit.charAt(1) == '1') {
                sql = "select id from permission where func_id =" + funcId + " and code like '%Update'";
                long permissionId = mysqlJtl.queryForObject(sql, java.lang.Long.class);
                sql = "Insert into role_permission Values(" + dbR.getId() + "," + permissionId + ") ";
                mysqlJtl.execute(sql);
            }
            if (authBit.charAt(2) == '1') {
                sql = "select id from permission where func_id =" + funcId + " and code like '%Create'";
                long permissionId = mysqlJtl.queryForObject(sql, java.lang.Long.class);
                sql = "Insert into role_permission Values(" + dbR.getId() + "," + permissionId + ") ";
                mysqlJtl.execute(sql);
            }
            if (authBit.charAt(3) == '1') {
                sql = "select id from permission where func_id =" + funcId + " and code like '%View'";
                long permissionId = mysqlJtl.queryForObject(sql, java.lang.Long.class);
                sql = "Insert into role_permission Values(" + dbR.getId() + "," + permissionId + ") ";
                mysqlJtl.execute(sql);
            }
        }
        logger.info("===> create role successfully");
        return new RestReturnMsg(200, "create role  successfully", dbR);
    }

    /**
     * @param
     * @return com.foxconn.iisd.rcadsvc.msg.RestReturnMsg
     * @author JasonLai
     * @date 2019/3/14 上午11:53
     * @description 新增角色
     */
    @ApiOperation("取得角色資料")
    @GetMapping("/{id}")
    @RequiresAuthentication
    public RestReturnMsg getRole(@PathVariable Long id) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Optional<Role> dbBase = roleService.findById(id);
        List<MenuAuth> mcaList = new ArrayList<>();
        RoleMenu roleMenu = new RoleMenu();
        roleMenu.setId(dbBase.get().getId());
        roleMenu.setName(dbBase.get().getName());
        roleMenu.setRemark(dbBase.get().getRemark());
        for (Permission p : dbBase.get().getPermissionList()) {
            boolean find = false;
            int index = 0;
            for (int i = 0; i < mcaList.size(); i++) {
                if (mcaList.get(i).getId() == p.getFunc_id()) {
                    find = true;
                    index = i;
                }
            }
            if (find) {
                StringBuilder authBit = new StringBuilder(mcaList.get(index).getAuthBit());
                if (p.getCode().contains("View"))
                    authBit.setCharAt(3, '1');
                else if (p.getCode().contains("Create"))
                    authBit.setCharAt(2, '1');
                else if (p.getCode().contains("Update"))
                    authBit.setCharAt(1, '1');
                else if (p.getCode().contains("Delete"))
                    authBit.setCharAt(0, '1');
                MenuAuth newMca = mcaList.get(index);
                newMca.setAuthBit(authBit.toString());
                int auth = Integer.parseInt(authBit.toString(), 2);
                newMca.setAuthority(auth);
                mcaList.set(index, newMca);
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
                mcaList.add(newMca);
            }
        }
        roleMenu.setPermission(mcaList);
        roleMenu.setCreateTime(dbBase.get().getCreateTime()==null ? null : dateFormat.format(dbBase.get().getCreateTime()));
        roleMenu.setModifyTime(dbBase.get().getModifyTime()==null ? null : dateFormat.format(dbBase.get().getModifyTime()));
        roleMenu.setCreateUser(dbBase.get().getCreateUser());
        roleMenu.setModifyUser(dbBase.get().getModifyUser());
        return new RestReturnMsg(200, "get role successfully", roleMenu);
    }

    /**
     * @param
     * @return com.foxconn.iisd.rcadsvc.msg.RestReturnMsg
     * @author JasonLai
     * @date 2019/3/14 上午11:53
     * @description 取得所有角色
     */
    @ApiOperation("取得所有角色")
    @GetMapping("")
    @RequiresAuthentication
    public RestReturnMsg getRoles() {

        List<Role> rList = roleService.findAll();
        List<RoleMsg> rMsgList = new ArrayList<>();
        for (Role r : rList) {
            RoleMsg rMsg = new RoleMsg();
            rMsg.setId(r.getId());
            rMsg.setName(r.getName());
            rMsgList.add(rMsg);
        }
        return new RestReturnMsg(200, "get role successfully", rMsgList);
    }

    public void updatePermissions(String authBit, String action, Long roleId, int index, int funcID) {
        String sql = "";
        sql = "select id from permission where func_id =" + funcID + " and code like '%" + action + "'";
        Long permissionId = mysqlJtl.queryForObject(sql, java.lang.Long.class);
        sql = "select count(*) from role_permission where role_id = " + roleId + " and permission_id = " + permissionId;
        int count = mysqlJtl.queryForObject(sql, java.lang.Integer.class);
        if (authBit.charAt(index) == '1') {
            if (count == 0) {
                sql = "Insert into role_permission Values(" + roleId + "," + permissionId + ") ";
                mysqlJtl.execute(sql);
            }
        } else {
            if (count != 0) {
                sql = "delete from role_permission where role_id = " + roleId + " and permission_id = " + permissionId;
                mysqlJtl.execute(sql);
            }
        }
    }


}

