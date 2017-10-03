package com.nsahukar.android.bakingapp.ui;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.nsahukar.android.bakingapp.R;
import com.nsahukar.android.bakingapp.adapter.IngredientsAdapter;
import com.nsahukar.android.bakingapp.utils.BakingAppJsonUtils;

import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;


public class IngredientsDetailFragment extends Fragment {

    private static final String TAG = IngredientsDetailFragment.class.getSimpleName();
    private static final String ARG_INGREDIENTS_JSON_STRING = "ingredients_json_string";
    private static final String STATE_RECYCLER_VIEW = "state_recycler_view";

    private String mIngredientsJsonStr;
    private OnFragmentInteractionListener mListener;
    private Parcelable mRecyclerViewSavedInstanceSate;
    @BindView(R.id.rv_ingredients_detail) RecyclerView mIngredientsRecyclerView;
    @Nullable @BindView(R.id.btn_steps) Button mStepsButton;


    // get ingredients content values from ingredients json string
    private ContentValues[] getIngredientsContentValues() {
        try {
            return BakingAppJsonUtils.getIngredientsContentValuesFromJson(mIngredientsJsonStr);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        return null;
    }

    // Set up recycler view
    private void setUpRecyclerView() {
        ContentValues[] ingredientsValues = getIngredientsContentValues();
        if (ingredientsValues != null) {
            LinearLayoutManager linearLayoutManager = new
                    LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            mIngredientsRecyclerView.setLayoutManager(linearLayoutManager);
            mIngredientsRecyclerView.setHasFixedSize(true);

            // set adapter to recycler view
            IngredientsAdapter ingredientsAdapter = new IngredientsAdapter(ingredientsValues);
            mIngredientsRecyclerView.setAdapter(ingredientsAdapter);
            if (mRecyclerViewSavedInstanceSate != null) {
                mIngredientsRecyclerView.getLayoutManager().
                        onRestoreInstanceState(mRecyclerViewSavedInstanceSate);
            }
        }
    }


    // Constructor
    public IngredientsDetailFragment() {
        // Required empty public constructor
    }

    // Static method to get the new fragment instance
    public static IngredientsDetailFragment getInstance(String ingredientsJsonStr) {
        IngredientsDetailFragment fragment = new IngredientsDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_INGREDIENTS_JSON_STRING, ingredientsJsonStr);
        fragment.setArguments(args);
        return fragment;
    }

    // lifecycle methods
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IngredientsDetailFragment.OnFragmentInteractionListener) {
            mListener = (IngredientsDetailFragment.OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mIngredientsJsonStr = getArguments().getString(ARG_INGREDIENTS_JSON_STRING);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ingredients_detail, container, false);
        ButterKnife.bind(this, view);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(STATE_RECYCLER_VIEW)) {
                mRecyclerViewSavedInstanceSate = savedInstanceState.
                        getParcelable(STATE_RECYCLER_VIEW);
            }
        }

        // set up recycler view
        setUpRecyclerView();

        // For width < 900dp
        // set click listeners for next and previous buttons
        if (mStepsButton != null) {
            mStepsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onStepsButtonClick();
                    }
                }
            });
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_RECYCLER_VIEW, mIngredientsRecyclerView.getLayoutManager().
                onSaveInstanceState());
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    // Fragment interaction listener
    public interface OnFragmentInteractionListener {
        void onStepsButtonClick();
    }

}
