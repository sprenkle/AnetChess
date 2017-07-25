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
public class RequestPieceAdjust implements Serializable{
    private final int col;
    
    public RequestPieceAdjust(int col){
        this.col = col;
    }
    
    @Override
    public String toString(){
        return String.format("Column %s", col);
    }
}
