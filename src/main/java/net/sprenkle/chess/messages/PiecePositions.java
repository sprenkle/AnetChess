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
public class PiecePositions implements Serializable{
    private final boolean capture;
    private final double[] from;
    private final double[] to;
    private final double[] captureTo;
    
    public PiecePositions(double[] from, double[] to){
        this(from, to, null);
    }

    public PiecePositions(double[] from, double[] to, double[] captureTo){
        this.from = from;
        this.to = to;
        this.captureTo = captureTo;
        this.capture = captureTo != null;
    }
    
    public boolean isCapture(){
        return capture;
    }
    
    public double[] getFrom(){
        return from;
    }
    
    public double[] getTo(){
        return to;
    }
    
    public double[] getCaptureTo(){
        return captureTo;
    }
    
    @Override
    public String toString(){
        return String.format("from=%s,%s to=%s,%s", from[0], from[1], to[0], to[1]);
    }
}
