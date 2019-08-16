 package com.example.temperaturesense;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

 public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connect();
    }

    public void connect() {
        String clientId = MqttClient.generateClientId();
        final MqttAndroidClient client =
                new MqttAndroidClient(this.getApplicationContext(),"tcp://soldier.cloudmqtt.com:17702",clientId);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
        options.setCleanSession(false);
        options.setUserName("ckbdtizs");
        options.setPassword("hASIpo8xbF36".toCharArray());

        try{
            IMqttToken token = client.connect(options);
            //IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    //Connected
                    Log.e("file", "onSuccess");
                    //publish(client,"payload");
                    subscribe(client,"dht");
                    subscribe(client,"bmp");
                    client.setCallback(new MqttCallback() {
                        TextView tt = findViewById(R.id.temp);
                        TextView th = findViewById(R.id.humid);
                        @Override
                        public void connectionLost(Throwable cause) {

                        }

                        @Override
                        public void messageArrived(String topic, MqttMessage message) throws Exception {
                            Log.e("file",message.toString());

                            if(topic.equals("dht")){
                                tt.setText(message.toString());
                            }

                            if (topic.equals("bmp")){
                                th.setText(message.toString());
                            }

                        }

                        @Override
                        public void deliveryComplete(IMqttDeliveryToken token) {

                        }
                    });
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e("file", "onFailure: ");
                }
            });
        }catch (MqttException e)
        {
            e.printStackTrace();
        }
    }
     public void publish(MqttAndroidClient client, String payload){
         String topic = "foo/bar";
         byte[] encodedPayload = new byte[0];
         try {
             encodedPayload = payload.getBytes("UTF-8");
             MqttMessage message = new MqttMessage(encodedPayload);
             client.publish(topic, message);
         } catch (UnsupportedEncodingException | MqttException e) {
             e.printStackTrace();
         }
     }

     public void subscribe(MqttAndroidClient client , String topic){
         int qos = 1;
         try {
             IMqttToken subToken = client.subscribe(topic, qos);
             subToken.setActionCallback(new IMqttActionListener() {

                 @Override
                 public void onSuccess(IMqttToken asyncActionToken) {
                     // The message was published
                 }

                 @Override
                 public void onFailure(IMqttToken asyncActionToken,
                                       Throwable exception) {
                     // The subscription could not be performed, maybe the user was not
                     // authorized to subscribe on the specified topic e.g. using wildcards

                 }
             });
         } catch (MqttException e) {
             e.printStackTrace();
         }
     }
}
