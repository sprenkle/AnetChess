/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.imaging;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import static java.lang.Math.abs;
import java.util.ArrayList;
import net.sprenkle.chess.Chess;
import net.sprenkle.chess.ImageUtil;
import net.sprenkle.chess.PossiblePiece;
import net.sprenkle.imageutils.BlackWhite;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.log4j.Logger;

/**
 *
 * @author david
 */
public class BoardCalculator {

    static Logger logger = Logger.getLogger(BoardCalculator.class.getSimpleName());
    static Line[] horizontalLines = new Line[8];
    static Line[] verticalLines = new Line[8];
    static PossiblePiece[][] lastBoard = new PossiblePiece[8][8];
    private boolean initialized = false;
    static int leftBoard;
    static int rightBoard;
    static int bottomBoard;
    static int topBoard;
    static boolean humanColor;

    static int lastNumPieces = 16;
    
    static double xSlope = -0.4262;
    static double ySlope = 0.4271;
    static double xIntercept = 175.376;
    static double yIntercept = -165.4933;

    ArrayList<Point> detectedPices = new ArrayList<>();
    //   Line horizontal[] = new Line[8];
    //   Line vertical[] = new Line[8];

    public BoardCalculator() {
    }

    public void parseBI(BufferedImage bi) {

    }

    public boolean getHumanColor() {
        return humanColor;
    }

    public void showCircles(BufferedImage bi){
        boolean[][] array = BlackWhite.convert(bi);
        Graphics2D g2 = bi.createGraphics();
        BasicStroke stroke = new BasicStroke(2);
        g2.setStroke(stroke);

       ArrayList<PossiblePiece> pieces = detectCircle(array);
       pieces.forEach((piece) -> markPiece(g2, piece.x, piece.y, piece.color));
    }
    
    public ArrayList<PossiblePiece> detectCircle(boolean[][] array) {
        ArrayList<PossiblePiece> pieces = new ArrayList<>();

        for (int y = 6; y < array[0].length - 6; y++) {
            for (int x = 6; x < array.length - 6; x++) {
                try {
                    if (detectC(array, x, y, true)) {
                        PossiblePiece piece = new PossiblePiece(x, y, true);
                        pieces.add(piece);
                        array[x][y] = true; // sets the color so it will not be detected again
                        array[x + 1][y] = true;
                        array[x][y + 1] = true;
                        array[x + 1][y + 1] = true;
                        x += 15;
                    } else if (detectC(array, x, y, false)) {
                        PossiblePiece piece = new PossiblePiece(x, y, false);
                        pieces.add(piece);
                        array[x][y] = false;
                        array[x + 1][y] = false;
                        array[x + 1][y] = false;
                        array[x + 1][y + 1] = false;
                        x += 15;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return pieces;
    }

    public void initialLines(BufferedImage bi) {
        Graphics2D g2 = bi.createGraphics();

        BasicStroke stroke = new BasicStroke(2);
        g2.setStroke(stroke);
        if (!isInitialized()) {
            //      ImageUtil.savePng(bi, "d:\testimage.png");
            detectedPices = new ArrayList<Point>();

            boolean[][] array = BlackWhite.convert(bi);
            boolean foundFirstColor = false;
            boolean firstColor = false;
            for (int y = 6; y < array[0].length - 6; y++) {
                for (int x = 6; x < array.length - 6; x++) {
                    if (detectC(array, x, y, true)) {
                        if (!foundFirstColor) {
                            firstColor = true;
                            foundFirstColor = true;
                        }
                        detectedPices.add(new Point(x, y, true));
                        markPiece(g2, x, y, true);
                        array[x][y] = true;
                        array[x + 1][y] = true;
                        array[x][y + 1] = true;
                        array[x + 1][y + 1] = true;
                        x += 15;
                    }
                    if (detectC(array, x, y, false)) {
                        if (!foundFirstColor) {
                            firstColor = false;
                            foundFirstColor = true;
                        }
                        detectedPices.add(new Point(x, y, false));
                        markPiece(g2, x, y, false);
                        array[x][y] = false;
                        array[x + 1][y] = false;
                        array[x + 1][y] = false;
                        array[x + 1][y + 1] = false;
                        x += 15;
                    }
                }
            }

            if (detectedPices.size() == 32) {
                markLines(bi, g2);
                setInitialized(true);
            }

            for (int i = 2; i < 6; i++) {
                for (int j = 2; j < 6; j++) {
                    lastBoard[i][j] = null;
                }
            }

            for (int i = 0; i < 8; i++) {
                lastBoard[i][1] = new PossiblePiece(firstColor, PossiblePiece.PAWN, i, 1);
                lastBoard[i][6] = new PossiblePiece(!firstColor, PossiblePiece.PAWN, i, 6);
            }
            lastBoard[0][0] = new PossiblePiece(firstColor, PossiblePiece.ROOK, 0, 0);
            lastBoard[7][0] = new PossiblePiece(firstColor, PossiblePiece.ROOK, 7, 0);
            lastBoard[0][7] = new PossiblePiece(!firstColor, PossiblePiece.ROOK, 0, 7);
            lastBoard[7][7] = new PossiblePiece(!firstColor, PossiblePiece.ROOK, 7, 7);

            lastBoard[1][0] = new PossiblePiece(firstColor, PossiblePiece.KNIGHT, 1, 0);
            lastBoard[6][0] = new PossiblePiece(firstColor, PossiblePiece.KNIGHT, 6, 0);
            lastBoard[1][7] = new PossiblePiece(!firstColor, PossiblePiece.KNIGHT, 1, 7);
            lastBoard[6][7] = new PossiblePiece(!firstColor, PossiblePiece.KNIGHT, 6, 7);

            lastBoard[2][0] = new PossiblePiece(firstColor, PossiblePiece.BISHOP, 2, 0);
            lastBoard[5][0] = new PossiblePiece(firstColor, PossiblePiece.BISHOP, 5, 0);
            lastBoard[2][7] = new PossiblePiece(!firstColor, PossiblePiece.BISHOP, 2, 7);
            lastBoard[5][7] = new PossiblePiece(!firstColor, PossiblePiece.BISHOP, 5, 7);

            lastBoard[3][0] = new PossiblePiece(firstColor, PossiblePiece.QUEEN, 3, 0);
            lastBoard[3][7] = new PossiblePiece(!firstColor, PossiblePiece.QUEEN, 3, 7);

            lastBoard[4][0] = new PossiblePiece(firstColor, PossiblePiece.KING, 4, 0);
            lastBoard[4][7] = new PossiblePiece(!firstColor, PossiblePiece.KING, 4, 7);

            leftBoard = verticalLines[0].start.x - 15 > 0 ? verticalLines[0].start.x - 15 : 0;
            rightBoard = verticalLines[7].start.x + 15 < bi.getWidth() ? verticalLines[7].start.x + 15 : bi.getWidth();
            topBoard = horizontalLines[0].start.y - 15 > 0 ? topBoard = horizontalLines[0].start.y - 15 : 0;
            bottomBoard = horizontalLines[7].start.y + 15 < bi.getHeight() ? horizontalLines[7].start.y + 15 : bi.getHeight();
            logger.debug(String.format("Left =%s Right=%s Top=%s Bottom=%s", leftBoard, rightBoard, topBoard, bottomBoard));

            humanColor = horizontalLines[0].start.color;

        }

//        for (int h = 0; h < 8; h++) {
//            g2.drawLine(horizontalLines[h].start.x, horizontalLines[h].start.y, horizontalLines[h].end.x, horizontalLines[h].end.y);
//        }
//
//        for (int v = 0; v < 8; v++) {
//            g2.drawLine(verticalLines[v].start.x, verticalLines[v].start.y, verticalLines[v].end.x, verticalLines[v].end.y);
//        }
        drawLastBoard(g2);

        g2.drawRect(leftBoard, topBoard, rightBoard - leftBoard, bottomBoard - topBoard);
    }

    public void CheckHumanMove() {
        // Go through all the pieces that are of human color and see which one has moved.
    }

    public void drawLastBoard(Graphics2D g2) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                PossiblePiece piece = lastBoard[i][j];
                if (piece != null) {
                    String p = "";
                    switch (piece.rank) {
                        case 0:
                            p = "P";
                            break;
                        case 1:
                            p = "B";
                            break;
                        case 2:
                            p = "N";
                            break;
                        case 3:
                            p = "R";
                            break;
                        case 4:
                            p = "Q";
                            break;
                        case 5:
                            p = "K";
                            break;
                    }
                    g2.drawString(p, verticalLines[piece.col].start.x, horizontalLines[piece.row].start.y);
                }
            }
        }
    }

    /**
     * The difference between detectCircles is that it takes in the limited area
     * defined by leftBoard ...
     *
     * @param bi
     */
    public int[] detectPieces(BufferedImage bi) {
        Graphics2D g2 = bi.createGraphics();

        BasicStroke stroke = new BasicStroke(2);
        g2.setStroke(stroke);

        boolean[][] array = BlackWhite.convert(bi.getSubimage(leftBoard, topBoard, rightBoard - leftBoard, bottomBoard - topBoard));
        ArrayList<PossiblePiece> pieces = new ArrayList<>();

        ArrayList<PossiblePiece> changedPiece = new ArrayList<>();
        ArrayList<PossiblePiece> takenPiece = new ArrayList<>();

        PossiblePiece[][] currentBoard = new PossiblePiece[8][8];

        int diff = 0;
        for (int y = 6; y < array[0].length - 6; y++) {
            for (int x = 6; x < array.length - 6; x++) {
                try {
                    if (detectC(array, x, y, true)) {
                        markPiece(g2, x + leftBoard, y + topBoard, true);
                        PossiblePiece piece = new PossiblePiece(x, y, true);
                        findOffFactor(piece);
                        pieces.add(piece);
                        array[x][y] = true;
                        array[x + 1][y] = true;
                        array[x][y + 1] = true;
                        array[x + 1][y + 1] = true;
                        x += 15;
                        // logger.debug(String.format("Found %s",piece.toString()));
                        if (humanColor) {
                            if (lastBoard[piece.col][piece.row] == null || lastBoard[piece.col][piece.row].color != piece.color) {
                                diff++;
                                changedPiece.add(piece);
                            }
                        }

                        if (currentBoard[piece.col][piece.row] != null) {
                            throw new Exception(String.format("Dectected two pieces on same square col=%s row=%s", piece.col, piece.row));
                        }
                        currentBoard[piece.col][piece.row] = piece;
                    } else if (detectC(array, x, y, false)) {
                        markPiece(g2, x + leftBoard, y + topBoard, true);
                        PossiblePiece piece = new PossiblePiece(x, y, false);
                        findOffFactor(piece);
                        pieces.add(piece);
                        array[x][y] = false;
                        array[x + 1][y] = false;
                        array[x + 1][y] = false;
                        array[x + 1][y + 1] = false;
                        x += 15;
                        if (!humanColor) {
                            if (lastBoard[piece.col][piece.row] == null || lastBoard[piece.col][piece.row].color != piece.color) {
                                diff++;
                                changedPiece.add(piece);
                            }
                        }
                        if (currentBoard[piece.col][piece.row] != null) {
                            throw new Exception(String.format("Dectected two pieces on same square col=%s row=%s", piece.col, piece.row));
                        }
                        currentBoard[piece.col][piece.row] = piece;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // Find piece from last board that is not there anymore
        ArrayList<PossiblePiece> lastLocation = new ArrayList<>();

        if (diff > 0) {
            for (int col = 0; col < 8; col++) {
                for (int row = 0; row < 8; row++) {
                    if (lastBoard[col][row] != null && currentBoard[col][row] == null) {
                        lastLocation.add(lastBoard[col][row]);
                    }
                }
            }
        }

        if (lastLocation.size() == 1 && changedPiece.size() == 1) {
            logger.debug(String.format("Piece moved from %s,%s to %s,%s", lastLocation.get(0).col, lastLocation.get(0).row, changedPiece.get(0).col, changedPiece.get(0).row));
            lastBoard = currentBoard;
            return new int[]{lastLocation.get(0).col, lastLocation.get(0).row, changedPiece.get(0).col, changedPiece.get(0).row};
        }

        drawLines(g2);
        return null;
    }

    /**
     * How far off from grid lines are points
     *
     * @param x
     * @param y
     * @return
     */
    private void findOffFactor(PossiblePiece piece) {
        int xminDiff = 100000;
        int yminDiff = 100000;
        for (int i = 0; i < 8; i++) {
            int xdiff = abs((piece.x + leftBoard) - verticalLines[i].start.x);
            if (xdiff < xminDiff) {
                xminDiff = xdiff;
                piece.col = i;
            }

            int ydiff = abs((piece.y + topBoard) - horizontalLines[i].start.y);
            if (ydiff < yminDiff) {
                yminDiff = ydiff;
                piece.row = i;
            }
        }

        piece.offFactor = (xminDiff ^ 2) + (yminDiff ^ 2);
    }

    private void markLines(BufferedImage bi, Graphics2D g2) {
        g2.setColor(Color.GREEN);
        logger.debug("Number of pieces are " + detectedPices.size());

        detectedPices.sort((Point a, Point b) -> Integer.compare(a.x, b.x));
        for (int i = 0; i < 8; i++) {
            double avgX = 0;
            for (int j = 0; j < 4; j++) {
                Point point = detectedPices.get(i * 4 + j);
                avgX += point.x;
                logger.debug(String.format("%s %s %s", i, point.x, point.y));
            }
            logger.debug(String.format("average = %s", avgX / 4));
            Point start = new Point((int) (avgX / 4), 0, false);
            Point end = new Point((int) (avgX / 4), bi.getHeight(), false);
            //         vertical[i] = new Line(start,end);
            verticalLines[i] = new Line(start, end);
        }

        detectedPices.sort((Point a, Point b) -> Integer.compare(a.y, b.y));
        for (int i = 0; i < 4; i++) {
            double avgY = 0;
            for (int j = 0; j < 8; j++) {
                Point point = detectedPices.get(i * 8 + j);
                avgY += point.y;
            }

            Point start = new Point(0, (int) (avgY / 8), detectedPices.get(i * 8).color);
            Point end = new Point(bi.getWidth(), (int) (avgY / 8), detectedPices.get(i * 8).color);
            if (i < 2) {
                horizontalLines[i] = new Line(start, end);
                //              horizontal[i] = new Line(start,end); 
            } else {
                horizontalLines[i + 4] = new Line(start, end);
            }
        }

        double spacingStart = (horizontalLines[6].start.y - horizontalLines[1].start.y) / 5.0;
        double spacingEnd = (horizontalLines[6].end.y - horizontalLines[1].end.y) / 5.0;

        for (int i = 0; i < 4; i++) {
            Point start = new Point(horizontalLines[0].start.x, (int) (horizontalLines[1].start.y + spacingStart + (spacingStart * i)), false);
            Point end = new Point(horizontalLines[0].end.x, (int) (horizontalLines[1].end.y + spacingEnd + (spacingEnd * i)), false);
            horizontalLines[i + 2] = new Line(start, end);
            g2.drawLine(horizontalLines[i + 2].start.x, horizontalLines[i + 2].start.y, horizontalLines[i + 2].end.x, horizontalLines[i + 2].end.y);
        }
    }

    private void drawLines(Graphics2D g2) {
        for (int i = 0; i < 8; i++) {
            g2.drawLine(horizontalLines[i].start.x, horizontalLines[i].start.y, horizontalLines[i].end.x, horizontalLines[i].end.y);
            g2.drawLine(verticalLines[i].start.x, verticalLines[i].start.y, verticalLines[i].end.x, verticalLines[i].end.y);
        }
    }

    private void markPiece(Graphics2D g2, int x, int y, boolean piece) {
        int size = 8;
        if (piece) {
            g2.setColor(Color.RED);
        } else {
            g2.setColor(Color.BLUE);
        }
        g2.drawLine(x - size, y - size, x + size, y - size);
        g2.drawLine(x + size, y - size, x + size, y + size);
        g2.drawLine(x + size, y + size, x - size, y + size);
        g2.drawLine(x - size, y + size, x - size, y - size);
    }

    private static boolean detectC(boolean[][] array, int startX, int startY, boolean piece) {
        int MAX_Distance = 3; // was 5

        //System.out.format("startX=%s startY=%s\n", startX, startY);
        if (array[startX][startY] == piece) {
            return false;
        }

        // Check up
        int y = startY - 1;
        while (array[startX][y] != piece && y > 0) {
            y--;
        }

        if (startY - y < MAX_Distance) {
            //System.out.format("startY - y < 5  startY=%s y=%s\n", startY, y);
            return false;
        }
        int t = y;
        int yUp = startY - y;
        // Check down
        y = startY + 1;
        while (array[startX][y] != piece && y < array[0].length - 1) {
            y++;
        }
        if (y - startY < MAX_Distance) {
            //System.out.format("y - startY < 5  startY=%s y=%s\n", startY, y);
            return false;
        }
        int b = y;
        int yDown = y - startY;
        if (Math.abs(yUp - yDown) > 1) {
            //System.out.format("Math.abs(yUp - yDown) > 3  yUp=%s yDown=%s\n", yUp, yDown);
            return false;
        }

        // Check Left
        int x = startX - 1;
        while (array[x][startY] != piece && x > 0) {
            x--;
        }
        if (startX - x < MAX_Distance) {
            //System.out.format("startX - x < 5  startX=%s x=%s\n", startX, x);
            return false;
        }
        int l = x;
        int xLeft = startX - x;

        x = startX + 1;
        while (array[x][startY] != piece && x < array.length - 1) {
            x++;
        }
        if (x - startX < MAX_Distance) {
            //System.out.format("x - startX < 5  startX=%s x=%s\n", startX, x);
            return false;
        }
        int r = x;
        int xRight = x - startX;

        if (Math.abs(xLeft - xRight) > 1) {
            //System.out.format("Math.abs(xLeft - xRight) > 4  xLeft=%s xRight=%s\n", xLeft, xRight);
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
        if (b - t < 11 || b - t > 26 || r - l < 11 || r - l > 26) {
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
                if (array[x][y] != piece) {
                    return false;
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

        for (int tx = x; tx > l; tx--) {
            for (int ty = y; ty > t; ty--) {
                if (array[x][y] != piece) {
                    return false;
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

        return true;
    }

    /**
     * @return the initialized
     */
    public boolean isInitialized() {
        return initialized;
    }

    public void setIsInitialized(boolean isInitialized) {
        this.initialized = isInitialized;
        horizontalLines = new Line[8];
        verticalLines = new Line[8];
        lastBoard = new PossiblePiece[8][8];

    }

    /**
     * @param initialized the initialized to set
     */
    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

}
