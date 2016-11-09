package com.mahoucoder.misakagate.widgets;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.mahoucoder.misakagate.R;

/**
 * Created by jamesji on 9/11/2016.
 */

public class ChooseResolutionDialogFragment extends DialogFragment {

    private CharSequence[] mOptions;
    private ChooseResolutionDialogClickListener mListener;

    public interface ChooseResolutionDialogClickListener {
        void itemClicked(int itemIndex);
    }

    public void setData(CharSequence[] options, ChooseResolutionDialogClickListener listener) {
        mOptions = options;
        mListener = listener;
    }

    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            mListener.itemClicked(i);
        }
    };

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.choose_resolution)
                .setItems(mOptions, listener)
                .create();
    }
}
