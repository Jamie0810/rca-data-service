package com.foxconn.iisd.rcadsvc.msg;


public class CaseMatrixFieldMsg {

    private String dataType;

    private String name;

    private String type;
    
    private Double testUpper;
    
    private Double testLower;

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

	public Double getTestUpper() {
		return testUpper;
	}

	public void setTestUpper(Double testUpper) {
		this.testUpper = testUpper;
	}

	public Double getTestLower() {
		return testLower;
	}

	public void setTestLower(Double testLower) {
		this.testLower = testLower;
	}
}
