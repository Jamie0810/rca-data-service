package com.foxconn.iisd.rcadsvc.service.Impl;

import com.foxconn.iisd.rcadsvc.domain.FunctionList;
import com.foxconn.iisd.rcadsvc.repo.FunctionListRepository;
import com.foxconn.iisd.rcadsvc.service.FunctionListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service(value = "FunctionListService")
@Transactional
public class FunctionListServiceImpl implements FunctionListService {

    @Autowired
    private FunctionListRepository functionListRepository;

    @Override
    public List<FunctionList> findAllByOrderByPathAsc() {
        return functionListRepository.findAllByOrderByPathAsc();
    }

    @Override
    public FunctionList findById(int id) {
        return functionListRepository.findById(id);
    }

}
