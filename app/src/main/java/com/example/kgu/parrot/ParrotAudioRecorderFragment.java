package com.example.kgu.parrot;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by kgu on 3/21/18.
 */

public class ParrotAudioRecorderFragment extends Fragment {
    private static final String LOG_TAG = "ParrotAudioRecorder";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final int STATE_READY = 0;
    private static final int STATE_PLAYING = 1;
    private static final int STATE_RECORDING = 2;
    private static String mFileName;
    private int mState = STATE_READY;
    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = { Manifest.permission.RECORD_AUDIO};

    class PlayButton {
        ImageButton mButton;

        View.OnClickListener clicker = new View.OnClickListener() {
            public void onClick(View v) {
                switch (mState) {
                    case STATE_RECORDING:
                        onRecord(false);
                        break;
                    case STATE_PLAYING:
                        onPlay(false);
                        break;
                }
                onPlay(true);
                mState = STATE_PLAYING;
            }
        };

        public PlayButton(ImageButton button) {
            mButton = button;
            button.setOnClickListener(clicker);
//            File file = new File(mFileName);
//            button.setVisibility( file.exists() ? View.VISIBLE : View.INVISIBLE);
        }
    }

    class RecordButton {
        ImageButton mButton;

        View.OnClickListener clicker = new View.OnClickListener() {
            public void onClick(View v) {
                switch (mState) {
                    case STATE_RECORDING:
                        onRecord(false);
                        break;
                    case STATE_PLAYING:
                        onPlay(false);
                        break;
                }
                onRecord(true);
                mState = STATE_RECORDING;
            }
        };

        public RecordButton(ImageButton button) {
            mButton = button;
            button.setOnClickListener(clicker);
        }
    }

    class StopButton {
        ImageButton mButton;

        View.OnClickListener clicker = new View.OnClickListener() {
            public void onClick(View v) {
                switch (mState) {
                    case STATE_RECORDING:
                        onRecord(false);
                        break;
                    case STATE_PLAYING:
                        onPlay(false);
                        break;
                }
                mState = STATE_READY;
            }
        };

        public StopButton(ImageButton button) {
            mButton = button;
            button.setOnClickListener(clicker);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View top = inflater.inflate(R.layout.fragment_parrot_audio_recorder, container, false);

        Activity activity = getActivity();

        // Record to the external cache directory for visibility
        mFileName = activity.getExternalCacheDir().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";

        ActivityCompat.requestPermissions(activity, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        ImageButton button = top.findViewById(R.id.play_audio);
        new PlayButton(button);

        button = top.findViewById(R.id.record_audio);
        new RecordButton(button);

        button = top.findViewById(R.id.stop_audio);
        new StopButton(button);

        return top;
    }

    @Override
    public void onStop() {
        super.onStop();

        stopRecording();
        stopPlaying();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) {
            getActivity().finish();
        }

    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        try {
            mRecorder.start();
            Toast.makeText(getContext(), "Start recording...", Toast.LENGTH_LONG).show();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            Log.e(LOG_TAG, "start() failed");
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            // mPlayer.start() won't throw IOException
            Log.e(LOG_TAG, "prepare() failed");
        }

    }

    private void stopPlaying() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }
}
