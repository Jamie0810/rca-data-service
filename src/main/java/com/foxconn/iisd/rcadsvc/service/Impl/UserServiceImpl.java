package com.foxconn.iisd.rcadsvc.service.Impl;

import com.foxconn.iisd.rcadsvc.domain.auth.Role;
import com.foxconn.iisd.rcadsvc.service.UserService;
import com.foxconn.iisd.rcadsvc.domain.auth.User;
import com.foxconn.iisd.rcadsvc.repo.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User updateUser(User updateUser, User userInfo){
        User dbUser = userRepository.findByUsername(userInfo.getUsername());
        BeanUtils.copyProperties(userInfo, dbUser, getNullPropertyNames(userInfo, "id,password,account,code,createUser,createTime,modifyUser,modifyTime,enable"));
        System.out.println("Source:"+dbUser.getEnable()+",UP:"+userInfo.getEnable());
        if(userInfo.getEnable().equals("0") && dbUser.getEnable().equals("1"))
            dbUser.setDisableModifyTime(new Date());
        else if(userInfo.getEnable().equals("1") && dbUser.getEnable().equals("0"))
            dbUser.setDisableModifyTime(null);
        dbUser.setEnable(userInfo.getEnable());
        dbUser.setModifyTime(new Date());
        dbUser.setModifyUser(updateUser.getUsername());
        User updateU = userRepository.save(dbUser);
        return updateU;
    }
    public static String[] getNullPropertyNames (Object source, String ignoreCols) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for(java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        String[] ignoreColArray = ignoreCols.split(",");
        for(String colName : ignoreColArray){
            emptyNames.add(colName);
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }
}
