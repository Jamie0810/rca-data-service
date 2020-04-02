package com.foxconn.iisd.rcadsvc.msg;

import com.foxconn.iisd.rcadsvc.domain.Log;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogMsg {

	private Long id;

	private String funcName;

	private String userAction;

	private String userActionParam;

	private String userActionJson;

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

	public String getUserAction() {
		return userAction;
	}

	public void setUserAction(String userAction) {
		this.userAction = userAction;
	}

	public String getUserActionParam() {
		return userActionParam;
	}

	public void setUserActionParam(String userActionParam) {
		this.userActionParam = userActionParam;
	}

	public String getUserActionJson() {
		return userActionJson;
	}

	public void setUserActionJson(String userActionJson) {
		this.userActionJson = userActionJson;
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

	public Log toNewDDS(){
		DateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Log newDss = new Log();
        newDss.setFuncName(this.getFuncName());
        newDss.setUserAction(this.getUserAction());
        newDss.setUserActionJson(this.getUserActionJson());
        newDss.setUserActionParam(this.getUserActionParam());

        return newDss;
    }
}
