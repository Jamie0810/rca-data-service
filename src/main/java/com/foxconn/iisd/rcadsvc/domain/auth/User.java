package com.foxconn.iisd.rcadsvc.domain.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/*
 *
 * @author JasonLai
 * @date 2019/3/12 上午10:14
 *
 */

@Entity
@Table(name="user")
@JsonIgnoreProperties(value = {"id", "role"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 32, unique = true, nullable = false)
    private String username;

    @Column(length = 100, nullable = false)
    private String password;

    @Column(name = "name")
    private String name;

    @Column(name = "org")
    private String org;

    @Column(name = "remark")
    private String remark;

    @Column(name = "email")
    private String email;

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

    @Column(name = "enable")
    private String enable;

    @Column(name = "`disable_modify_time`")
    @Temporal(TemporalType.TIMESTAMP)
    private Date disableModifyTime;

    @Column(name = "`last_login_time`")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLoginTime;

    @Column(name = "`superUser`")
    private String superUser;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")})
    private List<Role> roleList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
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

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }

    public Date getDisableModifyTime() {
        return disableModifyTime;
    }

    public void setDisableModifyTime(Date disableModifyTime) {
        this.disableModifyTime = disableModifyTime;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSuperUser() {
        return superUser;
    }

    public void setSuperUser(String superUser) {
        this.superUser = superUser;
    }
}
