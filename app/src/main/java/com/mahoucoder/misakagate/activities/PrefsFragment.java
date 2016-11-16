package com.mahoucoder.misakagate.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.widget.Toast;

import com.mahoucoder.misakagate.BuildConfig;
import com.mahoucoder.misakagate.GateApplication;
import com.mahoucoder.misakagate.R;
import com.mahoucoder.misakagate.SubscriptionService;
import com.mahoucoder.misakagate.api.GateAPI;

import rx.Observer;

/**
 * Created by jamesji on 14/11/2016.
 */

public class PrefsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref);
        Preference updates = findPreference("updates");
        updates.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setTitle(R.string.checking_for_updates);
                progressDialog.setMessage(getString(R.string.please_wait));
                progressDialog.setCancelable(false);
                progressDialog.show();
                GateAPI.getNewestVersion(new Observer<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if (progressDialog.isShowing()) {
                            progressDialog.cancel();
                        }
                        Toast.makeText(GateApplication.getGlobalContext(), R.string.latest_build, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(final String s) {
                        if (progressDialog.isShowing()) {
                            progressDialog.cancel();
                        }
                        if (TextUtils.isEmpty(s)) {
                            Toast.makeText(GateApplication.getGlobalContext(), R.string.latest_build, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        new AlertDialog.Builder(getActivity())
                                .setTitle(R.string.new_version_found)
                                .setMessage(getString(R.string.new_version_message)
                                        + s.substring(s.indexOf("v"), s.indexOf("/app-release.apk"))
                                        + getString(R.string.new_version_another_message)
                                        + BuildConfig.VERSION_NAME
                                        + getString(R.string.new_version_yet_another_message))
                                .setPositiveButton(R.string.download, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(s));
                                        startActivity(intent);
                                    }
                                })
                                .setNegativeButton(R.string.cancel, null)
                                .create()
                                .show();
                    }
                });
                return true;
            }
        });

        Preference newEpisodes = findPreference("new_episodes");
        newEpisodes.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(GateApplication.getGlobalContext(), SubscriptionService.class);
                GateApplication.getGlobalContext().startService(intent);
                return true;
            }
        });
    }
}
