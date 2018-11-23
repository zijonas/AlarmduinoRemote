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

public class AlarmduinoMqttClient {

    MqttAndroidClient client;

    public MqttAndroidClient getClient(Context pContext, final String pServerUrl, final String pClientId) {

        client = new MqttAndroidClient(pContext, pServerUrl, pClientId);

        try {
            IMqttToken token = client.connect(getMqttConnectionOption());
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    client.setBufferOpts(getDisconnectedBufferOptions());
                    Log.d("Client", "Connect Success");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d("Client", "Connect Failed - Verify the config " + pServerUrl + " " + pClientId);
                    Log.d("Exception:", exception.getMessage());
                    exception.printStackTrace();
                }
            });

        } catch (MqttException ex) {
            Log.d("Client", "Connect Failed to connect to server");
        }

        return client;
    }

    public void publishMessage(@NonNull MqttAndroidClient pClient, @NonNull String pMsg, int pQos, @NonNull String pTopic) throws MqttException, UnsupportedEncodingException {
        byte[] encodedPayload;
        encodedPayload = pMsg.getBytes("UTF-8");
        MqttMessage message = new MqttMessage(encodedPayload);
        message.setRetained(true);
        message.setQos(pQos);
        pClient.publish(pTopic, message);
    }

    public void subscribe(@NonNull MqttAndroidClient pClient, @NonNull final String pTopic, int pQos) throws MqttException {
        IMqttToken token = pClient.subscribe(pTopic, pQos);
        token.setActionCallback(new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken iMqttToken) {
                Log.d("Client", "Subscribe Successfully " + pTopic);
            }

            @Override
            public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                Log.e("Client", "Subscribe Failed " + pTopic);
            }
        });
    }

    public void unSubscribe(@NonNull MqttAndroidClient pClient, @NonNull final String pTopic) throws MqttException {
        IMqttToken token = pClient.unsubscribe(pTopic);
        token.setActionCallback(new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken iMqttToken) {
                Log.d("Client", "UnSubscribe Successfully " + pTopic);
            }

            @Override
            public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                Log.e("Client", "UnSubscribe Failed " + pTopic);
            }
        });
    }

    public void disconnect(@NonNull MqttAndroidClient pClient) throws MqttException {
        IMqttToken mqttToken = pClient.disconnect();
        mqttToken.setActionCallback(new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken iMqttToken) {
                Log.d("Client", "Successfully disconnected");
            }

            @Override
            public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                Log.d("Client", "Failed to disconnected " + throwable.toString());
            }
        });
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
