package com.foxconn.iisd.rcadsvc.service;

import com.foxconn.iisd.rcadsvc.domain.ProductInfo;
import com.foxconn.iisd.rcadsvc.domain.auth.User;

public interface ProductInfoService {

    ProductInfo createProductInfo(User createUser, ProductInfo productInfo);
    ProductInfo findByProduct(String product);
    ProductInfo updateProduct(User updateUser,ProductInfo productInfo,Long id);
    ProductInfo updateProductLogic(User updateUser,ProductInfo productInfo,Long id);
}
