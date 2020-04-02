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

import com.foxconn.iisd.rcadsvc.msg.VerifyMsg;

@Entity
@Table(name="`product_interactive_sn`")
public class ProductInteractiveSN {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "`product`")
    private String product;
    
    @Column(name = "`sn`")
    private String sn;
    
    @Column(name = "`query_time`")
    @Temporal(TemporalType.TIMESTAMP)
    private Date queryTime;
    
    @Column(name = "`status`")
    private String status;
    
    @Column(name = "`verify_time`")
    @Temporal(TemporalType.TIMESTAMP)
    private Date verifyTime;
    
    @Column(name = "`verify_result`")
    private String verifyResult;

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
	
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public Date getQueryTime() {
		return queryTime;
	}

	public void setQueryTime(Date queryTime) {
		this.queryTime = queryTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public Date getVerifyTime() {
		return verifyTime;
	}

	public void setVerifyTime(Date verifyTime) {
		this.verifyTime = verifyTime;
	}

	public String getVerifyResult() {
		return verifyResult;
	}

	public void setVerifyResult(String verifyResult) {
		this.verifyResult = verifyResult;
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

	public VerifyMsg toVerifyMsg(boolean withChild){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        
        VerifyMsg verifyMsg = new VerifyMsg();
        verifyMsg.setId(this.getId());
        verifyMsg.setProduct(this.getProduct());
        verifyMsg.setSn(this.getSn());
        verifyMsg.setQueryTime(this.getQueryTime()==null ? null : dateFormat.format(this.getQueryTime()));
        verifyMsg.setStatus(this.getStatus());
        verifyMsg.setVerifyTime(this.getVerifyTime()==null ? null : dateFormat.format(this.getVerifyTime()));
        verifyMsg.setVerifyResult(this.getVerifyResult());
        verifyMsg.setCreateUser(this.getCreateUser());
        verifyMsg.setCreateTime(this.getCreateTime()==null ? null : dateFormat.format(this.getCreateTime()));
        verifyMsg.setModifyUser(this.getModifyUser());
        verifyMsg.setModifyTime(this.getModifyTime()==null ? null : dateFormat.format(this.getModifyTime()));
        return verifyMsg;
    }
}
