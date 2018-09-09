package com.elmclass.elmclass.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.elmclass.elmclass.R;
import com.elmclass.elmclass.fragment.WebViewFragment;
import com.elmclass.elmclass.manager.AppManager;

import static com.elmclass.elmclass.manager.NetworkManager.KEY_URL;

/**
 *
 * Created by kgu on 5/18/18.
 */

public class WebViewActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppManager.setup(getApplicationContext());

        setContentView(R.layout.activity_frame);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setTitle(getTitle());
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Fragment f = new WebViewFragment();
            String url = getIntent().getStringExtra(KEY_URL);
            if (url != null) {
                Bundle bundle = new Bundle();
                bundle.putString(KEY_URL, url);
                f.setArguments(bundle);
            }
            ft.add(R.id.activity_frame, f);
            ft.commit();
        }
    }

    public void doLogOut() {
        AppManager.getInstance().getSessionData().logout();
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);

        // Don't come back to this activity anymore
        finish();
    }
}
