package com.example.mediaplayerv3;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private MyService myService; // calling instance of Myservice class to allows us to use its functions
    private Intent intent;
    private String Path;//filepath of selected music file

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ListView lv = (ListView) findViewById(R.id.listView);
        final TextView textviewDuration = (TextView) findViewById(R.id.Duration);
        final TextView textviewProgress = (TextView) findViewById(R.id.Progress);
        final SeekBar probar = (SeekBar) findViewById(R.id.seekBar);
        final Handler handler = new Handler();

        final File musicDir = new File(
                Environment.getExternalStorageDirectory().getPath()+ "/Download/");

        File list[] = musicDir.listFiles();

        lv.setAdapter(new ArrayAdapter<File>(this,android.R.layout.simple_list_item_1, list));

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt,
                                    long mylng) {
                File selectedFromList =(File) (lv.getItemAtPosition(myItemInt));
                Path = selectedFromList.getAbsolutePath();


                myService.startfile(Path);

                textviewDuration.setText(myService.getDuration());
                probar.setMax(myService.getDurationMS() / 1000);

                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        probar.setProgress(myService.getSongProgress() / 1000);
                        textviewProgress.setText(myService.getProgress());
                        handler.postDelayed(this, 1000);
                    }
                });

                probar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean input) {
                        if(input)
                            myService.setProgress(progress);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {}
                });
            }

        });



        playMusic();
        pauseMusic();
        stopMusic();

    }

    private ServiceConnection MusicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.MediaBinder binder = (MyService.MediaBinder)service;
            myService = binder.getService();

            Log.d("g53mdp", "Service connection successful");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("g53mdp", "Service Disconnection successful");
        }
    };

    @Override
    protected void onStart()
    {
        super.onStart();
        if(intent == null)
        {
            intent = new Intent(getBaseContext(), MyService.class);
            bindService(intent, MusicConnection, Context.BIND_AUTO_CREATE);
            startService(intent);
            Log.d("g53mdp", "Service Started");
        }
    }

    @Override
    protected void onDestroy()
    {
        stopService(intent);
        myService = null;
        super.onDestroy();
        Log.d("g53mdp", "Service Destroyed");
    }

    public void playMusic() //configuring button functions for play,pause,stop
    {
        ImageButton button = (ImageButton) findViewById(R.id.PlayButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                myService.play();
            }
        });
    }


    public void pauseMusic()
    {
        ImageButton button = (ImageButton) findViewById(R.id.PauseButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                myService.pause();
            }
        });
    }


    public void stopMusic()
    {
        ImageButton button = (ImageButton) findViewById(R.id.StopButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                myService.stop();
            }
        });
    }

}
