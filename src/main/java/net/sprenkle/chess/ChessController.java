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
import net.sprenkle.chess.exceptions.InvalidMoveException;
import net.sprenkle.chess.pieces.Board;

/**
 *
 * @author David
 */
public class ChessController implements ChessControllerInterface {
    static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ChessController.class.getSimpleName());

    //private final ChessResponseListenerInterface listener;
//    private Scanner stdin;
//    private BufferedWriter bw;
    private final ArrayList<ChessMove> moves = new ArrayList<>();
    private final Board board;
    private final BufferedWriter fileBufferedWriter;
    
    public ChessController() {
        board = new Board();
        File file = new File("chess.txt");
        try {
            file.createNewFile();
        } catch (IOException ex) {
            logger.error(ex);
        }
        FileWriter fw = null;
        try {
            fw = new FileWriter(file.getAbsoluteFile());
        } catch (IOException ex) {
            logger.error(ex);
        }
	fileBufferedWriter = new BufferedWriter(fw);
    }

    public void consoleOut(){
        board.consoleOut();
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
        logger.debug("New Game");
    }

    @Override
    public String makeMove(String move){
        try {
            if(board.makeMove(move)){
                move(move);
                logger.debug(move);
                return "moveOk";
            }else{
                logger.debug("Error " + move);
            }
        } catch (InvalidMoveException ex) {
            logger.error( ex);
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

    public String getMoves(){
        StringBuilder moveCommand = new StringBuilder();
        moveCommand.append("position startpos moves ");

        for (ChessMove move : moves) {
            moveCommand.append(move.getMove());
            moveCommand.append(" ");
        }

        return moveCommand.toString();
    }

    @Override
    public PossiblePiece[][] getKnownBoard() {
        return board.convertToCameraBoard();
    }
}
