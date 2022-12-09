package com.example.mymusicapp.playing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.mymusicapp.MainActivity;
import com.example.mymusicapp.R;
import com.example.mymusicapp.currentsong.NowPlaying;
import com.example.mymusicapp.databinding.FragmentPlayingNowBinding;

import java.util.Random;

public class PlayingNowFragment extends Fragment {

    @SuppressLint("StaticFieldLeak")
    public static FragmentPlayingNowBinding playingNowBinding;

    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        playingNowBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_playing_now, container, false);

        playingNowBinding.textViewSongName.setSelected(true);
        playingNowBinding.textViewArtistName.setSelected(true);

        sharedPreferences = requireContext().getSharedPreferences("MyMusicApp", Context.MODE_PRIVATE);

        playingNowBinding.iconBack.setOnClickListener(view-> MainActivity.fragmentFunctions.openFragment(MainActivity.fragmentFunctions.homeFragment));

        playingNowBinding.iconMenu.setOnClickListener(view -> Toast.makeText(requireContext(), "Menu Dialog", Toast.LENGTH_SHORT).show());

        playingNowBinding.iconFav.setOnClickListener(view -> new NowPlaying().checkFavorite(sharedPreferences, true));

        playingNowBinding.iconShuffle.setOnClickListener(view ->{
            NowPlaying.isShuffle = !NowPlaying.isShuffle;
            if(NowPlaying.isShuffle){
                NowPlaying.shuffledSongs.clear();
            }
            String alert = NowPlaying.isShuffle ? "Shuffle On" : "Shuffle Off";
            new NowPlaying().checkShuffle();
            Toast.makeText(requireContext(), alert, Toast.LENGTH_SHORT).show();
            sharedPreferences.edit().putBoolean("SHUFFLE", NowPlaying.isShuffle).apply();
        });

        playingNowBinding.iconPausePlay.setOnClickListener(view ->{
            if(NowPlaying.songInitialized){
                if(NowPlaying.isPlaying){
                    NowPlaying.mediaPlayer.pause();
                }else{
                    NowPlaying.mediaPlayer.start();
                }
                NowPlaying.isPlaying = !NowPlaying.isPlaying;
                new NowPlaying().checkPlayPause();
            }else{
                new NowPlaying().playSong();
            }
        });

        playingNowBinding.iconRepeat.setOnClickListener(view ->{
            NowPlaying.isRepeat = !NowPlaying.isRepeat;
            String alert = NowPlaying.isRepeat ? "Repeat On" : "Repeat Off";
            new NowPlaying().checkRepeat();
            Toast.makeText(requireContext(), alert, Toast.LENGTH_SHORT).show();
            sharedPreferences.edit().putBoolean("REPEAT", NowPlaying.isRepeat).apply();
        });

        playingNowBinding.iconPrev.setOnClickListener(view -> playNextOrPrev(false));

        playingNowBinding.iconNext.setOnClickListener(view -> playNextOrPrev(true));

        playingNowBinding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(seekBar.getProgress()<NowPlaying.mediaPlayer.getDuration()){
                    NowPlaying.mediaPlayer.seekTo(seekBar.getProgress());
                }else{
                    playingNowBinding.iconNext.performClick();
                }
            }
        });

        return playingNowBinding.getRoot();
    }

    private void playNextOrPrev(boolean isNext) {
        NowPlaying.mediaPlayer.stop();
        NowPlaying.mediaPlayer.release();

        if(!isNext && !NowPlaying.isRepeat && NowPlaying.isShuffle && NowPlaying.shuffledSongs.size()>1){
            NowPlaying.shuffledSongs.remove(NowPlaying.shuffledSongs.size()-1);
            NowPlaying.position = NowPlaying.shuffledSongs.get(NowPlaying.shuffledSongs.size()-1);
        }
        else if(isNext && !NowPlaying.isRepeat && NowPlaying.isShuffle){
            NowPlaying.position = new Random().nextInt((MainActivity.musicClass.size() - 1));
            //update duplicate songs in shuffled songs arraylist
            if(NowPlaying.shuffledSongs.contains(NowPlaying.position)){
                NowPlaying.shuffledSongs.remove(NowPlaying.position);
            }
            NowPlaying.shuffledSongs.add(NowPlaying.position);
        }
        else if(!NowPlaying.isRepeat){
            if(isNext){
                NowPlaying.position = (NowPlaying.position < MainActivity.musicClass.size() - 1) ? NowPlaying.position+1 : 0;
            }else{
                NowPlaying.position = (NowPlaying.position > 0) ? NowPlaying.position-1 : MainActivity.musicClass.size()-1;
            }
        }
        new NowPlaying(MainActivity.musicClass.get(NowPlaying.position), requireContext()).updateCurrentSongNowPlaying();
        new NowPlaying().playSong();
    }
}