/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.messages.interfaces;

import net.sprenkle.chess.messages.StartChessGame;

/**
 *
 * @author david
 */
public interface StartGameInterface {
    public void recieve(StartChessGame startGame);
}
