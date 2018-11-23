package org.zijonas.man.alarmduinoremote;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.zijonas.man.alarmduinoremote.connect.AlarmduinoMqttClient;
import org.zijonas.man.alarmduinoremote.connect.MqttService;

public class AlarmduinoHome extends AppCompatActivity {

    MqttAndroidClient mqttClient;
    AlarmduinoMqttClient alarmduinoClient;
    Button connectButton;
    EditText textArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarmduino_home);

        alarmduinoClient = new AlarmduinoMqttClient();
        mqttClient = alarmduinoClient.getClient(getApplicationContext(), Constants.BROKER_URL, Constants.CLIENT_ID);

        textArea = (EditText) findViewById(R.id.editTextMessages);

        mqttClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                Log.d("CONNECTED.....", "Home act");
            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("Message arrived.....", message.toString());
                textArea.setText(message.toString());

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        Log.d("Initializing.....", "Home act");

        connectButton = (Button) findViewById(R.id.buttonConnect);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Log.d("HOME", "Button clicked");
                    if (mqttClient.isConnected()) {
                        alarmduinoClient.subscribe(mqttClient, Constants.PUBLISH_TOPIC, 0);
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
