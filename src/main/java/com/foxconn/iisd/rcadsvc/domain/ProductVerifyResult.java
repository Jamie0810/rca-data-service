package com.foxconn.iisd.rcadsvc.domain;

import com.foxconn.iisd.rcadsvc.msg.ProductMsg;
import com.foxconn.iisd.rcadsvc.msg.ProductVerifyResultMsg;
import com.foxconn.iisd.rcadsvc.service.FileService;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="`product_verify_result`")
public class ProductVerifyResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "`product`")
    private String product;

	@Column(name = "`verify_type`")
	private String verifyType;

	@Column(name = "`verify_date`")
	@Temporal(TemporalType.TIMESTAMP)
	private Date verifyDate;

	@Column(name = "`verify_result`")
	private String verifyResult;

	@Column(name = "`report_path`")
	private String reportPath;

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

	public String getVerifyType() {
		return verifyType;
	}

	public void setVerifyType(String verifyType) {
		this.verifyType = verifyType;
	}

	public Date getVerifyDate() {
		return verifyDate;
	}

	public void setVerifyDate(Date verifyDate) {
		this.verifyDate = verifyDate;
	}

	public String getVerifyResult() {
		return verifyResult;
	}

	public void setVerifyResult(String verifyResult) {
		this.verifyResult = verifyResult;
	}

	public String getReportPath() {
		return reportPath;
	}

	public void setReportPath(String reportPath) {
		this.reportPath = reportPath;
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

	public ProductVerifyResultMsg toProductVerifyResultMsg(FileService fileService){
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		ProductVerifyResultMsg msg = new ProductVerifyResultMsg();
		msg.setFilePath(this.getReportPath());
		msg.setProduct(this.getProduct());
		msg.setId(this.getId());
		msg.setVerifyDate(dateFormat.format(this.getVerifyDate()));
		msg.setVerifyType(this.getVerifyType());
		msg.setVerifyResult(this.getVerifyResult());
		try{
			if(this.getReportPath()!=null){
				List<String> urlList = new ArrayList<String>();
				msg.setFilePath(fileService.getURL(this.getReportPath()));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return msg;
	}
}
