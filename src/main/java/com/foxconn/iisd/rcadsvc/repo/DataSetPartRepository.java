package com.foxconn.iisd.rcadsvc.repo;

import com.foxconn.iisd.rcadsvc.domain.DataSetPart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository(value = "dataSetPartRepository")
public interface DataSetPartRepository extends JpaRepository<DataSetPart, Long> {

}
