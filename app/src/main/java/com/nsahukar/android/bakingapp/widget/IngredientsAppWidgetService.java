package com.nsahukar.android.bakingapp.widget;

import android.appwidget.AppWidgetManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.nsahukar.android.bakingapp.R;
import com.nsahukar.android.bakingapp.data.BakingAppContract;
import com.nsahukar.android.bakingapp.data.Ingredient;
import com.nsahukar.android.bakingapp.utils.BakingAppJsonUtils;

import org.json.JSONException;

public class IngredientsAppWidgetService extends RemoteViewsService {

    public static final String EXTRA_RECIPE_ID = "extra_recipe_id";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new IngredientsRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class IngredientsRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private static final String TAG = IngredientsRemoteViewsFactory.class.getSimpleName();

    private Context mContext;
    private int mAppWidgetId;
    private long mRecipeId;
    private ContentValues[] mIngredients;


    // Constructor
    public IngredientsRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        mRecipeId = intent.getLongExtra(IngredientsAppWidgetService.EXTRA_RECIPE_ID, 0);
    }

    // get ingredients content values from ingredients json string
    private ContentValues[] getIngredientsContentValues(String ingredientsJsonStr) {
        try {
            return BakingAppJsonUtils.getIngredientsContentValuesFromJson(ingredientsJsonStr);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        return null;
    }


    // RemoteViewsFactory interface methods
    @Override
    public void onCreate() {
        // Recipe with id content uri
        final Uri recipeWithIdContentUri = BakingAppContract.RecipesEntry.getRecipeWithIdUri(mRecipeId);
        // columns
        final String[] columns = new String[]{
                BakingAppContract.RecipesEntry.COLUMN_INGREDIENTS
        };
        final Cursor data = mContext.getContentResolver().query(recipeWithIdContentUri, columns, null, null, null);
        if (data != null) {
            data.moveToPosition(0);
            // ingredient json string
            final String ingredientsJsonString =
                    data.getString(data.getColumnIndex(BakingAppContract.RecipesEntry.COLUMN_INGREDIENTS));
            mIngredients = getIngredientsContentValues(ingredientsJsonString);
            data.close();
        }
    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (mIngredients != null) {
            return mIngredients.length;
        }
        return 0;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        // Construct remote views item based on the app widget item XML file,
        // and set the values on the position.
        ContentValues ingredientValues = mIngredients[position];
        Ingredient ingredient = new Ingredient(ingredientValues);

        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.ingredient_widget_item);
        rv.setTextViewText(R.id.tv_ingredient, ingredient.getName());
        final String measureString = String.format("%s %s", ingredient.getQuantity(),
                ingredient.getFriendlyMeasureString());
        rv.setTextViewText(R.id.tv_measure, measureString);
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}
