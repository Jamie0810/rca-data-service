package com.foxconn.iisd.rcadsvc.msg;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.foxconn.iisd.rcadsvc.domain.ProductInteractiveSN;

public class VerifyMsg {

	private Long id;
	
    private String product;
    
    private String sn;
    
    private String snList;

    private String queryTime;
    
    private String status;
    
    private String verifyTime;
    
    private String verifyResult;

    private String createUser;

    private String createTime;

    private String modifyUser;
    
    private String modifyTime;

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

	public String getSnList() {
        return snList;
    }

    public void setSnList(String snList) {
        this.snList = snList;
    }

    public String getQueryTime() {
		return queryTime;
	}

	public void setQueryTime(String queryTime) {
		this.queryTime = queryTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getVerifyTime() {
		return verifyTime;
	}

	public void setVerifyTime(String verifyTime) {
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

	public List<ProductInteractiveSN> toNewDDS(){
		
		List<ProductInteractiveSN> newDssList = new ArrayList<ProductInteractiveSN>();
		String snString = this.getSnList();
		if(snString!=null){
			String[] snList = snString.split(",");
			for(String sn : snList){
				ProductInteractiveSN newDss = new ProductInteractiveSN();
				newDss.setProduct(this.getProduct());
				newDss.setSn(sn);
		        newDss.setQueryTime(new Date());
		        newDssList.add(newDss);
			}
		}
		
        return newDssList;
    }
}
