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
import java.io.IOException;
import java.util.HashMap;
import net.sprenkle.messages.MessageHolder;
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
        eventMap = new HashMap<String, MessageHandler>();
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
        factory.setHost("192.168.1.88");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, bindingKey);

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        Consumer consumer = new DefaultConsumer(channel) {
            public void handleDelivery(String consumerTag, Envelope envelope,
                    AMQP.BasicProperties properties, byte[] body) throws IOException {
                try {
                    MessageHolder mh = MessageHolder.fromBytes(body);
                    if (eventMap.containsKey(mh.getClassName())) {
                        logger.debug(String.format("%s received %s", name, mh.getClassName()));
                        switch (mh.getClassName()) {
                            case "StartGame":
                                eventMap.get(mh.getClassName()).handleMessage((StartGame) mh.getObject(StartGame.class));
                                break;
                            case "ChessMove":
                                eventMap.get(mh.getClassName()).handleMessage((ChessMove) mh.getObject(ChessMove.class));
                                break;
                            case "RequestMove":
                                eventMap.get(mh.getClassName()).handleMessage((RequestMove) mh.getObject(RequestMove.class));
                                break;
                            case "RequestBoardStatus":
                                eventMap.get(mh.getClassName()).handleMessage((RequestBoardStatus) mh.getObject(RequestBoardStatus.class));
                                break;
                            case "BoardStatus":
                                eventMap.get(mh.getClassName()).handleMessage((BoardStatus) mh.getObject(BoardStatus.class));
                                break;
                            case "SetBoardRestPosition":
                                eventMap.get(mh.getClassName()).handleMessage((SetBoardRestPosition) mh.getObject(SetBoardRestPosition.class));
                                break;
                            case "GCode":
                                eventMap.get(mh.getClassName()).handleMessage((GCode) mh.getObject(GCode.class));
                                break;
                            case "RequestImage":
                                eventMap.get(mh.getClassName()).handleMessage((RequestImage) mh.getObject(RequestImage.class));
                                break;
                            case "BoardImage":
                                eventMap.get(mh.getClassName()).handleMessage((BoardImage) mh.getObject(BoardImage.class));
                                break;
                            case "BoardAtRest":
                                eventMap.get(mh.getClassName()).handleMessage((BoardAtRest) mh.getObject(BoardAtRest.class));
                                break;
                            case "RequestMovePieces":
                                eventMap.get(mh.getClassName()).handleMessage((RequestMovePieces) mh.getObject(RequestMovePieces.class));
                                break;
                            case "ConfirmedPieceMove":
                                eventMap.get(mh.getClassName()).handleMessage((ConfirmedPieceMove) mh.getObject(ConfirmedPieceMove.class));
                                break;
                            case "RequestPiecePositions":
                                eventMap.get(mh.getClassName()).handleMessage((RequestPiecePositions) mh.getObject(RequestPiecePositions.class));
                                break;
                            case "PiecePositions":
                                eventMap.get(mh.getClassName()).handleMessage((PiecePositions) mh.getObject(PiecePositions.class));
                                break;
                            default:
                                throw new Exception("Undefined Message");
                        }
                    }
                } catch (ClassNotFoundException ex ) {
                    logger.debug(ex.getMessage());
                } catch (Exception ex2) {
                    logger.debug(ex2.getMessage());
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
