package net.sprenkle.chess;

import java.util.UUID;
import net.sprenkle.chess.controllers.PiecePositionsIdentifier;
import net.sprenkle.chess.imaging.BoardCalculator;
import net.sprenkle.chess.messages.RMQChessMessageReceiver;
import net.sprenkle.chess.messages.MqChessMessageSender;
import net.sprenkle.chess.messages.RequestPiecePositions;
import net.sprenkle.chess.messages.ChessMove;
import net.sprenkle.chess.messages.RMQChesssImageReceiver;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.mockito.Mockito.mock;

/**
 *
 * @author david
 */
public class BoardReaderIT {
    
    public BoardReaderIT() {
    }
    
    


    /**
     * Test of requestPiecePositions method, of class BoardReader.
     */
    @Test
    public void testRequestPiecePositions() throws Exception {
        System.out.println("requestPiecePositions");
        RequestPiecePositions requestPiecePositions = new RequestPiecePositions(new ChessMove(Player.White, "e7e6"), false, UUID.randomUUID());
        
   //         public BoardReader(
        MqChessMessageSender messageSender = mock(MqChessMessageSender.class);
        RMQChessMessageReceiver messageReceiver  = mock(RMQChessMessageReceiver.class);
        BoardCalculator boardCalculator = mock(BoardCalculator.class);

        BoardReaderState boardReaderState = new BoardReaderState();
        
        BoardReader instance = new BoardReader(boardReaderState, messageSender, messageReceiver, boardCalculator, mock(PiecePositionsIdentifier.class), mock(RMQChesssImageReceiver.class));

        boardReaderState.setPiecePosition();
        
      //  instance.calculateBoardPosition(5, 5);
        
        // TODO review the generated test code and remove the default call to fail.
    //    fail("The test case is a prototype.");
        instance.boardImage(null);
    }
    
}
