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
import javax.inject.Inject;
import net.sprenkle.chess.ChessInterface;
import net.sprenkle.messages.MessageHolder;


/**
 *
 * @author david
 */
public class ChessMessageReceiver {
    private static final String EXCHANGE_NAME = "CHESS";
    private final ChessInterface chess;
    
    @Inject
    public ChessMessageReceiver(ChessInterface chess) {
        this.chess = chess;
    }

    public void initialize() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, "");

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        Consumer consumer = new DefaultConsumer(channel) {
            public void handleDelivery(String consumerTag, Envelope envelope,
                    AMQP.BasicProperties properties, byte[] body) throws IOException {
                try {
                    MessageHolder mh = MessageHolder.fromBytes(body);
                    switch (mh.getClassName()) {
                        case "StartGame" :
                            chess.startGame((StartGame)mh.getObject(StartGame.class));
                            break;
                        case "ChessMove" :
                            chess.chessMoved((net.sprenkle.chess.messages.ChessMove)mh.getObject(ChessMove.class));
                            break;
                        case "RequestMove" :
                            RequestMove rm = (net.sprenkle.chess.messages.RequestMove)mh.getObject(RequestMove.class);
                            chess.requestMove(rm);
                            break;
                        default:
                            throw new Exception("Undefined Message");
                    }
                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        channel.basicConsume(queueName, true, consumer);
    }

    
//    public static void main(String[] args) throws Exception{
//         ObjectGraph objectGraph = ObjectGraph.create(new ChessModule());
//         ChessMessageReceiver chessMessageReceiver = objectGraph.get(ChessMessageReceiver.class);
//         chessMessageReceiver.initialize();
//    }
}
