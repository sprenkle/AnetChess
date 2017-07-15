/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess;

/**
 *
 * @author david
 */
public interface ChessControllerInterface {
    public String makeMove(String move);
    public void newGame();
    public String getMoves();
    public void consoleOut();
    public PossiblePiece[][] getKnownBoard();
    public boolean isLastMoveCastle();
}
