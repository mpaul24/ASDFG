package com.manojit.paul.MuBox;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.RemoteControlClient;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;


import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

public class MyService extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener,MediaPlayer.OnErrorListener,AudioManager.OnAudioFocusChangeListener {

    private final IBinder mBinder = new LocalBinder();
    static MediaPlayer mediaPlayer;
    private static ServiceCallbacks serviceCallbacks;
    static int index;
    static boolean isPrepared=false;
    static ArrayList<SongDB> songLists = MainActivity.songs;
    static ArrayList<SongDB> newSongLists;
    static ArrayList<SongDB> favourite = MainActivity.favouriteList;
    static Context context;
    boolean ActivityClosed=false;
    static int list;
    AudioManager am;
    boolean duck = false;
    private ComponentName componentName;
    private RemoteControlClient remoteControlClient;
    private boolean AudioFocusLost = false;
    private boolean wasPlaying = false;
    boolean repeat , repeatOne;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent!=null) {
            if (intent.getAction().equals(ACTION.STARTFOREGROUND_ACTION)) {
                //showNotification();
            }
            else if (intent.getAction().equals(ACTION.PREV_ACTION)) {
                if(!AudioFocusLost){
                    if(!ActivityClosed || !MainActivity.activityPaused) {
                        serviceCallbacks.prevPlayer();
                    }

                    else{
                        int total = songLists.size();
                        if(index==0){
                            index=total - 1;
                            play(index,list);
                        }
                        else{
                            index = index - 1;
                            play(index,list);
                        }
                        //showNotification();
                    }
                }
                else {
                    am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

// Request audio focus for playback
                    int result = am.requestAudioFocus(this,
                            // Use the music stream.
                            AudioManager.STREAM_MUSIC,
                            // Request permanent focus.
                            AudioManager.AUDIOFOCUS_GAIN);

                    if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {

                        if(!ActivityClosed || !MainActivity.activityPaused) {
                            serviceCallbacks.prevPlayer();
                        }

                        else{
                            int total = songLists.size();
                            if(index==0){
                                index=total - 1;
                                play(index,list);
                            }
                            else{
                                index = index - 1;
                                play(index,list);
                            }
                            //showNotification();
                        }
                    }
                }


            }
            else if (intent.getAction().equals(ACTION.PLAY_ACTION)) {


                if(!AudioFocusLost){
                if (mediaPlayer.isPlaying()) {
                    if (!ActivityClosed || !MainActivity.activityPaused) {
                        serviceCallbacks.pausePlayer();
                    }

                    else {
                        mediaPlayer.pause();
                        showNotification();
                    }

                } else {
                    if (!ActivityClosed) {
                        serviceCallbacks.startPlayer();
                    } else {
                        mediaPlayer.start();
                        showNotification();
                    }
                }
                }
                else{
                    am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

// Request audio focus for playback
                    int result = am.requestAudioFocus(this,
                            // Use the music stream.
                            AudioManager.STREAM_MUSIC,
                            // Request permanent focus.
                            AudioManager.AUDIOFOCUS_GAIN );

                    if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                        if (mediaPlayer.isPlaying()) {
                            if (!ActivityClosed || !MainActivity.activityPaused) {
                                serviceCallbacks.pausePlayer();
                            }

                            else {
                                mediaPlayer.pause();
                                showNotification();
                            }

                        } else {
                            if (!ActivityClosed) {
                                serviceCallbacks.startPlayer();
                            } else {
                                mediaPlayer.start();
                                showNotification();
                            }
                        }
                    }
                }
            } else if (intent.getAction().equals(ACTION.NEXT_ACTION)) {
                if(!AudioFocusLost){
                    if (!ActivityClosed || !MainActivity.activityPaused) {
                        serviceCallbacks.nextPlayer();
                    } else {
                        int total = songLists.size();
                        if (index == total - 1) {
                            index = 0;
                            play(index, list);
                        } else {
                            index = index + 1;
                            play(index, list);
                        }
                        //showNotification();
                    }
                }
                else {
                    am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

// Request audio focus for playback
                    int result = am.requestAudioFocus(this,
                            // Use the music stream.
                            AudioManager.STREAM_MUSIC,
                            // Request permanent focus.
                            AudioManager.AUDIOFOCUS_GAIN);

                    if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {

                        if (!ActivityClosed || !MainActivity.activityPaused) {
                            serviceCallbacks.nextPlayer();
                        } else {
                            int total = songLists.size();
                            if (index == total - 1) {
                                index = 0;
                                play(index, list);
                            } else {
                                index = index + 1;
                                play(index, list);
                            }
                            //showNotification();
                        }
                    }
                }

            }else if(intent.getAction().equals(ACTION.DELETE_ACTION)){
                if(ActivityClosed) {
                    am.abandonAudioFocus(this);
                    SharedPreferences sharedPreferences = context.getSharedPreferences("lastsong",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("list",list);
                    editor.putInt("lstSng",index);
                    editor.putBoolean("listmodified",MainActivity.listModified);
                    if(mediaPlayer!=null) {
                        SongDB lastSongPlayed = null;
                        if(songLists!=null) {
                            lastSongPlayed = songLists.get(index);
                            editor.putLong("lastSongID", lastSongPlayed.getId());
                        }
                        editor.putLong("songDuration", mediaPlayer.getDuration());
                        editor.putLong("songCurrent", mediaPlayer.getCurrentPosition());



                        SharedPreferences preferences1 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor edit1 = preferences1.edit();
                        edit1.putInt("list", list);
                        Gson gson = new Gson();
                        String song = new String();
                        song = gson.toJson(songLists);
                        edit1.putString("PlayListSong", song);
                        edit1.commit();
                    }
                    editor.commit();
                    if(mediaPlayer!=null) {
                        mediaPlayer.reset();
                        mediaPlayer.release();
                    }
                    mediaPlayer = null;
                    stopForeground(true);
                    stopSelf();
                }
                else{

                    serviceCallbacks.pausePlayer();
                    stopForeground(true);
                }
            }

            else {

            }
        }

        return START_STICKY;
    }

    public MyService() {
        //context = getApplicationContext();
    }

    public MyService(Context context) {
        this.context = context;
    }

    public void changeSongList()
    {
        if(!MainActivity.shuffle)
        {
            songLists = MainActivity.songs;
        }
        else
        {
          //  songLists = MainActivity.tempShuffle;
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.context = getApplicationContext();
    }

    public void initMusicPlayer(){
        //set player properties
        try {
            mediaPlayer.setWakeMode(context,
                    PowerManager.PARTIAL_WAKE_LOCK);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnErrorListener(this);
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
    public void setSongLists(ArrayList<SongDB> songDB,int index){
        this.index = index;
        songLists = songDB;
    }

    public void checkRepeatInService(boolean repeat , boolean repeatOne){
        this.repeat = repeat;
        this.repeatOne = repeatOne;
    }

    public void play(int currentSongPosition,int list){

        if(MainActivity.notSongFromLastSession) {
            am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

// Request audio focus for playback
            int result = am.requestAudioFocus(this,
                    // Use the music stream.
                    AudioManager.STREAM_MUSIC,
                    // Request permanent focus.
                    AudioManager.AUDIOFOCUS_GAIN);

            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                //am.registerMediaButtonEventReceiver(RemoteControlReceiver);
                // Start playback.
                if (serviceCallbacks != null) {
                    serviceCallbacks.changeFlag();
                }
                this.list = list;

                if (!ActivityClosed || !MainActivity.activityPaused) {
                    songLists = MainActivity.songs;
                }

                index = currentSongPosition;
                isPrepared = false;
                if (mediaPlayer != null) {
                    mediaPlayer.reset();
                    songLists = MainActivity.songs;
                    SongDB currSong = songLists.get(currentSongPosition);
                    String curr = currSong.getPath();

                    try {
                        mediaPlayer.setDataSource(curr);
                        mediaPlayer.prepare();

                    } catch (Exception e) {
                    }
                }
                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                    initMusicPlayer();
                    String curr;
                    if (songLists != null) {
                        SongDB currSong = songLists.get(currentSongPosition);
                        curr = currSong.getPath();
                    } else {
                        SongProvider1 songsProvider1 = new SongProvider1(getApplicationContext());
                        newSongLists = songsProvider1.getSongs();
                        songLists = newSongLists;
                        SongDB currSong = songLists.get(currentSongPosition);
                        curr = currSong.getPath();
                    }
                    try {
                        mediaPlayer.setDataSource(curr);
                        mediaPlayer.prepare();

                    } catch (Exception e) {
                        Log.e("PAUL", e.getMessage());
                    }
                }
            }
        }
        else if(!MainActivity.notSongFromLastSession)
        {
            this.list = list;

            if (!ActivityClosed || !MainActivity.activityPaused) {
                songLists = MainActivity.songs;
            }

            index = currentSongPosition;
            isPrepared = false;
            if (mediaPlayer != null) {
                mediaPlayer.reset();
                SongDB currSong = songLists.get(currentSongPosition);
                String curr = currSong.getPath();

                try {
                    mediaPlayer.setDataSource(curr);
                    mediaPlayer.prepare();

                } catch (Exception e) {
                }
            }
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
                initMusicPlayer();
                String curr;
                if (songLists != null) {
                    SongDB currSong = songLists.get(currentSongPosition);
                    curr = currSong.getPath();
                } else {
                    SongProvider1 songsProvider1 = new SongProvider1(getApplicationContext());
                    newSongLists = songsProvider1.getSongs();
                    songLists = newSongLists;
                    SongDB currSong = songLists.get(currentSongPosition);
                    curr = currSong.getPath();
                }
                try {
                    mediaPlayer.setDataSource(curr);
                    mediaPlayer.prepare();

                } catch (Exception e) {
                    Log.e("PAUL", e.getMessage());
                }
            }
        }

    }



    public void pausePlayer(){

        if(!ActivityClosed || !MainActivity.activityPaused){
            if (serviceCallbacks != null) {
                serviceCallbacks.pausePlayer();
            }
        }
        else{
            mediaPlayer.pause();
        }

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (!ActivityClosed || !MainActivity.activityPaused) {
            serviceCallbacks.onCompleteSong(index);
        }
        else {
            if(repeat) {
                int total = songLists.size();
                if (index == total - 1) {
                    index = 0;
                    play(index, list);
                } else {
                    index++;
                    play(index, list);
                }
            }
            else if(repeatOne){
                play(index, list);
            }
            else{
                int total = songLists.size();
                if (index == total - 1) {
                    index = 0;
                    mp.pause();
                    showSpecialNotification();
                    //play(index, list);
                } else {
                    index++;
                    play(index, list);
                }
            }

        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if(!ActivityClosed || !MainActivity.activityPaused) {
            if (serviceCallbacks != null) {
                serviceCallbacks.preparedPlayer();
                isPrepared = true;
            }
        }
        else{
            mp.start();
            showNotification();
        }

    }

    public void start(){
        am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

// Request audio focus for playback
        int result = am.requestAudioFocus(this,
                // Use the music stream.
                AudioManager.STREAM_MUSIC,
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mediaPlayer.start();
        }
    }

    public void go(){
        if(!ActivityClosed || !MainActivity.activityPaused){
            if (serviceCallbacks != null) {
                serviceCallbacks.startPlayer();
            }
        }
        else{
            mediaPlayer.start();
        }

    }

    public void seekTo(int pos){
        mediaPlayer.seekTo(pos);
    }

    public int getDuration(){
        return mediaPlayer.getDuration();
    }

    public int getCurrent(){
        if(mediaPlayer!=null) {
            if(mediaPlayer.isPlaying()) {
                return mediaPlayer.getCurrentPosition();
            }
            else{
                return mediaPlayer.getCurrentPosition();
            }
        }
        else{
            return 0;
        }
    }
    public boolean isplay(){
        return mediaPlayer.isPlaying();
    }

    /**
     * Called on the listener to notify it the audio focus for this listener has been changed.
     * The focusChange value indicates whether the focus was gained,
     * whether the focus was lost, and whether that loss is transient, or whether the new focus
     * holder will hold it for an unknown amount of time.
     * When losing focus, listeners can use the focus change information to decide what
     * behavior to adopt when losing focus. A music player could for instance elect to lower
     * the volume of its music stream (duck) for transient focus losses, and pause otherwise.
     *
     * @param focusChange the type of focus change, one of {@link AudioManager#AUDIOFOCUS_GAIN},
     *                    {@link AudioManager#AUDIOFOCUS_LOSS}, {@link AudioManager#AUDIOFOCUS_LOSS_TRANSIENT}
     *                    and {@link AudioManager#AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK}.
     */
    @Override
    public void onAudioFocusChange(int focusChange) {
        if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {

            // Pause playback
            if (!ActivityClosed || !MainActivity.activityPaused) {
                if(mediaPlayer.isPlaying())
                {
                    wasPlaying = true;
                    serviceCallbacks.pausePlayer();
                }
            }

            else {
                if(mediaPlayer.isPlaying())
                {
                    wasPlaying = true;
                    mediaPlayer.pause();
                    showNotification();
                }
            }
        }
        else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
            if(mediaPlayer!=null) {
                if(mediaPlayer.isPlaying()) {
                    mediaPlayer.setVolume(0.2f, 0.2f);
                    duck = true;
                }
            }
        }
        else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
            // Resume playback
            if(!AudioFocusLost) {
                if (!ActivityClosed) {
                    if(wasPlaying) {
                        serviceCallbacks.startPlayer();
                        wasPlaying = false;
                    }
                } else {
                    if(wasPlaying) {
                        mediaPlayer.start();
                        wasPlaying = false;
                        showNotification();
                    }
                }
                if (duck == true) {
                    if (mediaPlayer != null) {
                        mediaPlayer.setVolume(1f, 1f);
                        duck = false;
                    }
                }
            }

        }
        else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
            //am.unregisterMediaButtonEventReceiver(RemoteControlReceiver);
            //am.abandonAudioFocus(this);
            //stopForeground(true);
            // Stop playback
            AudioFocusLost = true;
            if (!ActivityClosed || !MainActivity.activityPaused) {
                serviceCallbacks.pausePlayer();
            }

            else {
                mediaPlayer.pause();
                showNotification();
            }
        }
    }

    public class LocalBinder extends Binder{
        MyService getServices(){
            ActivityClosed = false;
            return MyService.this;
        }
    }

    public void setFavouriteList(ArrayList<SongDB> f){
        favourite = f;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        SongDB lastSongPlayed = null;
        if (songLists != null && songLists.size()>0) {
            lastSongPlayed = songLists.get(index);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor edit = preferences.edit();
            edit.putInt("list", list);
            edit.commit();
            if (Build.VERSION.SDK_INT >= 23) {
                if (!mediaPlayer.isPlaying()) {
                    Log.e("Manojit", "SDK>23     is not playing");

                    SharedPreferences preferences1 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor edit1 = preferences1.edit();
                    edit1.putInt("list", list);
                    Gson gson = new Gson();
                    String song = new String();
                    song = gson.toJson(songLists);
                    edit1.putString("PlayListSong", song);
                    String favouriteSongs = gson.toJson(favourite);
                    edit1.putString("favouriteSongs",favouriteSongs);
                    edit1.commit();

                    SharedPreferences sharedPreferences = context.getSharedPreferences("lastsong", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("list", list);
                    editor.putInt("lstSng", index);
                    editor.putBoolean("listmodified",MainActivity.listModified);

                    if(MainActivity.repeat)
                    {
                        editor.putBoolean("repeat",true);
                        editor.putBoolean("repeatOne",false);
                        //Log.e("Manojit","acadca---repeat");
                    }
                    else if (MainActivity.repeatOne)
                    {
                        editor.putBoolean("repeatOne",true);
                        editor.putBoolean("repeat",false);
                        //Log.e("Manojit","acadca---repeatone");
                    }
                    else
                    {
                        editor.putBoolean("repeatOne",false);
                        editor.putBoolean("repeat",false);
                    }

                    if(MainActivity.shuffle)
                    {
                        editor.putBoolean("shuffle",true);
                        Log.e("Manojit","acadca---shuffle-true");
                    }
                    else
                    {
                        editor.putBoolean("shuffle",false);
                        Log.e("Manojit","acadca---shuffle-false");
                    }

                    if (mediaPlayer != null) {
                        Log.e("Manojit", "SDK>23     is not playing" + "   " + mediaPlayer.getCurrentPosition() + "    " + mediaPlayer.getDuration());
                        editor.putLong("songDuration", mediaPlayer.getDuration());
                        editor.putLong("songCurrent", mediaPlayer.getCurrentPosition());
                    }
                    editor.commit();
                    if (am != null) {
                        am.abandonAudioFocus(this);
                    }
                    if (mediaPlayer != null) {
                        mediaPlayer.reset();
                        mediaPlayer.release();
                    }
                    mediaPlayer = null;
                    stopForeground(true);
                    stopSelf();

                    super.onTaskRemoved(rootIntent);
                } else {
                    Log.e("Manojit", "SDK>23     is playing");
                    SharedPreferences preferences1 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor edit1 = preferences1.edit();
                    edit1.putInt("list", list);
                    Gson gson = new Gson();
                    String song = new String();
                    song = gson.toJson(songLists);
                    edit1.putString("PlayListSong", song);
                    String favouriteSongs = gson.toJson(favourite);
                    edit1.putString("favouriteSongs",favouriteSongs);
                    edit1.commit();

                    SharedPreferences sharedPreferences = context.getSharedPreferences("lastsong", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("listmodified",MainActivity.listModified);

                    if(MainActivity.repeat)
                    {
                        editor.putBoolean("repeat",true);
                        editor.putBoolean("repeatOne",false);
                        Log.e("Manojit","acadca---repeat");
                    }
                    else if (MainActivity.repeatOne)
                    {
                        editor.putBoolean("repeatOne",true);
                        editor.putBoolean("repeat",false);
                        Log.e("Manojit","acadca---repeatone");
                    }
                    else
                    {
                        editor.putBoolean("repeatOne",false);
                        editor.putBoolean("repeat",false);
                    }

                    if(MainActivity.shuffle)
                    {
                        editor.putBoolean("shuffle",true);
                        Log.e("Manojit","acadca---shuffle-true");
                    }
                    else
                    {
                        editor.putBoolean("shuffle",false);
                        Log.e("Manojit","acadca---shuffle-false");
                    }

                }
            } else {
                Log.e("Manojit", "SDK<23");
                if (am != null) {
                    am.abandonAudioFocus(this);
                }

                SharedPreferences preferences1 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor edit1 = preferences1.edit();
                Gson gson = new Gson();
                String song = new String();
                song = gson.toJson(songLists);
                edit1.putString("PlayListSong", song);
                String favouriteSongs = gson.toJson(favourite);
                edit1.putString("favouriteSongs",favouriteSongs);
                edit1.commit();

                SharedPreferences sharedPreferences = context.getSharedPreferences("lastsong", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("list", list);
                editor.putBoolean("listmodified",MainActivity.listModified);
                editor.putInt("lstSng", index);
                editor.putLong("lastSongID", lastSongPlayed.getId());

                if(MainActivity.repeat)
                {
                    editor.putBoolean("repeat",true);
                    editor.putBoolean("repeatOne",false);
                    //Log.e("Manojit","acadca---repeat");
                }
                else if (MainActivity.repeatOne)
                {
                    editor.putBoolean("repeatOne",true);
                    editor.putBoolean("repeat",false);
                    //Log.e("Manojit","acadca---repeatone");
                }
                else
                {
                    editor.putBoolean("repeatOne",false);
                    editor.putBoolean("repeat",false);
                }

                if(MainActivity.shuffle)
                {
                    editor.putBoolean("shuffle",true);
                    //Log.e("Manojit","acadca---shuffle-true");
                }
                else
                {
                    editor.putBoolean("shuffle",false);
                    //Log.e("Manojit","acadca---shuffle-false");
                }

                if (mediaPlayer != null) {
                    editor.putLong("songDuration", mediaPlayer.getDuration());
                    editor.putLong("songCurrent", mediaPlayer.getCurrentPosition());
                }
                editor.commit();
                if (mediaPlayer != null) {
                    mediaPlayer.reset();
                    mediaPlayer.release();
                }
                mediaPlayer = null;
                stopForeground(true);
                stopSelf();
                super.onTaskRemoved(rootIntent);
            }

        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.

        //ActivityClosed=false;
        return mBinder;
    }

    public void setCallbacks(ServiceCallbacks callbacks) {

        serviceCallbacks = callbacks;
    }

    @Override
    public boolean onUnbind(Intent intent) {

        ActivityClosed=true;

        return true;
    }


    public interface ServiceCallbacks {
        void preparedPlayer();
        void onCompleteSong(int data);
        void startPlayer();
        void pausePlayer();
        void nextPlayer();
        void prevPlayer();

        void changeFlag();
    }

    public int getIndex(){
        return index;
    }

    public interface ACTION {
        public static String MAIN_ACTION = "com.example.paul.asdfg.action.main";
        public static String DELETE_ACTION = "com.example.paul.asdfg.action.delete";
        public static String PREV_ACTION = "com.example.paul.asdfg.action.prev";
        public static String PLAY_ACTION = "com.example.paul.asdfg.action.play";
        public static String NEXT_ACTION = "com.example.paul.asdfg.action.next";
        public static String STARTFOREGROUND_ACTION = "com.example.paul.asdfg.action.startforeground";
        public static String STOPFOREGROUND_ACTION = "com.example.paul.asdfg.action.stopforeground";

    }
    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }

    Notification status;
    private final String LOG_TAG = "NotificationService";

    public void showNotification() {
        SongDB curr = songLists.get(index);
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(curr.getPath());
        byte[] art = mediaMetadataRetriever.getEmbeddedPicture();

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.lo);
        if(art!=null){
            bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
        }

// Using RemoteViews to bind custom layouts into Notification
        final RemoteViews views = new RemoteViews(getPackageName(),
                R.layout.status_bar);
        final RemoteViews bigViews = new RemoteViews(getPackageName(),
                R.layout.status_bar_expanded);

// showing default album image
        //views.setViewVisibility(R.id.status_albumart, View.VISIBLE);
        //views.setViewVisibility(R.id.status_albumart, View.GONE);
        bigViews.setImageViewBitmap(R.id.status_albumart, bitmap);
        views.setImageViewBitmap(R.id.status_albumart, bitmap);

        Picasso.with(context)
                .load("file://" + songLists.get(index).getAlbumDB().getAlbumArt())
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        bigViews.setImageViewBitmap(R.id.status_albumart,bitmap);
                        views.setImageViewBitmap(R.id.status_albumart,bitmap);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                       BitmapFailed = true;
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
        /*if(BitmapFailed) {
            bigViews.setImageViewBitmap(R.id.status_albumart, bitmap);
            views.setImageViewBitmap(R.id.status_albumart, bitmap);
            BitmapFailed = false;
        }*/


        Intent notificationIntent = new Intent(this, MainActivity.class);

        notificationIntent.setAction(ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Intent previousIntent = new Intent(this, MyService.class);
        previousIntent.setAction(ACTION.PREV_ACTION);
        PendingIntent ppreviousIntent = PendingIntent.getService(this, 0,
                previousIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent playIntent = new Intent(this, MyService.class);
        playIntent.setAction(ACTION.PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0,
                playIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent nextIntent = new Intent(this, MyService.class);
        nextIntent.setAction(ACTION.NEXT_ACTION);
        PendingIntent pnextIntent = PendingIntent.getService(this, 0,
                nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent deleteIntent = new Intent(this,MyService.class);
        deleteIntent.setAction(ACTION.DELETE_ACTION);
        PendingIntent pdeleteIntent = PendingIntent.getService(this,0,
                deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        /*Intent previousIntent = new Intent(ACTION.PREV_ACTION);
        PendingIntent ppreviousIntent = PendingIntent.getBroadcast(getApplicationContext(),0,previousIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent nextIntent = new Intent(ACTION.NEXT_ACTION);
        PendingIntent pnextIntent = PendingIntent.getBroadcast(getApplicationContext(),0,nextIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent playIntent = new Intent(ACTION.PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getBroadcast(getApplicationContext(),0,playIntent,PendingIntent.FLAG_UPDATE_CURRENT);*/


        views.setOnClickPendingIntent(R.id.status_play, pplayIntent);
        bigViews.setOnClickPendingIntent(R.id.status_play, pplayIntent);

        views.setOnClickPendingIntent(R.id.status_next, pnextIntent);
        bigViews.setOnClickPendingIntent(R.id.status_next, pnextIntent);

        views.setOnClickPendingIntent(R.id.status_prev, ppreviousIntent);
        bigViews.setOnClickPendingIntent(R.id.status_prev, ppreviousIntent);

        bigViews.setOnClickPendingIntent(R.id.cancel_action, pdeleteIntent);


        if(mediaPlayer.isPlaying()){
            views.setImageViewResource(R.id.status_play,R.drawable.pause_icon_white);
            bigViews.setImageViewResource(R.id.status_play,R.drawable.pause_icon_white);
        }
        else{
            views.setImageViewResource(R.id.status_play,R.drawable.play_icon_white);
            bigViews.setImageViewResource(R.id.status_play,R.drawable.play_icon_white);
        }

        views.setTextViewText(R.id.status_title, curr.getTitle());
        bigViews.setTextViewText(R.id.status_title, curr.getTitle());

        views.setTextViewText(R.id.status_artist, curr.getArtist());
        bigViews.setTextViewText(R.id.status_artist, curr.getArtist());

        bigViews.setTextViewText(R.id.status_album, curr.getAlbum());

        status = new Notification.Builder(this)

                .build();
        status.contentView = views;
        status.bigContentView = bigViews;
        status.flags = Notification.FLAG_ONGOING_EVENT;
        status.icon = android.R.drawable.ic_media_play;
        status.contentIntent = pendingIntent;
        status.priority = Notification.PRIORITY_MAX;
        startForeground(NOTIFICATION_ID.FOREGROUND_SERVICE, status);
    }

    private void UpdateMetadata(){
        if (remoteControlClient == null)
            return;
        /*RemoteControlClient.MetadataEditor metadataEditor = remoteControlClient.editMetadata(true);
        metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_ALBUM, data.getAlbum());
        metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_ARTIST, data.getArtist());
        metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_TITLE, data.getTitle());

        metadataEditor.apply();*/
        am.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }

    private Boolean BitmapFailed = false;
    public void showSpecialNotification() {

        SongDB curr = songLists.get(index);
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(curr.getPath());
        byte[] art = mediaMetadataRetriever.getEmbeddedPicture();

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.lo);
        if(art!=null){
            bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
        }



// Using RemoteViews to bind custom layouts into Notification
        final RemoteViews views = new RemoteViews(getPackageName(),
                R.layout.status_bar);
        final RemoteViews bigViews = new RemoteViews(getPackageName(),
                R.layout.status_bar_expanded);

// showing default album image
        //views.setViewVisibility(R.id.status_albumart, View.VISIBLE);
        //views.setViewVisibility(R.id.status_albumart, View.GONE);
        bigViews.setImageViewBitmap(R.id.status_albumart, bitmap);
        views.setImageViewBitmap(R.id.status_albumart, bitmap);
        /*if(!ActivityClosed) {
            Picasso.with(context)
                    .load("file://" + songLists.get(index).getAlbumDB().getAlbumArt())
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {
                            BitmapFailed = true;
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });
        }
        if(ActivityClosed) {
            bigViews.setImageViewBitmap(R.id.status_albumart, bitmap);
            views.setImageViewBitmap(R.id.status_albumart, bitmap);
            BitmapFailed = false;
        }*/

        Intent notificationIntent = new Intent(this, MainActivity.class);

        notificationIntent.setAction(ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Intent previousIntent = new Intent(this, MyService.class);
        previousIntent.setAction(ACTION.PREV_ACTION);
        PendingIntent ppreviousIntent = PendingIntent.getService(this, 0,
                previousIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent playIntent = new Intent(this, MyService.class);
        playIntent.setAction(ACTION.PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0,
                playIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent nextIntent = new Intent(this, MyService.class);
        nextIntent.setAction(ACTION.NEXT_ACTION);
        PendingIntent pnextIntent = PendingIntent.getService(this, 0,
                nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent deleteIntent = new Intent(this,MyService.class);
        deleteIntent.setAction(ACTION.DELETE_ACTION);
        PendingIntent pdeleteIntent = PendingIntent.getService(this,0,
                deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        /*Intent previousIntent = new Intent(ACTION.PREV_ACTION);
        PendingIntent ppreviousIntent = PendingIntent.getBroadcast(getApplicationContext(),0,previousIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent nextIntent = new Intent(ACTION.NEXT_ACTION);
        PendingIntent pnextIntent = PendingIntent.getBroadcast(getApplicationContext(),0,nextIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent playIntent = new Intent(ACTION.PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getBroadcast(getApplicationContext(),0,playIntent,PendingIntent.FLAG_UPDATE_CURRENT);*/


        views.setOnClickPendingIntent(R.id.status_play, pplayIntent);
        bigViews.setOnClickPendingIntent(R.id.status_play, pplayIntent);

        views.setOnClickPendingIntent(R.id.status_next, pnextIntent);
        bigViews.setOnClickPendingIntent(R.id.status_next, pnextIntent);

        views.setOnClickPendingIntent(R.id.status_prev, ppreviousIntent);
        bigViews.setOnClickPendingIntent(R.id.status_prev, ppreviousIntent);

        bigViews.setOnClickPendingIntent(R.id.cancel_action, pdeleteIntent);




            views.setImageViewResource(R.id.status_play,R.drawable.play_icon_white);
            bigViews.setImageViewResource(R.id.status_play,R.drawable.play_icon_white);


        views.setTextViewText(R.id.status_title, curr.getTitle());
        bigViews.setTextViewText(R.id.status_title, curr.getTitle());

        views.setTextViewText(R.id.status_artist, curr.getArtist());
        bigViews.setTextViewText(R.id.status_artist, curr.getArtist());

        bigViews.setTextViewText(R.id.status_album, curr.getAlbum());

        status = new Notification.Builder(this)

                .build();
        status.contentView = views;
        status.bigContentView = bigViews;
        status.flags = Notification.FLAG_ONGOING_EVENT;
        status.icon = android.R.drawable.ic_media_play;
        status.contentIntent = pendingIntent;
        status.priority = Notification.PRIORITY_MAX;
        startForeground(NOTIFICATION_ID.FOREGROUND_SERVICE, status);
    }

    public void clearNotification() {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        stopForeground(true);
        notificationManager.cancelAll();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
        SharedPreferences sharedPreferences1 = context.getSharedPreferences("lastsong", MODE_PRIVATE);
        SharedPreferences.Editor editor1 = sharedPreferences1.edit();
        editor1.clear();
        editor1.commit();
        SharedPreferences sp = getSharedPreferences("songs", MODE_PRIVATE);
        SharedPreferences.Editor editor2 = sp.edit();
        editor2.clear();
        editor2.commit();
    }


}
