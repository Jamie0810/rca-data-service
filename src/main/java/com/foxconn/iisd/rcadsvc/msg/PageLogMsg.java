package com.foxconn.iisd.rcadsvc.msg;

import com.foxconn.iisd.rcadsvc.domain.Log;
import com.foxconn.iisd.rcadsvc.domain.PageLog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PageLogMsg {

	private Long id;

	private String funcName;


	private String createUser;

	private Date createTime;


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFuncName() {
		return funcName;
	}

	public void setFuncName(String funcName) {
		this.funcName = funcName;
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

	public PageLog toNewDDS(){
		DateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		PageLog newDss = new PageLog();
        newDss.setFuncName(this.getFuncName());

        return newDss;
    }
}
