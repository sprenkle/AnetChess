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
public class Line {
    public Point start;
    public Point end;
    
    public Line(Point start, Point end){
        this.start = start;
        this.end = end;
    }
}
