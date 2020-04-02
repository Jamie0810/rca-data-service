package com.foxconn.iisd.rcadsvc.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foxconn.iisd.rcadsvc.domain.CaseMatrix;

@Repository(value = "caseMatrixRepository")
public interface CaseMatrixRepository extends JpaRepository<CaseMatrix, Long> {

    List<CaseMatrix> findByNameContaining(String name);
    List<CaseMatrix> findByCaseId(Long caseId);
    CaseMatrix findByName(String name);

    Page<CaseMatrix> findAll(Pageable pageable);

}