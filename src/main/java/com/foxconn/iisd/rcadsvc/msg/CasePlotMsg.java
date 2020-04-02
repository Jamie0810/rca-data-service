package com.foxconn.iisd.rcadsvc.msg;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.foxconn.iisd.rcadsvc.domain.CasePlot;
import com.google.api.client.util.DateTime;

public class CasePlotMsg {

	private Long id;

    private Long caseId;
    
    private Integer plotType;

    private String plotJson;

    private String remark;

    private String updateTime;

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCaseId() {
		return caseId;
	}

	public void setCaseId(Long caseId) {
		this.caseId = caseId;
	}

	public Integer getPlotType() {
		return plotType;
	}

	public void setPlotType(Integer plotType) {
		this.plotType = plotType;
	}

	public String getPlotJson() {
		return plotJson;
	}

	public void setPlotJson(String plotJson) {
		this.plotJson = plotJson;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public CasePlot toNewDDS(){
		DateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm");

		CasePlot newDss = new CasePlot();
        newDss.setCaseId(this.getCaseId());
        newDss.setPlotType(this.getPlotType());
        newDss.setPlotJson(this.getPlotJson());
        newDss.setRemark(this.getRemark());
		Date nowDate = new Date();
		newDss.setUpdateTime(nowDate);
//		newDss.setUpdateTime(nowDate);
        return newDss;
    }
}
