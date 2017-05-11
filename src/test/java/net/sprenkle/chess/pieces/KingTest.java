/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.pieces;

import java.util.ArrayList;
import net.sprenkle.chess.exceptions.InvalidLocationException;
import net.sprenkle.chess.exceptions.InvalidMoveException;
import net.sprenkle.chess.messages.ChessMessageSender;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 *
 * @author david
 */
public class KingTest {
    
    public KingTest() {
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


    /**
     * Test of isValidMoveTo method, of class King.
     */
    @Test
    public void testIsValidMoveTo() throws InvalidLocationException, InvalidMoveException {
        System.out.println("isValidMoveTo");
        Board board = new Board();
        board.setStartingPositionBoard();
        board.makeMove("e2e4");
        board.makeMove("e7e6");
        board.makeMove("d2d4");
        board.makeMove("d7d5");
        board.makeMove("b1c3");
        board.makeMove("f8b4");
        board.makeMove("e4d5");
        board.makeMove("e6d5");
        board.makeMove("g1f3");
        board.makeMove("g8f6");
        board.makeMove("f1d3");
        //board.makeMove("e8g8");

        
        PieceLocation location = new PieceLocation(6,7);
        King instance = new King(Board.WHITE, 4, 0);
        boolean expResult = false;

        boolean result = instance.isValidMoveTo(board, location);
        assertEquals(expResult, result);
    }
    
        @Test
    public void testPawnPomotion() throws InvalidMoveException{
        Board board = new Board();
        board.setStartingPositionBoard();
        String moves = "e2e4 e7e6 b1c3 c7c5 a2a3 d7d5 f1b5 b8c6 d2d4 g8f6 g1e2 f6e4 b5c6 b7c6 e1g1 e4c3 e2c3 c5d4 d1d4 f7f6 d4d1 f8d6 c3a4 d8e7 f1e1 e8g8 c1e3 e7c7 e3c5 d6h2 g1h1 f8f7 c2c3 h2d6 c3c4 d6e5 a1c1 a8b8 b2b3 e5f4 c4d5 c6d5 c1c2 c7b7 h1g1 c8d7 g2g3 f4c7 c5b4 d5d4 a4c5 b7d5 c5a6 d7c6 c2c6 d5c6 a6b8 c7b8 d1d4 b8c7 d4c4 c6c4 b3c4 e6e5 g1f1 a7a5 b4c5 c7d8 e1b1 d8c7 b1e1 f6f5 e1b1 f5f4 f1g2 g7g5 g3f4 e5f4 c5b6 g5g4 c4c5 h7h5 f2f3 g8h7 a3a4 h7h6 b1b2 h5h4 b6c7 g4f3 g2f2 f7c7 f2f3 h6g6 b2b5 h4h3 c5c6 c7c6 f3g4 h3h2 b5b1 c6c4 b1h1 c4a4 h1h2 a4c4 h2b2 g6f6 g4h5 f4f3 b2b6 f6e5 b6b1 c4f4 b1b5 e5e4 h5h6 f4f6 h6h7 f3f2 b5a5 f6f7 h7g6";
        String[] move = moves.split(" ");
        for(int i = 0 ; i < move.length; i++){
            board.makeMove(move[i]);
        }
        String lastMove = "f2f1q";
        boolean result = board.makeMove(lastMove);
        assertEquals(true, result);
    }

}
