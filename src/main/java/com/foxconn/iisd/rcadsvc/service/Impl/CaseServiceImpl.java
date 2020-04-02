package com.foxconn.iisd.rcadsvc.service.Impl;


import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foxconn.iisd.rcadsvc.domain.Case;
import com.foxconn.iisd.rcadsvc.domain.CaseMatrix;
import com.foxconn.iisd.rcadsvc.domain.CasePlot;
import com.foxconn.iisd.rcadsvc.domain.CasePlotNote;
import com.foxconn.iisd.rcadsvc.domain.DataSetSetting;
import com.foxconn.iisd.rcadsvc.domain.auth.User;
import com.foxconn.iisd.rcadsvc.repo.CaseMatrixRepository;
import com.foxconn.iisd.rcadsvc.repo.CasePlotNoteRepository;
import com.foxconn.iisd.rcadsvc.repo.CasePlotRepository;
import com.foxconn.iisd.rcadsvc.repo.CaseRepository;
import com.foxconn.iisd.rcadsvc.repo.DataSetRepository;
import com.foxconn.iisd.rcadsvc.service.CaseService;

@Service
@Transactional
public class CaseServiceImpl implements CaseService {

    @Autowired
    private CaseRepository caseRepository;
    
    @Autowired
    private CasePlotRepository casePlotRepository;
    
    @Autowired
    private CasePlotNoteRepository casePlotNoteRepository;
    
    @Autowired
    private CaseMatrixRepository caseMatrixRepository;

    @Autowired
    private DataSetRepository dataSetRepository;
    
    @Override
    public List<Case> findByNameContaining(String name) {
        return caseRepository.findByNameContaining(name);
    }

    @Override
    public List<Case> findByDssId(Long dssId){
    	return caseRepository.findByDssId(dssId);
    };
    
    @Override
    public Case findByName(String name) {
        return caseRepository.findByName(name);
    }

    @Override
    public Case createCase(User createUser,Case caseInfo)
    {
    	caseInfo.setCreateTime(new Date());
    	caseInfo.setCreateUser(createUser.getUsername());
    	caseRepository.save(caseInfo);

        return caseInfo;
    }

    @Override
    public Case updateCase(User updateUser,Case caseInfo,Long caseId)
    {
        Case dbcase = caseRepository.findById(caseId).get();

        BeanUtils.copyProperties(caseInfo, dbcase, getNullPropertyNames(caseInfo, "id, dssId, createUser, createTime, modifyUser, modifyTime"));
//        BeanUtils.copyProperties(caseInfo, dbcase, "id", "dssId", "createUser", "createTime", "modifyUser","modifyTime");
        dbcase.setModifyTime(new Date());
        dbcase.setModifyUser(updateUser.getUsername());
        Case updatecase = caseRepository.save(dbcase);
        return updatecase;
    }

    @Override
    public void deleteCase(User deleteUser,Case caseInfo){
    	List<CasePlot> casePlotList = casePlotRepository.findByCaseId(caseInfo.getId());
    	for(CasePlot cp : casePlotList){
    		List<CasePlotNote> casePlotNoteList = casePlotNoteRepository.findByPlotId(cp.getId());
    		casePlotNoteRepository.deleteAll(casePlotNoteList);
    	}
    	casePlotRepository.deleteAll(casePlotList);
        caseRepository.delete(caseInfo);
    }
    
//    @Override
//    public List<CasePlot> findPlotByNameContaining(String name) {
//        return casePlotRepository.findByNameContaining(name);
//    }
//
//    @Override
//    public CasePlot findPlotByName(String name) {
//        return casePlotRepository.findByName(name);
//    }

    @Override
    public CasePlot createCasePlot(User createUser,CasePlot casePlot)
    {
    	casePlotRepository.save(casePlot);

        return casePlot;
    }

    @Override
    public CasePlot updateCasePlot(User updateUser,CasePlot casePlot,Long plotId)
    {
    	CasePlot dbcaseplot = casePlotRepository.findById(plotId).get();

    	BeanUtils.copyProperties(casePlot, dbcaseplot, getNullPropertyNames(casePlot, "id, caseId"));
//        BeanUtils.copyProperties(casePlot, dbcaseplot, "id", "caseId");

        if(casePlot.getUpdateTime()==null || casePlot.getUpdateTime().equals(""))
            dbcaseplot.setUpdateTime(new Date());
        CasePlot updatecaseplot = casePlotRepository.save(dbcaseplot);
        return updatecaseplot;
    }

    @Override
    public void deleteCasePlot(User deleteUser,CasePlot casePlot){
    	//Note不刪除,保存.僅刪除CaePlot
        //List<CasePlotNote> casePlotNoteList = casePlotNoteRepository.findByPlotId(casePlot.getId());
		//casePlotNoteRepository.deleteAll(casePlotNoteList);
        casePlotRepository.delete(casePlot);
    }
    
//    @Override
//    public List<CasePlotNote> findPlotNoteByNameContaining(String name) {
//        return casePlotNoteRepository.findByNameContaining(name);
//    }
//
//    @Override
//    public CasePlotNote findPlotNoteByName(String name) {
//        return casePlotNoteRepository.findByName(name);
//    }

    @Override
    public CasePlotNote createCasePlotNote(User createUser,CasePlotNote casePlotNote)
    {
    	CasePlot dbcaseplot = casePlotRepository.findById(casePlotNote.getPlotId()).get();
    	casePlotNote.setPlotType(dbcaseplot.getPlotType());
    	
    	Case dbcase = caseRepository.findById(dbcaseplot.getCaseId()).get();
    	casePlotNote.setSettingJson(dbcase.getSettingJson());
    	
    	DataSetSetting dbDss = dataSetRepository.findById(dbcase.getDssId()).get();
    	casePlotNote.setDataSetName(dbDss.getName());
    	
    	casePlotNote.setCreateTime(new Date());
    	casePlotNote.setCreateUser(createUser.getUsername());
    	casePlotNoteRepository.save(casePlotNote);

        return casePlotNote;
    }

    @Override
    public CasePlotNote updateCasePlotNote(User updateUser,CasePlotNote casePlotNote,Long noteId)
    {
        CasePlotNote dbcaseplotnote = casePlotNoteRepository.findById(noteId).get();

        BeanUtils.copyProperties(casePlotNote, dbcaseplotnote, getNullPropertyNames(casePlotNote, "id, plotId, plotType, createUser, createTime, modifyUser"));
//        BeanUtils.copyProperties(casePlotNote, dbcaseplotnote, "id", "plotId", "createTime");
        dbcaseplotnote.setModifyUser(updateUser.getUsername());
        CasePlotNote updatecaseplotnote = casePlotNoteRepository.save(dbcaseplotnote);
        return updatecaseplotnote;
    }

    @Override
    public void deleteCasePlotNote(User deleteUser,CasePlotNote casePlotNote){
        casePlotNoteRepository.delete(casePlotNote);
    }
    
    @Override
    public CaseMatrix createCaseMatrix(User createUser,CaseMatrix caseMatrix)
    {
    	caseMatrix.setCreateTime(new Date());
    	caseMatrix.setCreateUser(createUser.getUsername());
    	caseMatrixRepository.save(caseMatrix);

        return caseMatrix;
    }

    @Override
    public CaseMatrix updateCaseMatrix(User updateUser, CaseMatrix caseMatrix, Long caseMatrixId)
    {
    	CaseMatrix dbcasematrix = caseMatrixRepository.findById(caseMatrixId).get();

        BeanUtils.copyProperties(caseMatrix, dbcasematrix, getNullPropertyNames(caseMatrix, "id, caseId, maxtrixType, createUser, createTime, modifyUser, modifyTime"));
//        BeanUtils.copyProperties(casePlotNote, dbcaseplotnote, "id", "plotId", "createTime");
        dbcasematrix.setModifyTime(new Date());
        dbcasematrix.setModifyUser(updateUser.getUsername());
        CaseMatrix updatecasematrix = caseMatrixRepository.save(dbcasematrix);
        return updatecasematrix;
    }

    @Override
    public void deleteCaseMatrix(User deleteUser,CaseMatrix matrix){
        caseMatrixRepository.delete(matrix);
    }
    
    public static String[] getNullPropertyNames (Object source, String ignoreCols) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for(java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        String[] ignoreColArray = ignoreCols.split(",");
        for(String colName : ignoreColArray){
        	emptyNames.add(colName);
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }
}