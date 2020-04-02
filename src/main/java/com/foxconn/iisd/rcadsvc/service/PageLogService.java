package com.foxconn.iisd.rcadsvc.service;

import com.foxconn.iisd.rcadsvc.domain.Log;
import com.foxconn.iisd.rcadsvc.domain.PageLog;
import com.foxconn.iisd.rcadsvc.domain.auth.User;

public interface PageLogService {

    PageLog createLog(User createUser, PageLog logInfo);
}
