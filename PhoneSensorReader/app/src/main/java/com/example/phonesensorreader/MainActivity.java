package com.example.phonesensorreader;

import static java.util.Collections.emptyMap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeoutException;

public class MainActivity extends AppCompatActivity implements SensorEventListener,View.OnClickListener {

    private TextView mTextViewAccX;
    private TextView mTextViewAccY;
    private TextView mTextViewAccZ;

    private volatile Channel ch;
    private volatile Connection conn;

    private volatile RabbitManager rabbitManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextViewAccX = (TextView) findViewById(R.id.textView1acc);
        mTextViewAccY = (TextView) findViewById(R.id.textView2acc);
        mTextViewAccZ = (TextView) findViewById(R.id.textView3acc);

        SensorManager sm = (SensorManager)this.getSystemService(SENSOR_SERVICE);
        Sensor se = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener((SensorEventListener) this, se, SensorManager.SENSOR_DELAY_GAME);

        Button publishButton = (Button) findViewById(R.id.publish);
        Button stopPublishButton = (Button) findViewById(R.id.stopPublish);

        publishButton.setOnClickListener(this);
        stopPublishButton.setOnClickListener(this);

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mTextViewAccX.setText("acc X =" + (sensorEvent.values[0]));
            mTextViewAccY.setText("acc Y =" + (sensorEvent.values[1]));
            mTextViewAccZ.setText("acc Z =" + (sensorEvent.values[2]));
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("test");
                    if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                        String strToSent = "";
                        if (ch != null) {
                            JSONObject jo = new JSONObject();
                            try {
                                jo.put("X:", String.valueOf(sensorEvent.values[0]));
                                jo.put("Y:", String.valueOf(sensorEvent.values[1]));
                                jo.put("Z:", String.valueOf(sensorEvent.values[2]));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            strToSent = jo.toString();
                        }
                        Channel chAux = ch;
                        if (chAux != null) {
                            try {
                                chAux.basicPublish("", "dataPhoneQueue", null, strToSent.getBytes());
                                System.out.println("-> " + strToSent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) { }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.publish:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                System.out.println("   - START button pressed -");
                                ConnectionFactory factory1 = new ConnectionFactory();
                                factory1.setUri("amqps://qnovhajz:3Mqw-uz3laEhM3mGfI9a-XHPNztW76ln@goose.rmq2.cloudamqp.com/qnovhajz");
                                Connection conAux = factory1.newConnection();
                                conn = conAux;
                                Channel chAux = conAux.createChannel();
                                ch = chAux;
                                chAux.queueDeclare("dataPhoneQueue", true, false, false, emptyMap());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        }).start();
                break;
            case R.id.stopPublish:
                System.out.println("    - STOP button pressed -");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            conn.close();
                            conn = null;
                            ch = null;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
        }
    }

    Thread publishThread;
    ConnectionFactory factory = new ConnectionFactory();
    public void publishToAMQP()
    {
        publishThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        Connection connection = factory.newConnection();
                        Channel ch = connection.createChannel();
                        ch.confirmSelect();
                    } catch (Exception e) {
                        Log.d("", "Connection broken: " + e.getClass().getName());
                    }
                }
            }
        });
        publishThread.start();
    }
}
