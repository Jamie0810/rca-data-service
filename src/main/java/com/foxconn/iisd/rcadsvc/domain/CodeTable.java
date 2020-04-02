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

import com.foxconn.iisd.rcadsvc.msg.CodeTableMsg;

@Entity
@Table(name="`code_table`")
public class CodeTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "`code_product`")
    private String codeProduct;
    
    @Column(name = "`code_category`")
    private String codeCategory;

    private String code;
    
    @Column(name = "`code_name`")
    private String codeName;

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
    
	public String getCodeProduct() {
		return codeProduct;
	}

	public void setCodeProduct(String codeProduct) {
		this.codeProduct = codeProduct;
	}

	public String getCodeCategory() {
		return codeCategory;
	}

	public void setCodeCategory(String codeCategory) {
		this.codeCategory = codeCategory;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCodeName() {
		return codeName;
	}

	public void setCodeName(String codeName) {
		this.codeName = codeName;
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

	public CodeTableMsg toCodeMsg(boolean withChild){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        
        CodeTableMsg codeTableMsg = new CodeTableMsg();
        codeTableMsg.setId(this.getId());
        codeTableMsg.setCodeProduct(this.getCodeProduct());
        codeTableMsg.setCodeCategory(this.getCodeCategory());
        codeTableMsg.setCode(this.getCode());
        codeTableMsg.setCodeName(this.getCodeName());
        codeTableMsg.setCreateUser(this.getCreateUser());
        codeTableMsg.setCreateTime(this.getCreateTime()==null ? null : dateFormat.format(this.getCreateTime()));
        codeTableMsg.setModifyUser(this.getModifyUser());
        codeTableMsg.setModifyTime(this.getModifyTime()==null ? null : dateFormat.format(this.getModifyTime()));
        return codeTableMsg;
    }
}
