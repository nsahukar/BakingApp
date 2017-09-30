package com.nsahukar.android.bakingapp.sync;

import android.content.Context;
import android.os.AsyncTask;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Created by Nikhil on 12/08/17.
 */

public class BakingAppJobService extends JobService {

    private AsyncTask<Void, Void, Void> mSyncRecipesTask;

    @Override
    public boolean onStartJob(final JobParameters job) {

        mSyncRecipesTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Context context = getApplicationContext();
                BakingAppSyncTask.syncRecipes(context);
                jobFinished(job, false);
                return null;
            }
        };
        mSyncRecipesTask.execute();
        return true;

    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if (mSyncRecipesTask != null) {
            mSyncRecipesTask.cancel(true);
        }
        return true;
    }

}
