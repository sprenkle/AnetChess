/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.pieces;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sprenkle.chess.exceptions.InvalidLocationException;

/**
 *
 * @author David
 */
public abstract class ChessPiece {

    protected PieceLocation location;
    protected final int color;
    private boolean hasMoved = false;
    protected String name;

    public ChessPiece(int color, int x, int y) throws InvalidLocationException {
        location = new PieceLocation(x, y);
        this.color = color;
        if(color == 0){
            name = "W";
        }else{
            name = "B";
        }
        
    }

    public String getName(){
        return name;
    }
    
    public void setHasMoved(){
        hasMoved = true;
    } 
    
    public boolean getHasMoved(){
        return hasMoved;
    }
    
    public void SetLocation(int x, int y) {
        try {
            location = new PieceLocation(x,y);
        } catch (InvalidLocationException ex) {
            Logger.getLogger(ChessPiece.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void SetLocation(PieceLocation pl) {
            location = pl;
    }

    public int getColor() {
        return color;
    }

    public PieceLocation getLocation() {
        return location;
    }

    public int returnColor() {
        return color;
    }

    public boolean move() {
        return true;
    }

    public abstract ArrayList<PieceLocation> validMoves(Board board);

    public abstract boolean isValidMoveTo(Board board, PieceLocation location);

    protected void validMovesInDirection(Board board, ArrayList<PieceLocation> validMoves, int xIncrement, int yIncrement) {
        for (int i = 1; i < 8; i++) {
            try {
                int newX = location.getX() + (i * xIncrement);
                int newY = location.getY() + (i * yIncrement);
                
                if(newX < 0 || newX > 7 || newY < 0 || newY > 7) return; 
                
                PieceLocation pl = new PieceLocation(location.getX() + (i * xIncrement), location.getY() + (i * yIncrement));
                if (isValidMoveTo(board, pl)) {
                    validMoves.add(pl);
                } else {
                    return;
                }
            } catch (InvalidLocationException ex) {
                Logger.getLogger(Bishop.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
