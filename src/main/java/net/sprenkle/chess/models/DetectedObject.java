/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.models;

import net.sprenkle.chess.Player;

/**
 *
 * @author david
 */
public class DetectedObject {
    private final int x;
    private final int y;
    private final Player color; // TODO This should be a object ID, so it would be a id for a black object or white object
    
    
    public DetectedObject(int x, int y, Player color){
        this.color = color;
        this.x = x;
        this.y = y;
    }
    
    /**
     * @return the x
     */
    public int getX() {
        return x;
    }

    /**
     * @return the y
     */
    public int getY() {
        return y;
    }

    /**
     * @return the color
     */
    public Player getColor() {
        return color;
    }

        @Override
    public String toString(){
        return String.format("%s %s,%s", getColor(), getX(), getY());
    }

}
