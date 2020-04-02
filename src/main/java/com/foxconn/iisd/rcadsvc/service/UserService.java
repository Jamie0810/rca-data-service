package com.foxconn.iisd.rcadsvc.service;

import com.foxconn.iisd.rcadsvc.domain.auth.User;

public interface UserService {

    User findByUsername(String username);

    User updateUser(User updateUser, User userInfo);
}
