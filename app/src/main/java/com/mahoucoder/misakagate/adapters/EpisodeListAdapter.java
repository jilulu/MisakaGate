package com.mahoucoder.misakagate.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mahoucoder.misakagate.GateApplication;
import com.mahoucoder.misakagate.R;
import com.mahoucoder.misakagate.activities.GatePlaybackActivity;
import com.mahoucoder.misakagate.api.GateAPI;
import com.mahoucoder.misakagate.api.models.PlaybackInfo;
import com.mahoucoder.misakagate.api.models.PlaybackSource;
import com.mahoucoder.misakagate.api.models.Thread;
import com.mahoucoder.misakagate.utils.GateUtils;
import com.mahoucoder.misakagate.widgets.ChooseResolutionDialogFragment;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by jamesji on 30/10/2016.
 */

public class EpisodeListAdapter extends RecyclerView.Adapter<EpisodeListAdapter.ViewHolder> implements View.OnClickListener {

    private List<String> episodeURLList;
    private Thread anime;

    public EpisodeListAdapter(List<String> episodeURLList) {
        this.episodeURLList = episodeURLList;
    }

    private ProgressDialog progressDialog;

    private static final String FRAGMENT_TAG = "resolution_choose_fragment_tag";

    public void setData(Thread anime) {
        this.anime = anime;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.episode_card, null);
        ViewHolder viewHolder = new ViewHolder(rootView);
        viewHolder.itemView.setOnClickListener(EpisodeListAdapter.this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textView.setText(String.format(Locale.ENGLISH, "%d", position + 1));
        holder.itemView.setTag(episodeURLList.get(position));
    }

    @Override
    public int getItemCount() {
        return this.episodeURLList.size();
    }

    @Override
    public void onClick(final View view) {
        String jsonURL = GateUtils.convertJsonFeed((String) view.getTag());

        progressDialog = ProgressDialog.show(view.getContext(),
                GateApplication.getGlobalContext().getString(R.string.loading),
                GateApplication.getGlobalContext().getString(R.string.loading_res_list));

        GateAPI.getPlaybackInfo(jsonURL, new Callback<PlaybackInfo[]>() {
            @Override
            public void onResponse(Call<PlaybackInfo[]> call, Response<PlaybackInfo[]> response) {
                PlaybackInfo[] body = response.body();

                final List<PlaybackSource> sources = body[0].sources;

                CharSequence[] titles = new String[sources.size()];
                for (int i = 0; i < sources.size(); i++) {
                    titles[i] = sources.get(i).label;
                }

                ChooseResolutionDialogFragment.ChooseResolutionDialogClickListener listener = new ChooseResolutionDialogFragment.ChooseResolutionDialogClickListener() {
                    public void itemClicked(int index) {
                        Intent intent = new Intent(GateApplication.getGlobalContext(), GatePlaybackActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(GatePlaybackActivity.PREFER_EXTENSION_DECODERS, false);
                        intent.setData(Uri.parse(sources.get(index).file));
                        intent.setAction(GatePlaybackActivity.ACTION_VIEW);
                        GateApplication.getGlobalContext().startActivity(intent);
                    }
                };

                showResolutionChooseDialog(titles, listener, view.getContext());

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<PlaybackInfo[]> call, Throwable t) {
                t.printStackTrace();
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(GateApplication.getGlobalContext(), GateApplication
                        .getGlobalContext().getString(R.string.loading_res_list_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showResolutionChooseDialog(CharSequence[] titles, ChooseResolutionDialogFragment.ChooseResolutionDialogClickListener listener, Context context) {
        ChooseResolutionDialogFragment resolutionChooseFragment = new ChooseResolutionDialogFragment();
        resolutionChooseFragment.setData(titles, listener);
        if (context instanceof AppCompatActivity) {
            FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(resolutionChooseFragment, FRAGMENT_TAG)
                    .commitAllowingStateLoss();
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.activity_anime_detail_recycler_text);
        }
    }
}
