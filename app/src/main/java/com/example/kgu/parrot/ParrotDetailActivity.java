package com.example.kgu.parrot;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ParrotListActivity}.
 */
public class ParrotDetailActivity extends AppCompatActivity {
    public static final String ARG_ITEM_ID = "item_id";
    public static final String PACKAGE_NAME = "com.example.kgu.parrot";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_parrot_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            String itemId = getIntent().getStringExtra(ARG_ITEM_ID);
            Bundle arguments = new Bundle();
            arguments.putString(ARG_ITEM_ID, itemId);
            TextView title = findViewById(R.id.title);
            if (title != null) {
                title.setText("Item " + itemId);
            }

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            Fragment f = new ParrotVideoPlayerFragment();
            f.setArguments(arguments);
            ft.add(R.id.video_container, f);

            f = new ParrotAudioRecorderFragment();
            f.setArguments(arguments);
            ft.add(R.id.audio_container, f);

            ft.commit();
        }
    }
}
