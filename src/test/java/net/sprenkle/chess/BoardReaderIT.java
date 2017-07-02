/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess;

import net.sprenkle.chess.imaging.BoardCalculator;
import net.sprenkle.chess.messages.ChessMessageReceiver;
import net.sprenkle.chess.messages.MqChessMessageSender;
import net.sprenkle.chess.messages.RequestPiecePositions;
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
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    // Piece moved from 6,7 to 5,5
    // hessController  - Error b8c6
    
//    /**
//     * Test of main method, of class BoardReader.
//     */
//    @Test
//    public void testMain() throws Exception {
//        System.out.println("main");
//        String[] arg = null;
//        BoardReader.main(arg);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of requestMove method, of class BoardReader.
//     */
//    @Test
//    public void testRequestMove() throws Exception {
//        System.out.println("requestMove");
//        RequestMove requestMove = null;
//        BoardReader instance = null;
//        instance.requestMove(requestMove);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of boardImage method, of class BoardReader.
//     */
//    @Test
//    public void testBoardImage() {
//        System.out.println("boardImage");
//        BoardImage boardImage = null;
//        BoardReader instance = null;
//        instance.boardImage(boardImage);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of calculateBoardPosition method, of class BoardReader.
//     */
//    @Test
//    public void testCalculateBoardPosition() {
//        System.out.println("calculateBoardPosition");
//        int x = 0;
//        int y = 0;
//        BoardReader instance = null;
//        double[] expResult = null;
//        double[] result = instance.calculateBoardPosition(x, y);
//        assertArrayEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of boardAtRest method, of class BoardReader.
//     */
//    @Test
//    public void testBoardAtRest() {
//        System.out.println("boardAtRest");
//        BoardAtRest boardAtRest = null;
//        BoardReader instance = null;
//        instance.boardAtRest(boardAtRest);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of requestBoardStatus method, of class BoardReader.
//     */
//    @Test
//    public void testRequestBoardStatus() throws Exception {
//        System.out.println("requestBoardStatus");
//        RequestBoardStatus requestBoardStatus = null;
//        BoardReader instance = null;
//        instance.requestBoardStatus(requestBoardStatus);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of startGame method, of class BoardReader.
//     */
//    @Test
//    public void testStartGame() {
//        System.out.println("startGame");
//        StartGame startGame = null;
//        BoardReader instance = null;
//        instance.startGame(startGame);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of requestPiecePositions method, of class BoardReader.
     */
    @Test
    public void testRequestPiecePositions() throws Exception {
        System.out.println("requestPiecePositions");
        RequestPiecePositions requestPiecePositions = new RequestPiecePositions("e7e6");
        
   //         public BoardReader(
        MqChessMessageSender messageSender = mock(MqChessMessageSender.class);
        ChessMessageReceiver messageReceiver  = mock(ChessMessageReceiver.class);
        BoardCalculator boardCalculator = mock(BoardCalculator.class);

        
        
        BoardReader instance = new BoardReader(messageSender, messageReceiver, boardCalculator);

        instance.setState("checkForPiecePositions");
        
        instance.calculateBoardPosition(5, 5);
        
        // TODO review the generated test code and remove the default call to fail.
    //    fail("The test case is a prototype.");
    }
    
}
