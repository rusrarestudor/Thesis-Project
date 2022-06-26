package com.example.phonesensorreader;

import static java.util.Collections.emptyMap;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeoutException;

public class RabbitManager {

    private Thread subscribeThread;
    private Thread publishThread;

    private volatile Channel ch;
    private volatile Connection conn;
    private volatile Channel ch1;
    private volatile Connection conn1;

    private String dataToSend;

    public RabbitManager() throws IOException, TimeoutException, URISyntaxException, NoSuchAlgorithmException, KeyManagementException {}

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
                                    if(dataToSend.length() != 0){
                                        String dataSendToServerString = dataToSend;
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
