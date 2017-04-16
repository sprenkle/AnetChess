/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.states;

import javax.swing.JOptionPane;

/**
 *
 * @author David
 */
public class EngineMoveState extends State {

    private boolean madeDecision = false;

    public EngineMoveState() {
        name = "EngineMoveState";
    }

    @Override
    public State stateProcess(int[][] squareValues, int nonMatchingSquares, int pieceTaken) {
        if (!madeDecision) {
            String bestMove = State.engine.getBestMove();

            JOptionPane.showMessageDialog(null, "Engine move is " + bestMove);

            System.out.println(bestMove);
            madeDecision = true;
        }else{
             if(nonMatchingSquares == 0 && pieceTaken == 0){
                 goodSquareValues = squareValues;
                 return State.getState(State.HUMANSTATE);
             }
        }
        
        return this;
    }

    @Override
    public void initialize() {
        madeDecision = false;
    }

}
