/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess;

/**
 *
 * @author david
 */
public class PossiblePiece {
    public static final int KING = 5;
    public static final int QUEEN = 4;
    public static final int ROOK = 3;
    public static final int BISHOP = 2;
    public static final int KNIGHT = 1;
    public static final int PAWN = 0;

    public int x;
    public int y;
    public Player color;
    public double offFactor;
    public int col;
    public int row;
    public int rank;
    
    public PossiblePiece(int x, int y, Player color){
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public PossiblePiece(Player color, int rank, int col, int row){
        this.col = col;
        this.row = row;
        this.color = color;
        this.rank = rank;
    }
 
    @Override
    public String toString(){
        return String.format("%s %s %s,%s", rank, color, col, row);
    }
}
