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
public class RequestMovePieces implements Serializable {
    private final String move;
    
    public RequestMovePieces(String move){
        this.move = move;
    }

    public String getMove(){
        return move;
    }
    
    @Override
    public String toString(){
        return move;
    }
}
