/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess;

import net.sprenkle.chess.messages.ChessMessageReceiver;
import net.sprenkle.chess.messages.ChessMessageSender;
import net.sprenkle.chess.messages.StartGame;
import net.sprenkle.chess.messages.ChessMoveMsg;
import net.sprenkle.chess.messages.RequestMove;
import net.sprenkle.messages.MessageHolder;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.logging.Level;
import net.sprenkle.chess.messages.BoardStatus;
import net.sprenkle.chess.messages.ConfirmedPieceMove;
import net.sprenkle.chess.messages.MessageHandler;
import net.sprenkle.chess.messages.MqChessMessageSender;
import net.sprenkle.chess.messages.RequestBoardStatus;
import net.sprenkle.chess.messages.RequestMovePieces;
import net.sprenkle.chess.messages.KnownBoardPositions;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author David
 *
 */
public class Chess extends TimerTask {

    static Logger logger = Logger.getLogger(Chess.class.getSimpleName());
    static final boolean WHITE = true;
    static final boolean BLACK = false;

    private final ChessControllerInterface chessEngine;
    private ChessState chessState;
    private ChessMessageSender chessMessageSender;
    private Timer timer = new Timer(true);
    private UUID expectedMove;

    public Chess(ChessControllerInterface chessEngine, ChessMessageSender chessMessageSender, ChessMessageReceiver messageReceiver) {
        this.chessEngine = chessEngine;
        this.chessMessageSender = chessMessageSender;
        chessState = new ChessState();

        messageReceiver.addMessageHandler(StartGame.class.getSimpleName(), new MessageHandler<StartGame>() {
            @Override
            public void handleMessage(StartGame startGame) {
                startGame(startGame);
            }
        });

        messageReceiver.addMessageHandler(ChessMoveMsg.class.getSimpleName(), new MessageHandler<ChessMoveMsg>() {
            @Override
            public void handleMessage(ChessMoveMsg chessMove) {
                chessMoved(chessMove);
            }
        });

        messageReceiver.addMessageHandler(BoardStatus.class.getSimpleName(), new MessageHandler<BoardStatus>() {
            @Override
            public void handleMessage(BoardStatus boardStatus) {
                boardStatus(boardStatus);
            }
        });

        messageReceiver.addMessageHandler(ConfirmedPieceMove.class.getSimpleName(), new MessageHandler<ConfirmedPieceMove>() {
            @Override
            public void handleMessage(ConfirmedPieceMove confirmedPieceMove) {
                confirmedPieceMove(confirmedPieceMove);
            }
        });

        try {
            messageReceiver.initialize();
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(Chess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setChessState(ChessState chessState){
        this.chessState = chessState;
    }
    
    public void setExpectedMove(UUID uuid){
        expectedMove = uuid;
    }
    
    public void startGame(StartGame startGame) {
       chessMessageSender.send(new MessageHolder(KnownBoardPositions.class.getSimpleName(), new KnownBoardPositions(chessEngine.getKnownBoard())));
       chessMessageSender.send(new MessageHolder(RequestBoardStatus.class.getSimpleName(), new RequestBoardStatus()));
    }

    public void chessMoved(ChessMoveMsg chessMoveMsg) {
        if (!chessMoveMsg.getMoveId().equals(expectedMove)) {
            logger.debug(String.format("Received Unknown move %s  Expected %s", chessMoveMsg.getMoveId(), expectedMove));
            return;
        }
        timer.cancel();
        logger.debug(String.format("Message received ChessMove %s", chessMoveMsg.toString()));
        if (!chessState.getTurn().equals(chessMoveMsg.getChessMove().getTurn())) {
            logger.debug("Player out of Turn Message");
            requestMove();
            return;
        }
        String result = chessEngine.makeMove(chessMoveMsg.getChessMove().getMove());
        if (!result.equals("moveOk")) {
            //Todo send out a Color out of turn message
            //Todo send out a verify board posiion message
            requestMove(); // need to request move again
            return;
        }
        chessEngine.consoleOut(); // prints ascii board to console

        if (chessState.isActivePlayerRobot()) {
            net.sprenkle.chess.messages.ChessMove chessMove = chessMoveMsg.getChessMove();
            chessMessageSender.send(new MessageHolder(RequestMovePieces.class.getSimpleName(), new RequestMovePieces(chessMove, chessEngine.isLastMoveCastle(), chessMoveMsg.getMoveId())));
            return;
        }
        chessState.setTurn(chessState.getTurn() == Player.White ? Player.Black : Player.White);
        requestMove();
    }

    private void requestMove() {
        expectedMove = UUID.randomUUID();
        RequestMove requestMove = new RequestMove(chessState.getTurn(), chessState.isActivePlayerRobot(), chessEngine.getMoves(), expectedMove);
        logger.debug(requestMove.toString());
        chessMessageSender.send(new MessageHolder(RequestMove.class.getSimpleName(), requestMove));
        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                logger.debug("Move Timeout Activated.");
                requestMove();
            }
        }, 2 * 60 * 1000);
    }

    public static void main(String[] args) throws Exception {
        PropertyConfigurator.configure("D:\\git\\Chess\\src\\main\\java\\log4j.properties");

//        AnetBoardController anetBoardController = new AnetBoardController(new MqChessMessageSender("AnetBoardController"), new ChessMessageReceiver("AnetBoardController", true));

//        BoardReader boardReader = new BoardReader(new BoardReaderState(), new MqChessMessageSender("boardReader"), new ChessMessageReceiver("BoardReader", true), 
//                new BoardCalculator(), new PiecePositionsIdentifier());
//
//        RobotMover robotMover = new RobotMover(new StockFishUCI(), new MqChessMessageSender("RobotMover"), new ChessMessageReceiver("RobotMover", false));

        Chess chess = new Chess(new ChessController(), new MqChessMessageSender("Chess"), new ChessMessageReceiver("Chess", false));
    }

    public void boardStatus(BoardStatus boardStatus) {
        logger.debug(boardStatus.toString());
        if (boardStatus.isStartingPositionSet()) {
            // Build board and send out
            chessMessageSender.send(new MessageHolder(KnownBoardPositions.class.getSimpleName(), new KnownBoardPositions(chessEngine.getKnownBoard())));

            chessEngine.newGame();
            chessState.setTurn(Player.White);
            chessState.setWhiteRobot(!boardStatus.isHumanSide());
            chessState.setBlackRobot(boardStatus.isHumanSide());
            requestMove();
        }
    }

    public void confirmedPieceMove(ConfirmedPieceMove confirmedPieceMove) {
        if (confirmedPieceMove.getPieceMoved()) {
            // Set KnownBoardPositions
            chessState.setTurn(chessState.getTurn() == Player.White ? Player.Black : Player.White);
            KnownBoardPositions knownBoardPositions = new KnownBoardPositions(chessEngine.getKnownBoard());
            MessageHolder mh = new MessageHolder(KnownBoardPositions.class.getSimpleName(), knownBoardPositions);
            chessMessageSender.send(mh);
            requestMove();
        }
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
