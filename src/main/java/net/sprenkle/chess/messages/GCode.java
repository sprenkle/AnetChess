/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.messages;

import java.io.Serializable;

/**
 *
 * @author david
 */
public class GCode  implements Serializable{
    private final String gcode;
    private final String note;
    
    public GCode(String gcode, String note){
        this.gcode = gcode;
        this.note = note;
    }
    
    public String getGCode(){
        return gcode;
    }
    
    public String getNote(){
        return note;
    }
    
    public String toString(){
        String.format("GCode %s", gcode);
    }
}
