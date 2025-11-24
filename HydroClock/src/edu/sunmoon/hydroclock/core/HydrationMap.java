package edu.sunmoon.hydroclock.core;

import java.util.LinkedHashMap;
import java.util.Map;

public class HydrationMap {
    private static final LinkedHashMap<String, Double> MAP = new LinkedHashMap<>();
    static {
        MAP.put("물", 1.00);
        MAP.put("커피(아메리카노)", 0.95);
        MAP.put("차(녹차/홍차)", 0.98);
        MAP.put("우유", 0.95);
        MAP.put("주스", 0.90);
        MAP.put("탄산음료", 0.85);
        MAP.put("에너지드링크", 0.80);
    }
    public static Map<String, Double> all() { return MAP; }
    public static double k(String label) { return MAP.getOrDefault(label, 1.0); }
}
