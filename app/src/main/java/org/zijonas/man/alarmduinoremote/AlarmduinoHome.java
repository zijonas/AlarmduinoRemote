package org.zijonas.man.alarmduinoremote;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.zijonas.man.alarmduinoremote.connect.AlarmduinoMqttClient;
import org.zijonas.man.alarmduinoremote.connect.MqttService;

public class AlarmduinoHome extends AppCompatActivity {

    AlarmduinoMqttClient alarmduinoClient;
    Button buttonConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarmduino_home);

        alarmduinoClient = new AlarmduinoMqttClient(getApplicationContext(), Constants.BROKER_URL, Constants.CLIENT_ID);

        buttonConnect = findViewById(R.id.buttonConnect);

        Status.registerListener(new MessageReceivedListener() {
            @Override
            public void onMessageReceived() {
                int state = Status.getStatus();
                if (state == Status.ENABLED) {
                    buttonConnect.setBackgroundColor(Color.GREEN);
                } else {
                    if (state == Status.DISABLED) {
                        buttonConnect.setBackgroundColor(Color.BLUE);
                    }
                }
            }
        });

        Log.d("Initializing.....", "Home act");

        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Log.d("HOME", "Button clicked");
                    if (alarmduinoClient.isConnected()) {
                        alarmduinoClient.subscribe(Constants.TOPIC, 0);
                    } else {
                        Log.d("HOME", "Client not conected");
                    }
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });


        Log.d("HOME", "Start service");

        Intent intent = new Intent(AlarmduinoHome.this, MqttService.class);
        startService(intent);
    }
}
