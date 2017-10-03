package com.nsahukar.android.bakingapp.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nsahukar.android.bakingapp.R;
import com.nsahukar.android.bakingapp.adapter.RecipesAdapter;
import com.nsahukar.android.bakingapp.data.BakingAppContract.RecipesEntry;
import com.nsahukar.android.bakingapp.sync.BakingAppSyncUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RecipesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        RecipesAdapter.OnItemClickListener {

    private static final String STATE_RECYCLER_VIEW = "state_recycler_view";

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.progress_bar) ProgressBar mProgressBar;
    @BindView(R.id.layout_error) ConstraintLayout mErrorLayout;
    @BindView(R.id.tv_error_msg) TextView mErrorMessageTextView;
    @BindView(R.id.rv_recipes) RecyclerView mRecipesRecyclerView;
    private Parcelable mRecyclerViewSavedInstanceSate;
    private RecipesAdapter mRecipesAdapter;

    // Item decoration for the grid
    private class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

        private int mItemOffset;

        public ItemOffsetDecoration(int itemOffset) {
            mItemOffset = itemOffset;
        }

        public ItemOffsetDecoration(@NonNull Context context, @DimenRes int itemOffsetId) {
            this(context.getResources().getDimensionPixelSize(itemOffsetId));
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset);
        }

    }

    // Set up recycler view
    private void setUpRecyclerView() {
        final int spanCount = getResources().getInteger(R.integer.grid_span_count);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, spanCount, GridLayoutManager.VERTICAL, false);
        mRecipesRecyclerView.setLayoutManager(gridLayoutManager);
        mRecipesRecyclerView.setHasFixedSize(true);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.grid_item_offset);
        mRecipesRecyclerView.addItemDecoration(itemDecoration);

        // set adapter to recycler view
        mRecipesAdapter = new RecipesAdapter(this, RecipesAdapter.VIEW_TYPE_GRID);
        mRecipesRecyclerView.setAdapter(mRecipesAdapter);
        if (mRecyclerViewSavedInstanceSate != null) {
            mRecipesRecyclerView.getLayoutManager().
                    onRestoreInstanceState(mRecyclerViewSavedInstanceSate);
        }
    }

    // Show loader indicator
    private void showLoaderIndicator() {
        mErrorLayout.setVisibility(ConstraintLayout.INVISIBLE);
        mRecipesRecyclerView.setVisibility(RecyclerView.INVISIBLE);
        mProgressBar.setVisibility(ProgressBar.VISIBLE);
    }

    // Show error message
    private void showErrorMessage(String errorMessage) {
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
        mRecipesRecyclerView.setVisibility(RecyclerView.INVISIBLE);
        mErrorLayout.setVisibility(ConstraintLayout.VISIBLE);
        mErrorMessageTextView.setText(errorMessage);
    }

    // Show grid view
    private void showRecipesGridView() {
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
        mErrorLayout.setVisibility(ConstraintLayout.INVISIBLE);
        mRecipesRecyclerView.setVisibility(RecyclerView.VISIBLE);
    }


    // Lifecycle methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);
        ButterKnife.bind(this);

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            mToolbar.setTitle(getTitle());
        }
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(STATE_RECYCLER_VIEW)) {
                mRecyclerViewSavedInstanceSate = savedInstanceState.
                        getParcelable(STATE_RECYCLER_VIEW);
            }
        }
        setUpRecyclerView();
        showLoaderIndicator();
        getSupportLoaderManager().initLoader(R.integer.id_recipes_loader, null, this);
        BakingAppSyncUtils.initialize(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_RECYCLER_VIEW, mRecipesRecyclerView.getLayoutManager().
                onSaveInstanceState());
    }

    @OnClick(R.id.btn_retry)
    void retry() {
        getSupportLoaderManager().restartLoader(R.integer.id_recipes_loader, null, this);
    }


    // LoaderCallback methods
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case R.integer.id_recipes_loader:
                // Recipes content uri
                final Uri RecipesContentUri = RecipesEntry.CONTENT_URI;
                // columns
                final String[] columns = new String[]{
                        RecipesEntry._ID,
                        RecipesEntry.COLUMN_IMAGE,
                        RecipesEntry.COLUMN_NAME
                };
                // sort order
                final String sortOrder = RecipesEntry._ID;
                return new CursorLoader(this, RecipesContentUri, columns, null, null, sortOrder);

            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            // show recipes grid view
            showRecipesGridView();
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
        Intent intentToStartRecipeStepsActivity = RecipeStepsActivity.getPreparedIntent(
                this, recipeId, recipeName
        );
        startActivity(intentToStartRecipeStepsActivity);
    }

}
