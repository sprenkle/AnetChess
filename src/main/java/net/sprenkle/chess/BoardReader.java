/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess;

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
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import net.sprenkle.chess.imaging.BoardCalculator;
import net.sprenkle.chess.messages.BoardStatus;
import net.sprenkle.chess.messages.ChessImageListenerInterface;
import net.sprenkle.chess.messages.ChessMessageReceiver;
import net.sprenkle.chess.messages.MessageHandler;
import net.sprenkle.chess.messages.MqChessMessageSender;
import net.sprenkle.chess.messages.RabbitMqChessImageReceiver;
import net.sprenkle.chess.messages.RequestBoardStatus;
import net.sprenkle.chess.messages.RequestMove;
import net.sprenkle.chess.messages.SetBoardRestPosition;
import net.sprenkle.chess.messages.ChessMove;
import net.sprenkle.messages.MessageHolder;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author david
 */
public class BoardReader implements ChessImageListenerInterface {

    static Logger logger = Logger.getLogger(BoardReader.class.getSimpleName());

    private static final String EXCHANGE_NAME = "images";
    private static final String IMAGE_UPDATE = "images_update";
    private final Connection connection;
    private final Channel sendChannel;

    private final MqChessMessageSender messageSender;
    private final BoardCalculator boardCalculator;

    private final String CHECK_FOR_GAME_SETUP = "checkForGameSetup";
    private final String CHECK_FOR_HUMAN_MOVE = "checkForHumanMove";
    private final String CHECK_FOR_REST_POSITION = "checkForRestPosition";
    private final String NONE = "none";
    private String state;

    public BoardReader(MqChessMessageSender messageSender, RabbitMqChessImageReceiver imageReceiver, ChessMessageReceiver messageReceiver, BoardCalculator boardCalculator) throws Exception {
        this.messageSender = messageSender;
        this.boardCalculator = boardCalculator;

        messageReceiver.addMessageHandler(RequestMove.class.getSimpleName(), new MessageHandler<RequestMove>() {
            @Override
            public void handleMessage(RequestMove requestMove) {
                try {
                    requestMove(requestMove);
                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(BoardReader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        messageReceiver.addMessageHandler(RequestBoardStatus.class.getSimpleName(), new MessageHandler<RequestBoardStatus>() {
            @Override
            public void handleMessage(RequestBoardStatus requestBoardStatus) {
                try {
                    requestBoardStatus(requestBoardStatus);
                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(BoardReader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        messageReceiver.addMessageHandler(BoardStatus.class.getSimpleName(), new MessageHandler<BoardStatus>() {
            @Override
            public void handleMessage(BoardStatus boardStatus) {
                try {
                    boardStatus(boardStatus);
                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(BoardReader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        messageReceiver.initialize();
        
        imageReceiver.setListener(this);
        state = NONE;

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.1.90");
        connection = factory.newConnection();

        sendChannel = connection.createChannel();
        sendChannel.exchangeDeclare(IMAGE_UPDATE, BuiltinExchangeType.FANOUT);
        startListening();

    }

    private void startListening() throws IOException, TimeoutException {
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, "");

        logger.debug(" [*] Waiting for messages. To exit press CTRL+C");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                    AMQP.BasicProperties properties, byte[] body) throws IOException {
                InputStream in = new ByteArrayInputStream(body);
                BufferedImage bImageFromConvert = ImageIO.read(in);
                //logger.debug("Received Image");
                if (state.equals(CHECK_FOR_GAME_SETUP)) {
                    boardCalculator.setInitialized(false);
                    boardCalculator.initialLines(bImageFromConvert);
                    if (boardCalculator.isInitialized()) {
                        BoardStatus boardStatus = new BoardStatus(true, true, true);
                        logger.debug(String.format("Sent %s", boardStatus.toString()));
                        messageSender.send(new MessageHolder(BoardStatus.class.getSimpleName(), boardStatus));
                        state = NONE;
                    }
                } else if (state.equals(CHECK_FOR_HUMAN_MOVE)) {
                    int[] move = boardCalculator.detectPieces(bImageFromConvert);
                    if (move != null) {
                        logger.debug("Found White move");
                        state = NONE;
                        messageSender.send(new MessageHolder(ChessMove.class.getSimpleName(), new ChessMove(requestedMove.getTurn(), convertToMove(move), requestedMove.getMoveId(), false)));
                        
                    }
                }
            }

        };
        channel.basicConsume(queueName, true, consumer);
    }

    public static void main(String[] arg) throws Exception {
        PropertyConfigurator.configure("D:\\git\\Chess\\src\\main\\java\\log4j.properties");
        new BoardReader(new MqChessMessageSender("boardReader"), new RabbitMqChessImageReceiver(), new ChessMessageReceiver("BoardReader", true), new BoardCalculator());
    }

    private RequestMove requestedMove;
    public void requestMove(RequestMove requestMove) throws Exception {
        if (!requestMove.isRobot()) {
            requestedMove = requestMove;
            state = CHECK_FOR_HUMAN_MOVE;
            logger.debug(String.format("Set state to %s", state.toString()));
        }
    }

    public void receivedImage(BufferedImage bi) { // TODO Is this really needed?
        if (state == CHECK_FOR_GAME_SETUP) {

        }
    }

    public void requestBoardStatus(RequestBoardStatus requestBoardStatus) throws Exception {
        state = CHECK_FOR_REST_POSITION;
        messageSender.send(new MessageHolder(SetBoardRestPosition.class.getSimpleName(), new SetBoardRestPosition()));
    }

    public void boardStatus(BoardStatus boardStatus) throws Exception {
        if (state.equals(CHECK_FOR_REST_POSITION) && boardStatus.isIsRestPosition()) {
            state = CHECK_FOR_GAME_SETUP;
        }
    }

    
    
    private String convertToMove(int[] move){
        
        return String.format("%s%s%s%s", convertAlpha(move[0]), move[1] + 1 , convertAlpha(move[2]), move[3] + 1);
    }
    
    private String convertAlpha(int value){
        switch(value){
            case 0: return "h";
            case 1: return "g";
            case 2: return "f";
            case 3: return "e";
            case 4: return "d";
            case 5: return "c";
            case 6: return "b";
            case 7: return "a";
        }
        return "Z";
       // --throw new Exception("not valid alpha");
    }
}
