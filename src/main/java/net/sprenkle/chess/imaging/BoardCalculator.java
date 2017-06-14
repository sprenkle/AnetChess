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
import net.sprenkle.chess.PossiblePiece;
import net.sprenkle.imageutils.BlackWhite;
import net.sprenkle.imageutils.ImageUtil;
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
    static int leftBoard = 239;
    static int rightBoard = 585;
    static int bottomBoard = 387;
    static int topBoard = 15;
    static boolean humanColor;

    static int lastNumPieces = 16;

    static double xSlope = -0.4262;
    static double ySlope = 0.4271;
    static double xIntercept = 175.376;
    static double yIntercept = -165.4933;
    private int threshHold = 100;

    ArrayList<PossiblePiece> detectedPices = new ArrayList<>();
    //   Line horizontal[] = new Line[8];
    //   Line vertical[] = new Line[8];

    public BoardCalculator() {
    }

    public void parseBI(BufferedImage bi) {

    }

    public boolean getHumanColor() {
        return humanColor;
    }

    public void showCircles(BufferedImage bi) {
        boolean[][] array = BlackWhite.convert(bi.getSubimage(leftBoard, topBoard, rightBoard - leftBoard, bottomBoard - topBoard), threshHold);
        Graphics2D g2 = bi.createGraphics();
        BasicStroke stroke = new BasicStroke(2);
        g2.setStroke(stroke);

        ArrayList<PossiblePiece> pieces = detectCircles(array, true);
        pieces.forEach((piece) -> markPiece(g2, piece.x, piece.y, piece.color));
    }

    public ArrayList<PossiblePiece> detectCircles(boolean[][] array, boolean restrictArea) {
        ArrayList<PossiblePiece> pieces = new ArrayList<>();
        int xOffset = restrictArea ? leftBoard : 0;
        int yOffset = restrictArea ? topBoard : 0;
        for (int y = 6; y < array[0].length - 6; y++) {
            for (int x = 6; x < array.length - 6; x++) {
                try {
                    if (detectC(array, x, y, true)) {
                        PossiblePiece piece = new PossiblePiece(x + xOffset, y + yOffset, true);
                        pieces.add(piece);
                        array[x][y] = true; // sets the color so it will not be detected again
                        array[x + 1][y] = true;
                        array[x][y + 1] = true;
                        array[x + 1][y + 1] = true;
                        x += 15;
                    } else if (detectC(array, x, y, false)) {
                        PossiblePiece piece = new PossiblePiece(x + xOffset, y + yOffset, false);
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
            boolean firstColor = false;
            boolean[][] array = BlackWhite.convert(bi.getSubimage(leftBoard, topBoard, rightBoard - leftBoard, bottomBoard - topBoard), threshHold);

            detectedPices = detectCircles(array, true);
            firstColor = detectedPices.get(0).color;

            if (detectedPices.size() != 32) {
                return;
            }

            verticalLines[0] = new Line(new Point(264, 0, false), new Point(264, 600, false)); //) 264,0-264,600
            horizontalLines[0] = new Line(new Point(0, 25, false), new Point(800, 25, false));// horizontalLines 0,25-800,25
            verticalLines[1] = new Line(new Point(305, 0, false), new Point(305, 600, false));// verticalLines 305,0-305,600
            horizontalLines[1] = new Line(new Point(0, 69, false), new Point(800, 69, false));// horizontalLines 0,69-800,69
            verticalLines[2] = new Line(new Point(350, 0, false), new Point(350, 600, false));// verticalLines 350,0-350,600
            horizontalLines[2] = new Line(new Point(0, 111, false), new Point(800, 111, false));// horizontalLines 0,111-800,111
            verticalLines[3] = new Line(new Point(389, 0, false), new Point(389, 600, false));// verticalLines 389,0-389,600
            horizontalLines[3] = new Line(new Point(0, 154, false), new Point(800, 154, false));// horizontalLines 0,154-800,154
            verticalLines[4] = new Line(new Point(430, 0, false), new Point(430, 600, false));// verticalLines 430,0-430,600
            horizontalLines[4] = new Line(new Point(0, 197, false), new Point(800, 197, false));// horizontalLines 0,197-800,197
            verticalLines[5] = new Line(new Point(476, 0, false), new Point(476, 600, false));// verticalLines 476,0-476,600
            horizontalLines[5] = new Line(new Point(0, 240, false), new Point(800, 240, false));// horizontalLines 0,240-800,240
            verticalLines[6] = new Line(new Point(515, 0, false), new Point(515, 600, false));// verticalLines 515,0-515,600
            horizontalLines[6] = new Line(new Point(0, 283, false), new Point(800, 283, false));// horizontalLines 0,283-800,283
            verticalLines[7] = new Line(new Point(559, 0, false), new Point(559, 600, false));// verticalLines 559,0-559,600
            horizontalLines[7] = new Line(new Point(0, 324, false), new Point(800, 324, false));// horizontalLines 0,324-800,324

            setInitialized(true);
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
//            try {
//                leftBoard = verticalLines[0].start.x - 15 > 0 ? verticalLines[0].start.x - 25 : 0;
//                rightBoard = verticalLines[7].start.x + 15 < bi.getWidth() ? verticalLines[7].start.x + 25 : bi.getWidth();
//                topBoard = horizontalLines[0].start.y - 15 > 0 ? topBoard = horizontalLines[0].start.y - 25 : 0;
//                bottomBoard = horizontalLines[7].start.y + 15 < bi.getHeight() ? horizontalLines[7].start.y + 25 : bi.getHeight();
//                logger.debug(String.format("Left =%s Right=%s Top=%s Bottom=%s", leftBoard, rightBoard, topBoard, bottomBoard));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            humanColor = horizontalLines[0].start.color;

            for (int i = 0; i < 8; i++) {
                logger.debug(String.format("verticalLines %s,%s-%s,%s", verticalLines[i].start.x, verticalLines[i].start.y, verticalLines[i].end.x, verticalLines[i].end.y));
                logger.debug(String.format("horizontalLines %s,%s-%s,%s", horizontalLines[i].start.x, horizontalLines[i].start.y, horizontalLines[i].end.x, horizontalLines[i].end.y));
            }
        }
        if (initialized) {
            drawLastBoard(g2);
        }
        g2.drawRect(leftBoard, topBoard, rightBoard - leftBoard, bottomBoard - topBoard);
    }

    public void CheckHumanMove() {
        // Go through all the pieces that are of human color and see which one has moved.
    }

    public void setMove(int[] moves){
        lastBoard[moves[2]][moves[3]] = lastBoard[moves[0]][moves[1]];
        lastBoard[moves[0]][moves[1]] = null;
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
                    try {
                        g2.drawString(p, verticalLines[piece.col].start.x, horizontalLines[piece.row].start.y);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
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
    public int[] detectPieces(BufferedImage bi) throws Exception {
        int[] rv = null;

        Graphics2D g2 = bi.createGraphics();

        BasicStroke stroke = new BasicStroke(2);
        g2.setStroke(stroke);

        ArrayList<PossiblePiece> changedPiece = new ArrayList<>();
        ArrayList<PossiblePiece> takenPiece = new ArrayList<>();

        PossiblePiece[][] currentBoard = new PossiblePiece[8][8];

        boolean[][] array = BlackWhite.convert(bi.getSubimage(leftBoard, topBoard, rightBoard - leftBoard, bottomBoard - topBoard), threshHold);
        ArrayList<PossiblePiece> pieces = detectCircles(array, true);

        int diff = 0;
        int count = 0;
        for (PossiblePiece piece : pieces) {
            markPiece(g2, piece.x, piece.y, piece.color);
            findOffFactor(piece);
            if (piece.offFactor <= 15) {
                if (lastBoard[piece.col][piece.row] == null || lastBoard[piece.col][piece.row].color != piece.color) {
                    diff++;
                    changedPiece.add(piece);
                }
                logger.debug(String.format("Piece %s %s,%s has offset of %s", count++, piece.col, piece.row, piece.offFactor));
                if (currentBoard[piece.col][piece.row] != null) {
                    throw new Exception(String.format("Dectected two pieces on same square col=%s row=%s", piece.col, piece.row));
                }
                currentBoard[piece.col][piece.row] = piece;
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
            lastBoard = new PossiblePiece[8][8];
            for (PossiblePiece piece : pieces) {
                lastBoard[piece.col][piece.row] = piece;
            }
            
            logger.debug(String.format("Piece moved from %s,%s to %s,%s", lastLocation.get(0).col, lastLocation.get(0).row, changedPiece.get(0).col, changedPiece.get(0).row));
//            lastBoard[lastLocation.get(0).col][lastLocation.get(0).row] = null;
//            lastBoard[changedPiece.get(0).col][changedPiece.get(0).row] = changedPiece.get(0);
//            
            rv = new int[]{lastLocation.get(0).col, lastLocation.get(0).row, changedPiece.get(0).col, changedPiece.get(0).row};
        }

        drawLines(g2);
        return rv;
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

    private void markLines(BufferedImage bi, Graphics2D g2) {
        g2.setColor(Color.GREEN);
        logger.debug("Number of pieces are " + detectedPices.size());

        detectedPices.sort((PossiblePiece a, PossiblePiece b) -> Integer.compare(a.x, b.x));
        for (int i = 0; i < 8; i++) {
            double avgX = 0;
            for (int j = 0; j < 4; j++) {
                PossiblePiece point = detectedPices.get(i * 4 + j);
                avgX += point.x;
                logger.debug(String.format("%s %s %s", i, point.x, point.y));
            }
            logger.debug(String.format("average = %s", avgX / 4));
            Point start = new Point((int) (avgX / 4), 0, false);
            Point end = new Point((int) (avgX / 4), bi.getHeight(), false);
            //         vertical[i] = new Line(start,end);
            verticalLines[i] = new Line(start, end);
        }

        detectedPices.sort((PossiblePiece a, PossiblePiece b) -> Integer.compare(a.y, b.y));
        for (int i = 0; i < 4; i++) {
            double avgY = 0;
            for (int j = 0; j < 8; j++) {
                PossiblePiece point = detectedPices.get(i * 8 + j);
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

//        double circle = 0;
//        double nonCircle = 0;
//        for(x = left; x <= right; x++){
//            for(y = top; y <= bottom; y++){
//                if (array[x][y] != piece) {
//                    circle++;
//                }else{
//                    nonCircle++;
//                }
//            }
//        }
//
//        double ratio = circle/nonCircle;
//        if(ratio < 1.3 || ratio > 1.7) return false;
//        
//        logger.debug(String.format("Circle ratio = %f", ratio));
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
        if (!initialized) {
            horizontalLines = new Line[8];
            verticalLines = new Line[8];
            lastBoard = new PossiblePiece[8][8];
        }
    }

    public PossiblePiece[][] getLastBoard() {
        return lastBoard;
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
        try {
            BufferedImage bi = ImageUtil.loadImage("D:\\git\\Chess\\images\\unitTestImages\\board914c6097-40b5-4bb8-a337-3ad18de0412b.png");
            boolean[][] array = BlackWhite.convert(bi.getSubimage(leftBoard, topBoard, rightBoard - leftBoard, bottomBoard - topBoard), 100);

            BoardCalculator boardCalculator = new BoardCalculator();
            boardCalculator.printToConsole(array, 474 - leftBoard, 322 - topBoard);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(BoardCalculator.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
