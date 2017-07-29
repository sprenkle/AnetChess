/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.messages;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.UUID;

/**
 *
 * @author david
 */
public class BoardImage implements Serializable {

    private final BufferedImage bufferedImage;
    private final UUID uuid;

    public BoardImage(BufferedImage bi) {
        uuid = UUID.randomUUID();
        bufferedImage = bi;
    }

    public BufferedImage getBi() {
        return bufferedImage;
    }
    
    public UUID getUuid(){
        return uuid;
    }
    
    @Override
    public String toString(){
        return String.format("BoardImage %s", uuid);
    }
}
