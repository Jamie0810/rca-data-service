package com.foxconn.iisd.rcadsvc.service.Impl;

import com.foxconn.iisd.rcadsvc.domain.Case;
import com.foxconn.iisd.rcadsvc.domain.ProductInfo;
import com.foxconn.iisd.rcadsvc.domain.auth.User;
import com.foxconn.iisd.rcadsvc.repo.ProductInfoRepository;
import com.foxconn.iisd.rcadsvc.service.ProductInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class ProductInfoServiceImpl implements ProductInfoService {

    @Autowired
    private ProductInfoRepository productInfoRepository;

    @Override
    public ProductInfo createProductInfo(User createUser, ProductInfo productInfo)
    {
        productInfo.setCreateTime(new Date());
        productInfo.setCreateUser(createUser.getUsername());
        productInfoRepository.save(productInfo);

        return productInfo;
    }

    @Override
    public ProductInfo findByProduct(String product) {
        return productInfoRepository.findByProduct(product);
    }

    @Override
    public  ProductInfo updateProduct(User updateUser,ProductInfo productInfo,Long id){
        ProductInfo dbcase = productInfoRepository.findById(id).get();

        BeanUtils.copyProperties(productInfo, dbcase, getNullPropertyNames(productInfo, "id, product,trueFailRule,mdTolerateTime," +
                "taTolerateTime,uploadFreq,ftpPath,summaryFilePath," +
                "verifyStarTime,verifyEndTime,verifyStatus,minioLastestTime ,createUser, createTime, modifyUser, modifyTime"));
        dbcase.setModifyTime(new Date());
        dbcase.setModifyUser(updateUser.getUsername());
        ProductInfo updatecase = productInfoRepository.save(dbcase);
        return updatecase;
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

    @Override
    public ProductInfo updateProductLogic(User updateUser,ProductInfo productInfo,Long id){
        ProductInfo dbcase = productInfoRepository.findById(id).get();

        BeanUtils.copyProperties(productInfo, dbcase, getNullPropertyNames(productInfo, "id, product,customer" +
                ",job_start_time,job_end_time,true_fail_rule,ftpPath,summaryFilePath," +
                "verifyStarTime,verifyEndTime,verifyStatus,minioLastestTime ,createUser, createTime, modifyUser, modifyTime"));
        dbcase.setModifyTime(new Date());
        dbcase.setModifyUser(updateUser.getUsername());
        ProductInfo updatecase = productInfoRepository.save(dbcase);
        return updatecase;
    }
}