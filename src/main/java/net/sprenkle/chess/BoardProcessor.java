/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess;

import java.util.HashSet;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sprenkle.chess.messages.BoardAtRest;
import net.sprenkle.chess.messages.ChessMessageReceiver;
import net.sprenkle.chess.messages.ChessMessageSender;
import net.sprenkle.chess.messages.RMQChessMessageReceiver;
import net.sprenkle.chess.messages.GCode;
import net.sprenkle.chess.messages.MessageHandler;
import net.sprenkle.chess.messages.RMQChessMessageSender;
import net.sprenkle.chess.messages.SetBoardRestPosition;
import net.sprenkle.chess.messages.ConfirmedPieceMove;
import net.sprenkle.chess.messages.MessageHolder;
import net.sprenkle.chess.messages.PieceAdjust;
import net.sprenkle.chess.messages.PiecePositions;
import net.sprenkle.chess.messages.RequestImage;
import net.sprenkle.chess.messages.RequestMovePieces;
import net.sprenkle.chess.messages.RequestPiecePositions;
import org.apache.log4j.PropertyConfigurator;

public class BoardProcessor {
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(BoardProcessor.class.getSimpleName());
    private final ChessMessageSender messageSender;
    private final BoardController boardController;
    private final double mid;
    private final double rest;
    private final HashSet<UUID> moveIds = new HashSet<>();
    private boolean homed = false;

    public BoardProcessor(ChessMessageSender messageSender, ChessMessageReceiver messageReceiver, BoardProperties boardProperties, BoardController boardController) {
        this.messageSender = messageSender;
        this.boardController = boardController;
        this.mid = boardProperties.getMid();
        this.rest = boardProperties.getRest();

        messageReceiver.addMessageHandler(GCode.class.getName(), new MessageHandler<GCode>() {
            @Override
            public void handleMessage(GCode gcode) {
                gCode(gcode);
            }
        });

        messageReceiver.addMessageHandler(RequestMovePieces.class.getName(), new MessageHandler<RequestMovePieces>() {
            @Override
            public void handleMessage(RequestMovePieces requestMovePieces) {
                requestMovePieces(requestMovePieces);
            }
        });

        messageReceiver.addMessageHandler(PiecePositions.class.getName(), new MessageHandler<PiecePositions>() {
            @Override
            public void handleMessage(PiecePositions piecePositions) {
                piecePositions(piecePositions);
            }
        });

        messageReceiver.addMessageHandler(PieceAdjust.class.getName(), new MessageHandler<PieceAdjust>() {
            @Override
            public void handleMessage(PieceAdjust pieceAdjust) {
                pieceAdjust(pieceAdjust);
            }
        });

        try {
            boardController.connect("COM7");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            messageReceiver.initialize(new RabbitConfiguration());
        } catch (Exception ex) {
            Logger.getLogger(BoardProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendCommand(String command, String notes) {
        if (!homed) {
            boardController.executeGcode("G91", "Relative Positioning");
            boardController.executeGcode(String.format("G1 X0 Y0 Z%f", mid), "Bring up hook.");
            boardController.executeGcode("G90", "Absolute Positioning");
            boardController.executeGcode("G28 X0 Y0", "Home");
            boardController.executeGcode(String.format("G1 X%f Y%f", 0.0, 200.0), "");
            boardController.executeGcode("G28 Z0", "Home");
            boardController.executeGcode(String.format("G1 Z%f", mid), "Bring up hook.");
            boardController.executeGcode(String.format("G1 X%f Y%f", -15.0, rest), "Bring up hook.");
            homed = true;
        }
        boardController.executeGcode(command, notes);
    }

    public static void main(String[] args) throws Exception {
        PropertyConfigurator.configure("D:\\git\\Chess\\src\\main\\java\\log4j.properties");
        BoardProcessor anetBoardController = new BoardProcessor(new RMQChessMessageSender("AnetBoardController",new RabbitConfiguration()), new RMQChessMessageReceiver("AnetBoardController", true), new BoardProperties(), new AnetController());
    }

    public void requestBoardRestPosition(SetBoardRestPosition boardRestPosition) {
        sendCommand(String.format("G1 Z%f", mid), "Set not to hit");
        sendCommand(String.format("G1 X0 Y%s Z%f", rest, mid), "Bring to Rest");
        messageSender.send(new MessageHolder(new BoardAtRest(true)));
    }

    public void gCode(GCode gcode) {
        sendCommand(gcode.getGCode(), gcode.getNote());
        if (gcode.getWait()) {
            sendCommand(String.format("G4 P10"), "Waiting");
            messageSender.send(new MessageHolder(new RequestImage(UUID.randomUUID())));
        }
    }

    public void requestMovePieces(RequestMovePieces requestMovePieces) {
        logger.debug(String.format("Requesting move %s", requestMovePieces));
        messageSender.send(new MessageHolder(new RequestPiecePositions(requestMovePieces.getChessMove(), requestMovePieces.isCastle(), requestMovePieces.getUuid())));
    }

    public void piecePositions(PiecePositions piecePositions) {
        if (moveIds.contains(piecePositions.getUui())) {
            return;
        }
        piecePositions.getMoveList().forEach((pieceMove) -> {
            movePiece(pieceMove.getFrom()[0], pieceMove.getFrom()[1], pieceMove.getTo()[0],
                    pieceMove.getTo()[1], pieceMove.getHeight(), piecePositions.getMid(), piecePositions.getHigh());
        });
        String gcode = String.format("G1 X0 Y%s Z%f", rest, mid);
        sendCommand(gcode, "");
        gcode = String.format("G1 X0 Y%s Z%f", rest, mid);
        sendCommand(gcode, "G4 10");
        moveIds.add(piecePositions.getUui());
        messageSender.send(new MessageHolder(new ConfirmedPieceMove(true)));
    }

    private void movePiece(double x, double y, double toX, double toY, double low, double mid, double high) {
        String gcode = String.format("G1 X%s Y%s Z%s", x, y + 12, this.mid);
        sendCommand(gcode, "");
        gcode = String.format("G1 X%s Y%s Z%s", x, y + 12, low + 10);
        sendCommand(gcode, "");
        gcode = String.format("G1 X%s Y%s Z%s", x, y + 12, low);
        sendCommand(gcode, "");
        gcode = String.format("G1 X%s Y%s Z%s", x, y, low);
        sendCommand(gcode, "");
        gcode = String.format("G1 X%s Y%s Z%s", x, y, high);
        sendCommand(gcode, "");
        jog();
        gcode = String.format("G1 X%s Y%s Z%s", toX, toY, high);
        sendCommand(gcode, "");
        gcode = String.format("G1 X%s Y%s Z%s", toX, toY, low);
        sendCommand(gcode, "");
        gcode = String.format("G1 X%s Y%s Z%s", toX, toY + 12, low);
        sendCommand(gcode, "");
        gcode = String.format("G1 X%s Y%s Z%s", toX, toY + 12, mid);
        sendCommand(gcode, "");
    }

    private void pieceAdjust(PieceAdjust pieceAdjust){
        
    }

    private void jog() {
        sendCommand("G91", "relative");
        sendCommand("G1 X1", "relative");
        sendCommand("G1 X-1", "relative");
        sendCommand("G1 X1", "relative");
        sendCommand("G1 X-1", "relative");
        sendCommand("G1 X1", "relative");
        sendCommand("G1 X-1", "relative");
        sendCommand("G1 X2", "relative");
        sendCommand("G1 X-2", "relative");
        sendCommand("G1 X2", "relative");
        sendCommand("G1 X-2", "relative");
        sendCommand("G1 X3", "relative");
        sendCommand("G1 X-3", "relative");
        sendCommand("G1 X3", "relative");
        sendCommand("G1 X-3", "relative");
        sendCommand("G1 X3", "relative");
        sendCommand("G1 X-3", "relative");
        sendCommand("G1 X3", "relative");
        sendCommand("G1 X-3", "relative");
        sendCommand("G90", "absolute");
    }

}
