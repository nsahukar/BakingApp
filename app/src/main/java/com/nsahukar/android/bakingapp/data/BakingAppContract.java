package com.nsahukar.android.bakingapp.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Nikhil on 10/08/17.
 */

public class BakingAppContract {

    // Content authority - name for the entire content provider
    public static final String CONTENT_AUTHORITY = "bakingapp.android.nsahukar.com.bakingapp";

    // Using content authority to create base of all URIs, which the app will use to contact content
    // provider
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    // Recipes path
    public static final String PATH_RECIPES = "/recipes";
    public static final String PATH_RECIPE_WITH_ID = PATH_RECIPES + "/#";


    // Abstract base entry inner class
    private static abstract class BaseEntry implements BaseColumns {

        protected static Uri getContentUriWithPath(String path) {
            return BASE_CONTENT_URI.buildUpon().appendEncodedPath(path).build();
        }

    }


    // Inner class that defines the table contents of recipes table
    public static final class RecipesEntry extends BaseEntry {

        // Table name
        public static final String TABLE_NAME = "recipes";

        // Columns
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SERVINGS = "servings";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_INGREDIENTS = "ingredients";
        public static final String COLUMN_STEPS = "steps";

        // Content Uris

        // Base content uri used to query the recipes table from the content provider
        public static final Uri CONTENT_URI = getContentUriWithPath(PATH_RECIPES);

        // Get recipe with id uri
        public static final Uri getRecipeWithIdUri(long recipeId) {
            final String path = PATH_RECIPE_WITH_ID.replace("#", Long.toString(recipeId));
            return getContentUriWithPath(path);
        }

    }

}
