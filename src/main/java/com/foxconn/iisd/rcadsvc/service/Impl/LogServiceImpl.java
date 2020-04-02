package com.foxconn.iisd.rcadsvc.service.Impl;

import com.foxconn.iisd.rcadsvc.domain.Log;
import com.foxconn.iisd.rcadsvc.domain.auth.User;
import com.foxconn.iisd.rcadsvc.repo.LogRepository;
import com.foxconn.iisd.rcadsvc.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class LogServiceImpl implements LogService {

    @Autowired
    private LogRepository logRepository;

    @Override
    public Log createLog(User createUser, Log logInfo)
    {
        logInfo.setCreateTime(new Date());
        logInfo.setCreateUser(createUser.getUsername());
        logRepository.save(logInfo);

        return logInfo;
    }
}