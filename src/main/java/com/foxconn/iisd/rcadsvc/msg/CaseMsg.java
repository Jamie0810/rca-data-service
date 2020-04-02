package com.foxconn.iisd.rcadsvc.msg;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.foxconn.iisd.rcadsvc.domain.Case;

public class CaseMsg {

	private Long id;

    private Long dssId;
    
    private String name;

    private String remark;

    private String createUser;

    private String createTime;

    private String modifyUser;
    
    private String modifyTime;

    private String settingJson;
    
    private String dataLastDate;
    
    private String product;
    
    private String dataSetName;

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

	public String getSettingJson() {
		return settingJson;
	}

	public void setSettingJson(String settingJson) {
		this.settingJson = settingJson;
	}

	public String getDataLastDate() {
		return dataLastDate;
	}

	public void setDataLastDate(String dataLastDate) {
		this.dataLastDate = dataLastDate;
	}
	
	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getDataSetName() {
		return dataSetName;
	}

	public void setDataSetName(String dataSetName) {
		this.dataSetName = dataSetName;
	}

	public Case toNewDDS(){
		DateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Case newDss = new Case();
        newDss.setDssId(this.getDssId());
        newDss.setName(this.getName());
        newDss.setRemark(this.getRemark());
        newDss.setSettingJson(this.getSettingJson());
        if(this.getDataLastDate()==null){
        	newDss.setDataLastDate(null);
        }else{
	        try{
	        	newDss.setDataLastDate(sdf.parse(this.getDataLastDate()));
	        }catch(ParseException e){
	        	//none
	        }
        }
        return newDss;
    }
}
