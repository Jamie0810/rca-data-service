package com.foxconn.iisd.rcadsvc.service;

import java.util.List;

import com.foxconn.iisd.rcadsvc.domain.CodeTable;
import com.foxconn.iisd.rcadsvc.domain.auth.User;

public interface CodeService {

    List<CodeTable> findByCodeNameContaining(String codeName);
    CodeTable findByCodeName(String codeName);
    List<CodeTable> findByCodeProductAndCodeCategory(String codeProduct, String codeCategory);
    void deleteCode(User deleteUser,CodeTable codeInfo);
    CodeTable createCode(User createUser,CodeTable codeInfo);
    CodeTable updateCode(User updateUser,CodeTable codeInfo,Long id);

}