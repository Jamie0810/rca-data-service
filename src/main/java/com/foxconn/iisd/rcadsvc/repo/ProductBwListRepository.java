package com.foxconn.iisd.rcadsvc.repo;

import com.foxconn.iisd.rcadsvc.domain.ProductBwList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository(value = "productBwListRepository")
public interface ProductBwListRepository extends JpaRepository<ProductBwList, Long> {
    ProductBwList findByProductAndListType(String product,String listType);
}
