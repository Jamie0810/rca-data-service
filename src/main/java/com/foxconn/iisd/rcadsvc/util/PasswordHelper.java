package com.foxconn.iisd.rcadsvc.util;

import com.foxconn.iisd.rcadsvc.domain.auth.User;
import org.apache.shiro.authc.credential.DefaultPasswordService;

public class PasswordHelper {
    static public User encryptPassword(User user) {
        DefaultPasswordService dps = new DefaultPasswordService();
        user.setPassword(dps.encryptPassword(user.getPassword()));
        return user;
//        user.setPassword(dps.encryptPassword(user.getPassword()));
    }
}
