package com.foxconn.iisd.rcadsvc.msg;

import com.foxconn.iisd.rcadsvc.domain.fa.*;
import com.foxconn.iisd.rcadsvc.util.FormatUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


public class NewFaCaseMsg {
    private Long id;

    //產品
    private String product;

    //線別
    private String line;

    //測試工站
    private String station;

    private SymptomType symptomType;

    //分析徵狀
    private String symptomName;

    //客戶
    private String customer;

    //徵狀描述
    private String failureDesc;

    //徵狀發生時間
    private String testStartTimeStr;

    //分析完成時間
    private String analyzeFinishTimeStr;

    //根因分析
    private String rootCause;

    //不良因子分類
    private String riskType;

    //分析過程說明
    private String analyzeDesc;

    //關鍵根因說明
    private String rootCauseDesc;

    //長短期方案
    private String plan;

    //最後行動
    private String action;

    private String createUsername;

    private String createTimeStr;

    private String updateUsername;

    private String updateTimeStr;

    private List<Long> snDel = new ArrayList<>();

    private List<Long> snTestingId;

    private List<String> snNumber;

    private List<String> snTestStartTime;

    private List<String> snFactory;

    private List<String> snProduct;

    private List<String> snFloor;

    private List<String> snLine;

    private List<String> snStation;

    private List<String> snMachine;

    private List<String> snFailureSymptom;

    private List<String> snFailureDesc;

//    private List<ActionType> snAction;

    private List<Long> refFileDel = new ArrayList<>();

    private List<MultipartFile> refFiles;

    private List<String> refFileNotes;

    public void clearId() {
        setId(null);
    }

    public List<FaSn> generateFaSnList() throws InvocationTargetException, IllegalAccessException {
//        Map<ActionType, List<FaSn>> snMap = new HashMap<>();

        List<FaSn> faSnCreateList = new ArrayList<>();
//        List<FaSn> faSnDeleteList = new ArrayList<FaSn>();
//        snMap.put(ActionType.CREATE, faSnCreateList);
//        snMap.put(ActionType.DELETE, faSnDeleteList);
//System.out.println("snFailureDesc: " + snFailureDesc.size());
        if (snNumber != null) {
            if (this.snNumber.size() != this.snFailureDesc.size() && this.snNumber.size() == 1) {
                this.snFailureDesc.add("");
            }
            for (int i = 0; i < snNumber.size(); i++) {
                FaSn faSn = new FaSn();
                faSn.setTesting_id(snTestingId.get(i));
                faSn.setSn(snNumber.get(i).isEmpty()? null: snNumber.get(i));
                faSn.setFactory(snFactory.get(i).isEmpty()? null: snFactory.get(i));
                faSn.setProduct(snProduct.get(i).isEmpty()? null: snProduct.get(i));
                faSn.setFloor(snFloor.get(i).isEmpty()? null: snFloor.get(i));
                faSn.setLine(snLine.get(i).isEmpty()? null: snLine.get(i));
                faSn.setTestStartTime(snTestStartTime.get(i).isEmpty()? null:
                        FormatUtils.timeStringToLocalDateTime(snTestStartTime.get(i)));
                faSn.setStation(snStation.get(i).isEmpty()? null: snStation.get(i));
                faSn.setMachine(snMachine.get(i).isEmpty()? null: snMachine.get(i));
                faSn.setFailureSymptom(snFailureSymptom.get(i).isEmpty()? null: snFailureSymptom.get(i));
                faSn.setFailureDesc(snFailureDesc.get(i).isEmpty()? null: snFailureDesc.get(i));

                faSnCreateList.add(faSn);
//            switch (snAction.get(i)) {
//                case CREATE:
//                    faSnCreateList.add(faSn);
//                    break;
//                case DELETE:
//                    faSnDeleteList.add(faSn);
//                    break;
//                default:
//                    break;
//            }
            }
        }

        return faSnCreateList;
    }

    public List<FaFile> generateFaFileList() {
        List<FaFile> faFileCreateList = new ArrayList<>();

        if (refFiles != null) {
            if (this.refFiles.size() != this.refFileNotes.size() && this.refFiles.size() == 1) {
                this.refFileNotes.add("");
            }
            for (int i = 0; i < refFiles.size(); i++) {
                MultipartFile multipartFile = refFiles.get(i);

                FaFile faFile = new FaFile();
                faFile.setDescription(refFileNotes.get(i).isEmpty()? null: refFileNotes.get(i));
                faFile.setOriginalFileName(multipartFile.getOriginalFilename());
                faFile.setObjectName(System.currentTimeMillis() + "-" + i );
                faFile.setSizeInBytes(BigInteger.valueOf(multipartFile.getSize()));
                faFile.setFile(multipartFile);

                faFileCreateList.add(faFile);
            }
        }

        return faFileCreateList;
    }

    public FaCase toFaCase() throws InvocationTargetException, IllegalAccessException {
        FaCase faCase = new FaCase();
        BeanUtils.copyProperties(this, faCase);
        faCase.setTestStartTime(FormatUtils.timeStringToLocalDateTime(testStartTimeStr));
        if (analyzeFinishTimeStr != null) {
            faCase.setAnalyzeFinishTime(FormatUtils.timeStringToLocalDateTime(analyzeFinishTimeStr));
        }
        Symptom symptom = new Symptom();
        symptom.setType(symptomType);
        symptom.setName(symptomName);
        symptom.setLine(line);
        symptom.setProduct(product);
        symptom.setStation(station);
        faCase.setSymptom(symptom);

        return faCase;
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

    public SymptomType getSymptomType() {
        return symptomType;
    }

    public void setSymptomType(SymptomType symptomType) {
        this.symptomType = symptomType;
    }

    public String getSymptomName() {
        return symptomName;
    }

    public void setSymptomName(String symptonName) {
        this.symptomName = symptonName;
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

    public String getTestStartTimeStr() {
        return testStartTimeStr;
    }

    public void setTestStartTimeStr(String testStartTimeStr) {
        this.testStartTimeStr = testStartTimeStr;
    }

    public String getAnalyzeFinishTimeStr() {
        return analyzeFinishTimeStr;
    }

    public void setAnalyzeFinishTimeStr(String analyzeFinishTimeStr) {
        this.analyzeFinishTimeStr = analyzeFinishTimeStr;
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

    public List<Long> getSnTestingId() {
        return snTestingId;
    }

    public void setSnTestingId(List<Long> snTestingId) {
        this.snTestingId = snTestingId;
    }

    public List<String> getSnNumber() {
        return snNumber;
    }

    public void setSnNumber(List<String> snNumber) {
        this.snNumber = snNumber;
    }

    public List<String> getSnTestStartTime() {
        return snTestStartTime;
    }

    public void setSnTestStartTime(List<String> snTestStartTime) {
        this.snTestStartTime = snTestStartTime;
    }

    public List<String> getSnFactory() {
        return snFactory;
    }

    public List<String> getSnProduct() {
        return snProduct;
    }

    public void setSnProduct(List<String> snProduct) {
        this.snProduct = snProduct;
    }

    public List<String> getSnLine() {
        return snLine;
    }

    public void setSnLine(List<String> snLine) {
        this.snLine = snLine;
    }

    public void setSnFactory(List<String> snFactory) {
        this.snFactory = snFactory;
    }

    public List<String> getSnFloor() {
        return snFloor;
    }

    public void setSnFloor(List<String> snFloor) {
        this.snFloor = snFloor;
    }

    public List<String> getSnStation() {
        return snStation;
    }

    public void setSnStation(List<String> snStation) {
        this.snStation = snStation;
    }

    public List<String> getSnMachine() {
        return snMachine;
    }

    public void setSnMachine(List<String> snMachine) {
        this.snMachine = snMachine;
    }

    public List<String> getSnFailureSymptom() {
        return snFailureSymptom;
    }

    public void setSnFailureSymptom(List<String> snFailureSympton) {
        this.snFailureSymptom = snFailureSympton;
    }

    public List<String> getSnFailureDesc() {
        return snFailureDesc;
    }

    public void setSnFailureDesc(List<String> snFailureDesc) {
        this.snFailureDesc = snFailureDesc;
    }

//    public List<ActionType> getSnAction() {
//        return snAction;
//    }
//
//    public void setSnAction(List<ActionType> snAction) {
//        this.snAction = snAction;
//    }

    public List<MultipartFile> getRefFiles() { return refFiles; }

    public void setRefFiles(List<MultipartFile> refFiles) { this.refFiles = refFiles; }

    public List<String> getRefFileNotes() {
        return refFileNotes;
    }

    public void setRefFileNotes(List<String> refFileNotes) {
        this.refFileNotes = refFileNotes;
    }

    public String getCreateTimeStr() { return createTimeStr; }

    public void setCreateTimeStr(String createTimeStr) { this.createTimeStr = createTimeStr; }

    public String getUpdateTimeStr() { return updateTimeStr; }

    public void setUpdateTimeStr(String updateTimeStr) { this.updateTimeStr = updateTimeStr; }

    public String getCreateUsername() { return createUsername; }

    public void setCreateUsername(String createUsername) { this.createUsername = createUsername; }

    public String getUpdateUsername() { return updateUsername; }

    public void setUpdateUsername(String updateUsername) { this.updateUsername = updateUsername; }

    public List<Long> getSnDel() {
        return snDel;
    }

    public void setSnDel(List<Long> snDel) {
        this.snDel = snDel;
    }

    public List<Long> getRefFileDel() {
        return refFileDel;
    }

    public void setRefFileDel(List<Long> refFileDel) {
        this.refFileDel = refFileDel;
    }
}

