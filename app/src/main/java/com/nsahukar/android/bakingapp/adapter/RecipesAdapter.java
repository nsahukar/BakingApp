package com.nsahukar.android.bakingapp.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nsahukar.android.bakingapp.R;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.nsahukar.android.bakingapp.data.BakingAppContract.RecipesEntry;

/**
 * Created by Nikhil on 12/08/17.
 */

public class RecipesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = RecipesAdapter.class.getSimpleName();
    public static final int VIEW_TYPE_GRID = 101;
    public static final int VIEW_TYPE_LIST = 102;

    private Cursor mCursor;
    private Context mContext;
    private int mViewType;
    private OnItemClickListener mItemClickListener;

    // Click listener interface
    public interface OnItemClickListener {
        void onClick(long recipeId, String recipeName);
    }

    // Constructor
    public RecipesAdapter(Context context, int viewType) {
        if (context instanceof OnItemClickListener) {
            mContext = context;
            mItemClickListener = (OnItemClickListener) context;
            mViewType = viewType;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement RecipesAdapter.OnItemClickListener");
        }
    }

    // swap cursor
    public void swapCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }


    // Recycler view adapter methods
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutId = 0;
        if (viewType == VIEW_TYPE_GRID) {
            layoutId = R.layout.recipe_grid_item;
        } else if (viewType == VIEW_TYPE_LIST) {
            layoutId = R.layout.recipe_list_item;
        }

        if (layoutId != 0) {
            LayoutInflater inflater = LayoutInflater.from(context);
            boolean shouldAttachToParentImmediately = false;
            View view = inflater.inflate(layoutId, parent, shouldAttachToParentImmediately);
            if (viewType == VIEW_TYPE_GRID) {
                return new RecipeGridViewHolder(view);
            } else {
                return new RecipeListViewHolder(view);
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        final int viewType = getItemViewType(position);
        if (viewType == VIEW_TYPE_GRID) {
            RecipeGridViewHolder recipeGridViewHolder = (RecipeGridViewHolder) holder;
            final String recipeImageUrl = mCursor.getString(mCursor.getColumnIndex(RecipesEntry.COLUMN_IMAGE));
            if (recipeImageUrl != null && recipeImageUrl.length() > 0) {
                Picasso.with(mContext).load(recipeImageUrl).into(recipeGridViewHolder.recipeImageView);
            }
            final String recipeName = mCursor.getString(mCursor.getColumnIndex(RecipesEntry.COLUMN_NAME));
            recipeGridViewHolder.recipeNameTextView.setText(recipeName);
        } else {
            RecipeListViewHolder recipeListViewHolder = (RecipeListViewHolder) holder;
            final String recipeName = mCursor.getString(mCursor.getColumnIndex(RecipesEntry.COLUMN_NAME));
            recipeListViewHolder.recipeNameTextView.setText(recipeName);
        }
    }

    @Override
    public int getItemCount() {
        if (mCursor != null) {
            return mCursor.getCount();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return mViewType;
    }


    // View holder - grid
    public class RecipeGridViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.iv_recipe_image) ImageView recipeImageView;
        @BindView(R.id.tv_recipe_name) public TextView recipeNameTextView;

        public RecipeGridViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mItemClickListener != null) {
                mCursor.moveToPosition(getAdapterPosition());
                final long recipeId = mCursor.getLong(mCursor.getColumnIndex(RecipesEntry._ID));
                final String recipeName = mCursor.getString(mCursor.getColumnIndex(RecipesEntry.COLUMN_NAME));
                mItemClickListener.onClick(recipeId, recipeName);
            }
        }
    }

    // View holder - list
    public class RecipeListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tv_recipe_name) TextView recipeNameTextView;

        public RecipeListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mItemClickListener != null) {
                mCursor.moveToPosition(getAdapterPosition());
                final long recipeId = mCursor.getLong(mCursor.getColumnIndex(RecipesEntry._ID));
                final String recipeName = mCursor.getString(mCursor.getColumnIndex(RecipesEntry.COLUMN_NAME));
                mItemClickListener.onClick(recipeId, recipeName);
            }
        }
    }

}
