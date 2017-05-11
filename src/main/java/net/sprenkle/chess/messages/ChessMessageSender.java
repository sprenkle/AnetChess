/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.messages;

import net.sprenkle.messages.MessageHolder;

/**
 *
 * @author david
 */
public interface ChessMessageSender {
    public void send(MessageHolder messageHolder);
}
