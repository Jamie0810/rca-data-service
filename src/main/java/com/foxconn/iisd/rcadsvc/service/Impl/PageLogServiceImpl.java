package com.foxconn.iisd.rcadsvc.service.Impl;

import com.foxconn.iisd.rcadsvc.domain.Log;
import com.foxconn.iisd.rcadsvc.domain.PageLog;
import com.foxconn.iisd.rcadsvc.domain.auth.User;
import com.foxconn.iisd.rcadsvc.repo.LogRepository;
import com.foxconn.iisd.rcadsvc.repo.PageLogRepository;
import com.foxconn.iisd.rcadsvc.service.LogService;
import com.foxconn.iisd.rcadsvc.service.PageLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;

@Service
@Transactional
public class PageLogServiceImpl implements PageLogService {

    @Autowired
    private PageLogRepository pageLogRepository;

    @Override
    public PageLog createLog(User createUser, PageLog pageLogInfo)
    {
        pageLogInfo.setCreateTime(new Date());
        pageLogInfo.setCreateUser(createUser.getUsername());
        pageLogRepository.save(pageLogInfo);

        return pageLogInfo;
    }
}