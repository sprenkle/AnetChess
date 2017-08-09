/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.messages;

import java.io.Serializable;
import java.util.List;
import net.sprenkle.chess.models.DetectedObject;

/**
 *
 * @author david
 */
public class DetectedObjects  implements Serializable {
    private final List<DetectedObject> detectedObjects;
    
    public DetectedObjects(final List<DetectedObject> detectedObjects){
        this.detectedObjects = detectedObjects;
    }
    
    public List<DetectedObject> getDetectedObjects(){
        return detectedObjects;
    }
}
