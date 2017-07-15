/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.controllers;

import java.util.UUID;
import net.sprenkle.chess.Player;
import net.sprenkle.chess.PossiblePiece;
import net.sprenkle.chess.imaging.BoardCalculator;
import net.sprenkle.chess.messages.BoardImage;
import net.sprenkle.chess.messages.PiecePositions;
import net.sprenkle.chess.messages.RequestPiecePositions;
import net.sprenkle.chess.messages.ChessMove;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author david
 */
public class PiecePositionsIdentifierIT {
    
    public PiecePositionsIdentifierIT() {
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
     * Test of processImage method, of class PiecePositionsIdentifier.
     */
    @Test
    public void testProcessImageForCapture() throws Exception {
        System.out.println("processImage");
        BoardImage boardImage = mock(BoardImage.class);
        BoardCalculator boardCalculator = mock(BoardCalculator.class);
        PossiblePiece[][] knownBoard = new PossiblePiece[8][8];
        knownBoard[3][4] = new PossiblePiece(0,0,Player.White);
        knownBoard[4][3] = new PossiblePiece(0,0,Player.Black);
        when(boardCalculator.getKnownBoard()).thenReturn(knownBoard);

        RequestPiecePositions requestPiecePositions = new RequestPiecePositions(new ChessMove(Player.White, "e5d4"), false, UUID.randomUUID());
        
        
        PiecePositionsIdentifier instance = new PiecePositionsIdentifier();
        PiecePositions expResult = null;
        PiecePositions result = instance.processImage(boardImage, boardCalculator, requestPiecePositions);
        
        
        assertEquals("Capture piece should have 2 moves.", 2, result.getMoveList().size());
    }
}
