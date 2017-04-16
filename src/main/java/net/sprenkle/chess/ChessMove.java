/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sprenkle.chess;

/**
 *
 * @author David
 */
public class ChessMove {
    private String move;
    
    public ChessMove(String move){
       this.move = move;
    }
    
    public String getMove(){
        return move;
    }
}
