package com.foxconn.iisd.rcadsvc.util;


import com.foxconn.iisd.rcadsvc.msg.RiskType;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class FormatUtils {

    private static String DEFAULT_TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm";

    private static String DEFAULT_TIME_FORMAT = "HH:mm:ss";

    private static String WYJ_RISK_ASSEMBLY = "riskAssemblyBy";

    private static String WYJ_RISK_PART = "relatedMaterial";

    private static String WYJ_RISK_TEST = "riskStation";


    public static String timestampToString(Timestamp timestamp) {
        return new SimpleDateFormat(DEFAULT_TIMESTAMP_FORMAT).format(timestamp);
    }

    public static String dateTimeToTimeString(LocalDateTime dt) {
        return DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT).format(dt);
    }

    public static String dateTimeToTimeString(ZonedDateTime dt) {
        return DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT).format(dt);
    }

    public static String dateTimeToString(LocalDateTime now) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_TIMESTAMP_FORMAT);
        return now.format(formatter);
    }

    public static String dateTimeToString(ZonedDateTime now) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_TIMESTAMP_FORMAT);
        return now.format(formatter);
    }

    public static LocalDateTime timeStringToLocalDateTime(String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_TIMESTAMP_FORMAT);
        return LocalDateTime.parse(time, formatter);
    }

    public static String wyjRiskType(String riskType) {
        switch (RiskType.fromText(riskType)) {
            case ASSEMBLY:
                return WYJ_RISK_ASSEMBLY;
            case PART:
                return WYJ_RISK_PART;
            case TEST:
                return WYJ_RISK_TEST;
            default:
                return null;
        }
    }
}
