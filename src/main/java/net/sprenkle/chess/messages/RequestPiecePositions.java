/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.messages;

import java.io.Serializable;
import java.util.UUID;

/**
 *
 * @author david
 */
public class RequestPiecePositions implements Serializable{
    private final ChessMove chessMove;
    private final boolean castle;
    private final UUID uuid;

    
    public RequestPiecePositions(ChessMove chessMove, boolean castle, UUID uuid){
        this.chessMove = chessMove;
        this.castle = castle;
        this.uuid = uuid;
    }
    
    public ChessMove getChessMove(){
        return chessMove;
    }
    
    public boolean getCastle(){
        return castle;
    }
    
    
    
    @Override
    public String toString(){
        return String.format("RequestPiecePosition %s",chessMove);
    }

    /**
     * @return the uuid
     */
    public UUID getUuid() {
        return uuid;
    }
}
