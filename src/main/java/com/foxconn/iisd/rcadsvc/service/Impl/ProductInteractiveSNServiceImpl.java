package com.foxconn.iisd.rcadsvc.service.Impl;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foxconn.iisd.rcadsvc.domain.ProductInteractiveSN;
import com.foxconn.iisd.rcadsvc.domain.auth.User;
import com.foxconn.iisd.rcadsvc.repo.ProductInteractiveSNRepository;
import com.foxconn.iisd.rcadsvc.service.ProductInteractiveSNService;

@Service
@Transactional
public class ProductInteractiveSNServiceImpl implements ProductInteractiveSNService {

	@PersistenceContext
	private EntityManager entityManager;

//	@Value("${hibernate.jdbc.batch_size}")
	private int batchSize = 100;
	
    @Autowired
    private ProductInteractiveSNRepository productInteractiveSNRepository;
    
    @Override
    public List<ProductInteractiveSN> findAll() {
        return productInteractiveSNRepository.findAll();
    }
    
    @Override
    public List<ProductInteractiveSN> findByProduct(String product) {
        return productInteractiveSNRepository.findByProduct(product);
    }

    @Override
    public List<ProductInteractiveSN> findByProductAndSn(String product, String sn){
    	return productInteractiveSNRepository.findByProductAndSn(product, sn);
    }
    
    @Override
    public <T extends ProductInteractiveSN> List<T> createProductInteractiveSN(User createUser, List<T> infoList){
    	final List<T> savedEntities = new ArrayList<T>(infoList.size());

    	int i = 0;
		for (T t : infoList) {
			t.setCreateTime(new Date());
	    	t.setCreateUser(createUser.getUsername());
	    	savedEntities.add(persistOrMerge(t));
	    	i++;
    	    if (i % batchSize == 0) {
    	      entityManager.flush();
    	      entityManager.clear();
    	    }
		}

		return savedEntities;
    }

    private <T extends ProductInteractiveSN> T persistOrMerge(T t) {
    	if (t.getId() == null) {
    	    entityManager.persist(t);
    	    return t;
    	} else {
    	    return entityManager.merge(t);
    	}
    }
    
    @Override
    public ProductInteractiveSN updateProductInteractiveSN(User updateUser,ProductInteractiveSN info,Long id){
    	ProductInteractiveSN dbcode = productInteractiveSNRepository.findById(id).get();

        BeanUtils.copyProperties(info, dbcode, getNullPropertyNames(info, "id, createUser, createTime, modifyUser, modifyTime"));
        dbcode.setModifyTime(new Date());
        dbcode.setModifyUser(updateUser.getUsername());
        ProductInteractiveSN updatecode = productInteractiveSNRepository.save(dbcode);
        return updatecode;
    }

    @Override
    public void deleteProductInteractiveSN(User deleteUser, ProductInteractiveSN info){
    	productInteractiveSNRepository.delete(info);
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