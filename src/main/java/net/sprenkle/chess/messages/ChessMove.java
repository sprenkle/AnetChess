/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.messages;

import java.util.UUID;
import net.sprenkle.chess.Player;

/**
 *
 * @author david
 */
public class ChessMove {

    private final Player turn;
    private final String move;

    public ChessMove(Player turn, String move) {
        this.turn = turn;
        this.move = move;
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


    @Override
    public String toString() {
        return String.format("ChessMove %s to move, %s", turn, move);
    }
}
