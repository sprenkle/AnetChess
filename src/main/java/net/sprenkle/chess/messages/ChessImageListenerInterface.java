/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.messages;

import java.awt.image.BufferedImage;

/**
 *
 * @author david
 */
public interface ChessImageListenerInterface {
    public void receivedImage(BufferedImage bi);
}
