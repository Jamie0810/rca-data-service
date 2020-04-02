package com.foxconn.iisd.rcadsvc.service.Impl;

import com.foxconn.iisd.rcadsvc.domain.auth.User;
import com.foxconn.iisd.rcadsvc.service.RoleService;
import com.foxconn.iisd.rcadsvc.domain.auth.Role;
import com.foxconn.iisd.rcadsvc.repo.RoleRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service(value = "roleService")
@Transactional
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    @Override
    public Optional<Role> findById(long id) {
        return roleRepository.findById(id);
    }

    @Override
    public Role createRole(User createUser, Role roleInfo){
        roleInfo.setCreateTime(new Date());
        roleInfo.setCreateUser(createUser.getUsername());
        roleRepository.save(roleInfo);
        return roleInfo;
    }

    @Override
    public Role updateRole(User updateUser, Role roleInfo){
        Role dbrole = roleRepository.findById(roleInfo.getId()).get();
        BeanUtils.copyProperties(roleInfo, dbrole, getNullPropertyNames(roleInfo, "id, code, createUser, createTime, modifyUser, modifyTime"));
        dbrole.setModifyTime(new Date());
        dbrole.setModifyUser(updateUser.getUsername());
        Role updateRole = roleRepository.save(dbrole);
        return updateRole;


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
