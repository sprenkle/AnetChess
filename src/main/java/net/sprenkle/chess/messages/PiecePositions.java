/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.messages;

import java.io.Serializable;
import java.util.List;
import net.sprenkle.chess.PieceMove;

/**
 *
 * @author david
 */
public class PiecePositions implements Serializable {
    private final List<PieceMove> moveList;
    
    private final double high;
    private final double mid;

    public PiecePositions(List<PieceMove> moveList, double mid, double high) {
        this.moveList = moveList; 
        this.mid = mid;
        this.high = high;
    }

    public List<PieceMove> getMoveList(){
        return moveList;
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
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("PiecePositions mid=%s high=%s ", mid, high));
        
        for(int i = 0 ; i < moveList.size(); i++){
            PieceMove pieceMove = moveList.get(i);
            sb.append(String.format(" From x=%s y=%s To x=%s y=%s hight=%s", pieceMove.getFrom()[0], pieceMove.getFrom()[1], 
                    pieceMove.getTo()[0], pieceMove.getTo()[1], pieceMove.getHeight()));
        }
        
        return sb.toString();
    }
}
