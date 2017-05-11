/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess;

import java.awt.image.BufferedImage;
import net.sprenkle.chess.messages.ChessImageListenerInterface;
import net.sprenkle.chess.messages.ChessMessageReceiver;
import net.sprenkle.chess.messages.ChessMove;
import net.sprenkle.chess.messages.MqChessMessageSender;
import net.sprenkle.chess.messages.RabbitMqChessImageReceiver;
import net.sprenkle.chess.messages.RequestMove;
import net.sprenkle.chess.messages.StartGame;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author david
 */
public class BoardReader implements ChessInterface, ChessImageListenerInterface {
    private final MqChessMessageSender messageSender;
    private final String CHECK_FOR_GAME_SETUP = "checkForGameSetup";
    private String state;
    
    
    public BoardReader(MqChessMessageSender messageSender, RabbitMqChessImageReceiver imageReceiver){
        this.messageSender = messageSender;
        imageReceiver.setListener(this);
        state = CHECK_FOR_GAME_SETUP;
    }

    public static void main(String[] arg) throws Exception{
        PropertyConfigurator.configure("D:\\git\\Chess\\src\\main\\java\\log4j.properties");

        BoardReader boardReader = new BoardReader(new MqChessMessageSender(), new RabbitMqChessImageReceiver());
        ChessMessageReceiver chessMessageReceiver = new ChessMessageReceiver(boardReader);
        chessMessageReceiver.initialize();
 
    }

    @Override
    public void startGame(StartGame startGame) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void chessMoved(ChessMove chessMove) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void requestMove(RequestMove requestMove) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void receivedImage(BufferedImage bi) {
        if(state == CHECK_FOR_GAME_SETUP){
            
        }
    }
    
    private void checkForGameSetup(){
        
    }
}
