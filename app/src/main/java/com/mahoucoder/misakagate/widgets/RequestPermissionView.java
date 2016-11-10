package com.mahoucoder.misakagate.widgets;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.mahoucoder.misakagate.GateApplication;
import com.mahoucoder.misakagate.R;

/**
 * Created by jamesji on 10/11/2016.
 */

public class RequestPermissionView extends LinearLayout {
    Button mButton;
    public static final int PERMISSION_REQUEST_CODE = 0;
    private PermissionListener mListener;

    public RequestPermissionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_permission_request, RequestPermissionView.this, true);
        mButton = (Button) findViewById(R.id.permission_request_button);

        mButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int phone = ContextCompat.checkSelfPermission(GateApplication.getGlobalContext(), Manifest.permission.READ_PHONE_STATE);
                int storage = ContextCompat.checkSelfPermission(GateApplication.getGlobalContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (phone != PackageManager.PERMISSION_GRANTED || storage != PackageManager.PERMISSION_GRANTED) {
                    if (getContext() instanceof Activity) {
                        ActivityCompat.requestPermissions((Activity) getContext(),
                                new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                PERMISSION_REQUEST_CODE
                        );
                    }
                } else if (mListener != null) {
                    mListener.onPermissionResult(true);
                }
            }
        });
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode != PERMISSION_REQUEST_CODE) {
            return;
        }
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                mListener.onPermissionResult(false);
                return;
            }
        }
        if (mListener != null) {
            mListener.onPermissionResult(true);
        }
    }

    public interface PermissionListener {
        void onPermissionResult(boolean granted);
    }

    public void setPermissionListener(PermissionListener listener) {
        this.mListener = listener;
    }
}