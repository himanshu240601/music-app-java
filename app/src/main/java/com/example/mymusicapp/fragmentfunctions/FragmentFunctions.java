package com.example.mymusicapp.fragmentfunctions;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.mymusicapp.playing.PlayingNowFragment;
import com.example.mymusicapp.R;
import com.example.mymusicapp.home.HomeFragment;

public class FragmentFunctions {

    public HomeFragment homeFragment = new HomeFragment();
    public PlayingNowFragment playingNowFragment = new PlayingNowFragment();
    public Fragment selected = homeFragment;
    public FragmentManager fragmentManager;

    public void createFragments(FragmentManager fragmentManager){
        this.fragmentManager = fragmentManager;
        fragmentManager.beginTransaction().add(R.id.fragmentsContent, homeFragment).hide(homeFragment).commit();
        fragmentManager.beginTransaction().add(R.id.fragmentsContent, playingNowFragment).hide(playingNowFragment).commit();
    }

    public void openFragment(Fragment fragment) {
        hideFragment(selected);
        selected = fragment;
        showFragment(selected);
    }

    private void showFragment(Fragment fragment){
        if(fragment==playingNowFragment){
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_up_in, R.anim.slide_stay)
                    .show(fragment).commit();
        }else{
            fragmentManager.beginTransaction()
                    .show(fragment).commit();
        }
    }
    private void hideFragment(Fragment fragment){
        if(fragment==playingNowFragment){
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_stay, R.anim.slide_down_out)
                    .hide(fragment).commit();
        }else{
            fragmentManager.beginTransaction()
                    .hide(fragment).commit();
        }
    }
}
