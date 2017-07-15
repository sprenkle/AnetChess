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
public class BoardReaderState {
    private String state = NONE;
    
    public static String NONE = "none";
    public static String CHECK_FOR_GAME_SETUP = "checkForGameSetup";
    public static String CHECK_FOR_HUMAN_MOVE = "checkForHumanMove";
    public static String CHECK_FOR_REST_POSITION = "checkForRestPosition";
    public static String CHECK_FOR_PIECE_POSITIONS = "checkForPiecePositions";

    public boolean inState(String state){
        return this.state.equals(state);
    }
    
    /**
     * @return the state
     */
    public String getState() {
        return state;
    }

    public void reset(){
        state = NONE;
    }
    
    public void setGameSetup(){
        state = CHECK_FOR_GAME_SETUP;
    }
    
    public void setHumanMove(){
        state = CHECK_FOR_HUMAN_MOVE;
    }
    
    public void setRestPositino(){
        state = CHECK_FOR_REST_POSITION;
    }
    
    public void setPiecePosition(){
        state = CHECK_FOR_PIECE_POSITIONS;
    }

    /**
     * @param state the state to set
     */
    public void setState(String state) {
        this.state = state;
    }


    
}
