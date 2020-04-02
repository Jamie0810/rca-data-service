package com.foxconn.iisd.rcadsvc.domain.fa;

import com.foxconn.iisd.rcadsvc.domain.auth.User;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "fa_case")
public class FaCase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String product;

    @Column(nullable = false)
    private String line;

    @Column(nullable = false)
    private String station;

    @OneToOne
    @JoinColumn(name = "sympton_id", referencedColumnName = "id", nullable = false)
    private Symptom symptom;

    @Column
    private String customer;

    @Column(name = "failure_desc")
    private String failureDesc;

    @Column(name = "test_starttime", nullable = false)
    private LocalDateTime testStartTime;

    @Column(name = "analyze_finishtime")
    private LocalDateTime analyzeFinishTime;

    @Column(name = "root_cause", nullable = false)
    private String rootCause;

    @Column(name = "risk_type", nullable = false)
    private String riskType;

    @Column(name = "analyze_desc", columnDefinition = "text")
    private String analyzeDesc;

    @Column(name = "root_cause_desc", columnDefinition = "text")
    private String rootCauseDesc;

    @Column(columnDefinition = "text")
    private String plan;

    @Column(columnDefinition = "text")
    private String action;

    @OneToOne
    @JoinColumn(name = "create_user_id", referencedColumnName = "id", nullable = false)
    private User createUser;

    @CreationTimestamp
    @Column(name = "createtime", nullable = false)
    private LocalDateTime createTime;

    @OneToOne
    @JoinColumn(name = "update_user_id", referencedColumnName = "id", nullable = false)
    private User updateUser;

    @UpdateTimestamp
    @Column(name = "updatetime", nullable = false)
    private LocalDateTime updateTime;

    public void clearId() {
        id = null;
    }

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

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public Symptom getSymptom() {
        return symptom;
    }

    public void setSymptom(Symptom symptom) {
        this.symptom = symptom;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getFailureDesc() {
        return failureDesc;
    }

    public void setFailureDesc(String failureDesc) {
        this.failureDesc = failureDesc;
    }

    public LocalDateTime getTestStartTime() {
        return testStartTime;
    }

    public void setTestStartTime(LocalDateTime testStartTime) {
        this.testStartTime = testStartTime;
    }

    public LocalDateTime getAnalyzeFinishTime() {
        return analyzeFinishTime;
    }

    public void setAnalyzeFinishTime(LocalDateTime analyzeFinishTime) {
        this.analyzeFinishTime = analyzeFinishTime;
    }

    public String getRootCause() {
        return rootCause;
    }

    public void setRootCause(String rootCause) {
        this.rootCause = rootCause;
    }

    public String getRiskType() {
        return riskType;
    }

    public void setRiskType(String riskType) {
        this.riskType = riskType;
    }

    public String getAnalyzeDesc() {
        return analyzeDesc;
    }

    public void setAnalyzeDesc(String analyzeDesc) {
        this.analyzeDesc = analyzeDesc;
    }

    public String getRootCauseDesc() {
        return rootCauseDesc;
    }

    public void setRootCauseDesc(String rootCauseDesc) {
        this.rootCauseDesc = rootCauseDesc;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public User getCreateUser() {
        return createUser;
    }

    public void setCreateUser(User createUser) {
        this.createUser = createUser;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public User getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(User updateUser) {
        this.updateUser = updateUser;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
