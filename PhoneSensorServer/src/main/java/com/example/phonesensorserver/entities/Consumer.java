package com.example.phonesensorserver.entities;


import com.example.phonesensorserver.DTOs.ClientDataDTO;
import com.example.phonesensorserver.services.ClientDataService;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Component
public class Consumer {

    private final ClientDataService clientDataService;

    @Autowired
    public Consumer(ClientDataService clientDataService) {

        this.clientDataService = clientDataService;
    }

    @Scheduled(fixedRate = 10000)
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
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);

            JSONParser parser = new JSONParser();
            try {
                JSONObject json = (JSONObject) parser.parse(message);

                LocalDateTime dateTime = LocalDateTime.now();

                float accX = 0, accY = 0, accZ = 0;
                accX = Float.parseFloat(json.getAsString("X:"));
                accY = Float.parseFloat(json.getAsString("Y:"));
                accZ = Float.parseFloat(json.getAsString("Z:"));
                int weight = 0, height = 0;
                weight = 0;
                height = 0;
                String exeType = "";
                exeType = "";

                //ClientDataDTO clientDataDTO = new ClientDataDTO(accX, accY, accZ, dateTime, weight, height, exeType);

                System.out.println("<- " + message);


            } catch (ParseException e) {
                e.printStackTrace();
            }
        };

        channel.basicConsume("dataPhoneQueue", true, deliverCallback, consumerTag -> { });
        connection.close();
    }
}

