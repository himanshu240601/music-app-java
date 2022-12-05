package com.example.mymusicapp.home.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymusicapp.R;
import com.example.mymusicapp.currentsong.NowPlaying;
import com.example.mymusicapp.databinding.SongListCardBinding;
import com.example.mymusicapp.home.HomeFragment;
import com.example.mymusicapp.models.MusicModel;

import java.util.ArrayList;

public class MySongListAdapter extends RecyclerView.Adapter<MySongListAdapter.ViewHolder> {

    private final ArrayList<MusicModel> mySongsFile;
    private final Context context;

    public MySongListAdapter(ArrayList<MusicModel> mySongsFile, Context context){
        this.mySongsFile = mySongsFile;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        SongListCardBinding songListCardBinding = DataBindingUtil.inflate(layoutInflater, R.layout.song_list_card, parent, false);
        return new ViewHolder(songListCardBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.songListCardBinding.setMusicModel(mySongsFile.get(position));

        holder.songListCardBinding.parentCard.setOnClickListener(view ->{
            new NowPlaying(mySongsFile.get(position), context);
            HomeFragment.fragmentHomeBinding.currentSongCard.setVisibility(View.VISIBLE);
            NowPlaying.mediaPlayer.stop();
            NowPlaying.mediaPlayer.release();
            new NowPlaying().playSong();
        });

        holder.songListCardBinding.itemMenu.setOnClickListener(view -> Toast.makeText(context, "Menu Dialog", Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return mySongsFile.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        SongListCardBinding songListCardBinding;
        public ViewHolder(@NonNull SongListCardBinding songListCardBinding) {
            super(songListCardBinding.getRoot());
            this.songListCardBinding = songListCardBinding;
        }
    }
}