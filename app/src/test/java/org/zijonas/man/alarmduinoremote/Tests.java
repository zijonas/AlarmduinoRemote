package org.zijonas.man.alarmduinoremote;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.zijonas.man.alarmduinoremote.helper.JsonHelper;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class Tests {


    @Test
    public void Jsontest() {
        String str = "{"+
                "\"alarm\": {\n" +
                "\"state\": \"100\",\n" +
                "\"timestamp\": \"88662861\"\n" +
                "\n" +
                "}\n" +
                "}";

        try {
            JSONObject my_obj = new JSONObject(str);
            assertEquals("100", JsonHelper.getElement(my_obj, "alarm.state"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}