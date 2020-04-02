package com.foxconn.iisd.rcadsvc.service;

import com.foxconn.iisd.rcadsvc.domain.auth.Permission;

import java.util.List;

public interface PermissionService {
    List<Permission> findAll();
}
