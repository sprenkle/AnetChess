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
import net.sprenkle.chess.messages.ChessMessageReceiver;
import net.sprenkle.chess.messages.ChessMessageSender;
import net.sprenkle.chess.messages.ChessMove;
import net.sprenkle.chess.messages.ConfirmedPieceMove;
import net.sprenkle.chess.messages.MessageHandler;
import net.sprenkle.chess.messages.StartGame;

/**
 *
 * @author david
 */
public class MessageTxRxTests {

    public MessageTxRxTests() {
        ChessMessageReceiver messageReceiver = new ChessMessageReceiver("MessageTxRxTests", true);
    }

    @org.junit.Test
    public void testSendingRecievingMessages() {

    }
}
