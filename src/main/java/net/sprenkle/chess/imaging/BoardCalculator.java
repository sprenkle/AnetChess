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
import java.util.List;
import net.sprenkle.chess.BoardProperties;
import net.sprenkle.chess.Player;
import net.sprenkle.chess.PossiblePiece;
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
    private PossiblePiece[][] knownBoard = new PossiblePiece[8][8];
    private boolean initialized = false;
    private final int leftBoard;
    private final int rightBoard;
    private final int bottomBoard;
    private final int topBoard;
    private final int leftDetect;
    private final int rightDetect;
    private final int bottomDetect;
    private final int topDetect;
    private final int leftHook;
    private final int rightHook;
    private final int bottomHook;
    private final int topHook;

    static Player humanColor;
    private ArrayList<PossiblePiece> detectedPieces;

    static int lastNumPieces = 16;

    static double xSlope = -0.4262;
    static double ySlope = 0.4271;
    static double xIntercept = 175.376;
    static double yIntercept = -165.4933;
    private int threshHold = 130;

    public BoardCalculator(BoardProperties boardProperties) {
        leftBoard = boardProperties.getLeftBoard();
        rightBoard = boardProperties.getRightBoard();
        bottomBoard = boardProperties.getBottomBoard();
        topBoard = boardProperties.getTopBoard();

        leftDetect = boardProperties.getLeftDetect();
        rightDetect = boardProperties.getRightDetect();
        bottomDetect = boardProperties.getBottomDetect();
        topDetect = boardProperties.getTopDetect();

        leftHook = boardProperties.getLeftHook();
        rightHook = boardProperties.getRightHook();
        bottomHook = boardProperties.getBottomHook();
        topHook = boardProperties.getTopHook();

        verticalLines[0] = new Line(new Point(215, 0, Player.White), new Point(215, 600, Player.White));
        horizontalLines[0] = new Line(new Point(0, 22, Player.White), new Point(800, 22, Player.White));
        verticalLines[1] = new Line(new Point(269, 0, Player.White), new Point(269, 600, Player.White));
        horizontalLines[1] = new Line(new Point(0, 85, Player.White), new Point(800, 85, Player.White));
        verticalLines[2] = new Line(new Point(324, 0, Player.White), new Point(324, 600, Player.White));
        horizontalLines[2] = new Line(new Point(0, 140, Player.White), new Point(800, 140, Player.White));
        verticalLines[3] = new Line(new Point(380, 0, Player.White), new Point(380, 600, Player.White));
        horizontalLines[3] = new Line(new Point(0, 195, Player.White), new Point(800, 195, Player.White));
        verticalLines[4] = new Line(new Point(434, 0, Player.White), new Point(434, 600, Player.White));
        horizontalLines[4] = new Line(new Point(0, 250, Player.White), new Point(800, 250, Player.White));
        verticalLines[5] = new Line(new Point(491, 0, Player.White), new Point(491, 600, Player.White));
        horizontalLines[5] = new Line(new Point(0, 305, Player.White), new Point(800, 305, Player.White));
        verticalLines[6] = new Line(new Point(549, 0, Player.White), new Point(549, 600, Player.White));
        horizontalLines[6] = new Line(new Point(0, 361, Player.White), new Point(800, 361, Player.White));
        verticalLines[7] = new Line(new Point(605, 0, Player.White), new Point(605, 600, Player.White));
        horizontalLines[7] = new Line(new Point(0, 420, Player.White), new Point(800, 420, Player.White));
    }

    public Player getHumanColor() {
        return humanColor;
    }

    public void syncImage(BufferedImage bi) {
        boolean[][] array = BlackWhite.convert(bi.getSubimage(leftBoard, topBoard, rightBoard - leftBoard, bottomBoard - topBoard), threshHold);
        Graphics2D g2 = bi.createGraphics();
        BasicStroke stroke = new BasicStroke(2);
        g2.setStroke(stroke);
        g2.setColor(Color.GREEN);
        drawLines(g2);
        ArrayList<PossiblePiece> pieces = detectCircles(array, true);
        pieces.forEach(x -> findOffFactor(x));

        SimpleRegression srX = new SimpleRegression();
        SimpleRegression srY = new SimpleRegression();
        pieces.forEach((piece) -> {
            markPiece(g2, piece.x, piece.y, piece.color);
            double xB = 95.4 + ((3 - piece.col) * 24) + 12;
            double yB = 193 - ((7 - piece.row) * 24 + 12);
            srX.addData(piece.x, xB);
            srY.addData(piece.y, yB);
        });
        g2.drawRect(leftBoard, topBoard, rightBoard - leftBoard, bottomBoard - topBoard);
        logger.info(String.format("X Slope=%s  Intercept=%s", srX.getSlope(), srX.getIntercept()));
        logger.info(String.format("Y Slope=%s  Intercept=%s", srY.getSlope(), srY.getIntercept()));
    }

    public void showCircles(BufferedImage bi) {
        boolean[][] array = BlackWhite.convert(bi.getSubimage(leftBoard, topBoard, rightBoard - leftBoard, bottomBoard - topBoard), threshHold);
        Graphics2D g2 = bi.createGraphics();
        BasicStroke stroke = new BasicStroke(2);
        g2.setStroke(stroke);
        g2.setColor(Color.GREEN);
        drawLines(g2);

        ArrayList<PossiblePiece> pieces = detectCircles(array, true);
        pieces.forEach((piece) -> markPiece(g2, piece.x, piece.y, piece.color));
        g2.drawRect(leftBoard, topBoard, rightBoard - leftBoard, bottomBoard - topBoard);

        ArrayList<PossiblePiece> markers = detectBoardMarker(bi);
        markers.forEach((piece) -> markPiece(g2, piece.x, piece.y, Color.CYAN));
        g2.setColor(Color.YELLOW);
        g2.drawRect(leftDetect, topDetect, rightDetect - leftDetect, bottomDetect - topDetect);

        ArrayList<PossiblePiece> hook = detectHook(bi);
        hook.forEach((piece) -> markPiece(g2, piece.x, piece.y, Color.PINK));
        g2.setColor(Color.PINK);
        g2.drawRect(leftHook, topHook, rightHook - leftHook, bottomHook - topHook);
    }

    public ArrayList<PossiblePiece> detectPieceCircles(boolean[][] array) {
        ArrayList<PossiblePiece> pieces = detectCircles(array, true);
        PossiblePiece[][] tempPiecePositions = new PossiblePiece[8][8];
        List<PossiblePiece> duplicates = new ArrayList<>();

        // Get offset
        pieces.forEach((piece) -> {
            findOffFactor(piece);
        });
        pieces.sort((t1, t2) -> Double.compare(t1.getOffset(), t2.getOffset()));

        for (PossiblePiece piece : pieces) {
            if (tempPiecePositions[piece.col][piece.row] != null) {
                logger.debug(String.format("Duplicate Piece %s,%s has offset of %s", piece.col, piece.row, piece.getOffset()));
                duplicates.add(piece);
                continue;
            }
            tempPiecePositions[piece.col][piece.row] = piece;
        }

        duplicates.forEach((piece) -> {
            pieces.remove(piece);
        });

        return pieces;
    }

    public ArrayList<PossiblePiece> detectCircles(boolean[][] array, boolean restrictArea) {
        int xOffset = restrictArea ? leftBoard : 0;
        int yOffset = restrictArea ? topBoard : 0;
        return detectCircles(array, xOffset, yOffset);
    }

    public ArrayList<PossiblePiece> detectCircles(boolean[][] array, int xOffset, int yOffset) {
        ArrayList<PossiblePiece> pieces = new ArrayList<>();
        for (int y = 6; y < array[0].length - 6; y++) {
//                        logger.debug(String.format("y=%s",  y));
            for (int x = 6; x < array.length - 6; x++) {
                try {
                    if (detectC(array, x, y, true)) {
                        PossiblePiece piece = new PossiblePiece(x + xOffset, y + yOffset, Player.White);
                        pieces.add(piece);
                        array[x][y] = true; // sets the color so it will not be detected again
                        array[x + 1][y] = true;
                        array[x][y + 1] = true;
                        array[x + 1][y + 1] = true;
                        x += 15;
                    } else if (detectC(array, x, y, false)) {
                        //                      logger.debug(String.format("x=%s y=%s", x, y));
                        PossiblePiece piece = new PossiblePiece(x + xOffset, y + yOffset, Player.Black);
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

    public boolean initialLines(BufferedImage bi) {
        Graphics2D g2 = bi.createGraphics();

        BasicStroke stroke = new BasicStroke(2);
        g2.setStroke(stroke);
        if (!isInitialized()) {
            boolean[][] array = BlackWhite.convert(bi.getSubimage(leftBoard, topBoard, rightBoard - leftBoard, bottomBoard - topBoard), threshHold);

            detectedPieces = new ArrayList<>();
            detectedPieces = detectPieceCircles(array);

            markLines(bi, g2);

            if (detectedPieces == null || detectedPieces.size() != 32) {
                return false;
            }

            setInitialized(true);
            humanColor = horizontalLines[0].start.color;

        }
        g2.drawRect(leftBoard, topBoard, rightBoard - leftBoard, bottomBoard - topBoard);
        return true;
    }

    public void drawLastBoard(Graphics2D g2, PossiblePiece[][] lastBoard) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                PossiblePiece piece = knownBoard[i][j];
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
                    try {
                        g2.drawString(p, verticalLines[piece.col].start.x, horizontalLines[piece.row].start.y);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void markLines(BufferedImage bi, Graphics2D g2) {
        g2.setColor(Color.GREEN);
        logger.debug("Number of pieces are " + detectedPieces.size());
        detectedPieces.sort((PossiblePiece a, PossiblePiece b) -> Integer.compare(a.y, b.y));
        int minY = detectedPieces.get(0).y;
        int maxY = detectedPieces.get(detectedPieces.size() - 1).y;

        detectedPieces.sort((PossiblePiece a, PossiblePiece b) -> Integer.compare(a.x, b.x));
        for (int i = 0; i < 8; i++) {
            SimpleRegression simpleRegression = new SimpleRegression(true);
            simpleRegression = new SimpleRegression(true);
            double avgX = 0;
            for (int j = 0; j < 4; j++) {
                PossiblePiece point = detectedPieces.get(i * 4 + j);
                avgX += point.x;
            }
            Point start = new Point((int) (avgX / 4), 0, Player.Black);
            Point end = new Point((int) (avgX / 4), bi.getHeight(), Player.Black);
            //         vertical[i] = new Line(start,end);
            verticalLines[i] = new Line(start, end);
        }

        detectedPieces.sort((PossiblePiece a, PossiblePiece b) -> Integer.compare(a.y, b.y));
        for (int i = 0; i < 4; i++) {
            SimpleRegression simpleRegression = new SimpleRegression();
            double avgY = 0;
            for (int j = 0; j < 8; j++) {
                PossiblePiece point = detectedPieces.get(i * 8 + j);
                avgY += point.y;
                //logger.debug(String.format("%s %s %s",i, point.x, point.y));
                simpleRegression.addData(point.x, point.y);
            }

            minY = (int) (simpleRegression.getIntercept());
            maxY = (int) (bi.getWidth() * simpleRegression.getSlope() + simpleRegression.getIntercept());
            Point start = new Point(0, (int) (avgY / 8), detectedPieces.get(i * 8).color);
            Point end = new Point(bi.getWidth(), (int) (avgY / 8), detectedPieces.get(i * 8).color);
            if (i < 2) {
                horizontalLines[i] = new Line(start, end);
                //              horizontal[i] = new Line(start,end); 
            } else {
                horizontalLines[i + 4] = new Line(start, end);
                //              horizontal[i+4] = new Line(start,end); 
            }
        }

        double spacingStart = (horizontalLines[6].start.y - horizontalLines[1].start.y) / 5.0;
        double spacingEnd = (horizontalLines[6].end.y - horizontalLines[1].end.y) / 5.0;

        for (int i = 0; i < 4; i++) {
            Point start = new Point(horizontalLines[0].start.x, (int) (horizontalLines[1].start.y + spacingStart + (spacingStart * i)), Player.Black);
            Point end = new Point(horizontalLines[0].end.x, (int) (horizontalLines[1].end.y + spacingEnd + (spacingEnd * i)), Player.Black);
            horizontalLines[i + 2] = new Line(start, end);
            g2.drawLine(horizontalLines[i + 2].start.x, horizontalLines[i + 2].start.y, horizontalLines[i + 2].end.x, horizontalLines[i + 2].end.y);
        }
    }

    public ArrayList<PossiblePiece> detectBoardMarker(BufferedImage bi) {
        boolean[][] array = BlackWhite.convert(bi.getSubimage(leftDetect, topDetect, rightDetect - leftDetect, bottomDetect - topDetect), threshHold);
        ArrayList<PossiblePiece> pieces = detectCircles(array, leftDetect, topDetect);
        return pieces;
    }

    public ArrayList<PossiblePiece> detectHook(BufferedImage bi) {
        boolean[][] array = BlackWhite.convert(bi.getSubimage(leftHook, topHook, rightHook - leftHook, bottomHook - topHook), threshHold);
        ArrayList<PossiblePiece> pieces = detectCircles(array, leftHook, topHook);
        return pieces;
    }

    public int getHookWidth(BufferedImage bi) {
        ArrayList<PossiblePiece> hooks = detectHook(bi);
        if (hooks == null || hooks.size() != 1) {
            return -1;
        }
        PossiblePiece hook = hooks.get(0);

        boolean[][] array = BlackWhite.convert(bi.getSubimage(hook.x - 15, hook.y - 15, 30, 30), threshHold);

        int y = 15;
        while (array[15][y] && y > 0) {
            y--;
        }

        int top = y;

        // Find bottom of circle
        y = 16;
        while (array[15][y] && y < array[0].length - 1) {
            y++;
        }

        return y - top;
    }

    /**
     * This method is used to sync what the chess engine knows is the board
     * position with what the camera sees.
     *
     * Side effect - Sets the x and y image locations for the pieces.
     *
     * @param bi
     * @param knownBoard
     * @return true if camera and knownBoard are the same
     */
    public boolean verifyPiecePositions(BufferedImage bi, PossiblePiece[][] knownBoard) {
        boolean[][] array = BlackWhite.convert(bi.getSubimage(leftBoard, topBoard, rightBoard - leftBoard, bottomBoard - topBoard), threshHold);
        ArrayList<PossiblePiece> pieces = detectPieceCircles(array);
        // Get offset
//        if (!pieces.stream().map((piece) -> {
//            findOffFactor(piece);
//            return piece;
//        }).noneMatch((piece) -> (knownBoard[piece.col][piece.row] == null))) {
//            return false;
//        }
        
        for(PossiblePiece piece : pieces){
            findOffFactor(piece);
            if(knownBoard[piece.col][piece.row] == null){
                return false;
            }
        }
        
        
        pieces.sort((t1, t2) -> Double.compare(t1.getOffset(), t2.getOffset()));

        for (int x = 0; x < 7; x++) {
            for (int y = 0; y < 7; y++) {
                if (knownBoard[x][y] != null) {
                    boolean foundMatchingPiece = false;
                    for (PossiblePiece piece : pieces) {
                        if (piece.col == x && piece.row == y) {
                            knownBoard[x][y].x = piece.x;
                            knownBoard[x][y].y = piece.y;
                            foundMatchingPiece = true;
                            break;
                        }
                    }
                    if (!foundMatchingPiece) {
                        return false;
                    }
                }
            }
        }
        
        return true;
    }

    /**
     * The difference between detectCircles is that it takes in the limited area
     * defined by leftBoard ...
     *
     * @param bi
     */
    public int[] detectPieces(BufferedImage bi, Player turn, PossiblePiece[][] lastBoard) throws Exception {
        int[] rv = null;

        Graphics2D g2 = bi.createGraphics();

        BasicStroke stroke = new BasicStroke(2);
        g2.setStroke(stroke);

        ArrayList<PossiblePiece> changedPiece = new ArrayList<>();

        PossiblePiece[][] currentBoard = new PossiblePiece[8][8];

        boolean[][] array = BlackWhite.convert(bi.getSubimage(leftBoard, topBoard, rightBoard - leftBoard, bottomBoard - topBoard), threshHold);
        ArrayList<PossiblePiece> pieces = detectCircles(array, true);

        int diff = 0;
        PossiblePiece[][] tempPiecePositions = new PossiblePiece[8][8];
        // Get offset
        for (PossiblePiece piece : pieces) {
            markPiece(g2, piece.x, piece.y, piece.color);
            findOffFactor(piece);
        }

        pieces.sort((t1, t2) -> Double.compare(t1.getOffset(), t2.getOffset()));
        List<PossiblePiece> duplicates = new ArrayList<>();
        for (PossiblePiece piece : pieces) {
            //logger.debug(String.format("Piece %s,%s has offset of %s", piece.col, piece.row, piece.offFactor));

            if (tempPiecePositions[piece.col][piece.row] != null) {
                logger.debug(String.format("Duplicate Piece %s,%s has offset of %s", piece.col, piece.row, piece.getOffset()));
                duplicates.add(piece);
                continue;
            }
            if (piece.getOffset() <= 200) {
                if (knownBoard[piece.col][piece.row] == null || !knownBoard[piece.col][piece.row].color.equals(piece.color)) {
                    logger.debug(String.format("Difference in Piece %s,%s has offset of %s", piece.col, piece.row, piece.getOffset()));
                    diff++;
                    changedPiece.add(piece);
                }
                if (currentBoard[piece.col][piece.row] == null || currentBoard[piece.col][piece.row].getOffset() > piece.getOffset()) {
                    currentBoard[piece.col][piece.row] = piece;
                }
            }
            tempPiecePositions[piece.col][piece.row] = piece;
        }

        duplicates.forEach((piece) -> {
            pieces.remove(piece);
        });

        // Find piece from last board that is not there anymore
        ArrayList<PossiblePiece> lastLocation = new ArrayList<>();

        if (diff > 0) {
            for (int col = 0; col < 8; col++) {
                for (int row = 0; row < 8; row++) {
                    if (knownBoard[col][row] != null && currentBoard[col][row] == null) {
                        lastLocation.add(knownBoard[col][row]);
                    }
                }
            }
        }

        if (lastLocation.size() == 1 && changedPiece.size() == 1 && turn.equals(changedPiece.get(0).color)) {
            logger.debug(String.format("Piece moved from %s,%s to %s,%s", lastLocation.get(0).col, lastLocation.get(0).row, changedPiece.get(0).col, changedPiece.get(0).row));
            rv = new int[]{lastLocation.get(0).col, lastLocation.get(0).row, changedPiece.get(0).col, changedPiece.get(0).row};
        } else if (changedPiece.size() == 2 && checkForCastle(changedPiece.get(0), changedPiece.get(1)) != null) {
            rv = checkForCastle(changedPiece.get(0), changedPiece.get(1));
        }

        drawLines(g2);
        return rv;
    }

    private int[] checkForCastle(PossiblePiece p1, PossiblePiece p2) {
        if ((p1.row > 0 && p1.row < 7) || p1.row != p2.row) {
            return null;
        }

        PossiblePiece king = null;
        PossiblePiece rook = null;

        switch (p1.col) {
            case 1:
                king = p1;
                break;
            case 2:
                rook = p1;
                break;
            case 5:
                rook = p1;
                break;
            case 6:
                king = p1;
                break;
        }

        switch (p2.col) {
            case 1:
                king = p2;
                break;
            case 2:
                rook = p2;
                break;
            case 5:
                rook = p2;
                break;
            case 6:
                king = p2;
                break;
        }

        if (king == null || rook == null || Math.abs(king.col - rook.col) != 1) {
            return null;
        }

        if (king.col == 2) {
            return new int[]{3, king.row, king.col, king.row};
        } else {
            return new int[]{3, king.row, king.col, king.row};
        }
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
            int xdiff = verticalLines[i].start.x - piece.x;
            if (abs(xdiff) < xminDiff) {
                xminDiff = abs(xdiff);
                piece.col = i;
            }

            int ydiff = horizontalLines[i].start.y - piece.y;
            if (ydiff < abs(yminDiff)) {
                yminDiff = abs(ydiff);
                piece.row = i;
            }
        }

        piece.xOffFactor = xminDiff;
        piece.yOffFactor = yminDiff;
    }

    private void drawLines(Graphics2D g2) {
        for (int i = 0; i < 8; i++) {
            g2.drawLine(horizontalLines[i].start.x, horizontalLines[i].start.y, horizontalLines[i].end.x, horizontalLines[i].end.y);
            g2.drawLine(verticalLines[i].start.x, verticalLines[i].start.y, verticalLines[i].end.x, verticalLines[i].end.y);
        }
    }

    private void markPiece(Graphics2D g2, int x, int y, Player piece) {
        Color color = Color.BLACK;
        if (piece == Player.White) {
            color = Color.RED;
        } else {
            color = Color.BLUE;
        }
        markPiece(g2, x, y, color);
    }

    private void markPiece(Graphics2D g2, int x, int y, Color color) {
        int size = 8;
        g2.setColor(color);
        g2.drawLine(x - size, y - size, x + size, y - size);
        g2.drawLine(x + size, y - size, x + size, y + size);
        g2.drawLine(x + size, y + size, x - size, y + size);
        g2.drawLine(x - size, y + size, x - size, y - size);
    }

    public boolean detectC(boolean[][] array, int startX, int startY, boolean piece) {
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

    /**
     * @return the initialized
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * @param initialized the initialized to set
     */
    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public PossiblePiece[][] getKnownBoard() {
        return knownBoard;
    }

    public void setKnownBoard(PossiblePiece[][] knownBoard) {
        this.knownBoard = knownBoard;
    }

    /**
     * @return the threshHold
     */
    public int getThreshHold() {
        return threshHold;
    }

    /**
     * @param threshHold the threshHold to set
     */
    public void setThreshHold(int threshHold) {
        this.threshHold = threshHold;
    }

    public void printToConsole(boolean[][] array, int x, int y) {
        StringBuilder sb = new StringBuilder();
        for (int yi = y - 15; yi <= y + 15; yi++) {
            for (int xi = x - 15; xi <= x + 15; xi++) {
                sb.append((array[xi][yi] ? 'X' : 'O'));
            }
            sb.append('\n');
        }
        System.out.println(sb.toString());
    }

    public static void main(String[] args) throws Exception {
//        try {
//            BufferedImage bi = ImageUtil.loadImage("D:\\git\\Chess\\images\\unitTestImages\\board914c6097-40b5-4bb8-a337-3ad18de0412b.png");
//            boolean[][] array = BlackWhite.convert(bi.getSubimage(leftBoard, topBoard, rightBoard - leftBoard, bottomBoard - topBoard), 100);
//
//            BoardCalculator boardCalculator = new BoardCalculator(new BoardProperties());
//            boardCalculator.printToConsole(array, 474 - leftBoard, 322 - topBoard);
//        } catch (IOException ex) {
//            java.util.logging.Logger.getLogger(BoardCalculator.class.getName()).log(Level.SEVERE, null, ex);
//        }

    }
}
