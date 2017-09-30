package com.nsahukar.android.bakingapp.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import com.nsahukar.android.bakingapp.data.BakingAppContract.RecipesEntry;
import com.nsahukar.android.bakingapp.utils.BakingAppJsonUtils;
import com.nsahukar.android.bakingapp.utils.BakingAppNetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;


/**
 * Created by Nikhil on 12/08/17.
 */

public class BakingAppSyncTask {

    synchronized public static void syncRecipes(Context context) {

        try {
            // Get recipes url
            URL recipesURL = new URL(BakingAppNetworkUtils.getRecipesUrl());

            // Use the recipes url to retrieve the JSON
            final String recipesJSONResponse = BakingAppNetworkUtils.getResponseFromHttpUrl(recipesURL);

            // Parse the JSON into a list of recipes content values
            ContentValues[] recipesContentValues = BakingAppJsonUtils.getRecipesContentValuesFromJson(recipesJSONResponse);

            if (recipesContentValues != null && recipesContentValues.length > 0) {

                // Get a handle on content resolver to delete and insert data
                ContentResolver bakingAppContentResolver = context.getContentResolver();

                // Delete old recipes
                bakingAppContentResolver.delete(RecipesEntry.CONTENT_URI, null, null);

                // Insert our new recipes into the content provider
                bakingAppContentResolver.bulkInsert(RecipesEntry.CONTENT_URI, recipesContentValues);

            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

    }

}
