/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sprenkle.chess.messages.BoardImage;
import net.sprenkle.chess.messages.ChessMessageReceiver;
import net.sprenkle.chess.messages.MessageHandler;

/**
 *
 * @author david
 */
public class ImageLogger {
    public final String logDir = "D:\\git\\Chess\\images\\logger";
    
    public ImageLogger(ChessMessageReceiver messageReceiver) {
        messageReceiver.addMessageHandler(BoardImage.class.getSimpleName(), new MessageHandler<BoardImage>() {
            @Override
            public void handleMessage(BoardImage boardImage) {
                try {
                    boardImage(boardImage);
                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(BoardReader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        try {
            messageReceiver.initialize();
        } catch (Exception ex) {
            Logger.getLogger(ImageLogger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void boardImage(BoardImage boardImage){
        Path path = Paths.get(logDir, boardImage.getUuid().toString() + ".png");
        ImageUtil.savePng(boardImage.GetBi(), path.toString());
    }
    
    public static void main(String[] arg){
        ImageLogger imageLogger = new ImageLogger(new ChessMessageReceiver("ImageLogger", true));
    }
}
