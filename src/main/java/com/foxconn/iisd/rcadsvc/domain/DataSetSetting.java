package com.foxconn.iisd.rcadsvc.domain;

import com.foxconn.iisd.rcadsvc.msg.DataSetSettingMsg;
import com.foxconn.iisd.rcadsvc.msg.DataSetPartMsg;
import com.foxconn.iisd.rcadsvc.msg.DataSetInfoMsg;
import com.foxconn.iisd.rcadsvc.msg.DataSetStationItemMsg;
import com.foxconn.iisd.rcadsvc.repo.CaseRepository;
import com.foxconn.iisd.rcadsvc.service.CaseService;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
/*
 *
 * @author Kenny
 * @date 2019/6/5 上午10:16
 */
@Entity
@Table(name="data_set_setting")
public class DataSetSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 32, unique = true, nullable = false)
    private String name;

    private String product;

    private String remark;

    @Column(name = "bt_name")
    private String btName;

    @Column(name = "bt_create_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date btCreateTime;

    @Column(name = "bt_last_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date btLastTime;

    @Column(name = "bt_next_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date btNextTime;

    @Column(name = "create_user")
    private String createUser;

    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @Column(name = "modify_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifyTime;

    @Column(name = "modify_user")
    private String modifyUser;

    @Column(name = "effective_start_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date effectiveStartDate;

    @Column(name = "effective_end_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date effectiveEndDate;

    @OneToMany(cascade={CascadeType.ALL})
    @JoinColumn(name="dss_id")
    private Set<DataSetPart> dataSetPart;

    @OneToMany(cascade={CascadeType.ALL})
    @JoinColumn(name="dss_id")
    private Set<DataSetStationItem> dataSetStationItem;

    public void clearId() {
        id = null;
    }

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

    public Date getBtCreateTime() {
        return btCreateTime;
    }

    public void setBtCreateTime(Date btCreateTime) {
        this.btCreateTime = btCreateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
    }

    public String getProduct() { return product; }

    public void setProduct(String product) { this.product = product; }

    public String getBtName() {
        return btName;
    }

    public void setBtName(String btName) {
        this.btName = btName;
    }

    public Date getEffectiveStartDate() {
        return effectiveStartDate;
    }

    public void setEffectiveStartDate(Date effectiveStartDate) {
        this.effectiveStartDate = effectiveStartDate;
    }

    public Date getEffectiveEndDate() {
        return effectiveEndDate;
    }

    public void setEffectiveEndDate(Date effectiveEndDate) {
        this.effectiveEndDate = effectiveEndDate;
    }

    public Set<DataSetPart> getDataSetPart() {
        return dataSetPart;
    }

    public void setDataSetPart(Set<DataSetPart> dataSetPart) {
        this.dataSetPart = dataSetPart;
    }

    public Set<DataSetStationItem> getDataSetStationItem() {
        return dataSetStationItem;
    }

    public void setDataSetStationItem(Set<DataSetStationItem> dataSetStationItem) {
        this.dataSetStationItem = dataSetStationItem;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getBtLastTime() {
        return btLastTime;
    }

    public void setBtLastTime(Date btLastTime) {
        this.btLastTime = btLastTime;
    }

    public Date getBtNextTime() {
        return btNextTime;
    }

    public void setBtNextTime(Date btNextTime) {
        this.btNextTime = btNextTime;
    }


//    public DataSetSettingMsg toDSSMsg(){
//        return this.toDSSMsg(false);
//    }
    public DataSetSettingMsg toDSSMsg(boolean withChild){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        DataSetSettingMsg dssm = new DataSetSettingMsg();
        dssm.setBtCreateTime(this.getBtCreateTime() ==null ?  ""  : dateFormat.format(this.getBtCreateTime()));
        dssm.setBtLastTime(this.getBtLastTime() ==null ?  ""  : dateFormat.format(this.getBtLastTime()));
        dssm.setBtNextTime(this.getBtNextTime() ==null ?  ""  : dateFormat.format(this.getBtNextTime()));
        dssm.setEffectiveStartDate(this.getEffectiveStartDate()==null ? "" : dateFormat.format(this.getEffectiveStartDate()));
        dssm.setEffectiveEndDate(this.getEffectiveEndDate() == null ? "" : dateFormat.format(this.getEffectiveEndDate()));
        //dssm.setEffectiveStartDate(this.getEffectiveStartDate());
        //dssm.setEffectiveEndDate(this.getEffectiveEndDate());
        dssm.setId(this.getId());
        dssm.setCreateUser(this.getCreateUser() == null ? "" : this.getCreateUser());
        dssm.setModifyUser(this.getModifyUser()==null ?  "" : this.getModifyUser());
        dssm.setName(this.getName() ==null ? "" : this.getName());
        dssm.setProduct(this.getProduct() ==null ? "" : this.getProduct());
        dssm.setRemark(this.getRemark() ==null ? "" : this.getRemark());
        dssm.setCreateTime(this.getCreateTime()==null ? "" : dateFormat.format(this.getCreateTime()));
        dssm.setModifyTime(this.getModifyTime()==null ? "" : dateFormat.format(this.getModifyTime()));
        
        if(withChild){
            List<DataSetStationItemMsg> itemList = new ArrayList<DataSetStationItemMsg>();
            for(DataSetStationItem dssi : this.getDataSetStationItem()){
                DataSetStationItemMsg item = new DataSetStationItemMsg();
                item.setStation(dssi.getStation());
                item.setItem(dssi.getItem());
                itemList.add(item);
            }
            Collections.sort(itemList, new Comparator<DataSetStationItemMsg>(){
                public int compare( DataSetStationItemMsg l1, DataSetStationItemMsg l2 ){
                    return (l1.getStation()+"_"+l1.getItem()).toLowerCase().compareTo((l2.getStation()+"_"+l2.getItem()).toLowerCase());
                }
            });
            dssm.setDataSetStationItem(itemList);
            List<DataSetPartMsg> componentLst = new ArrayList<DataSetPartMsg>();
            for(DataSetPart dsp : this.getDataSetPart()){
                DataSetPartMsg item = new DataSetPartMsg();
                item.setComponent(dsp.getComponent());
                item.setPartType(dsp.getPartType());
                componentLst.add(item);
            }
            Collections.sort(componentLst, new Comparator<DataSetPartMsg>(){
                public int compare( DataSetPartMsg l1, DataSetPartMsg l2 ){
                    return (l1.getPartType()+"_"+l1.getComponent()).toLowerCase().compareTo((l2.getPartType()+"_"+l2.getComponent()).toLowerCase());
                }
            });
            dssm.setDataSetPart(componentLst);
        }


        return dssm;
    }
    
    public DataSetSettingMsg toDSSMsg(boolean withChild, CaseService caseService){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        DataSetSettingMsg dssm = new DataSetSettingMsg();
        dssm.setBtCreateTime(this.getBtCreateTime() ==null ?  ""  : dateFormat.format(this.getBtCreateTime()));
        dssm.setBtLastTime(this.getBtLastTime() ==null ?  ""  : dateFormat.format(this.getBtLastTime()));
        dssm.setBtNextTime(this.getBtNextTime() ==null ?  ""  : dateFormat.format(this.getBtNextTime()));
        dssm.setEffectiveStartDate(this.getEffectiveStartDate()==null ? "" : dateFormat.format(this.getEffectiveStartDate()));
        dssm.setEffectiveEndDate(this.getEffectiveEndDate() == null ? "" : dateFormat.format(this.getEffectiveEndDate()));
        //dssm.setEffectiveStartDate(this.getEffectiveStartDate());
        //dssm.setEffectiveEndDate(this.getEffectiveEndDate());
        dssm.setId(this.getId());
        dssm.setCreateUser(this.getCreateUser() == null ? "" : this.getCreateUser());
        dssm.setModifyUser(this.getModifyUser()==null ?  "" : this.getModifyUser());
        dssm.setName(this.getName() ==null ? "" : this.getName());
        dssm.setProduct(this.getProduct() ==null ? "" : this.getProduct());
        dssm.setRemark(this.getRemark() ==null ? "" : this.getRemark());
        dssm.setCreateTime(this.getCreateTime()==null ? "" : dateFormat.format(this.getCreateTime()));
        dssm.setModifyTime(this.getModifyTime()==null ? "" : dateFormat.format(this.getModifyTime()));

        List<Case> caseList = caseService.findByDssId(this.getId());
        dssm.setCaseCount(caseList.size());
        
        if(withChild){
            List<DataSetStationItemMsg> itemList = new ArrayList<DataSetStationItemMsg>();
            for(DataSetStationItem dssi : this.getDataSetStationItem()){
                DataSetStationItemMsg item = new DataSetStationItemMsg();
                item.setStation(dssi.getStation());
                item.setItem(dssi.getItem());
                itemList.add(item);
            }
            Collections.sort(itemList, new Comparator<DataSetStationItemMsg>(){
                public int compare( DataSetStationItemMsg l1, DataSetStationItemMsg l2 ){
                    return (l1.getStation()+"_"+l1.getItem()).toLowerCase().compareTo((l2.getStation()+"_"+l2.getItem()).toLowerCase());
                }
            });
            dssm.setDataSetStationItem(itemList);
            List<DataSetPartMsg> componentLst = new ArrayList<DataSetPartMsg>();
            for(DataSetPart dsp : this.getDataSetPart()){
                DataSetPartMsg item = new DataSetPartMsg();
                item.setComponent(dsp.getComponent());
                item.setPartType(dsp.getPartType());
                componentLst.add(item);
            }
            Collections.sort(componentLst, new Comparator<DataSetPartMsg>(){
                public int compare( DataSetPartMsg l1, DataSetPartMsg l2 ){
                    return (l1.getPartType()+"_"+l1.getComponent()).toLowerCase().compareTo((l2.getPartType()+"_"+l2.getComponent()).toLowerCase());
                }
            });
            dssm.setDataSetPart(componentLst);
        }


        return dssm;
    }
    public DataSetInfoMsg toDSIMsg(boolean withChild){
        DataSetInfoMsg dssm = new DataSetInfoMsg();

        dssm.setDsid(this.getId());
        dssm.setDataset(this.getName() ==null ? "" : this.getName());
        dssm.setProduct(this.getProduct() ==null ? "" : this.getProduct());

        return dssm;
    }
}
