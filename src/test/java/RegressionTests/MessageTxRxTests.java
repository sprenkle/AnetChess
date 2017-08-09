/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RegressionTests;

import java.util.logging.Level;
import net.sprenkle.chess.Chess;
import net.sprenkle.chess.ChessControllerInterface;
import net.sprenkle.chess.ChessState;
import net.sprenkle.chess.messages.BoardStatus;
import net.sprenkle.chess.messages.RMQChessMessageReceiver;
import net.sprenkle.chess.messages.ChessMessageSender;
import net.sprenkle.chess.messages.ChessMoveMsg;
import net.sprenkle.chess.messages.ConfirmedPieceMove;
import net.sprenkle.chess.messages.MessageHandler;
import net.sprenkle.chess.messages.StartChessGame;

/**
 *
 * @author david
 */
public class MessageTxRxTests {

    public MessageTxRxTests() {
        RMQChessMessageReceiver messageReceiver = new RMQChessMessageReceiver("MessageTxRxTests", true);
    }

    @org.junit.Test
    public void testSendingRecievingMessages() {

    }
}
