/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import net.sprenkle.chess.imaging.BlackWhite;
import net.sprenkle.chess.imaging.ImageUtil;
import net.sprenkle.chess.imaging.Line;
import net.sprenkle.chess.models.DetectedObject;
import net.sprenkle.chess.models.GridObject;
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
public class ObjectDetectorIT {
    
    public ObjectDetectorIT() {
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
     * Test of detectObjectsWithinGrid method, of class ObjectDetector.
     */
    @Test
    public void testDetectObjectsWithinGrid() {
        System.out.println("detectObjectsWithinGrid");
        boolean[][] array = null;
        int xOffset = 0;
        int yOffset = 0;
        Line[] verticalLines = null;
        Line[] horizontalLines = null;
        ObjectDetector instance = new ObjectDetector();
        List<GridObject> expResult = null;
        List<GridObject> result = instance.detectObjectsWithinGrid(array, xOffset, yOffset, verticalLines, horizontalLines);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of detectObjects method, of class ObjectDetector.
     */
    @Test
    public void testDetectObjects() throws IOException {
        System.out.println("detectObjects");
        int xOffset = 0;
        int yOffset = 0;
        ObjectDetector instance = new ObjectDetector();
        List<DetectedObject> expResult = null;
        
        int bottomBoard=450;
        int topBoard=3;
        int leftBoard=187;
        int rightBoard=634;
        int threshHold = 160;
        
        BufferedImage bi = ImageUtil.loadImage("D:\\git\\Chess\\images\\unitTestImages\\241cbfc9-0433-4651-9b7f-b5b94a328458.png");
        boolean[][] array = BlackWhite.convert(bi.getSubimage(leftBoard, topBoard, rightBoard - leftBoard, bottomBoard - topBoard), threshHold);

        List<DetectedObject> result = instance.detectObjects(array, xOffset, yOffset);
        assertEquals(32, result.size());
    }
    
}
