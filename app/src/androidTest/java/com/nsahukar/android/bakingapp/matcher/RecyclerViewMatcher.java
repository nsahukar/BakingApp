package com.nsahukar.android.bakingapp.matcher;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;


/**
 * Created by Nikhil on 22/09/17.
 */

public class RecyclerViewMatcher {

    private final int mRecyclerViewId;

    public RecyclerViewMatcher(int recyclerViewId) {
        mRecyclerViewId = recyclerViewId;
    }

    public Matcher<View> atPosition(final int position) {
        return atPositionOnView(position, -1);
    }

    public Matcher<View> atPositionOnView(final int position, final int targetViewId) {
        return new TypeSafeMatcher<View>() {
            Resources resources;
            View childView;

            @Override
            protected boolean matchesSafely(View view) {
                resources = view.getResources();
                if (childView == null) {
                    RecyclerView recyclerView =
                            view.getRootView().findViewById(mRecyclerViewId);
                    if (recyclerView != null && recyclerView.getId() == mRecyclerViewId) {
                        childView = recyclerView.findViewHolderForAdapterPosition(position).itemView;
                    } else {
                        return false;
                    }
                }

                if (targetViewId == -1) {
                    return view == childView;
                } else {
                    View targetView = childView.findViewById(targetViewId);
                    return view == targetView;
                }
            }

            @Override
            public void describeTo(Description description) {

            }
        };
    }

    public Matcher<View> withTextInTextView(final String text, final int targetViewId) {
        return new TypeSafeMatcher<View>() {
            Resources resources;
            View childView;

            @Override
            protected boolean matchesSafely(View view) {
                resources = view.getResources();
                RecyclerView recyclerView =
                        view.getRootView().findViewById(mRecyclerViewId);
                if (recyclerView != null && recyclerView.getId() == mRecyclerViewId) {
                    RecyclerView.ViewHolder viewHolder = recyclerView.findContainingViewHolder(view);
                    if (viewHolder != null) {
                        childView = viewHolder.itemView;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
                TextView targetView = childView.findViewById(targetViewId);
                return targetView.getText().toString().equalsIgnoreCase(text);
            }

            @Override
            public void describeTo(Description description) {

            }
        };
    }

}
