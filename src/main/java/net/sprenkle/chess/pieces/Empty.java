/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sprenkle.chess.pieces;

import java.util.ArrayList;
import net.sprenkle.chess.exceptions.InvalidLocationException;

/**
 *
 * @author David
 */
public class Empty extends ChessPiece {

    public Empty(int color, int x, int y) throws InvalidLocationException {
        super(color, x, y);
    }

    @Override
    public ArrayList<PieceLocation> validMoves(Board board) {
        return new ArrayList<>();
    }

    @Override
    public boolean isValidMoveTo(Board board, PieceLocation location) {
        return false;
    }
    
}
