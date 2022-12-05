package com.example.mymusicapp.playing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.mymusicapp.MainActivity;
import com.example.mymusicapp.R;
import com.example.mymusicapp.currentsong.NowPlaying;
import com.example.mymusicapp.databinding.FragmentPlayingNowBinding;
import com.example.mymusicapp.utility.TimeClass;

import java.util.Random;

public class PlayingNowFragment extends Fragment {

    @SuppressLint("StaticFieldLeak")
    public static FragmentPlayingNowBinding playingNowBinding;

    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        playingNowBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_playing_now, container, false);

        sharedPreferences = requireContext().getSharedPreferences("MyMusicApp", Context.MODE_PRIVATE);

        setControls();

        playingNowBinding.iconBack.setOnClickListener(view-> MainActivity.fragmentFunctions.openFragment(MainActivity.fragmentFunctions.homeFragment));

        playingNowBinding.iconMenu.setOnClickListener(view -> Toast.makeText(requireContext(), "Menu Dialog", Toast.LENGTH_SHORT).show());

        playingNowBinding.iconFav.setOnClickListener(view ->{
            Drawable iconDrawable = (NowPlaying.isFav)
                    ? ContextCompat.getDrawable(requireContext(), R.drawable.icon_like)
                    : ContextCompat.getDrawable(requireContext(), R.drawable.icon_like_solid);
            playingNowBinding.iconFav.setImageDrawable(iconDrawable);
            String alert = NowPlaying.isFav ? "Added to Favourites" : "Removed from Favourites";
            Toast.makeText(requireContext(), alert, Toast.LENGTH_SHORT).show();
            NowPlaying.isFav = !NowPlaying.isFav;

        });

        playingNowBinding.iconShuffle.setOnClickListener(view ->{
            Drawable iconDrawable = (NowPlaying.isShuffle)
                    ? ContextCompat.getDrawable(requireContext(), R.drawable.icon_shuffle)
                    : ContextCompat.getDrawable(requireContext(), R.drawable.icon_shuffle_on);
            playingNowBinding.iconShuffle.setImageDrawable(iconDrawable);
            String alert = NowPlaying.isShuffle ? "Shuffle On" : "Shuffle Off";
            Toast.makeText(requireContext(), alert, Toast.LENGTH_SHORT).show();
            NowPlaying.isShuffle = !NowPlaying.isShuffle;
            sharedPreferences.edit().putBoolean("SHUFFLE", NowPlaying.isShuffle).apply();
        });

        playingNowBinding.iconPausePlay.setOnClickListener(view ->{
            Drawable iconDrawable = (NowPlaying.isPlaying)
                    ? ContextCompat.getDrawable(requireContext(), R.drawable.icon_play_lg)
                    : ContextCompat.getDrawable(requireContext(), R.drawable.icon_pause_lg);
            playingNowBinding.iconPausePlay.setImageDrawable(iconDrawable);

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

        playingNowBinding.iconRepeat.setOnClickListener(view ->{
            Drawable iconDrawable = (NowPlaying.isRepeat)
                    ? ContextCompat.getDrawable(requireContext(), R.drawable.icon_repeat)
                    : ContextCompat.getDrawable(requireContext(), R.drawable.icon_repeat_on);
            playingNowBinding.iconRepeat.setImageDrawable(iconDrawable);
            String alert = NowPlaying.isRepeat ? "Repeat On" : "Repeat Off";
            Toast.makeText(requireContext(), alert, Toast.LENGTH_SHORT).show();
            NowPlaying.isRepeat = !NowPlaying.isRepeat;
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

    private void setControls() {
        if(NowPlaying.isShuffle){
            playingNowBinding.iconShuffle.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.icon_shuffle_on)
            );
        }
        if(NowPlaying.isRepeat){
            playingNowBinding.iconRepeat.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.icon_repeat_on)
            );
        }
    }

    public void playingNow(){
        final Handler handler = new Handler();
        final int delay = 1000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int totalDuration = NowPlaying.mediaPlayer.getDuration();
                int currentPosition = NowPlaying.mediaPlayer.getCurrentPosition();
                String currentTime = new TimeClass().getDurationInMinutes(NowPlaying.mediaPlayer.getCurrentPosition());
                playingNowBinding.startTime.setText(currentTime);
                playingNowBinding.seekBar.setProgress(currentPosition);
                if(currentPosition == totalDuration) {
                    handler.removeCallbacks(this);
                }
                handler.postDelayed(this, delay);

//                sharedPreferences.edit().putInt("last_played_seek", currentPosition).apply();

                if(NowPlaying.mediaPlayer!=null){
                    NowPlaying.mediaPlayer.setOnCompletionListener(mediaPlayer -> playingNowBinding.iconNext.performClick());
                }
            }
        }, delay);
    }
}