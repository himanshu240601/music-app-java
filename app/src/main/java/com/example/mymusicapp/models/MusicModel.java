package com.example.mymusicapp.models;

import android.graphics.drawable.Drawable;

import com.example.mymusicapp.utility.TimeClass;

public class MusicModel {
    String title, artist, album, path, duration;
    Drawable image;

    public MusicModel(String title, String artist, String album, String duration, String path, Drawable image) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.path = path;
        this.image = image;
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

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
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
}
