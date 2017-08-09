/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.imaging;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import net.sprenkle.chess.BoardProperties;
import net.sprenkle.chess.Player;
import net.sprenkle.chess.RabbitConfiguration;
import net.sprenkle.chess.models.PossiblePiece;
import net.sprenkle.chess.messages.KnownBoardPositions;
import net.sprenkle.chess.messages.MessageHolder;
import net.sprenkle.chess.messages.RMQChessMessageSender;
import net.sprenkle.chess.messages.RabbitConfigurationInterface;
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
public class BoardCalculatorIT {
    
    public BoardCalculatorIT() {
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
     * Test of verifyPiecePositions method, of class BoardCalculator.
     */
    @Test
    public void testVerifyPiecePositions() throws IOException {
        System.out.println("verifyPiecePositions");
        Gson gson = new GsonBuilder().create();

        String json = "{\"jsonObject\":\"{\\\"knownBoard\\\":[[{\\\"x\\\":0,\\\"y\\\":0,\\\"color\\\":{\\\"color\\\":\\\"White\\\"},\\\"xOffFactor\\\":0.0,\\\"yOffFactor\\\":0.0,\\\"col\\\":0,\\\"row\\\":0,\\\"rank\\\":3},{\\\"x\\\":0,\\\"y\\\":0,\\\"color\\\":{\\\"color\\\":\\\"White\\\"},\\\"xOffFactor\\\":0.0,\\\"yOffFactor\\\":0.0,\\\"col\\\":0,\\\"row\\\":1,\\\"rank\\\":0},null,null,null,null,{\\\"x\\\":0,\\\"y\\\":0,\\\"color\\\":{\\\"color\\\":\\\"Black\\\"},\\\"xOffFactor\\\":0.0,\\\"yOffFactor\\\":0.0,\\\"col\\\":0,\\\"row\\\":6,\\\"rank\\\":0},{\\\"x\\\":0,\\\"y\\\":0,\\\"color\\\":{\\\"color\\\":\\\"Black\\\"},\\\"xOffFactor\\\":0.0,\\\"yOffFactor\\\":0.0,\\\"col\\\":0,\\\"row\\\":7,\\\"rank\\\":3}],[{\\\"x\\\":0,\\\"y\\\":0,\\\"color\\\":{\\\"color\\\":\\\"White\\\"},\\\"xOffFactor\\\":0.0,\\\"yOffFactor\\\":0.0,\\\"col\\\":1,\\\"row\\\":0,\\\"rank\\\":1},{\\\"x\\\":0,\\\"y\\\":0,\\\"color\\\":{\\\"color\\\":\\\"White\\\"},\\\"xOffFactor\\\":0.0,\\\"yOffFactor\\\":0.0,\\\"col\\\":1,\\\"row\\\":1,\\\"rank\\\":0},null,null,null,null,{\\\"x\\\":0,\\\"y\\\":0,\\\"color\\\":{\\\"color\\\":\\\"Black\\\"},\\\"xOffFactor\\\":0.0,\\\"yOffFactor\\\":0.0,\\\"col\\\":1,\\\"row\\\":6,\\\"rank\\\":0},{\\\"x\\\":0,\\\"y\\\":0,\\\"color\\\":{\\\"color\\\":\\\"Black\\\"},\\\"xOffFactor\\\":0.0,\\\"yOffFactor\\\":0.0,\\\"col\\\":1,\\\"row\\\":7,\\\"rank\\\":1}],[{\\\"x\\\":0,\\\"y\\\":0,\\\"color\\\":{\\\"color\\\":\\\"White\\\"},\\\"xOffFactor\\\":0.0,\\\"yOffFactor\\\":0.0,\\\"col\\\":2,\\\"row\\\":0,\\\"rank\\\":2},{\\\"x\\\":0,\\\"y\\\":0,\\\"color\\\":{\\\"color\\\":\\\"White\\\"},\\\"xOffFactor\\\":0.0,\\\"yOffFactor\\\":0.0,\\\"col\\\":2,\\\"row\\\":1,\\\"rank\\\":0},null,null,null,null,{\\\"x\\\":0,\\\"y\\\":0,\\\"color\\\":{\\\"color\\\":\\\"Black\\\"},\\\"xOffFactor\\\":0.0,\\\"yOffFactor\\\":0.0,\\\"col\\\":2,\\\"row\\\":6,\\\"rank\\\":0},{\\\"x\\\":0,\\\"y\\\":0,\\\"color\\\":{\\\"color\\\":\\\"Black\\\"},\\\"xOffFactor\\\":0.0,\\\"yOffFactor\\\":0.0,\\\"col\\\":2,\\\"row\\\":7,\\\"rank\\\":2}],[{\\\"x\\\":0,\\\"y\\\":0,\\\"color\\\":{\\\"color\\\":\\\"White\\\"},\\\"xOffFactor\\\":0.0,\\\"yOffFactor\\\":0.0,\\\"col\\\":3,\\\"row\\\":0,\\\"rank\\\":5},{\\\"x\\\":0,\\\"y\\\":0,\\\"color\\\":{\\\"color\\\":\\\"White\\\"},\\\"xOffFactor\\\":0.0,\\\"yOffFactor\\\":0.0,\\\"col\\\":3,\\\"row\\\":1,\\\"rank\\\":0},null,null,null,null,{\\\"x\\\":0,\\\"y\\\":0,\\\"color\\\":{\\\"color\\\":\\\"Black\\\"},\\\"xOffFactor\\\":0.0,\\\"yOffFactor\\\":0.0,\\\"col\\\":3,\\\"row\\\":6,\\\"rank\\\":0},{\\\"x\\\":0,\\\"y\\\":0,\\\"color\\\":{\\\"color\\\":\\\"Black\\\"},\\\"xOffFactor\\\":0.0,\\\"yOffFactor\\\":0.0,\\\"col\\\":3,\\\"row\\\":7,\\\"rank\\\":5}],[{\\\"x\\\":0,\\\"y\\\":0,\\\"color\\\":{\\\"color\\\":\\\"White\\\"},\\\"xOffFactor\\\":0.0,\\\"yOffFactor\\\":0.0,\\\"col\\\":4,\\\"row\\\":0,\\\"rank\\\":4},{\\\"x\\\":0,\\\"y\\\":0,\\\"color\\\":{\\\"color\\\":\\\"White\\\"},\\\"xOffFactor\\\":0.0,\\\"yOffFactor\\\":0.0,\\\"col\\\":4,\\\"row\\\":1,\\\"rank\\\":0},null,null,null,null,{\\\"x\\\":0,\\\"y\\\":0,\\\"color\\\":{\\\"color\\\":\\\"Black\\\"},\\\"xOffFactor\\\":0.0,\\\"yOffFactor\\\":0.0,\\\"col\\\":4,\\\"row\\\":6,\\\"rank\\\":0},{\\\"x\\\":0,\\\"y\\\":0,\\\"color\\\":{\\\"color\\\":\\\"Black\\\"},\\\"xOffFactor\\\":0.0,\\\"yOffFactor\\\":0.0,\\\"col\\\":4,\\\"row\\\":7,\\\"rank\\\":4}],[{\\\"x\\\":0,\\\"y\\\":0,\\\"color\\\":{\\\"color\\\":\\\"White\\\"},\\\"xOffFactor\\\":0.0,\\\"yOffFactor\\\":0.0,\\\"col\\\":5,\\\"row\\\":0,\\\"rank\\\":2},{\\\"x\\\":0,\\\"y\\\":0,\\\"color\\\":{\\\"color\\\":\\\"White\\\"},\\\"xOffFactor\\\":0.0,\\\"yOffFactor\\\":0.0,\\\"col\\\":5,\\\"row\\\":1,\\\"rank\\\":0},null,null,null,null,{\\\"x\\\":0,\\\"y\\\":0,\\\"color\\\":{\\\"color\\\":\\\"Black\\\"},\\\"xOffFactor\\\":0.0,\\\"yOffFactor\\\":0.0,\\\"col\\\":5,\\\"row\\\":6,\\\"rank\\\":0},{\\\"x\\\":0,\\\"y\\\":0,\\\"color\\\":{\\\"color\\\":\\\"Black\\\"},\\\"xOffFactor\\\":0.0,\\\"yOffFactor\\\":0.0,\\\"col\\\":5,\\\"row\\\":7,\\\"rank\\\":2}],[{\\\"x\\\":0,\\\"y\\\":0,\\\"color\\\":{\\\"color\\\":\\\"White\\\"},\\\"xOffFactor\\\":0.0,\\\"yOffFactor\\\":0.0,\\\"col\\\":6,\\\"row\\\":0,\\\"rank\\\":1},{\\\"x\\\":0,\\\"y\\\":0,\\\"color\\\":{\\\"color\\\":\\\"White\\\"},\\\"xOffFactor\\\":0.0,\\\"yOffFactor\\\":0.0,\\\"col\\\":6,\\\"row\\\":1,\\\"rank\\\":0},null,null,null,null,{\\\"x\\\":0,\\\"y\\\":0,\\\"color\\\":{\\\"color\\\":\\\"Black\\\"},\\\"xOffFactor\\\":0.0,\\\"yOffFactor\\\":0.0,\\\"col\\\":6,\\\"row\\\":6,\\\"rank\\\":0},{\\\"x\\\":0,\\\"y\\\":0,\\\"color\\\":{\\\"color\\\":\\\"Black\\\"},\\\"xOffFactor\\\":0.0,\\\"yOffFactor\\\":0.0,\\\"col\\\":6,\\\"row\\\":7,\\\"rank\\\":1}],[{\\\"x\\\":0,\\\"y\\\":0,\\\"color\\\":{\\\"color\\\":\\\"White\\\"},\\\"xOffFactor\\\":0.0,\\\"yOffFactor\\\":0.0,\\\"col\\\":7,\\\"row\\\":0,\\\"rank\\\":3},{\\\"x\\\":0,\\\"y\\\":0,\\\"color\\\":{\\\"color\\\":\\\"White\\\"},\\\"xOffFactor\\\":0.0,\\\"yOffFactor\\\":0.0,\\\"col\\\":7,\\\"row\\\":1,\\\"rank\\\":0},null,null,null,null,{\\\"x\\\":0,\\\"y\\\":0,\\\"color\\\":{\\\"color\\\":\\\"Black\\\"},\\\"xOffFactor\\\":0.0,\\\"yOffFactor\\\":0.0,\\\"col\\\":7,\\\"row\\\":6,\\\"rank\\\":0},{\\\"x\\\":0,\\\"y\\\":0,\\\"color\\\":{\\\"color\\\":\\\"Black\\\"},\\\"xOffFactor\\\":0.0,\\\"yOffFactor\\\":0.0,\\\"col\\\":7,\\\"row\\\":7,\\\"rank\\\":3}]]}\",\"className\":\"net.sprenkle.chess.messages.KnownBoardPositions\",\"id\":\"4f2dedfd-7520-463f-9030-f75e2d0176ee\"}";
        MessageHolder messageHolder = (MessageHolder) gson.fromJson(json, MessageHolder.class);
        KnownBoardPositions knownBoardPositions = (KnownBoardPositions) messageHolder.getObject();       

        BufferedImage bi = ImageUtil.loadImage("D:\\git\\Chess\\images\\unitTestImages\\startingposition.png");
        PossiblePiece[][] knownBoard = knownBoardPositions.getKnownPostions();
        BoardCalculator instance = new BoardCalculator(new BoardProperties(), new RMQChessMessageSender("tes",mock(RabbitConfigurationInterface.class)));
        
        // Test if image and knownboard are equal
        boolean result = instance.verifyPiecePositions(bi, knownBoard);
        assertEquals("Piece from image and knownboard should be equal.", true, result); 

        // Test if knownboard is missing a peice image has
        PossiblePiece removedPiece = knownBoard[0][0];
        knownBoard[0][0] = null;
        result = instance.verifyPiecePositions(bi, knownBoard);
        assertEquals("Piece from image and knownboard should not be equal.", false, result); 

        // Test if image is missing a piece knownBoard has
        knownBoard[0][0] = removedPiece;
        knownBoard[3][3] = removedPiece;
        result = instance.verifyPiecePositions(bi, knownBoard);
        assertEquals("Piece from image and knownboard should not be equal.", false, result); 

    }
    
}
