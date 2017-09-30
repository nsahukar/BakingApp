package com.nsahukar.android.bakingapp.activity;

import android.content.res.Resources;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.nsahukar.android.bakingapp.R;
import com.nsahukar.android.bakingapp.matcher.RecyclerViewMatcher;
import com.nsahukar.android.bakingapp.ui.RecipesActivity;

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
 * Created by Nikhil on 22/09/17.
 */

@RunWith(AndroidJUnit4.class)
public class RecipesActivityTest {

    /**
     * Test prerequisites
     */

    public static final int TEST_GRID_POSITION_0 = 0;
    public static final int TEST_GRID_POSITION_5 = 5;

    /**
     * Texts to assert the result of a test
     */

    public static final String TEXT_NUTELLA_PIE = "Nutella Pie";
    public static final String TEXT_INGREDIENTS = "Ingredients";
    public static final String TEXT_STEP_3 = "Press the crust into baking form.";

    /**
     * Activity Test Rule - RecipeStepsActivity
     */

    @Rule
    public ActivityTestRule<RecipesActivity> mActivityTestRule = new
            ActivityTestRule<>(RecipesActivity.class);

    /**
     * private methods
     */

    private boolean isTwoPane() {
        Resources resources = mActivityTestRule.getActivity().getResources();
        return resources.getBoolean(R.bool.is_tablet_landscape);
    }

    private RecyclerViewMatcher withRecyclerView(int recyclerViewId) {
        return new RecyclerViewMatcher(recyclerViewId);
    }


    /**
     * Tests
     */
    @Test
    public void clickRecipesGridViewItem_OpensRecipeStepsActivity() {
        // click the grid view item at position 0
        onView(withId(R.id.rv_recipes)).
                perform(RecyclerViewActions.actionOnItemAtPosition(
                        TEST_GRID_POSITION_0,
                        click()
                ));

        // assert toolbar title on RecipeStepsActivity
        onView(allOf(
                isAssignableFrom(TextView.class),
                withParent(isAssignableFrom(Toolbar.class)))
        ).check(matches(withText(TEXT_NUTELLA_PIE)));

        // assert first item text in list - "Ingredients"
        onView(withRecyclerView(R.id.rv_recipe_steps).atPositionOnView(
                TEST_GRID_POSITION_0,
                R.id.tv_step_short_desc
        )).check(matches(withText(TEXT_INGREDIENTS)));

        // assert third item text in list - "Press the crust into baking form."
        onView(withRecyclerView(R.id.rv_recipe_steps).atPositionOnView(
                TEST_GRID_POSITION_5,
                R.id.tv_step_short_desc
        )).check(matches(withText(TEXT_STEP_3)));

        // more tests for tablet in landscape mode
        if (isTwoPane()) {
            // assert if the detail container is displayed
            onView(withId(R.id.detail_container)).check(matches(isDisplayed()));

            // assert if the fragment with ingredient detail is shown
            onView(withId(R.id.tv_title_ingredients)).check(matches(withText(TEXT_INGREDIENTS)));
        }
    }

}
