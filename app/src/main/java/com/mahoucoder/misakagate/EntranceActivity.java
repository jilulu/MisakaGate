package com.mahoucoder.misakagate;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.mahoucoder.misakagate.activities.AnimeListActivity;
import com.mahoucoder.misakagate.utils.GateUtils;
import com.mahoucoder.misakagate.widgets.RequestPermissionView;

public class EntranceActivity extends AppCompatActivity implements RequestPermissionView.PermissionListener {

    private RequestPermissionView requestPermissionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrance);
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
        gotoAnimeListAndFinish();
    }

    private void gotoAnimeListAndFinish() {
        Intent intent = new Intent(EntranceActivity.this, AnimeListActivity.class);
        startActivity(intent);
        finish();
    }
}
