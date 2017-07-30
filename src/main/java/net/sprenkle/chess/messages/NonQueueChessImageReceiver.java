/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.messages;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author david
 */
public class NonQueueChessImageReceiver implements ChessImageReceiver, ChessImageSender {

    private final List<MessageHandler> messageHandlerList = new ArrayList<>();

    /**
     *
     * @param messageHandler
     */
    @Override
    public void add(MessageHandler messageHandler) {
        this.messageHandlerList.add(messageHandler);
    }

    @Override
    public void initialize() throws Exception {
    }

    @Override
    public void send(BoardImage boardImage) {
        process(boardImage);
    }

    private void process(BoardImage boardImage) {
        messageHandlerList.forEach((messageHandler) -> {
            messageHandler.handleMessage(boardImage);
        });
    }

}
