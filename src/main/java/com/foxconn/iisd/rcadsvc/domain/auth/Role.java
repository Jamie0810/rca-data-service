package com.foxconn.iisd.rcadsvc.domain.auth;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.foxconn.iisd.rcadsvc.msg.RoleMsg;

/*
 *
 * @author JasonLai
 * @date 2019/3/12 上午10:15
 */
@Entity
@Table(name = "role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 32, unique = true, nullable = false)
    private String code;

    @Column(length = 32, unique = true, nullable = false)
    private String name;


    private String remark;

    @Column(name = "`create_user`")
    private String createUser;

    @Column(name = "`create_time`")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @Column(name = "`modify_user`")
    private String modifyUser;

    @Column(name = "`modify_time`")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifyTime;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "role_permission", joinColumns = {@JoinColumn(name = "role_id")},
            inverseJoinColumns = {@JoinColumn(name = "permission_id")})
    private List<Permission> permissionList = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Permission> getPermissionList() {
        return permissionList;
    }

    public void setPermissionList(List<Permission> permissionList) {
        this.permissionList = permissionList;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public RoleMsg toRoleMsg() {
        RoleMsg roleMsg = new RoleMsg();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        roleMsg.setId(this.getId());
        roleMsg.setName(this.getName());
        roleMsg.setRemark(this.getRemark());
        roleMsg.setCreateTime(this.getCreateTime() == null ? null : dateFormat.format(this.getCreateTime()));
        roleMsg.setCreateUser(this.getCreateUser());
        roleMsg.setModifyUser(this.getModifyUser());
        roleMsg.setModifyTime(this.getModifyTime() == null ? null : dateFormat.format(this.getModifyTime()));
        return roleMsg;
    }
}
