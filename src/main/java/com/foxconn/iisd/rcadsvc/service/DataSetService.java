package com.foxconn.iisd.rcadsvc.service;

import com.foxconn.iisd.rcadsvc.msg.DataSetSettingMsg;
import com.foxconn.iisd.rcadsvc.domain.DataSetSetting;
import com.foxconn.iisd.rcadsvc.domain.auth.User;

import java.util.List;

public interface DataSetService {

    List<DataSetSetting> findByNameContaining(String name);
    DataSetSetting findByName(String name);
    List<DataSetSetting> findByProduct(String product);

    void deleteDataSetSetting(User deleteUser,DataSetSetting dss);
    DataSetSetting createDataSetSetting(User createUser,DataSetSetting dss);
    DataSetSetting updateDataSetSetting(User updateUser,DataSetSetting dss,Long dss_id);
    DataSetSetting copyDataSetSetting(User createUser, String name, Long dss_id);

}