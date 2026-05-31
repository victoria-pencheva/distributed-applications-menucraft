package com.recipes.util;

public class QuantityConverter {

    public static double toGrams(double quantity, String unit) {
        return "kg".equals(unit) ? quantity * 1000.0 : quantity;
    }

    public static double fromGrams(double grams, String targetUnit) {
        if ("kg".equals(targetUnit)) return Math.round(grams / 10.0) / 100.0;
        return (double) Math.round(grams);
    }
}
