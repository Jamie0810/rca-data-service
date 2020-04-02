package com.foxconn.iisd.rcadsvc.util;


import com.foxconn.iisd.rcadsvc.domain.Case;
import com.foxconn.iisd.rcadsvc.domain.auth.User;
import com.foxconn.iisd.rcadsvc.msg.BarFiledMsg;
import com.foxconn.iisd.rcadsvc.msg.CaseMsg;
import com.foxconn.iisd.rcadsvc.msg.PlotMsg;
import com.foxconn.iisd.rcadsvc.repo.CaseRepository;
import com.foxconn.iisd.rcadsvc.service.CaseService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.foxconn.iisd.rcadsvc.util.XWJUtil;

public class CaseFiledUtil  {
	@Autowired
	private CaseRepository caseRepository;

	private XWJUtil xwjUtil = new XWJUtil();


	private final String caseTableName = "case_dataset_bigtable@";

	private final String mappingTable = "case_bigtable_column_mapping";

	public Object updateCaseTable(CaseMsg caseMsg, JdbcTemplate mysqlJtl, User currentUser, Case dbCase) {
		HashMap dataMap = new HashMap<String, Object>();


		String msg= "";
		if(dbCase != null){
			Long caseId = dbCase.getId();
			//String settingJson = caseMsg.getSettingJson();
			JSONObject settingJson = new JSONObject(caseMsg.getSettingJson());
			JSONArray selectField = (JSONArray)settingJson.get("selectField");
			JSONArray createField = (JSONArray)settingJson.get("createField");
			JSONArray conditionField = (JSONArray)settingJson.get("condition");
			JSONArray filterField  =  (JSONArray)settingJson.get("filter");


			/** 新建立case分析表**/
			int maxvarchar = 200;
			int minvarchar = 50;
			String newTableName = xwjUtil.addSQLCHAR(caseTableName+caseId+"_new").toString();
			String oldTableName = xwjUtil.addSQLCHAR(caseTableName+caseId).toString();
			String dataSetTableName = "`data_set_bigtable@"+dbCase.getDssId()+"` ";
			StringBuffer createSB = new StringBuffer();
			StringBuffer insertSB = new StringBuffer();
			createSB.append(" CREATE TABLE ").append(newTableName).append(" (");
			insertSB.append(" INSERT INTO  ").append(newTableName).append("  SELECT ");
			//for(Map<String,Object> fieldObj : selectField){

			List<String> addField = new ArrayList<>();
			for(int idx = 0 ; idx < selectField.length() ; ++idx){
				JSONObject fieldObj = selectField.getJSONObject(idx);
				String type = fieldObj.get("type").toString();
				String name = fieldObj.get("name").toString();
				addField.add(name);
				if(type.equals("fixed")){
					int size = minvarchar;
					if(name.equals("data_set_name"))
						size = maxvarchar;
					if(name.equals("scantime")){
						createSB.append(" `").append(name).append("` varchar(").append(size).append(") , ");
						insertSB.append(xwjUtil.addSQLCHAR(name)).append(",");
					}
					else {
						createSB.append(" `").append(name).append("` varchar(").append(size).append(") , ");
						insertSB.append("ifnull(").append(xwjUtil.addSQLCHAR(name)).append(",null").append(") as ").append(xwjUtil.addSQLCHAR(name)).append(",");
					}

				}else{
					createSB.append(" `").append(setEncodeColumn(name,caseId,mysqlJtl)).append("` varchar(").append(maxvarchar).append(") , ");
					if(!type.equals("item_info")) {
						insertSB.append("ifnull(").append(type).append("->>'$.\"").append(name.trim()).append("\"'").append(",null) as ").append(xwjUtil.addSQLCHAR(name)).append(",");
					}
					else {
						insertSB.append("if(").append(type).append("->>'$.\"").append(name.trim()).append("\"' ='null'").append(",null,").append(type).append("->>'$.\"").append(name.trim()).append("\"') as ").append(xwjUtil.addSQLCHAR(name)).append(",");
						if (name.contains("@") && !name.contains("result") && name.contains("^")) {
							createSB.append(" `").append(setEncodeColumn(name,caseId,mysqlJtl)+"@result").append("` varchar(").append(maxvarchar).append(") NOT NULL, ");
							insertSB.append("ifnull(").append(type).append("->>'$.\"").append(name.trim()).append("@result").append("\"'").append(",'') as ").append(xwjUtil.addSQLCHAR(name + "@result")).append(",");
						}
					}
				}
			}
			//預設都要有的欄位(`data_set_id`,`product`,`sn`,`value_rank`),用來當PK Key
			String[] pkField = new String[]{"data_set_id","product","sn","value_rank","scantime"};
			for(String pkf : pkField){
				if(!addField.contains(pkf)) {
					if (pkf.equals("scantime")) {
						createSB.append(" `").append(pkf).append("` varchar(").append(pkf).append(") , ");
						insertSB.append(xwjUtil.addSQLCHAR(pkf)).append(",");
					} else {
						createSB.append(" `").append(pkf).append("` varchar(").append(minvarchar).append(") , ");
						insertSB.append("ifnull(").append(xwjUtil.addSQLCHAR(pkf)).append(",null").append(") as ").append(xwjUtil.addSQLCHAR(pkf)).append(",");
					}
				}
			}
			//for(Map<String,Object> fieldObj : createField){
			for(int idx = 0 ; idx < createField.length() ; ++idx){
				JSONObject fieldObj = createField.getJSONObject(idx);
				String dtType = fieldObj.get("dataType").toString();
				String type = fieldObj.get("type").toString();
				String name = fieldObj.get("name").toString();
				String newname =  fieldObj.get("newname").toString();
				if(dtType.equals("string")){
					int startIdx = Integer.parseInt(fieldObj.get("start").toString());
					int len = Integer.parseInt(fieldObj.get("length").toString());
					if(type.equals("fixed")){
						createSB.append(" `").append(newname).append("` varchar(").append(minvarchar).append(") , ");
						insertSB.append("ISNULL(SUBSTRING(").append(xwjUtil.addSQLCHAR(name)).append(",").append(startIdx).append(",").append(len).append("),'') as ").append(xwjUtil.addSQLCHAR(newname)).append(",");
					}else{
						createSB.append(" `").append(newname).append("` varchar(").append(maxvarchar).append(") , ");
						insertSB.append("ISNULL(SUBSTRING(").append(type).append("->>'$.\"").append(name).append("\"'").append(",").append(startIdx).append(",").append(len).append("),null) as ").append(xwjUtil.addSQLCHAR(newname)).append(",");
					}
				}else{
					int times = Integer.parseInt(fieldObj.get("times").toString());
					int precision = Integer.parseInt(fieldObj.get("precision").toString());
					if(type.equals("fixed")){
						int size = minvarchar;
						if(name.equals("data_set_name"))
							size = maxvarchar;
						createSB.append(" `").append(newname).append("` varchar(").append(size).append(") , ");
						//insertSB.append("ifnull(").append(xwjUtil.addSQLCHAR(name)).append(",''").append(") as ").append(xwjUtil.addSQLCHAR(name)).append(",");
						//insertSB.append("ISNULL(SUBSTRING(").append(xwjUtil.addSQLCHAR(name)).append(",").append(startIdx).append(",").append(len).append("),'') as ").append(xwjUtil.addSQLCHAR(newname)).append(",");
						insertSB.append("CAST((IFNULL(").append(xwjUtil.addSQLCHAR(name)).append(",0)*").append(times).append(") as DECIMAL(9,").append(precision).append(")) as ").append(xwjUtil.addSQLCHAR(newname)).append(",");

					}else{
						createSB.append(" `").append(newname).append("` varchar(").append(maxvarchar).append(") , ");
						//insertSB.append("ifnull(").append(type).append("->>'$.\"").append(name.trim()).append("\"'").append(",'') as ").append(xwjUtil.addSQLCHAR(name)).append(",");
						//insertSB.append("ISNULL(SUBSTRING(").append(type).append("->>'$.\"").append(name).append("\"'").append(",").append(startIdx).append(",").append(len).append("),'') as ").append(xwjUtil.addSQLCHAR(newname)).append(",");
						insertSB.append("CAST((IFNULL(").append(type).append("->>'$.\"").append(name).append("\"'").append(",0)*").append(times).append(") as DECIMAL(9,").append(precision).append(")) as ").append(xwjUtil.addSQLCHAR(newname)).append(",");
					}
				}

			}
			createSB.append(" PRIMARY KEY (`data_set_id`,`product`,`sn`,`value_rank`)  )ENGINE=InnoDB;");
			String insertSQL = insertSB.substring(0,insertSB.toString().length()-1) + " FROM " + dataSetTableName;
			StringBuffer whereSb = new StringBuffer();
			whereSb.append(" WHERE 1=1 ");
			//for(Map<String, Object> field : conditionField)
			for(int idx = 0 ; idx < conditionField.length() ; ++idx) {
				JSONObject fieldObj = conditionField.getJSONObject(idx);
				whereSb.append(" AND ").append(xwjUtil.convertJSONToCondition(fieldObj));
			}
			//for(Map<String, Object> field : filterField)
			for(int idx = 0 ; idx < filterField.length() ; ++idx) {
				JSONObject fieldObj = filterField.getJSONObject(idx);
				whereSb.append(" AND ").append(xwjUtil. convertJSONToCondition(fieldObj));
			}
			mysqlJtl.execute(createSB.toString());

			/** 將資料塞入新的case分析表**/
			System.out.println(insertSQL + whereSb.toString());
			mysqlJtl.execute(insertSQL + whereSb.toString());

			/** 刪除 Case 舊分析表**/
			mysqlJtl.execute(" DROP TABLE IF EXISTS "+ oldTableName);

			/** 更名新Case分析檔**/
			mysqlJtl.execute("RENAME TABLE "+newTableName+" TO " + oldTableName);

			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			dbCase.setDataLastDate((new Date()));
			dbCase.setSettingJson(caseMsg.getSettingJson());
			//if(caseMsg.getName()==null || caseMsg.getName().equals("")) caseMsg.setName(dbCase.getName());
			//if(caseMsg.getRemark()==null || caseMsg.getRemark().equals("")) caseMsg.setRemark(dbCase.getRemark());
			//dbCase = caseService.updateCase(currentUser,dbCase,caseMsg.getId());
//                dataMap.put(dbCase.getId(), dbCase.toCaseMsg(false, dataSetRepository));
			msg = "update case data successfully!!";
		}else{
			msg = "case not exist!!";
		}


//
		return null;
	}

	public Case updateCaseTable2(CaseMsg caseMsg, JdbcTemplate mysqlJtl, User currentUser, Case dbCase,CaseService caseService) {

		HashMap dataMap = new HashMap<String, Object>();


		String msg= "";
		if(dbCase != null){
			Long caseId = dbCase.getId();
			//String settingJson = caseMsg.getSettingJson();
			JSONObject settingJson = new JSONObject(caseMsg.getSettingJson());
			JSONArray selectField = (JSONArray)settingJson.get("selectField");
			JSONArray createField = (JSONArray)settingJson.get("createField");
			JSONArray conditionField = (JSONArray)settingJson.get("condition");
			JSONArray filterField  =  (JSONArray)settingJson.get("filter");

//                List<Map<String, Object>> selectField = getTestField(dbCase.getDssId());
//                List<Map<String, Object>> createField = new ArrayList<>();
//                List<Map<String, Object>> conditionField = new ArrayList<>();
//                List<Map<String, Object>> filterField  = new ArrayList<>();
			//List<Map<String, Object>> selectField = this.getTestGenCaseDataObj("selectField");
			//List<Map<String, Object>> conditionField = this.getTestGenCaseDataObj("condition");
			//List<Map<String, Object>> filterField = this.getTestGenCaseDataObj("filter");

			/** 新建立case分析表**/
			int maxvarchar = 100;
			int minvarchar = 50;
			String newTableName = xwjUtil.addSQLCHAR(caseTableName+caseId+"_new").toString();
			String oldTableName = xwjUtil.addSQLCHAR(caseTableName+caseId).toString();
			String dataSetTableName = "`data_set_bigtable@"+dbCase.getDssId()+"` ";
			StringBuffer createSB = new StringBuffer();
			StringBuffer insertSB = new StringBuffer();
			createSB.append(" CREATE TABLE ").append(newTableName).append(" (");
			insertSB.append(" INSERT INTO  ").append(newTableName).append("  SELECT ");
			//for(Map<String,Object> fieldObj : selectField){
			List<String> addField = new ArrayList<>();
			List<String> testField = new ArrayList<>();
			for(int idx = 0 ; idx < selectField.length() ; ++idx){
				JSONObject fieldObj = selectField.getJSONObject(idx);
				String type = fieldObj.get("type").toString();
				String name = fieldObj.get("name").toString();
				addField.add(name);
				if(type.equals("fixed")){
					int size = minvarchar;
					if(name.equals("data_set_name"))
						size = 200;
					if(name.equals("scantime")){
						createSB.append(" `").append(name).append("` varchar(").append(size).append(") , ");
						insertSB.append(xwjUtil.addSQLCHAR(name)).append(",");
					}
					else {
						createSB.append(" `").append(name).append("` varchar(").append(size).append(") , ");
						insertSB.append("ifnull(").append(xwjUtil.addSQLCHAR(name)).append(",null").append(") as ").append(xwjUtil.addSQLCHAR(name)).append(",");
					}

				}else {
					if (type.equals("station_info") ) {
						if(!name.contains("@test_starttime")) {
							createSB.append(" `").append(name).append("` varchar(").append(maxvarchar).append(") , ");
							insertSB.append("ifnull(").append(type).append("->>'$.\"").append(name.trim()).append("\"'").append(",null) as ").append(xwjUtil.addSQLCHAR(name)).append(",");
							String station = name.split("@")[0];
							station = station + "@test_starttime";
							if (!testField.contains(station)) {
								createSB.append(" `").append(station).append("` DATETIME , ");
								insertSB.append("ifnull(").append(type).append("->>'$.\"").append(station).append("\"'").append(",null) as ").append(xwjUtil.addSQLCHAR(station)).append(",");
								testField.add(station);
							}
						}
					}
					else {
						createSB.append(" `").append(setEncodeColumn(name,caseId,mysqlJtl)).append("` varchar(").append(maxvarchar).append(") , ");
						if (!type.equals("item_info")) {
							insertSB.append("ifnull(").append(type).append("->>'$.\"").append(name.trim()).append("\"'").append(",null) as ").append(xwjUtil.addSQLCHAR(name)).append(",");
						} else {
							insertSB.append("if(").append(type).append("->>'$.\"").append(name.trim()).append("\"' ='null'").append(",null,").append(type).append("->>'$.\"").append(name.trim()).append("\"') as ").append(xwjUtil.addSQLCHAR(name)).append(",");
							String str = name + "@result";
							String sql = "select column_name from data_set_bigtable_columns where column_name = '" + str + "' and data_set_id = " + dbCase.getDssId();
							List<Map<String, Object>> resultList = mysqlJtl.queryForList(sql);
							if (resultList.size() > 0) {
								createSB.append(" `").append(setEncodeColumn(name,caseId,mysqlJtl)).append("@result").append("` varchar(").append(25).append(") NOT NULL, ");
								insertSB.append("ifnull(").append(type).append("->>'$.\"").append(name.trim()).append("@result").append("\"'").append(",'') as ").append(xwjUtil.addSQLCHAR(name + "@result")).append(",");
							}
							String station = name.split("@")[0];
							station = station + "@test_starttime";
							if (!testField.contains(station)) {
								sql = "select column_name from data_set_bigtable_columns where column_name = '" + station + "' and data_set_id = " + dbCase.getDssId();
								resultList = mysqlJtl.queryForList(sql);
								if (resultList.size() > 0) {
									createSB.append(" `").append(station).append("` DATETIME , ");
									insertSB.append("ifnull(").append("station_info").append("->>'$.\"").append(station).append("\"'").append(",null) as ").append(xwjUtil.addSQLCHAR(station)).append(",");
									testField.add(station);
								}
							}
						}
					}
				}
			}
			//預設都要有的欄位(`data_set_id`,`product`,`sn`,`value_rank`),用來當PK Key
			String[] pkField = new String[]{"data_set_id","product","sn","value_rank","scantime"};
			for(String pkf : pkField){
				if(!addField.contains(pkf)) {
					if (pkf.equals("scantime")) {
						createSB.append(" `").append(pkf).append("` varchar(").append(50).append(") , ");
						insertSB.append(xwjUtil.addSQLCHAR(pkf)).append(",");
					} else {
						createSB.append(" `").append(pkf).append("` varchar(").append(minvarchar).append(") NOT NULL, ");
						insertSB.append("ifnull(").append(xwjUtil.addSQLCHAR(pkf)).append(",null").append(") as ").append(xwjUtil.addSQLCHAR(pkf)).append(",");
					}
				}
			}
			//for(Map<String,Object> fieldObj : createField){
			for(int idx = 0 ; idx < createField.length() ; ++idx){
				JSONObject fieldObj = createField.getJSONObject(idx);
				String dtType = fieldObj.get("dataType").toString();
				String type = fieldObj.get("type").toString();
				String name = fieldObj.get("name").toString();
				String newname =  fieldObj.get("newname").toString();
				if(dtType.equals("string")){
					int startIdx = Integer.parseInt(fieldObj.get("start").toString());
					int len = Integer.parseInt(fieldObj.get("length").toString());
					if(type.equals("fixed")){
						createSB.append(" `").append(newname).append("` varchar(").append(minvarchar).append(") , ");
						insertSB.append("IFNULL(SUBSTRING(").append(xwjUtil.addSQLCHAR(name)).append(",").append(startIdx).append(",").append(len).append("),'') as ").append(xwjUtil.addSQLCHAR(newname)).append(",");
					}else{
						createSB.append(" `").append(newname).append("` varchar(").append(maxvarchar).append(") , ");
						insertSB.append("IFNULL(SUBSTRING(").append(type).append("->>'$.\"").append(name).append("\"'").append(",").append(startIdx).append(",").append(len).append("),null) as ").append(xwjUtil.addSQLCHAR(newname)).append(",");
					}
				}else{
					int times = Integer.parseInt(fieldObj.get("times").toString());
					int precision = Integer.parseInt(fieldObj.get("precision").toString());
					if(type.equals("fixed")){
						int size = minvarchar;
						if(name.equals("data_set_name"))
							size = maxvarchar;
						createSB.append(" `").append(newname).append("` varchar(").append(size).append(") , ");
						//insertSB.append("ifnull(").append(xwjUtil.addSQLCHAR(name)).append(",''").append(") as ").append(xwjUtil.addSQLCHAR(name)).append(",");
						//insertSB.append("ISNULL(SUBSTRING(").append(xwjUtil.addSQLCHAR(name)).append(",").append(startIdx).append(",").append(len).append("),'') as ").append(xwjUtil.addSQLCHAR(newname)).append(",");
						insertSB.append("CAST((IFNULL(").append(xwjUtil.addSQLCHAR(name)).append(",0)*").append(times).append(") as DECIMAL(9,").append(precision).append(")) as ").append(xwjUtil.addSQLCHAR(newname)).append(",");

					}else{
						createSB.append(" `").append(newname).append("` varchar(").append(maxvarchar).append(") , ");
						//insertSB.append("ifnull(").append(type).append("->>'$.\"").append(name.trim()).append("\"'").append(",'') as ").append(xwjUtil.addSQLCHAR(name)).append(",");
						//insertSB.append("ISNULL(SUBSTRING(").append(type).append("->>'$.\"").append(name).append("\"'").append(",").append(startIdx).append(",").append(len).append("),'') as ").append(xwjUtil.addSQLCHAR(newname)).append(",");
						insertSB.append("CAST((IFNULL(").append(type).append("->>'$.\"").append(name).append("\"'").append(",0)*").append(times).append(") as DECIMAL(9,").append(precision).append(")) as ").append(xwjUtil.addSQLCHAR(newname)).append(",");
					}
				}

			}
			createSB.append(" PRIMARY KEY (`data_set_id`,`product`,`sn`,`value_rank`)  )ENGINE=InnoDB;");
			String insertSQL = insertSB.substring(0,insertSB.toString().length()-1) + " FROM " + dataSetTableName;
			StringBuffer whereSb = new StringBuffer();
			whereSb.append(" WHERE 1=1 ");
			//for(Map<String, Object> field : conditionField)
			for(int idx = 0 ; idx < conditionField.length() ; ++idx) {
				JSONObject fieldObj = conditionField.getJSONObject(idx);
				whereSb.append(" AND ").append(xwjUtil.convertJSONToCondition(fieldObj));
			}
			//for(Map<String, Object> field : filterField)
			for(int idx = 0 ; idx < filterField.length() ; ++idx) {
				JSONObject fieldObj = filterField.getJSONObject(idx);
				whereSb.append(" AND ").append(xwjUtil. convertJSONToCondition(fieldObj));
			}
			mysqlJtl.execute(createSB.toString());

			/** 將資料塞入新的case分析表**/
			System.out.println(insertSQL + whereSb.toString());
			mysqlJtl.execute(insertSQL + whereSb.toString());

			/** 刪除 Case 舊分析表**/
			mysqlJtl.execute(" DROP TABLE IF EXISTS "+ oldTableName);

			/** 更名新Case分析檔**/
			mysqlJtl.execute("RENAME TABLE "+newTableName+" TO " + oldTableName);

			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			dbCase.setDataLastDate((new Date()));
			dbCase.setSettingJson(caseMsg.getSettingJson());
			//if(caseMsg.getName()==null || caseMsg.getName().equals("")) caseMsg.setName(dbCase.getName());
			//if(caseMsg.getRemark()==null || caseMsg.getRemark().equals("")) caseMsg.setRemark(dbCase.getRemark());
			dbCase = caseService.updateCase(currentUser,dbCase,caseMsg.getId());
//                dataMap.put(dbCase.getId(), dbCase.toCaseMsg(false, dataSetRepository));
			msg = "update case data successfully!!";
		}else{
			msg = "case not exist!!";
		}


//
		return dbCase;
	}

	public String setEncodeColumn(String sourceName,Long caseId,JdbcTemplate mysqlJtl){
		if(sourceName.length() > 64){
			String encodeName ="";
			try {
				MessageDigest md = MessageDigest.getInstance("MD5");
				md.update(sourceName.getBytes());
				encodeName = new BigInteger(1, md.digest()).toString(16);
				String searchSql = "select * from case_bigtable_column_mapping  where source_name ='"+sourceName+"' " +
						"and caseID = "+caseId ;
				List<Map<String, Object>> resultList = mysqlJtl.queryForList(searchSql);
				if(resultList.size() == 0) {
					String sql = "Insert into case_bigtable_column_mapping Values("
							+ caseId + ",'" + sourceName + "','" + encodeName + "','" + sourceName + "')";
					mysqlJtl.execute(sql);
				}
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			return encodeName;
		}
		else
			return sourceName;
	}
	public String getEncodeColumn(String sourceName,Long caseId,JdbcTemplate mysqlJtl){
			String encodeName ="";
			if(!sourceName.contains("@result")) {
				String sql = "select encode_name from " + xwjUtil.addSQLCHAR(mappingTable)
						+ " where caseID = " + caseId + " and source_name = '" + sourceName + "'";
				System.out.println(sql);
				List<Map<String, Object>> resultList = mysqlJtl.queryForList(sql);
				if (resultList.size() > 0)
					encodeName = resultList.get(0).get("encode_name").toString();
				else
					encodeName = sourceName;
			}
			else {
				String temp = sourceName.split("@result")[0];
				String sql = "select encode_name from " + xwjUtil.addSQLCHAR(mappingTable)
						+ " where caseID = " + caseId + " and source_name = '" + temp + "'";
				List<Map<String, Object>> resultList = mysqlJtl.queryForList(sql);
				if (resultList.size() > 0)
					encodeName = resultList.get(0).get("encode_name").toString() + "@result";
				else
					encodeName = sourceName;
			}
			return encodeName;
	}
	public String getDecodeColumn(String encodeName,Long caseId,JdbcTemplate mysqlJtl){
	    if(!encodeName.contains("@result")) {
            String sql = "select source_name from " + xwjUtil.addSQLCHAR(mappingTable)
                    + " where caseID = " + caseId + " and encode_name = '" + encodeName + "'";
            List<Map<String, Object>> resultList = mysqlJtl.queryForList(sql);
            if (resultList.size() > 0)
                return (String) resultList.get(0).get("source_name");
            else
                return encodeName;
        }
	    else{
	        String temp = encodeName.split("@result")[0];
            String sql = "select source_name from " + xwjUtil.addSQLCHAR(mappingTable)
                    + " where caseID = " + caseId + " and encode_name = '" + temp + "'";
            List<Map<String, Object>> resultList = mysqlJtl.queryForList(sql);
            if (resultList.size() > 0)
                return (String) resultList.get(0).get("source_name")+"@result";
            else
                return encodeName;
        }
	}


}
