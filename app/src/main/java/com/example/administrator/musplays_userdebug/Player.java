package com.example.administrator.musplays_userdebug;

import static com.example.administrator.musplays_userdebug.MainActivity.mysongs;
import static com.example.administrator.musplays_userdebug.MainActivity.selectedAudio;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Picture;
import android.graphics.drawable.Icon;
import android.media.session.PlaybackState;
import android.os.Build;
import android.os.Bundle;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;

import androidx.activity.EdgeToEdge;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
//import androidx.media.app.NotificationCompat;

import android.os.Handler;
import android.media.session.MediaSession.Token;
//import android.media.session.MediaSession;

import androidx.core.app.NotificationCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlaybackException;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.MediaSession;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.session.CommandButton;
import androidx.media3.session.MediaStyleNotificationHelper;
import android.support.v4.media.MediaMetadataCompat;
//import android.support.v4.media.app.NotificationCompat.MediaStyle;
//import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.slider.Slider;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;

public class Player extends AppCompatActivity implements View.OnClickListener {

    private static final String CHANNEL_ID = "Test";
    private static final int NOTIFICATION_ID = 1;
    public static MediaPlayer mp;
    public static ExoPlayer exp;

    public static MediaSession mediaSession;

    private NotificationManager notimgr;


    String songName;
    //ArrayList<Audio> mySongs;
    int position;
    Uri u;

    //SeekBar sb;

    Slider sl;
    Button btPlay,btFF,btFb,btNxt,btPv;
    Handler mhandler=new Handler();
    TextView stitle;


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    /*private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };*/

    @OptIn(markerClass = UnstableApi.class)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_player);

        GestureDetector gestureDetector = new GestureDetector(this, new GestureListener());
        View touchRegion = findViewById(R.id.touchRegion);
        touchRegion.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

        //sb=(SeekBar)findViewById(R.id.seekBar);
        sl=(Slider)findViewById(R.id.slider);

        btPlay = (Button)findViewById(R.id.btPlay);
        btFF = (Button)findViewById(R.id.btFF);
        btFb = (Button)findViewById(R.id.btFB);
        btNxt = (Button)findViewById(R.id.btNxt);
        btPv = (Button)findViewById(R.id.btPv);
        stitle=(TextView)findViewById(R.id.texttitle);

        btPlay.setOnClickListener(this);
        btFF.setOnClickListener(this);
        btFb.setOnClickListener(this);
        btNxt.setOnClickListener(this);
        btPv.setOnClickListener(this);



        Intent i = getIntent();
        Bundle b = i.getExtras();
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mySongs = b.getParcelableArrayList("songlist");
        }*/
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mySongs = b.getParcelable("songlist");
        }*/
        Audio audio= b.getParcelable("songlist");
        //ArrayList<Audio> songArray = i.getParcelableArrayListExtra("song_array");
        //mySongs = b.getParcelable("songlist");


        position =b.getInt("pos",0);

        //u = Uri.parse(mySongs.get(position+1).toString());
        u = Uri.parse(audio.getData());
        Log.d("Test", String.valueOf(u));
        //mp = MediaPlayer.create(getApplicationContext(),u);

        exp= new ExoPlayer.Builder(this).build();
        exp.setMediaItem(MediaItem.fromUri(u));

          // Add this line after initializing media player

        songName=i.getStringExtra("song_name");
        exp.prepare();
        exp.play();
        //mp.start();
        initializeMediaSession();
        createNotificationChannel();
        showNotification();
        Player.this.runOnUiThread(new Runnable(){
            @Override
            public void run() {
                if(exp!=null){
                    stitle.setText(songName);
                    long currentpos=exp.getCurrentPosition()/1000;
                    sl.setValue(currentpos);
                    //sb.setProgress(currentpos);

                }
                mhandler.postDelayed(this,1000);
            }

        });
        sl.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float progress, boolean fromUser) {
                Log.d("test", String.valueOf((int) exp.getDuration()/1000));
                sl.setValueTo( exp.getDuration() /1000);
                if(fromUser){
                    exp.seekTo((long)(progress*1000));
                }
            }
        });
        /*sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                sb.setMax(mp.getDuration()/1000);
                if(fromUser){
                    mp.seekTo(progress*1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });*/
    }

    @Override
    public void onClick(View v) {
        //stitle=findViewById(R.id.texttitle);
        //Intent i = getIntent();
        //ArrayList<Audio> songArray = i.getParcelableArrayListExtra("song_array");
        int id = v.getId();
        switch (id){
            case R.id.btPlay:
                if (exp.isPlaying()){
                    exp.pause();
                }
                else exp.play();
                break;
            case R.id.btFF:

                exp.seekTo(exp.getCurrentPosition()+5000);
                break;
            case R.id.btFB:

                exp.seekTo(exp.getCurrentPosition()-5000);
                break;
            case R.id.btNxt:
                playNextSong();
                break;
            case R.id.btPv:
                playPreviousSong();
                break;
        }
        showNotification();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.requestPointerCapture();
        }
    }

    /*@Override
    public boolean onTouchEvent(MotionEvent event){
        float initialX;
        float initialY;
        float currentX;
        float currentY;
        boolean touch;

        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            initialX = event.getX();
            initialY = event.getY();
            currentX = initialX;
            currentY = initialY;
        } else if (action == MotionEvent.ACTION_MOVE) {
            currentX = event.getX();
            currentY = event. getY();

            touch = true;
        } else if (action == MotionEvent.ACTION_UP) {
            touch = false;
        }
        return true; // Indicate event was handled
    }*/
    private class GestureListener extends GestureDetector.SimpleOnGestureListener{
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1 == null) return false;

            float diffX = e2.getX() - e1.getX();
            float diffY = e2.getY() - e1.getY();

            // Check if horizontal swipe is dominant
            if (Math.abs(diffX) > Math.abs(diffY)) {
                // Check if swipe distance and velocity meet threshold
                if (Math.abs(diffX) > SWIPE_THRESHOLD &&
                        Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        onSwipeRight();
                    } else {
                        onSwipeLeft();
                    }
                    return true;
                }
            }
            return false;
        }
        private void onSwipeLeft() {
            playPreviousSong();
        }

        private void onSwipeRight() {
            playNextSong();
        }
    }

    @OptIn(markerClass = UnstableApi.class)
    private void showNotification() {
        // Create intent for when notification is clicked
        Intent intent = new Intent(this, Player.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Create playback actions
        Intent playIntent = new Intent(this, MediaButtonReceiver.class);
        playIntent.setAction("ACTION_PLAY");
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(this, 0, playIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent pauseIntent = new Intent(this, MediaButtonReceiver.class);
        pauseIntent.setAction("ACTION_PAUSE");
        PendingIntent pausePendingIntent = PendingIntent.getBroadcast(this, 0, pauseIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent nextIntent = new Intent(this, MediaButtonReceiver.class);
        nextIntent.setAction("ACTION_NEXT");
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(this, 0, nextIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent prevIntent = new Intent(this, MediaButtonReceiver.class);
        prevIntent.setAction("ACTION_PREVIOUS");
        PendingIntent prevPendingIntent = PendingIntent.getBroadcast(this, 0, prevIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Build the notification
        Notification builder = null;
        //Notification.MediaStyle mediaStyle = new Notification.MediaStyle().setMediaSession(getMediaController().getSessionToken());
        //NotificationCompat.MediaStyle mediaStyle = new NotificationCompat.MediaStyle().setMediaSession(MediaSession.Token.fromToken(mediaSession2.getSessionToken()));
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.foreground2) // Add your own notification icon
                    .setContentTitle(songName)
                    .setContentText("Artist Name") // Modify as needed
                    .setContentIntent(pendingIntent)
                    //.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    //.setPriority(NotificationCompat.PRIORITY_LOW)
                    .setStyle(mediaStyle)
                            //.setShowActionsInCompactView(0, 1, 2) // Positions of actions in compact view
                    .addAction(R.drawable.foreground2, "Previous", prevPendingIntent)
                    .addAction(mp.isPlaying() ? R.drawable.foreground2 : R.drawable.foreground2,
                            mp.isPlaying() ? "Pause" : "Play",
                            mp.isPlaying() ? pausePendingIntent : playPendingIntent)
                    .addAction(R.drawable.foreground2, "Next", nextPendingIntent);
        }*/
        builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.foreground2)
                .addAction(R.drawable.foreground2, "Previous", prevPendingIntent)
                .addAction(R.drawable.foreground2, "Pause", pausePendingIntent)
                .addAction(R.drawable.foreground2, "Next", nextPendingIntent)
                .setStyle(new MediaStyleNotificationHelper.MediaStyle(mediaSession)
                        .setShowActionsInCompactView(3 /* #1: pause button */))
                .setContentTitle(songName)
                .setContentText("My Awesome Band")
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.foreground2))
                .build();

        /*Notification.Action pauseA = new Notification.Action.Builder(3,"Pause",pausePendingIntent).build();
        builder.addAction(pauseA);*/
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder);
    }

    @OptIn(markerClass = UnstableApi.class)
    private void initializeMediaSession() {

        //mediaSession = new MediaSessionCompat(this, "PlayerService");
        //private MediaSessionCompat mediaSession;
        //public static MediaSession mediaSession2;
        mediaSession = new MediaSession.Builder(this, exp).build();
        //mediaSession2 = new MediaSession(this, "PlayerService");

        Notification.MediaStyle media= new Notification.MediaStyle().setMediaSession(mediaSession.getPlatformToken());
        //Notification noti = new Notification.Builder(this,CHANNEL_ID);*/
        //mediaSession.setPlaybackState(new PlaybackStateCompat.Builder().setState(PlaybackStateCompat.STATE_PLAYING,(long) mp.getCurrentPosition(), (float)1.0).setActions(PlaybackStateCompat.ACTION_SEEK_TO).build());
        //mediaSession2.setPlaybackState(new PlaybackState.Builder().setState(PlaybackState.STATE_PLAYING,(long) mp.getCurrentPosition(), (float)1.0).setActions(PlaybackState.ACTION_SEEK_TO).build());

        /*mediaSession2.setCallback(new MediaSession.Callback() {
            @Override
            public void onPlay() {
                super.onPlay();
            }

            @Override
            public void onPause() {
                super.onPause();
            }

            @Override
            public void onStop() {
                super.onStop();
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
            }

            @Override
            public void onSeekTo(long pos) {
                super.onSeekTo(pos);
            }
        });

        /*mediaSession.setCallback(new MediaSession.Callback() {
            @Override
            public void onPlay() {
                if (!mp.isPlaying()) {
                    mp.start();
                    updatePlaybackState();
                }
            }

            @Override
            public void onPause() {
                if (mp.isPlaying()) {
                    mp.pause();
                    updatePlaybackState();
                }
            }

            @Override
            public void onSkipToNext() {
                playNextSong();
            }

            @Override
            public void onSkipToPrevious() {
                playPreviousSong();
            }

            @Override
            public void onSeekTo(long pos) {
                mp.seekTo((int) pos);
                updatePlaybackState();
            }
        });
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        // Set initial playback state and metadata
        updatePlaybackState();
        updateMetadata();
        mediaSession.setActive(true); // Activate the media session*/
    }
    private void updatePlaybackState() {
        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PAUSE |
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_SEEK_TO);

        stateBuilder.setState(exp.isPlaying() ? PlaybackStateCompat.STATE_PLAYING : PlaybackStateCompat.STATE_PAUSED,
                exp.getCurrentPosition(), 1.0f);
        //mediaSession.setPlaybackState(stateBuilder.build());
    }

    // Update song metadata (title, artist, etc.)
    private void updateMetadata() {
        MediaMetadataCompat.Builder metadataBuilder = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, songName)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, exp.getDuration());
        //mediaSession.setMetadata(metadataBuilder.build());
    }
    private void playNextSong() {
        exp.stop();
        //exp.release();
        if (position + 1 > mysongs.size() - 1) {
            position = 0;
        } else {
            position += 1;
        }
        selectedAudio = mysongs.get(position);
        u = Uri.parse(selectedAudio.getData());
        songName = selectedAudio.geStitle();
        exp.setMediaItem(MediaItem.fromUri(u));
        Log.d("test", String.valueOf((int) exp.getDuration()/1000));
        exp.prepare();
        exp.play();

        //sl.setValueTo((float) exp.getDuration() / 1000);
        updatePlaybackState();
        updateMetadata();
        stitle.setText(songName);
        showNotification();
    }

    // Play the previous song
    private void playPreviousSong() {
        exp.stop();
        //exp.release();
        if (position - 1 < 0) {
            position = mysongs.size() - 1;
        } else {
            position -= 1;
        }
        selectedAudio = mysongs.get(position);
        u = Uri.parse(selectedAudio.getData());
        songName = selectedAudio.geStitle();
        exp.setMediaItem(MediaItem.fromUri(u));
        exp.prepare();
        exp.play();
        //sl.setValueTo(mp.getDuration() / 1000);
        updatePlaybackState();
        updateMetadata();
        stitle.setText(songName);
        showNotification();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Media Playback",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Media playback controls");
            channel.setShowBadge(false);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
   /* @Override
    protected void onDestroy() {
        super.onDestroy();
        if (exp != null) {
            exp.release();
            exp = null;
        }
        if (mediaSession != null) {
            mediaSession.release();
        }
    }*/


}
