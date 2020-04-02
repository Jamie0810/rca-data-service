package com.foxconn.iisd.rcadsvc.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foxconn.iisd.rcadsvc.domain.Case;

@Repository(value = "caseRepository")
public interface CaseRepository extends JpaRepository<Case, Long> {

    List<Case> findByNameContaining(String name);
    List<Case> findByDssId(Long dssId);
    Case findByName(String name);

    Page<Case> findAll(Pageable pageable);

}