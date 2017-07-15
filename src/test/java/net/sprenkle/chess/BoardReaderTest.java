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
import net.sprenkle.chess.messages.ChessMessageReceiver;
import net.sprenkle.chess.messages.KnownBoardPositions;
import net.sprenkle.chess.messages.MqChessMessageSender;
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
    public void testSomeMethod() {
        fail("The test case is a prototype.");
    }


    @Test
    public void testMain() throws Exception {
        System.out.println("main");
        String[] arg = null;
        BoardReader.main(arg);
        fail("The test case is a prototype.");
    }

    @Test
    public void testRequestMove() throws Exception {
        System.out.println("requestMove");
        RequestMove requestMove = null;
        MqChessMessageSender messageSender = mock(MqChessMessageSender.class);
        ChessMessageReceiver messageReceiver = mock(ChessMessageReceiver.class); 
        BoardCalculator boardCalculator = mock(BoardCalculator.class);
        BoardReaderState state = new BoardReaderState();
        BoardReader instance = new BoardReader(state, messageSender, messageReceiver, boardCalculator, mock(PiecePositionsIdentifier.class));

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

    @Test
    public void testBoardImage() {
        System.out.println("boardImage");
        BoardImage boardImage = null;
        BoardReader instance = null;
        instance.boardImage(boardImage);
        fail("The test case is a prototype.");
    }
    
    @Test
    public void testPiecePosition(){
        
    }

    @Test
    public void testCalculateBoardPosition() {
//        System.out.println("calculateBoardPosition");
//        int x = 0;
//        int y = 0;
//        BoardReader instance = null;
//        double[] expResult = null;
//        double[] result = instance.calculateBoardPosition(x, y);
//        assertArrayEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testBoardAtRest() {
        System.out.println("boardAtRest");
        BoardAtRest boardAtRest = null;
        BoardReader instance = null;
        instance.boardAtRest(boardAtRest);
        fail("The test case is a prototype.");
    }

    @Test
    public void testRequestBoardStatus() throws Exception {
        System.out.println("requestBoardStatus");
        RequestBoardStatus requestBoardStatus = null;
        BoardReader instance = null;
        instance.requestBoardStatus(requestBoardStatus);
        fail("The test case is a prototype.");
    }

    @Test
    public void testStartGame() {
        System.out.println("startGame");
        StartGame startGame = null;
        BoardReader instance = null;
        instance.startGame(startGame);
        fail("The test case is a prototype.");
    }

    @Test
    public void testRequestPiecePositions() {
        System.out.println("requestPiecePositions");
        RequestPiecePositions requestPiecePositions = null;
        BoardReader instance = null;
        instance.requestPiecePositions(requestPiecePositions);
        fail("The test case is a prototype.");
    }

    @Test
    public void testKnownBoardPositions() {
        System.out.println("knownBoardPositions");
        KnownBoardPositions knownBoardPositions = null;
        BoardReader instance = null;
        instance.knownBoardPositions(knownBoardPositions);
        fail("The test case is a prototype.");
    }
    
}
