/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess;

import net.sprenkle.chess.pieces.Board;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author david
 */
public class ChessControllerTest {
    
    public ChessControllerTest() {
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

//    /**
//     * Test of getBoard method, of class ChessController.
//     */
//    @Test
//    public void testGetBoard() {
//        System.out.println("getBoard");
//        ChessController instance = null;
//        Board expResult = null;
//        Board result = instance.getBoard();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getNumActivePieces method, of class ChessController.
//     */
//    @Test
//    public void testGetNumActivePieces() {
//        System.out.println("getNumActivePieces");
//        ChessController instance = null;
//        int expResult = 0;
//        int result = instance.getNumActivePieces();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of newGame method, of class ChessController.
//     */
//    @Test
//    public void testNewGame() {
//        System.out.println("newGame");
//        ChessController instance = null;
//        instance.newGame();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of makeMove method, of class ChessController.
//     */
//    @Test
//    public void testMakeMove() {
//        System.out.println("makeMove");
//        String move = "";
//        ChessController instance = null;
//        String expResult = "";
//        String result = instance.makeMove(move);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of isInCheck method, of class ChessController.
//     */
//    @Test
//    public void testIsInCheck() {
//        System.out.println("isInCheck");
//        int color = 0;
//        ChessController instance = null;
//        boolean expResult = false;
//        boolean result = instance.isInCheck(color);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of isAbleToMove method, of class ChessController.
//     */
//    @Test
//    public void testIsAbleToMove() {
//        System.out.println("isAbleToMove");
//        int color = 0;
//        ChessController instance = null;
//        boolean expResult = false;
//        boolean result = instance.isAbleToMove(color);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getMoves method, of class ChessController.
//     */
//    @Test
//    public void testGetMoves() {
//        System.out.println("getMoves");
//        ChessController instance = null;
//        String expResult = "";
//        String result = instance.getMoves();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getBestMove method, of class ChessController.
//     */
//    @Test
//    public void testGetBestMove() {
//        System.out.println("getBestMove");
//        ChessController instance = null;
//        String expResult = "";
//        String result = instance.getBestMove();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of main method, of class ChessController.
     */
    @Test
    public void testBlackKingSideCastle() {
        System.out.println("main");
        ChessController chessController = new ChessController();
        chessController.newGame();
        String results = chessController.makeMove("e2e4");
        results = chessController.makeMove("e7e6");
        results = chessController.makeMove("d2d4");
        results = chessController.makeMove("d7d5");
        results = chessController.makeMove("b1c3");
        results = chessController.makeMove("f8b4");
        results = chessController.makeMove("e4d5");
        results = chessController.makeMove("e6d5");
        results = chessController.makeMove("g1f3");
        results = chessController.makeMove("g8f6");
        results = chessController.makeMove("f1d3");
        results = chessController.makeMove("e8g8");
        assertEquals("moveOk", results);
        assertTrue(chessController.isLastMoveCastle());
    }


//    @Test
//    public void testBlackQueenSideCastle() {
//        System.out.println("main");
//        String[] args = null;
//        ChessController chessController = new ChessController();
//        chessController.newGame();
//        
//        String moves = "e2e4 e7e5 b1c3 b8c6 g1f3 f8c5 f1c4 g8e7 e1g1 f7f6 d2d3 c6b4 a2a3 c7c6 a3b4 c5b6 c3a4 d7d5 e4d5 c6d5 c4b5 c8d7 b5d7 d8d7 a4b6 d7d8 b6a8 d8a8 c2c3 b7b6 d1a4 e7c6 d3d4 e5e4 f3h4 e8g8 h4f5 f8e8 b4b5 c6a5 a4a2 a8d8 f5e3 d8d7 e3d5 d7f7";
//
//        String[] moveList = moves.split(" ");
//        
//        for(String move : moveList){
//            chessController.makeMove(move);
//        }
//        
//        String results = chessController.makeMove("c3c4");
//        assertEquals("moveOk", results);
//        assertTrue(chessController.isLastMoveCastle());
//    }

}
