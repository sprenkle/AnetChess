/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.messages.interfaces;

/**
 *
 * @author david
 */
public interface MessageReciever<T> {
    public void reciever(T reciever);
}
