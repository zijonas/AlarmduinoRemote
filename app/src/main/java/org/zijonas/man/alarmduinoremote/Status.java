package org.zijonas.man.alarmduinoremote;

import java.util.ArrayList;

public class Status {
    private static int status = 0;

    private static ArrayList<MessageReceivedListener> listeners = new ArrayList<MessageReceivedListener>();


    public static int getStatus() {
        return status;
    }

    public static void setStatus(int pStatus) {
        if(pStatus != status) {
            status = pStatus;
            for(MessageReceivedListener l : listeners) {
                try {
                    l.onMessageReceived();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            for(MessageReceivedListener rl : listeners) {
                rl.onMessageReceived();
            }
        }
    }

    public static void registerListener(MessageReceivedListener pListener) {
        listeners.add(pListener);
    }

}
