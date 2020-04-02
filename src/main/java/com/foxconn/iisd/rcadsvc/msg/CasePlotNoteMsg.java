package com.foxconn.iisd.rcadsvc.msg;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.foxconn.iisd.rcadsvc.domain.CasePlotNote;

public class CasePlotNoteMsg {

	private Long id;

    private Long plotId;

	private Long caseId;
    
    private String noteTitle;

    private String noteText;

    private String noteRemark;
    
    private String createTime;

	private String plotJson;

	private String dataJson;
	
	private Integer plotType;
	
	private String updateTime;

    private String createUser;

    private String modifyUser;

    private String filePath;
    
    private String caseSettingJson;
    
    private String dataSetName;

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPlotId() {
		return plotId;
	}

	public void setPlotId(Long plotId) {
		this.plotId = plotId;
	}

	public String getNoteTitle() {
		return noteTitle;
	}

	public void setNoteTitle(String noteTitle) {
		this.noteTitle = noteTitle;
	}

	public String getNoteText() {
		return noteText;
	}

	public void setNoteText(String noteText) {
		this.noteText = noteText;
	}

	public String getNoteRemark() {
		return noteRemark;
	}

	public void setNoteRemark(String noteRemark) {
		this.noteRemark = noteRemark;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public Long getCaseId() {
		return caseId;
	}

	public void setCaseId(Long caseId) {
		this.caseId = caseId;
	}

	public String getPlotJson() {
		return plotJson;
	}

	public void setPlotJson(String plotJson) {
		this.plotJson = plotJson;
	}

	public String getDataJson() {
		return dataJson;
	}

	public void setDataJson(String dataJson) {
		this.dataJson = dataJson;
	}
	
	public Integer getPlotType() {
		return plotType;
	}

	public void setPlotType(Integer plotType) {
		this.plotType = plotType;
	}
	
	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	
	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getModifyUser() {
		return modifyUser;
	}

	public void setModifyUser(String modifyUser) {
		this.modifyUser = modifyUser;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getCaseSettingJson() {
		return caseSettingJson;
	}

	public void setCaseSettingJson(String caseSettingJson) {
		this.caseSettingJson = caseSettingJson;
	}
	
	public String getDataSetName() {
		return dataSetName;
	}

	public void setDataSetName(String dataSetName) {
		this.dataSetName = dataSetName;
	}

	public CasePlotNote toNewDDS(){
		DateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm");
		CasePlotNote newDss = new CasePlotNote();
        newDss.setPlotId(this.getPlotId());
        newDss.setNoteTitle(this.getNoteTitle());
        newDss.setNoteText(this.getNoteText());
        newDss.setNoteRemark(this.getNoteRemark());
        newDss.setCaseId(this.getCaseId());
		newDss.setDataJson(this.getDataJson());
		newDss.setPlotJson(this.getPlotJson());
		newDss.setPlotType(this.getPlotType());
		newDss.setFilePath(this.getFilePath());
		Date nowDate = new Date();
		newDss.setUpdateTime(nowDate);
        return newDss;
    }
}
