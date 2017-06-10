/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.messages;

import java.util.UUID;

/**
 *
 * @author david
 */
public class RequestImage {
    private final UUID messageId;
    
    public RequestImage(UUID id){
        this.messageId = id;
    }
    
    public UUID getMessageId(){
        return messageId;
    }
}
