package org.zijonas.man.alarmduinoremote.gui;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;

import org.zijonas.man.alarmduinoremote.MessageReceivedListener;
import org.zijonas.man.alarmduinoremote.R;
import org.zijonas.man.alarmduinoremote.Status;
import org.zijonas.man.alarmduinoremote.connect.MessageService;

public class AlarmduinoHome extends AppCompatActivity {

    ImageButton buttonConnect;
    ImageButton buttonSettings;
    Intent myServiceIntent;
    private MessageService myService;
    private Context context;

    private Context getContext() {
        return context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarmduino_home);
        this.context = getApplicationContext();
        myService = new MessageService(getContext());
        myServiceIntent = new Intent(getContext(), myService.getClass());

        if(!isMyServiceRunning(myService.getClass())) {
            Log.i(this.getClass().toString(), "Service not running. Starting it.");
            startService(myServiceIntent);
        }

//        buttonSettings = findViewById(R.id.buttonSettings);
//        buttonSettings.setImageResource(R.mipmap.config);


        buttonConnect = findViewById(R.id.buttonTrigger);

        Status.registerListener(new MessageReceivedListener() {
            @Override
            public void onMessageReceived() {
                int state = Status.getStatus();
                Log.i(this.getClass().toString(), "New Status = " + state);

                switch (state) {
                    case Status.ENABLED : {
                        buttonConnect.setBackgroundColor(Color.GREEN);
                        buttonConnect.setImageResource(R.mipmap.shield_active);
                        buttonConnect.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        break;
                    }
                    case Status.DISABLED: {
                        buttonConnect.setBackgroundColor(Color.BLUE);
                        buttonConnect.setImageResource(R.mipmap.shield_not_active);
                        buttonConnect.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        break;
                    }
                    case Status.FIRED : {
                        buttonConnect.setBackgroundColor(Color.RED);
                        buttonConnect.setImageResource(R.mipmap.ic_launcher_round);
                        buttonConnect.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        break;
                    }
                    case Status.REARMED: {

                        break;
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        stopService(myServiceIntent);
        Log.i(this.getClass().toString(), "Destroyed!!");
        Status.unregisterListener();
        super.onDestroy();
    }

    private boolean isMyServiceRunning(Class<?> pServiceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo rService : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (rService.service.getClassName().equals(pServiceClass.getName())) {
                Log.i(this.getClass().toString(), "MyService Is running");
                return true;
            }
        }
        Log.i(this.getClass().toString(), "MyService Is not running");
        return false;
    }
}
