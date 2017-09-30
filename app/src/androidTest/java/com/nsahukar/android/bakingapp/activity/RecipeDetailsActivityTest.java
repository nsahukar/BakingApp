package com.nsahukar.android.bakingapp.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.nsahukar.android.bakingapp.R;
import com.nsahukar.android.bakingapp.ui.RecipeDetailsActivity;
import com.nsahukar.android.bakingapp.utils.BakingAppJsonUtils;

import org.json.JSONException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.not;

/**
 * Created by Nikhil on 29/09/17.
 */

@RunWith(AndroidJUnit4.class)
public class RecipeDetailsActivityTest {

    private static final String TAG = RecipeDetailsActivity.class.getSimpleName();

    /**
     * Test prerequisites
     */

    // Recipe name
    private static final String TEST_EXTRA_RECIPE_NAME = "Cheesecake";
    // Recipe ingredients JSON
    private static final String TEST_EXTRA_INGREDIENTS_JSON = "[{\"quantity\":2,\"measure\":\"CUP\",\"ingredient\":\"GrahamCrackercrumbs\"},{\"quantity\":6,\"measure\":\"TBLSP\",\"ingredient\":\"unsaltedbutter, melted\"},{\"quantity\":250,\"measure\":\"G\",\"ingredient\":\"granulatedsugar\"},{\"quantity\":1,\"measure\":\"TSP\",\"ingredient\":\"salt\"},{\"quantity\":4,\"measure\":\"TSP\",\"ingredient\":\"vanilla, divided\"},{\"quantity\":680,\"measure\":\"G\",\"ingredient\":\"creamcheese, softened\"},{\"quantity\":3,\"measure\":\"UNIT\",\"ingredient\":\"largewholeeggs\"},{\"quantity\":2,\"measure\":\"UNIT\",\"ingredient\":\"largeeggyolks\"},{\"quantity\":250,\"measure\":\"G\",\"ingredient\":\"heavycream\"}]";
    // Recipe steps JSON
    private static final String TEST_EXTRA_RECIPE_STEPS_JSON = "[{\"id\":0,\"shortDescription\":\"Recipe Introduction\",\"description\":\"Recipe Introduction\",\"videoURL\":\"https://d17h27t6h515a5.cloudfront.net/topher/2017/April/58ffdae8_-intro-cheesecake/-intro-cheesecake.mp4\",\"thumbnailURL\":\"\"},{\"id\":1,\"shortDescription\":\"Starting prep.\",\"description\":\"1. Preheat the oven to 350°F. Grease the bottom of a 9-inch round springform pan with butter. \",\"videoURL\":\"\",\"thumbnailURL\":\"\"},{\"id\":2,\"shortDescription\":\"Prep the cookie crust.\",\"description\":\"2. To assemble the crust, whisk together the cookie crumbs, 50 grams (1/4 cup) of sugar, and 1/2 teaspoon of salt for the crust in a medium bowl. Stir in the melted butter and 1 teaspoon of vanilla extract until uniform. \",\"videoURL\":\"https://d17h27t6h515a5.cloudfront.net/topher/2017/April/58ffdb1d_2-form-crust-to-bottom-of-pan-cheesecake/2-form-crust-to-bottom-of-pan-cheesecake.mp4\",\"thumbnailURL\":\"\"},{\"id\":3,\"shortDescription\":\"Start water bath.\",\"description\":\"3. Fill a large roasting pan with a few inches of hot water and place it on the bottom rack of the oven.\",\"videoURL\":\"\",\"thumbnailURL\":\"\"},{\"id\":4,\"shortDescription\":\"Prebake cookie crust. \",\"description\":\"4. Press the cookie mixture into the bottom and slightly up the sides of the prepared pan. Bake for 11 minutes and then let cool.\",\"videoURL\":\"\",\"thumbnailURL\":\"\"},{\"id\":5,\"shortDescription\":\"Mix cream cheese and dry ingredients.\",\"description\":\"5. Beat the cream cheese, remaining 200 grams (1 cup) of sugar, and remaining 1/2 teaspoon salt on medium speed in a stand mixer with the paddle attachment for 3 minutes (or high speed if using a hand mixer). \",\"videoURL\":\"https://d17h27t6h515a5.cloudfront.net/topher/2017/April/58ffdb3a_3-mix-sugar-salt-together-cheesecake/3-mix-sugar-salt-together-cheesecake.mp4\",\"thumbnailURL\":\"\"},{\"id\":6,\"shortDescription\":\"Add eggs.\",\"description\":\"6. Scrape down the sides of the pan. Add in the eggs one at a time, beating each one on medium-low speed just until incorporated. Scrape down the sides and bottom of the bowl. Add in both egg yolks and beat until just incorporated. \",\"videoURL\":\"https://d17h27t6h515a5.cloudfront.net/topher/2017/April/58ffdb55_4-add-eggs-mix-cheesecake/4-add-eggs-mix-cheesecake.mp4\",\"thumbnailURL\":\"\"},{\"id\":7,\"shortDescription\":\"Add heavy cream and vanilla.\",\"description\":\"7. Add the cream and remaining tablespoon of vanilla to the batter and beat on medium-low speed until just incorporated. \",\"videoURL\":\"https://d17h27t6h515a5.cloudfront.net/topher/2017/April/58ffdb72_5-mix-vanilla-cream-together-cheesecake/5-mix-vanilla-cream-together-cheesecake.mp4\",\"thumbnailURL\":\"\"},{\"id\":8,\"shortDescription\":\"Pour batter in pan.\",\"description\":\"8. Pour the batter into the cooled cookie crust. Bang the pan on a counter or sturdy table a few times to release air bubbles from the batter.\",\"videoURL\":\"https://d17h27t6h515a5.cloudfront.net/topher/2017/April/58ffdb88_6-add-the-batter-to-the-pan-w-the-crumbs-cheesecake/6-add-the-batter-to-the-pan-w-the-crumbs-cheesecake.mp4\",\"thumbnailURL\":\"\"},{\"id\":9,\"shortDescription\":\"Bake the cheesecake.\",\"description\":\"9. Bake the cheesecake on a middle rack of the oven above the roasting pan full of water for 50 minutes. \",\"videoURL\":\"\",\"thumbnailURL\":\"\"},{\"id\":10,\"shortDescription\":\"Turn off oven and leave cake in.\",\"description\":\"10. Turn off the oven but keep the cheesecake in the oven with the door closed for 50 more minutes.\",\"videoURL\":\"\",\"thumbnailURL\":\"\"},{\"id\":11,\"shortDescription\":\"Remove from oven and cool at room temperature.\",\"description\":\"11. Take the cheesecake out of the oven. It should look pale yellow or golden on top and be set but still slightly jiggly. Let it cool to room temperature. \",\"videoURL\":\"\",\"thumbnailURL\":\"\"},{\"id\":12,\"shortDescription\":\"Final cooling and set.\",\"description\":\"12. Cover the cheesecake with plastic wrap, not allowing the plastic to touch the top of the cake, and refrigerate it for at least 8 hours. Then it's ready to serve!\",\"videoURL\":\"https://d17h27t6h515a5.cloudfront.net/topher/2017/April/58ffdbac_9-finished-product-cheesecake/9-finished-product-cheesecake.mp4\",\"thumbnailURL\":\"\"}]";
    // step number for ingredients
    private static final int TEST_STEP_NUMBER_INGREDIENT = -1;
    // step number for last recipe step
    private static final int TEST_STEP_NUMBER_LAST = 99;

    /**
     * Texts to assert the result of a test
     */

    public static final String TEXT_INGREDIENTS = "Ingredients";
    public static final String TEXT_INTRODUCTION = "Introduction";

    /**
     * Number of recipe steps
     */

    private int mRecipeStepsCount;

    /**
     * Activity Test Rule - RecipeStepsActivity
     */

    @Rule
    public ActivityTestRule<RecipeDetailsActivity> mActivityTestRule = new
            ActivityTestRule<>(RecipeDetailsActivity.class, true, false);


    /**
     * private methods
     */

    private Intent prepareActivityIntentForStepNumber(int stepNumber) {
        ContentValues[] steps = null;
        try {
            steps = BakingAppJsonUtils.
                    getStepsContentValuesFromJson(TEST_EXTRA_RECIPE_STEPS_JSON);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }

        if (steps != null) {
            // Excluding recipe introduction from recipe steps count
            mRecipeStepsCount = steps.length - 1;
            // Update step number for last recipe step number
            if (stepNumber == TEST_STEP_NUMBER_LAST) {
                stepNumber = mRecipeStepsCount;
            }
        }

        Context targetContext = InstrumentationRegistry.getInstrumentation()
                .getTargetContext();
        return RecipeDetailsActivity.getPreparedIntent(targetContext, TEST_EXTRA_RECIPE_NAME,
                TEST_EXTRA_INGREDIENTS_JSON, steps, stepNumber);
    }

    private void forwardNavigateFromStep(int stepNumber) {
        onView(withId(R.id.btn_previous)).check(matches(isDisplayed()));
        if (stepNumber != mRecipeStepsCount) {
            onView(withId(R.id.btn_next)).check(matches(isDisplayed()));
            onView(withId(R.id.btn_next)).perform(click());
        } else {
            onView(withId(R.id.btn_next)).check(matches(not(isDisplayed())));
        }
    }

    private void backwardNavigateFromStep(int stepNumber) {
        if (stepNumber != mRecipeStepsCount) {
            onView(withId(R.id.btn_next)).check(matches(isDisplayed()));
        } else {
            onView(withId(R.id.btn_next)).check(matches(not(isDisplayed())));
        }
        onView(withId(R.id.btn_previous)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_previous)).perform(click());
    }

    private boolean isTwoPane() {
        Context targetContext = InstrumentationRegistry.getInstrumentation()
                .getTargetContext();
        Resources resources = targetContext.getResources();
        return resources.getBoolean(R.bool.is_tablet_landscape);
    }


    /**
     * Tests
     */

    @Test
    public void forwardNavigationThroughAllRecipeDetails() {
        if (!isTwoPane()) {
            // initialize activity for this test with appropriate intent
            Intent intent = prepareActivityIntentForStepNumber(TEST_STEP_NUMBER_INGREDIENT);
            mActivityTestRule.launchActivity(intent);

            // assert if the fragment with ingredient detail is shown
            onView(withId(R.id.tv_title_ingredients)).check(matches(withText(TEXT_INGREDIENTS)));
            // assert if the steps button is visible
            onView(withId(R.id.btn_steps)).check(matches(isDisplayed()));
            // click the steps button
            onView(withId(R.id.btn_steps)).perform(click());

            // assert if the recipe introduction is displayed
            onView(withId(R.id.tv_recipe_step_number)).check(matches(withText(TEXT_INTRODUCTION)));
            // assert if the "INGREDIENTS" button is displayed
            onView(anyOf(withId(R.id.btn_previous), withText(TEXT_INGREDIENTS))).
                    check(matches(isDisplayed()));
            // assert if the next button is displayed
            onView(withId(R.id.btn_next)).check(matches(isDisplayed()));
            // click the next button
            onView(withId(R.id.btn_next)).perform(click());

            // forward navigating through all the recipe steps
            for (int stepNumber = 1; stepNumber <= mRecipeStepsCount; stepNumber++) {
                final String stepNumberString = "Step " + stepNumber;
                // assert if step number is displayed
                onView(withId(R.id.tv_recipe_step_number)).check(matches(withText(stepNumberString)));
                // forward navigate from current step, asserting navigation buttons
                forwardNavigateFromStep(stepNumber);
            }
        }
    }

    @Test
    public void backwardNavigationThroughAllRecipeDetails() {
        if (!isTwoPane()) {
            // initialize activity for this test with appropriate intent
            Intent intent = prepareActivityIntentForStepNumber(TEST_STEP_NUMBER_LAST);
            mActivityTestRule.launchActivity(intent);

            // backward navigating through all the recipe steps
            for (int stepNumber = mRecipeStepsCount; stepNumber >= 1; stepNumber--) {
                final String stepNumberString = "Step " + stepNumber;
                // assert if step number is displayed
                onView(withId(R.id.tv_recipe_step_number)).check(matches(withText(stepNumberString)));
                // backward navigate from current step, asserting navigation buttons
                backwardNavigateFromStep(stepNumber);
            }

            // assert if the recipe introduction is displayed
            onView(withId(R.id.tv_recipe_step_number)).check(matches(withText(TEXT_INTRODUCTION)));
            // assert if the "INGREDIENTS" button is displayed
            onView(anyOf(withId(R.id.btn_previous), withText(TEXT_INGREDIENTS))).
                    check(matches(isDisplayed()));
            // assert if the next button is displayed
            onView(withId(R.id.btn_next)).check(matches(isDisplayed()));
            // click the "INGREDIENTS" button
            onView(withId(R.id.btn_previous)).perform(click());

            // assert if the fragment with ingredient detail is shown
            onView(withId(R.id.tv_title_ingredients)).check(matches(withText(TEXT_INGREDIENTS)));
            // assert if the steps button is visible
            onView(withId(R.id.btn_steps)).check(matches(isDisplayed()));
        }
    }

}
