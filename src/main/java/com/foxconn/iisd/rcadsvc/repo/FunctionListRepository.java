package com.foxconn.iisd.rcadsvc.repo;

import com.foxconn.iisd.rcadsvc.domain.FunctionList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository(value = "functionListRepository")
public interface FunctionListRepository extends JpaRepository<FunctionList, Integer> {

	List<FunctionList> findAllByOrderByPathAsc();
	FunctionList findById(int id);


}