package com.foxconn.iisd.rcadsvc.service.Impl;


import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foxconn.iisd.rcadsvc.domain.Case;
import com.foxconn.iisd.rcadsvc.domain.CasePlot;
import com.foxconn.iisd.rcadsvc.domain.CasePlotNote;
import com.foxconn.iisd.rcadsvc.domain.CodeTable;
import com.foxconn.iisd.rcadsvc.domain.auth.User;
import com.foxconn.iisd.rcadsvc.repo.CodeTableRepository;
import com.foxconn.iisd.rcadsvc.service.CodeService;

@Service
@Transactional
public class CodeServiceImpl implements CodeService {

    @Autowired
    private CodeTableRepository codeTableRepository;
    
    @Override
    public List<CodeTable> findByCodeNameContaining(String codeName) {
        return codeTableRepository.findByCodeNameContaining(codeName);
    }

    @Override
    public List<CodeTable> findByCodeProductAndCodeCategory(String codeProduct, String codeCategory) {
        return codeTableRepository.findByCodeProductAndCodeCategory(codeProduct,codeCategory);
    }
    @Override
    public CodeTable findByCodeName(String codeName) {
        return codeTableRepository.findByCodeName(codeName);
    }

    @Override
    public CodeTable createCode(User createUser,CodeTable codeInfo){
    	codeInfo.setCreateTime(new Date());
    	codeInfo.setCreateUser(createUser.getUsername());
    	codeTableRepository.save(codeInfo);

        return codeInfo;
    }

    @Override
    public CodeTable updateCode(User updateUser,CodeTable codeInfo,Long id){
    	CodeTable dbcode = codeTableRepository.findById(id).get();

        BeanUtils.copyProperties(codeInfo, dbcode, getNullPropertyNames(codeInfo, "id, createUser, createTime, modifyUser, modifyTime"));
        dbcode.setModifyTime(new Date());
        dbcode.setModifyUser(updateUser.getUsername());
        CodeTable updatecode = codeTableRepository.save(dbcode);
        return updatecode;
    }

    @Override
    public void deleteCode(User deleteUser,CodeTable codeInfo){
    	codeTableRepository.delete(codeInfo);
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