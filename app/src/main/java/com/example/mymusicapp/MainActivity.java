package com.example.mymusicapp;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.mymusicapp.currentsong.NowPlaying;
import com.example.mymusicapp.fragmentfunctions.FragmentFunctions;
import com.example.mymusicapp.models.MusicModel;

import java.util.ArrayList;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity{

    public static final FragmentFunctions fragmentFunctions = new FragmentFunctions();

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 121) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callFunctionsForSongsAndFragments();
            } else {
                finishAffinity();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_main);

        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    121
            );
        }else{
            callFunctionsForSongsAndFragments();
        }
    }

    private void callFunctionsForSongsAndFragments(){
        try{
            getMusicFiles();
        }catch (Exception e){
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }

        fragmentFunctions.createFragments(getSupportFragmentManager());

        fragmentFunctions.openFragment(fragmentFunctions.homeFragment);

        checkPrefs();
    }

    private void checkPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyMusicApp", MODE_PRIVATE);
        NowPlaying.isShuffle = sharedPreferences.getBoolean("SHUFFLE", false);
        NowPlaying.isRepeat = sharedPreferences.getBoolean("REPEAT", false);
    }

    public static ArrayList<MusicModel> musicClass = new ArrayList<>();

    private void getMusicFiles(){
        String selection = MediaStore.Audio.Media.DATA + " != 0";

        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,
        };

        Cursor cursor = this.managedQuery(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null);

        while(cursor.moveToNext()){
            if(!cursor.getString(2).contains("Recordings")){
                MusicModel musicModel = new MusicModel(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3)
                );
                musicModel.setImageData(this);
                musicClass.add(musicModel);
            }
        }
        musicClass.sort(Comparator.comparing(MusicModel::getTitle));
    }

    @Override
    public void onBackPressed() {
        if(fragmentFunctions.selected==fragmentFunctions.homeFragment){
            moveTaskToBack(true);
        }else{
            fragmentFunctions.openFragment(fragmentFunctions.homeFragment);
        }
    }
}