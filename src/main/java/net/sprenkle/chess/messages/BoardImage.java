/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.messages;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author david
 */
public class BoardImage implements Serializable {

    private byte[] imageInByte;

    public BoardImage(BufferedImage bi) {

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(bi, "jpg", baos);
            baos.flush();
            imageInByte = baos.toByteArray();
        } catch (IOException ex) {
            Logger.getLogger(BoardImage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public BufferedImage GetBi() {
        try {
            InputStream in = new ByteArrayInputStream(imageInByte);
            return ImageIO.read(in);
        } catch (IOException ex) {
            Logger.getLogger(BoardImage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public String toString(){
        return "BoardImage";
    }
}
