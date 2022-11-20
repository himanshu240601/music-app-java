package com.example.mymusicapp;

import static com.example.mymusicapp.MainActivity.mediaPlayer;
import static com.example.mymusicapp.MainActivity.musicClass;

import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.Calendar;
import java.util.Collections;

public class HomeFragment extends Fragment {

    private View view;

    private RecyclerView recyclerView;

    private ConstraintLayout currentSong;
    private ImageView coverArt;
    private TextView textViewSong, textViewArtist;
    private ImageView iconFav, iconPlayPause;
    private TextView greeting;

    private final MediaMetadataRetriever mmr = new MediaMetadataRetriever();

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            setCurrentSongData();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);

        initViews();

        setGreetingTitle();

        currentSong.setOnClickListener(view -> {
            ((MainActivity) requireActivity()).hideFragment(((MainActivity) requireActivity()).selected);
            ((MainActivity) requireActivity()).selected = ((MainActivity) requireActivity()).playingNowFragment;
            ((MainActivity) requireActivity()).showFragment(((MainActivity) requireActivity()).selected);
        });

        iconFav.setOnClickListener(view -> {
            if(MainActivity.isFav){
                iconFav.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_like_icon));
                MainActivity.isFav = false;
            }else{
                iconFav.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_like_solid_icon));
                MainActivity.isFav = true;
            }
        });

        iconPlayPause.setOnClickListener(view -> {
            if(MainActivity.isPlaying){
                iconPlayPause.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_play_icon_lg));
                MainActivity.isPlaying = false;
                mediaPlayer.pause();
            }else{
                iconPlayPause.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_pause_icon_lg));
                MainActivity.isPlaying = true;
                mediaPlayer.start();
            }
        });

        if(!musicClass.isEmpty()){
            MySongListAdapter adapter = new MySongListAdapter(requireActivity(), musicClass);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        }

        return view;
    }

    private void setGreetingTitle() {
        Calendar c = Calendar.getInstance();
        int time = c.get(Calendar.HOUR_OF_DAY);

        if(time>=0 && time<12){
            greeting.setText(R.string.good_morning);
        }else if(time>=12 && time<16){
            greeting.setText(R.string.good_afternoon);
        }else{
            greeting.setText(R.string.good_evening);
        }
    }

    public void setCurrentSongData() {
        if(MainActivity.position!=-1){
            mmr.setDataSource(musicClass.get(MainActivity.position).getPath());

            currentSong.setVisibility(View.VISIBLE);

            Glide.with(this).load(mmr.getEmbeddedPicture()).into(coverArt);
            textViewSong.setText(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
            textViewArtist.setText(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));

            //change icon favourite
            if(MainActivity.isFav) {
                iconFav.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_like_solid_icon));
            }

            //change icon play pause
            if(MainActivity.isPlaying){
                iconPlayPause.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_pause_icon_lg));
            }else{
                iconPlayPause.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_play_icon_lg));
            }
        }else{
            currentSong.setVisibility(View.GONE);
        }
    }

    //initializing all the views in ui
    void initViews(){
        recyclerView = view.findViewById(R.id.recyclerView);

        currentSong = view.findViewById(R.id.constraintViewCurrentSong);

        coverArt = view.findViewById(R.id.imageViewAlbumArt);

        textViewSong = view.findViewById(R.id.textViewSong);
        textViewArtist = view.findViewById(R.id.textViewArtist);

        iconFav = view.findViewById(R.id.iconFavourite);
        iconPlayPause = view.findViewById(R.id.iconPausePlay);

        greeting = view.findViewById(R.id.greeting);
    }
}