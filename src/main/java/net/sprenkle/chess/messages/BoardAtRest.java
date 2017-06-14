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
public class BoardAtRest implements Serializable{
    private final boolean atRest;
    
    public BoardAtRest(boolean atRest){
        this.atRest = atRest;
    }
    
    public boolean IsAtRest(){
        return atRest;
    }
}
