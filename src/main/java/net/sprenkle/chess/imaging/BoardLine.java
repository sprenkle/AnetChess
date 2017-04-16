/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.imaging;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

/**
 *
 * @author David
 */
public class BoardLine {

    private Point startLine;
    private int count = 0;
    private int lastWhite;
    private double lastGap = 0;
    private int startGaps = -1;
    private int numGaps = 0;
    private boolean foundChessLine = false;
    private int finalStart;
    private int finalEnd;
    private int finalGaps;
    private final int numberSquares = 7;

    private final ArrayList<int[]> gapList = new ArrayList<>();

    public BoardLine(Point startLine) {
        this.startLine = startLine;
        lastWhite = -1;
    }

    public void addPixel(int rgb) {
        if(numGaps > numberSquares - 1) return;
        Color color = new Color(rgb);

        if (color.getRed() > 100 && color.getGreen() > 100 && color.getBlue() > 100) {
            int gap = count - lastWhite;
            if (startGaps == -1) {
                startGaps = count;
            }
            if (gap > 10 && gap < 100) {
                if (gap > lastGap * .8 && gap < lastGap * 1.2) {
                    numGaps++;

                    if (numGaps > 5 && numGaps <= numberSquares) {
                        foundChessLine = true;
                        finalStart = startGaps;
                        finalEnd = count + 1;
                    }
                } else {
                   // if (startGaps != count && lastGap != 0) {
                        startGaps = count - gap;
                        numGaps = 0;
                 //   }
                }
                lastGap = gap;
            } else {
                startGaps = count;
                numGaps = 0;
            }
            lastWhite = count;
        }
        count++;
    }

    public boolean foundLine() {
        return foundChessLine;
    }

    public int getStart() {
        return finalStart;
    }

    public int getEnd() {
        return finalEnd;
    }

    public void finished() {

    }
}
