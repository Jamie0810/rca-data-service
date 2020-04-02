package com.foxconn.iisd.rcadsvc.domain;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.foxconn.iisd.rcadsvc.msg.CasePlotNoteMsg;
import org.springframework.beans.factory.annotation.Value;

@Entity
@Table(name="case_plot_note")
public class CasePlotNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plot_id")
    private Long plotId;

    @Column(name = "case_Id")
    private Long caseId;
    
    @Column(name = "note_title")
    private String noteTitle;

    @Column(name = "note_text")
    private String noteText;
    
    @Column(name = "note_remark")
    private String noteRemark;
    
    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @Column(name = "plot_json")
    private String plotJson;

    @Column(name = "data_json")
    private String dataJson;
    
    @Column(name = "plot_type")
    private Integer plotType;

    @Column(name = "update_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;
    
    @Column(name = "`create_user`")
    private String createUser;

    @Column(name = "`modify_user`")
    private String modifyUser;

    @Column(name = "`file_path`")
    private String filePath;
    
    @Column(name = "`setting_json`")
    private String settingJson;
    
    @Column(name = "`dataSetName`")
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

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
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
	
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
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
    
    public String getSettingJson() {
		return settingJson;
	}

	public void setSettingJson(String settingJson) {
		this.settingJson = settingJson;
	}
	
	public String getDataSetName() {
		return dataSetName;
	}

	public void setDataSetName(String dataSetName) {
		this.dataSetName = dataSetName;
	}

	public CasePlotNoteMsg toCasePlotNoteMsg(boolean withChild){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        CasePlotNoteMsg casePlotNoteMsg = new CasePlotNoteMsg();
        casePlotNoteMsg.setId(this.getId());
        casePlotNoteMsg.setPlotId(this.getPlotId());
        casePlotNoteMsg.setNoteTitle(this.getNoteTitle());
        casePlotNoteMsg.setNoteText(this.getNoteText());
        casePlotNoteMsg.setNoteRemark(this.getNoteRemark());
        casePlotNoteMsg.setCreateTime(this.getCreateTime()==null ? null : dateFormat.format(this.getCreateTime()));
        casePlotNoteMsg.setCaseId(this.getCaseId());
        casePlotNoteMsg.setPlotJson(this.getPlotJson()==null ? null :this.getPlotJson());
        casePlotNoteMsg.setDataJson(this.getDataJson()==null ? null :this.getDataJson());
        casePlotNoteMsg.setPlotType(this.getPlotType());
        casePlotNoteMsg.setUpdateTime(this.getUpdateTime()==null ? null : dateFormat.format(this.getUpdateTime()));
        casePlotNoteMsg.setCreateUser(this.getCreateUser()==null ? null : this.getCreateUser());
        casePlotNoteMsg.setModifyUser(this.getModifyUser()==null ? null : this.getModifyUser());
        casePlotNoteMsg.setFilePath(this.getFilePath()==null ? null : this.getFilePath());
        casePlotNoteMsg.setCaseSettingJson(this.getSettingJson()==null ? null :this.getSettingJson());
        casePlotNoteMsg.setDataSetName(this.getDataSetName()==null ? null :this.getDataSetName());
        return casePlotNoteMsg;
    }
}
