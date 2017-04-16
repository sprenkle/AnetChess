/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.states;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.logging.Logger;
import net.sprenkle.chess.CameraToBoard;
import net.sprenkle.chess.ChessEngine;
import net.sprenkle.chess.StockFishUCI;
import net.sprenkle.chess.imaging.BoardDetails;
import net.sprenkle.chess.imaging.ChessBoardImage;
import net.sprenkle.chess.imaging.SquareValue;
import net.sprenkle.chess.pieces.ChessPiece;

/**
 *
 * @author David
 */
public abstract class State {

    private static final ChessBoardImage cbi;
    private static BoardDetails boardDetails;
    public static final ChessEngine engine;
    protected String name;
    private final static HashMap<String, State> states;
    //protected final int[][] squareValues = new int[8][8];
    protected int[][] goodSquareValues = new int[8][8];

    protected ArrayList<SquareValue> diffList = new ArrayList<>();

    public final static String STARTSTATE = "STARTSTATE";
    public final static String HUMANSTATE = "HUMANSTATE";
    public final static String ENGINESTATE = "ENGINESTATE";
    public final static String UNKNOWNSTATE = "UNKNOWNSTATE";
    public static int turnToMove = 0;
    public static int humanColor = 0;
    public static int engineColor = 1;
    protected static double emptySquareDetectionValue = -1;
    protected static Logger stateLogger = Logger.getLogger("State");
    public final static int secondsForBoardCheck = 3;
    
    static {
        String filename = "C:\\dev\\Chess\\Chess\\boardDetail.ser";

        FileInputStream fis;
        ObjectInputStream in;

        try {
            fis = new FileInputStream(filename);
            in = new ObjectInputStream(fis);
            boardDetails = (BoardDetails) in.readObject();
            in.close();
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }

        cbi = new ChessBoardImage(boardDetails.getTop(), boardDetails.getBottom(), boardDetails.getLeft(), boardDetails.getRight(), boardDetails.getSquareWidth(), boardDetails.getSquareHeight());

        engine = new ChessEngine(new StockFishUCI());
        engine.newGame();

        states = new HashMap<>();
        states.put(STARTSTATE, new StartState());
        states.put(HUMANSTATE, new HumanMoveState());
        states.put(ENGINESTATE, new EngineMoveState());
        states.put(UNKNOWNSTATE, new UnknownPositionState());

    }

    public String getName() {
        return name;
    }

    public State process(BufferedImage bi) {
        int[][] squareValues = new int[8][8];
        diffList.clear();
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                int boardX = CameraToBoard.getX(x);
                int boardY = CameraToBoard.getY(y);
                diffList.add(new SquareValue(boardX, boardY, cbi.getDiffSquare(x, y, bi)));
                squareValues[boardX][boardY] = cbi.getAvgGreyValue(x, y, bi);
            }
        }

        Collections.sort(diffList, new Comparator<SquareValue>() {
            @Override
            public int compare(SquareValue v1, SquareValue v2) {
                return Double.compare(v2.value, v1.value);
            }
        });

        ChessPiece cp = null;
        int nonMatchingSquares = 0;
        int pieceTaken = 0;
        int activePieceNumber = engine.getNumActivePieces();

        for(int x = 0 ; x < 8; x++){
            for(int y = 0 ; y < 8 ; y++){
              //  DetectUtil.debugSquare(boardDetails,x, y, engine.getBoard(), diffList, bi);
            }
        }
        for (int i = 0; i < activePieceNumber; i++) {
            SquareValue sv = diffList.get(i);
            if (emptySquareDetectionValue < 0 || emptySquareDetectionValue < sv.value) {
                cp = engine.getBoard().getPiece(sv.x, sv.y);
                if (cp == null) {
                    nonMatchingSquares++;
                }
             //   DetectUtil.displaySquare(cp != null ? cp.getName() : "", boardDetails, CameraToBoard.BoardToCameraX(sv.x), CameraToBoard.BoardToCameraY(sv.y), bi);
            } else {
            //    DetectUtil.displaySquare("?", boardDetails, CameraToBoard.BoardToCameraX(sv.x), CameraToBoard.BoardToCameraY(sv.y), bi);
                pieceTaken++;
            }
        }
        return stateProcess(squareValues, nonMatchingSquares, pieceTaken);
    }

    public abstract State stateProcess(int[][] squareValues, int nonMatchingSquares, int pieceTaken);

    public abstract void initialize();

    public static State getState(String state) {
        System.out.println(state);
        stateLogger.info("Changing to " + state);
        State s = states.get(state);
        s.initialize();
        return s;
    }

    public static boolean isStartState(ArrayList<SquareValue> diffList) {
        for (int i = 0; i < 32; i++) {
            SquareValue sv = diffList.get(i);
            ChessPiece cp = engine.getBoard().getPiece(CameraToBoard.getX(sv.x), CameraToBoard.getY(sv.y));
            if (cp == null) {
                return false;
            }
        }
        return true;
    }

}
