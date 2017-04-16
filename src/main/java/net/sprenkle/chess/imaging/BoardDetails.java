/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sprenkle.chess.imaging;

import java.awt.Rectangle;
import java.io.Serializable;

/**
 *
 * @author David
 */
public class BoardDetails implements Serializable {
    private int top;
    private int bottom;
    private int left;
    private int right;
    
    private double squareHeight;
    private double squareWidth;
    
    private double[][][] values;

    private int[][] diffValues;
    
    public BoardDetails(int left, int right, int top, int bottom, double squareWidth, double squareHeight){
        this.top = top;
        this.bottom  = bottom;
        this.left = left;
        this.right = right;
        this.squareWidth = squareWidth;
        this.squareHeight = squareHeight;
    }

    BoardDetails() {
        
    }
    
    /**
     * @return the top
     */
    public int getTop() {
        return top;
    }

    /**
     * @param top the top to set
     */
    public void setTop(int top) {
        this.top = top;
    }

    /**
     * @return the bottom
     */
    public int getBottom() {
        return bottom;
    }

    /**
     * @param bottom the bottom to set
     */
    public void setBottom(int bottom) {
        this.bottom = bottom;
    }

    /**
     * @return the left
     */
    public int getLeft() {
        return left;
    }

    /**
     * @param left the left to set
     */
    public void setLeft(int left) {
        this.left = left;
    }

    /**
     * @return the right
     */
    public int getRight() {
        return right;
    }

    /**
     * @param right the right to set
     */
    public void setRight(int right) {
        this.right = right;
    }

    /**
     * @return the squareHeight
     */
    public double getSquareHeight() {
        return squareHeight;
    }

    /**
     * @param squareHeight the squareHeight to set
     */
    public void setSquareHeight(double squareHeight) {
        this.squareHeight = squareHeight;
    }

    /**
     * @return the squareWidth
     */
    public double getSquareWidth() {
        return squareWidth;
    }

    /**
     * @param squareWidth the squareWidth to set
     */
    public void setSquareWidth(double squareWidth) {
        this.squareWidth = squareWidth;
    }

    /**
     * @return the values
     */
    public double[][][] getValues() {
        return values;
    }

    /**
     * @param values the values to set
     */
    public void setValues(double[][][] values) {
        this.values = values;
    }

    /**
     * @return the diffValues
     */
    public int[][] getDiffValues() {
        return diffValues;
    }

    /**
     * @param diffValues the diffValues to set
     */
    public void setDiffValues(int[][] diffValues) {
        this.diffValues = diffValues;
    }
 
        public Rectangle getSquare(int x, int y) {
        Rectangle rv = new Rectangle((int) (left + (x * squareWidth) + squareWidth * .1), (int) (top + y * squareHeight + (squareHeight * .1)), (int) (squareWidth * .8), (int) (squareHeight * .8));

        return rv;
    }

}
