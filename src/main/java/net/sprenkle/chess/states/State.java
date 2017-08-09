/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.states;

/**
 *
 * @author david
 */
public class State {

    public static final String START = "START";

    private String state = START;

    public boolean inState(String state) {
        return this.state.equals(state);
    }

    public void reset() {
        state = START;
    }
    
    public void setState(String state) {
        this.state = state;
    }

    public String getState(){
        return state;
    }
}
