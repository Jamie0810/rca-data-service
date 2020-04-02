package com.foxconn.iisd.rcadsvc.service;

import com.foxconn.iisd.rcadsvc.domain.ProductVerifyResult;
import com.foxconn.iisd.rcadsvc.domain.auth.User;

import java.util.List;

public interface ProductVerifyResultService {

    List<ProductVerifyResult> findByProductAndVerifyTypeOrderByVerifyDate(String product, String verifyType);
}
