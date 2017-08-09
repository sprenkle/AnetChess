/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess;

import net.sprenkle.chess.states.BoardReaderState;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sprenkle.chess.controllers.PiecePositionsIdentifier;
import net.sprenkle.chess.imaging.BoardCalculator;
import net.sprenkle.chess.imaging.ImageUtil;
import net.sprenkle.chess.messages.BoardImage;
import net.sprenkle.chess.messages.MessageHolder;
import net.sprenkle.chess.messages.NonQueueChessImageReceiver;
import net.sprenkle.chess.messages.NonQueueMessageTranRec;

/**
 *
 * @author david
 */
public class LoggerTester {

    NonQueueChessImageReceiver nonQueueChessImageReceiver;
    NonQueueMessageTranRec nonQueueMessageReceiver;
    BoardReader boardReader;
    Chess chess;
    RobotMover robotMover;
    BoardProcessor boardProcessor;
    HashSet<String> receivedMessages = new HashSet<>();

    public LoggerTester() {
        initialize();
    }

    public final void initialize() {
        try {
            nonQueueMessageReceiver = new NonQueueMessageTranRec();
            nonQueueChessImageReceiver = new NonQueueChessImageReceiver();

            boardReader = new BoardReader(new BoardReaderState(), nonQueueMessageReceiver, nonQueueMessageReceiver,
                    new BoardCalculator(new BoardProperties()), new PiecePositionsIdentifier(new BoardProperties()), nonQueueChessImageReceiver, new BoardProperties());
            chess = new Chess(new ChessController(), nonQueueMessageReceiver, nonQueueMessageReceiver);
            //robotMover = new RobotMover(new StockFishUCI(), nonQueueMessageReceiver, nonQueueMessageReceiver);
            //boardProcessor = new BoardProcessor(nonQueueMessageReceiver, nonQueueMessageReceiver, new BoardProperties(), new TestController());

        } catch (Exception ex) {
            Logger.getLogger(LoggerTester.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void process(String logFile, String component) {
        // reading file line by line in Java 8 
        try {
            Pattern pattern = Pattern.compile(".* (?:RMQChessMessageReceiver|RMQChesssImageReceiver)  - (\\w+) id=([\\w+-]+) received ([\\w.]+) (.*)");

            Files.lines(Paths.get(logFile)).forEach((String x) -> {
                try {
                    Matcher matcher = pattern.matcher(x);
                    if (matcher.matches()) {
                        String receiver = matcher.group(1);
                        String id = matcher.group(2);
                        String message = matcher.group(3);
                        String json = matcher.group(4);
                        if (receivedMessages.contains(id)) {
                            return;
                        }
                        System.out.println(x);
                        receivedMessages.add(id);
                        processRecieveMessage(x, receiver, message, json);
                    }
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            });
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void processRecieveMessage(String line, String receiver, String message, String json) {
        try {
            Gson gson = new GsonBuilder().create();

            MessageHolder messageHolder;

            switch (message) {
                case "net.sprenkle.chess.messages.GCode":
                case "net.sprenkle.chess.messages.ChessMoveMsg":
                case "net.sprenkle.chess.messages.RequestMove":
                case "net.sprenkle.chess.messages.RequestBoardStatus":
                case "net.sprenkle.chess.messages.BoardAtRest":
                case "net.sprenkle.chess.messages.StartGame":
                case "net.sprenkle.chess.messages.RequestPiecePositions":
                case "net.sprenkle.chess.messages.KnownBoardPositions":
                case "net.sprenkle.chess.messages.RequestImage":
                    messageHolder = (MessageHolder) gson.fromJson(json, MessageHolder.class);
                    nonQueueMessageReceiver.send(messageHolder);
                    break;
                case "net.sprenkle.chess.messages.BoardImage":
                    BufferedImage bi = ImageUtil.loadImage(String.format("D:\\git\\Chess\\images\\logger\\%s.png", json));
                    BoardImage board = new BoardImage(bi);
                    nonQueueChessImageReceiver.send(board);
                    break;
                default:
                    break;
            }

        } catch (JsonSyntaxException | IOException ex) {
            Logger.getLogger(LoggerTester.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        LoggerTester loggerTester = new LoggerTester();
        loggerTester.process("D:\\git\\Chess\\chess2.log", "BoardReader");
    }
}
