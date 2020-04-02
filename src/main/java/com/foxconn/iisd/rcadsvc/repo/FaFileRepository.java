package com.foxconn.iisd.rcadsvc.repo;

import com.foxconn.iisd.rcadsvc.domain.fa.FaFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository(value = "faFileRepository")
public interface FaFileRepository extends JpaRepository<FaFile, Long> {
    @Query("select new map(id as id, originalFileName as originalFileName, objectName as objectName, " +
            "description as description, sizeInBytes as sizeInBytes) " +
            "from FaFile f where f.faCase.id = :faCaseId")
    List<Map<String, Object>> findByFaCase(@Param("faCaseId") long fa_case_id);
}
