package com.example.phonesensorreader;

import static java.util.Collections.emptyMap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
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
import com.rabbitmq.client.DeliverCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeoutException;

public class MainActivity extends AppCompatActivity implements SensorEventListener,View.OnClickListener {

    private static final int TIME_STAMP = 100;
    private static final String TAG = "MainActivity";

    private TextView mTextViewAccX;
    private TextView mTextViewAccY;
    private TextView mTextViewAccZ;

    private TextView mTextViewGyroX;
    private TextView mTextViewGyroY;
    private TextView mTextViewGyroZ;

    private TextView mTextViewLinAccX;
    private TextView mTextViewLinAccY;
    private TextView mTextViewLinAccZ;

    private TextView mTextViewFeedback;
    private TextView mTextViewStatus;

    private volatile Channel ch;
    private volatile Connection conn;
    private volatile Channel ch1;
    private volatile Connection conn1;

    private static List<Float> ax,ay,az;
    private static List<Float> gx,gy,gz;
    private static List<Float> lx,ly,lz;

    private static List<Float> dataSendToServer;

    private static List<Float> result;

    private volatile RabbitManager rabbitManager;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer, mGyroscope, mLinearAcceleration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ax=new ArrayList<>(); ay=new ArrayList<>(); az=new ArrayList<>();
        gx=new ArrayList<>(); gy=new ArrayList<>(); gz=new ArrayList<>();
        lx=new ArrayList<>(); ly=new ArrayList<>(); lz=new ArrayList<>();

        dataSendToServer = new ArrayList<>();

        mTextViewAccX = (TextView) findViewById(R.id.textView1acc);
        mTextViewAccY = (TextView) findViewById(R.id.textView2acc);
        mTextViewAccZ = (TextView) findViewById(R.id.textView3acc);

        mTextViewGyroX = (TextView) findViewById(R.id.textView1gyro);
        mTextViewGyroY = (TextView) findViewById(R.id.textView2gyro);
        mTextViewGyroZ = (TextView) findViewById(R.id.textView3gyro);

        mTextViewLinAccX = (TextView) findViewById(R.id.textView1linAcc);
        mTextViewLinAccY = (TextView) findViewById(R.id.textView2linAcc);
        mTextViewLinAccZ = (TextView) findViewById(R.id.textView3linAcc);

        mTextViewFeedback = (TextView) findViewById(R.id.textViewFeedback);
        mTextViewStatus = (TextView) findViewById(R.id.textViewStatus);

        mSensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);

        mAccelerometer=mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mLinearAcceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        mSensorManager.registerListener(this,mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this,mGyroscope, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this,mLinearAcceleration, SensorManager.SENSOR_DELAY_FASTEST);

        Button publishButton = (Button) findViewById(R.id.publish);
        Button stopPublishButton = (Button) findViewById(R.id.stopPublish);

        publishButton.setOnClickListener(this);
        stopPublishButton.setOnClickListener(this);

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;
        JSONObject jo = new JSONObject();
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mTextViewAccX.setText("acc X =" + (sensorEvent.values[0]));
            mTextViewAccY.setText("acc Y =" + (sensorEvent.values[1]));
            mTextViewAccZ.setText("acc Z =" + (sensorEvent.values[2]));

            ax.add(sensorEvent.values[0]);
            ay.add(sensorEvent.values[1]);
            az.add(sensorEvent.values[2]);
        }
        if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            mTextViewGyroX.setText("gyro X =" + (sensorEvent.values[0]));
            mTextViewGyroY.setText("gyro Y =" + (sensorEvent.values[1]));
            mTextViewGyroZ.setText("gyro Z =" + (sensorEvent.values[2]));

            gx.add(sensorEvent.values[0]);
            gy.add(sensorEvent.values[1]);
            gz.add(sensorEvent.values[2]);
        }
        if(sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
            mTextViewLinAccX.setText("linAcc X =" + (sensorEvent.values[0]));
            mTextViewLinAccY.setText("linAcc Y =" + (sensorEvent.values[1]));
            mTextViewLinAccZ.setText("linAcc Z =" + (sensorEvent.values[2]));

            lx.add(sensorEvent.values[0]);
            ly.add(sensorEvent.values[1]);
            lz.add(sensorEvent.values[2]);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Channel chAux = ch;
                    if (chAux != null) {
                        try {
                            dataSendToServer = sendDataToQueue();
                            if(dataSendToServer.size() != 0){
                                String dataSendToServerString = dataSendToServer.toString();
                                System.out.println(dataSendToServerString.length());
                                chAux.basicPublish("", "dataPhoneQueue", null, dataSendToServerString.getBytes(StandardCharsets.UTF_8));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
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
        mSensorManager.registerListener(this,mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this,mGyroscope, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this,mLinearAcceleration, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.publish:
                mTextViewStatus.setTextColor(Color.GRAY);
                mTextViewStatus.setText("recording...");
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
                mTextViewStatus.setTextColor(Color.GRAY);
                mTextViewStatus.setText("- result -");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                                mTextViewFeedback.setTextColor(Color.GREEN);
                                mTextViewFeedback.setText(message);
                            };
                            ch.basicConsume("dataPhoneQueue2", true, deliverCallback, consumerTag -> { });

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

    private float[] toFloatArray(List<Float> data) {
        int i=0;
        float[] array=new float[data.size()];
        for (Float f:data) {
            array[i++] = (f != null ? f: Float.NaN);
        }
        return array;
    }

    List<Float> sendDataToQueue(){
        dataSendToServer.clear();
        List<Float> dataList = new ArrayList<>();
        if (ax.size() >= TIME_STAMP && ay.size() >= TIME_STAMP && az.size() >= TIME_STAMP
                && gx.size() >= TIME_STAMP && gy.size() >= TIME_STAMP && gz.size() >= TIME_STAMP
                && lx.size() >= TIME_STAMP && ly.size() >= TIME_STAMP && lz.size() >= TIME_STAMP) {

            dataList.addAll(ax.subList(0,TIME_STAMP));
            dataList.addAll(ay.subList(0,TIME_STAMP));
            dataList.addAll(az.subList(0,TIME_STAMP));

            dataList.addAll(gx.subList(0,TIME_STAMP));
            dataList.addAll(gy.subList(0,TIME_STAMP));
            dataList.addAll(gz.subList(0,TIME_STAMP));

            dataList.addAll(lx.subList(0,TIME_STAMP));
            dataList.addAll(ly.subList(0,TIME_STAMP));
            dataList.addAll(lz.subList(0,TIME_STAMP));

            dataSendToServer = dataList;
            ax.clear(); ay.clear(); az.clear();
            gx.clear(); gy.clear(); gz.clear();
            lx.clear();ly.clear(); lz.clear();
        }
        return dataList;
    }
}
