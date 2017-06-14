/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.messages;

import java.io.Serializable;

/**
 *
 * @author david
 */
public class ConfirmedPieceMove implements Serializable {
    private final boolean pieceMoved;
    
    public ConfirmedPieceMove(boolean pieceMoved){
        this.pieceMoved = pieceMoved;
    }
    
    public boolean getPieceMoved(){
        return this.pieceMoved;
    }
}
