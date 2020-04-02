package com.foxconn.iisd.rcadsvc.domain;

import javax.persistence.*;
import com.foxconn.iisd.rcadsvc.msg.DataSetStationItemMsg;
/*
 *
 * @author Kenny
 * @date 2019/6/5 上午10:16
 */
@Entity
@Table(name="data_set_station_item")
public class DataSetStationItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dss_id")
    private Long dssId;

    private String station;

    private String item;

    private String product;

    public void clearId() {
        id = null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public Long getDssId() {
        return dssId;
    }

    public void setDssId(Long dssId) {
        this.dssId = dssId;
    }
}
