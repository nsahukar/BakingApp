package com.nsahukar.android.bakingapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.nsahukar.android.bakingapp.R;
import com.nsahukar.android.bakingapp.matcher.RecyclerViewMatcher;
import com.nsahukar.android.bakingapp.ui.RecipeStepsActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;

/**
 * Created by Nikhil on 28/09/17.
 */

@RunWith(AndroidJUnit4.class)
public class RecipeStepsActivityTest {

    /**
     * Test prerequisites
     */

    // Recipe id for "Brownies"
    private static final int TEST_EXTRA_RECIPE_ID = 2;
    // Recipe name
    private static final String TEST_EXTRA_RECIPE_NAME = "Brownies";
    // List position for "ingredients"
    public static final int TEST_LIST_POSITION_0 = 0;
    // List position for recipe step "Add eggs"
    public static final int TEST_LIST_POSITION_7 = 7;

    // List position for ingredient - "Large Eggs"
    public static final int TEST_LIST_POSITION_4 = 4;

    /**
     * Texts to assert the result of a test
     */

    public static final String TEXT_INGREDIENTS = "Ingredients";
    public static final String TEXT_LARGE_EGGS = "Large eggs";
    public static final String TEXT_5_UNIT = "5 unit";
    public static final String TEXT_STEP_5 = "Step 5";
    public static final String TEXT_RECIPE_DESC = "Crack 3 eggs into the chocolate mixture and carefully fold them in. Crack the other 2 eggs in and carefully fold them in. Fold in the vanilla.";


    /**
     * Activity Test Rule - RecipeStepsActivity
     */

    @Rule
    public ActivityTestRule<RecipeStepsActivity> mActivityTestRule = new
            ActivityTestRule<RecipeStepsActivity>(RecipeStepsActivity.class) {
                @Override
                protected Intent getActivityIntent() {
                    Context targetContext = InstrumentationRegistry.getInstrumentation()
                            .getTargetContext();
                    return RecipeStepsActivity.getPreparedIntent(targetContext,
                            TEST_EXTRA_RECIPE_ID, TEST_EXTRA_RECIPE_NAME);
                }
            };


    /**
     * private methods
     */

    private RecyclerViewMatcher withRecyclerView(int recyclerViewId) {
        return new RecyclerViewMatcher(recyclerViewId);
    }

    private boolean isTwoPane() {
        Resources resources = mActivityTestRule.getActivity().getResources();
        return resources.getBoolean(R.bool.is_tablet_landscape);
    }


    /**
     * Tests
     */

    @Test
    public void clickIngredientsViewItem_OpensRecipeDetailsActivityWithIngredientsFragment() {
        // click the list view item at position 0 - ingredients
        onView(ViewMatchers.withId(R.id.rv_recipe_steps)).
                perform(RecyclerViewActions.actionOnItemAtPosition(
                        TEST_LIST_POSITION_0,
                        click()
                ));

        // assert toolbar title on RecipeStepsActivity
        onView(allOf(
                isAssignableFrom(TextView.class),
                withParent(isAssignableFrom(Toolbar.class)))
        ).check(matches(withText(TEST_EXTRA_RECIPE_NAME)));

        // assert if the fragment with "Ingredients" text is shown
        onView(withId(R.id.tv_title_ingredients)).check(matches(withText(TEXT_INGREDIENTS)));

        // assert fourth item text in list - "Large eggs - 5 unit"
        onView(withRecyclerView(R.id.rv_ingredients_detail).atPositionOnView(
                TEST_LIST_POSITION_4,
                R.id.tv_ingredient
        )).check(matches(withText(TEXT_LARGE_EGGS)));
        onView(withRecyclerView(R.id.rv_ingredients_detail).atPositionOnView(
                TEST_LIST_POSITION_4,
                R.id.tv_measure
        )).check(matches(withText(TEXT_5_UNIT)));

        if (!isTwoPane()) {
            // assert "steps" button is visible
            onView(withId(R.id.btn_steps)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void clickRecipeStepViewItem_OpensRecipeDetailsActivityWithStepDetailFragment() {
        // click the list view item at position 0 - ingredients
        onView(withId(R.id.rv_recipe_steps)).
                perform(RecyclerViewActions.actionOnItemAtPosition(
                        TEST_LIST_POSITION_7,
                        click()
                ));

        // assert toolbar title on RecipeStepsActivity
        onView(allOf(
                isAssignableFrom(TextView.class),
                withParent(isAssignableFrom(Toolbar.class)))
        ).check(matches(withText(TEST_EXTRA_RECIPE_NAME)));

        // assert the step number - "Step 5"
        onView(withId(R.id.tv_recipe_step_number)).check(matches(withText(TEXT_STEP_5)));

        // assert the recipe step long description
        onView(withId(R.id.tv_recipe_step)).check(matches(withText(TEXT_RECIPE_DESC)));

        if (!isTwoPane()) {
            // assert if the previous button is shown
            onView(withId(R.id.btn_previous)).check(matches(isDisplayed()));
            // assert if the next button is shown
            onView(withId(R.id.btn_next)).check(matches(isDisplayed()));
        }
    }

}
