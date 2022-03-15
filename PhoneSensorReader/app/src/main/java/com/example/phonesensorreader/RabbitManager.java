package com.example.phonesensorreader;

import static java.util.Collections.emptyMap;

import android.util.Log;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeoutException;

public class RabbitManager {

    private ConnectionFactory factory;
    private Connection con;
    private Channel ch;


    public RabbitManager() throws IOException, TimeoutException, URISyntaxException, NoSuchAlgorithmException, KeyManagementException {

    }

    public ConnectionFactory getFactory() { return factory; }

    public void setFactory(ConnectionFactory factory) { this.factory = factory; }

    public Connection getCon() { return con; }

    public void setCon(Connection con) { this.con = con; }

    public Channel getCh() { return ch; }

    public void setCh(Channel ch) { this.ch = ch; }


    public RabbitManager creteAMQP() throws IOException, URISyntaxException, NoSuchAlgorithmException, KeyManagementException, TimeoutException {

        RabbitManager rabbitManager = new RabbitManager();

        this.factory = new ConnectionFactory();
        factory.setUri("amqps://qnovhajz:3Mqw-uz3laEhM3mGfI9a-XHPNztW76ln@goose.rmq2.cloudamqp.com/qnovhajz");
        this.con = factory.newConnection();
        this.ch = con.createChannel();

        Connection conAux = rabbitManager.getFactory().newConnection();
        rabbitManager.setCon(conAux);

        Channel chAux = conAux.createChannel();
        chAux.queueDeclare("dataPhoneQueue", false, false, false, emptyMap());
        rabbitManager.setCh(chAux);

        return rabbitManager;
    }

}
