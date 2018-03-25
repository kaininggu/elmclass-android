package com.example.kgu.parrot;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import static com.example.kgu.parrot.ParrotDetailActivity.ARG_ITEM_ID;
import static com.example.kgu.parrot.ParrotDetailActivity.PACKAGE_NAME;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ParrotListActivity}
 * in two-pane mode (on tablets) or a {@link ParrotDetailActivity}
 * on handsets.
 */
public class ParrotVideoPlayerFragment extends Fragment {
    private String mItemId;
    private VideoView mSimpleVideoView;
    private MediaController mMediaController;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View top = inflater.inflate(R.layout.fragment_parrot_video_player, container, false);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItemId = getArguments().getString(ARG_ITEM_ID);
        }

        final Context context = getContext();
        mSimpleVideoView = top.findViewById(R.id.video_view);

        // create an object of media controller: http://abhiandroid.com/ui/videoview
        if (mMediaController == null) {
            mMediaController = new MediaController(context) {
                @Override
                public void hide() {
                    show();
                }
            };
            View videoFrame = top.findViewById(R.id.video_container);
            mMediaController.setAnchorView(videoFrame);
        }

        // initiate a video view
        Uri uri;
        if ("1".equals(mItemId)) {
            uri = Uri.parse("http://abhiandroid-8fb4.kxcdn.com/ui/wp-content/uploads/2016/04/videoviewtestingvideo.mp4");
        } else {
            uri = Uri.parse("android.resource://" + PACKAGE_NAME + "/" + R.raw.venice);
        }
        mSimpleVideoView.setVideoURI(uri);
        mSimpleVideoView.setMediaController(mMediaController);

        // start a video
        mSimpleVideoView.start();

        // implement on completion listener on video view
        mSimpleVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Toast.makeText(context, "Thank You!", Toast.LENGTH_LONG).show(); // display a toast when an video is completed
            }
        });
        mSimpleVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(context, "Oops An Error Occur While Playing Video...!!!", Toast.LENGTH_LONG).show(); // display a toast when an error is occured while playing an video
                return false;
            }
        });

        return top;
    }

    @Override
    public void onStop() {
        if (mSimpleVideoView != null) {
            mSimpleVideoView.stopPlayback();
        }
        super.onStop();
    }
}