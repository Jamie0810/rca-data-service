package com.foxconn.iisd.rcadsvc.repo;

import com.foxconn.iisd.rcadsvc.domain.ProductInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository(value = "productInfoRepository")
public interface ProductInfoRepository extends JpaRepository<ProductInfo, Long> {
    ProductInfo findByProduct(String product);
}
