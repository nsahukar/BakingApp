package com.nsahukar.android.bakingapp.data;

import android.content.ContentValues;

/**
 * Created by Nikhil on 12/08/17.
 */

public class Step {

    public static final String ID = "id";
    public static final String DESCRIPTION = "description";
    public static final String SHORT_DESCRIPTION = "shortDescription";
    public static final String VIDEO_URL = "videoURL";
    public static final String THUMBNAIL_URL = "thumbnailURL";

//    private long mId;                       // Step id
//    private String mDescription;            // Description of the step
//    private String mShortDescription;       // Short description of the step
//    private String mVideoUrl;               // Associated video url (describing the step)
//    private String mThumbnailUrl;           // Thumbnail url of the video

    private ContentValues mStepContentValues;

    public Step(ContentValues stepContentValues) {
        mStepContentValues = stepContentValues;
    }


    // Getters
    public long getId() {
        return mStepContentValues.getAsLong(ID);
    }

    public String getFriendlyId() {
        long stepId = mStepContentValues.getAsLong(ID);
        if (stepId == 0) {
            return "Introduction";
        } else {
            return "Step " + stepId;
        }
    }

    public String getDescription() {
        return mStepContentValues.getAsString(DESCRIPTION);
    }

    // Removes the starting step number from the description
    public String getFriendlyDescription() {
        String description = getDescription();
        String[] descriptionStatements = description.split("\\.");
        final String firstStatement = descriptionStatements[0];
        int index = firstStatement.matches("\\d+")? 1 : 0;
        StringBuilder friendlyDescriptionBuilder = new StringBuilder();
        descriptionStatements[index] = descriptionStatements[index].trim();
        for (; index<descriptionStatements.length; index++) {
            friendlyDescriptionBuilder.append(descriptionStatements[index] + ".");
        }
        return friendlyDescriptionBuilder.toString();
    }

    public String getShortDescription() {
        return mStepContentValues.getAsString(SHORT_DESCRIPTION);
    }

    public String getVideoUrl() {
        return mStepContentValues.getAsString(VIDEO_URL);
    }

    public String getThumbnailUrl() {
        return mStepContentValues.getAsString(THUMBNAIL_URL);
    }

}
