package com.nsahukar.android.bakingapp.utils;

import android.content.ContentValues;

import com.nsahukar.android.bakingapp.data.Ingredient;
import com.nsahukar.android.bakingapp.data.Recipe;
import com.nsahukar.android.bakingapp.data.Step;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Nikhil on 12/08/17.
 */

public class BakingAppJsonUtils {

    public static ContentValues[] getRecipesContentValuesFromJson(String recipesJsonString) throws JSONException {

        JSONArray recipesJsonArr = new JSONArray(recipesJsonString);
        ContentValues[] recipesContentValues = new ContentValues[recipesJsonArr.length()];
        for (int i = 0; i < recipesJsonArr.length(); i++) {
            JSONObject recipeJsonObject = recipesJsonArr.getJSONObject(i);

            ContentValues recipeCV = new ContentValues();
            recipeCV.put(Recipe.ID, recipeJsonObject.getLong("id"));
            recipeCV.put(Recipe.NAME, recipeJsonObject.getString(Recipe.NAME));
            recipeCV.put(Recipe.SERVINGS, recipeJsonObject.getString(Recipe.SERVINGS));
            recipeCV.put(Recipe.IMAGE, recipeJsonObject.getString(Recipe.IMAGE));
            recipeCV.put(Recipe.INGREDIENTS, recipeJsonObject.getString(Recipe.INGREDIENTS));
            recipeCV.put(Recipe.STEPS, recipeJsonObject.getString(Recipe.STEPS));

            recipesContentValues[i] = recipeCV;
        }

        return recipesContentValues;

    }

    public static ContentValues[] getIngredientsContentValuesFromJson(String ingredientsJsonString) throws JSONException {

        JSONArray ingredientsJsonArr = new JSONArray(ingredientsJsonString);
        ContentValues[] ingredientsContentValues = new ContentValues[ingredientsJsonArr.length()];
        for (int i = 0; i < ingredientsJsonArr.length(); i++) {
            JSONObject ingredientJsonObject = ingredientsJsonArr.getJSONObject(i);

            ContentValues ingredientCV = new ContentValues();
            ingredientCV.put(Ingredient.NAME, ingredientJsonObject.getString(Ingredient.NAME));
            ingredientCV.put(Ingredient.QUANTITY, ingredientJsonObject.getString(Ingredient.QUANTITY));
            ingredientCV.put(Ingredient.MEASURE, ingredientJsonObject.getString(Ingredient.MEASURE));

            ingredientsContentValues[i] = ingredientCV;
        }

        return ingredientsContentValues;

    }

    public static ContentValues[] getStepsContentValuesFromJson(String stepsJsonString) throws JSONException {

        JSONArray stepsJsonArr = new JSONArray(stepsJsonString);
        ContentValues[] stepsContentValues = new ContentValues[stepsJsonArr.length()];
        for (int i = 0; i < stepsJsonArr.length(); i++) {
            JSONObject stepJsonObject = stepsJsonArr.getJSONObject(i);

            ContentValues stepCV = new ContentValues();
            stepCV.put(Step.ID, stepJsonObject.getLong(Step.ID));
            stepCV.put(Step.DESCRIPTION, stepJsonObject.getString(Step.DESCRIPTION));
            stepCV.put(Step.SHORT_DESCRIPTION, stepJsonObject.getString(Step.SHORT_DESCRIPTION));
            stepCV.put(Step.VIDEO_URL, stepJsonObject.getString(Step.VIDEO_URL));
            stepCV.put(Step.THUMBNAIL_URL, stepJsonObject.getString(Step.THUMBNAIL_URL));

            stepsContentValues[i] = stepCV;
        }

        return stepsContentValues;

    }

}
