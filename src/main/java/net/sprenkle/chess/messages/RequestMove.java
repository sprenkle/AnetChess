/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.messages;

import java.io.Serializable;
import java.util.UUID;
import net.sprenkle.chess.ChessState;

/**
 *
 * @author david
 */
public class RequestMove implements Serializable {
    private final ChessState.Player turn;
    private final boolean robot;
    private final String moveHistory;
    private final UUID moveId;
    
    public RequestMove(ChessState.Player turn, boolean robot, String moveHistory, UUID moveId){
        this.turn = turn;
        this.robot = robot;
        this.moveHistory = moveHistory;
        this.moveId = moveId;
    }
    
    public boolean isRobot(){
        return robot;
    }

    /**
     * @return the whiteTurn
     */
    public ChessState.Player getTurn() {
        return turn;
    }
    
    public String getMoveHistory(){
        return this.moveHistory;
    }
    
    public UUID getMoveId(){
        return moveId;
    }
    
    @Override
    public String toString(){
        return String.format("Request move for %s %s %s %s", turn, robot ? "Robot" : "Human", moveId, moveHistory);
    }
}
