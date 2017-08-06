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
public class GridObject  {

    private double xOffFactor;
    private double yOffFactor;
    private int col;
    private int row;
    private DetectedObject detectedObject;

    public GridObject(DetectedObject detectedObject) {
        this.detectedObject = detectedObject;
    }

    public double getOffset() {
        return getxOffFactor() + getyOffFactor();
    }

    @Override
    public String toString() {
        return String.format("%s,%s", getCol(), getRow());
    }

    public int getX(){
        return detectedObject.getX();
    }
    
    public int getY(){
        return detectedObject.getY();
    }
    
    public Player getColor(){
        return detectedObject.getColor();
    }
    
    /**
     * @return the xOffFactor
     */
    public double getxOffFactor() {
        return xOffFactor;
    }

    /**
     * @return the yOffFactor
     */
    public double getyOffFactor() {
        return yOffFactor;
    }

    /**
     * @return the col
     */
    public int getCol() {
        return col;
    }

    /**
     * @return the row
     */
    public int getRow() {
        return row;
    }

    /**
     * @param xOffFactor the xOffFactor to set
     */
    public void setxOffFactor(double xOffFactor) {
        this.xOffFactor = xOffFactor;
    }

    /**
     * @param yOffFactor the yOffFactor to set
     */
    public void setyOffFactor(double yOffFactor) {
        this.yOffFactor = yOffFactor;
    }

    /**
     * @param col the col to set
     */
    public void setCol(int col) {
        this.col = col;
    }

    /**
     * @param row the row to set
     */
    public void setRow(int row) {
        this.row = row;
    }

}
