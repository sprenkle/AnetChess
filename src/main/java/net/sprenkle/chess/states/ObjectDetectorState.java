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
public class ObjectDetectorState extends State {
    private Object stateObject;
            
    public static final String REQUEST_DETECTED_OBJECTS = "RequestDetectedObjects";
    public static final String REQUEST_GRID_OBJECTS = "RequestGridObjects";

    /**
     * @return the stateObject
     */
    public Object getStateObject() {
        return stateObject;
    }

    /**
     * @param stateObject the stateObject to set
     */
    public void setStateObject(Object stateObject) {
        this.stateObject = stateObject;
    }
    
    
}
