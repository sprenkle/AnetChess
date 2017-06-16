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
public class RequestPiecePositions implements Serializable{
    private final String move;
    
    public RequestPiecePositions(String move){
        this.move = move;
    }
    
    public String getMove(){
        return move;
    }
    
    public String toString(){
        return String.format("RequestPiecePosition %s",move);
    }
}
