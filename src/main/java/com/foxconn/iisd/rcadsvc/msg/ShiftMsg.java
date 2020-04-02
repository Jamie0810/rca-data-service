package com.foxconn.iisd.rcadsvc.msg;

import com.foxconn.iisd.rcadsvc.domain.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ShiftMsg {


	private String startTime;

	private String stopTime;

	private String description;

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getStopTime() {
		return stopTime;
	}

	public void setStopTime(String stopTime) {
		this.stopTime = stopTime;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
