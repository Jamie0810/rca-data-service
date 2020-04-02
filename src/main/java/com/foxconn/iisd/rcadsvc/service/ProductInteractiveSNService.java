package com.foxconn.iisd.rcadsvc.service;

import java.util.List;

import com.foxconn.iisd.rcadsvc.domain.ProductInteractiveSN;
import com.foxconn.iisd.rcadsvc.domain.auth.User;

public interface ProductInteractiveSNService {

	List<ProductInteractiveSN> findAll();
	List<ProductInteractiveSN> findByProduct(String product);
    List<ProductInteractiveSN> findByProductAndSn(String product, String sn);

    void deleteProductInteractiveSN(User deleteUser, ProductInteractiveSN info);
    <T extends ProductInteractiveSN> List<T> createProductInteractiveSN(User createUser, List<T> infoList);
    ProductInteractiveSN updateProductInteractiveSN(User updateUser, ProductInteractiveSN info,Long id);

}