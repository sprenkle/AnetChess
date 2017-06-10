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
 * Used to give the status of the board.  
 * Used to return that the board has been setup to play and the game can start.
 */
public class BoardStatus implements Serializable {
    private final boolean isRestPosition;
    private final boolean startingPositionSet;
    private final boolean humanSide;

    public BoardStatus(boolean startingPositionSet, boolean humanSide, boolean isRestPosition){
        this.startingPositionSet = startingPositionSet;
        this.humanSide = humanSide;
        this.isRestPosition = isRestPosition;
    }
    
    /**
     * @return the startingPositionSet
     */
    public boolean isStartingPositionSet() {
        return startingPositionSet;
    }

    /**
     * @return the humanSide
     */
    public boolean isHumanSide() {
        return humanSide;
    }
    
    public String toString(){
        return String.format("Setup=%s Human=%s", startingPositionSet, (humanSide ? "White" : "Black"));
    }

    /**
     * @return the isRestPosition
     */
    public boolean isIsRestPosition() {
        return isRestPosition;
    }
}
