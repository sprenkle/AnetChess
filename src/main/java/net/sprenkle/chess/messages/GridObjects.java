/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.messages;

import java.io.Serializable;
import java.util.List;
import net.sprenkle.chess.models.GridObject;

/**
 *
 * @author david
 */
public class GridObjects implements Serializable {
    List<GridObject> gridObjects;
    
    public GridObjects(List<GridObject> gridObjects){
        this.gridObjects = gridObjects;
    }
    
    public List<GridObject> getGridObjects(){
        return this.gridObjects;
    }
}
