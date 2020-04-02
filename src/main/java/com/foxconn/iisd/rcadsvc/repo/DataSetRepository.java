package com.foxconn.iisd.rcadsvc.repo;

import com.foxconn.iisd.rcadsvc.domain.DataSetSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import java.util.List;

@Repository(value = "dataSetRepository")
public interface DataSetRepository extends JpaRepository<DataSetSetting, Long> {

    List<DataSetSetting> findByNameContaining(String name);
    List<DataSetSetting> findByProduct(String product);
    DataSetSetting findByName(String name);

    Page<DataSetSetting> findByProduct(String product,Pageable pageable);
    Page<DataSetSetting> findAll(Pageable pageable);

    Page<DataSetSetting> findByProductContainingOrNameContainingOrRemarkContainingOrCreateUserContaining(String product,String name,String remark,String createUser,Pageable pageable);
    List<DataSetSetting> findByProductContainingOrNameContainingOrRemarkContainingOrCreateUserContaining(String product,String name,String remark,String createUser);
}