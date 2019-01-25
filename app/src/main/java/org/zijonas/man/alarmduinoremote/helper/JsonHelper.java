package org.zijonas.man.alarmduinoremote.helper;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonHelper {
    public static Object getElement(@NonNull JSONObject pObject, String pElement) throws JSONException {
        if(pElement.contains(".")) {
            int token = pElement.indexOf('.');
            String name = pElement.substring(0, token);
            return getElement(pObject.getJSONObject(name), pElement.substring(token + 1));
        } else {
            return pObject.get(pElement);
        }
    }
}
