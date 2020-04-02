package com.foxconn.iisd.rcadsvc.service.Impl;

import com.foxconn.iisd.rcadsvc.domain.ProductInfo;
import com.foxconn.iisd.rcadsvc.domain.ProductVerifyResult;
import com.foxconn.iisd.rcadsvc.domain.auth.User;
import com.foxconn.iisd.rcadsvc.repo.ProductVerifyResultRepository;
import com.foxconn.iisd.rcadsvc.service.ProductInfoService;
import com.foxconn.iisd.rcadsvc.service.ProductVerifyResultService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class ProductVerifyResultServiceImpl implements ProductVerifyResultService {

    @Autowired
    private ProductVerifyResultRepository productVerifyResultRepository;


    @Override
    public List<ProductVerifyResult> findByProductAndVerifyTypeOrderByVerifyDate(String product, String verifyType) {
        return productVerifyResultRepository.findByProductAndVerifyTypeOrderByVerifyDate(product,verifyType);
    }
}