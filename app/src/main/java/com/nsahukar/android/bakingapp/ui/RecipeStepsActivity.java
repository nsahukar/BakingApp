package com.nsahukar.android.bakingapp.ui;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.nsahukar.android.bakingapp.R;
import com.nsahukar.android.bakingapp.adapter.RecipeStepsAdapter;
import com.nsahukar.android.bakingapp.data.BakingAppContract.RecipesEntry;
import com.nsahukar.android.bakingapp.utils.BakingAppJsonUtils;

import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.v4.app.NavUtils.navigateUpFromSameTask;

public class RecipeStepsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        RecipeStepsAdapter.OnItemClickListener {

    private static final String TAG = RecipeStepsActivity.class.getSimpleName();
    private static final String EXTRA_RECIPE_ID = "extra_recipe_id";
    private static final String EXTRA_RECIPE_NAME = "extra_recipe_name";
    public static final String EXTRA_STEP_NUMBER_VISIBLE = "extra_step_number_visible";
    private static final String STATE_STEP_NUMBER_VISIBLE = "state_step_number_visible";
    private static final int RID_DETAIL_CONTAINER = R.id.detail_container;

    private boolean mTwoPane;
    private int mStepNumberVisible = -1;
    private long mRecipeId;
    private String mRecipeName;
    private RecipeStepsAdapter mRecipeStepsAdapter;
    private String mIngredientsJsonString;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.rv_recipe_steps) RecyclerView mRecipeStepsRecyclerView;


    // Set recipe id
    private void setRecipeId(long recipeId) {
        mRecipeId = recipeId;
        if (recipeId != 0) {
            // launch CursorLoader to load recipe data from DB
            // which is to be displayed in this view
            setUpRecyclerView();
            getSupportLoaderManager().initLoader(R.integer.id_recipe_steps_loader, null, this);
        } else {
            throw new UnsupportedOperationException("Recipe Id required to show details of the recipe");
        }
    }

    // Set recipe name
    private void setRecipeName(String recipeName) {
        if (recipeName != null && mToolbar != null) {
            mRecipeName = recipeName;
            getSupportActionBar().setTitle(recipeName);
        }
    }

    // Set up recycler view
    private void setUpRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecipeStepsRecyclerView.setLayoutManager(linearLayoutManager);
        mRecipeStepsRecyclerView.setHasFixedSize(true);

        // set adapter to recycler view
        mRecipeStepsAdapter = new RecipeStepsAdapter(this);
        mRecipeStepsRecyclerView.setAdapter(mRecipeStepsAdapter);
    }

    // Show ingredients detail activity
    private void showIngredientsDetailActivity() {
        final Intent showRecipeDetailsActivityIntent =
                RecipeDetailsActivity.getPreparedIntent(
                        this,
                        mRecipeName,
                        mIngredientsJsonString,
                        mRecipeStepsAdapter.getSteps()
                );
        if (getResources().getBoolean(R.bool.is_tablet)) {
            int requestCode = getResources().
                    getInteger(R.integer.request_code_show_recipe_details_portrait);
            startActivityForResult(showRecipeDetailsActivityIntent,
                    requestCode);
        } else {
            startActivity(showRecipeDetailsActivityIntent);
        }
    }

    // Show step detail activity
    private void showStepDetailActivity(int stepPosition) {
        final Intent showRecipeDetailsActivityIntent =
                RecipeDetailsActivity.getPreparedIntent(
                        this,
                        mRecipeName,
                        mIngredientsJsonString,
                        mRecipeStepsAdapter.getSteps(),
                        stepPosition
                );
        if (getResources().getBoolean(R.bool.is_tablet)) {
            int requestCode = getResources().
                    getInteger(R.integer.request_code_show_recipe_details_portrait);
            startActivityForResult(showRecipeDetailsActivityIntent,
                    requestCode);
        } else {
            startActivity(showRecipeDetailsActivityIntent);
        }
    }

    // Show ingredients detail fragment
    private void showIngredientsDetailFragment() {
        IngredientsDetailFragment fragment = IngredientsDetailFragment.
                getInstance(mIngredientsJsonString);
        getSupportFragmentManager().beginTransaction()
                .replace(RID_DETAIL_CONTAINER, fragment)
                .commitAllowingStateLoss();
    }

    // Show step detail fragment
    private void showStepDetailFragment(ContentValues stepContentValues, boolean finalStep) {
        StepDetailFragment fragment = StepDetailFragment.getInstance(stepContentValues, finalStep);
        getSupportFragmentManager().beginTransaction()
                .replace(RID_DETAIL_CONTAINER, fragment)
                .commitAllowingStateLoss();
    }


    // Static method that returns the prepared intent for this activity
    public static Intent getPreparedIntent(Context context, final long recipeId, final String recipeName) {
        Intent intent = new Intent(context, RecipeStepsActivity.class);
        intent.putExtra(EXTRA_RECIPE_ID, recipeId);
        intent.putExtra(EXTRA_RECIPE_NAME, recipeName);
        return intent;
    }


    // lifecycle methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_steps);
        ButterKnife.bind(this);

        // set up action bar
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (findViewById(RID_DETAIL_CONTAINER) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        // Check for the previously saved states
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(STATE_STEP_NUMBER_VISIBLE)) {
                mStepNumberVisible = savedInstanceState.getInt(STATE_STEP_NUMBER_VISIBLE);
            }
        }

        // get bundled extras (if any)
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey(EXTRA_RECIPE_ID)) {
                // set up the recycler view - master view
                setRecipeId(bundle.getLong(EXTRA_RECIPE_ID));
            }
            if (bundle.containsKey(EXTRA_RECIPE_NAME)) {
                // set title on toolbar - recipe name
                setRecipeName(bundle.getString(EXTRA_RECIPE_NAME));
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (getResources().getBoolean(R.bool.is_tablet)) {
            outState.putInt(STATE_STEP_NUMBER_VISIBLE, mStepNumberVisible);
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == getResources().getInteger(R.integer.request_code_show_recipe_details_portrait)) {
            if (resultCode == getResources().getInteger(R.integer.result_code_show_recipe_details_landscape)) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    if (bundle.containsKey(EXTRA_STEP_NUMBER_VISIBLE)) {
                        mStepNumberVisible = bundle.getInt(EXTRA_STEP_NUMBER_VISIBLE);
                        if (mStepNumberVisible == -1) {
                            onClickIngredients();
                        } else if (mStepNumberVisible > -1) {
                            onClickStep(mStepNumberVisible);
                        }
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // LoaderCallback methods
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case R.integer.id_recipe_steps_loader:
                // Recipe with id content uri
                final Uri recipeWithIdContentUri = RecipesEntry.getRecipeWithIdUri(mRecipeId);
                // columns
                final String[] columns = new String[]{
                        RecipesEntry._ID,
                        RecipesEntry.COLUMN_IMAGE,
                        RecipesEntry.COLUMN_NAME,
                        RecipesEntry.COLUMN_INGREDIENTS,
                        RecipesEntry.COLUMN_STEPS,
                        RecipesEntry.COLUMN_SERVINGS
                };
                return new CursorLoader(this, recipeWithIdContentUri, columns, null, null, null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            try {
                data.moveToPosition(0);

                // steps content values
                final String stepsJsonStr =
                        data.getString(data.getColumnIndex(RecipesEntry.COLUMN_STEPS));
                ContentValues[] stepsContentValues = BakingAppJsonUtils.
                        getStepsContentValuesFromJson(stepsJsonStr);

                mRecipeStepsAdapter.swapContentValues(stepsContentValues);

                // ingredient json string
                mIngredientsJsonString =
                        data.getString(data.getColumnIndex(RecipesEntry.COLUMN_INGREDIENTS));

                if (getResources().getBoolean(R.bool.is_tablet)) {
                    if (mStepNumberVisible == -1) {
                        onClickIngredients();
                    } else if (mStepNumberVisible > -1) {
                        onClickStep(mStepNumberVisible);
                    }
                }
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecipeStepsAdapter.swapContentValues(null);
    }


    // RecipeStepsAdapter OnItemClickListener method
    @Override
    public void onClickIngredients() {
        mStepNumberVisible = -1;
        if (mTwoPane) {
            showIngredientsDetailFragment();
        } else {
            showIngredientsDetailActivity();
        }
    }

    @Override
    public void onClickStep(int position) {
        mStepNumberVisible = position;
        if (mTwoPane) {
            ContentValues stepContentValues = mRecipeStepsAdapter.getSteps()[position];
            boolean finalStep = mRecipeStepsAdapter.isFinalStep(position);
            showStepDetailFragment(stepContentValues, finalStep);
        } else {
            showStepDetailActivity(position);
        }
    }

}
