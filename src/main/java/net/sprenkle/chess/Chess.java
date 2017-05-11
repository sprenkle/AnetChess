/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess;

import dagger.ObjectGraph;
import javax.inject.Inject;
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
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
/**
 *
 * @author David
 *
 */
public class Chess extends TimerTask implements ChessInterface {
    static Logger logger = Logger.getLogger(Chess.class.getSimpleName());
 
    private final ChessControllerInterface chessEngine;
    ChessState chessState;
    ChessMessageSender chessMessageSender;
    Timer timer = new Timer(true);
    UUID expectedMove;
    
    @Inject
    public Chess(ChessControllerInterface chessEngine, ChessState chessState, ChessMessageSender chessMessageSender) {
        this.chessEngine = chessEngine;
        this.chessState = chessState;
        this.chessMessageSender = chessMessageSender;
    }

    public void start() {
        logger.debug("Started");
    }

    @Override
    public void startGame(StartGame startGame) {
        logger.debug("Message received StartGame");
        chessEngine.newGame();
        chessState.setTurn(Player.White);
        chessState.setWhiteRobot(startGame.isWhiteRobot());
        chessState.setBlackRobot(startGame.isBlackRobot());
        sendMove();
    }

    @Override
    public void chessMoved(ChessMove chessMove) throws Exception {
        if(!chessMove.getMoveId().equals(expectedMove)){
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

        chessState.setTurn(chessState.getTurn() == Player.White ? Player.Black : Player.White);

        //chessMessageSender.send(new MessageHolder(RequestMove.class.getSimpleName(), new RequestMove(chessState.getTurn(), chessState.isActivePlayerRobot(),chessEngine.getMoves())));
        sendMove();
    }

    private void sendMove() {
        expectedMove = UUID.randomUUID();
        RequestMove requestMove = new RequestMove(chessState.getTurn(), chessState.isActivePlayerRobot(), chessEngine.getMoves(), expectedMove);
        logger.debug(requestMove.toString());
        chessMessageSender.send(new MessageHolder(RequestMove.class.getSimpleName(), requestMove));
        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
               logger.debug("Move Timeout Activated.");
                sendMove();
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
        ObjectGraph objectGraph = ObjectGraph.create(new ChessModule());
        ChessMessageReceiver chessMessageReceiver = objectGraph.get(ChessMessageReceiver.class);
        chessMessageReceiver.initialize();
    }

    @Override
    public void requestMove(RequestMove requestMove) throws Exception {
        //System.out.format("requestMove %s\n", requestMove);
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
