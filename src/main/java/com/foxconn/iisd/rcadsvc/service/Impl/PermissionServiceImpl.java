package com.foxconn.iisd.rcadsvc.service.Impl;

import com.foxconn.iisd.rcadsvc.service.PermissionService;
import com.foxconn.iisd.rcadsvc.domain.auth.Permission;
import com.foxconn.iisd.rcadsvc.repo.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service(value = "permissionService")
@Transactional
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public List<Permission> findAll() {
        return permissionRepository.findAll();
    }
}
