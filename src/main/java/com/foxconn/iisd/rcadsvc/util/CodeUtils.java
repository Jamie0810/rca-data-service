package com.foxconn.iisd.rcadsvc.util;

import com.foxconn.iisd.rcadsvc.domain.CodeTable;
import com.foxconn.iisd.rcadsvc.repo.CodeTableRepository;

public class CodeUtils {
	/**
	產品代碼：通用=general、產品代碼清單=product（除了產品清單外，再加上一個特殊類別，無產品代碼時，以取用通用設定）
	代碼類別：不良徵狀=1、廠商代碼=2、Date code=3、物料=4、XWJ通用欄位=5（類別固定，以下拉供設定）
	代碼：文字輸入
	*/
    public static String getCodeName(CodeTableRepository codeTableRepository, String codeProduct, String codeCategory, String code, String riskType){
        String returnCode = code;
        try{
        	if("1".equals(codeCategory) || "part".equals(riskType)){
	        	if("2_3".equals(codeCategory)){
	        		String[] codeArray = code.split("_");
	        		if(codeArray.length == 2){
	        			String factory = codeArray[0];
	        			String item = codeArray[1];
	        			try{
	        				CodeTable codeTable1 = codeTableRepository.findByCodeProductAndCodeCategoryAndCode(codeProduct, "2", codeArray[0]);
	        				if(codeTable1!=null){
	        					factory = codeTable1.getCodeName();
	        				}
	        			}catch(Exception e){
	        				e.printStackTrace();
	        			}
	        			try{
	        				CodeTable codeTable2 = codeTableRepository.findByCodeProductAndCodeCategoryAndCode(codeProduct, "3", codeArray[1]);
	        				if(codeTable2!=null){
	        					item = codeTable2.getCodeName();
	        				}
	        			}catch(Exception e){
	        				e.printStackTrace();
	        			}
	        			returnCode = factory + "_" + item;
	        		}
	        	}else{
	        		CodeTable codeTable = codeTableRepository.findByCodeProductAndCodeCategoryAndCode(codeProduct, codeCategory, code);
	        		if(codeTable!=null){
	        			returnCode = codeTable.getCodeName();
	        		}
	        	}
        	}
        }catch(Exception e){
        	e.printStackTrace();
        }
        return returnCode;
    }
}
