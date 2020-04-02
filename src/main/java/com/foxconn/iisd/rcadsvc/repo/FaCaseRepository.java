package com.foxconn.iisd.rcadsvc.repo;

import com.foxconn.iisd.rcadsvc.domain.fa.FaCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository(value = "faCaseRepository")
public interface FaCaseRepository extends JpaRepository<FaCase, Long> {
    @Query("select distinct f.product, f.symptom.name from FaCase f order by f.product")
    List<Object[]> findProductSymptoms();

    @Query("select distinct f.createUser.username as username from FaCase f order by username")
    List<String> findCreateUser();
}
