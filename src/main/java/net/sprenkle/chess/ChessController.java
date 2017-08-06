/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess;

import net.sprenkle.chess.models.PossiblePiece;
import java.util.ArrayList;
import net.sprenkle.chess.exceptions.InvalidMoveException;
import net.sprenkle.chess.pieces.Board;

/**
 *
 * @author David
 */
public class ChessController implements ChessControllerInterface {
    static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ChessController.class.getSimpleName());

    private final ArrayList<ChessMove> moves = new ArrayList<>();
    private Board board;
    
    public ChessController() {
        board = new Board();
        board.setStartingPositionBoard();
    }

    @Override
    public void consoleOut(){
        board.consoleOut();
    }
    
    public Board getBoard(){
        return board;
    }
    
    public int getNumActivePieces(){
        return board.getActivePieces()[0].size() + board.getActivePieces()[1].size();
    }
    
    @Override
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
    
    @Override
    public boolean isLastMoveCastle(){
        return board.isLastMoveCastle();
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

    @Override
    public String getMoves(){
        StringBuilder moveCommand = new StringBuilder();
        moveCommand.append("position startpos moves ");

        moves.stream().map((move) -> {
            moveCommand.append(move.getMove());
            return move;
        }).forEachOrdered((_item) -> {
            moveCommand.append(" ");
        });

        return moveCommand.toString();
    }

    
    @Override
    public PossiblePiece[][] getKnownBoard() {
        return board.convertToCameraBoard();
    }

    @Override
    public void reset() {
        board = new Board();
        board.setStartingPositionBoard();
    }
}
