package com.example.mymusicapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MySongListAdapter extends RecyclerView.Adapter<MySongListAdapter.ViewHolder> {

    private final ArrayList<MusicModel> mySongsFile;

    private final Context context;

    public MySongListAdapter(Context context, ArrayList<MusicModel> mySongsFile){
        this.context = context;
        this.mySongsFile = mySongsFile;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mylist, parent, false);
        return new ViewHolder(view);
    }


    @SuppressLint({"ResourceAsColor", "ResourceType"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Glide.with(context).load(mySongsFile.get(position).getBitmap()).into(holder.icon);
        holder.title.setText(mySongsFile.get(position).getTitle());
        holder.subtitle.setText(mySongsFile.get(position).getArtist());

        holder.parent_item.setOnClickListener(view -> {
            MainActivity.position = position;
            ((MainActivity) context).hideFragment(((MainActivity) context).selected);
            ((MainActivity) context).selected = ((MainActivity) context).playingNowFragment;
            ((MainActivity) context).showFragment(((MainActivity) context).selected);
        });

        holder.itemMenu.setOnClickListener(view -> Toast.makeText(context, "Song Menu", Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return mySongsFile.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private final TextView title;
        private final TextView subtitle;
        private final ImageView icon;
        private final ConstraintLayout parent_item;
        private final ImageButton itemMenu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent_item = itemView.findViewById(R.id.parent_card);
            icon = itemView.findViewById(R.id.icon);
            title = itemView.findViewById(R.id.title);
            subtitle = itemView.findViewById(R.id.subtitle);
            itemMenu = itemView.findViewById(R.id.itemMenu);
        }
    }
}