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
            if (eventMap.containsKey(mh.getClassName())) {
                switch (mh.getClassName()) {
                    case "StartGame":
                        logger.info(String.format("%s received %s", name, mh.getClassName()));
                        eventMap.get(mh.getClassName()).handleMessage((StartGame) mh.getObject(StartGame.class));
                        break;
                    case "ChessMoveMsg":
                        ChessMoveMsg chessMove = (ChessMoveMsg) mh.getObject(ChessMoveMsg.class);
                        logger.info(String.format("%s received %s", name, chessMove.toString()));
                        eventMap.get(mh.getClassName()).handleMessage(chessMove);
                        break;
                    case "RequestMove":
                        try {
                            RequestMove requestMove = (RequestMove) mh.getObject(RequestMove.class);
                            logger.info(String.format("%s received %s", name, requestMove.toString()));
                            eventMap.get(mh.getClassName()).handleMessage(requestMove);
                        } catch (Exception ex) {
                            logger.error(ex.getMessage());
                        }
                        break;
                    case "RequestBoardStatus":
                        logger.info(String.format("%s received %s", name, mh.getClassName()));
                        eventMap.get(mh.getClassName()).handleMessage((RequestBoardStatus) mh.getObject(RequestBoardStatus.class));
                        break;
                    case "BoardStatus":
                        BoardStatus boardStatus = (BoardStatus) mh.getObject(BoardStatus.class);
                        logger.info(String.format("%s received %s", name, boardStatus.toString()));
                        eventMap.get(mh.getClassName()).handleMessage(boardStatus);
                        break;
                    case "SetBoardRestPosition":
                        logger.info(String.format("%s received %s", name, mh.getClassName()));
                        eventMap.get(mh.getClassName()).handleMessage((SetBoardRestPosition) mh.getObject(SetBoardRestPosition.class));
                        break;
                    case "GCode":
                        logger.info(String.format("%s received %s", name, mh.getClassName()));
                        eventMap.get(mh.getClassName()).handleMessage((GCode) mh.getObject(GCode.class));
                        break;
                    case "RequestImage":
                        logger.info(String.format("%s received %s", name, mh.getClassName()));
                        eventMap.get(mh.getClassName()).handleMessage((RequestImage) mh.getObject(RequestImage.class));
                        break;
                    case "BoardImage":
                        logger.info(String.format("%s received %s", name, mh.getClassName()));
                        eventMap.get(mh.getClassName()).handleMessage((BoardImage) mh.getObject(BoardImage.class));
                        break;
                    case "BoardAtRest":
                        logger.info(String.format("%s received %s", name, mh.getClassName()));
                        eventMap.get(mh.getClassName()).handleMessage((BoardAtRest) mh.getObject(BoardAtRest.class));
                        break;
                    case "RequestMovePieces":
                        RequestMovePieces requestMovePieces = (RequestMovePieces) mh.getObject(RequestMovePieces.class);
                        logger.info(String.format("%s received %s", name, requestMovePieces));
                        eventMap.get(mh.getClassName()).handleMessage(requestMovePieces);
                        break;
                    case "ConfirmedPieceMove":
                        logger.info(String.format("%s received %s", name, mh.getClassName()));
                        eventMap.get(mh.getClassName()).handleMessage((ConfirmedPieceMove) mh.getObject(ConfirmedPieceMove.class));
                        break;
                    case "RequestPiecePositions":
                        RequestPiecePositions requestPiecePositions = (RequestPiecePositions) mh.getObject(RequestPiecePositions.class);
                        logger.info(String.format("%s received %s", name, requestPiecePositions));
                        eventMap.get(mh.getClassName()).handleMessage(requestPiecePositions);
                        break;
                    case "PiecePositions":
                        PiecePositions piecePositions = (PiecePositions) mh.getObject(PiecePositions.class);
                        logger.info(String.format("%s received %s", name, piecePositions));
                        eventMap.get(mh.getClassName()).handleMessage(piecePositions);
                        break;
                    case "KnownBoardPositions":
                        KnownBoardPositions knownBoardPositions = (KnownBoardPositions) mh.getObject(KnownBoardPositions.class);
                        logger.info(String.format("%s received %s", name, knownBoardPositions));
                        eventMap.get(mh.getClassName()).handleMessage(knownBoardPositions);
                        break;
                    default:
                        throw new Exception("Undefined Message");
                }
            }
        } catch (ClassNotFoundException ex) {
            logger.debug(ex.getMessage());
        } catch (Exception ex2) {
            logger.debug(ex2.getMessage());
        }
    }

//    public static void main(String[] args) throws Exception{
//         ObjectGraph objectGraph = ObjectGraph.create(new ChessModule());
//         ChessMessageReceiver chessMessageReceiver = objectGraph.get(ChessMessageReceiver.class);
//         chessMessageReceiver.initialize();
//    }
}
