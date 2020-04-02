package com.foxconn.iisd.rcadsvc.service;

import com.foxconn.iisd.rcadsvc.domain.FunctionList;
import com.foxconn.iisd.rcadsvc.domain.auth.User;

import java.util.List;

public interface FunctionListService {

	List<FunctionList> findAllByOrderByPathAsc();

	FunctionList findById(int id);


}