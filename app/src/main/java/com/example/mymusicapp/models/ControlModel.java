package com.example.mymusicapp.models;

import android.graphics.drawable.Drawable;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

public class ControlModel extends BaseObservable {
    Drawable playPause;

    Drawable shuffle, repeat;

    Drawable favorite;

    public ControlModel(){}

    @Bindable
    public Drawable getPlayPause() {
        return playPause;
    }

    public void setPlayPause(Drawable playPause) {
        this.playPause = playPause;
        notifyPropertyChanged(BR.playPause);
    }

    @Bindable
    public Drawable getShuffle() {
        return shuffle;
    }

    public void setShuffle(Drawable shuffle) {
        this.shuffle = shuffle;
        notifyPropertyChanged(BR.shuffle);
    }

    @Bindable
    public Drawable getRepeat() {
        return repeat;
    }

    public void setRepeat(Drawable repeat) {
        this.repeat = repeat;
        notifyPropertyChanged(BR.repeat);
    }

    @Bindable
    public Drawable getFavorite() {
        return favorite;
    }

    public void setFavorite(Drawable favorite) {
        this.favorite = favorite;
        notifyPropertyChanged(BR.favorite);
    }
}
