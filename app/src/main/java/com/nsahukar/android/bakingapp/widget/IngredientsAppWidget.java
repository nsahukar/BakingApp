package com.nsahukar.android.bakingapp.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.nsahukar.android.bakingapp.R;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link IngredientsAppWidgetConfigureActivity IngredientsAppWidgetConfigureActivity}
 */
public class IngredientsAppWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Instantiate the RemoteViews object for the app widget layout.
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.ingredients_app_widget);

        // Set up the recipe name
        final String recipeName = IngredientsAppWidgetConfigureActivity.
                loadRecipeNamePref(context, appWidgetId);
        rv.setTextViewText(R.id.tv_recipe_name, context.getString(R.string.title_ingredients) +
                " - " + recipeName);

        // Set up the intent that starts the IngredientsAppWidgetService, which will
        // provide the views for ingredients list.
        Intent intent = new Intent(context, IngredientsAppWidgetService.class);
        // Add the app widget ID and recipe ID to the intent extras.
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        final long recipeId = IngredientsAppWidgetConfigureActivity.
                loadRecipeIdPref(context, appWidgetId);
        intent.putExtra(IngredientsAppWidgetService.EXTRA_RECIPE_ID, recipeId);

        // Set up the RemoteViews object to use a RemoteViews adapter.
        // This adapter connects
        // to a RemoteViewsService  through the specified intent.
        // This is how you populate the data.
        rv.setRemoteAdapter(R.id.app_widget_ingredients_list, intent);

        appWidgetManager.updateAppWidget(appWidgetId, rv);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            IngredientsAppWidgetConfigureActivity.deleteRecipePrefs(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

