/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess;

import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.List;
import net.sprenkle.chess.imaging.Line;
import net.sprenkle.chess.models.DetectedObject;
import net.sprenkle.chess.models.GridObject;
import org.apache.log4j.Logger;

/**
 *
 * @author david
 */
public class ObjectDetector implements ObjectDetectorInterface {

    static Logger logger = Logger.getLogger(ObjectDetector.class.getSimpleName());

    /**
     * Used to detect objects within a grid defined by the intersection of lines.
     * @param array
     * @param xOffset 
     * @param yOffset
     * @param verticalLines
     * @param horizontalLines
     * @return 
     */
    @Override
    public List<GridObject> detectObjectsWithinGrid(boolean[][] array, int xOffset, int yOffset, Line[] verticalLines, Line[] horizontalLines) {
        List<DetectedObject> pieces = detectObjects(array, xOffset, yOffset);
        GridObject[][] tempPiecePositions = new GridObject[verticalLines.length][horizontalLines.length];
        List<GridObject> duplicates = new ArrayList<>();

        List<GridObject> gridObjects = new ArrayList<>();
        // Get offset
        pieces.forEach((piece) -> {
            gridObjects.add(findOffFactor(piece, verticalLines, horizontalLines));
        });
        gridObjects.sort((t1, t2) -> Double.compare(t1.getOffset(), t2.getOffset()));

        for (GridObject piece : gridObjects) {
            if (tempPiecePositions[piece.getCol()][piece.getRow()] != null) {
                logger.debug(String.format("Duplicate Piece %s,%s has offset of %s", piece.getCol(), piece.getRow(), piece.getOffset()));
                duplicates.add(piece);
                continue;
            }
            tempPiecePositions[piece.getCol()][piece.getRow()] = piece;
        }

        duplicates.forEach((piece) -> {
            gridObjects.remove(piece);
        });

        return gridObjects;
    }


    
    /**
     * Used to find all objects within a defined array
     * @param array
     * @param xOffset
     * @param yOffset
     * @return 
     */
    @Override
    public List<DetectedObject> detectObjects(boolean[][] array, int xOffset, int yOffset) {
        ArrayList<DetectedObject> pieces = new ArrayList<>();
        for (int y = 6; y < array[0].length - 6; y++) {
//                        logger.debug(String.format("y=%s",  y));
            for (int x = 6; x < array.length - 6; x++) {
                try {
                    if (detectC(array, x, y, true)) {
                        DetectedObject piece = new DetectedObject(x + xOffset, y + yOffset, Player.White);
                        pieces.add(piece);
                        array[x][y] = true; // sets the color so it will not be detected again
                        array[x + 1][y] = true;
                        array[x][y + 1] = true;
                        array[x + 1][y + 1] = true;
                        x += 15;
                    } else if (detectC(array, x, y, false)) {
                        //                      logger.debug(String.format("x=%s y=%s", x, y));
                        DetectedObject piece = new DetectedObject(x + xOffset, y + yOffset, Player.Black);
                        pieces.add(piece);
                        array[x][y] = false;
                        array[x + 1][y] = false;
                        array[x + 1][y] = false;
                        array[x + 1][y + 1] = false;
                        x += 15;
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        }
        return pieces;
    }

    /**
     * How far off from grid lines are points
     *
     * @param x
     * @param y
     * @return
     */
    private GridObject findOffFactor(DetectedObject piece, Line[] verticalLines, Line[] horizontalLines) {
        GridObject gridObject = new GridObject(piece);
        int xminDiff = 100000;
        int yminDiff = 100000;
        for (int i = 0; i < 8; i++) {
            int xdiff = verticalLines[i].start.x - piece.getX();
            if (abs(xdiff) < xminDiff) {
                xminDiff = abs(xdiff);
                gridObject.setCol(i);
            }

            int ydiff = horizontalLines[i].start.y - piece.getY();
            if (ydiff < abs(yminDiff)) {
                yminDiff = abs(ydiff);
                gridObject.setRow(i);
            }
        }
        gridObject.setxOffFactor(xminDiff);
        gridObject.setyOffFactor(yminDiff);
        return gridObject;
    }

    private boolean detectC(boolean[][] array, int startX, int startY, boolean piece) {
        int MAX_Distance = 3; // was 5
        int top;
        int bottom;
        int left;
        int right;

        // Check to make sure the nodle mark is the circle color
        if (array[startX][startY] == piece) {
            return false;
        }

        // Find top of circle
        int y = startY - 1;
        while (array[startX][y] != piece && y > 0) {
            y--;
        }

        // Checks that 3 pixels out from top are non circle color
        if (y >= 3 && (array[startX][y - 1] != piece || array[startX][y - 2] != piece || array[startX][y - 3] != piece)) {
            return false;
        }

        // Checks that length of mid to top is less than MAX_Distance
        if (startY - y < MAX_Distance) {
            //System.out.format("startY - y < 5  startY=%s y=%s\n", startY, y);
            return false;
        }

        int t = y;
        top = y;
        int yUp = startY - y;
        // Find bottom of circle
        y = startY + 1;
        while (array[startX][y] != piece && y < array[0].length - 1) {
            y++;
        }
        // Check that length of mid to bottom are non circle color
        if (y <= array[0].length - 4 && (array[startX][y + 1] != piece || array[startX][y + 2] != piece || array[startX][y + 3] != piece)) {
            return false;
        }
        // Checks that the length of mid to bottom is less than MAX_Distance
        if (y - startY < MAX_Distance) {
            //System.out.format("y - startY < 5  startY=%s y=%s\n", startY, y);
            return false;
        }

        int b = y;
        int yDown = y - startY;
        bottom = y;
        // returns false if not in the middle of the circle
        if (Math.abs(yUp - yDown) > 1) {
            //System.out.format("Math.abs(yUp - yDown) > 3  yUp=%s yDown=%s\n", yUp, yDown);
            return false;
        }

        // Find left of circle
        int x = startX - 1;
        while (array[x][startY] != piece && x > 0) {
            x--;
        }
        // Checks that the mid to left is less than max distance
        if (startX - x < MAX_Distance) {
            return false;
        }

        // Find the right of the circle
        int l = x;
        int xLeft = startX - x;
        left = x;
        x = startX + 1;
        while (array[x][startY] != piece && x < array.length - 1) {
            x++;
        }
        // Checks that the mid to right is less than max distance
        if (x - startX < MAX_Distance) {
            return false;
        }
        int r = x;
        int xRight = x - startX;
        right = x;
        // returns false if not in the middle of the circle
        if (Math.abs(xLeft - xRight) > 1) {
            return false;
        }

        if (array[l][t] != piece || array[r][t] != piece || array[l][b] != piece || array[r][b] != piece) {
            return false;
        }

        if (array[l + 1][t] != piece || array[r - 1][t] != piece || array[l + 1][b] != piece || array[r - 1][b] != piece) {
            return false;
        }

        if (array[l][t + 1] != piece || array[r][t + 1] != piece || array[l][b - 1] != piece || array[r][b - 1] != piece) {
            return false;
        }

        if (array[l + 1][t + 1] != piece || array[r - 1][t + 1] != piece || array[l + 1][b - 1] != piece || array[r - 1][b - 1] != piece) {
            return false;
        }
        //System.out.format("height=%s Width=%s\n", b - t, r - l);
        if (b - t < 8 || b - t > 26 || r - l < 8 || r - l > 26) {
            return false;
        }

        x = startX + 1;
        y = startY + 1;
        int rs = 0;
        while (array[x][y] != piece && x < array.length - 1 && y < array.length - 1) {
            x++;
            y++;
            rs++;
        }
        // x, y = first non circle square down right
        //    System.out.format("%s %s %s %s\n", x, r, y, b);
        for (int tx = x; tx < r; tx++) {
            for (int ty = y; ty < b; ty++) {
                if (array[tx][ty] != piece) {
//                    return false;
                }
            }
        }

        x = startX - 1;
        y = startY - 1;
        int rs2 = 0;
        while (array[x][y] != piece && x > 0 && y > 0) {
            x--;
            y--;
            rs2++;
        }
        // x, y = first non circle square left up
        if (true == true) {
            return true;
        }

        for (int tx = x; tx > l; tx--) {
            for (int ty = y; ty > t; ty--) {
                if (array[tx][ty] != piece) {
//                    return false;
                }
            }
        }
////////////////
        if (Math.abs(rs - rs2) > 2) {
            return false;
        }

        rs = rs + rs2;

        x = startX - 1;
        y = startY + 1;
        int ls = 0;
        while (array[x][y] != piece && x > 0 && y < array.length - 1) {
            x--;
            y++;
            ls++;
        }

        for (int tx = x; tx > startX + x; tx++) {
            for (int ty = startY; ty < startY + y; ty++) {
                if (array[x][y] != piece) {
                    return false;
                }
            }
        }

        x = startX + 1;
        y = startY - 1;
        int ls2 = 0;
        while (array[x][y] != piece && x < array.length - 1 && y > 0) {
            x++;
            y--;
            ls2++;
        }

        if (Math.abs(ls - ls2) > 3) {
            return false;
        }

        ls = ls + ls2;

        if (Math.abs(rs - ls) > 2) {
            return false;
        }

        // Get ratio of black to white squares
        for (int tx = x; tx > startX + x; tx++) {
            for (int ty = startY; ty < startY + y; ty++) {
                if (array[x][y] != piece) {
                    return false;
                }
            }
        }

        return true;
    }

}
