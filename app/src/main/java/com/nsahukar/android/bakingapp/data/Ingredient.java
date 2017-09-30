package com.nsahukar.android.bakingapp.data;

import android.content.ContentValues;

import com.nsahukar.android.bakingapp.utils.BakingAppUtils;

/**
 * Created by Nikhil on 12/08/17.
 */

public class Ingredient {

    public static final String NAME = "ingredient";
    public static final String QUANTITY = "quantity";
    public static final String MEASURE = "measure";

    private ContentValues mIngredientContentValues;

    public Ingredient(ContentValues ingredientContentValues) {
        mIngredientContentValues = ingredientContentValues;
    }

    // Getters
    public String getName() {
        // returning name with first letter as uppercase
        String name = mIngredientContentValues.getAsString(NAME);
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public String getQuantity() {
        return mIngredientContentValues.getAsString(QUANTITY);
    }

    public String getMeasure() {
        return mIngredientContentValues.getAsString(MEASURE);
    }

    public String getFriendlyMeasureString() {
        return BakingAppUtils.getFriendlyMeasureStringForString(getMeasure());
    }

}
