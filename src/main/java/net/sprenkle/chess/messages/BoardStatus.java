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
}
