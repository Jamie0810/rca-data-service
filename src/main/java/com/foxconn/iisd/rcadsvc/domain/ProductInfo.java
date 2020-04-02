package com.foxconn.iisd.rcadsvc.domain;

import com.foxconn.iisd.rcadsvc.msg.ProductMsg;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name="`product_info`")
public class ProductInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "`product`")
    private String product;

    @Column(name = "`customer`")
    private String customer;

	@Column(name = "`true_fail_rule`")
    private String trueFailRule;

	@Column(name = "`job_start_time`")
	@Temporal(TemporalType.TIMESTAMP)
	private Date jobStartTime;

	@Column(name = "`job_end_time`")
	@Temporal(TemporalType.TIMESTAMP)
	private Date jobEndTime;

	@Column(name = "`md_tolerate_time`")
	private Integer mdTolerateTime;

	@Column(name = "`ta_tolerate_time`")
	private Integer taTolerateTime;

	@Column(name = "`upload_freq`")
	private Integer uploadFreq;

	@Column(name = "`ftp_path`")
	private String ftpPath;

	@Column(name = "`summary_file_path`")
	private String summaryFilePath;

	@Column(name = "`verify_start_time`")
	@Temporal(TemporalType.TIMESTAMP)
	private Date verifyStarTime;

	@Column(name = "`verify_end_time`")
	@Temporal(TemporalType.TIMESTAMP)
	private Date verifyEndTime;

	@Column(name = "`is_enable`")
	private String isEnable;

	@Column(name = "`verify_status`")
	private String verifyStatus;

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

    @Column(name = "`minio_lastest_time`")
    @Temporal(TemporalType.TIMESTAMP)
    private Date minioLastestTime;

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

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getTrueFailRule() {
		return trueFailRule;
	}

	public void setTrueFailRule(String trueFailRule) {
		this.trueFailRule = trueFailRule;
	}

	public Date getJobStartTime() {
		return jobStartTime;
	}

	public void setJobStartTime(Date jobStartTime) {
		this.jobStartTime = jobStartTime;
	}

	public Date getJobEndTime() {
		return jobEndTime;
	}

	public void setJobEndTime(Date jobEndTime) {
		this.jobEndTime = jobEndTime;
	}

	public Integer getMdTolerateTime() {
		return mdTolerateTime;
	}

	public void setMdTolerateTime(Integer mdTolerateTime) {
		this.mdTolerateTime = mdTolerateTime;
	}

	public Integer getTaTolerateTime() {
		return taTolerateTime;
	}

	public void setTaTolerateTime(Integer taTolerateTime) {
		this.taTolerateTime = taTolerateTime;
	}

	public Integer getUploadFreq() {
		return uploadFreq;
	}

	public void setUploadFreq(Integer uploadFreq) {
		this.uploadFreq = uploadFreq;
	}

	public String getFtpPath() {
		return ftpPath;
	}

	public void setFtpPath(String ftpPath) {
		this.ftpPath = ftpPath;
	}

	public String getSummaryFilePath() {
		return summaryFilePath;
	}

	public void setSummaryFilePath(String summaryFilePath) {
		this.summaryFilePath = summaryFilePath;
	}

	public Date getVerifyStarTime() {
		return verifyStarTime;
	}

	public void setVerifyStarTime(Date verifyStarTime) {
		this.verifyStarTime = verifyStarTime;
	}

	public Date getVerifyEndTime() {
		return verifyEndTime;
	}

	public void setVerifyEndTime(Date verifyEndTime) {
		this.verifyEndTime = verifyEndTime;
	}

	public String getIsEnable() {
		return isEnable;
	}

	public void setIsEnable(String isEnable) {
		this.isEnable = isEnable;
	}

	public String getVerifyStatus() {
		return verifyStatus;
	}

	public void setVerifyStatus(String verifyStatus) {
		this.verifyStatus = verifyStatus;
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

    public Date getMinioLastestTime() {
        return minioLastestTime;
    }

    public void setMinioLastestTime(Date minioLastestTime) {
        this.minioLastestTime = minioLastestTime;
    }

	public ProductMsg toProductMsg(){
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");


		ProductMsg productMsg = new ProductMsg();
		productMsg.setId(this.getId());
		productMsg.setProduct(this.getProduct());
		productMsg.setCustomer(this.getCustomer());
		productMsg.setIsEnable(this.getIsEnable());
		productMsg.setCreateUser(this.getCreateUser());
		productMsg.setJobStartTime(this.getJobStartTime()==null ? null : dateFormat.format(this.getJobStartTime()));
		productMsg.setJobEndTime(this.getJobEndTime()==null ? null : dateFormat.format(this.getJobEndTime()));
		productMsg.setCreateUser(this.getCreateUser());
		productMsg.setCreateTime(this.getCreateTime()==null ? null : dateFormat.format(this.getCreateTime()));
		productMsg.setModifyUser(this.getModifyUser());
		productMsg.setModifyTime(this.getModifyTime()==null ? null : dateFormat.format(this.getModifyTime()));


		return productMsg;
	}

	public ProductMsg toLogicMsg(){
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");


		ProductMsg productMsg = new ProductMsg();
		productMsg.setId(this.getId());
		productMsg.setProduct(this.getProduct());
		productMsg.setTrueFailRule(this.getTrueFailRule());
		productMsg.setMdTolerateTime(this.getMdTolerateTime());
		productMsg.setTaTolerateTime(this.getTaTolerateTime());
		productMsg.setUploadFreq(this.getUploadFreq());


		return productMsg;
	}

	public ProductMsg toProducVerifytMsg(String ftpPath, String summaryPath){
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


		ProductMsg productMsg = new ProductMsg();
		productMsg.setFtpPath(ftpPath);
		productMsg.setSummaryFilePath(summaryPath);
		productMsg.setVerify_start_time(this.getVerifyStarTime()==null ? null : dateFormat.format(this.getVerifyStarTime()));
		productMsg.setVerify_end_time(this.getVerifyEndTime()==null ? null : dateFormat.format(this.getVerifyEndTime()));



		return productMsg;
	}
}
