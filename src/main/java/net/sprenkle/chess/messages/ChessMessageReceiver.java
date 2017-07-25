/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.messages;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author david
 */
public class ChessMessageReceiver {

    static Logger logger = Logger.getLogger(ChessMessageReceiver.class.getSimpleName());

    private final String EXCHANGE_NAME = "CHESS";
    private final String name;
    private final HashMap<String, MessageHandler> eventMap;
    private final String bindingKey;

    public ChessMessageReceiver(String name, boolean isRecievingImages) {
        this.name = name;
        eventMap = new HashMap<>();
        if (isRecievingImages) {
            // bindingKey = "#";
        } else {
            //  bindingKey = "*.none";
        }
        bindingKey = "#";
    }

    public void addMessageHandler(String messageType, MessageHandler messageHandler) {
        eventMap.put(messageType, messageHandler);
    }

    public void initialize() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("pi");
        factory.setPassword("ferret");
        factory.setHost("192.168.1.80");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, bindingKey);

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                    AMQP.BasicProperties properties, byte[] body) throws IOException {
                ChessMessageReceiver.this.handleDelivery(body);
            }
        };
        channel.basicConsume(queueName, true, consumer);
    }

    public void handleDelivery(byte[] body) throws IOException {
        try {
            MessageHolder mh = MessageHolder.fromBytes(body);
            if (mh.getClassName().equals(BoardImage.class.getName())) {
                logger.info("Received BoardImage");
            } else if (mh.getClassName().equals(KnownBoardPositions.class.getName())) {
                logger.info("Received KnownBoardPosition");
            } else {
                logger.info(String.format("%s received %s %s", name, mh.getClassName(), new String(body, "UTF-8")));
            }
            eventMap.get(mh.getClassName()).handleMessage(mh.getObject());
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ChessMessageReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
