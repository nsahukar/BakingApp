package com.nsahukar.android.bakingapp.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nsahukar.android.bakingapp.R;
import com.nsahukar.android.bakingapp.data.Ingredient;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Nikhil on 19/08/17.
 */

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.IngredientViewHolder> {

    private static final String TAG = IngredientsAdapter.class.getSimpleName();

    private ContentValues[] mIngredients;

    // Constructor
    public IngredientsAdapter(ContentValues[] ingredients) {
        mIngredients = ingredients;
    }

    // swap ingredients and steps content values
    public void swapContentValues(ContentValues[] ingredients) {
        mIngredients = ingredients;
        notifyDataSetChanged();
    }

    // Recycler view adapter methods
    @Override
    public IngredientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutId = R.layout.ingredient_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutId, parent, shouldAttachToParentImmediately);
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IngredientViewHolder holder, int position) {
        ContentValues ingredientValues = mIngredients[position];
        Ingredient ingredient = new Ingredient(ingredientValues);
        holder.ingredientTextView.setText(ingredient.getName());
        final String measureString = String.format("%s %s", ingredient.getQuantity(),
                ingredient.getFriendlyMeasureString());
        holder.measureTextView.setText(measureString);

    }

    @Override
    public int getItemCount() {
        if (mIngredients != null) {
            return mIngredients.length;
        }
        return 0;
    }

    // View holder - recipe ingredients
    public class IngredientViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_measure) TextView measureTextView;
        @BindView(R.id.tv_ingredient) TextView ingredientTextView;

        public IngredientViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}
