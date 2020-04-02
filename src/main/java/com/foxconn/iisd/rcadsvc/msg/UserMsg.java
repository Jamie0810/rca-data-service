package com.foxconn.iisd.rcadsvc.msg;

import java.util.List;

public class UserMsg {

	private Long id;

	private String account;

	private String name;

	private String remark;

	private String org;

	private Integer enable;

	private String roleName;

	private String createUser;

	private String createTime;

	private String modifyUser;

	private String modifyTime;

	private String lastLoginTime;

	private String disableModifyTime;


	private List<UserRoleMsg> roles;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
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

	public Integer getEnable() {
		return enable;
	}

	public void setEnable(Integer enable) {
		this.enable = enable;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getModifyUser() {
		return modifyUser;
	}

	public void setModifyUser(String modifyUser) {
		this.modifyUser = modifyUser;
	}

	public String getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(String modifyTime) {
		this.modifyTime = modifyTime;
	}

	public String getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(String lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public List<UserRoleMsg> getRoles() {
		return roles;
	}

	public void setRoles(List<UserRoleMsg> roles) {
		this.roles = roles;
	}


	public String getDisableModifyTime() {
		return disableModifyTime;
	}

	public void setDisableModifyTime(String disableModifyTime) {
		this.disableModifyTime = disableModifyTime;
	}

	public String getOrg() {
		return org;
	}

	public void setOrg(String org) {
		this.org = org;
	}
}
