package org.zijonas.man.alarmduinoremote.helper;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonHelper {
    public static Object getElement(@NonNull JSONObject pObject, String pElement) throws JSONException {
        return pObject.getJSONObject("alarm").getString(pElement);
    }
}
