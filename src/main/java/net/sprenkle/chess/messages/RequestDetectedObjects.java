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
public class RequestDetectedObjects implements Serializable {
    private final int top;
    private final int bottom;
    private final int left;
    private final int right;
    
    public RequestDetectedObjects(int top, int bottom, int left, int right){
        this.top = top;
        this.bottom = bottom;
        this.left = left;
        this.right = right;
    }

    /**
     * @return the top
     */
    public int getTop() {
        return top;
    }

    /**
     * @return the bottom
     */
    public int getBottom() {
        return bottom;
    }

    /**
     * @return the left
     */
    public int getLeft() {
        return left;
    }

    /**
     * @return the right
     */
    public int getRight() {
        return right;
    }
}
