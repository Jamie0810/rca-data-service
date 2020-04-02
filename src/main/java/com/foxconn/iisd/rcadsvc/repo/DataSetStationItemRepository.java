package com.foxconn.iisd.rcadsvc.repo;

import com.foxconn.iisd.rcadsvc.domain.DataSetStationItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository(value = "DataSetStationItemRepository")
public interface DataSetStationItemRepository extends JpaRepository<DataSetStationItem, Long> {

}
