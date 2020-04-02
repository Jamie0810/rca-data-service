package com.foxconn.iisd.rcadsvc.util.menu;


import java.util.Date;
import java.util.List;

/*
 *
 * @author Keeny
 * @date 2019/5/24 上午10:15
 */

public class RoleMenu {

    private Long id;

    private String name;

    private String remark;

    private String modifyUser;

    private String createTime;

    private String createUser;

    private String modifyTime;

    private List<MenuAuth> permission;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


    public List<MenuAuth> getPermission() {
        return permission;
    }

    public void setPermission(List<MenuAuth> permission) {
        this.permission = permission;
    }

    public String getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }
}
