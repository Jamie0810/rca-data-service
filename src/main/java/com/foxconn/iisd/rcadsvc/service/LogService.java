package com.foxconn.iisd.rcadsvc.service;

import com.foxconn.iisd.rcadsvc.domain.Log;
import com.foxconn.iisd.rcadsvc.domain.auth.User;

import java.util.List;

public interface LogService {

    Log createLog(User createUser, Log logInfo);
}
