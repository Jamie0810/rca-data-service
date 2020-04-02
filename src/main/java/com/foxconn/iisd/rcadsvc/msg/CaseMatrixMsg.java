package com.foxconn.iisd.rcadsvc.msg;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import com.foxconn.iisd.rcadsvc.domain.Case;
import com.foxconn.iisd.rcadsvc.domain.CaseMatrix;

public class CaseMatrixMsg {

	private Long id;
    
    private Long caseId;
	
    private Integer matrixType;
    
    private String name;

    private String description;

    private String testTimeStart;
    
    private String testTimeEnd;
    
    private String settingJson;
    
    private Integer insideND;
    
    private Integer overallND;
    
    private String reportTime;
    
    private String createUser;

    private String createTime;

    private String modifyUser;
    
    private String modifyTime;
    
    private List<String> filePath;
    
    private String status;

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

	public Integer getMatrixType() {
		return matrixType;
	}

	public void setMatrixType(Integer matrixType) {
		this.matrixType = matrixType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTestTimeStart() {
		return testTimeStart;
	}

	public void setTestTimeStart(String testTimeStart) {
		this.testTimeStart = testTimeStart;
	}

	public String getTestTimeEnd() {
		return testTimeEnd;
	}

	public void setTestTimeEnd(String testTimeEnd) {
		this.testTimeEnd = testTimeEnd;
	}

	public String getSettingJson() {
		return settingJson;
	}

	public void setSettingJson(String settingJson) {
		this.settingJson = settingJson;
	}

	public Integer getInsideND() {
		return insideND;
	}

	public void setInsideND(Integer insideND) {
		this.insideND = insideND;
	}

	public Integer getOverallND() {
		return overallND;
	}

	public void setOverallND(Integer overallND) {
		this.overallND = overallND;
	}

	public String getReportTime() {
		return reportTime;
	}

	public void setReportTime(String reportTime) {
		this.reportTime = reportTime;
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

	public List<String> getFilePath() {
		return filePath;
	}

	public void setFilePath(List<String> filePath) {
		this.filePath = filePath;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public CaseMatrix toNewDDS(){
		DateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm");
		CaseMatrix newDss = new CaseMatrix();
        newDss.setCaseId(this.getCaseId());
        newDss.setMatrixType(this.getMatrixType());
        newDss.setName(this.getName());
        newDss.setDescription(this.getDescription());
        try{
        	newDss.setTestTimeStart(sdf.parse(this.getTestTimeStart()));
        }catch(Exception e){
        	//none
        }
        try{
        	newDss.setTestTimeEnd(sdf.parse(this.getTestTimeEnd()));
        }catch(Exception e){
        	//none
        }
        newDss.setSettingJson(this.getSettingJson());
        newDss.setInsideND(this.getInsideND()==null?0:this.getInsideND());
        newDss.setOverallND(this.getOverallND()==null?0:this.getOverallND());
        try{
        	newDss.setReportTime(sdf.parse(this.getReportTime()));
        }catch(Exception e){
        	//none
        }
        return newDss;
    }
}
