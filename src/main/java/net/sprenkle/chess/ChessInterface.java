/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess;

import net.sprenkle.chess.messages.StartGame;
import net.sprenkle.chess.messages.ChessMove;
import net.sprenkle.chess.messages.RequestMove;

/**
 *
 * @author david
 */
public interface ChessInterface {
    public void startGame(StartGame startGame) throws Exception;
    public void chessMoved(ChessMove chessMove) throws Exception;
    public void requestMove(RequestMove requestMove) throws Exception;
}
