package com.foxconn.iisd.rcadsvc.domain;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.*;

import com.foxconn.iisd.rcadsvc.msg.CaseMsg;
import com.foxconn.iisd.rcadsvc.msg.CasePlotMsg;

@Entity
@Table(name="case_plot")
public class CasePlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "case_id")
    private Long caseId;
    
    @Column(name = "plot_type")
    private Integer plotType;

    @Column(name = "plot_json")
    private String plotJson;

    @Column(name = "update_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    private String remark;
    
//    @OneToMany(cascade={CascadeType.ALL})
//    @JoinColumn(name="plotId")
//    private Set<CasePlotNote> casePlotNote;

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

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    //	public Set<CasePlotNote> getCasePlotNote() {
//		return casePlotNote;
//	}
//
//	public void setCasePlotNote(Set<CasePlotNote> casePlotNote) {
//		this.casePlotNote = casePlotNote;
//	}
    
    public CasePlotMsg toCasePlotMsg(boolean withChild){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        CasePlotMsg casePlotMsg = new CasePlotMsg();
        casePlotMsg.setId(this.getId());
        casePlotMsg.setCaseId(this.getCaseId());
        casePlotMsg.setPlotType(this.getPlotType());
        casePlotMsg.setPlotJson(this.getPlotJson());
        casePlotMsg.setRemark(this.getRemark());
        casePlotMsg.setUpdateTime(this.getUpdateTime()==null ? null : dateFormat.format(this.getUpdateTime()));
        return casePlotMsg;
    }
}
