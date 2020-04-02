package com.foxconn.iisd.rcadsvc.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foxconn.iisd.rcadsvc.domain.CodeTable;

@Repository(value = "codeTableRepository")
public interface CodeTableRepository extends JpaRepository<CodeTable, Long> {

    List<CodeTable> findByCodeNameContaining(String codeName);
    CodeTable findByCodeName(String codeName);
    CodeTable findByCodeProductAndCodeCategoryAndCode(String codeProduct, String codeCategory, String code);

    Page<CodeTable> findAll(Pageable pageable);
    List<CodeTable> findByCodeProductAndCodeCategory(String codeProduct, String codeCategory);

}