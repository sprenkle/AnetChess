/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.pieces;

import java.util.ArrayList;
import net.sprenkle.chess.exceptions.InvalidMoveException;

/**
 *
 * @author david
 */
public interface BoardInterface {

    void addPiece(ChessPiece piece);

    ArrayList<ChessPiece>[] getActivePieces();

    ChessPiece getPiece(int x, int y);

    boolean isAbleToMove(int color);

    boolean isKingInCheck(int color);

    boolean makeMove(PieceLocation from, PieceLocation to, String promoteTo);

    boolean makeMove(String move) throws InvalidMoveException;

    ChessPiece removePiece(int x, int y);

    ChessPiece removePiece(ChessPiece rp);

    void setStartingPositionBoard();

    boolean validMove(PieceLocation from, PieceLocation to);
    
}
