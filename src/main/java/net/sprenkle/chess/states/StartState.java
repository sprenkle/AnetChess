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
public class StartState extends State {

    
    public StartState(){
        name = "StartState";
    }
    
    @Override
    public State stateProcess(int[][] squareValues, int nonMatchingSquares, int pieceTaken) {
        if(nonMatchingSquares == 0 && pieceTaken == 0){
            int whiteTotal = 0;
            int blackTotal = 0;
            
            for(int x = 0 ; x < 8; x++){
                whiteTotal += squareValues[x][0];
                whiteTotal += squareValues[x][1];
                blackTotal += squareValues[x][6];
                blackTotal += squareValues[x][7];
            }
            
            humanColor = whiteTotal > blackTotal ? 0 : 1;
            engineColor = 1;
            turnToMove = humanColor == 0 ? 0 : 1;
            
            emptySquareDetectionValue = (diffList.get(32).value + diffList.get(31).value) / 2;
            
            goodSquareValues = squareValues;
            
            if(turnToMove == 0){
                stateLogger.info("Engine Plays Black");
                return getState(State.HUMANSTATE);
            }else{
                stateLogger.info("Engine Plays White");
                return getState(State.ENGINESTATE);
            }
        }
        return this;
    }

    @Override
    public void initialize() {
        // maybe need this not sure emptySquareDetectionValue = -1;
    }
    
}
