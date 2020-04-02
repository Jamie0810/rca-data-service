package com.foxconn.iisd.rcadsvc.service.Impl;

import com.foxconn.iisd.rcadsvc.domain.ProductBwList;
import com.foxconn.iisd.rcadsvc.repo.ProductBwListRepository;
import com.foxconn.iisd.rcadsvc.service.ProductBwListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;

@Service
@Transactional
public class ProductBwListServiceImpl implements ProductBwListService {

    @Autowired
    private ProductBwListRepository productBwListRepository;


    @Override
    public ProductBwList findByProductAndListType(String product,String listType) {
        return productBwListRepository.findByProductAndListType(product,listType);
    }


}