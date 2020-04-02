package com.foxconn.iisd.rcadsvc.repo;

import com.foxconn.iisd.rcadsvc.domain.Log;
import com.foxconn.iisd.rcadsvc.domain.PageLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository(value = "PageLogRepository")
public interface PageLogRepository extends JpaRepository<PageLog, Long> {

}
