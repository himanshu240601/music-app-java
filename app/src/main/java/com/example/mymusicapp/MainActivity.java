package com.example.mymusicapp;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.mymusicapp.currentsong.NowPlaying;
import com.example.mymusicapp.fragmentfunctions.FragmentFunctions;
import com.example.mymusicapp.models.MusicModel;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity{

    public static final FragmentFunctions fragmentFunctions = new FragmentFunctions();

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 121) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getAudioFiles();

                fragmentFunctions.createFragments(getSupportFragmentManager());

                fragmentFunctions.openFragment(fragmentFunctions.homeFragment);

                checkPrefs();
            } else {
                finishAffinity();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DataBindingUtil.setContentView(this, R.layout.activity_main);

        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    121
            );
        }else{
            getAudioFiles();

            fragmentFunctions.createFragments(getSupportFragmentManager());

            fragmentFunctions.openFragment(fragmentFunctions.homeFragment);

            checkPrefs();
        }
    }

    private void checkPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyMusicApp", MODE_PRIVATE);
        NowPlaying.isShuffle = sharedPreferences.getBoolean("SHUFFLE", false);
        NowPlaying.isRepeat = sharedPreferences.getBoolean("REPEAT", false);
    }

    public static ArrayList<MusicModel> musicClass = new ArrayList<>();

    public void getAudioFiles(){
        final MediaMetadataRetriever mmr = new MediaMetadataRetriever();

        String type = MediaStore.Files.FileColumns.MIME_TYPE + "=?";
        String mp3 = MimeTypeMap.getSingleton().getMimeTypeFromExtension("mp3");
        String[] arguments = new String[]{mp3};

        Cursor mCursor = getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.DATA }, type, arguments, null);

        while (mCursor.moveToNext()) {
            String path = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
            mmr.setDataSource(path);

            byte[] byteArray = mmr.getEmbeddedPicture();
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            Drawable img = new BitmapDrawable(bmp);

            musicClass.add(new MusicModel(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE),
                    mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST),
                    mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM),
                    mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION),
                    path, img));
        }
        mCursor.close();
        Collections.reverse(musicClass);
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