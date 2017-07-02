/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author david
 */
public class ChessTest {
    
    public ChessTest() {
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
     * Test of startGame method, of class Chess.
     */
    @Test
    public void testStartGame() {
//        System.out.println("startGame");
//        StartGame startGame = new StartGame(true, true);
//        Player chessState = new Player();
//        chessState.setTurn(Player.Color.Black); 
//        ArgumentCaptor<MessageHolder> argument = ArgumentCaptor.forClass(MessageHolder.class);
//        ChessMessageSender sender = mock(ChessMessageSender.class);
//        ChessControllerInterface chessEngine = mock(ChessControllerInterface.class); 
//        ChessMessageReceiver chessMessageReceiver = mock(ChessMessageReceiver.class);
//        
//        Chess instance = new Chess(chessEngine, sender, chessMessageReceiver);
//        instance.startGame(startGame);
//        
//        verify(sender, times(1)).send(argument.capture()); 
//        assertEquals(argument.getValue().getClassName(), RequestMove.class.getSimpleName()); // Sends out a RequestMove
//        assertEquals(Color.White, chessState.getTurn()); // Verify setting starting player to white
    }

    @Test
    public void testChessMovedAndWrongPlayer() throws Exception {
//        System.out.println("ChessMovedAndWrongPlayer");
//        ChessMove chessMove = new ChessMove(false, "e3-34");
//
//        Player chessState = new Player();
//        chessState.setWhiteTurn(true);
//        ArgumentCaptor<MessageHolder> argument = ArgumentCaptor.forClass(MessageHolder.class);
//        ChessMessageSender sender = mock(ChessMessageSender.class);
//        
//        
//        Chess instance = new Chess(null, chessState, sender);
//        instance.chessMoved(chessMove);
//
//        verify(sender, times(1)).send(argument.capture()); 
//        assertEquals(argument.getValue().getClassName(), RequestMove.class.getSimpleName()); // Sends out a RequestMove
//        assertEquals(true, chessState.isWhiteTurn()); // Verify setting starting player to white
    }
    
}
