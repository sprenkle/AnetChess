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
public class Pawn extends ChessPiece{

    public Pawn(int color, int x, int y) throws InvalidLocationException {
        super(color, x, y);
        name = name + "P";
    }

    @Override
    public ArrayList<PieceLocation> validMoves(Board board) {
        ArrayList<PieceLocation> validMoves = new ArrayList<>();
        int direction = getColor() == 0 ? 1 : -1;
        
        
        if(board.getPiece(this.getLocation().getX(), this.getLocation().getY() + direction) == null){
            try {
                validMoves.add(new PieceLocation(this.getLocation().getX(),this.getLocation().getY() + 1));
            } catch (InvalidLocationException ex) {
                Logger.getLogger(Pawn.class.getName()).log(Level.SEVERE, null, ex);
            }
        } 
                
        if(this.getLocation().getY() + (2 * direction) >= 0 &&  this.getLocation().getY() + (2 * direction) <= 7 && board.getPiece(this.getLocation().getX(), this.getLocation().getY() + (2 * direction)) == null){
            try {
                validMoves.add(new PieceLocation(this.getLocation().getX(),this.getLocation().getY() + (direction * 2)));
            } catch (InvalidLocationException ex) {
                Logger.getLogger(Pawn.class.getName()).log(Level.SEVERE, null, ex);
            }
        } 
        
        ChessPiece takePiece =  board.getPiece(this.getLocation().getX() - 1, this.getLocation().getY() + direction);
        if(takePiece != null && takePiece.getColor() != this.getColor()){
            try {
                validMoves.add(new PieceLocation(this.getLocation().getX() - 1,this.getLocation().getY() + direction));
            } catch (InvalidLocationException ex) {
                Logger.getLogger(Pawn.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        takePiece =  board.getPiece(this.getLocation().getX() + 1, this.getLocation().getY() + direction);
        if(takePiece != null && takePiece.getColor() != this.getColor()){
            try {
                validMoves.add(new PieceLocation(this.getLocation().getX() + 1,this.getLocation().getY() + direction));
            } catch (InvalidLocationException ex) {
                Logger.getLogger(Pawn.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return validMoves;
    }

    @Override
    public boolean isValidMoveTo(Board board, PieceLocation location) {
        if(location.getX() > 7 || location.getX() < 0 || location.getY() > 7 || location.getX() < 0) return false;
        
        int direction = getColor() == 0 ? 1 : -1;
        
        if(location.getX() == this.location.getX() && location.getY() == this.location.getY() + direction){
            return board.getPiece(location.getX(), location.getY()) == null;
        } 
                
        if(location.getX() == this.location.getX() && location.getY() == this.location.getY() + (2 * direction)){
            return board.getPiece(location.getX(), location.getY() - direction) == null && board.getPiece(location.getX(), location.getY()) == null;
        } 

        if(location.getX() == this.location.getX() + 1 && location.getY() == this.location.getY() + direction){
            return board.getPiece(location.getX(), location.getY()) != null 
                    && board.getPiece(location.getX(), location.getY()).getColor() != this.getColor();
        } 
  
        if(location.getX() == this.location.getX() - 1 && location.getY() == this.location.getY() + direction){
            return board.getPiece(location.getX(), location.getY()) != null 
                    && board.getPiece(location.getX(), location.getY()).getColor() != this.getColor();
        } 
  
        
        return false;
    }
    
}
