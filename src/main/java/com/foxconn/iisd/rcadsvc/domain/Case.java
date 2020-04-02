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

import com.foxconn.iisd.rcadsvc.msg.CaseMsg;
import com.foxconn.iisd.rcadsvc.repo.DataSetRepository;

@Entity
@Table(name="`case`")
public class Case {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "`data_set_id`")
    private Long dssId;
    
    @Column(length = 50)
    private String name;

    private String remark;

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

    @Column(name = "`setting_json`")
    private String settingJson;
    
    @Column(name = "`data_last_date`")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataLastDate;
    
//    @OneToMany(cascade={CascadeType.ALL})
//    @JoinColumn(name="caseId")
//    private Set<CasePlot> casePlot;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getDssId() {
		return dssId;
	}

	public void setDssId(Long dssId) {
		this.dssId = dssId;
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

	public String getSettingJson() {
		return settingJson;
	}

	public void setSettingJson(String settingJson) {
		this.settingJson = settingJson;
	}

	public Date getDataLastDate() {
		return dataLastDate;
	}

	public void setDataLastDate(Date dataLastDate) {
		this.dataLastDate = dataLastDate;
	}

//	public Set<CasePlot> getCasePlot() {
//		return casePlot;
//	}
//
//	public void setCasePlot(Set<CasePlot> casePlot) {
//		this.casePlot = casePlot;
//	}
	
	public CaseMsg toCaseMsg(boolean withChild, DataSetRepository dataSetRepository){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        DataSetSetting dss = dataSetRepository.findById(this.getDssId()).isPresent()?dataSetRepository.findById(this.getDssId()).get():null;
        
        CaseMsg caseMsg = new CaseMsg();
        caseMsg.setId(this.getId());
        if(dss!=null){
            caseMsg.setProduct(dss.getProduct());
            caseMsg.setDataSetName(dss.getName());
        }else{
        	caseMsg.setProduct("");
            caseMsg.setDataSetName("");
        }
        caseMsg.setDssId(this.getDssId());
        caseMsg.setName(this.getName());
        caseMsg.setRemark(this.getRemark());
        caseMsg.setCreateUser(this.getCreateUser());
        caseMsg.setCreateTime(this.getCreateTime()==null ? null : dateFormat.format(this.getCreateTime()));
        caseMsg.setModifyUser(this.getModifyUser());
        caseMsg.setModifyTime(this.getModifyTime()==null ? null : dateFormat.format(this.getModifyTime()));
        caseMsg.setSettingJson(this.getSettingJson());
        caseMsg.setDataLastDate(this.getDataLastDate()==null ? null : dateFormat.format(this.getDataLastDate()));
        return caseMsg;
    }
}
