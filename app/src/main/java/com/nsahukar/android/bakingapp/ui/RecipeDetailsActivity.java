package com.nsahukar.android.bakingapp.ui;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.google.android.exoplayer2.C;
import com.nsahukar.android.bakingapp.R;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.v4.app.NavUtils.navigateUpFromSameTask;

public class RecipeDetailsActivity extends AppCompatActivity
        implements IngredientsDetailFragment.OnFragmentInteractionListener,
        StepDetailFragment.OnFragmentInteractionListener {

    private static final String TAG = RecipeDetailsActivity.class.getSimpleName();
    private static final String EXTRA_RECIPE_NAME = "extra_recipe_name";
    private static final String EXTRA_INGREDIENTS_JSON_STRING = "extra_ingredients_json_string";
    private static final String EXTRA_STEPS_CONTENT_VALUES = "extra_steps_values";
    private static final String EXTRA_STEP_NUMBER = "extra_step_number";
    private static final String EXTRA_RESUME_WINDOW = "extra_resume_window";
    private static final String EXTRA_RESUME_POSITION = "extra_resume_position";
    private static final String STATE_STEP_NUMBER_VISIBLE = "state_step_number_visible";
    private static final String STATE_RESUME_WINDOW = "state_resume_window";
    private static final String STATE_RESUME_POSITION = "state_resume_position";
    private static final int RID_RECIPE_DETAILS_CONTAINER = R.id.recipe_details_container;

    private String mIngredientsJsonString;
    private ContentValues[] mSteps;
    private int mStepNumberVisible = -1;
    private int mResumeWindow;
    private long mResumePosition;
    @BindView(R.id.toolbar) Toolbar mToolbar;


    // Finish this activity if orientation landscape in a tablet
    private void finishIfTabletLandscape() {
        if (getResources().getBoolean(R.bool.is_tablet_landscape)) {
            Intent intent = getIntent();
            intent.putExtra(RecipeStepsActivity.EXTRA_STEP_NUMBER_VISIBLE, mStepNumberVisible);
            intent.putExtra(RecipeStepsActivity.EXTRA_RESUME_WINDOW, mResumeWindow);
            intent.putExtra(RecipeStepsActivity.EXTRA_RESUME_POSITION, mResumePosition);
            setResult(getResources().getInteger(R.integer.result_code_show_recipe_details_landscape),
                    intent);
            navigateUpFromSameTask(this);
        }
    }

    // Hide the system bars
    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
        );
    }

    // Show fragment based on step number visible
    // mStepNumberVisible == -1                  -----> IngredientsDetailFragment
    // mStepNumberVisible in [0:mSteps.length-1] -----> StepDetailFragment
    private void showNecessaryRecipeDetailFragment() {
        if (mStepNumberVisible == -1) {
            // show ingredients detail fragment
            showIngredientsDetailFragment();
        } else if (mStepNumberVisible > -1 && mStepNumberVisible < mSteps.length) {
            // show step detail fragment
            final boolean finalStep = mStepNumberVisible == mSteps.length - 1;
            ContentValues stepContentValues = mSteps[mStepNumberVisible];
            showStepDetailFragment(stepContentValues, finalStep);
        }
    }

    // clear resume position
    private void clearResumePosition() {
        mResumeWindow = C.INDEX_UNSET;
        mResumePosition = C.TIME_UNSET;
    }

    // Show next recipe detail
    private void showNextRecipeDetail() {
        mStepNumberVisible += 1;
        showNecessaryRecipeDetailFragment();
    }

    // Show previous recipe detail
    private void showPreviousRecipeDetail() {
        mStepNumberVisible -= 1;
        showNecessaryRecipeDetailFragment();
    }

    // Show ingredients detail fragment
    private void showIngredientsDetailFragment() {
        IngredientsDetailFragment fragment = IngredientsDetailFragment.
                getInstance(mIngredientsJsonString);
        getSupportFragmentManager().beginTransaction()
                .replace(RID_RECIPE_DETAILS_CONTAINER, fragment)
                .commit();
    }

    // Show step detail fragment
    private void showStepDetailFragment(ContentValues stepContentValues, boolean finalStep) {
        StepDetailFragment fragment = StepDetailFragment.getInstance(stepContentValues,
                finalStep, mResumeWindow, mResumePosition);
        clearResumePosition();
        getSupportFragmentManager().beginTransaction()
                .replace(RID_RECIPE_DETAILS_CONTAINER, fragment)
                .commit();
    }

    // static methods that return the prepared intent for this activity
    public static Intent getPreparedIntent(Context context, String recipeName,
                                           String ingredientsJsonString,
                                           ContentValues[] steps) {
        return getPreparedIntent(context, recipeName, ingredientsJsonString, steps, -1,
                C.INDEX_UNSET, C.TIME_UNSET);
    }

    public static Intent getPreparedIntent(Context context, String recipeName,
                                           String ingredientsJsonString,
                                           ContentValues[] steps,
                                           int stepNumber,
                                           int playerResumeWindow,
                                           long playerResumePosition) {
        Intent intent = new Intent(context, RecipeDetailsActivity.class);
        intent.putExtra(EXTRA_RECIPE_NAME, recipeName);
        intent.putExtra(EXTRA_INGREDIENTS_JSON_STRING, ingredientsJsonString);
        intent.putExtra(EXTRA_STEPS_CONTENT_VALUES, steps);
        intent.putExtra(EXTRA_STEP_NUMBER, stepNumber);
        intent.putExtra(EXTRA_RESUME_WINDOW, playerResumeWindow);
        intent.putExtra(EXTRA_RESUME_POSITION, playerResumePosition);
        return intent;
    }

    // Update resume window and position of ExoPlayer
    public void updateExoPlayerResumeWindowAndPosition(int resumeWindow, long resumePosition) {
        if (getResources().getBoolean(R.bool.is_tablet)) {
            mResumeWindow = resumeWindow;
            mResumePosition = resumePosition;
        }
    }


    // lifecycle methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(STATE_STEP_NUMBER_VISIBLE)) {
                mStepNumberVisible = savedInstanceState.getInt(STATE_STEP_NUMBER_VISIBLE);
            }
            if (savedInstanceState.containsKey(STATE_RESUME_WINDOW)) {
                mResumeWindow = savedInstanceState.getInt(STATE_RESUME_WINDOW);
            }
            if (savedInstanceState.containsKey(STATE_RESUME_POSITION)) {
                mResumePosition = savedInstanceState.getLong(STATE_RESUME_POSITION);
            }
        }
        finishIfTabletLandscape();
        ButterKnife.bind(this);

        // set up action bar
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            if (getResources().getBoolean(R.bool.is_landscape)) {
                // hide the status bar
                hideSystemUI();
                // hide the action bar
                actionBar.hide();
            } else {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }

        // get bundled extras (if any)
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey(EXTRA_RECIPE_NAME)) {
                if (actionBar != null) {
                    actionBar.setTitle(bundle.getString(EXTRA_RECIPE_NAME));
                }
            }
            if (bundle.containsKey(EXTRA_INGREDIENTS_JSON_STRING)) {
                mIngredientsJsonString = bundle.getString(EXTRA_INGREDIENTS_JSON_STRING);
            }
            if (bundle.containsKey(EXTRA_STEPS_CONTENT_VALUES)) {
                Parcelable[] parcelableSteps = bundle.getParcelableArray(EXTRA_STEPS_CONTENT_VALUES);
                if (parcelableSteps != null) {
                    mSteps = Arrays.copyOf(parcelableSteps, parcelableSteps.length, ContentValues[].class);
                }
            }
            if (savedInstanceState == null && bundle.containsKey(EXTRA_STEP_NUMBER)) {
                mStepNumberVisible = bundle.getInt(EXTRA_STEP_NUMBER, -1);
            }
            if (savedInstanceState == null && bundle.containsKey(EXTRA_RESUME_WINDOW)) {
                mResumeWindow = bundle.getInt(EXTRA_RESUME_WINDOW);
            }
            if (savedInstanceState == null && bundle.containsKey(EXTRA_RESUME_POSITION)) {
                mResumePosition = bundle.getLong(EXTRA_RESUME_POSITION);
            }
        }

        if (savedInstanceState == null) {
            // show necessary recipe detail fragment
            showNecessaryRecipeDetailFragment();
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
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_STEP_NUMBER_VISIBLE, mStepNumberVisible);
        outState.putInt(STATE_RESUME_WINDOW, mResumeWindow);
        outState.putLong(STATE_RESUME_POSITION, mResumePosition);
    }


    // OnFragmentInteractionListener methods
    @Override
    public void onStepsButtonClick() {
        showNextRecipeDetail();
    }

    @Override
    public void onPreviousDetailButtonClick() {
        showPreviousRecipeDetail();
    }

    @Override
    public void onNextDetailButtonClick() {
        showNextRecipeDetail();
    }
}
