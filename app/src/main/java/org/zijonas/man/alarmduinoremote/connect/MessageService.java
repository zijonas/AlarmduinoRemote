package org.zijonas.man.alarmduinoremote.connect;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;
import org.zijonas.man.alarmduinoremote.gui.AlarmduinoHome;
import org.zijonas.man.alarmduinoremote.Constants;
import org.zijonas.man.alarmduinoremote.R;
import org.zijonas.man.alarmduinoremote.Status;
import org.zijonas.man.alarmduinoremote.helper.JsonHelper;

public class MessageService extends Service {
    private static final String TAG = "MessageService";
    public static final String NOTIFICATION_CHANNEL = "AlamduinoRemoteNotifyChannel";
    public static final String NOTIFICATION_CHANNEL_NAME = "Alamduino Remote Notification Channel";
    private AlarmduinoMqttClient client;
    private NotificationManager notifManager;

    public AlarmduinoMqttClient getClient() {
        return client;
    }

    private HandlerThread mHandlerThread;
    private Handler mHandler;
    private boolean run = true;
    private int i = 0;

    public MessageService() {
    }

    public MessageService(Context applicationContext) {
        super();
        Log.d(TAG, "Constructor");
        final Context ctx = applicationContext;


        if (client == null) {
            client = new AlarmduinoMqttClient(applicationContext, Constants.BROKER_URL, Constants.CLIENT_ID);

            client.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectComplete(boolean b, String s) {
                    Log.d(TAG, "Connected");
                }

                @Override
                public void connectionLost(Throwable throwable) {
                    Log.d(TAG, "Connection Lost\n" + throwable.getStackTrace().toString());
                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) {
                    Log.d(this.getClass().toString(), mqttMessage.toString());
                    String state = (String) getElement(mqttMessage.toString(), "alarm.state");
//                    String type = (String) getElement(mqttMessage.toString(), "alarm.type");
                    if (state != null && Integer.parseInt(state) != Status.getStatus()) {
                        Status.setStatus(Integer.parseInt(state));
                        setMessageNotification(ctx, "New Status", Status.description());
                    } else {
                        Status.setStatus(Integer.parseInt(state));
                    }
                        /*if(Status.IS_ALIVE == Integer.parseInt(type)) {
                        setMessageNotification(ctx, "Is Alive", "Actual status:" + Status.description());
                    }*/
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
            });
            mHandlerThread = new HandlerThread("LocalServiceThread");
            mHandlerThread.start();

            mHandler = new Handler(mHandlerThread.getLooper());
            postRunnable(new Runnable() {
                @Override
                public void run() {
                    while (!client.isConnected()) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        client.subscribe(Constants.TOPIC, 0);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        run = false;
        Intent starterIntent = new Intent(this, ServiceStarter.class);
        sendBroadcast(starterIntent);
        if (client != null) {
            client.unregisterResources();
            client.close();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        mHandlerThread = new HandlerThread("LocalServiceThread");
        mHandlerThread.start();

        mHandler = new Handler(mHandlerThread.getLooper());
        Log.d(TAG, "onStartCommand");
        return START_STICKY;
    }

    void setMessageNotification(Context pContext, @NonNull String topic, @NonNull String msg) {
        Log.i(this.getClass().toString(), "Write notification");

        final int NOTIFY_ID = 12; // ID of notification
        Intent intent;
        PendingIntent pendingIntent;
        NotificationCompat.Builder builder;
        if (notifManager == null) {
            notifManager = (NotificationManager) pContext.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = notifManager.getNotificationChannel(NOTIFICATION_CHANNEL);
            if (mChannel == null) {
                mChannel = new NotificationChannel(NOTIFICATION_CHANNEL, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                mChannel.enableVibration(true);
                mChannel.enableLights(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(pContext, NOTIFICATION_CHANNEL);
            intent = new Intent(pContext, AlarmduinoHome.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(pContext, 0, intent, 0);
            builder.setContentTitle(msg)
                    .setSmallIcon(R.mipmap.shield)
                    .setContentText(pContext.getString(R.string.app_name))
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(msg);
        } else {
            builder = new NotificationCompat.Builder(pContext, NOTIFICATION_CHANNEL);
            intent = new Intent(pContext, AlarmduinoHome.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(pContext, 0, intent, 0);
            builder.setContentTitle(msg)
                    .setSmallIcon(R.mipmap.shield)
                    .setContentText(pContext.getString(R.string.app_name))
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(msg)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                    .setPriority(Notification.PRIORITY_HIGH);
        }
        Notification notification = builder.build();
        notifManager.notify(NOTIFY_ID, notification);
    }

    @Nullable
    private Object getElement(String pMessage, String pElement) {
        try {
            return JsonHelper.getElement(new JSONObject(pMessage), pElement);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void postRunnable(Runnable pRunnable) {
        mHandler.post(pRunnable);
    }
}