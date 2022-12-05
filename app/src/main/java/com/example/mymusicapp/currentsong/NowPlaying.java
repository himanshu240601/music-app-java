package com.example.mymusicapp.currentsong;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;

import androidx.core.content.ContextCompat;

import com.example.mymusicapp.MainActivity;
import com.example.mymusicapp.R;
import com.example.mymusicapp.home.HomeFragment;
import com.example.mymusicapp.models.MusicModel;
import com.example.mymusicapp.playing.PlayingNowFragment;

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

    public static boolean isShuffle = false;
    public static boolean isRepeat = false;

    public static boolean isFav = false;
    public static boolean isPlaying = false;

    public void playSong(){
        Uri uri = Uri.parse(currentSong.getPath());
        NowPlaying.mediaPlayer = MediaPlayer.create(context, uri);
        PlayingNowFragment.playingNowBinding.iconPausePlay.setImageResource(R.drawable.icon_pause_lg);
        NowPlaying.mediaPlayer.start();

        PlayingNowFragment.playingNowBinding.seekBar.setMax(NowPlaying.mediaPlayer.getDuration());

        new PlayingNowFragment().playingNow();

        NowPlaying.isPlaying = true;
    }

    public void updateCurrentSong(){
        HomeFragment.fragmentHomeBinding.setCurrentSong(currentSong);

        position =  MainActivity.musicClass.indexOf(currentSong);

        SharedPreferences sharedPreferences = context.getSharedPreferences("MyMusicApp", MODE_PRIVATE);
        sharedPreferences.edit().putString("last_played_song", currentSong.getPath()).apply();
    }

    public void updateCurrentSongNowPlaying(){
        PlayingNowFragment.playingNowBinding.setCurrentSong(NowPlaying.currentSong);
    }
}
