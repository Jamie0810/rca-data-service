package com.foxconn.iisd.rcadsvc.msg;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.foxconn.iisd.rcadsvc.domain.fa.FaCase;
import com.foxconn.iisd.rcadsvc.domain.fa.FaSn;
import com.foxconn.iisd.rcadsvc.domain.fa.Symptom;
import org.springframework.beans.BeanUtils;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class FaCaseMsg extends FaCase {
    private Long id;

    private String product;

    private String line;

    private String station;

    private Symptom symptom;

    private String customer;

    private String failureDesc;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime testStartTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime analyzeFinishTime;

    private String rootCause;

    private RiskType riskType;

    private String analyzeDesc;

    private String rootCauseDesc;

    private String plan;

    private String action;

    private List<FaSnMsg> faSnMsgList;

    private List<MultipartFile> files;

    public List<MultipartFile> getFiles() {
        return files;
    }

    public void setFiles(List<MultipartFile> files) {
        this.files = files;
    }

    public List<FaSnMsg> getFaSnMsgList() {
        return faSnMsgList;
    }

    public void setFaSnMsgList(List<FaSnMsg> faSnMsgList) {
        this.faSnMsgList = faSnMsgList;
    }

    public void clearId() {
        setId(null);
    }

    public List<FaSn> generateFaSnList() throws InvocationTargetException, IllegalAccessException {
        List<FaSn> faSnList = new ArrayList<FaSn>();
        for (FaSnMsg faSnMsg : faSnMsgList) {
            FaSn faSn = new FaSn();
            BeanUtils.copyProperties(faSnMsg, faSn);
            faSnList.add(faSn);
        }
        return faSnList;
    }
}

