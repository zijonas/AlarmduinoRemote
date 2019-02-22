package org.zijonas.man.alarmduinoremote;

import java.util.HashMap;

public class Status {
    private static int status = 0;
    private static MessageReceivedListener listener;

    public static final int FIRED = 5;
    public static final int ENABLED = 30;
    public static final int REARMED = 60;
    public static final int DISABLED = 100;
    public static final int IS_ALIVE = 1;

    private static HashMap<Integer, String> descs = null;

    public static int getStatus() {
        return status;
    }

    public static void setStatus(int pStatus) {
        if (pStatus != status) {
            status = pStatus;
            if(listener != null) {
                listener.onMessageReceived();
            }
        }
    }

    public static String description() {
        if(descs == null) {
            descs = new HashMap<>(4);
            descs.put(5, "Fired");
            descs.put(30, "Enabled");
            descs.put(60, "Rearmed");
            descs.put(100, "Disabled");
        }
        return descs.get(status);
    }

    public static void registerListener(MessageReceivedListener pListener) {
        listener = pListener;
    }

    public static void unregisterListener() {
        listener = null;
    }

}
