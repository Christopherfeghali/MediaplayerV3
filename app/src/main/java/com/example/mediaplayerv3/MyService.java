package com.example.mediaplayerv3;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.concurrent.TimeUnit;

public class MyService extends Service {

    private final IBinder PB = new MediaBinder();
    private MP3player MP;
    private String Path;
    private static final int NOTIFY_ID=1;

    public void onCreate()
    {
        super.onCreate();
        MP = new MP3player(); // create instance of mp3player on creation of service to be used to call its functions
        Log.d("g53mdp", "Service onCreate");
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return PB;
    }

    @Override
    public boolean onUnbind(Intent intent)
    {
        MP.stop();
        return false;
    }

    public class MediaBinder extends Binder
    {
        MyService getService()
        {
            return MyService.this;
        }
    }


    public void startfile(String filePath_) //starts the selected file using filepath from getpath earlier
    {
        Path = filePath_;
        MP.stop();
        if(Path != null)
        {
            MP.load(Path);
            Log.d("g53mdp", "Music started =  " + Path);
        }
        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendInt)
                .setOngoing(true)
                .setContentTitle("Playing");
        Notification not = builder.build();

        startForeground(NOTIFY_ID, not);
    }


    public void play()
    {
        MP.play();
        Log.d("g53mdp", "Service play success()"); //Plays song if it was paused
    }


    public void pause()
    {
        MP.pause();
        Log.d("g53mdp", "Service pause success"); //Pauses song if it was playing
    }


    public void stop()
    {
        MP.stop();
        Log.d("g53mdp", "Service stop success");
    }


    public String getDuration()
    {
        String duration = (String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(MP.getDuration()) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(MP.getDuration())),
                TimeUnit.MILLISECONDS.toSeconds(MP.getDuration()) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(MP.getDuration())))); // get full song duration
        return duration;
    }


    public String getProgress()
    {
        String progress = (String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(MP.getProgress()) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(MP.getProgress())),
                TimeUnit.MILLISECONDS.toSeconds(MP.getProgress()) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(MP.getProgress())))); // song progress in milliseconds
        return progress;
    }


    public int getDurationMS()
    {
        return MP.getDuration();
    } //return full duration of song


    public int getSongProgress()
    {
        return MP.getProgress();
    } //gets song progress in milliseconds


    public void setProgress(int seekProgress_)
    {
        MP.seekTo(seekProgress_*1000); // seek to certain progress in song
    }

    //Destroy notification if service is destroyed
    @Override
    public void onDestroy()
    {
        stopForeground(true);
    }



}
