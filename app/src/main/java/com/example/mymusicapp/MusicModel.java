package com.example.mymusicapp;

public class MusicModel {
    String title, artist, path;
    byte[] bitmap;

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getPath(){
        return path;
    }

    public byte[] getBitmap() {
        return bitmap;
    }

    public MusicModel(String title, String artist, String path, byte[] bitmap) {
        this.title = title;
        this.artist = artist;
        this.path = path;
        this.bitmap = bitmap;
    }
}
