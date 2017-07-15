/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess;

import java.util.UUID;
import net.sprenkle.chess.messages.ChessMessageReceiver;
import net.sprenkle.chess.messages.ChessMessageSender;
import net.sprenkle.chess.messages.RequestMove;
import net.sprenkle.chess.messages.StartGame;
import net.sprenkle.messages.MessageHolder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @author david
 */
public class RobotMoverTest {
    
    public RobotMoverTest() {
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

    @Test
    public void testStartGame() {
        // Verify start of Chess engine is started
        System.out.println("startGame");
        StartGame startGame = new StartGame(true, false);
        UCIInterface uci = mock(UCIInterface.class); 
        ChessMessageSender messageSender = mock(ChessMessageSender.class); 
        ChessMessageReceiver messageReceiver = mock(ChessMessageReceiver.class);
        RobotMover instance = new RobotMover(uci, messageSender, messageReceiver);
        instance.startGame(startGame);
        verify(messageSender, times(0)).send(any(MessageHolder.class));
        verify(uci, times(1)).sendCommand("ucinewgame");
        verify(uci, times(2)).sendCommandAndWait("isready", "readyok");
    }

    @Test
    public void testRequestMoveForHuman() throws Exception {
        // Verify start of Chess engine is started
        System.out.println("testRequestMoveForHuman");
        UUID uuid = UUID.randomUUID();
        String command = "go wtime 300000 btime 300000 winc 0 binc 0";
        RequestMove request = new RequestMove(Player.Black, true, "a1a2", uuid);
        UCIInterface uci = mock(UCIInterface.class);
        
        when(uci.sendCommandAndWait(command, "bestmove")).thenReturn("bestmove a2a3");
        
        ChessMessageSender messageSender = mock(ChessMessageSender.class); 
        ChessMessageReceiver messageReceiver = mock(ChessMessageReceiver.class);
        RobotMover instance = new RobotMover(uci, messageSender, messageReceiver);
        instance.requestMove(request);

        verify(messageSender, times(1)).send(any(MessageHolder.class));
        verify(uci, times(1)).sendCommandAndWait(command, "bestmove");
    }

    @Test
    public void testRequestMoveForRobot() throws Exception {
        // Verify start of Chess engine is started
        System.out.println("testRequestMoveForRobot");
        UUID uuid = UUID.randomUUID();
        String command = "go wtime 300000 btime 300000 winc 0 binc 0";
        RequestMove request = new RequestMove(Player.White, false, "a1a2", uuid);
        UCIInterface uci = mock(UCIInterface.class);
        
        when(uci.sendCommandAndWait(command, "bestmove")).thenReturn("bestmove a2a3");
        
        ChessMessageSender messageSender = mock(ChessMessageSender.class); 
        ChessMessageReceiver messageReceiver = mock(ChessMessageReceiver.class);
        RobotMover instance = new RobotMover(uci, messageSender, messageReceiver);
        instance.requestMove(request);

        verify(messageSender, times(0)).send(any(MessageHolder.class));
    }
}
