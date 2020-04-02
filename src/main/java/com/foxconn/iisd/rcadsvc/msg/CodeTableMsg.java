package com.foxconn.iisd.rcadsvc.msg;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.foxconn.iisd.rcadsvc.domain.CodeTable;

public class CodeTableMsg {

	private Long id;
	
    private String codeProduct;
    
    private String codeCategory;

    private String code;
    
    private String codeName;

    private String createUser;

    private String createTime;

    private String modifyUser;
    
    private String modifyTime;

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
    
	public String getCodeProduct() {
		return codeProduct;
	}

	public void setCodeProduct(String codeProduct) {
		this.codeProduct = codeProduct;
	}

	public String getCodeCategory() {
		return codeCategory;
	}

	public void setCodeCategory(String codeCategory) {
		this.codeCategory = codeCategory;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCodeName() {
		return codeName;
	}

	public void setCodeName(String codeName) {
		this.codeName = codeName;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getModifyUser() {
		return modifyUser;
	}

	public void setModifyUser(String modifyUser) {
		this.modifyUser = modifyUser;
	}

	public String getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(String modifyTime) {
		this.modifyTime = modifyTime;
	}

	public CodeTable toNewDDS(){
		DateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm");
		CodeTable newDss = new CodeTable();
		newDss.setCodeProduct(this.getCodeProduct());
		newDss.setCodeCategory(this.getCodeCategory());
		newDss.setCode(this.getCode());
		newDss.setCodeName(this.getCodeName());
		newDss.setCreateUser(this.getCreateUser());
        newDss.setModifyUser(this.getModifyUser());

        if(this.getCreateTime()==null){
        	newDss.setCreateTime(null);
        }else{
	        try{
	        	newDss.setCreateTime(sdf.parse(this.getCreateTime()));
	        }catch(ParseException e){
	        	//none
	        }
        }
        
        if(this.getModifyTime()==null){
        	newDss.setModifyTime(null);
        }else{
	        try{
	        	newDss.setModifyTime(sdf.parse(this.getModifyTime()));
	        }catch(ParseException e){
	        	//none
	        }
        }
        return newDss;
    }
}
