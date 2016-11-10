package com.mahoucoder.misakagate.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.mahoucoder.misakagate.GateApplication;
import com.mahoucoder.misakagate.R;
import com.mahoucoder.misakagate.utils.GateUtils;
import com.mahoucoder.misakagate.widgets.RequestPermissionView;

import java.lang.ref.WeakReference;

public class EntranceActivity extends AppCompatActivity implements RequestPermissionView.PermissionListener {

    private RequestPermissionView requestPermissionView;
    private Handler mainHandler;
    public static final int STAY_TIME = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrance);

        mainHandler = new MainHandler(this);
        if (!GateUtils.checkAllPermissions()) {
            requestPermissionView = (RequestPermissionView) findViewById(R.id.permission_request_group);
            requestPermissionView.setVisibility(View.VISIBLE);
            requestPermissionView.setPermissionListener(this);
        } else {
            gotoAnimeListAndFinish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        requestPermissionView.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            gotoAnimeListAndFinish();
        }
    }

    private void gotoAnimeListAndFinish() {
        mainHandler.sendEmptyMessageDelayed(MainHandler.GOTO_ANIME_LIST_AND_FINISH, STAY_TIME);
    }

    private static class MainHandler extends Handler {
        WeakReference<Activity> activityRef;
        static final int GOTO_ANIME_LIST_AND_FINISH = 0;

        MainHandler(Activity activityRef) {
            this.activityRef = new WeakReference<Activity>(activityRef);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GOTO_ANIME_LIST_AND_FINISH:
                    if (activityRef.get() != null) {
                        Intent intent = new Intent(GateApplication.getGlobalContext(), AnimeListActivity.class);
                        activityRef.get().startActivity(intent);
                        activityRef.get().finish();
                    }
                    break;
            }
        }
    }
}
