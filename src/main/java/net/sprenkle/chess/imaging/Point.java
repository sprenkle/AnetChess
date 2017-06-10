/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.imaging;

/**
 *
 * @author david
 */
public class Point {
    public int x;
    public int y;
    public boolean color;
    
    public Point(int x, int y, boolean color){
        this.x = x;
        this.y = y;
        this.color = color;
    }
}
