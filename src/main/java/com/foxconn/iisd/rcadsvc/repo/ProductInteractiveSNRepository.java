package com.foxconn.iisd.rcadsvc.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foxconn.iisd.rcadsvc.domain.ProductInteractiveSN;

@Repository(value = "productInteractiveSNRepository")
public interface ProductInteractiveSNRepository extends JpaRepository<ProductInteractiveSN, Long> {

	List<ProductInteractiveSN> findByProduct(String product);
	List<ProductInteractiveSN> findByProductAndSn(String product, String sn);

}