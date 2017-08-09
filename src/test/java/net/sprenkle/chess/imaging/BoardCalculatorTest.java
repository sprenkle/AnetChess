/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.imaging;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import net.sprenkle.chess.BoardProperties;
import net.sprenkle.chess.messages.ChessMessageSender;
import net.sprenkle.chess.models.DetectedObject;
import net.sprenkle.chess.models.PossiblePiece;
import org.junit.Test;

import static org.junit.Assert.*;
import org.junit.Ignore;
import static org.mockito.Mockito.mock;

/**
 *
 * @author david
 */
public class BoardCalculatorTest {
    
    public BoardCalculatorTest() {
    }

    @Test
    @Ignore("Need to take a look at this again.")
    public void testDetectBoardMarker() throws IOException {
        BufferedImage bi = ImageUtil.loadImage("D:\\git\\Chess\\images\\unitTestImages\\board11bef2ab-6b9e-4813-8ac3-6b86d793e006.png");
        BoardCalculator bc = new BoardCalculator(new BoardProperties(), mock(ChessMessageSender.class));
        List<DetectedObject> list = bc.detectBoardMarker(bi);
        assertEquals(1, list.size());
    }
    
}
