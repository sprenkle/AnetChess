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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.logging.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author david
 */
public class RMQChesssImageReceiver {

    static Logger logger = Logger.getLogger(ChessMessageReceiver.class.getSimpleName());

    private final String EXCHANGE_NAME = "CHESSIMAGE";
    private final String name;
    private MessageHandler messageHandler;

    public RMQChesssImageReceiver(String name) {
        this.name = name;
    }

    public void add(MessageHandler messageHandler){
        this.messageHandler = messageHandler;
    }
    
    public void initialize() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("pi");
        factory.setPassword("ferret");
        factory.setHost("192.168.1.80");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, "");

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                    AMQP.BasicProperties properties, byte[] body) throws IOException {
                RMQChesssImageReceiver.this.handleDelivery(body);
            }
        };
        channel.basicConsume(queueName, true, consumer);
    }

    public void handleDelivery(byte[] body) throws IOException {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(body);
            ObjectInput in = null;
            try {
                in = new ObjectInputStream(bis);
                BoardImage boardImage = (BoardImage)in.readObject();
                logger.info(String.format("%s Received BoardImage %s",name, boardImage.getUuid()));
                messageHandler.handleMessage(boardImage);
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException ex) {
                    // ignore close exception
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ChessMessageReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
