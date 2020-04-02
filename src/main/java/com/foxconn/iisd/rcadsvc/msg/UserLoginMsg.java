package com.foxconn.iisd.rcadsvc.msg;

import com.foxconn.iisd.rcadsvc.util.menu.MenuAuth;

import java.util.List;

public class UserLoginMsg {


	private String account;

	private String name;

	private List<MenuAuth> permission;

	private Integer superuser;

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

	public List<MenuAuth> getPermission() {
		return permission;
	}

	public void setPermission(List<MenuAuth> permission) {
		this.permission = permission;
	}

	public Integer getSuperuser() {
		return superuser;
	}

	public void setSuperuser(Integer superuser) {
		this.superuser = superuser;
	}
}
