package com.foxconn.iisd.rcadsvc.service;

import java.util.List;

import com.foxconn.iisd.rcadsvc.domain.Case;
import com.foxconn.iisd.rcadsvc.domain.CaseMatrix;
import com.foxconn.iisd.rcadsvc.domain.CasePlot;
import com.foxconn.iisd.rcadsvc.domain.CasePlotNote;
import com.foxconn.iisd.rcadsvc.domain.auth.User;

public interface CaseService {

    List<Case> findByNameContaining(String name);
    List<Case> findByDssId(Long dssId);
    Case findByName(String name);

    void deleteCase(User deleteUser,Case caseInfo);
    Case createCase(User createUser,Case caseInfo);
    Case updateCase(User updateUser,Case caseInfo,Long caseId);
    
//    List<CasePlot> findPlotByNameContaining(String name);
//    CasePlot findPlotByName(String name);
    
    void deleteCasePlot(User deleteUser,CasePlot casePlot);
    CasePlot createCasePlot(User createUser,CasePlot casePlot);
    CasePlot updateCasePlot(User updateUser,CasePlot casePlot,Long plotId);
    
//    List<CasePlotNote> findPlotNoteByNameContaining(String name);
//    CasePlotNote findPlotNoteByName(String name);
    
    void deleteCasePlotNote(User deleteUser,CasePlotNote casePlotNote);
    CasePlotNote createCasePlotNote(User createUser,CasePlotNote casePlotNote);
    CasePlotNote updateCasePlotNote(User updateUser,CasePlotNote casePlotNote,Long noteId);
    
    void deleteCaseMatrix(User deleteUser,CaseMatrix caseMatrix);
    CaseMatrix createCaseMatrix(User createUser,CaseMatrix caseMatrix);
    CaseMatrix updateCaseMatrix(User updateUser,CaseMatrix caseMatrix,Long caseMatrixId);

}