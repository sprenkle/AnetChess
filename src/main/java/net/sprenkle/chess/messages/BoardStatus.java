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
    private boolean startingPositionSet;
    private boolean humanSide;

    public BoardStatus(boolean startingPositionSet, boolean humanSide){
        this.startingPositionSet = startingPositionSet;
        this.humanSide = humanSide;
    }
    
    /**
     * @return the startingPositionSet
     */
    public boolean isStartingPositionSet() {
        return startingPositionSet;
    }

    /**
     * @param startingPositionSet the startingPositionSet to set
     */
    public void setStartingPositionSet(boolean startingPositionSet) {
        this.startingPositionSet = startingPositionSet;
    }

    /**
     * @return the humanSide
     */
    public boolean isHumanSide() {
        return humanSide;
    }

    /**
     * @param humanSide the humanSide to set
     */
    public void setHumanSide(boolean humanSide) {
        this.humanSide = humanSide;
    }
    
    public String toString(){
        return String.format("Setup=%s Human=%s", startingPositionSet, (humanSide ? "White" : "Black"));
    }
}
