package com.example.phonesensorserver.entities;


import com.example.phonesensorserver.DTOs.AccelerometerDataDTO;
import com.example.phonesensorserver.DTOs.GyroscopeDataDTO;
import com.example.phonesensorserver.DTOs.LinearaccDataDTO;
import com.example.phonesensorserver.services.AccelerometerDataService;
import com.example.phonesensorserver.services.ClientDataService;
import com.example.phonesensorserver.services.GyroscopeDataService;
import com.example.phonesensorserver.services.LinearaccDataService;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.tensorflow.Session;
import org.tensorflow.Tensor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Component
public class Consumer {
    private final ClientDataService clientDataService;
    private final AccelerometerDataService accelerometerDataService;
    private final GyroscopeDataService gyroscopeDataService;
    private final LinearaccDataService linearaccDataService;

    @Autowired
    public Consumer(ClientDataService clientDataService, AccelerometerDataService accelerometerDataService, GyroscopeDataService gyroscopeDataService, LinearaccDataService linearaccDataService) throws IOException {

        this.clientDataService = clientDataService;
        this.accelerometerDataService = accelerometerDataService;
        this.gyroscopeDataService = gyroscopeDataService;
        this.linearaccDataService = linearaccDataService;
    }

    @Scheduled(fixedRate = 1000)
    void listener() throws Exception {

        String url = System.getenv("CLOUDAMQP_URL");
        if(url == null){
            url = "amqps://qnovhajz:3Mqw-uz3laEhM3mGfI9a-XHPNztW76ln@goose.rmq2.cloudamqp.com/qnovhajz";
        }

        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(url);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare("dataPhoneQueue", true, false, false, null);
        System.out.print("zZz... ");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            try {
                // websocket send data to HAR processor
                try {

                    Socket socket = new Socket("localhost", 2004);

                    if(socket.isConnected()) {
                        if(message.length() > 3) {
                            if(!message.contains("null")){
                                DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
                                dataOut.writeUTF(message);
                                dataOut.flush();
                            }
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                System.out.println("<- " + message + "  " + message.length());

            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        channel.basicConsume("dataPhoneQueue", true, deliverCallback, consumerTag -> { });
        connection.close();
    }

    private static float[][] predict(Session sess, Tensor inputTensor) {
        Tensor result = sess.runner()
                .feed("input", inputTensor)
                .fetch("not_activated_output").run().get(0);
        float[][] outputBuffer = new float[1][3];
        result.copyTo(outputBuffer);
        return outputBuffer;
    }
}

