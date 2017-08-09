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
import java.util.ArrayList;
import java.util.List;
import net.sprenkle.chess.BoardProperties;
import net.sprenkle.chess.ObjectDetector;
import net.sprenkle.chess.Player;
import net.sprenkle.chess.messages.ChessMessageSender;
import net.sprenkle.chess.messages.GameInformation;
import net.sprenkle.chess.messages.MessageHolder;
import net.sprenkle.chess.models.DetectedObject;
import net.sprenkle.chess.models.GridObject;
import net.sprenkle.chess.models.PossiblePiece;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.log4j.Logger;

/**
 *
 * @author david
 */
public class BoardCalculator {
    ObjectDetector objectDetector = new ObjectDetector();
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
    private final ChessMessageSender messageSender;

    static Player humanColor;
//    private ArrayList<PossiblePiece> detectedPieces;

    static int lastNumPieces = 16;

    static double xSlope = -0.4262;
    static double ySlope = 0.4271;
    static double xIntercept = 175.376;
    static double yIntercept = -165.4933;
    private int threshHold = 130;

    public BoardCalculator(BoardProperties boardProperties, ChessMessageSender messageSender) {
        this.messageSender = messageSender;
        
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

    public boolean verifyStartingPosition(List<GridObject> gridObjects){
        if(gridObjects.size() == 32){
            boolean[][] pieces = new boolean[8][];
            gridObjects.forEach((gridObject) -> {
                pieces[gridObject.getCol()][gridObject.getRow()] = true;
            });
            
            for(int y = 0; y < 8; y++){
                if(y == 2) y = 6;
                for(int x = 0 ; x < 8; y++){
                    if(!pieces[x][y]){
                        messageSender.send(new MessageHolder(new GameInformation(String.format("No piece on %s,%s.", x, y))));
                        return false;
                    }
                }
            }
            return true;
        }
        messageSender.send(new MessageHolder(new GameInformation(String.format("Should have 32 pieces, have %s.", gridObjects.size()))));
        return false;
    }
    
    
//    public void showCircles(BufferedImage bi) {
//        boolean[][] array = BlackWhite.convert(bi.getSubimage(leftBoard, topBoard, rightBoard - leftBoard, bottomBoard - topBoard), threshHold);
//        Graphics2D g2 = bi.createGraphics();
//        BasicStroke stroke = new BasicStroke(2);
//        g2.setStroke(stroke);
//        g2.setColor(Color.GREEN);
//        drawLines(g2);
//
//        List<DetectedObject> pieces = objectDetector.detectObjects(array, leftBoard, topBoard);
//        pieces.forEach((piece) -> markPiece(g2, piece.getX(), piece.getY(), piece.getColor()));
//        g2.drawRect(leftBoard, topBoard, rightBoard - leftBoard, bottomBoard - topBoard);
//
//        List<DetectedObject> markers = detectBoardMarker(bi);
//        markers.forEach((piece) -> markPiece(g2, piece.getX(), piece.getY(), Color.CYAN));
//        g2.setColor(Color.YELLOW);
//        g2.drawRect(leftDetect, topDetect, rightDetect - leftDetect, bottomDetect - topDetect);
//
//        List<DetectedObject> hook = detectHook(bi);
//        hook.forEach((piece) -> markPiece(g2, piece.getX(), piece.getY(), Color.PINK));
//        g2.setColor(Color.PINK);
//        g2.drawRect(leftHook, topHook, rightHook - leftHook, bottomHook - topHook);
//    }


    /**
     * Just checks to see if there are 32 piece on the board, this needs to be looked at.
     * @param bi
     * @return 
     */
    public boolean initialLines(BufferedImage bi) {
        Graphics2D g2 = bi.createGraphics();

        BasicStroke stroke = new BasicStroke(2);
        g2.setStroke(stroke);
        if (!isInitialized()) {
            boolean[][] array = BlackWhite.convert(bi.getSubimage(leftBoard, topBoard, rightBoard - leftBoard, bottomBoard - topBoard), threshHold);

            List<DetectedObject> detectedPieces = objectDetector.detectObjects(array, topDetect, topDetect);

            markLines(bi,detectedPieces, g2);

            if (detectedPieces == null || detectedPieces.size() != 32) {
                return false;
            }

            setInitialized(true);

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
                    switch (piece.objectId) {
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
                       logger.error(e.getMessage()); 
                    }
                }
            }
        }
    }

    private void markLines(BufferedImage bi, List<DetectedObject> detectedPieces, Graphics2D g2) {
        g2.setColor(Color.GREEN);
        logger.debug("Number of pieces are " + detectedPieces.size());
        detectedPieces.sort((DetectedObject a, DetectedObject b) -> Integer.compare(a.getX(), b.getY()));
        int minY = detectedPieces.get(0).getY();
        int maxY = detectedPieces.get(detectedPieces.size() - 1).getY();

        detectedPieces.sort((DetectedObject a, DetectedObject b) -> Integer.compare(a.getX(), b.getX()));
        for (int i = 0; i < 8; i++) {
            SimpleRegression simpleRegression = new SimpleRegression(true);
            simpleRegression = new SimpleRegression(true);
            double avgX = 0;
            for (int j = 0; j < 4; j++) {
                DetectedObject point = detectedPieces.get(i * 4 + j);
                avgX += point.getX();
            }
            Point start = new Point((int) (avgX / 4), 0, Player.Black);
            Point end = new Point((int) (avgX / 4), bi.getHeight(), Player.Black);
            //         vertical[i] = new Line(start,end);
            verticalLines[i] = new Line(start, end);
        }

        detectedPieces.sort((DetectedObject a, DetectedObject b) -> Integer.compare(a.getY(), b.getY()));
        for (int i = 0; i < 4; i++) {
            SimpleRegression simpleRegression = new SimpleRegression();
            double avgY = 0;
            for (int j = 0; j < 8; j++) {
                DetectedObject point = detectedPieces.get(i * 8 + j);
                avgY += point.getY();
                //logger.debug(String.format("%s %s %s",i, point.x, point.y));
                simpleRegression.addData(point.getX(), point.getY());
            }

            minY = (int) (simpleRegression.getIntercept());
            maxY = (int) (bi.getWidth() * simpleRegression.getSlope() + simpleRegression.getIntercept());
            Point start = new Point(0, (int) (avgY / 8), detectedPieces.get(i * 8).getColor());
            Point end = new Point(bi.getWidth(), (int) (avgY / 8), detectedPieces.get(i * 8).getColor());
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

    public List<DetectedObject> detectBoardMarker(BufferedImage bi) {
        boolean[][] array = BlackWhite.convert(bi.getSubimage(leftDetect, topDetect, rightDetect - leftDetect, bottomDetect - topDetect), threshHold);
        List<DetectedObject> pieces = objectDetector.detectObjects(array, topDetect, topDetect);
        return pieces;
    }

    public List<DetectedObject> detectHook(BufferedImage bi) {
        boolean[][] array = BlackWhite.convert(bi.getSubimage(leftHook, topHook, rightHook - leftHook, bottomHook - topHook), threshHold);
        List<DetectedObject> pieces = objectDetector.detectObjects(array, topDetect, topDetect);
        return pieces;
    }

    public int getHookWidth(BufferedImage bi) {
        List<DetectedObject> hooks = detectHook(bi);
        if (hooks == null || hooks.size() != 1) {
            return -1;
        }
        DetectedObject hook = hooks.get(0);

        boolean[][] array = BlackWhite.convert(bi.getSubimage(hook.getX() - 15, hook.getY() - 15, 30, 30), threshHold);

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
     * @param pieces
     * @param knownBoard
     * @return true if camera and knownBoard are the same
     */
    public boolean verifyPiecePositions(List<GridObject> pieces, PossiblePiece[][] knownBoard) {
        if (!pieces.stream().noneMatch((piece) -> (knownBoard[piece.getCol()][piece.getRow()] == null))) {
            return false;
        } // todo add message that says image does not match Known Board
        
        
        pieces.sort((t1, t2) -> Double.compare(t1.getOffset(), t2.getOffset()));

        for (int x = 0; x < 7; x++) {
            for (int y = 0; y < 7; y++) {
                if (knownBoard[x][y] != null) {
                    boolean foundMatchingPiece = false;
                    for (GridObject piece : pieces) {
                        if (piece.getCol() == x && piece.getRow() == y) {
                            knownBoard[x][y].x = piece.getX();
                            knownBoard[x][y].y = piece.getY();
                            foundMatchingPiece = true;
                            break;
                        }
                    }
                    if (!foundMatchingPiece) {
                        // todo add message that says image does not match Known Board
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
     * @param pieces
     * @param turn
     * @param lastBoard
     * @return 
     * @throws java.lang.Exception
     */
    public int[] detectPieces(List<GridObject> pieces, Player turn, PossiblePiece[][] lastBoard) throws Exception {
        int[] rv = null;
        ArrayList<GridObject> changedPiece = new ArrayList<>();
        GridObject[][] currentBoard = new GridObject[8][8];
        int diff = 0;
        GridObject[][] tempPiecePositions = new GridObject[8][8];
        pieces.sort((t1, t2) -> Double.compare(t1.getOffset(), t2.getOffset()));
        List<GridObject> duplicates = new ArrayList<>();
        for (GridObject piece : pieces) {
            if (tempPiecePositions[piece.getCol()][piece.getRow()] != null) {
                logger.debug(String.format("Duplicate Piece %s,%s has offset of %s", piece.getCol(), piece.getRow(), piece.getOffset()));
                duplicates.add(piece);
                continue;
            }
            if (piece.getOffset() <= 200) {
                if (knownBoard[piece.getCol()][piece.getRow()] == null || !knownBoard[piece.getCol()][piece.getRow()].color.equals(piece.getColor())) {
                    logger.debug(String.format("Difference in Piece %s,%s has offset of %s", piece.getCol(), piece.getRow(), piece.getOffset()));
                    diff++;
                    changedPiece.add(piece);
                }
                if (currentBoard[piece.getCol()][piece.getRow()] == null || currentBoard[piece.getCol()][piece.getRow()].getOffset() > piece.getOffset()) {
                    currentBoard[piece.getCol()][piece.getRow()] = piece;
                }
            }
            tempPiecePositions[piece.getCol()][piece.getRow()] = piece;
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

        if (lastLocation.size() == 1 && changedPiece.size() == 1 && turn.equals(changedPiece.get(0).getColor())) {
            logger.debug(String.format("Piece moved from %s,%s to %s,%s", lastLocation.get(0).col, lastLocation.get(0).row, changedPiece.get(0).getCol(), changedPiece.get(0).getRow()));
            rv = new int[]{lastLocation.get(0).col, lastLocation.get(0).row, changedPiece.get(0).getCol(), changedPiece.get(0).getRow()};
        } else if (changedPiece.size() == 2 && checkForCastle(changedPiece.get(0), changedPiece.get(1)) != null) {
            rv = checkForCastle(changedPiece.get(0), changedPiece.get(1));
        }
        return rv;
    }

    private int[] checkForCastle(GridObject p1, GridObject p2) {
        if ((p1.getRow() > 0 && p1.getRow() < 7) || p1.getRow() != p2.getRow()) {
            return null;
        }

        GridObject king = null;
        GridObject rook = null;

        switch (p1.getCol()) {
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

        switch (p2.getCol()) {
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

        if (king == null || rook == null || Math.abs(king.getCol() - rook.getCol()) != 1) {
            return null;
        }

        if (king.getCol() == 2) {
            return new int[]{3, king.getRow(), king.getCol(), king.getRow()};
        } else {
            return new int[]{3, king.getRow(), king.getCol(), king.getRow()};
        }
    }


    private void drawLines(Graphics2D g2) {
        for (int i = 0; i < 8; i++) {
            g2.drawLine(horizontalLines[i].start.x, horizontalLines[i].start.y, horizontalLines[i].end.x, horizontalLines[i].end.y);
            g2.drawLine(verticalLines[i].start.x, verticalLines[i].start.y, verticalLines[i].end.x, verticalLines[i].end.y);
        }
    }

//    private void markPiece(Graphics2D g2, int x, int y, Player piece) {
//        Color color = Color.BLACK;
//        if (piece == Player.White) {
//            color = Color.RED;
//        } else {
//            color = Color.BLUE;
//        }
//        markPiece(g2, x, y, color);
//    }

    private void markPiece(Graphics2D g2, int x, int y, Color color) {
        int size = 8;
        g2.setColor(color);
        g2.drawLine(x - size, y - size, x + size, y - size);
        g2.drawLine(x + size, y - size, x + size, y + size);
        g2.drawLine(x + size, y + size, x - size, y + size);
        g2.drawLine(x - size, y + size, x - size, y - size);
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
