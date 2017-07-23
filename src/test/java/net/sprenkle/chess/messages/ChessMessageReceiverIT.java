/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.messages;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import net.sprenkle.chess.BoardReader;
import net.sprenkle.chess.Player;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author david
 */
public class ChessMessageReceiverIT {
    
    public ChessMessageReceiverIT() {
    }

    @Test
    public void testChessMoveMessage() {
        UUID id = UUID.randomUUID();
        
        ChessMessageReceiver uut = new ChessMessageReceiver("test", true);
        
          uut.addMessageHandler(RequestMove.class.getSimpleName(), new MessageHandler<RequestMove>() {
            @Override
            public void handleMessage(RequestMove requestMove) {
                try {
                    requestMove(requestMove);
                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(BoardReader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            private void requestMove(RequestMove requestMove) {
                assertEquals("test Turn", requestMove.getTurn(), Player.White);
                assertEquals("test Move History", requestMove.getMoveHistory(), "e2e4");
                assertEquals("test Turn", requestMove.getMoveId(), id);
                assertEquals("test isRobot", requestMove.isRobot(), true);
            }
        });
        
        RequestMove requestMove = new RequestMove(Player.White, true, "e2e4", id);
        MessageHolder mh = new MessageHolder(RequestMove.class.getSimpleName(), requestMove);
        
        try {
            uut.handleDelivery(mh.toBytes());
        } catch (IOException ex) {
            Assert.fail("Threw an exception.");
        }
    }
}
