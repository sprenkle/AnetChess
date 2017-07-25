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
public class PieceAdjust implements Serializable{
    public final double xOffset;
    public final double yOffset;
    
    public PieceAdjust(double xOffset, double yOffset){
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }
    
    @Override
    public String toString(){
        return String.format("XOffset=%s YOffset=%s", xOffset, yOffset);
    }
}
