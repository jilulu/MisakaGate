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
    private android.app.Fragment prefFragment;

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
            case R.id.bottomItemPreferences:
                replacePrefFragment();
                break;
        }
    }

    private void replaceListFragment() {
        if (animeListFragment == null) {
            animeListFragment = new AnimeListFragment();
        }
        if (prefFragment != null && prefFragment.isAdded()) {
            getFragmentManager().beginTransaction().remove(prefFragment).commitAllowingStateLoss();
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
        if (prefFragment != null && prefFragment.isAdded()) {
            getFragmentManager().beginTransaction().remove(prefFragment).commitAllowingStateLoss();
        }
        if (!favoriteAnimeFragment.isAdded()) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, favoriteAnimeFragment);
            fragmentTransaction.commitAllowingStateLoss();
        }
    }

    private void replacePrefFragment() {
        if (prefFragment == null) {
            prefFragment = new PrefsFragment();
        }
        if (!prefFragment.isAdded()) {
            FragmentTransaction preTransaction = getSupportFragmentManager().beginTransaction();
            if (animeListFragment != null && animeListFragment.isAdded()) {
                preTransaction.remove(animeListFragment);
            }
            if (favoriteAnimeFragment != null && favoriteAnimeFragment.isAdded()) {
                preTransaction.remove(favoriteAnimeFragment);
            }
            preTransaction.commitAllowingStateLoss();

            android.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, prefFragment);
            fragmentTransaction.commitAllowingStateLoss();
        }
    }
}