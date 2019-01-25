package org.zijonas.man.alarmduinoremote;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

import org.zijonas.man.alarmduinoremote.connect.MqttService;

public class AlarmduinoHome extends AppCompatActivity {

    Button buttonConnect;
    Intent myServiceIntent;
    private MqttService myService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarmduino_home);
        myService = new MqttService(getApplicationContext());
        myServiceIntent = new Intent(getApplicationContext(), myService.getClass());

        if(isMyServiceRunning(myService.getClass())) {
            startService(myServiceIntent);
        }

        buttonConnect = findViewById(R.id.buttonTrigger);

        Status.registerListener(new MessageReceivedListener() {
            @Override
            public void onMessageReceived() {
                int state = Status.getStatus();
                if (state == Status.ENABLED) {
                    buttonConnect.setBackgroundColor(Color.GREEN);
                    buttonConnect.setText("ACTIVATED");
                } else {
                    if (state == Status.DISABLED) {
                        buttonConnect.setBackgroundColor(Color.BLUE);
                        buttonConnect.setText("DESACTIVATED");
                    }
                }
            }
        });

        Log.d("Initializing.....", "Home act");

//        buttonConnect.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    Log.d("HOME", "Button clicked");
//                    if (alarmduinoClient.isConnected()) {
//                        alarmduinoClient.subscribe(Constants.TOPIC, 0);
//                    } else {
//                        Log.d("HOME", "Client not conected");
//                    }
//                } catch (MqttException e) {
//                    e.printStackTrace();
//                }
//            }
//        });


//        Log.d("HOME", "Start service");

//        Intent intent = new Intent(AlarmduinoHome.this, MqttService.class);
//        startService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(myServiceIntent);
        Log.i("Main Activiti", "Destroyed!!");
        Status.unregisterListener();
    }

    private boolean isMyServiceRunning(Class<?> pServiceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo rService : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (rService.service.getClassName().equals(pServiceClass.getName())) {
                Log.i("MyService", "Is running");
                return true;
            }
        }
        Log.i("MyService", "Is not running");
        return false;
    }
}
