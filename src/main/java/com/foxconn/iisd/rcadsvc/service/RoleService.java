package com.foxconn.iisd.rcadsvc.service;

import com.foxconn.iisd.rcadsvc.domain.Case;
import com.foxconn.iisd.rcadsvc.domain.auth.Role;
import com.foxconn.iisd.rcadsvc.domain.auth.User;

import java.util.List;
import java.util.Optional;

public interface RoleService {

    List<Role> findAll();

    Optional<Role> findById(long id);

    Role createRole(User createUser, Role roleInfo);
    Role updateRole(User updateUser, Role roleInfo);
}
