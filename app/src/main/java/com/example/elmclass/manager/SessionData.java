package com.example.elmclass.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import java.io.File;

/**
 * The class manages both cache and persistence data for the session
 *
 * Created by kgu on 4/12/18.
 */

public class SessionData {
    public static final int HOUR_TO_MILLIS = 3600000;

    private SharedPreferences mPersistenceStore;
    private UserManager mUserManager;
    private File mOutputFileDir;

    SessionData(@NonNull Context context) {
        mPersistenceStore = PersistenceManager.getSharedPersistenceStore(context);

        mUserManager = new UserManager(mPersistenceStore);

        // child (the second param) must matches "path" in xml/filepaths.xml
        mOutputFileDir = new File(context.getExternalCacheDir(), "output");
        if (!mOutputFileDir.exists()) {
            mOutputFileDir.mkdir();
        }
    }

    public UserManager getUserManager() { return mUserManager; }

    public void logout() {
        mUserManager.logout();
    }
}
