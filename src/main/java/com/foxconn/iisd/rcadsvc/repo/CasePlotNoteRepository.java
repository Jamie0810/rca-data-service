package com.foxconn.iisd.rcadsvc.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foxconn.iisd.rcadsvc.domain.CasePlot;
import com.foxconn.iisd.rcadsvc.domain.CasePlotNote;

@Repository(value = "casePlotNoteRepository")
public interface CasePlotNoteRepository extends JpaRepository<CasePlotNote, Long> {

//    List<CasePlotNote> findByNameContaining(String name);
//    CasePlotNote findByName(String name);

    Page<CasePlotNote> findAll(Pageable pageable);
    List<CasePlotNote> findByPlotId(Long plotId);
    List<CasePlotNote> findByCaseId(Long caseId);
}