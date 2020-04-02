package com.foxconn.iisd.rcadsvc.repo;

import com.foxconn.iisd.rcadsvc.domain.fa.Symptom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository(value = "symptonRepository")
public interface SymptonRepository extends JpaRepository<Symptom, Long> {
    @Query("select distinct s.name from Symptom s where s.type = 0" +
            " and s.product = :product and s.line = :line and s.station = :station order by s.name")
    List<Symptom> findTestSympton(@Param("product") String product,
                                  @Param("line") String line, @Param("station") String station);
}
