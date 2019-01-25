package org.zijonas.man.alarmduinoremote;

public class Status {
    private static int status = 0;
    private static MessageReceivedListener listener;

    public static final int FIRED = 5;
    public static final int ENABLED = 30;
    public static final int REARMED = 60;
    public static final int DISABLED = 100;

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

    public static void registerListener(MessageReceivedListener pListener) {
        listener = pListener;
    }

    public static void unregisterListener() {
        listener = null;
    }

}
