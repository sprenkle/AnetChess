/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess;

import java.util.List;
import net.sprenkle.chess.imaging.Line;
import net.sprenkle.chess.models.DetectedObject;
import net.sprenkle.chess.models.GridObject;

/**
 *
 * @author david
 */
public interface ObjectDetectorInterface {

    public List<GridObject> detectObjectsWithinGrid(boolean[][] array, int xOffset, int yOffset, Line[] verticalLines, Line[] horizontalLines);

    public List<DetectedObject> detectObjects(boolean[][] array, int xOffset, int yOffset);

}
