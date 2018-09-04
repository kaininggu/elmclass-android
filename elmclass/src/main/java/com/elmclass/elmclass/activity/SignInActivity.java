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

/**
 *
 * Created by kgu on 4/16/18.
 */

public class SignInActivity extends AppCompatActivity {
    private static final String LOG_TAG = SignInActivity.class.getName();
    private static String [] PERMISSIONS = { Manifest.permission.READ_PHONE_STATE };
    private static final boolean allow_read_phone_number_from_device = false;

    public static final int REQUEST_SEND_SMS_PERMISSION = 1;
    public static final int REQUEST_SEND_SMS = 2;
    public static final int REQUEST_READ_PHONE_NUMBERS_PERMISSION = 3;

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
            } else if (allow_read_phone_number_from_device) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, SignInActivity.REQUEST_READ_PHONE_NUMBERS_PERMISSION);
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

        Fragment f;
        switch(requestCode) {
            case REQUEST_SEND_SMS_PERMISSION:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    finish();
                }
                f = getSupportFragmentManager().findFragmentById(R.id.activity_frame);
                if (f != null) {
                    f.onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
                break;
            case REQUEST_READ_PHONE_NUMBERS_PERMISSION:
                UserManager userManager = AppManager.getInstance().getSessionData().getUserManager();
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    try {
                        String mPhoneNumber = tMgr.getLine1Number();
                        userManager.setUid(mPhoneNumber);
                    } catch (SecurityException ex) {
                        // continue
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
