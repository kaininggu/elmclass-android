package com.example.elmclass.manager;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Singleton holds a sesson cache
 *
 * Created by kgu on 4/10/18.
 */

public class AppManager {
    public static final String PACKAGE_NAME = "com.example.elmclass";
    public static final boolean DEBUG = true;
    private static final boolean MOCK = false;

    private static final String LOG_TAG = AppManager.class.getName();
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

    public @NonNull String getAppName() { return mAppContext.getPackageName(); }

    public String getAppVersion() {
        try {
            return mAppContext.getPackageManager().getPackageInfo(getAppName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            if (AppManager.DEBUG) {
                Log.w(LOG_TAG, "Missing android:versionName entry in AndroidManifest.xml");
            }
            return null;
        }
    }

    private AppManager(@NonNull Context context) {
        mAppContext = context.getApplicationContext();
        mSessionData = new SessionData(mAppContext);
    }

    private AppManager() {}
}
