package com.nsahukar.android.bakingapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nsahukar.android.bakingapp.data.BakingAppContract.RecipesEntry;

import static com.nsahukar.android.bakingapp.data.BakingAppContract.CONTENT_AUTHORITY;
import static com.nsahukar.android.bakingapp.data.BakingAppContract.PATH_RECIPES;
import static com.nsahukar.android.bakingapp.data.BakingAppContract.PATH_RECIPE_WITH_ID;

/**
 * Created by Nikhil on 10/08/17.
 */

public class BakingAppProvider extends ContentProvider {

    // These constants will be used to match URIs with the data they are looking for
    public static final int CODE_RECIPES = 100;
    public static final int CODE_RECIPE_WITH_ID = 101;

    // URI matcher that will match each uri to the constants defined above
    public static UriMatcher buildUriMatcher() {

        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CONTENT_AUTHORITY;

        // recipes uri - content://<pkg-name>/recipes
        uriMatcher.addURI(authority, PATH_RECIPES, CODE_RECIPES);
        // recipe with id uri - content://<pkg-name>/recipe/#
        uriMatcher.addURI(authority, PATH_RECIPE_WITH_ID, CODE_RECIPE_WITH_ID);

        return uriMatcher;
    }

    public static final UriMatcher sUriMatcher = buildUriMatcher();
    private BakingAppDbHelper mDbHelper;


    // Content provider methods

    @Override
    public boolean onCreate() {
        mDbHelper = new BakingAppDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] columns, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        String tableName;
        switch (sUriMatcher.match(uri)) {

            case CODE_RECIPES: {
                tableName = RecipesEntry.TABLE_NAME;
                break;
            }
            case CODE_RECIPE_WITH_ID: {
                tableName = RecipesEntry.TABLE_NAME;
                selection = RecipesEntry._ID + "=?";
                final String recipeId = uri.getLastPathSegment();
                selectionArgs = new String[]{recipeId};
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }

        final SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.query(tableName, columns, selection, selectionArgs, null, null, sortOrder);
        if (cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {

        String tableName;
        switch (sUriMatcher.match(uri)) {

            case CODE_RECIPES:
                tableName = RecipesEntry.TABLE_NAME;
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.beginTransaction();
        int rowsInserted = 0;
        try {
            for (ContentValues value : values) {
                long _id = db.insert(tableName, null, value);
                if (_id != -1) {
                    rowsInserted++;
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        if (rowsInserted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsInserted;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        String tableName;
        switch (sUriMatcher.match(uri)) {

            case CODE_RECIPES:
                tableName = RecipesEntry.TABLE_NAME;
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri : " + uri);

        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        if (null == selection) selection = "1";
        int numRowsDeleted = db.delete(tableName, selection, selectionArgs);
        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

}
