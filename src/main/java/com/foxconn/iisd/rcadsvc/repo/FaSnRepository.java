package com.foxconn.iisd.rcadsvc.repo;

import com.foxconn.iisd.rcadsvc.domain.fa.FaCase;
import com.foxconn.iisd.rcadsvc.domain.fa.FaSn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository(value = "faSnRepository")
public interface FaSnRepository extends JpaRepository<FaSn, Long> {
    @Query("select new map(" +
            "id as id, testing_id as testing_id, sn as sn, station as station, machine as machine, " +
            "testStartTime as testStartTime, failureSymptom as failureSymptom, failureDesc as failureDesc) " +
            "from FaSn f where f.faCase.id = :faCaseId")
    List<FaSn> findByFaCase(@Param("faCaseId") long fa_case_id);
}
