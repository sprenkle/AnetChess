/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.messages;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author david
 */
public class RMQChessMessageSender implements ChessMessageSender {
    static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(RMQChessMessageSender.class.getSimpleName());

    private static final String EXCHANGE_NAME = "CHESS";
    private final String name;
    private ConnectionFactory factory;
    Connection connection;
    Channel channel;

    public RMQChessMessageSender(String name, RabbitConfigurationInterface configuration) {
        this.name = name;
        try {
            factory = new ConnectionFactory();
            factory.setUsername(configuration.getUser());
            factory.setPassword(configuration.getPassword());
            factory.setHost(configuration.getServer());
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        } catch (IOException | TimeoutException ex) {
            Logger.getLogger(RMQChessMessageSender.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void send(MessageHolder messageHolder) {
        try {
            String routingKey = String.format("%s.%s", "BufferedImage".equals(messageHolder.getClassName()) ? "none" : "general", "BufferedImage".equals(messageHolder.getClassName()) ? "image" : "none");
            if(messageHolder.getClassName().equals(BoardImage.class.getName())){
                logger.info(String.format("%s sends %s  %s", name, messageHolder.getClassName(), "BufferedImage"));
            }else{
                logger.info(String.format("%s sends %s  %s", name, messageHolder.getClassName(), messageHolder.toString()));
            }
            channel.basicPublish(EXCHANGE_NAME, routingKey, null, messageHolder.toBytes());

        } catch (IOException ex) {
            Logger.getLogger(RMQChessMessageSender.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
