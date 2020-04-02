package com.foxconn.iisd.rcadsvc.service.Impl;


import com.foxconn.iisd.rcadsvc.service.DataSetService;
import com.foxconn.iisd.rcadsvc.domain.DataSetSetting;
import com.foxconn.iisd.rcadsvc.domain.auth.User;
import com.foxconn.iisd.rcadsvc.domain.DataSetPart;
import com.foxconn.iisd.rcadsvc.domain.DataSetStationItem;
import com.foxconn.iisd.rcadsvc.repo.DataSetRepository;
import com.foxconn.iisd.rcadsvc.repo.DataSetStationItemRepository;
import com.foxconn.iisd.rcadsvc.repo.DataSetPartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.BeanUtils;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Service
@Transactional
public class DataSetServiceImpl implements DataSetService {

    @Autowired
    private DataSetRepository dataSetRepository;

    @Autowired
    private DataSetStationItemRepository dataSetStationItemRepository;

    @Autowired
    private DataSetPartRepository dataSetPartRepository;

    @Override
    public List<DataSetSetting> findByNameContaining(String name) {
        return dataSetRepository.findByNameContaining(name);
    }

    @Override
    public DataSetSetting findByName(String name) {
        return dataSetRepository.findByName(name);
    }

    @Override
    public List<DataSetSetting> findByProduct(String product) {
        return dataSetRepository.findByProduct(product);
    }

    @Override
    public DataSetSetting createDataSetSetting(User createUser,DataSetSetting dss)
    {
        for(DataSetStationItem item : dss.getDataSetStationItem()){
            item.setProduct(dss.getProduct());
        }
        for(DataSetPart dsp : dss.getDataSetPart()){
            dsp.setProduct(dss.getProduct());
        }
        dss.setCreateTime(new Date());
        dss.setCreateUser(createUser.getUsername());
        dataSetRepository.save(dss);

        return dss;
    }

    @Override
    public DataSetSetting updateDataSetSetting(User updateUser,DataSetSetting dss,Long dssId)
    {
        DataSetSetting dbDss = dataSetRepository.findById(dssId).get();

        //舊的刪除
        dataSetStationItemRepository.deleteAll(dbDss.getDataSetStationItem());
        dataSetPartRepository.deleteAll(dbDss.getDataSetPart());

        for(DataSetStationItem item : dss.getDataSetStationItem()){
            item.setProduct(dbDss.getProduct());
        }

        for(DataSetPart dsp : dss.getDataSetPart()){
            dsp.setProduct(dbDss.getProduct());
        }

        BeanUtils.copyProperties(dss, dbDss, "id","product", "btCreateTime", "btName", "createTime", "createUser");
        dbDss.setModifyTime(new Date());
        dbDss.setModifyUser("admin");
        DataSetSetting updatedss = dataSetRepository.save(dbDss);
        return updatedss;
    }

    @Override
    public DataSetSetting copyDataSetSetting(User createUser, String name, Long dssId)
    {
    	DataSetSetting dbDss = dataSetRepository.findById(dssId).get();
    	
    	DataSetSetting new_one = new DataSetSetting();
    	new_one.setName(name);
    	BeanUtils.copyProperties(dbDss, new_one, "id", "name", "btCreateTime", "btLastTime", "btName", "btNextTime", "createTime", "createUser", "modifyTime", "modifyUser", "dataSetPart", "dataSetStationItem");

//    	dbDss.setId(null);								//new one
//    	dbDss.setName(name);							//clone dateset with new name
//    	dbDss.setBtCreateTime(null);					//ignore (bigtable use)
//    	dbDss.setBtLastTime(null);						//ignore (bigtable use)
//    	dbDss.setBtName(null);							//ignore (bigtable use)
//    	dbDss.setBtNextTime(null);						//ignore (bigtable use)
//    	dbDss.setCreateTime(new Date());
//    	dbDss.setCreateUser(createUser.getUsername());
//    	dbDss.setModifyTime(null);						//new one with no modifytime
//        dbDss.setModifyUser(null);						//new one with no modifyuser
    	new_one.setCreateTime(new Date());
    	new_one.setCreateUser(createUser.getUsername());
    	new_one = dataSetRepository.save(new_one);

        Set<DataSetStationItem> dssi = new HashSet<DataSetStationItem>();
    	Set<DataSetPart> dsp = new HashSet<DataSetPart>();
    	
    	for(DataSetStationItem dssi_item : dbDss.getDataSetStationItem()){
    		DataSetStationItem new_dssi_item = new DataSetStationItem();
    		BeanUtils.copyProperties(dssi_item, new_dssi_item, "id", "dss_id");
    		new_dssi_item.setDssId(new_one.getId());
    		dssi.add(new_dssi_item);
    	}
    	for(DataSetPart dsp_item : dbDss.getDataSetPart()){
    		DataSetPart new_dsp_item = new DataSetPart();
    		BeanUtils.copyProperties(dsp_item, new_dsp_item, "id", "dss_id");
    		new_dsp_item.setDssId(new_one.getId());
    		dsp.add(new_dsp_item);
    	}
    	new_one.setDataSetStationItem(dssi);
    	new_one.setDataSetPart(dsp);
        
    	dataSetRepository.save(new_one);
    	
        return new_one;
    }
    
    @Override
    public void deleteDataSetSetting(User deleteUser,DataSetSetting dss){
        dataSetStationItemRepository.deleteAll( dss.getDataSetStationItem());
        dataSetPartRepository.deleteAll(dss.getDataSetPart());
        dataSetRepository.delete(dss);
    }
//
//    private Set<DataSetStationItem> createDSStationItem(Set<DataSetStationItem> list,DataSetSetting dss){
//        Set<DataSetStationItem> result = new HashSet<DataSetStationItem>();
//        for(DataSetStationItem dssi : list){
//            DataSetStationItem newdssi = new DataSetStationItem();
//            newdssi.setDssId(dss.getId());
//            newdssi.setProduct(dss.getProduct());
//            newdssi.setStation(dssi.getStation());
//            newdssi.setItem(dssi.getItem());
//            newdssi = dataSetStationItemRepository.save(newdssi);
//            result.add(newdssi);
//        }
//        return result;
//    }
//
//    private Set<DataSetPart> createDSPart(Set<DataSetPart> list,DataSetSetting dss){
//        Set<DataSetPart> result = new HashSet<DataSetPart>();
//        for(DataSetPart dsp : list){
//            DataSetPart newdsp = new DataSetPart();
//            newdsp.setDssId(dss.getId());
//            newdsp.setProduct(dss.getProduct());
//            newdsp.setPartType(dsp.getPartType());
//            newdsp.setComponent(dsp.getComponent());
//            newdsp = dataSetPartRepository.save(newdsp);
//            result.add(newdsp);
//        }
//        return result;
//    }
}