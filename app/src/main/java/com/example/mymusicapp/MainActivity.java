package com.example.mymusicapp;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity{

    public static ArrayList<MusicModel> musicClass;

    private final MediaMetadataRetriever mmr = new MediaMetadataRetriever();

    public static MediaPlayer mediaPlayer;

    public static final ArrayList<Integer> shuffledLastSong = new ArrayList<>();

    static boolean isFav = false;
    static boolean isShuffle = false;
    static boolean isRepeat = false;
    static boolean isPlaying = false;
    static int position = -1;

    public HomeFragment homeFragment = new HomeFragment();
    public PlayingNowFragment playingNowFragment = new PlayingNowFragment();

    public Fragment selected = homeFragment;

    public void createFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().add(R.id.fragmentsContent, fragment).hide(fragment).commit();
    }
    public void showFragment(Fragment fragment){
        if(fragment==playingNowFragment){
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_up_in, R.anim.slide_stay)
                    .show(fragment).commit();
        }else{
            getSupportFragmentManager().beginTransaction()
                    .show(fragment).commit();
        }
    }
    public void hideFragment(Fragment fragment){
        if(fragment==playingNowFragment){
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_stay, R.anim.slide_down_out)
                    .hide(fragment).commit();
        }else{
            getSupportFragmentManager().beginTransaction()
                    .hide(fragment).commit();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 121) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getAudioFiles();
                checkPrefs();
                createFragment(homeFragment);
                createFragment(playingNowFragment);
                firstFragment();
            } else {
                finishAffinity();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setting the top bar color
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.status_bar));

        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 121);
        }else{
            getAudioFiles();
            checkPrefs();
            createFragment(homeFragment);
            createFragment(playingNowFragment);
            firstFragment();
        }
    }

    public void firstFragment() {
        hideFragment(selected);
        selected = homeFragment;
        showFragment(selected);
    }

    private void checkPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences("MUSIC_APP_INFO", MODE_PRIVATE);
        int last_pos = sharedPreferences.getInt("LAST_PLAYED_POS", -1);
        if(last_pos!=-1){
            position = last_pos;

            Uri uri = Uri.parse(MainActivity.musicClass.get(position).getPath());
            mediaPlayer = MediaPlayer.create(MainActivity.this, uri);
            int last_seek_pos = sharedPreferences.getInt("LAST_PLAYED_SEEK", 0);
            mediaPlayer.seekTo(last_seek_pos);
        }
        isShuffle = sharedPreferences.getBoolean("SHUFFLE", false);
        isRepeat = sharedPreferences.getBoolean("REPEAT", false);
    }

    //get all audio files
    public void getAudioFiles(){
        musicClass = new ArrayList<>();
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
            musicClass.add(new MusicModel(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE),
                    mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST), path, mmr.getEmbeddedPicture()));
        }
        mCursor.close();
        Collections.reverse(musicClass);
    }

    @Override
    public void onBackPressed() {
        if(selected==homeFragment){
            moveTaskToBack(true);
        }else{
            hideFragment(selected);
            selected = homeFragment;
            showFragment(selected);
        }
    }
}