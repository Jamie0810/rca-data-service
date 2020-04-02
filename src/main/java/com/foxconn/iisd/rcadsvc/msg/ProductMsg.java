package com.foxconn.iisd.rcadsvc.msg;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import com.foxconn.iisd.rcadsvc.domain.ProductInfo;

public class ProductMsg {

	private Long id;

    private String customer;

    private String product;

    private String jobStartTime;

    private String jobEndTime;

    private String isEnable;

    private String referProduct;

    private List<Integer> referType;

    private String createUser;

    private String createTime;

    private String modifyUser;
    
    private String modifyTime;

    private Integer verifyResult;

    private List<ShiftMsg> shift;

    private String trueFailRule ;

    private Integer mdTolerateTime;

    private Integer taTolerateTime;

    private Integer uploadFreq;

    private String ftpPath;

    private String summaryFilePath;

    private String verify_start_time;

    private String verify_end_time;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getJobStartTime() {
        return jobStartTime;
    }

    public void setJobStartTime(String jobStartTime) {
        this.jobStartTime = jobStartTime;
    }

    public String getJobEndTime() {
        return jobEndTime;
    }

    public void setJobEndTime(String jobEndTime) {
        this.jobEndTime = jobEndTime;
    }


    public String getIsEnable() {
        return isEnable;
    }

    public void setIsEnable(String isEnable) {
        this.isEnable = isEnable;
    }

    public String getReferProduct() {
        return referProduct;
    }

    public void setReferProduct(String referProduct) {
        this.referProduct = referProduct;
    }

    public List<Integer> getReferType() {
        return referType;
    }

    public void setReferType(List<Integer> referType) {
        this.referType = referType;
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

    public Integer getVerifyResult() {
        return verifyResult;
    }

    public void setVerifyResult(Integer verifyResult) {
        this.verifyResult = verifyResult;
    }

    public List<ShiftMsg> getShift() {
        return shift;
    }

    public void setShift(List<ShiftMsg> shift) {
        this.shift = shift;
    }

    public String getTrueFailRule() {
        return trueFailRule;
    }

    public void setTrueFailRule(String trueFailRule) {
        this.trueFailRule = trueFailRule;
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

    public String getVerify_start_time() {
        return verify_start_time;
    }

    public void setVerify_start_time(String verify_start_time) {
        this.verify_start_time = verify_start_time;
    }

    public String getVerify_end_time() {
        return verify_end_time;
    }

    public void setVerify_end_time(String verify_end_time) {
        this.verify_end_time = verify_end_time;
    }

    public ProductInfo toNewDDS(){
        DateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
        ProductInfo newDss = new ProductInfo();
        newDss.setCustomer(this.getCustomer());
        newDss.setProduct(this.getProduct());

        try {
            newDss.setJobStartTime(sdf.parse(this.getJobStartTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            newDss.setJobEndTime(sdf.parse(this.getJobEndTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        newDss.setIsEnable(this.getIsEnable());
        try {
            newDss.setVerifyStarTime(sdf.parse(this.getJobStartTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            Calendar c = Calendar.getInstance();
            c.setTime(sdf.parse(this.getJobStartTime()));
            c.add(Calendar.DATE, 10);
            newDss.setVerifyEndTime(c.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        newDss.setMdTolerateTime(this.getMdTolerateTime());
        newDss.setUploadFreq(this.getUploadFreq());
        newDss.setTaTolerateTime(this.getTaTolerateTime());
        newDss.setTrueFailRule(this.getTrueFailRule());
        return newDss;
    }

    public ProductInfo toNewUpdateDDS(){
        DateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
        ProductInfo newDss = new ProductInfo();
        newDss.setCustomer(this.getCustomer());
        newDss.setProduct(this.getProduct());

        try {
            newDss.setJobStartTime(sdf.parse(this.getJobStartTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            newDss.setJobEndTime(sdf.parse(this.getJobEndTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        newDss.setIsEnable(this.getIsEnable());
        return newDss;
    }
    public ProductInfo toUpdateLogicDDS(){
        ProductInfo newDss = new ProductInfo();
        newDss.setProduct(this.getProduct());
        newDss.setTaTolerateTime(this.getTaTolerateTime());
        newDss.setUploadFreq(this.getUploadFreq());
        newDss.setMdTolerateTime(this.getMdTolerateTime());
        newDss.setId(this.getId());
        newDss.setTrueFailRule(this.getTrueFailRule());
        return newDss;
    }
}
