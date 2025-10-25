package com.wanderlust.WanderLust.entity.type;

import java.util.HashMap;
import java.util.Map;


public class CurrencyUtil {
    private static final Map<String,String> currMap=new HashMap<>();

    static {
        currMap.put("United States", "$");
        currMap.put("India", "₹");
        currMap.put("United Kingdom", "£");
        currMap.put("Japan", "¥");
    }
    public static String getCurrencySymbol(String country) {
        return currMap.getOrDefault(country, "$"); // default $
    }
}
