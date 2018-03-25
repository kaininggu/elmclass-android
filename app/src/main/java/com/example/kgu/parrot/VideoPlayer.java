package com.example.kgu.parrot;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * Created by kgu on 3/20/18.
 */

public class VideoPlayer {
    private MediaPlayer mPlayer;

    public void stop() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }
    public void play(Context c) {
        // Keep exactly one MediaPlayer around
        stop();
        mPlayer = MediaPlayer.create(c, R.raw.venice);

        // Keep the MediaPlayer around only as long as it is playing something.
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                stop(); }
        });

        mPlayer.start();
    }
}
