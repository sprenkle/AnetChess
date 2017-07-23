/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.imaging;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import net.sprenkle.chess.BoardProperties;
import net.sprenkle.chess.PossiblePiece;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author david
 */
public class BoardCalculatorTest {
    
    public BoardCalculatorTest() {
    }

    @Test
    public void testDetectBoardMarker() throws IOException {
        BufferedImage bi = ImageUtil.loadImage("D:\\git\\Chess\\images\\unitTestImages\\board11bef2ab-6b9e-4813-8ac3-6b86d793e006.png");
        BoardCalculator bc = new BoardCalculator(new BoardProperties());
        ArrayList<PossiblePiece> list = bc.detectBoardMarker(bi);
        assertEquals(1, list.size());
    }
    
}
