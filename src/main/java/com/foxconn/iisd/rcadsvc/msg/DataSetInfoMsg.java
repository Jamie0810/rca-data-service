package com.foxconn.iisd.rcadsvc.msg;

import com.foxconn.iisd.rcadsvc.domain.DataSetPart;
import com.foxconn.iisd.rcadsvc.domain.DataSetSetting;
import com.foxconn.iisd.rcadsvc.domain.DataSetStationItem;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DataSetInfoMsg {

    private Long dsid;

    private String dataset;

    private String product;



    private List<DataSetStationItemMsg> dataSetStationItem;

    public Long getDsid() {
        return dsid;
    }

    public void setDsid(Long dsid) {
        this.dsid = dsid;
    }

    public List<DataSetStationItemMsg> getDataSetStationItem() {
        return dataSetStationItem;
    }

    public void setDataSetStationItem(List<DataSetStationItemMsg> dataSetStationItem) {
        this.dataSetStationItem = dataSetStationItem;
    }

    public String getDataset() {
        return dataset;
    }

    public void setDataset(String dataset) {
        this.dataset = dataset;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;

    }




}
