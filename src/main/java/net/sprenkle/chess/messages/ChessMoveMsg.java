/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.messages;

import java.io.Serializable;
import java.util.UUID;

/**
 *
 * @author david
 */
public class ChessMoveMsg implements Serializable {
    private final UUID   moveId;
    private final boolean robot;
    private final ChessMove chessMove;

    
    public ChessMoveMsg(UUID moveId, boolean robot, ChessMove chessMove) {
        this.moveId = moveId;
        this.robot = robot;
        this.chessMove = chessMove;
    }

    /**
     * @return the moveId
     */
    public UUID getMoveId() {
        return moveId;
    }
    
    public boolean isRobot(){
        return robot;
    }
    
    public ChessMove getChessMove(){
        return chessMove;
    }
    
    @Override
    public String toString(){
        return String.format("Id=%s %s %s", moveId, robot ? "Robot" : "Human", chessMove.getMove());
    }
}
