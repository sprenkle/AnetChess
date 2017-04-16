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
public class King extends ChessPiece {

    public King(int color, int x, int y) throws InvalidLocationException {
        super(color, x, y);
        name = name + "K";
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
        int xDiff = abs(location.getX() - this.location.getX());
        int yDiff = abs(location.getY() - this.location.getY());
        if ((yDiff == 1 && (xDiff == 0 || xDiff == 1)) || (xDiff == 1 && (yDiff == 1 || yDiff == 0))) {
            ChessPiece pieceAtPosition = board.getPiece(location.getX(), location.getY());
            if (pieceAtPosition == null || pieceAtPosition.getColor() != this.getColor()) {
                return true;
            }
        }

        if (!getHasMoved() && ((location.getX() == 2 && this.getLocation().getX()== 4)|| (location.getX() == 6 && this.getLocation().getX()== 4))
                && !board.isKingInCheck(getColor())) {
            if (location.getX() == 2) {
                return board.getPiece(1,this.location.getY()) == null
                        && board.getPiece(2,this.location.getY()) == null
                        && board.getPiece(3,this.location.getY()) == null;
            }
            return board.getPiece(5,this.location.getY()) == null
                    && board.getPiece(6,this.location.getY()) == null;
        }
        return false;
    }

}
