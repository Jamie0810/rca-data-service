package com.foxconn.iisd.rcadsvc.domain;

import javax.persistence.*;
import java.util.Date;

/*
 *
 * @author Kenny
 * @date 2019/6/5 上午10:16
 */
@Entity
@Table(name="data_set_part")
public class DataSetPart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dss_id")
    private Long dssId;

    @Column(name = "part_type")
    private String partType;

    private String component;

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

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
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

    public String getPartType() {
        return partType;
    }

    public void setPartType(String partType) {
        this.partType = partType;
    }
}
