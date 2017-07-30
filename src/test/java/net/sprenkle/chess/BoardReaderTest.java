/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess;

import net.sprenkle.chess.controllers.PiecePositionsIdentifier;
import net.sprenkle.chess.imaging.BoardCalculator;
import net.sprenkle.chess.messages.BoardAtRest;
import net.sprenkle.chess.messages.BoardImage;
import net.sprenkle.chess.messages.RMQChessMessageReceiver;
import net.sprenkle.chess.messages.KnownBoardPositions;
import net.sprenkle.chess.messages.MqChessMessageSender;
import net.sprenkle.chess.messages.RMQChesssImageReceiver;
import net.sprenkle.chess.messages.RequestBoardStatus;
import net.sprenkle.chess.messages.RequestMove;
import net.sprenkle.chess.messages.RequestPiecePositions;
import net.sprenkle.chess.messages.StartGame;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


/**
 *
 * @author david
 */
public class BoardReaderTest {
    
    public BoardReaderTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }


    @Test
    public void testRequestMove() throws Exception {
        System.out.println("requestMove");
        RequestMove requestMove = null;
        MqChessMessageSender messageSender = mock(MqChessMessageSender.class);
        RMQChessMessageReceiver messageReceiver = mock(RMQChessMessageReceiver.class); 
        BoardCalculator boardCalculator = mock(BoardCalculator.class);
        BoardReaderState state = new BoardReaderState();
        BoardReader instance = new BoardReader(state, messageSender, messageReceiver, boardCalculator, mock(PiecePositionsIdentifier.class), mock(RMQChesssImageReceiver.class));

        // Is robot 
        requestMove = new RequestMove(Player.White, true, "", null);
        instance.requestMove(requestMove);
        verify(messageSender, times(0)).send(null);
        assertTrue("Robot Move should do nothing", state.inState(BoardReaderState.NONE));

        // Not a robot move
        state.reset();
        requestMove = new RequestMove(Player.White, false, "", null);
        instance.requestMove(requestMove);
        verify(messageSender, times(1)).send(notNull());
        assertTrue("Not robot move look for human move", state.inState(BoardReaderState.CHECK_FOR_HUMAN_MOVE));
    }

}
