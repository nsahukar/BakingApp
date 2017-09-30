package com.nsahukar.android.bakingapp.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nsahukar.android.bakingapp.R;
import com.nsahukar.android.bakingapp.data.Step;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Nikhil on 19/08/17.
 */

public class RecipeStepsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = RecipeStepsAdapter.class.getSimpleName();
    private static final int VIEW_TYPE_HEADER = 1;
    private static final int VIEW_TYPE_STEP = 2;

    private ContentValues[] mSteps;
    private Context mContext;
    private OnItemClickListener mItemClickListener;
    private int mSelectedPosition;


    // Click listener interface
    public interface OnItemClickListener {
        void onClickIngredients();
        void onClickStep(int position);
    }

    // Constructor
    public RecipeStepsAdapter(Context context) {
        if (context instanceof OnItemClickListener) {
            mContext = context;
            mItemClickListener = (OnItemClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement RecipesAdapter.OnItemClickListener");
        }
    }

    // Swap ingredients and steps content values
    public void swapContentValues(ContentValues[] steps) {
        mSteps = steps;
        notifyDataSetChanged();
    }

    // Get recipe steps
    public ContentValues[] getSteps() {
        return mSteps;
    }

    // Is the given position the last recipe step
    public boolean isFinalStep(int stepPosition) {
        return stepPosition == mSteps.length - 1;
    }



    // Recycler view adapter methods
    @Override
    public int getItemViewType(int position) {
        if (position == 1) {
            return VIEW_TYPE_HEADER;
        }
        return VIEW_TYPE_STEP;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        int layoutId = 0;
        if (viewType == VIEW_TYPE_HEADER) {
            layoutId = R.layout.header_item;
        } else if (viewType == VIEW_TYPE_STEP) {
            layoutId = R.layout.recipe_step_item;
        }

        if (layoutId != 0) {
            LayoutInflater inflater = LayoutInflater.from(context);
            boolean shouldAttachToParentImmediately = false;
            View view = inflater.inflate(layoutId, parent, shouldAttachToParentImmediately);

            if (viewType == VIEW_TYPE_HEADER) {
                return new HeaderViewHolder(view);
            } else {
                return new StepViewHolder(view);
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (getItemViewType(position) == VIEW_TYPE_HEADER) {
            // Header view type - Steps
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.headerTextView.
                    setText(mContext.getString(R.string.title_header_steps));
        } else {
            // Step view type
            StepViewHolder stepViewHolder = (StepViewHolder) holder;
            stepViewHolder.stepLayout.setSelected(mSelectedPosition == position);
            if (position == 0) {
                // Ingredients
                stepViewHolder.stepIdTextView.setText("*");
                stepViewHolder.stepShortDescTextView.
                        setText(mContext.getString(R.string.title_ingredients));
            } else {
                // Step #
                position = position - 2;
                ContentValues stepContentValues = mSteps[position];
                Step step = new Step(stepContentValues);
                stepViewHolder.stepIdTextView.setText(String.valueOf(step.getId()));
                stepViewHolder.stepShortDescTextView.setText(step.getShortDescription());
            }
        }

    }

    @Override
    public int getItemCount() {
        if (mSteps != null) {
            // plus two for ingredients and steps header row at position 0 and 1 respectively
            return mSteps.length + 2;
        }
        return 0;
    }



    // View holder - recipe ingredients
    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_header) TextView headerTextView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }



    // View holder - recipe steps
    public class StepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.layout_step) LinearLayout stepLayout;
        @BindView(R.id.tv_step_id) TextView stepIdTextView;
        @BindView(R.id.tv_step_short_desc) public TextView stepShortDescTextView;

        public StepViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mSelectedPosition = position;
            notifyDataSetChanged();
            switch (position) {
                case 0:
                    mItemClickListener.onClickIngredients();
                    break;

                case 1:
                    // Nothing happens when the header "STEPS" is clicked
                    break;

                default:
                    position -= 2;
                    mItemClickListener.onClickStep(position);
            }
        }
    }

}
