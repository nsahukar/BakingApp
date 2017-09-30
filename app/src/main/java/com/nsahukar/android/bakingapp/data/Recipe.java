package com.nsahukar.android.bakingapp.data;

import android.content.ContentValues;

import com.nsahukar.android.bakingapp.data.BakingAppContract.RecipesEntry;


/**
 * Created by Nikhil on 12/08/17.
 */

public class Recipe {

    public static final String ID = RecipesEntry._ID;
    public static final String NAME = RecipesEntry.COLUMN_NAME;
    public static final String SERVINGS = RecipesEntry.COLUMN_SERVINGS;
    public static final String IMAGE = RecipesEntry.COLUMN_IMAGE;
    public static final String INGREDIENTS = RecipesEntry.COLUMN_INGREDIENTS;
    public static final String STEPS = RecipesEntry.COLUMN_STEPS;

    private ContentValues mRecipeContentValues;

    public Recipe(ContentValues recipeContentValues) {
        mRecipeContentValues = recipeContentValues;
    }


    // Getters
    public long getId() {
        return mRecipeContentValues.getAsLong(ID);
    }

    public String getName() {
        return mRecipeContentValues.getAsString(NAME);
    }

    public int getServings() {
        return mRecipeContentValues.getAsInteger(SERVINGS);
    }

    public String getImage() {
        return mRecipeContentValues.getAsString(IMAGE);
    }

    public String getIngredients() {
        return mRecipeContentValues.getAsString(INGREDIENTS);
    }

    public String getSteps() {
        return mRecipeContentValues.getAsString(STEPS);
    }

}
