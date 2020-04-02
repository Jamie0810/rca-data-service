package com.foxconn.iisd.rcadsvc.msg;

import java.util.Arrays;

public enum CommonalityAccessType {

    CONFIRMED("confirmed"),
    DATALACK("datalack"),
    NEGATIVE("negative");

    private String text;

    CommonalityAccessType(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public static CommonalityAccessType fromText(String text) {
        return Arrays.stream(values())
                .filter(rt -> rt.getText().equalsIgnoreCase(text))
                .findFirst()
                .orElse(null);
    }
}
