/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sprenkle.chess.states;


/**
 *
 * @author David
 */
public class UnknownPositionState extends State {
    
    public UnknownPositionState(){
        name = "UnknownPositionState";
    }
    
    @Override
    public State stateProcess(int[][] squareValues, int nonMatchingSquares, int pieceTaken) {
        return this;
    }

    @Override
    public void initialize() {
    }
}
