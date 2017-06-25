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
    private final double heightFrom;
    private final double heightTo;
    private final double high;
    private final double mid;
    
    public PiecePositions(double[] from, double[] to, double heightFrom, double mid, double high){
        this(from, to, null, heightFrom, 0, mid, high);
    }

    public PiecePositions(double[] from, double[] to, double[] captureTo, double heightFrom, double heightTo, double mid, double high){
        this.from = from;
        this.to = to;
        this.captureTo = captureTo;
        this.capture = captureTo != null;
        this.heightFrom = heightFrom;
        this.heightTo = heightTo;
        this.mid = mid;
        this.high = high;
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
        return String.format("PiecePositions from=%s,%s to=%s,%s", from[0], from[1], to[0], to[1]);
    }

    /**
     * @return the heightFrom
     */
    public double getHeightFrom() {
        return heightFrom;
    }

    /**
     * @return the heightTo
     */
    public double getHeightTo() {
        return heightTo;
    }

    /**
     * @return the high
     */
    public double getHigh() {
        return high;
    }

    /**
     * @return the mid
     */
    public double getMid() {
        return mid;
    }
}
