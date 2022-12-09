package com.example.mymusicapp.currentsong;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.mymusicapp.MainActivity;
import com.example.mymusicapp.R;
import com.example.mymusicapp.home.HomeFragment;
import com.example.mymusicapp.models.ControlModel;
import com.example.mymusicapp.models.MusicModel;
import com.example.mymusicapp.playing.PlayingNowFragment;
import com.example.mymusicapp.utility.TimeClass;

import java.util.ArrayList;

public class NowPlaying {
    public static MediaPlayer mediaPlayer = new MediaPlayer();
    public static MusicModel currentSong;
    public static int position;

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    public static ArrayList<Integer> shuffledSongs = new ArrayList<>();

    public NowPlaying(){
    }

    public NowPlaying(MusicModel currentSong, Context context) {
        NowPlaying.currentSong = currentSong;
        NowPlaying.context = context;
        updateCurrentSong();
    }

    /* app settings */
    public static boolean isShuffle = false;
    public static boolean isRepeat = false;

    public static boolean isPlaying = false;

    public static ControlModel controlModel = new ControlModel();

    public static boolean songInitialized = false;

    /* playing song*/
    public void playSong(){
        songInitialized = true;
        Uri uri = Uri.parse(currentSong.getPath());
        NowPlaying.mediaPlayer = MediaPlayer.create(context, uri);
        NowPlaying.mediaPlayer.start();

        PlayingNowFragment.playingNowBinding.seekBar.setMax(NowPlaying.mediaPlayer.getDuration());

        playingNow();

        isPlaying = true;
        checkPlayPause();
    }
    public void playingNow(){
        final Handler handler = new Handler();
        final int delay = 1000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = mediaPlayer.getCurrentPosition();
                String currentTime = new TimeClass().getDurationInMinutes(currentPosition);
                PlayingNowFragment.playingNowBinding.startTime.setText(currentTime);
                PlayingNowFragment.playingNowBinding.seekBar.setProgress(currentPosition);
                if(currentPosition == totalDuration) {
                    handler.removeCallbacks(this);
                }
                handler.postDelayed(this, delay);

                if(NowPlaying.mediaPlayer!=null){
                    NowPlaying.mediaPlayer.setOnCompletionListener(mediaPlayer -> PlayingNowFragment.playingNowBinding.iconNext.performClick());
                }
            }
        }, delay);
    }


    /* controls */
    private void setPlayPause(Drawable playPause) {
        controlModel.setPlayPause(playPause);
    }

    public void checkPlayPause(){
        if(isPlaying){
            setPlayPause(ContextCompat.getDrawable(context, R.drawable.icon_pause));
        }else{
            setPlayPause(ContextCompat.getDrawable(context, R.drawable.icon_play));
        }
    }

    public void checkShuffle(){
        if(isShuffle){
            controlModel.setShuffle(ContextCompat.getDrawable(context, R.drawable.icon_shuffle_on));
        }else{
            controlModel.setShuffle(ContextCompat.getDrawable(context, R.drawable.icon_shuffle));
        }
    }

    public void checkRepeat(){
        if(isRepeat){
            controlModel.setRepeat(ContextCompat.getDrawable(context, R.drawable.icon_repeat_on));
        }else{
            controlModel.setRepeat(ContextCompat.getDrawable(context, R.drawable.icon_repeat));
        }
    }

    public void checkFavorite(SharedPreferences sharedPreferences, boolean fromClick){
        boolean fav = sharedPreferences.getBoolean(NowPlaying.currentSong.getPath() + "_isFavourite", false);
        if(fromClick){
            String alert;
            if (!fav) {
                controlModel.setFavorite(ContextCompat.getDrawable(context, R.drawable.icon_like_solid));
                alert = "Added to Favourites";
                addToFavourites(sharedPreferences);
            }
            else {
                controlModel.setFavorite(ContextCompat.getDrawable(context, R.drawable.icon_like));
                alert = "Removed from Favourites";
                removeFromFavourites(sharedPreferences);
            }
            Toast.makeText(context, alert, Toast.LENGTH_SHORT).show();
        }else{
            if (fav)
            {
                controlModel.setFavorite(ContextCompat.getDrawable(context, R.drawable.icon_like_solid));
            }
            else {
                controlModel.setFavorite(ContextCompat.getDrawable(context, R.drawable.icon_like));
            }
        }
    }

    /* update ui */
    public void updateCurrentSong(){
        HomeFragment.fragmentHomeBinding.setCurrentSong(currentSong);
        HomeFragment.fragmentHomeBinding.setControls(controlModel);

        checkPlayPause();

        position =  MainActivity.musicClass.indexOf(currentSong);

        SharedPreferences sharedPreferences = context.getSharedPreferences("MyMusicApp", MODE_PRIVATE);
        sharedPreferences.edit().putString("last_played_song", currentSong.getPath()).apply();

        checkFavorite(sharedPreferences, false);
    }

    public void updateCurrentSongNowPlaying(){
        PlayingNowFragment.playingNowBinding.setCurrentSong(currentSong);

        PlayingNowFragment.playingNowBinding.setControls(controlModel);

        checkShuffle();

        checkRepeat();
    }

    private void addToFavourites(SharedPreferences sharedPreferences) {
        sharedPreferences.edit().putBoolean(currentSong.getPath()+"_isFavourite", true).apply();
    }

    private void removeFromFavourites(SharedPreferences sharedPreferences) {
        sharedPreferences.edit().remove(currentSong.getPath()+"_isFavourite").apply();
    }
}
