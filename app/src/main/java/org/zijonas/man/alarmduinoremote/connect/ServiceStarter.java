package org.zijonas.man.alarmduinoremote.connect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class ServiceStarter extends BroadcastReceiver {

    public ServiceStarter() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(ServiceStarter.class.toString(), "Starting message service");

        context.startService(new Intent(context, MessageService.class));
    }

}
