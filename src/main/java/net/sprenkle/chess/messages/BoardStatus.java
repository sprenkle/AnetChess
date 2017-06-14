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
    
    @Override
    public String toString(){
        return String.format("BoardStatus startingPositionSet=%s humanSide=%s isRestPosition=%s", startingPositionSet, (humanSide ? "White" : "Black"), isRestPosition);
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

    /**
     * @return the isRestPosition
     */
    public boolean isIsRestPosition() {
        return isRestPosition;
    }
}
