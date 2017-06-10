/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.messages;

import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 *
 * @author david
 */
public class BoardImage implements Serializable {
    private final BufferedImage bi;
    
    public BoardImage(BufferedImage bi){
        this.bi = bi;
    }
    
    public BufferedImage GetBi(){
        return this.bi;
    }
}
