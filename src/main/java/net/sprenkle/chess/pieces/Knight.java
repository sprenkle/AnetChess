/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.pieces;

import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sprenkle.chess.exceptions.InvalidLocationException;

/**
 *
 * @author David
 */
public class Knight extends ChessPiece {

    public Knight(int color, int x, int y) throws InvalidLocationException {
        super(color, x, y);
        name = name + "N" ;
    }

        @Override
    public ArrayList<PieceLocation> validMoves(Board board) {
        ArrayList<PieceLocation> validMoves = new ArrayList<>();
        validKnightMove(board, validMoves, -2, 1);
        validKnightMove(board, validMoves, -1, 2);
        validKnightMove(board, validMoves, 1, 2);
        validKnightMove(board, validMoves, 2, 1);
        validKnightMove(board, validMoves, 2, -1);
        validKnightMove(board, validMoves, 1, -2);
        validKnightMove(board, validMoves, -1, -2);
        validKnightMove(board, validMoves, -2, -1);
        return validMoves;
    }

    private void validKnightMove(Board board,ArrayList<PieceLocation> validMoves,  int x, int y){
        if(x<0 || x>7 || y < 0 || y > 7) return;
        ChessPiece cp = board.getPiece(x, y);
        if(cp == null || cp.getColor() != this.getColor())
        {
            try {
                validMoves.add(new PieceLocation(x,y));
            } catch (InvalidLocationException ex) {
                Logger.getLogger(Knight.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public boolean isValidMoveTo(Board board, PieceLocation location) {
        if ((abs(location.getX() - this.location.getX()) == 2 && abs(location.getY() - this.location.getY()) == 1)
                || (abs(location.getX() - this.location.getX()) == 1 && abs(location.getY() - this.location.getY()) == 2)) {
            return board.getPiece(location.getX(), location.getY()) == null || board.getPiece(location.getX(), location.getY()).getColor() != this.getColor();
        }
        return false;
    }

}
