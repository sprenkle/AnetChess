/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.messages;

/**
 *
 * @author david
 */
public interface ChessMessageReceiver {
    public void addMessageHandler(String messageType, MessageHandler messageHandler);
    public void initialize(RabbitConfigurationInterface configuration) throws Exception;
}
