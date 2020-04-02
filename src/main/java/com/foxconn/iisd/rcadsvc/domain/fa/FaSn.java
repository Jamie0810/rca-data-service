package com.foxconn.iisd.rcadsvc.domain.fa;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
@Table(name = "fa_sn",
        uniqueConstraints = @UniqueConstraint(columnNames = {"fa_case_id", "sn", "factory", "product", "floor",
                "line", "station", "failure_sympton", "start_time"}))
public class FaSn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = null;

    @Column(nullable = false)
    private Long testing_id;

    @Column(nullable = false)
    private String sn;

    @JsonIgnoreProperties
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "fa_case_id", referencedColumnName = "id")
    private FaCase faCase;

    @Column(nullable = false)
    private String factory;

    @Column(nullable = false)
    private String product;

    @Column(nullable = false)
    private String floor;

    @Column(nullable = false)
    private String line;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime testStartTime;

    @Column(nullable = false)
    private String station;

    @Column(nullable = false)
    private String machine;

    @Column(name = "failure_sympton", nullable = false)
    private String failureSymptom;

    @Column(name = "failure_desc")
    private String failureDesc;

    public void clearId() {
        id = null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public FaCase getFaCase() {
        return faCase;
    }

    public void setFaCase(FaCase faCase) {
        this.faCase = faCase;
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

    public String getFailureSymptom() {
        return failureSymptom;
    }

    public void setFailureSymptom(String failureSympton) {
        this.failureSymptom = failureSympton;
    }

    public String getFailureDesc() {
        return failureDesc;
    }

    public void setFailureDesc(String failureDesc) {
        this.failureDesc = failureDesc;
    }

    public Long getTesting_id() { return testing_id; }

    public void setTesting_id(Long testing_id) { this.testing_id = testing_id; }
}
