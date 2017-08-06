/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.messages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author david
 */
public class NonQueueMessageTranRec implements ChessMessageReceiver, ChessMessageSender {

    private final HashMap<String, List<MessageHandler>> eventMap;

    public NonQueueMessageTranRec() {
        eventMap = new HashMap<>();
    }

    @Override
    public void addMessageHandler(String messageType, MessageHandler messageHandler) {
        if(!eventMap.containsKey(messageType)){
            List<MessageHandler> handlerList = new ArrayList<>();
            eventMap.put(messageType, handlerList);
        }
        List<MessageHandler> handlerList = eventMap.get(messageType);
        handlerList.add(messageHandler);
    }

    private void process(MessageHolder mh) {
        if (!eventMap.containsKey(mh.getClassName())) {
            return;
        }
        List<MessageHandler> handlerList = eventMap.get(mh.getClassName());
        handlerList.forEach((messageHandler) -> {
            messageHandler.handleMessage(mh.getObject());
        });
    }

    @Override
    public void initialize(RabbitConfigurationInterface configuration) throws Exception {
    }

    @Override
    public void send(MessageHolder messageHolder) {
        process(messageHolder);
    }
}
