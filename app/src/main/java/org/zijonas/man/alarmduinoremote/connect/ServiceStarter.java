package org.zijonas.man.alarmduinoremote.connect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ServiceStarter extends BroadcastReceiver {

    public ServiceStarter() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Message Receive", "Starting mesage service");
        context.startService(new Intent(context, MqttService.class));
    }

}
