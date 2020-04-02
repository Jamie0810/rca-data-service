package com.foxconn.iisd.rcadsvc.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.Map;

public class WYJAlertListComparator implements Comparator<Map<String, Object>> {
    @Override
    public int compare(Map<String, Object> o1, Map<String, Object> o2) {
        BigDecimal failRate1 = new BigDecimal((String) o1.get("failRate"));
        BigDecimal failRate2 = new BigDecimal((String) o2.get("failRate"));
        int failRateComp = failRate1.compareTo(failRate2);
        if (failRateComp != 0) {
            return -failRateComp;
        }

        BigInteger defectQty1 = new BigInteger((String) o1.get("testStationFail"));
        BigInteger defectQty2 = new BigInteger((String) o2.get("testStationFail"));

        int sigComp = defectQty1.compareTo(defectQty2);
        if (sigComp != 0) {
            return -sigComp;
        }

        String station1 = (String) o1.get("testStation");
        String stattion2 = (String) o2.get("testStation");
        int stationComp = station1.compareTo(stattion2);
        if (stationComp != 0) {
            return stationComp;
        }

        String symp1 = (String) o1.get("failureSymptom");
        String symp2 = (String) o2.get("failureSymptom");
        return symp1.compareTo(symp2);
    }
}
