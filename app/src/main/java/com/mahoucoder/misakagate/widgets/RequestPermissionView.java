package com.mahoucoder.misakagate.widgets;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mahoucoder.misakagate.GateApplication;
import com.mahoucoder.misakagate.R;
import com.mahoucoder.misakagate.utils.GateUtils;

/**
 * Created by jamesji on 10/11/2016.
 */

public class RequestPermissionView extends LinearLayout {
    Button mButton;
    public static final int PERMISSION_REQUEST_CODE = 0;
    private PermissionListener mListener;
    private ViewGroup iconGroup;
    private TextView explanationText, mainText;

    public RequestPermissionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_permission_request, RequestPermissionView.this, true);
        mButton = (Button) findViewById(R.id.permission_request_button);
        iconGroup = (ViewGroup) findViewById(R.id.permission_request_icon_group);
        explanationText = (TextView) findViewById(R.id.permission_request_explanation_text);
        mainText = (TextView) findViewById(R.id.permission_request_main_text);

        mButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] permReqList = GateUtils.getMissingPermissionAsArray();
                if (permReqList.length != 0) {
                    if (getContext() instanceof Activity) {
                        ActivityCompat.requestPermissions((Activity) getContext(),
                                permReqList,
                                PERMISSION_REQUEST_CODE
                        );
                        mButton.setEnabled(false);
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
                mButton.setEnabled(true);
                return;
            }
        }

        replaceExplanationWithProgress();

        if (mListener != null) {
            mListener.onPermissionResult(true);
        }
    }

    private void replaceExplanationWithProgress() {
        int heightAndWidth = iconGroup.getMeasuredHeight() + explanationText.getMeasuredHeight();
        float progressBarHeightAndWidth = (int) GateUtils.dp2px(GateApplication.getGlobalContext(), 48);
        int margin = (int) (heightAndWidth - progressBarHeightAndWidth) / 2;
        ProgressBar progressBar = new ProgressBar(getContext());
        progressBar.setIndeterminate(true);
        LayoutParams params = new LayoutParams((int) progressBarHeightAndWidth, (int) progressBarHeightAndWidth);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.setMargins(margin, margin, margin, margin);
        removeView(iconGroup);
        removeView(explanationText);
        addView(progressBar, 1, params);
        mainText.setText(GateApplication.getGlobalContext().getString(R.string.permission_request_result_thanks));
    }

    public interface PermissionListener {
        void onPermissionResult(boolean granted);
    }

    public void setPermissionListener(PermissionListener listener) {
        this.mListener = listener;
    }
}