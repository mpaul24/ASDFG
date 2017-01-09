package com.manojit.paul.MuBox;


import com.google.gson.Gson;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity implements MyService.ServiceCallbacks, Song.getItemIndex
        , Album.AlbumlistClick, Album_list.AlbumListGivesIndex, Artist.ArtistClickFragmentChange,
        Artist_List_Fragment.ArtistListAlbumFragmentChange, ActivityCompat.OnRequestPermissionsResultCallback,
        Playlist_Fragment.playListChangeSong, NavigationView.OnNavigationItemSelectedListener,
        CustomPlaylist.customPlaylistInterface, MainFragment.mainFragment, CustomModel.OnCustomStateListener {


    int REQUEST_EQ = 6276;
    Intent intent;
    static ArrayList<SongDB> songs;
    static ArrayList<SongDB> songList;
    static ArrayList<SongDB> favouriteList;
    static ArrayList<SongDB> songDBs;
    static MyService mService;
    boolean mBound;
    ImageView controller_play;
    static SeekBar seekBar;
    static TextView currentText;
    static TextView durationText, drawerTextSong, drawerTextAlbum;
    static int songIndex;
    long id;
    boolean addtoQueueDone = false;

    boolean paused = false;
    Handler mHandler = new Handler();
    Utilities utils;
    ImageView repeatButton, shuffleButton;

    static int prog;
    static String progCurrent, progDuration;
    LinearLayout touchContainer;
    boolean NosongRunning = false;
    static boolean notSongFromLastSession = true;
    long oldsong_duration, oldsong_current;
    ProgressBar progressBar;
    static boolean activityPaused = false;
    long totalDuration, currentDuration;
    int data;
    static ArrayList<SongDB> ArtistList;
    static ArrayList<SongDB> AlbumList;
    static SlidingUpPanelLayout slidingUpPanelLayout;
    RelativeLayout cardView;
    final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 1234;
    ProgressBar initialProgress;
    boolean permissionGranted = false;
    ImageView playlistButton, drawerImage;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    View header;
    Fragment fragment;
    CircleImageView circleImageView;
    boolean songPlayedFromALbumOrArtist = false;
    static boolean listModified = false;


    SeekBar seekBar_demo;
    TextView currentText_demo, durationText_demo, demo_title, demo_artist;
    ImageView playButton, nextButton, prevButton;
    ImageView imageView, demo_image;
    boolean playlistListinAlive = false;
    static boolean repeat = false, repeatOne = false;
    static boolean shuffle = false;
    boolean songNotPlayedFromPlaylist = true;
    boolean songChangedFromNextPrevCompClickShuffleOn = false;
    ImageView likeButton;
    ImageView play_album_art;
    TextView play_song_name, play_artist;
    static Activity activity;
    LinearLayout upperTouch;
    boolean songDismissed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(R.style.splashScreenTheme);
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if (preferences.contains("theme")) {
            String s = preferences.getString("theme", "light");
            if (s.equals("dark"))
                setTheme(R.style.AppTheme_Dark);
            else if (s.equals("red"))
                setTheme(R.style.AppTheme_Red);
            else if (s.equals("purple"))
                setTheme(R.style.AppTheme_Purple);
            else if (s.equals("green"))
                setTheme(R.style.AppTheme_Green);
        }

        setContentView(R.layout.navigational_drawer_layout);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);


        slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        controller_play = (ImageView) findViewById(R.id.controller_play);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        currentText = (TextView) findViewById(R.id.cuurentText);
        durationText = (TextView) findViewById(R.id.durationText);


        progressBar = (ProgressBar) findViewById(R.id.seekBar1);
        mHandler = new Handler();
        utils = new Utilities();
        seekBar.setMax(100000);
        progressBar.setMax(100000);
        mHandler = new Handler();
        cardView = (RelativeLayout) findViewById(R.id.card);
        initialProgress = (ProgressBar) findViewById(R.id.initialProgress);
        playlistButton = (ImageView) findViewById(R.id.playListButton);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        circleImageView = (CircleImageView) findViewById(R.id.profile_image);
        favouriteList = new ArrayList<>();
        songs = new ArrayList<>();

        upperTouch = (LinearLayout) findViewById(R.id.upperTouch);
        seekBar_demo = (SeekBar) findViewById(R.id.seekBar_demo);
        seekBar_demo.setMax(100000);
        currentText_demo = (TextView) findViewById(R.id.cuurentText_demo);
        durationText_demo = (TextView) findViewById(R.id.durationText_demo);
        playButton = (ImageView) findViewById(R.id.playButton);
        nextButton = (ImageView) findViewById(R.id.nextButton);
        prevButton = (ImageView) findViewById(R.id.prevButton);
        imageView = (ImageView) findViewById(R.id.imageView);
        demo_image = (ImageView) findViewById(R.id.demo_image);
        demo_title = (TextView) findViewById(R.id.demo_title);
        demo_artist = (TextView) findViewById(R.id.demo_artist);
        repeatButton = (ImageView) findViewById(R.id.repeatButton);
        shuffleButton = (ImageView) findViewById(R.id.shuffleButton);
        touchContainer = (LinearLayout) findViewById(R.id.touchContainer);

        play_song_name = (TextView) findViewById(R.id.play_song_name);
        play_artist = (TextView) findViewById(R.id.play_artist);
        play_album_art = (ImageView) findViewById(R.id.play_album_art);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        header = navigationView.inflateHeaderView(R.layout.nav_header_navigational__drawer);
        drawerImage = (ImageView) header.findViewById(R.id.drawerImage);
        drawerTextSong = (TextView) header.findViewById(R.id.drawerTextSong);
        drawerTextAlbum = (TextView) header.findViewById(R.id.drawerTextAlbum);
        likeButton = (ImageView) findViewById(R.id.likeButton);
        cardView.bringToFront();


        activity = getParent();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        playlistButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (playlistListinAlive == false) {
                            playlistButton.setImageResource(R.drawable.playlist_icon_focussed);
                            slidingUpPanelLayout.setDragView(R.id.container);
                            playlistListinAlive = true;
                            Playlist_Fragment fragment = new Playlist_Fragment();
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            transaction.add(R.id.playlistViewer, fragment, "playlist");
                            transaction.commit();
                        } else {
                            playlistButton.setImageResource(R.drawable.playlist_icon);
                            slidingUpPanelLayout.setDragView(R.id.container);
                            playlistListinAlive = false;
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            Playlist_Fragment fragment = (Playlist_Fragment) getSupportFragmentManager().findFragmentByTag("playlist");
                            if (fragment != null) {
                                transaction.remove(fragment);
                                transaction.commit();
                            }
                        }
                    }
                }
        );

        repeatButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mService.mediaPlayer != null) {
                            if (repeat) {
                                repeatButton.setImageResource(R.drawable.repeat_no);
                                repeat = false;
                                mService.checkRepeatInService(false, false);
                            } else if (repeatOne) {
                                repeatButton.setImageResource(R.drawable.repeat);
                                repeatOne = false;
                                repeat = true;
                                mService.checkRepeatInService(true, false);
                            } else {
                                repeatButton.setImageResource(R.drawable.repeat_one);
                                repeatOne = true;
                                mService.checkRepeatInService(false, true);
                            }
                        }
                    }
                }
        );

        shuffleButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mService.mediaPlayer != null) {
                            if (shuffle) {
                                shuffleButton.setImageResource(R.drawable.shuffle);
                                long id = songs.get(songIndex).getId();
                                shuffle = false;
                                if (data != 4) {
                                    Collections.sort(songs, new Comparator<SongDB>() {
                                        @Override
                                        public int compare(SongDB lhs, SongDB rhs) {
                                            return lhs.getTitle().compareToIgnoreCase(rhs.getTitle());
                                        }
                                    });
                                } else {
                                    songs.clear();
                                    for (int i = 0; i < favouriteList.size(); i++) {
                                        songs.add(favouriteList.get(i));
                                    }
                                }
                                for (int i = 0; i < songs.size(); i++) {
                                    if (id == songs.get(i).getId()) {
                                        songIndex = i;
                                        break;
                                    }
                                }
                                mService.setSongLists(songs, songIndex);

                            } else {
                                shuffleButton.setImageResource(R.drawable.shuffle_on);
                                //DrawableCompat.setTint(shuffleButton.getDrawable(), ContextCompat.getColor(MainActivity.this, R.color.colorPrimary));
                                shuffle = true;
                                long id = songs.get(songIndex).getId();
                                ArrayList<SongDB> temp = new ArrayList<SongDB>();
                                Collections.shuffle(songs);

                                for (int i = 0; i < songs.size(); i++) {
                                    if (id == songs.get(i).getId()) {
                                        Log.e("Manojit", "hi" + i);
                                        songIndex = i;
                                        break;
                                    }
                                }
                                mService.setSongLists(songs, songIndex);
                            }
                        }
                    }
                }
        );

        likeButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (songs.size() > 0) {
                            SongDB db = songs.get(songIndex);
                            if (db.getLiked() == 0) {//not favourite
                                db.setLiked(1);
                                likeButton.setImageResource(R.drawable.like_pressed);
                                favouriteList.add(db);
                                if (data == 1 || data == 2 || data == 3 || data == 5) {
                                    //songs.add(db);
                                    Fragment fragment = getSupportFragmentManager().findFragmentByTag("customplaylist");
                                    if (fragment != null) {
                                        FragmentTransaction ADDtransaction = getSupportFragmentManager().beginTransaction();
                                        FragmentTransaction REMOVEtransaction = getSupportFragmentManager().beginTransaction();
                                        REMOVEtransaction.remove(fragment);
                                        REMOVEtransaction.commit();
                                        ADDtransaction.add(R.id.mainFragmentContainer, new CustomPlaylist(), "customplaylist");
                                        ADDtransaction.addToBackStack(null);
                                        ADDtransaction.commit();
                                    }

                                }
                                mService.setFavouriteList(favouriteList);


                            } else if (db.getLiked() == 1) {
                                db.setLiked(0);
                                likeButton.setImageResource(R.drawable.like);
                                favouriteList.remove(db);
                                if (data == 4) {
                                    songs.remove(db);
                                    songIndex--;
                                    Fragment fragment = getSupportFragmentManager().findFragmentByTag("customplaylist");
                                    if (fragment != null) {
                                        FragmentTransaction ADDtransaction = getSupportFragmentManager().beginTransaction();
                                        FragmentTransaction REMOVEtransaction = getSupportFragmentManager().beginTransaction();
                                        REMOVEtransaction.remove(fragment);
                                        REMOVEtransaction.commit();
                                        ADDtransaction.add(R.id.mainFragmentContainer, new CustomPlaylist(), "customplaylist");
                                        ADDtransaction.addToBackStack(null);
                                        ADDtransaction.commit();
                                    }
                                    if (songs.size() > 0) {
                                        if (shuffle) {
                                            songChangedFromNextPrevCompClickShuffleOn = true;
                                        }
                                        mHandler.removeCallbacks(mUpdateTimeTask);
                                        nextPlayer();
                                    } else {
                                        mHandler.removeCallbacks(mUpdateTimeTask);
                                        songs.clear();
                                        for (int i = 0; i < songList.size(); i++) {
                                            songs.add(songList.get(i));
                                        }
                                        songIndex = 0;
                                        notOldSongSongFragment();
                                        getIndex(0, 1);
                                        Toast.makeText(MainActivity.this, "Favourite List Empty!\nMain Songlist Playing.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Fragment fragment = getSupportFragmentManager().findFragmentByTag("customplaylist");
                                    if (fragment != null) {
                                        FragmentTransaction ADDtransaction = getSupportFragmentManager().beginTransaction();
                                        FragmentTransaction REMOVEtransaction = getSupportFragmentManager().beginTransaction();
                                        REMOVEtransaction.remove(fragment);
                                        REMOVEtransaction.commit();
                                        ADDtransaction.add(R.id.mainFragmentContainer, new CustomPlaylist(), "customplaylist");
                                        ADDtransaction.addToBackStack(null);
                                        ADDtransaction.commit();
                                    }
                                }
                                mService.setFavouriteList(favouriteList);
                            }
                        }
                    }
                }
        );


        playButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (mService.mediaPlayer == null) {
                            Toast.makeText(MainActivity.this, "Select a song", Toast.LENGTH_SHORT).show();
                        } else if (mService.mediaPlayer != null) {
                            if (mService.isplay()) {
                                pausePlayer();

                            } else if (mService.isplay() == false) {
                                if (!notSongFromLastSession) {
                                    controller_play.setImageResource(R.drawable.pause_icon1);
                                    playButton.setImageResource(R.drawable.pause_icon);
                                    mService.start();
                                    mService.showNotification();
                                    notSongFromLastSession = true;
                                    updateProgressBar();
                                    paused = false;
                                } else {
                                    startPlayer();
                                }

                            }
                        }
                    }
                }
        );

        nextButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (MainActivity.mService.mediaPlayer == null) {
                            Toast.makeText(MainActivity.this, "Select a song", Toast.LENGTH_SHORT).show();
                        } else if (MainActivity.mService.mediaPlayer != null) {
                            if (shuffle) {
                                songChangedFromNextPrevCompClickShuffleOn = true;
                            }
                            mHandler.removeCallbacks(mUpdateTimeTask);
                            nextPlayer();

                        }
                    }
                }
        );

        prevButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (MainActivity.mService.mediaPlayer == null) {
                            Toast.makeText(MainActivity.this, "Select a song", Toast.LENGTH_SHORT).show();
                        } else if (MainActivity.mService.mediaPlayer != null) {
                            if (shuffle) {
                                songChangedFromNextPrevCompClickShuffleOn = true;
                            }
                            mHandler.removeCallbacks(mUpdateTimeTask);
                            prevPlayer();

                        }
                    }
                }
        );

        seekBar_demo.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    /**
                     *
                     * */
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

                    }

                    /**
                     * When user starts moving the progress handler
                     * */
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // remove message Handler from updating progress bar
                        if (mService.mediaPlayer != null) {
                            mHandler.removeCallbacks(mUpdateTimeTask);
                        }
                    }

                    /**
                     * When user stops moving the progress hanlder
                     * */
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        if (mService.mediaPlayer != null) {
                            mHandler.removeCallbacks(mUpdateTimeTask);
                            int totalDuration = mService.mediaPlayer.getDuration();
                            int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

                            // forward or backward to certain seconds
                            mService.mediaPlayer.seekTo(currentPosition);

                            // update timer progress again
                            updateProgressBar();
                        }
                    }
                }
        );


        slidingUpPanelLayout.addPanelSlideListener(
                new SlidingUpPanelLayout.PanelSlideListener() {
                    @Override
                    public void onPanelSlide(View panel, float slideOffset) {

                    }

                    @Override
                    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                        if (newState.equals(SlidingUpPanelLayout.PanelState.DRAGGING)) {

                        } else if (newState.equals(SlidingUpPanelLayout.PanelState.EXPANDED)) {

                            cardView.setVisibility(View.INVISIBLE);

                        } else if (newState.equals(SlidingUpPanelLayout.PanelState.COLLAPSED)) {

                            cardView.setVisibility(View.VISIBLE);
                            slidingUpPanelLayout.setDragView(R.id.container);
                            playlistButton.setImageResource(R.drawable.playlist_icon);
                            playlistListinAlive = false;
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            Playlist_Fragment fragment = (Playlist_Fragment) getSupportFragmentManager().findFragmentByTag("playlist");
                            if (fragment != null) {
                                transaction.remove(fragment);
                                transaction.commit();

                            }
                        }
                    }
                }
        );


        controller_play.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mService.mediaPlayer == null) {
                            Toast.makeText(MainActivity.this, "select a song", Toast.LENGTH_SHORT).show();
                        } else {
                            if (mService.isplay()) {
                                pausePlayer();

                            } else if (mService.isplay() == false) {
                                if (!notSongFromLastSession) {
                                    controller_play.setImageResource(R.drawable.pause_icon1);
                                    playButton.setImageResource(R.drawable.pause_icon);
                                    mService.start();
                                    mService.showNotification();
                                    notSongFromLastSession = true;
                                    updateProgressBar();
                                    paused = false;
                                } else {
                                    startPlayer();
                                }

                            }
                        }
                    }
                }
        );


        touchContainer.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (slidingUpPanelLayout.getPanelState().equals(SlidingUpPanelLayout.PanelState.COLLAPSED)) {
                            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                        } else if (slidingUpPanelLayout.getPanelState().equals(SlidingUpPanelLayout.PanelState.EXPANDED)) {
                            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                        }
                    }
                }
        );

        upperTouch.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (slidingUpPanelLayout.getPanelState().equals(SlidingUpPanelLayout.PanelState.EXPANDED)) {
                            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                        } else if (slidingUpPanelLayout.getPanelState().equals(SlidingUpPanelLayout.PanelState.COLLAPSED)) {
                            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                        }
                    }
                }
        );


        seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    /**
                     *
                     * */
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

                    }

                    /**
                     * When user starts moving the progress handler
                     * */
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // remove message Handler from updating progress bar
                        if (mService.mediaPlayer != null) {
                            mHandler.removeCallbacks(mUpdateTimeTask);
                        }
                    }

                    /**
                     * When user stops moving the progress hanlder
                     * */
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        if (mService.mediaPlayer != null) {
                            mHandler.removeCallbacks(mUpdateTimeTask);
                            int totalDuration = mService.mediaPlayer.getDuration();
                            int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

                            // forward or backward to certain seconds
                            mService.mediaPlayer.seekTo(currentPosition);

                            // update timer progress again
                            updateProgressBar();
                        }
                    }
                }
        );

        checkPermission();

    }


    public void afterPermissionGranted() {
        if (mService.mediaPlayer == null) {
            NosongRunning = true;
        }
        if (Build.VERSION.SDK_INT > 23) {
            mService = new MyService(getApplicationContext());
        } else {
            mService = new MyService();
        }

        new checkSong().execute();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (permissionGranted) {
            if (intent == null)
                intent = new Intent(this, MyService.class);
            intent.setAction(MyService.ACTION.STARTFOREGROUND_ACTION);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            startService(intent);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        activityPaused = false;
    }


    @Override
    protected void onPause() {
        super.onPause();
        activityPaused = true;
    }


    @Override
    protected void onDestroy() {

        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        if(!saveSONG) {
            mHandler.removeCallbacks(mUpdateTimeTask);
            SharedPreferences sp = getSharedPreferences("songs", MODE_PRIVATE);
            SharedPreferences.Editor e = sp.edit();
            e.putLong("duration", totalDuration);
            e.putLong("current", currentDuration);
            e.putInt("data", data);
            e.putBoolean("listmodified", listModified);
            if (mService.mediaPlayer != null)
                e.putLong("lastSongID", songs.get(songIndex).getId());
            if (repeat) {
                e.putBoolean("repeat", true);
                e.putBoolean("repeatOne", false);

            } else if (repeatOne) {
                e.putBoolean("repeatOne", true);
                e.putBoolean("repeat", false);

            } else {
                e.putBoolean("repeatOne", false);
                e.putBoolean("repeat", false);
            }

            if (shuffle) {
                e.putBoolean("shuffle", true);
            } else {
                e.putBoolean("shuffle", false);
            }

            Gson gson = new Gson();
            String song = new String();
            song = gson.toJson(songs);
            e.putString("songList", song);
            e.commit();


            SharedPreferences preferences1 = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            SharedPreferences.Editor edit1 = preferences1.edit();
            Gson gson1 = new Gson();
            String song1 = new String();
            song1 = gson1.toJson(songs);
            edit1.putString("PlayListSong", song1);
            String favouriteSongs = gson1.toJson(favouriteList);
            edit1.putString("favouriteSongs", favouriteSongs);
            edit1.commit();
        }

        super.onDestroy();
    }
    boolean saveSONG = false;

    void saveSong() {
        saveSONG = true;
        mHandler.removeCallbacks(mUpdateTimeTask);
        SharedPreferences sp = getSharedPreferences("songs", MODE_PRIVATE);
        SharedPreferences.Editor e = sp.edit();
        e.putLong("duration", totalDuration);
        e.putLong("current", currentDuration);
        e.putInt("data", data);
        if (mService.mediaPlayer != null)
            e.putLong("lastSongID", songs.get(songIndex).getId());
        if (repeat) {
            e.putBoolean("repeat", true);
            e.putBoolean("repeatOne", false);

        } else if (repeatOne) {
            e.putBoolean("repeatOne", true);
            e.putBoolean("repeat", false);

        } else {
            e.putBoolean("repeatOne", false);
            e.putBoolean("repeat", false);
        }

        if (shuffle) {
            e.putBoolean("shuffle", true);
        } else {
            e.putBoolean("shuffle", false);
        }

        Gson gson = new Gson();
        String song = new String();
        song = gson.toJson(songs);
        e.putString("songList", song);
        e.commit();


        SharedPreferences preferences1 = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor edit1 = preferences1.edit();
        Gson gson1 = new Gson();
        String song1 = new String();
        song1 = gson1.toJson(songs);
        edit1.putString("PlayListSong", song1);
        String favouriteSongs = gson1.toJson(favouriteList);
        edit1.putString("favouriteSongs", favouriteSongs);
        edit1.commit();
    }

    //------------------------------------------------------------------------------------------------------------------------------
    private void playNext() {Log.e("ManojitPaul","playNext");
        if (!notSongFromLastSession) {
            notSongFromLastSession = true;
        }
        int total = songs.size();
        if (songIndex == total - 1) {
            songIndex = 0;
            mService.play(songIndex, MyService.list);
        } else {
            songIndex = songIndex + 1;
            mService.play(songIndex, MyService.list);
        }
    }


    private void playPrevious() {Log.e("ManojitPaul","playPrevious");
        if (!notSongFromLastSession) {
            notSongFromLastSession = true;
        }
        int total = songs.size();
        if (songIndex == 0) {
            songIndex = total - 1;
            mService.play(songIndex, MyService.list);
        } else {
            songIndex = songIndex - 1;
            mService.play(songIndex, MyService.list);
        }
    }
//------------------------------------------------------------------------------------------------------------------------------


    private void retrieveoldSong() {Log.e("ManojitPaul","retrieveoldSong");
        SharedPreferences sharedPreferences = getSharedPreferences("lastsong", MODE_PRIVATE);
        if (sharedPreferences.contains("lstSng")) {
            int lst = 0;
            long id = sharedPreferences.getLong("lastSongID", 0);
            boolean f = false;
            listModified = sharedPreferences.getBoolean("listmodified", false);
            for (int i = 0; i < songs.size(); i++) {
                if (songs.get(i).getId() == id) {
                    lst = i;
                    f = true;
                    break;
                }
            }

            oldsong_duration = sharedPreferences.getLong("songDuration", 0);
            oldsong_current = sharedPreferences.getLong("songCurrent", 0);
            if (songs.size() == 0 && lst == 0) {
                for (int i = 0; i < songList.size(); i++) {
                    songs.add(songList.get(i));
                }
                lst = 0;
                oldsong_current = 0;
                oldsong_duration = 0;
            }
            checkRepeatAfterTaskRemoved();
            if (mService != null) {
                songIndex = lst;
                notSongFromLastSession = false;
                getIndex(lst, data);
            }
        }
    }

    public void checkRepeatAfterTaskRemoved() {Log.e("ManojitPaul","checkRepeatAfterTaskRemoved");
        SharedPreferences sharedPreferences = getSharedPreferences("lastsong", MODE_PRIVATE);
        if (sharedPreferences.contains("lstSng")) {
            {
                if (sharedPreferences.getBoolean("repeat", false)) {
                    repeat = sharedPreferences.getBoolean("repeat", false);
                    repeatButton.setImageResource(R.drawable.repeat);
                } else if (sharedPreferences.getBoolean("repeatOne", false)) {
                    repeatOne = sharedPreferences.getBoolean("repeatOne", false);
                    repeatButton.setImageResource(R.drawable.repeat_one);
                }

                if (sharedPreferences.getBoolean("shuffle", false)) {
                    shuffle = sharedPreferences.getBoolean("shuffle", false);
                    shuffleButton.setImageResource(R.drawable.shuffle_on);

                } else {
                    shuffle = sharedPreferences.getBoolean("shuffle", false);
                    shuffleButton.setImageResource(R.drawable.shuffle);
                }
            }
        }
    }


    ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MyService.LocalBinder binder = (MyService.LocalBinder) service;
            mService = binder.getServices();
            mService.setCallbacks(MainActivity.this);
            if (NosongRunning) {
                retrieveoldSong();
            }
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };


    @Override
    public void getIndex(int data, int choice) {Log.e("ManojitPaul","getIndex");

        if (songNotPlayedFromPlaylist) {Log.e("ManojitPaul","getIndex | songNotPlayedFromPlaylist");
            if (!notSongFromLastSession) {
                Log.e("ManojitPaul","getIndex | songNotPlayedFromPlaylist | !notSongFromLastSession");
            }
            else {
                if (choice != 4) {Log.e("ManojitPaul","getIndex | songNotPlayedFromPlaylist | choice!=4");

                    //when shuffle is on and song is played from song fragment so song list should be sorted first

                    /*Collections.sort(songs, new Comparator<SongDB>() {
                        @Override
                        public int compare(SongDB lhs, SongDB rhs) {
                            return lhs.getTitle().compareToIgnoreCase(rhs.getTitle());
                        }
                    });*/

                }
            }
        }

        this.data = choice;
        if (choice == 1) {Log.e("ManojitPaul","getIndex | choice=1");
            if (!notSongFromLastSession) {Log.e("ManojitPaul","getIndex | choice=1 | !notSongFromLastSession");
                //if song is from last session
                /*if (songPlayedFromALbumOrArtist) {Log.e("ManojitPaul","getIndex | choice=1 | songPlayedFromALbumOrArtist");
                    //if last session was played from album or artist
                    ArrayList<SongDB> temp = new ArrayList<>();
                    for (int i = 0; i < songList.size(); i++) {
                        temp.add(songList.get(i));
                    }
                    songs = temp;
                    songPlayedFromALbumOrArtist = false;
                }*/
            } /*else if (songPlayedFromALbumOrArtist) {Log.e("ManojitPaul","getIndex | choice=1 | songPlayedFromALbumOrArtist");
                //if song is not from last session
                //and prev song was played from album or artist
                ArrayList<SongDB> temp = new ArrayList<>();

                for (int i = 0; i < songList.size(); i++) {
                    temp.add(songList.get(i));
                }
                songs = temp;
                songPlayedFromALbumOrArtist = false;
            } *//*else if (addtoQueueDone) {Log.e("ManojitPaul","getIndex | choice=1 | addtoQueueDone");
                //if song is modified by adding a song to queue
                ArrayList<SongDB> temp = new ArrayList<>();
                for (int i = 0; i < songList.size(); i++) {
                    temp.add(songList.get(i));
                }
                songs = temp;
                addtoQueueDone = false;
            } */else {
                if (songs.size() == 0) {Log.e("ManojitPaul","getIndex | choice=1 | size=0");
                    ArrayList<SongDB> temp = new ArrayList<>();
                    for (int i = 0; i < songList.size(); i++) {
                        temp.add(songList.get(i));
                    }
                    songs = temp;
                } else if (listModified) {Log.e("ManojitPaul","getIndex | choice=1 | listmodified");
                    //if songs is modified by drag and swipe
                    /*if(songDismissed == false) {//if song is dismissed so song is not required to be updated
                        ArrayList<SongDB> temp = new ArrayList<>();
                        for (int i = 0; i < songList.size(); i++) {
                            temp.add(songList.get(i));
                        }
                        songs = temp;
                    }*/
                    songDismissed = true;
                }
            }
            songIndex = data;
            layoutUpdater();
        } else if (choice == 2) {Log.e("ManojitPaul","getIndex | choice=2");
            songIndex = data;
            layoutUpdater();
        } else if (choice == 3) {Log.e("ManojitPaul","getIndex | choice=3");
            songIndex = data;
            layoutUpdater();
        } else if (choice == 4) {Log.e("ManojitPaul","getIndex | choice=4");
            songIndex = data;
            layoutUpdater();
        } else if (choice == 5) {Log.e("ManojitPaul","getIndex | choice=5");
            songIndex = data;
            layoutUpdater();
        }
        mHandler.removeCallbacks(mUpdateTimeTask);
        mService.play(data, choice);

    }

    @Override
    public void notOldSongSongFragment() {
        Log.e("ManojitPaul","notOldSongSongFragment");
        notSongFromLastSession = true;
    }

    @Override
    public void SongGiveIndex(int data, int position) {
        //song played from song Frgament
        ArrayList<SongDB> temp = new ArrayList<>();
        for (int i = 0; i < songList.size(); i++) {
            temp.add(songList.get(i));
        }
        songs = temp;

        getIndex(data, position);
    }

    @Override
    public void addtoQueue(int position) {
        //triggered from song Fragment through popUp
        if (songs != null) {
            addtoQueueDone = true;
            if (songs.size() == 0) {
                Log.e("ManojitPaul","notOldSongSongFragment | size=0");
                songs.add(songList.get(position));
                songIndex = 0;
                layoutUpdater();
                data = 1;
                listModified(true);//first time when song is played by adding to queue using Song Fragment
                mService.play(0, 1);
            } else {
                Log.e("ManojitPaul","notOldSongSongFragment | size!=0");
                songs.add((songIndex + 1), songList.get(position));
            }
        }
    }

    @Override
    public void clickOnPlaylist(int position, int choice) {Log.e("ManojitPaul","clickOnPlaylist");
        //triggered from favourite fragment

        songs.clear();
        for (int i = 0; i < favouriteList.size(); i++) {
            songs.add(favouriteList.get(i));
        }
        songPlayedFromALbumOrArtist = true;
        getIndex(position, 4);
    }

    @Override
    public void notOldSong() {Log.e("ManojitPaul","notOldSong");
        //triggered from Playlist_Fragment

        notSongFromLastSession = true;

        if (shuffle) {
            songNotPlayedFromPlaylist = false;
            songChangedFromNextPrevCompClickShuffleOn = true;
        }
    }

    @Override
    public void artistClickChangeFragment(int position) {
        Artist_List_Fragment artist_list_fragment = new Artist_List_Fragment(position);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.mainFragmentContainer, artist_list_fragment, "artistListFragment");
        fragmentTransaction.addToBackStack("add artistList");
        fragmentTransaction.commit();

    }

    @Override
    public void artistAlbumFragmentChange(int position) {
        changeFragment(position);
    }

    @Override
    public void artistListSongsList(int position) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String list = preferences.getString("ArtistSongList", null);
        Gson gson = new Gson();
        SongDB[] savedList = gson.fromJson(list, SongDB[].class);
        List<SongDB> before = Arrays.asList(savedList);
        ArrayList<SongDB> after = new ArrayList<>(before);
        songs.clear();
        for (int i = 0; i < after.size(); i++) {
            for (int j = 0; j < songDBs.size(); j++) {
                if (after.get(i).getId() == songDBs.get(j).getId()) {
                    songs.add(songDBs.get(j));
                }
            }
        }
        songIndex = position;
        songPlayedFromALbumOrArtist = true;
        getIndex(position, 3);
    }



/*-------------------------------------------ALBUM FRAGMENT-----------------------------------------------------------------
* Album Fragment Interface Communicator
* changeFragment(int data)
 */

    @Override
    public void changeFragment(int data) {
        Album_list album_list = new Album_list(data);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.mainFragmentContainer, album_list, "albumList");
        fragmentTransaction.addToBackStack("add albumList");
        fragmentTransaction.commit();
    }

    //-----------------------------------------------END-----------------------------------------------------------------------



/*-----------------------------------------AlBUM_LIST FRAGMENT--------------------------------------------------------------
* Album_list Fragment Interface Communicator
* AlbumListGivesIndex(int data, int position)
 */

    @Override
    public void AlbumListGivesIndex(int data, int position) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String list = preferences.getString("Albumlist", null);
        Gson gson = new Gson();
        SongDB[] savedList = gson.fromJson(list, SongDB[].class);
        List<SongDB> before = Arrays.asList(savedList);
        ArrayList<SongDB> after = new ArrayList<>(before);
        songs.clear();
        for (int i = 0; i < after.size(); i++) {
            for (int j = 0; j < songDBs.size(); j++) {
                if (after.get(i).getId() == songDBs.get(j).getId()) {
                    songs.add(songDBs.get(j));
                }
            }
        }
        //songs = after;
        songIndex = position;
        songPlayedFromALbumOrArtist = true;
        getIndex(position, 2);
    }

    @Override
    public void changeSongFromPlaylist(int position) {
        //triggered from Playlist_Fragment
        Log.e("ManojitPaul","changeSongFromPlaylist");
        boolean flag = false;
        if (addtoQueueDone) {
            flag = true;
        }
        addtoQueueDone = false;
        getIndex(position, data);

        if (flag) {
            addtoQueueDone = true;
        }
    }

    @Override
    public void listModified(boolean b) {Log.e("ManojitPaul","listModified");
        listModified = b;
    }

    @Override
    public void resetPlayer() {
        //when last song is dismissed from playlist
        mHandler.removeCallbacks(mUpdateTimeTask);
        demo_title.setText("");
        demo_artist.setText("");
        drawerTextSong.setText("");
        drawerTextAlbum.setText("");

        play_song_name.setText("");
        play_artist.setText("");


        demo_image.setImageResource(R.drawable.lo);
        circleImageView.setImageResource(R.drawable.lo);
        play_album_art.setImageResource(R.drawable.lo);
        drawerImage.setImageResource(R.drawable.drawer_pic);

        circleImageView.setBorderColor(Color.TRANSPARENT);

        imageView.setImageResource(R.color.lightGrayBg);
        likeButton.setImageResource(R.drawable.like);
        currentText_demo.setText("0:00");
        durationText_demo.setText("0:00");
        mService.clearNotification();
        seekBar.setProgress(0);
        seekBar_demo.setProgress(0);
        mService.mediaPlayer.reset();
        mService.mediaPlayer = null;

    }

    @Override
    public void itemDismissed() {
        //if a song is dismissed
        songDismissed = true;
    }

    @Override
    public void changePlaylistColor() {
        //change the color of playing song in Playlist Frgament
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("playlist");
        if (fragment != null) {
            InterfacePlaylist.getInstance().changeState(songIndex);
        }
    }

    @Override
    public void aa() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            drawerLayout.openDrawer(GravityCompat.START);
        }
        // Toast.makeText(MainActivity.this, "Hi", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void notifyItem() {
        long modelState = CustomModel.getInstance().getState();
        boolean flag = false;
        for (int i = 0; i < songList.size(); i++) {
            if (songList.get(i).getId() == modelState) {
                songs.clear();
                songs.add(songList.get(i));
                flag = true;
                break;
            }
        }
        if (flag) {
            notOldSongSongFragment();
            songPlayedFromALbumOrArtist = true;
            getIndex(0, 5);
        }
    }


    //----------------------------------------------------END-----------------------------------------------------------------------

    /*--------------------------------------------------------IMPORTANT STUFSS---------------------------------------------------------------

    * ViewPager Adapter
    * SeekbarUpdater Handler
    * Zoom and Depth Animation for pageViewer
     */
    class MyCustomTabAdapter extends FragmentPagerAdapter {

        public MyCustomTabAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            if (position == 0) {
                fragment = new Song();
            }
            if (position == 1) {
                fragment = new Album();
            }
            if (position == 2) {
                fragment = new Artist();
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = new String();
            if (position == 0) {
                title = "SONG";
            }
            if (position == 1) {
                title = "Album";
            }
            if (position == 2) {
                title = "Artist";
            }
            return title;
        }
    }

    /**
     * Update timer on seekbar
     */
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);

    }

    /**
     * Background Runnable thread
     */
    private Runnable mUpdateTimeTask = new Runnable() {
        @Override
        public void run() {
            totalDuration = mService.mediaPlayer.getDuration();
            currentDuration = mService.mediaPlayer.getCurrentPosition();

            // Displaying Total Duration time
            durationText.setText("" + utils.milliSecondsToTimer(totalDuration));
            progDuration = "" + utils.milliSecondsToTimer(totalDuration);
            durationText_demo.setText(progDuration);
            // Displaying time completed playing
            currentText.setText("" + utils.milliSecondsToTimer(currentDuration));
            progCurrent = "" + utils.milliSecondsToTimer(currentDuration);
            currentText_demo.setText(progCurrent);

            // Updating progress bar
            int progress = (int) (utils.getProgressPercentage(currentDuration, totalDuration));
            prog = progress;
            seekBar.setProgress(progress);
            progressBar.setProgress(progress);
            seekBar_demo.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }

    };

    public class ZoomOutPageTransformer implements ViewPager.PageTransformer {

        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }

    public class DepthPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.75f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                view.setAlpha(1);
                view.setTranslationX(0);
                view.setScaleX(1);
                view.setScaleY(1);

            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                view.setAlpha(1 - position);

                // Counteract the default slide transition
                view.setTranslationX(pageWidth * -position);

                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }

//-----------------------------------------------END-------------------------------------------------------------------------------

/*-----------------------------------------------------MYSERVICE-------------------------------------------------------------------
Interface methods of Myservice
* preparedPlayer();
* onCompleteSong(int data);
* void startPlayer();
* void pausePlayer();
* void nextPlayer();
* void prevPlayer();
 */

    @Override
    public void preparedPlayer() {Log.e("ManojitPaul","preparedPlayer");
        updateProgressBar();
        if (notSongFromLastSession) {Log.e("ManojitPaul","preparedPlayer | notSongFromLastSession");
            updateProgressBar();
            controller_play.setImageResource(R.drawable.pause_icon1);
            playButton.setImageResource(R.drawable.pause_icon);
            mService.mediaPlayer.start();
            mService.showNotification();

            if (shuffle && songNotPlayedFromPlaylist) {Log.e("ManojitPaul","preparedPlayer | shuffle | songNotPlayedFromPlaylist");
                //shuffle is on and song is not played from playlist i.e playlist has to be shuffled
                if (!songChangedFromNextPrevCompClickShuffleOn) {Log.e("ManojitPaul","preparedPlayer | !songChangedFromNextPrevCompClickShuffleOn");
                    //proves that song is not changed from prev or next button
                    long id = songs.get(songIndex).getId();
                    Collections.shuffle(songs);
                    for (int i = 0; i < songs.size(); i++) {
                        if (songs.get(i).getId() == id) {
                            songIndex = i;
                            break;
                        }
                    }
                    mService.setSongLists(songs, songIndex);
                }
            }
            if (!songNotPlayedFromPlaylist) {Log.e("ManojitPaul","preparedPlayer | !songNotPlayedFromPlaylist");
                //song is played from playlist so no shuffle is required
                songNotPlayedFromPlaylist = true;
            }
            if (songChangedFromNextPrevCompClickShuffleOn) {Log.e("ManojitPaul","preparedPlayer | songChangedFromNextPrevCompClickShuffleOn");
                //song is changed from NextPrevCompClick ,so no shuffle is required
                songChangedFromNextPrevCompClickShuffleOn = false;
            }

        } else if (!notSongFromLastSession) {Log.e("ManojitPaul","preparedPlayer | !notSongFromLastSession");
            controller_play.setImageResource(R.drawable.play_icon1);
            playButton.setImageResource(R.drawable.play_icon);
            mService.mediaPlayer.seekTo((int) oldsong_current);
            int progress = (int) (utils.getProgressPercentage(oldsong_current, oldsong_duration));
            prog = progress;
            seekBar.setProgress(progress);
            progressBar.setProgress(progress);
            seekBar_demo.setProgress(progress);
            progDuration = "" + utils.milliSecondsToTimer(oldsong_duration);
            durationText_demo.setText(progDuration);
            progCurrent = "" + utils.milliSecondsToTimer(oldsong_current);
            currentText_demo.setText(progCurrent);
            mHandler.removeCallbacks(mUpdateTimeTask);
        }
    }

    @Override
    public void onCompleteSong(int data) {
        songIndex = data;
        int total = songs.size();
        if (repeat) {
            if (shuffle) {
                songChangedFromNextPrevCompClickShuffleOn = true;
            }
            //Log.e("Manojit","repeat");
            if (songIndex == total - 1) {
                playButton.setImageResource(R.drawable.pause_icon);
                songIndex = 0;

                layoutUpdater();
                mService.play(songIndex, MyService.list);
            } else {
                playButton.setImageResource(R.drawable.pause_icon);
                songIndex++;

                layoutUpdater();
                mService.play(songIndex, MyService.list);

            }

            Fragment fragment = getSupportFragmentManager().findFragmentByTag("playlist");
            if (fragment != null) {
                InterfacePlaylist.getInstance().changeState(songIndex);
            }

        } else if (repeatOne) {
            if (shuffle) {
                songChangedFromNextPrevCompClickShuffleOn = true;
            }
            // Log.e("Manojit","repeatone "+songIndex);
            playButton.setImageResource(R.drawable.pause_icon);

            layoutUpdater();
            mService.play(songIndex, MyService.list);

        } else {
            if (shuffle) {
                songChangedFromNextPrevCompClickShuffleOn = true;
            }
            Log.e("Manojit", "else");
            if (songIndex == total - 1) {
                //songIndex = 0;

                playButton.setImageResource(R.drawable.play_icon);
                controller_play.setImageResource(R.drawable.play_icon1);
                layoutUpdater();
                mService.showSpecialNotification();
                //mService.play(songIndex, MyService.list);
            } else {
                songIndex++;

                playButton.setImageResource(R.drawable.pause_icon);
                layoutUpdater();
                mService.play(songIndex, MyService.list);

            }

            Fragment fragment = getSupportFragmentManager().findFragmentByTag("playlist");
            if (fragment != null) {
                InterfacePlaylist.getInstance().changeState(songIndex);
            }

        }
    }

    @Override
    public void startPlayer() {
        updateProgressBar();
        controller_play.setImageResource(R.drawable.pause_icon1);
        playButton.setImageResource(R.drawable.pause_icon);
        paused = false;
        mService.mediaPlayer.start();
        mService.showNotification();
    }

    @Override
    public void pausePlayer() {
        mHandler.removeCallbacks(mUpdateTimeTask);
        controller_play.setImageResource(R.drawable.play_icon1);
        playButton.setImageResource(R.drawable.play_icon);
        paused = true;
        mService.mediaPlayer.pause();
        mService.showNotification();
    }

    @Override
    public void nextPlayer() {
        mHandler.removeCallbacks(mUpdateTimeTask);
        playNext();
        playButton.setImageResource(R.drawable.pause_icon);
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("playlist");
        if (fragment != null) {
            InterfacePlaylist.getInstance().changeState(songIndex);
        }
        layoutUpdater();
        updateProgressBar();
    }

    @Override
    public void prevPlayer() {
        mHandler.removeCallbacks(mUpdateTimeTask);
        playPrevious();
        playButton.setImageResource(R.drawable.pause_icon);
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("playlist");
        if (fragment != null) {
            InterfacePlaylist.getInstance().changeState(songIndex);
        }
        layoutUpdater();
        updateProgressBar();

    }

    @Override
    public void changeFlag() {Log.e("ManojitPaul","changeFlag");

    }

    //-------------------------------------------------------------------------------------------------------------------------


    @Override
    public void onBackPressed() {
        if (slidingUpPanelLayout != null && slidingUpPanelLayout.getPanelState().equals(SlidingUpPanelLayout.PanelState.EXPANDED)) {
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    afterPermissionGranted();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Toast.makeText(MainActivity.this, "Permission not Granted", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    public class checkSong extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            SharedPreferences sp = getSharedPreferences("songs", MODE_PRIVATE);
            if (sp.contains("songList")) {
                //Log.e("Manojit","hello how are you");

                totalDuration = sp.getLong("duration", 0);
                currentDuration = sp.getLong("current", 0);
                data = sp.getInt("data", 1);
                listModified = sp.getBoolean("listmodified", false);
                id = sp.getLong("lastSongID", 0);

                //Log.e("Manojit",""+data);

                SongProvider1 provider1 = new SongProvider1(MainActivity.this);

                songDBs = provider1.getSongs();
                ArrayList<AlbumDB> albumDBs = provider1.getAlbum();
                ArrayList<ArtistDB> artistDBs = provider1.getArtistList();
                SharedPreferences ps = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                if (ps.contains("favouriteSongs")) {
                    String listFavourite = ps.getString("favouriteSongs", null);
                    Gson gsonFavourite = new Gson();
                    SongDB[] savedListFavourite = gsonFavourite.fromJson(listFavourite, SongDB[].class);
                    List<SongDB> beforeFavourite = Arrays.asList(savedListFavourite);
                    ArrayList<SongDB> afterFavourite = new ArrayList<>(beforeFavourite);

                    if (afterFavourite.size() >= 0) {
                        for (int i = 0; i < afterFavourite.size(); i++) {
                            for (int j = 0; j < songDBs.size(); j++) {
                                if (afterFavourite.get(i).getId() == songDBs.get(j).getId()) {
                                    songDBs.get(j).setLiked(1);
                                    favouriteList.add(songDBs.get(j));
                                    break;
                                }
                            }
                        }
                    }
                    mService.setFavouriteList(favouriteList);

                }


                for (int i = 0; i < songDBs.size(); i++) {
                    for (int j = 0; j < albumDBs.size(); j++) {
                        if (songDBs.get(i).getAlbumID() == albumDBs.get(j).getAlbumID()) {
                            songDBs.get(i).setAlbumDB(albumDBs.get(j));
                            break;
                        }
                    }
                }
                for (int i = 0; i < songDBs.size(); i++) {
                    for (int j = 0; j < artistDBs.size(); j++) {
                        if (songDBs.get(i).getArtistID() == artistDBs.get(j).getArtist_ID()) {

                            songDBs.get(i).setArtistDB(artistDBs.get(j));
                            break;
                        }
                    }
                }

                for(int i=0;i< songDBs.size();i++){
                    if(songDBs.get(i).getAlbumDB() == null || songDBs.get(i).getArtistDB() == null){
                        songDBs.remove(i);
                    }
                }

                ArrayList<SongDB> albumList = provider1.getAlbumList(songDBs);
                ArrayList<SongDB> artistList = provider1.getArtistList(songDBs);
                songList = new ArrayList<>();
                for (int i = 0; i < songDBs.size(); i++) {
                    songList.add(songDBs.get(i));
                }
                ArtistList = artistList;
                AlbumList = albumList;

                if (data == 1) {

                    try {

                        SharedPreferences preferences1 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        String list = preferences1.getString("PlayListSong", null);
                        Gson gson = new Gson();
                        SongDB[] savedList = gson.fromJson(list, SongDB[].class);
                        List<SongDB> before = Arrays.asList(savedList);
                        ArrayList<SongDB> after = new ArrayList<>(before);
                        for (int i = 0; i < after.size(); i++) {
                            for (int j = 0; j < songDBs.size(); j++) {
                                if (after.get(i).getId() == songDBs.get(j).getId()) {
                                    songs.add(songDBs.get(j));
                                }
                            }
                        }
                    } catch (Exception e) {
                    }
                } else if (data == 2) {
                    SharedPreferences preferences1 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    String list = preferences1.getString("PlayListSong", null);
                    Gson gson = new Gson();
                    SongDB[] savedList = gson.fromJson(list, SongDB[].class);
                    List<SongDB> before = Arrays.asList(savedList);
                    ArrayList<SongDB> after = new ArrayList<>(before);
                    for (int i = 0; i < after.size(); i++) {
                        for (int j = 0; j < songDBs.size(); j++) {
                            if (after.get(i).getId() == songDBs.get(j).getId()) {
                                songs.add(songDBs.get(j));
                            }
                        }
                    }
                    songPlayedFromALbumOrArtist = true;
                } else if (data == 3) {
                    SharedPreferences preferences1 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    String list = preferences1.getString("PlayListSong", null);
                    Gson gson = new Gson();
                    SongDB[] savedList = gson.fromJson(list, SongDB[].class);
                    List<SongDB> before = Arrays.asList(savedList);
                    ArrayList<SongDB> after = new ArrayList<>(before);
                    for (int i = 0; i < after.size(); i++) {
                        for (int j = 0; j < songDBs.size(); j++) {
                            if (after.get(i).getId() == songDBs.get(j).getId()) {
                                songs.add(songDBs.get(j));
                            }
                        }
                    }
                    songPlayedFromALbumOrArtist = true;
                } else if (data == 4) {
                    SharedPreferences preferences1 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    String list = preferences1.getString("PlayListSong", null);
                    Gson gson = new Gson();
                    SongDB[] savedList = gson.fromJson(list, SongDB[].class);
                    List<SongDB> before = Arrays.asList(savedList);
                    ArrayList<SongDB> after = new ArrayList<>(before);
                    for (int i = 0; i < after.size(); i++) {
                        for (int j = 0; j < songDBs.size(); j++) {
                            if (after.get(i).getId() == songDBs.get(j).getId()) {
                                songs.add(songDBs.get(j));
                            }
                        }
                    }
                    songPlayedFromALbumOrArtist = true;
                } else if (data == 5) {
                    SharedPreferences preferences1 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    String list = preferences1.getString("PlayListSong", null);
                    Gson gson = new Gson();
                    SongDB[] savedList = gson.fromJson(list, SongDB[].class);
                    List<SongDB> before = Arrays.asList(savedList);
                    ArrayList<SongDB> after = new ArrayList<>(before);
                    for (int i = 0; i < after.size(); i++) {
                        for (int j = 0; j < songDBs.size(); j++) {
                            if (after.get(i).getId() == songDBs.get(j).getId()) {
                                songs.add(songDBs.get(j));
                            }
                        }
                    }
                    songPlayedFromALbumOrArtist = true;
                }

            } else {
                SongProvider1 provider1 = new SongProvider1(MainActivity.this);

                songDBs = provider1.getSongs();
                ArrayList<AlbumDB> albumDBs = provider1.getAlbum();
                ArrayList<ArtistDB> artistDBs = provider1.getArtistList();


                for (int i = 0; i < songDBs.size(); i++) {
                    for (int j = 0; j < albumDBs.size(); j++) {
                        if (songDBs.get(i).getAlbumID() == albumDBs.get(j).getAlbumID()) {
                            songDBs.get(i).setAlbumDB(albumDBs.get(j));
                            break;
                        }
                    }
                }
                for (int i = 0; i < songDBs.size(); i++) {
                    for (int j = 0; j < artistDBs.size(); j++) {
                        if (songDBs.get(i).getArtistID() == artistDBs.get(j).getArtist_ID()) {

                            songDBs.get(i).setArtistDB(artistDBs.get(j));
                            break;
                        }
                    }
                }

                ArrayList<SongDB> albumList = provider1.getAlbumList(songDBs);
                ArrayList<SongDB> artistList = provider1.getArtistList(songDBs);

                songList = new ArrayList<>();
                for (int i = 0; i < songDBs.size(); i++) {
                    songList.add(songDBs.get(i));

                }
                ArtistList = artistList;
                AlbumList = albumList;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            postExecute();
        }
    }


    public void postExecute() {
        initialProgress.setVisibility(View.GONE);
        if (mService.mediaPlayer != null) {
            if (mService.mediaPlayer.isPlaying()) {
                controller_play.setImageResource(R.drawable.pause_icon1);
                playButton.setImageResource(R.drawable.pause_icon);


                int index = mService.index;
                id = songs.get(index).getId();


                for (int j = 0; j < songs.size(); j++) {
                    if (songs.get(j).getId() == id) {
                        songIndex = j;
                        break;
                    }
                }

                if (songs.size() == 0) {
                    for (int i = 0; i < songList.size(); i++) {
                        songs.add(songList.get(i));
                    }
                    songIndex = 0;
                    totalDuration = 0;
                    currentDuration = 0;
                }

                layoutUpdater();
                checkRepeat();
                updateProgressBar();

            } else if (!mService.mediaPlayer.isPlaying()) {
                controller_play.setImageResource(R.drawable.play_icon1);
                playButton.setImageResource(R.drawable.play_icon);

                songIndex = 0;

                for (int j = 0; j < songs.size(); j++) {
                    if (songs.get(j).getId() == id) {
                        songIndex = j;
                        break;
                    }
                }

                if (songs.size() == 0) {
                    for (int i = 0; i < songList.size(); i++) {
                        songs.add(songList.get(i));
                    }
                    songIndex = 0;
                    totalDuration = 0;
                    currentDuration = 0;
                }

                int progress = (int) (utils.getProgressPercentage(currentDuration, totalDuration));
                prog = progress;
                durationText_demo.setText("" + utils.milliSecondsToTimer(totalDuration));
                currentText_demo.setText("" + utils.milliSecondsToTimer(currentDuration));
                progressBar.setProgress(prog);
                seekBar_demo.setProgress(prog);
                checkRepeat();
                layoutUpdater();

            }
        }

        MainFragment mainFragment = new MainFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.mainFragmentContainer, mainFragment, "naya")
                .commit();


        if (!permissionGranted) {
            if (intent == null) {
                intent = new Intent(MainActivity.this, MyService.class);
            }
            intent.setAction(MyService.ACTION.STARTFOREGROUND_ACTION);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            startService(intent);
            permissionGranted = true;
        }
        if (NosongRunning) {

        }
    }

    public void checkRepeat() {
        SharedPreferences sp = getSharedPreferences("songs", MODE_PRIVATE);
        //Log.e("Manojit",""+sp.getBoolean("repeat",false));
        if (sp.contains("songList")) {
            //Log.e("Manojit",""+sp.getBoolean("repeat",false));
            if (sp.getBoolean("repeat", false)) {

                repeat = sp.getBoolean("repeat", false);
                repeatButton.setImageResource(R.drawable.repeat);
            } else if (sp.getBoolean("repeatOne", false)) {

                repeatOne = sp.getBoolean("repeatOne", false);
                repeatButton.setImageResource(R.drawable.repeat_one);
            }

            if (sp.getBoolean("shuffle", false)) {
                shuffle = sp.getBoolean("shuffle", false);
                shuffleButton.setImageResource(R.drawable.shuffle_on);
            } else {
                shuffle = sp.getBoolean("shuffle", false);
                shuffleButton.setImageResource(R.drawable.shuffle);
            }
        }
    }

    private boolean BitMapFailed = false;
    private void layoutUpdater() {
        SongDB songList = songs.get(songIndex);

        if (songList.getLiked() == 0) {
            likeButton.setImageResource(R.drawable.like);
        } else if (songList.getLiked() == 1) {
            likeButton.setImageResource(R.drawable.like_pressed);
        }

        demo_title.setText(songList.getTitle());
        demo_artist.setText(songList.getArtist());
        drawerTextSong.setText(songList.getTitle());
        drawerTextAlbum.setText(songList.getAlbum());

        play_song_name.setText(songList.getTitle());
        play_artist.setText(songList.getArtist());

        try {
            Picasso.with(this)
                    .load("file://" + songList.getAlbumDB().getAlbumArt())
                    .error(R.drawable.lo)
                    .into(demo_image);

            Picasso.with(this)
                    .load("file://" + songList.getAlbumDB().getAlbumArt())
                    .error(R.drawable.lo)
                    .into(circleImageView);

            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            byte[] art = null;
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.lo);
            if (songList.getPath() != null) {
                try {
                    mediaMetadataRetriever.setDataSource(songList.getPath());
                    art = mediaMetadataRetriever.getEmbeddedPicture();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (art != null) {
                bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
            }

            /*BlurBuilder blurBuilder = new BlurBuilder();
            Bitmap blurImage = blurBuilder.blur(MainActivity.this, bitmap);*/
            //Drawable verticalImage = new BitmapDrawable(getResources(), blurImage);
            //imageView.setImageBitmap(blurImage);
            imageView.setImageResource(R.color.lightGrayBg);

            /*Palette palette = Palette.from(bitmap).maximumColorCount(32).generate();
            int color = palette.getVibrantColor(Color.GRAY);
            if (color == Color.GRAY) {
                color = palette.getLightVibrantColor(Color.GRAY);
                if (color == Color.GRAY) {
                    color = palette.getDominantColor(Color.GRAY);
                }
            }*/
            circleImageView.setBorderColor(Color.GRAY);
            Log.e("ManojitPaul123","Hello");


            /*if(BitMapFailed) {
                imageView.setBackgroundColor(Color.GRAY);
                circleImageView.setBorderColor(Color.GRAY);
                BitMapFailed = false;
            }*/
            Picasso.with(this)
                    .load("file://" + songList.getAlbumDB().getAlbumArt())
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            //bigViews.setImageViewBitmap(R.id.status_albumart,bitmap);
                            //views.setImageViewBitmap(R.id.status_albumart,bitmap);
                            BlurBuilder blurBuilder = new BlurBuilder();
                            Bitmap blurImage = blurBuilder.blur(MainActivity.this, bitmap);
                            //Drawable verticalImage = new BitmapDrawable(getResources(), blurImage);
                            imageView.setImageBitmap(blurImage);
                            Palette palette = Palette.from(bitmap).maximumColorCount(32).generate();
                            int color = palette.getVibrantColor(Color.GRAY);
                            if (color == Color.GRAY) {
                                color = palette.getLightVibrantColor(Color.GRAY);
                                if (color == Color.GRAY) {
                                    color = palette.getDominantColor(Color.GRAY);
                                }
                            }
                            circleImageView.setBorderColor(color);
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {

                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });

            Picasso.with(this)
                    .load("file://" + songList.getAlbumDB().getAlbumArt())
                    .error(R.drawable.lo)
                    .into(play_album_art);

            Picasso.with(this)
                    .load("file://" + songList.getAlbumDB().getAlbumArt())
                    .error(R.drawable.lo)
                    .into(drawerImage);
        } catch (Exception e) {
            e.printStackTrace();
            //Log.e("Bittu","" + songList.getAlbumDB().getAlbumArt());
        }


        try {
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(songList.getPath());
            byte[] art = mediaMetadataRetriever.getEmbeddedPicture();

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_media_play);
            if (art != null) {
                bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
            }


        } catch (Exception e) {

        }
    }

    public class BlurBuilder {
        private static final float BITMAP_SCALE = 0.4f;
        private static final float BLUR_RADIUS = 3.5f;

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        public Bitmap blur(Context context, Bitmap image) {
            int width = Math.round(image.getWidth() * BITMAP_SCALE);
            int height = Math.round(image.getHeight() * BITMAP_SCALE);

            Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
            Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

            RenderScript rs = RenderScript.create(context);
            ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
            Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
            theIntrinsic.setRadius(BLUR_RADIUS);
            theIntrinsic.setInput(tmpIn);
            theIntrinsic.forEach(tmpOut);
            tmpOut.copyTo(outputBitmap);

            return outputBitmap;
        }
    }


    public void checkPermission() {

        if (Build.VERSION.SDK_INT >= 23) {


            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //Log.e("Manojit","permission granted>23");
                afterPermissionGranted();

            } else if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //Log.e("Manojit","permission not granted>23");
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(MainActivity.this, "Provide Permission to access the Storage", Toast.LENGTH_SHORT).show();
                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_STORAGE);

                } else {
                    //Toast.makeText(MainActivity.this, "False", Toast.LENGTH_SHORT).show();
                    // No explanation needed, we can request the permission.
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        //Toast.makeText(MainActivity.this, "False", Toast.LENGTH_SHORT).show();
                        showMessageOKCancel("You need to allow access to Storage",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                MY_PERMISSIONS_REQUEST_READ_STORAGE);
                                    }
                                });
                        return;
                    }
                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }
        } else {
            //Log.e("Manojit","permission granted <23");
            afterPermissionGranted();
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", getPackageName(), null));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        dialog.dismiss();
                        finish();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent();
            intent.setAction("android.media.action.DISPLAY_AUDIO_EFFECT_CONTROL_PANEL");
            if ((intent.resolveActivity(getPackageManager()) != null)) {
                startActivityForResult(intent, REQUEST_EQ);
                // REQUEST_EQ is an int of your choosing
            } else {
                // No equalizer found :(
            }
        } else if (item.getItemId() == android.R.id.home) {
            //finish();
        } else if (item.getItemId() == R.id.menu_serch_n) {
            CustomModel.getInstance().setListener(this);

            long id = CustomModel.getInstance().getState();
            Intent intent = new Intent(MainActivity.this, SearchableActivity.class);
            startActivityForResult(intent, 1234);
        } else if (item.getItemId() == R.id.dark) {

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("theme", "dark");
            editor.commit();
            if (mService.mediaPlayer != null && mService.mediaPlayer.isPlaying()) {
                saveSong();
            }
            startActivity(new Intent(this, MainActivity.class));
            finish();

        } else if (item.getItemId() == R.id.light) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("theme", "light");
            editor.commit();
            if (mService.mediaPlayer != null && mService.mediaPlayer.isPlaying()) {
                saveSong();
            }
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else if (item.getItemId() == R.id.red) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("theme", "red");
            editor.commit();
            if (mService.mediaPlayer != null && mService.mediaPlayer.isPlaying()) {
                saveSong();
            }
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else if (item.getItemId() == R.id.purple) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("theme", "purple");
            editor.commit();
            if (mService.mediaPlayer != null && mService.mediaPlayer.isPlaying()) {
                saveSong();
            }
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else if (item.getItemId() == R.id.green) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("theme", "green");
            editor.commit();
            if (mService.mediaPlayer != null && mService.mediaPlayer.isPlaying()) {
                saveSong();
            }
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.favourite) {
            FragmentTransaction ADDtransaction = getSupportFragmentManager().beginTransaction();
            FragmentTransaction REMOVEtransaction = getSupportFragmentManager().beginTransaction();
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("customplaylist");
            if (fragment != null) {
                REMOVEtransaction.remove(fragment);
                REMOVEtransaction.commit();
                //Log.e("manojitM","adadas");
            }
            ADDtransaction.add(R.id.mainFragmentContainer, new CustomPlaylist(), "customplaylist");
            ADDtransaction.addToBackStack(null);
            ADDtransaction.commit();
            // Handle the camera action
        } else if (id == R.id.feedback) {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "manojitp4@gmail.com", null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "FeedBack");
            //emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
        } else if (id == R.id.about) {
            AboutUSDialog aboutUSDialog = new AboutUSDialog();
            aboutUSDialog.show(getFragmentManager(), "About US");
        } else if (id == R.id.opensource) {
            OpenSource openSource = new OpenSource();
            openSource.show(getFragmentManager(), "Open Source");
        }

        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}




