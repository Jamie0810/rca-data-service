package com.foxconn.iisd.rcadsvc.msg;

import java.util.Arrays;

public enum RiskType {

    ASSEMBLY("assembly"),
    PART("part"),
    TEST("test");

    private String text;

    RiskType(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public static RiskType fromText(String text) {
        return Arrays.stream(values())
                .filter(rt -> rt.getText().equalsIgnoreCase(text))
                .findFirst()
                .orElse(null);
    }
}
