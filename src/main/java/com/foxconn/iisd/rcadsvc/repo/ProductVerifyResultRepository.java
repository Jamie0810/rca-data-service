package com.foxconn.iisd.rcadsvc.repo;

import com.foxconn.iisd.rcadsvc.domain.ProductVerifyResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository(value = "productVerifyResultRepository")
public interface ProductVerifyResultRepository extends JpaRepository<ProductVerifyResult, Long> {
    List<ProductVerifyResult> findByProductAndVerifyTypeOrderByVerifyDate(String product, String verifyType);
}
