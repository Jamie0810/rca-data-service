package com.foxconn.iisd.rcadsvc.msg;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class FaSnMsg {
    private Integer id;

    private String sn;

    private String factory;

    private String product;

    private String floor;

    private String line;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime testStartTime;

    private String station;

    private String machine;

    private String failureSympton;

    private String failureDesc;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getFactory() {
        return factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public LocalDateTime getTestStartTime() {
        return testStartTime;
    }

    public void setTestStartTime(LocalDateTime testStartTime) {
        this.testStartTime = testStartTime;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public String getMachine() {
        return machine;
    }

    public void setMachine(String machine) {
        this.machine = machine;
    }

    public String getFailureSympton() {
        return failureSympton;
    }

    public void setFailureSympton(String failureSympton) {
        this.failureSympton = failureSympton;
    }

    public String getFailureDesc() {
        return failureDesc;
    }

    public void setFailureDesc(String failureDesc) {
        this.failureDesc = failureDesc;
    }
}
