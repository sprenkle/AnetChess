/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.messages;

import net.sprenkle.chess.PossiblePiece;

/**
 *
 * @author david
 */
public class KnownBoardPositions {
        private final PossiblePiece[][] knownBoard;
        
        public KnownBoardPositions(PossiblePiece[][] knownBoard){
            this.knownBoard = knownBoard;
        }
        
        public PossiblePiece[][] getKnownPostions(){
            return knownBoard;
        }
        
}