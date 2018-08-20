package com.elmclass.elmclass.manager;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.elmclass.elmclass.BuildConfig;

/**
 * Singleton holds a sesson cache
 *
 * Created by kgu on 4/10/18.
 */

public class AppManager {
    public static final String VERSION_NAME = BuildConfig.VERSION_NAME;

    public static final boolean DEBUG = true;
    private static AppManager sInstance;

    private Context mAppContext;
    private SessionData mSessionData;

    public static synchronized void setup(Context context) {
        if (sInstance == null) {
            sInstance = new AppManager(context);
        }
    }

    public static @NonNull AppManager getInstance() { return sInstance; }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public @NonNull Context getAppContext() { return mAppContext; }

    public @NonNull SessionData getSessionData() { return mSessionData; }

    private AppManager(@NonNull Context context) {
        mAppContext = context.getApplicationContext();
        mSessionData = new SessionData(mAppContext);
    }

    private AppManager() {}
}
