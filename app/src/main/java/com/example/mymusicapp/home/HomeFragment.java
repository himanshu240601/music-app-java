package com.example.mymusicapp.home;


import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.mymusicapp.MainActivity;
import com.example.mymusicapp.R;
import com.example.mymusicapp.currentsong.NowPlaying;
import com.example.mymusicapp.databinding.FragmentHomeBinding;
import com.example.mymusicapp.home.adapters.MySongListAdapter;
import com.example.mymusicapp.models.MusicModel;

import java.util.Calendar;

public class HomeFragment extends Fragment {

    @SuppressLint("StaticFieldLeak")
    public static FragmentHomeBinding fragmentHomeBinding;

    private void setUserGreeting() {
        Calendar c = Calendar.getInstance();
        int time = c.get(Calendar.HOUR_OF_DAY);

        if(time<12){
            fragmentHomeBinding.greetUser.setText(R.string.good_morning);
        }else if(time<16){
            fragmentHomeBinding.greetUser.setText(R.string.good_afternoon);
        }else{
            fragmentHomeBinding.greetUser.setText(R.string.good_evening);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);

        setUserGreeting();

        fragmentHomeBinding.iconFavourite.setOnClickListener(view ->{
            Drawable iconDrawable = (NowPlaying.isFav)
                    ? ContextCompat.getDrawable(requireContext(), R.drawable.icon_like)
                    : ContextCompat.getDrawable(requireContext(), R.drawable.icon_like_solid);
            fragmentHomeBinding.iconFavourite.setImageDrawable(iconDrawable);
            NowPlaying.isFav = !NowPlaying.isFav;

        });
        fragmentHomeBinding.iconPausePlay.setOnClickListener(view ->{
            Drawable iconDrawable = (NowPlaying.isPlaying)
                    ? ContextCompat.getDrawable(requireContext(), R.drawable.icon_play)
                    : ContextCompat.getDrawable(requireContext(), R.drawable.icon_pause);
            fragmentHomeBinding.iconPausePlay.setImageDrawable(iconDrawable);

            if(NowPlaying.isPlaying){
                NowPlaying.mediaPlayer.pause();
            }else{
                if(NowPlaying.mediaPlayer.isPlaying()){
                    NowPlaying.mediaPlayer.start();
                }else{
                    new NowPlaying().playSong();
                }
            }

            NowPlaying.isPlaying = !NowPlaying.isPlaying;
        });

        fragmentHomeBinding.currentSongCard.setOnClickListener(view -> {
            new NowPlaying().updateCurrentSongNowPlaying();
            MainActivity.fragmentFunctions.openFragment(MainActivity.fragmentFunctions.playingNowFragment);
        });

        setSongsAdapter();

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyMusicApp", MODE_PRIVATE);
        String path = sharedPreferences.getString("last_played_song", null);
        if(path!=null){
            for(MusicModel musicModel : MainActivity.musicClass){
                if(path.equals(musicModel.getPath())){
                    new NowPlaying(musicModel, requireActivity());
                    break;
                }
            }
        }else{
            fragmentHomeBinding.currentSongCard.setVisibility(View.GONE);
        }

        return fragmentHomeBinding.getRoot();
    }

    private void setSongsAdapter(){
        MySongListAdapter adapter = new MySongListAdapter(MainActivity.musicClass, requireActivity());
        fragmentHomeBinding.recyclerViewSongs.setHasFixedSize(true);
        fragmentHomeBinding.recyclerViewSongs.setAdapter(adapter);
        fragmentHomeBinding.recyclerViewSongs.setLayoutManager(new LinearLayoutManager(requireContext()));
    }
}