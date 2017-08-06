/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess;

import net.sprenkle.chess.models.PossiblePiece;
import net.sprenkle.chess.messages.RMQChessMessageReceiver;
import net.sprenkle.chess.messages.ChessMessageSender;
import net.sprenkle.chess.messages.StartGame;
import net.sprenkle.chess.messages.ChessMoveMsg;
import net.sprenkle.chess.messages.RequestMove;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.logging.Level;
import net.sprenkle.chess.messages.BoardStatus;
import net.sprenkle.chess.messages.ChessMessageReceiver;
import net.sprenkle.chess.messages.ConfirmedPieceMove;
import net.sprenkle.chess.messages.GCode;
import net.sprenkle.chess.messages.MessageHandler;
import net.sprenkle.chess.messages.RMQChessMessageSender;
import net.sprenkle.chess.messages.RequestBoardStatus;
import net.sprenkle.chess.messages.RequestMovePieces;
import net.sprenkle.chess.messages.KnownBoardPositions;
import net.sprenkle.chess.messages.MessageHolder;
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

        messageReceiver.addMessageHandler(StartGame.class.getName(), new MessageHandler<StartGame>() {
            @Override
            public void handleMessage(StartGame startGame) {
                startGame(startGame);
            }
        });

        messageReceiver.addMessageHandler(ChessMoveMsg.class.getName(), new MessageHandler<ChessMoveMsg>() {
            @Override
            public void handleMessage(ChessMoveMsg chessMove) {
                chessMoveMsg(chessMove);
            }
        });

        messageReceiver.addMessageHandler(BoardStatus.class.getName(), new MessageHandler<BoardStatus>() {
            @Override
            public void handleMessage(BoardStatus boardStatus) {
                boardStatus(boardStatus);
            }
        });

        messageReceiver.addMessageHandler(ConfirmedPieceMove.class.getName(), new MessageHandler<ConfirmedPieceMove>() {
            @Override
            public void handleMessage(ConfirmedPieceMove confirmedPieceMove) {
                confirmedPieceMove(confirmedPieceMove);
            }
        });

        try {
            messageReceiver.initialize(new RabbitConfiguration());
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(Chess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setChessState(ChessState chessState) {
        this.chessState = chessState;
    }

    public void setExpectedMove(UUID uuid) {
        expectedMove = uuid;
    }

    public void startGame(StartGame startGame) {
        chessMessageSender.send(new MessageHolder(new GCode("G4 P10", "Home X and Z")));
        chessMessageSender.send(new MessageHolder(new KnownBoardPositions(chessEngine.getKnownBoard())));
        chessEngine.reset();
        chessMessageSender.send(new MessageHolder(new RequestBoardStatus()));
    }

    public void chessMoveMsg(ChessMoveMsg chessMoveMsg) {
        if (!chessMoveMsg.getMoveId().equals(expectedMove)) {
            logger.debug(String.format("Received Unknown move %s  Expected %s", chessMoveMsg.getMoveId(), expectedMove));
            return;
        }

        expectedMove = null;

        timer.cancel();
        logger.debug(String.format("Message received ChessMove %s", chessMoveMsg.toString()));
        if (!chessState.getTurn().equals(chessMoveMsg.getChessMove().getTurn())) {
            logger.debug("Player out of Turn Message");
            requestMove(UUID.randomUUID());
            return;
        }
        String result = chessEngine.makeMove(chessMoveMsg.getChessMove().getMove());
        if (!result.equals("moveOk")) {
            //Todo send out a Color out of turn message
            //Todo send out a verify board posiion message
            requestMove(UUID.randomUUID()); // need to request move again
            return;
        }
        chessEngine.consoleOut(); // prints ascii board to console

        // Updates known board
        if (chessState.isActivePlayerRobot()) {
            net.sprenkle.chess.messages.ChessMove chessMove = chessMoveMsg.getChessMove();
            chessMessageSender.send(new MessageHolder(new RequestMovePieces(chessMove, chessEngine.isLastMoveCastle(), chessMoveMsg.getMoveId())));
            return;
        }

        chessMessageSender.send(new MessageHolder(new KnownBoardPositions(chessEngine.getKnownBoard())));

        chessState.setTurn(chessState.getTurn() == Player.White ? Player.Black : Player.White);
        requestMove(UUID.randomUUID());
    }

    private void requestMove(UUID id) {
        expectedMove = id;
        RequestMove requestMove = new RequestMove(chessState.getTurn(), chessState.isActivePlayerRobot(), chessEngine.getMoves(), expectedMove);
        logger.debug(requestMove.toString());
        chessMessageSender.send(new MessageHolder(requestMove));
        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                logger.debug("Move Timeout Activated.");
                requestMove(expectedMove);
            }
        }, 2 * 60 * 1000);
    }

    public static void main(String[] args) throws Exception {
        PropertyConfigurator.configure("D:\\git\\Chess\\src\\main\\java\\log4j.properties");

//        AnetBoardController anetBoardController = new AnetBoardController(new RMQChessMessageSender("AnetBoardController"), new RMQChessMessageReceiver("AnetBoardController", true));
//        BoardReader boardReader = new BoardReader(new BoardReaderState(), new RMQChessMessageSender("boardReader"), new RMQChessMessageReceiver("BoardReader", true), 
//                new BoardCalculator(), new PiecePositionsIdentifier());
//
//        RobotMover robotMover = new RobotMover(new StockFishUCI(), new RMQChessMessageSender("RobotMover"), new RMQChessMessageReceiver("RobotMover", false));
        Chess chess = new Chess(new ChessController(), new RMQChessMessageSender("Chess", new RabbitConfiguration()), new RMQChessMessageReceiver("Chess", false));
    }

    public void boardStatus(BoardStatus boardStatus) {
        logger.debug(boardStatus.toString());
        if (boardStatus.isStartingPositionSet()) {
            // Build board and send out
            PossiblePiece[][] knownBoard = chessEngine.getKnownBoard();
            chessMessageSender.send(new MessageHolder(new KnownBoardPositions(knownBoard)));

            chessEngine.newGame();
            chessState.setTurn(Player.White);
            chessState.setWhiteRobot(!boardStatus.isHumanSide());
            chessState.setBlackRobot(boardStatus.isHumanSide());
            requestMove(UUID.randomUUID());
        }
    }

    public void confirmedPieceMove(ConfirmedPieceMove confirmedPieceMove) {
        if (confirmedPieceMove.getPieceMoved()) {
            // Set KnownBoardPositions
            chessState.setTurn(chessState.getTurn() == Player.White ? Player.Black : Player.White);
            PossiblePiece[][] knownBoard = chessEngine.getKnownBoard();
            KnownBoardPositions knownBoardPositions = new KnownBoardPositions(knownBoard);
            MessageHolder mh = new MessageHolder(knownBoardPositions);
            chessMessageSender.send(mh);
            requestMove(UUID.randomUUID());
        }
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
