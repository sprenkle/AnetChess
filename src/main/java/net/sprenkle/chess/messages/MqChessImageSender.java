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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author david
 */
public class MqChessImageSender implements ChessImageSender {

    static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(MqChessMessageSender.class.getSimpleName());
    private static final String EXCHANGE_NAME = "CHESSIMAGE";
    private ConnectionFactory factory;
    Connection connection;
    Channel channel;

    public MqChessImageSender(String name) {
        try {
            factory = new ConnectionFactory();
            factory.setUsername("pi");
            factory.setPassword("ferret");
            factory.setHost("192.168.1.80");
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
        } catch (IOException | TimeoutException ex) {
            logger.error(ex);
        }

    }

    @Override
    public void send(BoardImage boardImage) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(boardImage);
            out.flush();
            byte[] bytes = bos.toByteArray();
            channel.basicPublish(EXCHANGE_NAME, "", null, bytes);
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                logger.error(ex.getMessage());
            }
        }
    }
}
