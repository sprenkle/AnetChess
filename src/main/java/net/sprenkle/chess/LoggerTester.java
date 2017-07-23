/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess;

import net.sprenkle.chess.imaging.ImageUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sprenkle.chess.controllers.PiecePositionsIdentifier;
import net.sprenkle.chess.imaging.BoardCalculator;
import net.sprenkle.chess.messages.BoardAtRest;
import net.sprenkle.chess.messages.BoardImage;
import net.sprenkle.chess.messages.ChessMessageReceiver;
import net.sprenkle.chess.messages.KnownBoardPositions;
import net.sprenkle.chess.messages.MqChessMessageSender;
import net.sprenkle.chess.messages.RequestBoardStatus;
import net.sprenkle.chess.messages.RequestMove;
import net.sprenkle.chess.messages.RequestPiecePositions;
import net.sprenkle.chess.messages.StartGame;

/**
 *
 * @author david
 */
public class LoggerTester {

    public void process(String logFile, String component) {
        // reading file line by line in Java 8 
        System.out.println("Reading file line by line using Files.lines() in Java 8");
        try {
            Pattern pattern = Pattern.compile(".* ChessMessageReceiver  - BoardReader received (\\w+) (.*)");
            BoardReader boardReader = new BoardReader(new BoardReaderState(), new MqChessMessageSender("boardReader"), new ChessMessageReceiver("BoardReader", true),
                    new BoardCalculator(new BoardProperties()), new PiecePositionsIdentifier(new BoardProperties()));

            Files.lines(Paths.get(logFile)).forEach((String x) -> {
                try {
                    Matcher matcher = pattern.matcher(x);
                    if (matcher.matches()) {
                        String comp = matcher.group(1);
                        String json = matcher.group(2);
                        
                        Gson gson = new GsonBuilder().create();
                        
                        switch (comp) {
                            case "RequestMove":
                                RequestMove rm = gson.fromJson(json, RequestMove.class);
                                boardReader.requestMove(rm);
                                break;
                            case "RequestBoardStatus":
                                RequestBoardStatus rbs = gson.fromJson(json, RequestBoardStatus.class);
                                boardReader.requestBoardStatus(rbs);
                                break;
                            case "BoardImage":
                                BufferedImage bi = ImageUtil.loadImage(String.format("D:\\git\\Chess\\images\\logger\\%s.png", json));
                                BoardImage board = new BoardImage(bi);
                                boardReader.boardImage(board);
                                break;
                            case "BoardAtRest":
                                BoardAtRest bar = gson.fromJson(json, BoardAtRest.class);
                                //boardReader.boardAtRest(bar);
                                break;
                            case "StartGame":
                                StartGame sg = gson.fromJson(json, StartGame.class);
                                boardReader.startGame(sg);
                                break;
                            case "RequestPiecePositions":
                                RequestPiecePositions rpp = gson.fromJson(json, RequestPiecePositions.class);
                                boardReader.requestPiecePositions(rpp);
                                break;
                            case "KnownBoardPositions":
                                KnownBoardPositions kbp = gson.fromJson(json, KnownBoardPositions.class);
                                boardReader.knownBoardPositions(kbp);
                                break;
                        }
                    }
                } catch (Exception ex) {
                    Logger.getLogger(LoggerTester.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        } catch (Exception ex) {
            Logger.getLogger(LoggerTester.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        LoggerTester loggerTester = new LoggerTester();
        loggerTester.process("D:\\git\\Chess\\chess.log", "BoardReader");
    }
}
