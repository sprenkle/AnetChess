/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.pieces;

import static java.lang.Math.abs;
import java.util.ArrayList;
import net.sprenkle.chess.exceptions.InvalidLocationException;

/**
 *
 * @author David
 */
public class Queen extends ChessPiece {

    public Queen(int color, int x, int y) throws InvalidLocationException {
        super(color, x, y);
        name = name + "Q";

    }

    @Override
    public ArrayList<PieceLocation> validMoves(Board board) {
        ArrayList<PieceLocation> validMoves = new ArrayList<>();
        validMovesInDirection(board, validMoves, 0, 1);
        validMovesInDirection(board, validMoves, 1, 0);
        validMovesInDirection(board, validMoves, 0, -1);
        validMovesInDirection(board, validMoves, -1, 0);
        validMovesInDirection(board, validMoves, 1, 1);
        validMovesInDirection(board, validMoves, 1, -1);
        validMovesInDirection(board, validMoves, -1, 1);
        validMovesInDirection(board, validMoves, -1, -1);
        return validMoves;
    }

    @Override
    public boolean isValidMoveTo(Board board, PieceLocation location) {
        if (location.getX() == this.location.getX() || location.getY() == this.location.getY()) {
            int xincrement = location.getX() == this.location.getX() ? 0 : (location.getX() - this.location.getX()) / abs(location.getX() - this.location.getX());
            int yincrement = location.getY() == this.location.getY() ? 0 : (location.getY() - this.location.getY()) / abs(location.getY() - this.location.getY());

            int testX = this.location.getX() + xincrement;
            int testY = this.location.getY() + yincrement;
            while (location.getX() != testX || location.getY() != testY) {
                if (board.getPiece(testX, testY) != null) {
                    return false;
                }
                testX += xincrement;
                testY += yincrement;
            }

            return board.getPiece(testX, testY) == null || board.getPiece(testX, testY).getColor() != this.getColor();
        }

        double slope = (double)(location.getY() - this.location.getY())/(double)(location.getX() - this.location.getX());
        
        if(abs(slope) != 1) 
            return false;
        
        int xincrement = (location.getX() - this.location.getX())/abs(location.getX() - this.location.getX());
        int yincrement = (location.getY() - this.location.getY())/abs(location.getY() - this.location.getY());
        
        int testX = this.location.getX() + xincrement;
        int testY = this.location.getY() + yincrement;
        while(location.getX() != testX){
            if(board.getPiece(testX, testY) != null) return false;
            testX += xincrement;
            testY += yincrement;
        }
        
        return board.getPiece(testX, testY) == null || board.getPiece(testX, testY).getColor() != this.getColor();
    }

}
