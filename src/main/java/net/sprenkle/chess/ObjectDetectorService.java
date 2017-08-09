/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import net.sprenkle.chess.imaging.BlackWhite;
import net.sprenkle.chess.messages.BoardImage;
import net.sprenkle.chess.messages.ChessImageReceiver;
import net.sprenkle.chess.messages.ChessMessageReceiver;
import net.sprenkle.chess.messages.ChessMessageSender;
import net.sprenkle.chess.messages.DetectedObjects;
import net.sprenkle.chess.messages.GridObjects;
import net.sprenkle.chess.messages.RequestDetectedObjects;
import net.sprenkle.chess.messages.MessageHandler;
import net.sprenkle.chess.states.ObjectDetectorState;
import net.sprenkle.chess.messages.MessageHolder;
import net.sprenkle.chess.messages.RequestImage;
import net.sprenkle.chess.models.DetectedObject;
import net.sprenkle.chess.messages.RequestGridObjects;
import net.sprenkle.chess.models.GridObject;

/**
 *  This will be used to return DetectedObjects and GridObjects.   
 * @author david
 */
public class ObjectDetectorService {

    private final ObjectDetectorState state;
    private final ObjectDetectorInterface objectDetector;
    private final ChessMessageSender messageSender;
    private int threshHold = 130;

    public ObjectDetectorService(ObjectDetectorState state, ObjectDetectorInterface objectDetector, ChessMessageSender messageSender, ChessMessageReceiver messageReceiver, ChessImageReceiver imageReceiver) {
        this.state = state;
        this.objectDetector = objectDetector;
        this.messageSender = messageSender;

        imageReceiver.add(new MessageHandler<BoardImage>() {
            @Override
            public void handleMessage(BoardImage boardImage) {
                try {
                    boardImage(boardImage);
                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(BoardReader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        messageReceiver.addMessageHandler(RequestDetectedObjects.class.getName(), new MessageHandler<RequestDetectedObjects>() {
            @Override
            public void handleMessage(RequestDetectedObjects requestDetectedObjects) {
                try {
                    requestDetectedObjects(requestDetectedObjects);
                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(BoardReader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        messageReceiver.addMessageHandler(RequestGridObjects.class.getName(), new MessageHandler<RequestGridObjects>() {
            @Override
            public void handleMessage(RequestGridObjects requestGridObjects) {
                try {
                    requestGridObjects(requestGridObjects);
                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(BoardReader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    public void requestGridObjects(RequestGridObjects requestGridObjects) {
        state.setState(ObjectDetectorState.REQUEST_GRID_OBJECTS);
        state.setStateObject(requestGridObjects);
        messageSender.send(new MessageHolder(new RequestImage(UUID.randomUUID())));
    }

    public void requestDetectedObjects(RequestDetectedObjects requestDetectedObjects) {
        state.setState(ObjectDetectorState.REQUEST_DETECTED_OBJECTS);
        state.setStateObject(requestDetectedObjects);
        messageSender.send(new MessageHolder(new RequestImage(UUID.randomUUID())));
    }

    public void boardImage(BoardImage boardImage) {
        switch (state.getState()) {
            case ObjectDetectorState.REQUEST_DETECTED_OBJECTS:
                processDetectedObjects(boardImage.getBi());
                break;
            case ObjectDetectorState.REQUEST_GRID_OBJECTS:
                processGridObjects(boardImage.getBi());
                break;

        }

    }
    
    private void processDetectedObjects(BufferedImage bi){
        RequestDetectedObjects requestDetectedObjects = (RequestDetectedObjects) state.getStateObject();
                boolean[][] array = BlackWhite.convert(bi.getSubimage(
                        requestDetectedObjects.getLeft(),
                        requestDetectedObjects.getTop(),
                        requestDetectedObjects.getRight() - requestDetectedObjects.getLeft(),
                        requestDetectedObjects.getBottom() - requestDetectedObjects.getTop()), threshHold);
                List<DetectedObject> detectedObjects = objectDetector.detectObjects(array, requestDetectedObjects.getLeft(), requestDetectedObjects.getTop());
                messageSender.send(new MessageHolder(new DetectedObjects(detectedObjects)));
    }
    
    private void processGridObjects(BufferedImage bi){
        RequestGridObjects requestGridObjects = (RequestGridObjects) state.getStateObject();
                boolean[][] array2 = BlackWhite.convert(bi.getSubimage(
                        requestGridObjects.getLeft(),
                        requestGridObjects.getTop(),
                        requestGridObjects.getRight() - requestGridObjects.getLeft(),
                        requestGridObjects.getBottom() - requestGridObjects.getTop()), threshHold);
                List<GridObject> gridObjects = objectDetector.detectObjectsWithinGrid(array2,
                        requestGridObjects.getLeft(),
                        requestGridObjects.getTop(),
                        requestGridObjects.getVerticalLines(),
                        requestGridObjects.getHorizontalLines()
                );
                messageSender.send(new MessageHolder(new GridObjects(gridObjects)));
    }
}
