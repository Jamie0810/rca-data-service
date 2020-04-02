package com.foxconn.iisd.rcadsvc.service.Impl;

import com.foxconn.iisd.rcadsvc.domain.auth.User;
import com.foxconn.iisd.rcadsvc.domain.fa.FaCase;
import com.foxconn.iisd.rcadsvc.domain.fa.FaFile;
import com.foxconn.iisd.rcadsvc.domain.fa.FaSn;
import com.foxconn.iisd.rcadsvc.domain.fa.Symptom;
import com.foxconn.iisd.rcadsvc.repo.FaCaseRepository;
import com.foxconn.iisd.rcadsvc.repo.FaFileRepository;
import com.foxconn.iisd.rcadsvc.repo.FaSnRepository;
import com.foxconn.iisd.rcadsvc.repo.SymptonRepository;
import com.foxconn.iisd.rcadsvc.service.FaCaseService;
import com.foxconn.iisd.rcadsvc.service.FileService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service(value = "faCaseService")
@Transactional
public class FaCaseServiceImpl implements FaCaseService {
    @Autowired
    private FaCaseRepository faCaseRepository;

    @Autowired
    private FaSnRepository faSnRepository;

    @Autowired
    private FaFileRepository faFileRepository;

    @Autowired
    private SymptonRepository symptonRepository;

    @Autowired
    @Qualifier("minioFileService")
    private FileService fileService;

    @PersistenceContext
    private EntityManager em;

    @Override
    public FaCase create(User createUser, FaCase faCase) throws Exception {
        Symptom symptom = faCase.getSymptom();
        symptom.clearId();
        symptom = symptonRepository.save(symptom);
        faCase.setSymptom(symptom);
        faCase.clearId();
        faCase.setCreateUser(createUser);
        faCase.setCreateTime(LocalDateTime.now());
        faCase.setUpdateUser(createUser);
        faCase.setUpdateTime(LocalDateTime.now());
        return faCaseRepository.save(faCase);
    }

    @Override
    public FaCase update(User updateUser, FaCase faCase) throws Exception {
        FaCase _faCase = faCaseRepository.findById(faCase.getId()).get();
        BeanUtils.copyProperties(faCase, _faCase,
                "product", "station", "symptom", "createTime", "createUser");
        _faCase.setUpdateUser(updateUser);
        _faCase.setUpdateTime(LocalDateTime.now());
        return faCaseRepository.save(_faCase);
    }

    @Override
    public List<FaCase> queryFaCases(
            String product, String riskType, LocalDateTime startTime, LocalDateTime stopTime,
            String createUser, String failSymptom) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<FaCase> cq = cb.createQuery(FaCase.class);

        Root<FaCase> faCase = cq.from(FaCase.class);
        List<Predicate> predicates = new ArrayList<>();

        if (product != null) {
            predicates.add(cb.equal(faCase.get("product"), product));
        }
        if (riskType != null) {
            predicates.add(cb.equal(faCase.get("riskType"), riskType));
        }
        if (startTime != null) {
            predicates.add(cb.greaterThanOrEqualTo(faCase.get("testStartTime"), startTime));
        }
        if (stopTime != null) {
            predicates.add(cb.lessThanOrEqualTo(faCase.get("testStartTime"), stopTime));
        }
        if (createUser != null) {
            Join<FaCase, User> join = faCase.join("createUser", JoinType.INNER);
            predicates.add(cb.equal(join.get("username"), createUser));
        }
        if (failSymptom != null) {
            Join<FaCase, Symptom> join = faCase.join("symptom", JoinType.INNER);
            predicates.add(cb.equal(join.get("name"), failSymptom));
        }

        cq.where(predicates.toArray(new Predicate[predicates.size()]));

        return em.createQuery(cq).getResultList();
    }

    @Override
    public List<FaCase> queryFaCaseById(
            Long id) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<FaCase> cq = cb.createQuery(FaCase.class);

        Root<FaCase> faCase = cq.from(FaCase.class);
        List<Predicate> predicates = new ArrayList<>();

        if (id != null) {
            predicates.add(cb.equal(faCase.get("id"), id));
        }


        cq.where(predicates.toArray(new Predicate[predicates.size()]));

        return em.createQuery(cq).getResultList();
    }
}