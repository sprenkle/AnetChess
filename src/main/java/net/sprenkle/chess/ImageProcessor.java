/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess;

import dagger.ObjectGraph;
import net.sprenkle.chess.messages.RMQChessMessageReceiver;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author david
 */
public class ImageProcessor {
    public static void main(String[] args) throws Exception {
        PropertyConfigurator.configure("D:\\git\\Chess\\src\\main\\java\\log4j.properties");
        
    }

}
