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
    private final boolean wait;
    
    public GCode(String gcode, String note){
        this(gcode, note, false);
    }


    public GCode(String gcode, String note, boolean wait){
        this.gcode = gcode;
        this.note = note;
        this.wait = wait;
    }
    
    public String getGCode(){
        return gcode;
    }
    
    public String getNote(){
        return note;
    }
    
    public boolean getWait(){
        return wait;
    }
    
    @Override
    public String toString(){
        return String.format("GCode %s %s %s", gcode, note, wait);
    }
}
