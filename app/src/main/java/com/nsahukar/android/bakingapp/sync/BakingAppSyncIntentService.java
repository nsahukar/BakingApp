package com.nsahukar.android.bakingapp.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by Nikhil on 12/08/17.
 */

public class BakingAppSyncIntentService extends IntentService {

    public BakingAppSyncIntentService() {
        super("BakingAppSyncIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        BakingAppSyncTask.syncRecipes(this);
    }

}
