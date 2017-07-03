/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess;

import gnu.io.CommPortIdentifier;
import gnu.io.CommPort;

import gnu.io.SerialPort;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sprenkle.chess.messages.BoardAtRest;
import net.sprenkle.chess.messages.ChessMessageReceiver;
import net.sprenkle.chess.messages.GCode;
import net.sprenkle.chess.messages.MessageHandler;
import net.sprenkle.chess.messages.MqChessMessageSender;
import net.sprenkle.chess.messages.SetBoardRestPosition;
import net.sprenkle.chess.messages.ChessMove;
import net.sprenkle.chess.messages.ConfirmedPieceMove;
import net.sprenkle.chess.messages.PiecePositions;
import net.sprenkle.chess.messages.RequestMovePieces;
import net.sprenkle.chess.messages.RequestPiecePositions;
import net.sprenkle.messages.MessageHolder;
import org.apache.log4j.PropertyConfigurator;

public class AnetBoardController {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(AnetBoardController.class.getSimpleName());
    private String lastString;
    private OutputStream out;
    private InputStream in;
    private final MqChessMessageSender messageSender;
    private final double mid = 54;

    
    private final double rest = 165;

    private boolean homed = false;

    public AnetBoardController(MqChessMessageSender messageSender, ChessMessageReceiver messageReceiver) {
        this.messageSender = messageSender;

        messageReceiver.addMessageHandler(SetBoardRestPosition.class.getSimpleName(), new MessageHandler<SetBoardRestPosition>() {
            @Override
            public void handleMessage(SetBoardRestPosition requestBoardRestPosition) {
                requestBoardRestPosition(requestBoardRestPosition);
            }
        });

        messageReceiver.addMessageHandler(GCode.class.getSimpleName(), new MessageHandler<GCode>() {
            @Override
            public void handleMessage(GCode gcode) {
                gCode(gcode);
            }
        });

        messageReceiver.addMessageHandler(RequestMovePieces.class.getSimpleName(), new MessageHandler<RequestMovePieces>() {
            @Override
            public void handleMessage(RequestMovePieces requestMovePieces) {
                requestMovePieces(requestMovePieces);
            }
        });

        messageReceiver.addMessageHandler(PiecePositions.class.getSimpleName(), new MessageHandler<PiecePositions>() {
            @Override
            public void handleMessage(PiecePositions piecePositions) {
                piecePositions(piecePositions);
            }
        });

        try {
            connect("COM7");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            messageReceiver.initialize();
        } catch (Exception ex) {
            Logger.getLogger(AnetBoardController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void connect(String portName) throws Exception {
        CommPortIdentifier portIdentifier = CommPortIdentifier
                .getPortIdentifier(portName);
        if (portIdentifier.isCurrentlyOwned()) {
            System.out.println("Error: Port is currently in use");
        } else {
            int timeout = 2000;
            CommPort commPort = portIdentifier.open(this.getClass().getName(), timeout);

            if (commPort instanceof SerialPort) {
                SerialPort serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(115200,
                        SerialPort.DATABITS_8,
                        SerialPort.STOPBITS_1,
                        SerialPort.PARITY_NONE);

                in = serialPort.getInputStream();
                out = serialPort.getOutputStream();
            } else {
                System.out.println("Error: Only serial ports are handled by this example.");
            }
        }
    }

    public void process() {
        int len = -1;
        while (true) {
            try {
                lastString = "nothing";
                StringBuilder sb = new StringBuilder();
                while ((len = this.in.read()) > -1) {
                    if (len == '\n') {
                        String out = sb.toString();
                        if (!out.equals(lastString)) {
                            lastString = out;
                        }
                        sb = new StringBuilder();
                    } else {
                        sb.append((char) len);
                        if (sb.indexOf("ok") > -1) {
                            return;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendCommand(String command, String notes) {
        if (!homed) {
            executeGcode("G91", "Relative Positioning");
            executeGcode(String.format("G1 X0 Y0 Z%f", mid), "Bring up hook.");
            executeGcode("G90", "Absolute Positioning");
            executeGcode("G28 X0 Y0", "Home");
            executeGcode(String.format("G1 X%f Y%f", 0.0, 200.0), "");
            executeGcode("G28 Z0", "Home");
            executeGcode(String.format("G1 Z%f", mid), "Bring up hook.");
            executeGcode(String.format("G1 X%f Y%f", -15.0, rest), "Bring up hook.");
            homed = true;
        }
        executeGcode(command, notes);
    }

    private void executeGcode(String gcode, String notes) {
        try {
            System.out.format("%s   %s\n", gcode, notes);
            this.out.write(String.format("%s\n", gcode).getBytes());
            //this.out.write("M105\n".getBytes());
            process();
            //System.out.println("Command Sent");
        } catch (IOException ex) {
            Logger.getLogger(AnetBoardController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) throws Exception {
        PropertyConfigurator.configure("D:\\git\\Chess\\src\\main\\java\\log4j.properties");
        AnetBoardController anetBoardController = new AnetBoardController(new MqChessMessageSender("AnetBoardController"), new ChessMessageReceiver("AnetBoardController", true));
    }

    public void requestBoardRestPosition(SetBoardRestPosition boardRestPosition) {
        sendCommand(String.format("G1 Z%f", mid), "Set not to hit");
        sendCommand(String.format("G1 X0 Y%s Z%f", rest, mid), "Bring to Rest");
        messageSender.send(new MessageHolder(BoardAtRest.class.getSimpleName(), new BoardAtRest(true)));
    }

    public void gCode(GCode gcode) {
        sendCommand(gcode.getGCode(), gcode.getNote());
    }



    public void requestMovePieces(RequestMovePieces requestMovePieces) {
        logger.debug(String.format("Requesting move %s", requestMovePieces));
        messageSender.send(new MessageHolder(RequestPiecePositions.class.getSimpleName(), new RequestPiecePositions(requestMovePieces.getMove())));
    }
    
    public void piecePositions(PiecePositions piecePositions){
        for(PieceMove pieceMove : piecePositions.getMoveList()){
            movePiece(pieceMove.getFrom()[0], pieceMove.getFrom()[1], pieceMove.getTo()[0], 
                    pieceMove.getTo()[1], pieceMove.getHeight(), piecePositions.getMid(), piecePositions.getHigh());
        }
        
        messageSender.send(new MessageHolder(ConfirmedPieceMove.class.getSimpleName(), new ConfirmedPieceMove(true)));
    }

    private void movePiece(double x, double y, double toX, double toY, double low, double mid, double high) {
        String gcode = String.format("G1 X%s Y%s Z%s", x, y + 9, mid);
        sendCommand(gcode, "");
        gcode = String.format("G1 X%s Y%s Z%s", x, y + 9, low);
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
        gcode = String.format("G1 X%s Y%s Z%s", toX, toY + 9, low);
        sendCommand(gcode, "");
        gcode = String.format("G1 X%s Y%s Z%s", toX, toY + 9, mid);
        sendCommand(gcode, "");
        gcode = String.format("G1 X0 Y%s Z%f", rest, mid);
        sendCommand(gcode, "");
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
