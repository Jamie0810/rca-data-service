package com.foxconn.iisd.rcadsvc.msg;

import java.util.Arrays;

public enum ActionType {
    CREATE("create"),
    DELETE("delete"),
    NONE("none");

    private String text;

    ActionType(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public static ActionType fromText(String text) {
        return Arrays.stream(values())
                .filter(rt -> rt.getText().equalsIgnoreCase(text))
                .findFirst()
                .orElse(null);
    }
}
