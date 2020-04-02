package com.foxconn.iisd.rcadsvc.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foxconn.iisd.rcadsvc.domain.CasePlot;
import com.foxconn.iisd.rcadsvc.domain.DataSetSetting;

@Repository(value = "casePlotRepository")
public interface CasePlotRepository extends JpaRepository<CasePlot, Long> {

//    List<CasePlot> findByNameContaining(String name);
//    CasePlot findByName(String name);

    Page<CasePlot> findAll(Pageable pageable);
    List<CasePlot> findByCaseId(Long caseId);
}