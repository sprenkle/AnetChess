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
import net.sprenkle.messages.MessageHolder;

/**
 *
 * @author david
 */
public class MqChessMessageSender implements ChessMessageSender {

    private static final String EXCHANGE_NAME = "CHESS";
    private ConnectionFactory factory;
    Connection connection;
    Channel channel;

    public MqChessMessageSender()  {
        try {
            factory = new ConnectionFactory();
            factory.setUsername("guest");
            factory.setPassword("guest");
            factory.setHost("localhost");
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
        } catch (IOException ex) {
            Logger.getLogger(MqChessMessageSender.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TimeoutException ex) {
            Logger.getLogger(MqChessMessageSender.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void send(MessageHolder messageHolder) {
        try {
            channel.basicPublish(EXCHANGE_NAME, "", null, messageHolder.toBytes());

        } catch (IOException ex) {
            Logger.getLogger(MqChessMessageSender.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
