package com.example.mymusicapp.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;

import androidx.core.content.ContextCompat;

import com.example.mymusicapp.R;
import com.example.mymusicapp.utility.TimeClass;

public class MusicModel {
    String title, artist, path, duration;
    Drawable image;
    Bitmap bitmap;

    public MusicModel(String title, String artist, String path, String duration) {
        this.title = title;
        this.artist = artist;
        this.path = path;
        this.duration = duration;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDuration() {
        return new TimeClass().getDurationInMinutes(Integer.parseInt(duration));
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public void setImageData(Context context){
        final MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(path);
        Drawable img = ContextCompat.getDrawable(context, R.drawable.icon_music_note);
        try{
            byte[] byteArray = mmr.getEmbeddedPicture();
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            img = new BitmapDrawable(bmp);
        }catch (Exception e){
            e.printStackTrace();
        }
        setImage(img);
    }
}
