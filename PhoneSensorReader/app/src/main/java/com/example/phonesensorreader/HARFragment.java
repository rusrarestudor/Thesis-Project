package com.example.phonesensorreader;

import static java.util.Collections.emptyMap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HARFragment extends AppCompatActivity implements SensorEventListener, View.OnClickListener {

    private static final int TIME_STAMP = 100;
    private static final String TAG = "HARActivity";

    private TextView mTextViewFeedback;
    private TextView mTextViewStatus;

    private volatile Channel ch;
    private volatile Connection conn;
    private volatile Channel ch1;
    private volatile Connection conn1;

    private static List<Float> ax,ay,az;
/*    private static List<Float> gx,gy,gz;
    private static List<Float> lx,ly,lz;*/

    private static List<String> finalPrediction;

    private static List<Float> dataSendToServer;

    private static List<Float> result;

    private volatile RabbitManager rabbitManager;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer, mGyroscope, mLinearAcceleration;

    Button goToMain;

    Thread subscribeThread;
    Thread publishThread;

    List<String> lastRecived;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_har);


        ax=new ArrayList<>(); ay=new ArrayList<>(); az=new ArrayList<>();
/*        gx=new ArrayList<>(); gy=new ArrayList<>(); gz=new ArrayList<>();
        lx=new ArrayList<>(); ly=new ArrayList<>(); lz=new ArrayList<>();*/

        finalPrediction = new ArrayList<>();

        dataSendToServer = new ArrayList<>();

        lastRecived = new ArrayList<>();

        mTextViewFeedback = (TextView) findViewById(R.id.textViewFeedback);
        mTextViewStatus = (TextView) findViewById(R.id.textViewStatus);

        mSensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);

        mAccelerometer=mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
/*        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mLinearAcceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);*/

        mSensorManager.registerListener(this,mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
/*        mSensorManager.registerListener(this,mGyroscope, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this,mLinearAcceleration, SensorManager.SENSOR_DELAY_FASTEST);*/

        Button publishButton = (Button) findViewById(R.id.publish);
        Button stopPublishButton = (Button) findViewById(R.id.stopPublish);

        publishButton.setOnClickListener(this);
        stopPublishButton.setOnClickListener(this);

        goToMain = findViewById(R.id.backButton);
        goToMain.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(HARFragment.this,MainActivity.class);
                        startActivity(i);
                    }
                }
        );

       /* @SuppressLint("HandlerLeak") final Handler incomingMessageHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String message = msg.getData().getString("msg");
                if(message != null){
                    lastRecived.add(message);
                }
                mTextViewStatus.setText("Current Activity: " + message);

            }
        };
        subscribe(incomingMessageHandler);*/

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;
        JSONObject jo = new JSONObject();
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            ax.add(sensorEvent.values[0]);
            ay.add(sensorEvent.values[1]);
            az.add(sensorEvent.values[2]);
        }
/*        if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gx.add(sensorEvent.values[0]);
            gy.add(sensorEvent.values[1]);
            gz.add(sensorEvent.values[2]);
        }
        if(sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
            lx.add(sensorEvent.values[0]);
            ly.add(sensorEvent.values[1]);
            lz.add(sensorEvent.values[2]);
        }*/

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) { }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this,mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
/*        mSensorManager.registerListener(this,mGyroscope, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this,mLinearAcceleration, SensorManager.SENSOR_DELAY_FASTEST);*/
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
                publishToAMQP();

                break;
            case R.id.stopPublish:
                System.out.println("    - STOP button pressed -");
                mTextViewStatus.setText("");
                publishThread.interrupt();
                subscribeThread.interrupt();

                conn = null;
                ch = null;

                if(lastRecived.size() != 0){
                    mTextViewFeedback.setText(mostFreq(lastRecived));
                }

                mTextViewStatus.setText("Current Activity: ");

                break;

            case R.id.backButton:
                Intent i = new Intent(HARFragment.this,MainActivity.class);
                startActivity(i);

        }
    }

    List<Float> sendDataToQueue(){
        dataSendToServer.clear();
        List<Float> dataList = new ArrayList<>();
        if (ax.size() >= TIME_STAMP && ay.size() >= TIME_STAMP && az.size() >= TIME_STAMP){
//                && gx.size() >= TIME_STAMP && gy.size() >= TIME_STAMP && gz.size() >= TIME_STAMP
//                && lx.size() >= TIME_STAMP && ly.size() >= TIME_STAMP && lz.size() >= TIME_STAMP) {

            dataList.addAll(ax.subList(0,TIME_STAMP));
            dataList.addAll(ay.subList(0,TIME_STAMP));
            dataList.addAll(az.subList(0,TIME_STAMP));

/*            dataList.addAll(gx.subList(0,TIME_STAMP));
            dataList.addAll(gy.subList(0,TIME_STAMP));
            dataList.addAll(gz.subList(0,TIME_STAMP));

            dataList.addAll(lx.subList(0,TIME_STAMP));
            dataList.addAll(ly.subList(0,TIME_STAMP));
            dataList.addAll(lz.subList(0,TIME_STAMP));*/

            dataSendToServer = dataList;
            ax.clear(); ay.clear(); az.clear();
/*            gx.clear(); gy.clear(); gz.clear();
            lx.clear();ly.clear(); lz.clear();*/
        }
        return dataList;
    }

    String mostFreq(List<String> list){

        Map<String, Integer> map = new HashMap<>();

        for (String t : list) {
            Integer val = map.get(t);
            map.put(t, val == null ? 1 : val + 1);
        }

        Map.Entry<String, Integer> max = null;

        for (Map.Entry<String, Integer> e : map.entrySet()) {
            if (max == null || e.getValue() > max.getValue())
                max = e;
        }

        return max.getKey();

    }

    public void publishToAMQP()
    {
        publishThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        System.out.println("   - START button pressed -");
                        ConnectionFactory factory = new ConnectionFactory();
                        factory.setUri("amqps://qnovhajz:3Mqw-uz3laEhM3mGfI9a-XHPNztW76ln@goose.rmq2.cloudamqp.com/qnovhajz");
                        Connection conAux = factory.newConnection();
                        conn = conAux;
                        Channel chAux = conAux.createChannel();
                        ch = chAux;

                        while (true) {
                            if (chAux != null) {
                                try {

                                    dataSendToServer = sendDataToQueue();
                                    if(dataSendToServer.size() != 0){
                                        String dataSendToServerString = dataSendToServer.toString();
                                        chAux.basicPublish("", "dataPhoneQueue", null, dataSendToServerString.getBytes(StandardCharsets.UTF_8));
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            if (publishThread.interrupted()) {
                                return;
                            }
                        }
                    } catch (IOException | TimeoutException | URISyntaxException | NoSuchAlgorithmException | KeyManagementException e) {
                        break;
                    }
                }
            }
        });
        publishThread.start();
    }

    void subscribe(final Handler handler)
    {
        subscribeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        String url = System.getenv("CLOUDAMQP_URL");
                        if(url == null){
                            url = "amqps://qnovhajz:3Mqw-uz3laEhM3mGfI9a-XHPNztW76ln@goose.rmq2.cloudamqp.com/qnovhajz";
                        }

                        ConnectionFactory factory = new ConnectionFactory();
                        factory.setUri(url);
                        Connection connection = factory.newConnection();
                        Channel channel = connection.createChannel();

                        while (true) {
                            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                                System.out.println("===============> " + message + "<================");
                                try {
                                    System.out.println("===============> " + message + " <================");

                                    Message msg = handler.obtainMessage();
                                    Bundle bundle = new Bundle();

                                    bundle.putString("msg", message);
                                    msg.setData(bundle);
                                    handler.sendMessage(msg);

                                    if (subscribeThread.interrupted()) {
                                        return;
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            };
                            channel.basicConsume("dataPhoneQueue2", true, deliverCallback, consumerTag -> { });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        subscribeThread.start();
    }

}

