package com.elmclass.elmclass.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.elmclass.elmclass.R;
import com.elmclass.elmclass.fragment.LogInFragment;
import com.elmclass.elmclass.fragment.SignUpFragment;
import com.elmclass.elmclass.manager.AppManager;
import com.elmclass.elmclass.manager.NetworkManager;
import com.elmclass.elmclass.manager.UserManager;

import static com.elmclass.elmclass.manager.AppManager.PERMISSION_REQUEST_READ_PHONE_NUMBER;

/**
 *
 * Created by kgu on 4/16/18.
 */

public class SignInActivity extends AppCompatActivity {
    private static final String LOG_TAG = SignInActivity.class.getName();
    // requires SDK version 26 support Manifest.permission.READ_PHONE_NUMBERS
    private static String [] PERMISSIONS = { Manifest.permission.READ_PHONE_NUMBERS };
    private static final boolean attempt_read_phone_number_from_device = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppManager.setup(getApplicationContext());

        setContentView(R.layout.activity_frame);

        if (savedInstanceState == null) {

            UserManager userManager = AppManager.getInstance().getSessionData().getUserManager();
            if (AppManager.DEBUG) {
                Log.i(LOG_TAG, "token=" + userManager.getUserToken() + " url=" + userManager.getUrl());
            }
            if (!TextUtils.isEmpty(userManager.getUserToken())) {
                boolean expired = userManager.getExpiration() > 0 && userManager.getExpiration() < System.currentTimeMillis() / 1000;
                if (!expired) {
                    navigateToWebView(NetworkManager.ENDPOINT_ASSIGNMENT);
                } else {
                    navigateToLogIn(userManager.getUid());
                }
            } else if (!TextUtils.isEmpty(userManager.getUid())) {
                navigateToLogIn(userManager.getUid());
            } else if (attempt_read_phone_number_from_device) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_READ_PHONE_NUMBER);
            } else {
                navigateToSignUp(userManager.getUid());
            }
        }
    }

    @Override
    public void onBackPressed() { /* Disable back button */ }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode) {
            case PERMISSION_REQUEST_READ_PHONE_NUMBER:
                UserManager userManager = AppManager.getInstance().getSessionData().getUserManager();
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    try {
                        String mPhoneNumber = tMgr.getLine1Number();
                        userManager.setUid(mPhoneNumber);
                    } catch (SecurityException ex) {
                        if (AppManager.DEBUG) {
                            Log.d(LOG_TAG, "Security exception when get phone number: " + ex.getMessage());
                        }
                    } catch (NullPointerException ex) {
                        if (AppManager.DEBUG) {
                            Log.d(LOG_TAG, "NullPointer exception when get phone number: " + ex.getMessage());
                        }
                    }
                }
                navigateToSignUp(userManager.getUid());
                break;
        }
    }

    public void navigateToWebView(String url) {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra(NetworkManager.KEY_URL, url);
        startActivity(intent);

        // Don't come back to this activity anymore
        finish();
    }

    public void navigateToSignUp(String uid) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment f = new SignUpFragment();
        if (!TextUtils.isEmpty(uid)) {
            Bundle args = new Bundle();
            args.putString("u", uid);
            f.setArguments(args);
        }
        ft.replace(R.id.activity_frame, f);
        ft.commit();
    }

    public void navigateToLogIn(String uid) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment f = new LogInFragment();
        if (!TextUtils.isEmpty(uid)) {
            Bundle args = new Bundle();
            args.putString("u", uid);
            f.setArguments(args);
        }
        ft.replace(R.id.activity_frame, f);
        ft.commit();
    }
}
