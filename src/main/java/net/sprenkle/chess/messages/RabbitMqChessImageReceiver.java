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
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

/**
 *
 * @author david
 */
public class RabbitMqChessImageReceiver implements ChesssImageReceiver {

    private BufferedImage bi;
    private static final String EXCHANGE_NAME = "images";
    private static final String IMAGE_UPDATE = "images_update";
    private Connection connection;
    private Channel sendChannel;
    ChessImageListenerInterface listener;
    
    
    public void initialize() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.1.90");
        connection = factory.newConnection();

        sendChannel = connection.createChannel();
        sendChannel.exchangeDeclare(IMAGE_UPDATE, BuiltinExchangeType.FANOUT);
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, "");

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                    AMQP.BasicProperties properties, byte[] body) throws IOException {
                InputStream in = new ByteArrayInputStream(body);
                BufferedImage bImageFromConvert = ImageIO.read(in);
                if(listener != null) listener.receivedImage(bi);
            }
        };
        channel.basicConsume(queueName, true, consumer);
    }
   
    @Override
    public void setListener(ChessImageListenerInterface listener){
        this.listener = listener;
    }
}
