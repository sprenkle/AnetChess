/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess;

/**
 *
 * @author david
 */
public class ChessState {
    public enum Player {White, Black}

    private Player turn;
    private boolean whiteRobot;
    private boolean blackRobot;
    
    public void setWhiteRobot(boolean robot){
        this.whiteRobot = robot;
    }
    
    public void setBlackRobot(boolean robot){
        this.blackRobot = robot;
    }
    
    /**
     * @return the whiteTurn
     */
    public Player getTurn() {
        return turn;
    }

    /**
     * @param turn
     */
    public void setTurn(Player turn) {
        this.turn = turn;
    }
    
    public boolean isActivePlayerRobot(){
        return (turn == Player.White && whiteRobot) || (turn == Player.Black && blackRobot);
    }
}
