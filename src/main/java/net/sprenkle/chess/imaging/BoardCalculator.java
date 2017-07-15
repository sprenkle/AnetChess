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
import java.io.IOException;
import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.logging.Level;
import net.sprenkle.chess.BoardProperties;
import net.sprenkle.chess.Player;
import net.sprenkle.chess.PossiblePiece;
import net.sprenkle.imageutils.BlackWhite;
import net.sprenkle.imageutils.ImageUtil;
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
    static Player humanColor;
    private ArrayList<PossiblePiece> detectedPieces;

    static int lastNumPieces = 16;

    static double xSlope = -0.4262;
    static double ySlope = 0.4271;
    static double xIntercept = 175.376;
    static double yIntercept = -165.4933;
    private int threshHold = 110;

    //   Line horizontal[] = new Line[8];
    //   Line vertical[] = new Line[8];
    public BoardCalculator(BoardProperties boardProperties) {
        leftBoard = boardProperties.getLeftBoard();
        rightBoard = boardProperties.getRightBoard();
        bottomBoard = boardProperties.getBottomBoard();
        topBoard = boardProperties.getTopBoard();
        
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

    public void parseBI(BufferedImage bi) {

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
    }

    public ArrayList<PossiblePiece> detectCircles(boolean[][] array, boolean restrictArea) {
        ArrayList<PossiblePiece> pieces = new ArrayList<>();
        int xOffset = restrictArea ? leftBoard : 0;
        int yOffset = restrictArea ? topBoard : 0;
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
            detectedPieces = detectCircles(array, true);

            markLines(bi, g2);

            if (detectedPieces.isEmpty()) {
                return false;
            }
            detectedPieces.forEach(x -> findOffFactor(x));

            detectedPieces.sort((e1, e2) -> Double.compare(e1.offFactor, e2.offFactor));
            for (PossiblePiece detectedPiece : detectedPieces) {
                PossiblePiece check = knownBoard[detectedPiece.col][detectedPiece.row];
                if (check == null || !check.color.equals(detectedPiece.color)) {
                    return false;
                }
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

//        for (int i = 0; i < 8; i++) {
//            logger.info(String.format("verticalLines[0] = new Line(new Point(%s, %s, false), new Point(%s, %s, false));", verticalLines[i].start.x, verticalLines[i].start.y, verticalLines[i].end.x, verticalLines[i].end.y));
//            logger.info(String.format("horizontalLines[0] = new Line(new Point(%s, %s, false), new Point(%s, %s, false));", horizontalLines[i].start.x, horizontalLines[i].start.y, horizontalLines[i].end.x, horizontalLines[i].end.y));
//        }
    }
//    public boolean detectPieceLocations(BufferedImage bi) {
//
//    }

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
        int count = 0;
        for (PossiblePiece piece : pieces) {
            markPiece(g2, piece.x, piece.y, piece.color);
            findOffFactor(piece);
            if (piece.offFactor <= 200) {
                if (knownBoard[piece.col][piece.row] == null || !knownBoard[piece.col][piece.row].color.equals(piece.color)) {
                    diff++;
                    changedPiece.add(piece);
                }
                //logger.debug(String.format("Piece %s %s,%s has offset of %s", count++, piece.col, piece.row, piece.offFactor));
                if (currentBoard[piece.col][piece.row] == null || currentBoard[piece.col][piece.row].offFactor > piece.offFactor) {
                    currentBoard[piece.col][piece.row] = piece;
                }
            }
        }

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
        }else if(changedPiece.size() == 2 && checkForCastle(changedPiece.get(0), changedPiece.get(1)) != null){
            return checkForCastle(changedPiece.get(0), changedPiece.get(1));
        }

        drawLines(g2);
        return rv;
    }
    
    private int[] checkForCastle(PossiblePiece p1, PossiblePiece p2){
        if((p1.row > 0 && p1.row < 7) || p1.row != p2.row) return null;

        PossiblePiece king = null;
        PossiblePiece rook = null;

        switch(p1.col){
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

        switch(p2.col){
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

        if(king == null || rook == null || Math.abs(king.col - rook.col) != 1) return null;
        
        if(king.col == 2){
            return new int[] {3, king.row, king.col, king.row};
        }else{
            return new int[] {3, king.row, king.col, king.row};
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
            int xdiff = abs(piece.x - verticalLines[i].start.x);
            if (xdiff < xminDiff) {
                xminDiff = xdiff;
                piece.col = i;
            }

            int ydiff = abs(piece.y - horizontalLines[i].start.y);
            if (ydiff < yminDiff) {
                yminDiff = ydiff;
                piece.row = i;
            }
        }

        piece.offFactor = xminDiff + yminDiff;
    }

    private void drawLines(Graphics2D g2) {
        for (int i = 0; i < 8; i++) {
            g2.drawLine(horizontalLines[i].start.x, horizontalLines[i].start.y, horizontalLines[i].end.x, horizontalLines[i].end.y);
            g2.drawLine(verticalLines[i].start.x, verticalLines[i].start.y, verticalLines[i].end.x, verticalLines[i].end.y);
        }
    }

    private void markPiece(Graphics2D g2, int x, int y, Player piece) {
        int size = 8;
        if (piece == Player.White) {
            g2.setColor(Color.RED);
        } else {
            g2.setColor(Color.BLUE);
        }
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
        top = y;
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
        bottom = y;
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
        left = x;
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
        right = x;
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
        // if( true) return true;
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
