package com.foxconn.iisd.rcadsvc.util;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Map;

public class FDJL2QueryResultComparator implements Comparator<Map<String, Object>> {
    @Override
    public int compare(Map<String, Object> o1, Map<String, Object> o2) {
        BigDecimal riskpoint1 = new BigDecimal((String) o1.get("riskPoint"));
        BigDecimal riskpoint2 = new BigDecimal((String) o2.get("riskPoint"));
        int riskComp = riskpoint1.compareTo(riskpoint2);
        if (riskComp != 0) {
            return -riskComp;
        }

        BigDecimal sig1 = new BigDecimal((String) o1.get("significant"));
        BigDecimal sig2 = new BigDecimal((String) o2.get("significant"));

        int sigComp = sig1.compareTo(sig2);
        if (sigComp != 0) {
            return -sigComp;
        }

        BigDecimal comm1 = new BigDecimal((String) o1.get("commonality"));
        BigDecimal comm2 = new BigDecimal((String) o2.get("commonality"));

        int commComp = comm1.compareTo(comm2);
        if (commComp != 0) {
            return -commComp;
        }

        String code1 = (String) o1.get("riskCode");
        String code2 = (String) o2.get("riskCode");
        return code1.compareTo(code2);
    }
}
