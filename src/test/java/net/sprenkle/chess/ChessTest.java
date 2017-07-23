/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess;

import java.util.UUID;
import net.sprenkle.chess.messages.ChessMessageReceiver;
import net.sprenkle.chess.messages.ChessMessageSender;
import net.sprenkle.chess.messages.ChessMoveMsg;
import net.sprenkle.chess.messages.ChessMove;
import net.sprenkle.chess.messages.MessageHolder;
import net.sprenkle.chess.messages.RequestMove;
import net.sprenkle.chess.messages.RequestMovePieces;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Test
    public void testChessMovedInvalidUuid() {
        System.out.println("chessMoved");
        UUID uuid = UUID.randomUUID();
        ChessMove chessMove = new ChessMove(Player.White, "a2a3");
        ChessMoveMsg chessMoveMsg = new ChessMoveMsg(uuid, true, chessMove);
        ChessControllerInterface chessEngine = mock(ChessControllerInterface.class); 
        ChessMessageSender chessMessageSender = mock(ChessMessageSender.class);
        ChessMessageReceiver messageReceiver = mock(ChessMessageReceiver.class);
        
        Chess instance = new Chess(chessEngine, chessMessageSender, messageReceiver);
        instance.setExpectedMove(UUID.randomUUID());
        instance.chessMoved(chessMoveMsg);
        verify(chessMessageSender, times(0)).send(any(MessageHolder.class));
    }
    
    @Test
    public void testChessMovedOutOfTurn() {
        System.out.println("chessMoved");
        UUID uuid = UUID.randomUUID();
        ChessMove chessMove = new ChessMove(Player.White, "a2a3");
        ChessMoveMsg chessMoveMsg = new ChessMoveMsg(uuid, true, chessMove);
        ChessControllerInterface chessEngine = mock(ChessControllerInterface.class); 
        ChessMessageSender chessMessageSender = mock(ChessMessageSender.class);
        ArgumentCaptor<MessageHolder> captor = ArgumentCaptor.forClass(MessageHolder.class);

        ChessMessageReceiver messageReceiver = mock(ChessMessageReceiver.class);
        Chess instance = new Chess(chessEngine, chessMessageSender, messageReceiver);
        ChessState chessState = new ChessState();
        chessState.setTurn(Player.Black);
        instance.setChessState(chessState);
        instance.setExpectedMove(uuid);
        instance.chessMoved(chessMoveMsg);

        verify(chessMessageSender).send(captor.capture()); 
        verify(chessMessageSender, times(1)).send(any(MessageHolder.class));


        RequestMove requestMove = (RequestMove) captor.getValue().getObject(RequestMove.class);
        assertTrue("Verify it send the same player turn",Player.Black.equals(requestMove.getTurn()));
    }

    
    @Test
    public void testChessMovedRobot() {
        System.out.println("chessMoved");
        UUID uuid = UUID.randomUUID();
        ChessMove chessMove = new ChessMove(Player.White, "a2a3");
        ChessMoveMsg chessMoveMsg = new ChessMoveMsg(uuid, true, chessMove);
        ChessControllerInterface chessEngine = mock(ChessControllerInterface.class);
        when(chessEngine.makeMove(any(String.class))).thenReturn("moveOk");
        ChessMessageSender chessMessageSender = mock(ChessMessageSender.class);
        ArgumentCaptor<MessageHolder> captor = ArgumentCaptor.forClass(MessageHolder.class);
        ChessMessageReceiver messageReceiver = mock(ChessMessageReceiver.class);
        Chess instance = new Chess(chessEngine, chessMessageSender, messageReceiver);
        ChessState chessState = new ChessState();
        chessState.setTurn(Player.White);
        chessState.setWhiteRobot(true);
        instance.setChessState(chessState);
        instance.setExpectedMove(uuid);
        instance.chessMoved(chessMoveMsg);
        verify(chessMessageSender).send(captor.capture()); 
        verify(chessMessageSender, times(1)).send(any(MessageHolder.class));
        RequestMovePieces requestMovePieces = (RequestMovePieces) captor.getValue().getObject(RequestMovePieces.class);
        assertEquals("Verify a requestMovePiece is made with correct move.", chessMove.getMove(), requestMovePieces.getChessMove().getMove());
        assertEquals("Verify a requestMovePiece is made with correct turn.", chessMove.getTurn(), requestMovePieces.getChessMove().getTurn());
    }

    @Test
    public void testChessMovedHuman() {
        System.out.println("chessMoved");
        UUID uuid = UUID.randomUUID();
        ChessMove chessMove = new ChessMove(Player.White, "a2a3");
        ChessMoveMsg chessMoveMsg = new ChessMoveMsg(uuid, false, chessMove);
        ChessControllerInterface chessEngine = mock(ChessControllerInterface.class);
        when(chessEngine.makeMove(any(String.class))).thenReturn("moveOk");
        ChessMessageSender chessMessageSender = mock(ChessMessageSender.class);
        ArgumentCaptor<MessageHolder> captor = ArgumentCaptor.forClass(MessageHolder.class);
        ChessMessageReceiver messageReceiver = mock(ChessMessageReceiver.class);
        Chess instance = new Chess(chessEngine, chessMessageSender, messageReceiver);
        ChessState chessState = new ChessState();
        chessState.setTurn(Player.White);
        chessState.setWhiteRobot(false);
        chessState.setBlackRobot(true);
        instance.setChessState(chessState);
        instance.setExpectedMove(uuid);
        instance.chessMoved(chessMoveMsg);
        verify(chessMessageSender).send(captor.capture()); 
        verify(chessMessageSender, times(1)).send(any(MessageHolder.class));
        RequestMove requestMove = (RequestMove) captor.getValue().getObject(RequestMove.class);
        assertTrue("Verify it send the same player turn",Player.Black.equals(requestMove.getTurn()));
        assertEquals(true, requestMove.isRobot());
    }

    
//    @Test
//    public void testMain() throws Exception {
//        System.out.println("main");
//        String[] args = null;
//        Chess.main(args);
//        fail("The test case is a prototype.");
//    }
//
//    @Test
//    public void testBoardStatus() {
//        System.out.println("boardStatus");
//        BoardStatus boardStatus = null;
//        Chess instance = null;
//        instance.boardStatus(boardStatus);
//        fail("The test case is a prototype.");
//    }
//
//    @Test
//    public void testConfirmedPieceMove() {
//        System.out.println("confirmedPieceMove");
//        ConfirmedPieceMove confirmedPieceMove = null;
//        Chess instance = null;
//        instance.confirmedPieceMove(confirmedPieceMove);
//        fail("The test case is a prototype.");
//    }
//
//    @Test
//    public void testRun() {
//        System.out.println("run");
//        Chess instance = null;
//        instance.run();
//        fail("The test case is a prototype.");
//    }
    
}
