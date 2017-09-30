package com.nsahukar.android.bakingapp.utils;

import java.util.HashMap;

/**
 * Created by Nikhil on 12/08/17.
 */

public class BakingAppUtils {

    private static final HashMap<String, String> sIngredientMeasureMap = buildIngredientMeasureMap();

    private static HashMap<String, String> buildIngredientMeasureMap() {
        HashMap<String, String> measureMap = new HashMap<>();
        measureMap.put("CUP", "cup");
        measureMap.put("TBLSP", "tablespoon");
        measureMap.put("TSP", "teaspoon");
        measureMap.put("K", "kg");
        measureMap.put("G", "grams");
        measureMap.put("OZ", "ounces");
        measureMap.put("UNIT", "unit");
        return measureMap;
    }

    public static String getFriendlyMeasureStringForString(String measure) {
        return sIngredientMeasureMap.get(measure);
    }
}
