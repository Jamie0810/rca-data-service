package com.foxconn.iisd.rcadsvc.domain;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.foxconn.iisd.rcadsvc.msg.CaseMatrixMsg;
import com.foxconn.iisd.rcadsvc.msg.CaseMsg;
import com.foxconn.iisd.rcadsvc.repo.DataSetRepository;
import com.foxconn.iisd.rcadsvc.service.FileService;

@Entity
@Table(name="`case_matrix`")
public class CaseMatrix {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "case_id")
    private Long caseId;
    
    @Column(name = "`matrix_type`")
    private Integer matrixType;
    
    @Column(length = 50)
    private String name;

    private String description;

    @Column(name = "`test_time_start`")
    @Temporal(TemporalType.TIMESTAMP)
    private Date testTimeStart;
    
    @Column(name = "`test_time_end`")
    @Temporal(TemporalType.TIMESTAMP)
    private Date testTimeEnd;
    
    @Column(name = "`setting_json`")
    private String settingJson;
    
    @Column(name = "`inside_nd`")
    private Integer insideND;
    
    @Column(name = "`overall_nd`")
    private Integer overallND;
    
    @Column(name = "`report_time`")
    @Temporal(TemporalType.TIMESTAMP)
    private Date reportTime;
    
    @Column(name = "`create_user`")
    private String createUser;

    @Column(name = "`create_time`")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @Column(name = "`modify_user`")
    private String modifyUser;
    
    @Column(name = "`modify_time`")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifyTime;
    
    @Column(name = "`file_path`")
    private String filePath;
    
    @Column(name = "`status`")
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

	public Date getTestTimeStart() {
		return testTimeStart;
	}

	public void setTestTimeStart(Date testTimeStart) {
		this.testTimeStart = testTimeStart;
	}

	public Date getTestTimeEnd() {
		return testTimeEnd;
	}

	public void setTestTimeEnd(Date testTimeEnd) {
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

	public Date getReportTime() {
		return reportTime;
	}

	public void setReportTime(Date reportTime) {
		this.reportTime = reportTime;
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

	public String getModifyUser() {
		return modifyUser;
	}

	public void setModifyUser(String modifyUser) {
		this.modifyUser = modifyUser;
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public CaseMatrixMsg toCaseMatrixMsg(boolean withChild, FileService fileService){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        CaseMatrixMsg caseMatrixMsg = new CaseMatrixMsg();
        caseMatrixMsg.setId(this.getId());
        caseMatrixMsg.setCaseId(this.getCaseId());
        caseMatrixMsg.setMatrixType(this.getMatrixType());
        caseMatrixMsg.setName(this.getName());
        caseMatrixMsg.setDescription(this.getDescription());
        caseMatrixMsg.setTestTimeStart(this.getTestTimeStart()==null ? null : dateFormat.format(this.getTestTimeStart()));
        caseMatrixMsg.setTestTimeEnd(this.getTestTimeEnd()==null ? null : dateFormat.format(this.getTestTimeEnd()));
        caseMatrixMsg.setSettingJson(this.getSettingJson());
        caseMatrixMsg.setInsideND(this.getInsideND());
        caseMatrixMsg.setOverallND(this.getOverallND());
        caseMatrixMsg.setReportTime(this.getReportTime()==null ? null : dateFormat.format(this.getReportTime()));
        caseMatrixMsg.setCreateUser(this.getCreateUser());
        caseMatrixMsg.setCreateTime(this.getCreateTime()==null ? null : dateFormat.format(this.getCreateTime()));
        caseMatrixMsg.setModifyUser(this.getModifyUser());
        caseMatrixMsg.setModifyTime(this.getModifyTime()==null ? null : dateFormat.format(this.getModifyTime()));
        try{
        	if(this.getFilePath()!=null){
        		List<String> urlList = new ArrayList<String>();
        		String[] filePathList = this.getFilePath().split(",");
        		for(String fp : filePathList){
        			urlList.add(fileService.getURL(fp));
        		}
        		caseMatrixMsg.setFilePath(urlList);
        	}
        	//caseMatrixMsg.setFilePath(this.getFilePath()==null ? null : fileService.getURL(this.getFilePath()));
        }catch(Exception e){
        	e.printStackTrace();
        }
        return caseMatrixMsg;
    }
}
