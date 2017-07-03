/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.messages;

import java.io.Serializable;
import java.util.UUID;
import net.sprenkle.chess.Player;

/**
 *
 * @author david
 */
public class ChessMove implements Serializable {


    private final Player turn;
    private final String move;
    private final UUID   moveId;
    private final boolean robot;
    private final boolean castle;

    
    public ChessMove(Player turn, String move, UUID moveId, boolean robot) {
        this(turn, move, moveId, robot, false);
    }

    
    public ChessMove(Player turn, String move, UUID moveId, boolean robot, boolean castle) {
        this.turn = turn;
        this.move = move;
        this.moveId = moveId;
        this.robot = robot;
        this.castle = castle;
    }

    /**
     * @return the white
     */
    public Player getTurn() {
        return turn;
    }

    /**
     * @return the move
     */
    public String getMove() {
        return move;
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
    
    @Override
    public String toString(){
        return String.format("ChessMove %s to move, %s Id=%s", turn, move, moveId);
    }
}
