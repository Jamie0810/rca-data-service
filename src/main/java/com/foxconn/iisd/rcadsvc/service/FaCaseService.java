package com.foxconn.iisd.rcadsvc.service;

import com.foxconn.iisd.rcadsvc.domain.auth.User;
import com.foxconn.iisd.rcadsvc.domain.fa.FaCase;
import com.foxconn.iisd.rcadsvc.domain.fa.FaFile;
import com.foxconn.iisd.rcadsvc.domain.fa.FaSn;
import com.foxconn.iisd.rcadsvc.msg.RiskType;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public interface FaCaseService {
    FaCase create(User createUser, FaCase faCase) throws Exception;

    FaCase update(User updateUser, FaCase faCase) throws Exception;

    List<FaCase> queryFaCases(String product, String riskType, LocalDateTime startTime, LocalDateTime stopTime, String createUser, String failSymptom);

    List<FaCase> queryFaCaseById(Long id);
}
