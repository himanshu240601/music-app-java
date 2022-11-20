package com.example.mymusicapp;

import static com.example.mymusicapp.MainActivity.mediaPlayer;
import static com.example.mymusicapp.MainActivity.shuffledLastSong;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import java.util.Random;

public class PlayingNowFragment extends Fragment {

    private View view;

    private ImageButton iconBack, iconMenu, iconFav;
    public ImageView iconShuffle, iconPrev, iconPausePlay, iconNext, iconRepeat;
    private ImageView imageCoverArt;
    private TextView textViewSongName, textViewArtistName;
    private TextView textViewStart, textViewEnd;
    private SeekBar seekBar;

    private SharedPreferences sharedPreferences;

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            updateUi();
            if (MainActivity.position != sharedPreferences.getInt("LAST_PLAYED_POS", -1)) {
                if(mediaPlayer!=null && mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                    mediaPlayer.release();
                }
                startNewSong(MainActivity.position);
            }else{
                if(mediaPlayer==null){
                    Uri uri = Uri.parse(MainActivity.musicClass.get(MainActivity.position).getPath());
                    mediaPlayer = MediaPlayer.create(requireContext(), uri);
                    int last_seek_pos = sharedPreferences.getInt("LAST_PLAYED_SEEK", -1);
                    mediaPlayer.seekTo(last_seek_pos);
                }

                playingNow();
                textViewEnd.setText(getDurationInMinutes(mediaPlayer.getDuration()));
                textViewStart.setText(getDurationInMinutes(mediaPlayer.getCurrentPosition()));
                seekBar.setMax(mediaPlayer.getDuration());
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_playing_now, container, false);

        sharedPreferences = requireContext().getSharedPreferences("MUSIC_APP_INFO", Context.MODE_PRIVATE);

        initViews();

        //basic icon on click functionalities
        iconBack.setOnClickListener(view -> {
            ((MainActivity) requireActivity()).hideFragment(((MainActivity) requireActivity()).selected);
            ((MainActivity) requireActivity()).selected = ((MainActivity) requireActivity()).homeFragment;
            ((MainActivity) requireActivity()).showFragment(((MainActivity) requireActivity()).selected);
        });

        iconMenu.setOnClickListener(view -> Toast.makeText(requireContext(), "Menu Dialog", Toast.LENGTH_SHORT).show());

        iconFav.setOnClickListener(view -> {
            if (MainActivity.isFav) {
                iconFav.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_like_icon));
                MainActivity.isFav = false;
            } else {
                iconFav.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_like_solid_icon));
                MainActivity.isFav = true;
            }
        });

        iconShuffle.setOnClickListener(view -> {
            if (MainActivity.isShuffle) {
                Toast.makeText(requireContext(), "Shuffle Off", Toast.LENGTH_SHORT).show();
                iconShuffle.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.shuffle_24));
                MainActivity.isShuffle = false;
                sharedPreferences.edit().putBoolean("SHUFFLE", false).apply();
            } else {
                Toast.makeText(requireContext(), "Shuffle On", Toast.LENGTH_SHORT).show();
                iconShuffle.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.shuffle_on_24));
                MainActivity.isShuffle = true;
                sharedPreferences.edit().putBoolean("SHUFFLE", true).apply();
            }
        });

        iconPrev.setOnClickListener(view -> {
            mediaPlayer.stop();
            mediaPlayer.release();
            if(MainActivity.isRepeat){
                //do nothing
            }
            else if(MainActivity.isShuffle && !shuffledLastSong.isEmpty()){
                MainActivity.position = shuffledLastSong.remove(shuffledLastSong.size()-1);
            }
            else{
                if (MainActivity.position > 0) {
                    --MainActivity.position;
                } else {
                    MainActivity.position = MainActivity.musicClass.size() - 1;
                }
            }
            updateUi();
            startNewSong(MainActivity.position);
        });

        iconPausePlay.setOnClickListener(view -> {
            if (MainActivity.isPlaying) {
                iconPausePlay.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_play_icon_lg));
                MainActivity.isPlaying = false;
                mediaPlayer.pause();
            } else {
                iconPausePlay.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_pause_icon_lg));
                MainActivity.isPlaying = true;
                mediaPlayer.start();
            }
        });

        iconNext.setOnClickListener(view -> {
            mediaPlayer.stop();
            mediaPlayer.release();
            iconPausePlay.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_play_icon_lg));
            if(MainActivity.isRepeat){
                //do nothing
            }
            else if(MainActivity.isShuffle){
                shuffledLastSong.add(MainActivity.position);
                Random random = new Random();
                MainActivity.position = random.nextInt((MainActivity.musicClass.size() - 1));
            }
            else{
                if (MainActivity.position < MainActivity.musicClass.size() - 1) {
                    ++MainActivity.position;
                }else {
                    MainActivity.position = 0;
                }
            }
            updateUi();
            startNewSong(MainActivity.position);
        });

        iconRepeat.setOnClickListener(view -> {
            if (MainActivity.isRepeat) {
                Toast.makeText(requireContext(), "Repeat Off", Toast.LENGTH_SHORT).show();
                iconRepeat.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.repeat_24));
                MainActivity.isRepeat = false;
                sharedPreferences.edit().putBoolean("REPEAT", false).apply();
            } else {
                Toast.makeText(requireContext(), "Repeat On", Toast.LENGTH_SHORT).show();
                iconRepeat.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.repeat_on_24));
                MainActivity.isRepeat = true;
                sharedPreferences.edit().putBoolean("REPEAT", true).apply();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(seekBar.getProgress()<mediaPlayer.getDuration()){
                    mediaPlayer.seekTo(seekBar.getProgress());
                }else{
                    iconNext.performClick();
                }
            }
        });

        return view;
    }

    public void playingNow(){
        final Handler handler = new Handler();
        final int delay = 1000;
        handler.postDelayed(new Runnable() {
            @SuppressLint("CommitPrefEdits")
            @Override
            public void run() {
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = mediaPlayer.getCurrentPosition();
                String currentTime = getDurationInMinutes(mediaPlayer.getCurrentPosition());
                textViewStart.setText(currentTime);
                seekBar.setProgress(currentPosition);
                if(currentPosition == totalDuration) {
                    handler.removeCallbacks(this);
                }
                handler.postDelayed(this, delay);
                sharedPreferences.edit().putInt("LAST_PLAYED_SEEK", currentPosition).apply();

                if(mediaPlayer!=null){
                    mediaPlayer.setOnCompletionListener(mediaPlayer -> iconNext.performClick());
                }
            }
        }, delay);
    }

    public String getDurationInMinutes(int ms){
        long minutes = (ms/1000)/60;
        long seconds = (ms/1000)%60;
        String min=String.valueOf(minutes),sec=String.valueOf(seconds);
        if(minutes<10){
            min = "0"+minutes;
        }
        if(seconds<10){
            sec = "0"+seconds;
        }
        return min+":"+sec;
    }

    @SuppressLint("SetTextI18n")
    public void startNewSong(int position){
        Uri uri = Uri.parse(MainActivity.musicClass.get(position).getPath());
        mediaPlayer = MediaPlayer.create(requireContext(), uri);
        iconPausePlay.setImageResource(R.drawable.ic_pause_icon_lg);
        mediaPlayer.start();

        seekBar.setMax(mediaPlayer.getDuration());
        playingNow();

        textViewEnd.setText(getDurationInMinutes(mediaPlayer.getDuration()));

        MainActivity.isPlaying = true;
        sharedPreferences.edit().putInt("LAST_PLAYED_POS", position).apply();
    }

    public void updateUi(){
        if(MainActivity.position!=-1){
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(MainActivity.musicClass.get(MainActivity.position).getPath());

            Glide.with(this).load(mediaMetadataRetriever.getEmbeddedPicture()).into(imageCoverArt);
            textViewSongName.setText(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
            textViewArtistName.setText(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));

            if(MainActivity.isPlaying){
                iconPausePlay.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_pause_icon_lg));
            }else{
                iconPausePlay.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_play_icon_lg));
            }

            if(MainActivity.isRepeat){
                iconRepeat.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.repeat_on_24));
            }

            if(MainActivity.isShuffle){
                iconShuffle.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.shuffle_on_24));
            }

            if(MainActivity.isFav){
                iconFav.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_like_solid_icon));
            }

            ((MainActivity) requireActivity()).homeFragment.setCurrentSongData();
        }
    }

    //initializing all the views in the ui
    public void initViews(){
        iconBack = view.findViewById(R.id.iconBack);
        iconMenu = view.findViewById(R.id.iconMenu);
        iconFav = view.findViewById(R.id.iconFav);

        iconShuffle = view.findViewById(R.id.iconShuffle);
        iconPrev = view.findViewById(R.id.iconPrev);
        iconPausePlay = view.findViewById(R.id.iconPausePlay);
        iconNext = view.findViewById(R.id.iconNext);
        iconRepeat = view.findViewById(R.id.iconRepeat);

        imageCoverArt = view.findViewById(R.id.songCoverArt);

        textViewSongName = view.findViewById(R.id.textViewSongName);
        textViewArtistName = view.findViewById(R.id.textViewArtistName);

        textViewStart = view.findViewById(R.id.startTime);
        textViewEnd = view.findViewById(R.id.endTime);

        seekBar = view.findViewById(R.id.seekBar);
    }
}