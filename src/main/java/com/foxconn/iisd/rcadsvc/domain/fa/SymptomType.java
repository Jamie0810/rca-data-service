package com.foxconn.iisd.rcadsvc.domain.fa;

public enum SymptomType {

    EXISTING("E"),
    MANUAL("A");

    private String value;

    SymptomType(String value) {
        this.value = value;
    }
}
