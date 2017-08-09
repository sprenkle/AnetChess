/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.messages;

import java.io.Serializable;
import net.sprenkle.chess.imaging.Line;

/**
 *
 * @author david
 */
public class RequestGridObjects extends RequestDetectedObjects implements Serializable {
    private Line[] horizontalLines;
    private Line[] verticalLines;

    public RequestGridObjects(int top, int bottom, int left, int right, Line[] horizontalLines, Line[] verticalLines) {
        super(top, bottom, left, right);
    }
    
    
//    public RequestGridObjects(Line[] horizontalLines, Line[] verticalLines){
//        this.horizontalLines = horizontalLines;
//        this.verticalLines = verticalLines;
//    }

    /**
     * @return the horizontalLines
     */
    public Line[] getHorizontalLines() {
        return horizontalLines;
    }

    /**
     * @return the verticalLines
     */
    public Line[] getVerticalLines() {
        return verticalLines;
    }
}
