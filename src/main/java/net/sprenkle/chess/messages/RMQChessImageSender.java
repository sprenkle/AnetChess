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
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.concurrent.TimeoutException;
import net.sprenkle.chess.RabbitConfiguration;
import net.sprenkle.chess.imaging.ImageUtil;

/**
 *
 * @author david
 */
public class RMQChessImageSender implements ChessImageSender {

    static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(RMQChessMessageSender.class.getSimpleName());
    private static final String EXCHANGE_NAME = "CHESSIMAGE";
    private ConnectionFactory factory;
    Connection connection;
    Channel channel;

    public RMQChessImageSender(String name, RabbitConfigurationInterface configuration) {
        try {
            factory = new ConnectionFactory();
            factory.setUsername(configuration.getUser());
            factory.setPassword(configuration.getPassword());
            factory.setHost(configuration.getServer());
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
    
    public static void main(String[] arg) throws IOException{
        BufferedImage bi = ImageUtil.loadImage("D:\\git\\Chess\\images\\board1cec39d9-2820-48f1-963b-acb605ff2f2e.png");
        RMQChessImageSender is = new RMQChessImageSender("this", new RabbitConfiguration());
        is.send(new BoardImage(bi));
    }
}
