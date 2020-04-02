package com.foxconn.iisd.rcadsvc.msg;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.foxconn.iisd.rcadsvc.domain.DataSetPart;
import com.foxconn.iisd.rcadsvc.domain.DataSetSetting;
import com.foxconn.iisd.rcadsvc.domain.DataSetStationItem;
import com.google.errorprone.annotations.FormatString;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.List;
import java.util.Date;
import java.util.Set;
import java.util.HashSet;

public class DataSetSettingMsg {

    private Long id;

    private String name;

    private String product;

    private String remark;

    private String btName;

    private String createTime;

    private String createUser;

    private String modifyTime;

    private String modifyUser;

    private String btCreateTime;

    private String btNextTime;

    private String btLastTime;

    //@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private String effectiveStartDate;

    //@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private String effectiveEndDate;

    private List<DataSetPartMsg> dataSetPart;

    private List<DataSetStationItemMsg> dataSetStationItem;
    
    private Integer caseCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getBtName() {
        return btName;
    }

    public void setBtName(String btName) {
        this.btName = btName;
    }

    public String getEffectiveStartDate() {
        return effectiveStartDate;
    }

    public void setEffectiveStartDate(String effectiveStartDate) {
        this.effectiveStartDate = effectiveStartDate;
    }

    public String getEffectiveEndDate() {
        return effectiveEndDate;
    }

    public void setEffectiveEndDate(String effectiveEndDate) {
        this.effectiveEndDate = effectiveEndDate;
    }

    public List<DataSetPartMsg> getDataSetPart() {
        return dataSetPart;
    }

    public void setDataSetPart(List<DataSetPartMsg> dataSetPart) {
        this.dataSetPart = dataSetPart;
    }

    public List<DataSetStationItemMsg> getDataSetStationItem() {
        return dataSetStationItem;
    }

    public void setDataSetStationItem(List<DataSetStationItemMsg> dataSetStationItem) {
        this.dataSetStationItem = dataSetStationItem;
    }

    public String getBtCreateTime() {
        return btCreateTime;
    }

    public void setBtCreateTime(String btCreateTime) {
        this.btCreateTime = btCreateTime;
    }

    public String getBtNextTime() {
        return btNextTime;
    }

    public void setBtNextTime(String btNextTime) {
        this.btNextTime = btNextTime;
    }

    public String getBtLastTime() {
        return btLastTime;
    }

    public void setBtLastTime(String btLastTime) {
        this.btLastTime = btLastTime;
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

    public String getCreateTime() {return createTime;}

    public void setCreateTime(String createTime) { this.createTime = createTime; }

    public String getModifyTime() {return modifyTime; }

    public void setModifyTime(String modifyTime) {  this.modifyTime = modifyTime; }
    
    public Integer getCaseCount() {
		return caseCount;
	}

	public void setCaseCount(Integer caseCount) {
		this.caseCount = caseCount;
	}

	public DataSetSetting toNewDDS(){
        DateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm");
        DataSetSetting newDss = new DataSetSetting();
        //newDss.setId(this.getId());
        try{
            newDss.setEffectiveStartDate(sdf.parse(this.getEffectiveStartDate()));
            newDss.setEffectiveEndDate(sdf.parse(this.getEffectiveEndDate()));
        }catch(Exception e){
            newDss.setEffectiveStartDate(new Date());
            newDss.setEffectiveEndDate((new Date()));
        }

        newDss.setName(this.getName() ==null ? "" : this.getName());
        newDss.setProduct(this.getProduct() ==null ? "" : this.getProduct());
        newDss.setRemark(this.getRemark() ==null ? "" : this.getRemark());

        Set<DataSetStationItem> dsItemSet = new HashSet<DataSetStationItem>();
        List<DataSetStationItemMsg> itemList = this.getDataSetStationItem();
        for(DataSetStationItemMsg item : itemList){
            DataSetStationItem newitem = new DataSetStationItem();
            newitem.setItem(item.getItem());
            newitem.setStation(item.getStation());
            dsItemSet.add(newitem);
        }
        newDss.setDataSetStationItem(dsItemSet);

        Set<DataSetPart> dsPartSet = new HashSet<DataSetPart>();
        List<DataSetPartMsg> partList = this.getDataSetPart();
        for(DataSetPartMsg part : partList){
            DataSetPart newpart = new DataSetPart();
            newpart.setPartType(part.getPartType());
            newpart.setComponent(part.getComponent());
            dsPartSet.add(newpart);
        }
        newDss.setDataSetPart(dsPartSet);
        return newDss;
    }
}
