package com.foxconn.iisd.rcadsvc.service;

import com.foxconn.iisd.rcadsvc.domain.ProductBwList;
import com.foxconn.iisd.rcadsvc.domain.auth.User;

public interface ProductBwListService {

    ProductBwList findByProductAndListType(String product,String listType);

}
