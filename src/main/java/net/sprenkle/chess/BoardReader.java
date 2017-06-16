/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess;

import java.awt.image.BufferedImage;
import java.util.logging.Level;
import net.sprenkle.chess.imaging.BoardCalculator;
import net.sprenkle.chess.messages.BoardAtRest;
import net.sprenkle.chess.messages.BoardImage;
import net.sprenkle.chess.messages.BoardStatus;
import net.sprenkle.chess.messages.ChessMessageReceiver;
import net.sprenkle.chess.messages.MessageHandler;
import net.sprenkle.chess.messages.MqChessMessageSender;
import net.sprenkle.chess.messages.RabbitMqChessImageReceiver;
import net.sprenkle.chess.messages.RequestBoardStatus;
import net.sprenkle.chess.messages.RequestMove;
import net.sprenkle.chess.messages.SetBoardRestPosition;
import net.sprenkle.chess.messages.ChessMove;
import net.sprenkle.chess.messages.PiecePositions;
import net.sprenkle.chess.messages.RequestPiecePositions;
import net.sprenkle.chess.messages.StartGame;
import net.sprenkle.messages.MessageHolder;
import net.sprenkle.messages.images.RequestImage;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author david
 */
public class BoardReader {
    
    static Logger logger = Logger.getLogger(BoardReader.class.getSimpleName());
    
    private final MqChessMessageSender messageSender;
    private final BoardCalculator boardCalculator;
    
    private final String CHECK_FOR_GAME_SETUP = "checkForGameSetup";
    private final String CHECK_FOR_HUMAN_MOVE = "checkForHumanMove";
    private final String CHECK_FOR_REST_POSITION = "checkForRestPosition";
    private final String CHECK_FOR_PIECE_POSITIONS = "checkForPiecePositions";
    
    private final String NONE = "none";
    private String state;
    static double xSlope = -0.4262;
    static double ySlope = 0.4271;
    static double xIntercept = 175.376;
    static double yIntercept = -165.4933;
    static double orgX = 94.3;
    static double orgY = 170.4;
    private RequestPiecePositions requestPiecePositions;
    
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
        
        messageReceiver.addMessageHandler(BoardImage.class.getSimpleName(), new MessageHandler<BoardImage>() {
            @Override
            public void handleMessage(BoardImage boardImage) {
                try {
                    boardImage(boardImage);
                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(BoardReader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        messageReceiver.addMessageHandler(BoardAtRest.class.getSimpleName(), new MessageHandler<BoardAtRest>() {
            @Override
            public void handleMessage(BoardAtRest boardAtRest) {
                try {
                    boardAtRest(boardAtRest);
                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(BoardReader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        messageReceiver.addMessageHandler(StartGame.class.getSimpleName(), new MessageHandler<StartGame>() {
            @Override
            public void handleMessage(StartGame startGame) {
                try {
                    startGame(startGame);
                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(BoardReader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        messageReceiver.addMessageHandler(RequestPiecePositions.class.getSimpleName(), new MessageHandler<RequestPiecePositions>() {
            @Override
            public void handleMessage(RequestPiecePositions requestPiecePositions) {
                try {
                    requestPiecePositions(requestPiecePositions);
                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(BoardReader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        messageReceiver.addMessageHandler(ChessMove.class.getSimpleName(), new MessageHandler<ChessMove>() {
            @Override
            public void handleMessage(ChessMove chessMove) {
                try {
                    chessMove(chessMove);
                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(BoardReader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        
        messageReceiver.initialize();
        state = NONE;
    }
    
    public void setState(String state){
        this.state = state;
    }
    
    public static void main(String[] arg) throws Exception {
        PropertyConfigurator.configure("D:\\git\\Chess\\src\\main\\java\\log4j.properties");
        new BoardReader(new MqChessMessageSender("boardReader"), new RabbitMqChessImageReceiver(), new ChessMessageReceiver("BoardReader", true), new BoardCalculator());
    }
    
    private RequestMove requestedMove;

    
    public void chessMove(ChessMove chessMove){
        if(chessMove.isRobot()){
            int[] moves = convertFromMove(chessMove.getMove());
            boardCalculator.setMove(moves);
        }
    }
    
    public void requestMove(RequestMove requestMove) throws Exception {
        if (!requestMove.isRobot()) {
            requestedMove = requestMove;
            state = CHECK_FOR_HUMAN_MOVE;
            logger.debug(String.format("Set state to %s", state.toString()));
            messageSender.send(new MessageHolder(RequestImage.class.getSimpleName(), new RequestImage()));
        }
    }
    
    public void boardImage(BoardImage boardImage) {        
        BufferedImage bImageFromConvert = boardImage.GetBi();
        //logger.debug("Received Image");
        if (state.equals(CHECK_FOR_GAME_SETUP)) {
            boardCalculator.setInitialized(false);
            boardCalculator.initialLines(bImageFromConvert);
            if (boardCalculator.isInitialized()) {
                BoardStatus boardStatus = new BoardStatus(true, true, true);
                logger.debug(String.format("Sent %s", boardStatus.toString()));
                messageSender.send(new MessageHolder(BoardStatus.class.getSimpleName(), boardStatus));
                state = NONE;
            } else {
                messageSender.send(new MessageHolder(RequestImage.class.getSimpleName(), new RequestImage()));
            }
        } else if (state.equals(CHECK_FOR_HUMAN_MOVE)) {
            try {
                int[] move = boardCalculator.detectPieces(bImageFromConvert);
                if (move != null) {
                    logger.debug("Found White move");
                    state = NONE;
                    messageSender.send(new MessageHolder(ChessMove.class.getSimpleName(), new ChessMove(requestedMove.getTurn(), convertToMove(move), requestedMove.getMoveId(), false)));
                }else{
                    messageSender.send(new MessageHolder(RequestImage.class.getSimpleName(), new RequestImage()));
                }
            } catch (Exception ex) {
                logger.error(ex.getLocalizedMessage());
                messageSender.send(new MessageHolder(RequestImage.class.getSimpleName(), new RequestImage()));
            }
        } else if (state.equals(CHECK_FOR_PIECE_POSITIONS)) {
            try {
                boardCalculator.detectPieces(bImageFromConvert);
                PossiblePiece[][] lastBoard = boardCalculator.getLastBoard();
                logger.debug(String.format("received %s", requestPiecePositions.getMove()));
                int[] moves = convertFromMove(requestPiecePositions.getMove());
                logger.debug(String.format("Converted to %s,%s  %s,%s", moves[0], moves[1], moves[2], moves[3]));
                PossiblePiece fromPiece = lastBoard[moves[0]][moves[1]];
                if(lastBoard[moves[2]][moves[3]] == null){
                    logger.debug(String.format("fromPiece x=%s, y=%s row=%s col=%s", fromPiece.x, fromPiece.y, fromPiece.row, fromPiece.col));
                    double[] from = new double[2];
                    from[0] = (int) (xSlope * fromPiece.x + xIntercept + orgX); 
                    from[1] = (int) (ySlope * fromPiece.y + yIntercept + 190);
                    double[] to = calculateBoardPosition(moves[2], moves[3]);
                    PiecePositions piecePositions = new PiecePositions(from, to);
                    logger.debug(String.format("Sending peice positions %s", piecePositions));
                    messageSender.send(new MessageHolder(PiecePositions.class.getSimpleName(), piecePositions));
                    state = NONE;
                    return;
                }
                PossiblePiece capturePiece = lastBoard[moves[2]][moves[3]]; 
                state = NONE;
           } catch (Exception ex) {
                java.util.logging.Logger.getLogger(BoardReader.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }
    
    public double[] calculateBoardPosition(int x, int y) {
        double[] pos = new double[2];

        pos[0] = orgX + ((3 - x) * 18) + 9;
        pos[1] = orgY - ((7-y) * 18 + 9);

        return pos;
    } 
    
    public void boardAtRest(BoardAtRest boardAtRest) {
        if (state.equals(CHECK_FOR_REST_POSITION) && boardAtRest.IsAtRest()) {
            state = CHECK_FOR_GAME_SETUP;
            messageSender.send(new MessageHolder(RequestImage.class.getSimpleName(), new RequestImage()));
        }
    }
    
    public void requestBoardStatus(RequestBoardStatus requestBoardStatus) throws Exception {
        state = CHECK_FOR_REST_POSITION;
        messageSender.send(new MessageHolder(SetBoardRestPosition.class.getSimpleName(), new SetBoardRestPosition()));
    }
    
    public void startGame(StartGame startGame) {
        boardCalculator.setInitialized(false);
    }
    
    public void requestPiecePositions(RequestPiecePositions requestPiecePositions) {
        state = CHECK_FOR_PIECE_POSITIONS;
        this.requestPiecePositions = requestPiecePositions;
        messageSender.send(new MessageHolder(RequestImage.class.getSimpleName(), new RequestImage()));
    }
    
    private int[] convertFromMove(String move){
        int[] rv = new int[4];
        
        String from = move.substring(0,2);
        String to = move.substring(2,4);
        
        int[] fromValues = convertSingleMove(from);
        int[] toValues = convertSingleMove(to);
        
        rv[0] = fromValues[0];
        rv[1] = fromValues[1];
        rv[2] = toValues[0];
        rv[3] = toValues[1];
        
        return rv;
    }
    
    private int[] convertSingleMove(String move){
        int[] rv = new int[2];
        
        switch(move.charAt(0)){
            case 'a' : rv[0] = 7;
                break;
            case 'b' : rv[0] = 6;
                break;
            case 'c' : rv[0] = 5;
                break;
            case 'd' : rv[0] = 4;
                break;
            case 'e' : rv[0] = 3;
                break;
            case 'f' : rv[0] = 2;
                break;
            case 'g' : rv[0] = 1;
                break;
            case 'h' : rv[0] = 0;
                break;
        }

        rv[1] = Integer.parseInt(move.substring(1,2)) - 1;
        
        return rv;
    }
    
    private String convertToMove(int[] move) {
        
        return String.format("%s%s%s%s", convertAlpha(move[0]), move[1] + 1, convertAlpha(move[2]), move[3] + 1);
    }
    
    private String convertAlpha(int value) {
        switch (value) {
            case 0:
                return "h";
            case 1:
                return "g";
            case 2:
                return "f";
            case 3:
                return "e";
            case 4:
                return "d";
            case 5:
                return "c";
            case 6:
                return "b";
            case 7:
                return "a";
        }
        return "Z";
        // --throw new Exception("not valid alpha");
    }

}
