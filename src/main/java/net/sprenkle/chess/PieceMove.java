/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess;

/**
 *
 * @author david
 */
public class PieceMove {
    private final double[] from;
    private final double[] to;
    private final double height;
    private final boolean low;

    public PieceMove(double[] from, double[] to, double height, boolean low){
        this.from = from;
        this.to = to;
        this.height = height;
        this.low = low;
    }

    /**
     * @return the from
     */
    public double[] getFrom() {
        return from;
    }

    /**
     * @return the to
     */
    public double[] getTo() {
        return to;
    }

    /**
     * @return the height
     */
    public double getHeight() {
        return height;
    }

    /**
     * @return the low
     */
    public boolean isLow() {
        return low;
    }
    
    
}
