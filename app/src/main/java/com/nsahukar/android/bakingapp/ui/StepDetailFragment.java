package com.nsahukar.android.bakingapp.ui;


import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;
import com.nsahukar.android.bakingapp.R;
import com.nsahukar.android.bakingapp.data.Step;

import butterknife.BindView;
import butterknife.ButterKnife;


public class StepDetailFragment extends Fragment implements ExoPlayer.EventListener {

    private static final String TAG = StepDetailFragment.class.getSimpleName();
    private static final String ARG_STEP_CONTENT_VALUES = "step_values";
    private static final String ARG_FINAL_STEP = "final_step";
    private static final long CACHE_MAX_SIZE = 1024 * 1024 * 100;

    private ContentValues mStepContentValues;
    private SimpleExoPlayer mPlayer;
    private OnFragmentInteractionListener mListener;
    private boolean mFinalStep;
    @BindView(R.id.tv_no_video) TextView mNoVideoTextView;
    @BindView(R.id.sepv_recipe_step) SimpleExoPlayerView mPlayerView;
    @BindView(R.id.tv_recipe_step_number) TextView mRecipeStepNumberTextView;
    @BindView(R.id.tv_recipe_step) TextView mRecipeStepTextView;
    @Nullable @BindView(R.id.btn_previous) Button mPreviousButton;
    @Nullable @BindView(R.id.btn_next) Button mNextButton;


    // initialize exo player for the recipe step
    private void initializePlayer(Uri videoUri) {
        // initialize player and player view
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);
        mPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector);
        mPlayerView.setPlayer(mPlayer);

        // set event listener for the player as this activity
        mPlayer.addListener(this);

        // set click listener for replay button
        ImageButton replayButton = (ImageButton) mPlayerView.findViewById(R.id.exo_replay);
        replayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlayer.seekToDefaultPosition();
                mPlayer.setPlayWhenReady(true);
                showPlayPause();
            }
        });

        // set up media source
        String userAgent = Util.getUserAgent(getActivity(), getString(R.string.app_name));
        HttpDataSource.Factory httpDataSourceFactory = new DefaultHttpDataSourceFactory(userAgent);
        Cache cache = new SimpleCache(getActivity().getCacheDir(),
                new LeastRecentlyUsedCacheEvictor(CACHE_MAX_SIZE));
        CacheDataSourceFactory cacheDataSourceFactory = new CacheDataSourceFactory(cache, httpDataSourceFactory);
        DefaultExtractorsFactory defaultExtractorsFactory = new DefaultExtractorsFactory();
        MediaSource mediaSource = new ExtractorMediaSource(videoUri, cacheDataSourceFactory,
                defaultExtractorsFactory, null, null);

        // prepare the media source and set it to play when ready
        mPlayer.prepare(mediaSource);
    }

    // hide play/pause button of exo-player
    private void hidePlayPause() {
        mPlayerView.setControllerHideOnTouch(false);
        mPlayerView.findViewById(R.id.exo_play).setVisibility(View.GONE);
        mPlayerView.findViewById(R.id.exo_pause).setVisibility(View.GONE);
        mPlayerView.findViewById(R.id.exo_replay).setVisibility(View.VISIBLE);
    }

    // show play/pause button of exo-player
    private void showPlayPause() {
        mPlayerView.setControllerHideOnTouch(true);
        mPlayerView.findViewById(R.id.exo_play).setVisibility(View.VISIBLE);
        mPlayerView.findViewById(R.id.exo_pause).setVisibility(View.VISIBLE);
        mPlayerView.findViewById(R.id.exo_replay).setVisibility(View.GONE);
    }


    // Constructor
    public StepDetailFragment() {
        // Required empty public constructor
    }

    // Static method to get the new fragment instance
    public static StepDetailFragment getInstance(ContentValues stepContentValues,
                                                 boolean finalStep) {
        StepDetailFragment fragment = new StepDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_STEP_CONTENT_VALUES, stepContentValues);
        args.putBoolean(ARG_FINAL_STEP, finalStep);
        fragment.setArguments(args);
        return fragment;
    }

    // lifecycle methods
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mStepContentValues = getArguments().getParcelable(ARG_STEP_CONTENT_VALUES);
            mFinalStep = getArguments().getBoolean(ARG_FINAL_STEP, false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_step_detail, container, false);
        ButterKnife.bind(this, view);

        Step step = new Step(mStepContentValues);

        // initialize player
        final String videoUrlString = step.getVideoUrl();
        if (videoUrlString != null && videoUrlString.length() > 0) {
            Uri videoUri = Uri.parse(videoUrlString);
            initializePlayer(videoUri);
        } else {
            mNoVideoTextView.setVisibility(TextView.VISIBLE);
        }

        // set recipe step number text view
        mRecipeStepNumberTextView.setText(step.getFriendlyId());

        // set recipe step text view
        mRecipeStepTextView.setText(step.getFriendlyDescription());

        // For width < 900dp
        // set click listeners for next and previous buttons
        if (mPreviousButton != null) {
            if (step.getId() == 0) {
                mPreviousButton.setText(getString(R.string.title_ingredients));
            }
            mPreviousButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onPreviousDetailButtonClick();
                    }
                }
            });
        }

        if (mNextButton != null) {
            if (mFinalStep) {
                // For width < 900dp
                // hide next button if its the last step
                mNextButton.setVisibility(Button.GONE);
            } else {
                mNextButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mListener != null) {
                            mListener.onNextDetailButtonClick();
                        }
                    }
                });
            }
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof StepDetailFragment.OnFragmentInteractionListener) {
            mListener = (StepDetailFragment.OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPlayerView = null;
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }


    // ExoPlayer Event Listener methods
    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == ExoPlayer.STATE_ENDED) {
            Log.d(TAG, "playback finished");
            hidePlayPause();
        }
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }


    // Fragment interaction listener
    public interface OnFragmentInteractionListener {
        void onPreviousDetailButtonClick();

        void onNextDetailButtonClick();
    }

}
