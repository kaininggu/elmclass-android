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
    public static final String USER_AGENT = "ElmClass " + VERSION_NAME + "; " + System.getProperty("http.agent");
    public static final int PERMISSION_REQUEST_SEND_SMS = 1;
    public static final int PERMISSION_REQUEST_READ_PHONE_NUMBER = 2;
    public static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 3;

    public static final boolean DEBUG = BuildConfig.BUILD_TYPE.equals("debug");
    private static AppManager sInstance;

    private Context mAppContext;
    private SessionData mSessionData;

    public static synchronized void setup(Context context) {
        if (sInstance == null) {
            sInstance = new AppManager(context);
        }
    }

    public static @NonNull AppManager getInstance() { return sInstance; }

    public static void showKeyboard(Activity activity, View hostView) {
        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(hostView, InputMethodManager.SHOW_IMPLICIT);
        } catch (java.lang.NullPointerException ex) {
            //Ignore
        }
    }

    public static void hideKeyboard(Context context, View view) {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (NullPointerException ex) {
            // ignore
        }
    }

    public @NonNull Context getAppContext() { return mAppContext; }

    public @NonNull SessionData getSessionData() { return mSessionData; }

    private AppManager(@NonNull Context context) {
        mAppContext = context.getApplicationContext();
        mSessionData = new SessionData(mAppContext);
    }

    private AppManager() {}
}
