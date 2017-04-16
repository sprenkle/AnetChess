/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sprenkle.chess.exceptions.InvalidMoveException;
import net.sprenkle.chess.pieces.Board;

/**
 *
 * @author David
 */
public class ChessEngine {

    //private final ChessResponseListenerInterface listener;
//    private Scanner stdin;
//    private BufferedWriter bw;
    private final ArrayList<ChessMove> moves = new ArrayList<>();
    private final Board board;
    private final BufferedWriter fileBufferedWriter;
    Logger moveLogger = Logger.getLogger(ChessEngine.class.getName());
    UCIInterface uci;
    
    public ChessEngine(UCIInterface uci) {
        this.uci = uci;

        initializeEngine();
        board = new Board();
        File file = new File("chess.txt");
        try {
            file.createNewFile();
        } catch (IOException ex) {
            Logger.getLogger(ChessEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        FileWriter fw = null;
        try {
            fw = new FileWriter(file.getAbsoluteFile());
        } catch (IOException ex) {
            Logger.getLogger(ChessEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
	fileBufferedWriter = new BufferedWriter(fw);
			
    }

    public Board getBoard(){
        return board;
    }
    
    public int getNumActivePieces(){
        return board.getActivePieces()[0].size() + board.getActivePieces()[1].size();
    }
    
    public void newGame() {

        board.setStartingPositionBoard();
        moves.clear();
        uci.sendCommand("ucinewgame");
        uci.sendCommandAndWait("isready", "readyok");
        moveLogger.info("New Game");
    }

    public String makeMove(String move){
        try {
            if(board.makeMove(move)){
                move(move);
                moveLogger.info(move);
                return "moveOk";
            }else{
                moveLogger.info("Error " + move);
            }
        } catch (InvalidMoveException ex) {
            Logger.getLogger(ChessEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "Invalid Move";
    }
    
    public boolean isInCheck(int color){
        return board.isKingInCheck(color);
    }
    
    public boolean isAbleToMove(int color){
       return board.isAbleToMove(color);
    }
    
    private void move(String move) {
        try {
            moves.add(new ChessMove(move));
        } catch (Exception e) {
        }
    }

    public String getBestMove() {
        StringBuilder moveCommand = new StringBuilder();
        moveCommand.append("position startpos moves ");

        for (ChessMove move : moves) {
            moveCommand.append(move.getMove());
            moveCommand.append(" ");
        }

        uci.sendCommand(moveCommand.toString());
        String move = uci.sendCommandAndWait("go " + getTimeString(), "bestmove");
        move = move.substring(9, move.indexOf(" ", 10));
        System.out.println(move);
        addEngineMove(move);
        return move;
    }
    
    private void addEngineMove(String move){
        moveLogger.info(move);

        if(moves.isEmpty() || !moves.get(moves.size()-1).getMove().equals(move)){
            moves.add(new ChessMove(move));
            try {
                if(!board.makeMove(move)){
                    Logger.getLogger(ChessEngine.class.getName()).log(Level.SEVERE,"Engine Move Failed");
                }
            } catch (InvalidMoveException ex) {
                Logger.getLogger(ChessEngine.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private String getTimeString() {
        return "wtime 300000 btime 300000 winc 0 binc 0";
    }


    /**
     * *
     * Sets the options of the Engine. I am using Stockfish and hardcoding the
     * options. Work could be done to read these from a file or something. Note:
     * options are everything after "setoption name"
     */
    private void setOptions() {
        String[] options = {"Write Search Log value false"
                ,"Search Log Filename value SearchLog.txt"
                , "Book File value book.bin"
                , "Best Book Move value false"
                , "Contempt Factor value 0"
                , "Mobility (Midgame) value 100"
                , "Mobility (Endgame) value 100"
                , "Pawn Structure (Midgame) value 100"
                , "Pawn Structure (Endgame) value 100"
                , "Passed Pawns (Midgame) value 100"
                , "Passed Pawns (Endgame) value 100"
                , "Space value 100", "Aggressiveness value 100"
                , "Cowardice value 100"
                , "Min Split Depth value 0"
                , "Max Threads per Split Point value 5"
                , "Threads value 1"
                , "Idle Threads Sleep value false"
                , "Hash value 128"
                , "Ponder value true"
                , "OwnBook value true"
                , "MultiPV value 1"
                , "Skill Level value 20"
                , "Emergency Move Horizon value 40"
                , "Emergency Base Time value 200"
                , "Emergency Move Time value 70"
                , "Minimum Thinking Time value 20"
                , "Slow Mover value 100"
                , "UCI_Chess960 value false"
                , "UCI_AnalyseMode value false"
        };
        for (String option : options) {
            uci.sendCommand("setoption name " + option);
        }
    }


    private void initializeEngine() {
        uci.sendCommandAndWait("uci", "uciok");
        setOptions();
        uci.sendCommandAndWait("isready", "readyok");

    }



    public static void main(String args[]) {
        ChessEngine ce = new ChessEngine(new StockFishUCI());
        ce.newGame();
        ce.move("e2e4");
        ce.getBestMove();
        ce.move("d2d4");
        ce.getBestMove();
    }
}
