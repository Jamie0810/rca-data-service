package com.foxconn.iisd.rcadsvc.msg;

import com.foxconn.iisd.rcadsvc.domain.Case;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class CaseCopyMsg {

	private Long caseId;

	private String name;

	public Long getCaseId() {
		return caseId;
	}

	public void setCaseId(Long caseId) {
		this.caseId = caseId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}

