package com.foxconn.iisd.rcadsvc.msg;

import java.util.List;

public class UserRoleMsg {

	private Long id;

	private String name;

	private String check ;

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

	public String getCheck() {
		return check;
	}

	public void setCheck(String check) {
		this.check = check;
	}
}
