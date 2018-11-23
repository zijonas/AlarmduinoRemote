package org.zijonas.man.alarmduinoremote.connect;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.zijonas.man.alarmduinoremote.AlarmduinoHome;
import org.zijonas.man.alarmduinoremote.Constants;
import org.zijonas.man.alarmduinoremote.R;

public class MqttService extends Service {
    private static final String TAG = "MqttService";
    private AlarmduinoMqttClient client;
    private MqttAndroidClient aClient;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "On Create");

        if(client == null) {
            client = new AlarmduinoMqttClient();
            aClient = client.getClient(getApplicationContext(), Constants.BROKER_URL, Constants.CLIENT_ID);

            aClient.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectComplete(boolean b, String s) {
                    Log.d(TAG, "Connected");
                }

                @Override
                public void connectionLost(Throwable throwable) {
                    Log.d(TAG, "Connection Lost\n" + throwable.getStackTrace());
                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                    setMessageNotification(s, new String(mqttMessage.getPayload()));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
            });
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return START_STICKY;
    }

    private void setMessageNotification(@NonNull String topic, @NonNull String msg) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "XXX")
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle(topic)
                        .setContentText(msg);
        Intent resultIntent = new Intent(this, AlarmduinoHome.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(AlarmduinoHome.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(100, mBuilder.build());
    }
}
