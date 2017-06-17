/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess;

import net.sprenkle.chess.ChessState.Player;
import net.sprenkle.chess.messages.ChessMessageReceiver;
import net.sprenkle.chess.messages.ChessMessageSender;
import net.sprenkle.chess.messages.StartGame;
import net.sprenkle.chess.messages.ChessMove;
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
    ChessState chessState;
    ChessMessageSender chessMessageSender;
    Timer timer = new Timer(true);
    UUID expectedMove;

    
    public Chess(ChessControllerInterface chessEngine, ChessMessageSender chessMessageSender, ChessMessageReceiver messageReceiver) {
        this.chessEngine = chessEngine;
        this.chessMessageSender = chessMessageSender;
        chessState = new ChessState();
        
        messageReceiver.addMessageHandler(StartGame.class.getSimpleName(), new MessageHandler<StartGame>(){
            @Override
            public void handleMessage(StartGame startGame) {
                startGame(startGame);
            }
        });
        
        messageReceiver.addMessageHandler(ChessMove.class.getSimpleName(), new MessageHandler<ChessMove>(){
            @Override
            public void handleMessage(ChessMove chessMove) {
                chessMoved(chessMove);
            }
        });

        messageReceiver.addMessageHandler(BoardStatus.class.getSimpleName(), new MessageHandler<BoardStatus>(){
            @Override
            public void handleMessage(BoardStatus boardStatus) {
                boardStatus(boardStatus);
            }
        });

        messageReceiver.addMessageHandler(ConfirmedPieceMove.class.getSimpleName(), new MessageHandler<ConfirmedPieceMove>(){
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

    public void startGame(StartGame startGame) {
        logger.debug("Message received StartGame");
        logger.debug("Sending RequestBoardStatus");
        chessMessageSender.send(new MessageHolder(RequestBoardStatus.class.getSimpleName(), new RequestBoardStatus()));
    }

    public void chessMoved(ChessMove chessMove) {
        if (!chessMove.getMoveId().equals(expectedMove)) {
            logger.debug(String.format("Received Unknown move %s  Expected %s", chessMove.getMoveId(), expectedMove));
            return;
        }
        timer.cancel();
        logger.debug(String.format("Message received ChessMove %s", chessMove.toString()));
        if (chessState.getTurn() != chessMove.getTurn()) {
            sendPlayerOutOfTurnMessage();
        }

        String result = chessEngine.makeMove(chessMove.getMove());
        if (!result.equals("moveOk")) {
            //Todo send out a Player out of turn message
            //Todo send out a verify board posiion message
            return;
        }

        chessEngine.consoleOut();

        if(chessState.isActivePlayerRobot()){
            chessMessageSender.send(new MessageHolder(RequestMovePieces.class.getSimpleName(), new RequestMovePieces(chessMove.getMove())));
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

//    private void sendPlayerMakeMove() {
//        System.out.format("Player %s make move.", chessState.getTurn() == Player.White ? "White" : "Black");
//    }
    private void sendPlayerOutOfTurnMessage() {
        logger.debug("Player out of Turn Message");
    }

//    private void sendInvalidMoveMessage(String mesg) {
//        System.out.format("Invalid Move %s\n", mesg);
//    }
    public static void main(String[] args) throws Exception {
        PropertyConfigurator.configure("D:\\git\\Chess\\src\\main\\java\\log4j.properties");
        Chess chess = new Chess(new ChessController(), new MqChessMessageSender("Chess"), new ChessMessageReceiver("Chess", false));
        
//        new BoardReader(new MqChessMessageSender("boardReader"), new RabbitMqChessImageReceiver(), new ChessMessageReceiver("BoardReader"), new BoardCalculator());
//
//        AnetBoardController anetBoardController = new AnetBoardController(new MqChessMessageSender("AnetBoardController"), new ChessMessageReceiver("AnetBoardController"));
//
//        new RobotMover(new StockFishUCI(), new MqChessMessageSender("RobotMover"), new ChessMessageReceiver("RobotMover"));

    }

    public void boardStatus(BoardStatus boardStatus){
        logger.debug(boardStatus.toString());
        if (boardStatus.isStartingPositionSet()) {
            // Build board and send out
            PossiblePiece[][] knownBoard = new PossiblePiece[8][8];
            knownBoard[0][0] = new PossiblePiece(true, PossiblePiece.ROOK, 0, 0);
            knownBoard[7][0] = new PossiblePiece(true, PossiblePiece.ROOK, 7, 0);
            knownBoard[0][7] = new PossiblePiece(false, PossiblePiece.ROOK, 0, 7);
            knownBoard[7][7] = new PossiblePiece(false, PossiblePiece.ROOK, 7, 7);
            knownBoard[1][0] = new PossiblePiece(true, PossiblePiece.KNIGHT, 1, 0);
            knownBoard[6][0] = new PossiblePiece(true, PossiblePiece.KNIGHT, 6, 0);
            knownBoard[1][7] = new PossiblePiece(false, PossiblePiece.KNIGHT, 1, 7);
            knownBoard[6][7] = new PossiblePiece(false, PossiblePiece.KNIGHT, 6, 7);
            knownBoard[2][0] = new PossiblePiece(true, PossiblePiece.BISHOP, 2, 0);
            knownBoard[5][0] = new PossiblePiece(true, PossiblePiece.BISHOP, 5, 0);
            knownBoard[2][7] = new PossiblePiece(false, PossiblePiece.BISHOP, 2, 7);
            knownBoard[5][7] = new PossiblePiece(false, PossiblePiece.BISHOP, 5, 7);
            knownBoard[3][0] = new PossiblePiece(true, PossiblePiece.QUEEN, 4, 0);
            knownBoard[3][7] = new PossiblePiece(false, PossiblePiece.QUEEN, 4, 7);
            knownBoard[4][0] = new PossiblePiece(true, PossiblePiece.KING, 3, 0);
            knownBoard[4][7] = new PossiblePiece(false, PossiblePiece.KING, 3, 7);
            for (int i = 0; i < 8; i++) {
               knownBoard[i][1] = new PossiblePiece(true, PossiblePiece.PAWN, i, 1);
                knownBoard[i][6] = new PossiblePiece(false, PossiblePiece.PAWN, i, 6);
            }

            chessMessageSender.send(new MessageHolder(KnownBoardPositions.class.getSimpleName(), new KnownBoardPositions(chessEngine.getKnownBoard())));
            
            chessEngine.newGame();
            chessState.setTurn(Player.White); 
            chessState.setWhiteRobot(!boardStatus.isHumanSide());
            chessState.setBlackRobot(boardStatus.isHumanSide());
            requestMove();
        }
    }
    
    public void confirmedPieceMove(ConfirmedPieceMove confirmedPieceMove){
        if(confirmedPieceMove.getPieceMoved()){
            // Set KnownBoardPositions
            
            
            chessState.setTurn(chessState.getTurn() == Player.White ? Player.Black : Player.White);
            requestMove();
        }
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
