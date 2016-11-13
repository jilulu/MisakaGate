package com.mahoucoder.misakagate.activities;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;

import com.mahoucoder.misakagate.R;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

public class MainActivity extends BaseActivity implements OnTabSelectListener {

    private Fragment animeListFragment, favoriteAnimeFragment;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(this);
        replaceListFragment();
    }

    @Override
    public void onTabSelected(@IdRes int tabId) {
        switch (tabId) {
            case R.id.bottomItemRecent:
                replaceListFragment();
                break;
            case R.id.bottomItemSubscription:
                replaceFavoriteFragment();
                break;

        }
    }

    private void replaceListFragment() {
        if (animeListFragment == null) {
            animeListFragment = new AnimeListFragment();
        }
        if (!animeListFragment.isAdded()) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, animeListFragment);
            fragmentTransaction.commitAllowingStateLoss();
        }
    }

    private void replaceFavoriteFragment() {
        if (favoriteAnimeFragment == null) {
            favoriteAnimeFragment = new FavoriteAnimeFragment();
        }
        if (!favoriteAnimeFragment.isAdded()) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, favoriteAnimeFragment);
            fragmentTransaction.commitAllowingStateLoss();
        }
    }
}