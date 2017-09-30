package com.nsahukar.android.bakingapp.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.nsahukar.android.bakingapp.R;
import com.nsahukar.android.bakingapp.adapter.RecipesAdapter;
import com.nsahukar.android.bakingapp.data.BakingAppContract;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * The configuration screen for the {@link IngredientsAppWidget IngredientsAppWidget} AppWidget.
 */
public class IngredientsAppWidgetConfigureActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        RecipesAdapter.OnItemClickListener {

    private static final String PREFS_NAME = "com.nsahukar.android.bakingapp.widget.IngredientsAppWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    private static final String PREF_KEY_RECIPE_ID = "_recipe_id";
    private static final String PREF_KEY_RECIPE_NAME = "_recipe_name";


    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.rv_recipes) RecyclerView mRecipesRecyclerView;
    private RecipesAdapter mRecipesAdapter;
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;


    // Write the SharedPreferences object for the widget
    static void saveRecipePrefs(Context context, int appWidgetId, long recipeId, String recipeName) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putLong(PREF_PREFIX_KEY + appWidgetId + PREF_KEY_RECIPE_ID, recipeId);
        prefs.putString(PREF_PREFIX_KEY + appWidgetId + PREF_KEY_RECIPE_NAME, recipeName);
        prefs.apply();
    }

    // Read the recipe id from the SharedPreferences object for the widget.
    static long loadRecipeIdPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getLong(PREF_PREFIX_KEY + appWidgetId + PREF_KEY_RECIPE_ID, 0);
    }

    // Read the recipe name from the SharedPreferences object for the widget.
    static String loadRecipeNamePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getString(PREF_PREFIX_KEY + appWidgetId + PREF_KEY_RECIPE_NAME, null);
    }

    static void deleteRecipePrefs(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId + PREF_KEY_RECIPE_ID);
        prefs.remove(PREF_PREFIX_KEY + appWidgetId + PREF_KEY_RECIPE_NAME);
        prefs.apply();
    }

    // Set up recycler view
    private void setUpRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecipesRecyclerView.setLayoutManager(linearLayoutManager);
        mRecipesRecyclerView.setHasFixedSize(true);

        // set adapter to recycler view
        mRecipesAdapter = new RecipesAdapter(this, RecipesAdapter.VIEW_TYPE_LIST);
        mRecipesRecyclerView.setAdapter(mRecipesAdapter);
    }


    // lifecycle methods
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.ingredients_app_widget_configure);
        ButterKnife.bind(this);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setTitle(getString(R.string.title_ingredients_app_widget_config_activity));
        }
        setUpRecyclerView();
        getSupportLoaderManager().initLoader(R.integer.id_recipes_loader, null, this);
    }


    // LoaderCallback methods
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case R.integer.id_recipes_loader:
                // Recipes content uri
                final Uri RecipesContentUri = BakingAppContract.RecipesEntry.CONTENT_URI;
                // columns
                final String[] columns = new String[]{
                        BakingAppContract.RecipesEntry._ID,
                        BakingAppContract.RecipesEntry.COLUMN_NAME
                };
                // sort order
                final String sortOrder = BakingAppContract.RecipesEntry._ID;
                return new CursorLoader(this, RecipesContentUri, columns, null, null, sortOrder);

            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            // swap the cursor of the recipe adapter
            mRecipesAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecipesAdapter.swapCursor(null);
    }


    // RecipesAdapter OnItemClickListener method
    @Override
    public void onClick(long recipeId, String recipeName) {
        // save recipe id preference
        saveRecipePrefs(this, mAppWidgetId, recipeId, recipeName);
        // update the app widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        IngredientsAppWidget.updateAppWidget(this, appWidgetManager, mAppWidgetId);
        // Set RESULT_OK and finish activity
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }

}

