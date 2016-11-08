package com.mahoucoder.misakagate.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mahoucoder.misakagate.R;
import com.mahoucoder.misakagate.api.models.Thread;
import com.mahoucoder.misakagate.utils.GateUtils;

public class PlaybackActivity extends AppCompatActivity {

    private String videoURL;
    private Thread anime;

    private static final String INTENT_KEY_URL = "URL";
    private static final String INTENT_KEY_ANIME = "ANIME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playback);

        getIntentExtra();

        TextView mTextView = new TextView(PlaybackActivity.this);
        mTextView.setText(videoURL);

        ViewGroup rootView = (ViewGroup) findViewById(R.id.activity_playback);
        rootView.addView(mTextView);
    }

    private void getIntentExtra() {
        videoURL = GateUtils.convertJsonFeed(getIntent().getStringExtra(INTENT_KEY_URL));
        anime = (Thread) getIntent().getSerializableExtra(INTENT_KEY_ANIME);
    }

    public static Intent buildLaunchIntent(Context context, Thread anime, String videoURL) {
        Intent intent = new Intent(context, PlaybackActivity.class);
        intent.putExtra(INTENT_KEY_URL, videoURL);
        intent.putExtra(INTENT_KEY_ANIME, anime);
        return intent;
    }
}
