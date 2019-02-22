package org.zijonas.man.alarmduinoremote.controler;

import android.widget.ImageButton;

import org.zijonas.man.alarmduinoremote.model.WatchDuino;

public class WatchDuinoController {
    private WatchDuino myModel;

    public WatchDuinoController () { }

    public WatchDuinoController (WatchDuino pWatchDuino) {
        myModel = pWatchDuino;
    }

    public void setMyModel(WatchDuino myModel) {
        this.myModel = myModel;
    }

    public WatchDuino getMyModel() {
        if(myModel == null) {
            myModel = fetch();
        }
        return myModel;
    }

    private WatchDuino fetch() {
        //TODO Get it from persistence
        return new WatchDuino();
    }

    public ImageButton constructGui(ImageButton pButton) {
        return null;
    }

    public void startService() {

    }

    //TODO Status and actualize model
}
