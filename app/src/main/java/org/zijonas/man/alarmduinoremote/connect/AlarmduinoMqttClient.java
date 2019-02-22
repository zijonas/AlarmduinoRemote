package org.zijonas.man.alarmduinoremote.connect;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

public class AlarmduinoMqttClient extends MqttAndroidClient{


    public AlarmduinoMqttClient(Context pContext, String pServerURI, String pClientId) {
        super(pContext, pServerURI, pClientId);

        initClient(pContext, pServerURI, pClientId);
    }

    private void initClient(Context pContext, final String pServerUrl, final String pClientId) {

        try {
            IMqttToken token = super.connect(getMqttConnectionOption());
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    setBufferOpts(getDisconnectedBufferOptions());
                    Log.d(this.getClass().toString(), "Connect Success");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d(this.getClass().toString(), "Connect Failed - Verify the config " + pServerUrl + " " + pClientId);
                    Log.d("Exception:", exception.getMessage());
                    exception.printStackTrace();
                }
            });

        } catch (MqttException ex) {
            Log.d(this.getClass().toString(), "Connect Failed to connect to server");
        }
    }

    public void publishMessage(@NonNull String pMsg, int pQos, @NonNull String pTopic) throws MqttException, UnsupportedEncodingException {
        byte[] encodedPayload;
        encodedPayload = pMsg.getBytes("UTF-8");
        MqttMessage message = new MqttMessage(encodedPayload);
        message.setRetained(true);
        message.setQos(pQos);
        publish(pTopic, message);
    }

    public void subscribeTopic(@NonNull final String pTopic, int pQos) throws MqttException {
        IMqttToken token = super.subscribe(pTopic, pQos);
        token.setActionCallback(new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken iMqttToken) {
                Log.d(this.getClass().toString(), "Subscribe Successfully " + pTopic);
            }

            @Override
            public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                Log.e(this.getClass().toString(), "Subscribe Failed " + pTopic);
            }
        });
    }

    public void unsubscribeTopic(@NonNull final String pTopic) throws MqttException {
        IMqttToken token = super.unsubscribe(pTopic);
        token.setActionCallback(new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken iMqttToken) {
                Log.d(this.getClass().toString(), "UnSubscribe Successfully " + pTopic);
            }

            @Override
            public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                Log.e(this.getClass().toString(), "UnSubscribe Failed " + pTopic);
            }
        });
    }

    public IMqttToken disconnect() throws MqttException {
        IMqttToken mqttToken = super.disconnect();
        mqttToken.setActionCallback(new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken iMqttToken) {
                Log.d(this.getClass().toString(), "Successfully disconnected");
            }

            @Override
            public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                Log.d(this.getClass().toString(), "Failed to disconnected " + throwable.toString());
            }
        });
        return mqttToken;
    }

    private DisconnectedBufferOptions getDisconnectedBufferOptions() {
        DisconnectedBufferOptions dBufferOptions = new DisconnectedBufferOptions();
        dBufferOptions.setBufferEnabled(true);
        dBufferOptions.setBufferSize(100);
        dBufferOptions.setPersistBuffer(false);
        dBufferOptions.setDeleteOldestMessages(false);
        return dBufferOptions;
    }

    private MqttConnectOptions getMqttConnectionOption() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setAutomaticReconnect(true);
        return mqttConnectOptions;
    }
}
