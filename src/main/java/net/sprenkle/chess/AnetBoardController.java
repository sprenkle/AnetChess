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
import net.sprenkle.chess.exceptions.InvalidLocationException;
import net.sprenkle.chess.messages.BoardStatus;
import net.sprenkle.chess.messages.ChessMessageReceiver;
import net.sprenkle.chess.messages.GCode;
import net.sprenkle.chess.messages.MessageHandler;
import net.sprenkle.chess.messages.MqChessMessageSender;
import net.sprenkle.chess.messages.SetBoardRestPosition;
import net.sprenkle.chess.messages.ChessMove;
import net.sprenkle.messages.MessageHolder;
import org.apache.log4j.PropertyConfigurator;

public class AnetBoardController {

    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(AnetBoardController.class.getSimpleName());
    private String lastString;
    private OutputStream out;
    private InputStream in;
    private final MqChessMessageSender messageSender;
    private final double low = 14;
    private final double high = 60;
    private final double mid = 26;
    private final double rest = 190;
    private final double xSlope = -0.4262;
    private final double ySlope = 0.4271;
    private final double xIntercept = 175.376;
    private final double yIntercept = -165.4933;
    private final double orgX = 94.3;
    private final double orgY = 170.4;


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

        messageReceiver.addMessageHandler(ChessMove.class.getSimpleName(), new MessageHandler<ChessMove>() {
            @Override
            public void handleMessage(ChessMove chessMove) {
                chessMove(chessMove);
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

//          String out = new String( buffer, 0, len );
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendCommand(String command, String notes) {
        if (!homed) {
            sendCommand("G28", "Home");
            sendCommand("G90", "Absolute Positioning");
            sendCommand(String.format("G1 X0 Y0 Z%f", mid), "Bring up hook.");
            homed = true;
        }
        try {
            System.out.format("%s   %s\n", command, notes);
            this.out.write(String.format("%s\n", command).getBytes());
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
        messageSender.send(new MessageHolder(BoardStatus.class.getSimpleName(), new BoardStatus(false, false, true)));

    }

    public void gCode(GCode gcode) {
        sendCommand(gcode.getGCode(), gcode.getNote());
    }

    public void chessMove(ChessMove chessMove) {
        if (chessMove.isRobot()) {
            try {
                String[] moves = ChessUtil.ConvertChessMove(chessMove.getMove());
                int[] from = ChessUtil.ConvertLocation(moves[0]);
                int[] to = ChessUtil.ConvertLocation(moves[1]);
                
                
            } catch (Exception ex) {
                Logger.getLogger(AnetBoardController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void movePiece(int imageX, int imageY, int boardX, int boardY) {
        int x = (int) (xSlope * imageX + xIntercept + orgX);
        int y = (int) (ySlope * imageY + yIntercept + 190);

        int[] dest = calculateBoardPosition(boardX, boardY);

        String gcode = String.format("G1 X%s Y%s Z%s", x, y + 9, mid);
        MessageHolder mh = new MessageHolder(GCode.class.getSimpleName(), new GCode(gcode, "Go to high offset"));
        messageSender.send(mh);

        gcode = String.format("G1 X%s Y%s Z%s", x, y + 9, low);
        mh = new MessageHolder(GCode.class.getSimpleName(), new GCode(gcode, "Go to Low offset"));
        messageSender.send(mh);

        gcode = String.format("G1 X%s Y%s Z%s", x, y, low);
        mh = new MessageHolder(GCode.class.getSimpleName(), new GCode(gcode, "Move to center low"));
        messageSender.send(mh);

        gcode = String.format("G1 X%s Y%s Z%s", x, y, high);
        mh = new MessageHolder(GCode.class.getSimpleName(), new GCode(gcode, "Lift up piece"));
        messageSender.send(mh);

        jog();
        
        gcode = String.format("G1 X%s Y%s Z%s", dest[0], dest[1], high);
        mh = new MessageHolder(GCode.class.getSimpleName(), new GCode(gcode, "Move to new position high"));
        messageSender.send(mh);

        gcode = String.format("G1 X%s Y%s Z%s", dest[0], dest[1], low);
        mh = new MessageHolder(GCode.class.getSimpleName(), new GCode(gcode, "Lower piece"));
        messageSender.send(mh);

        gcode = String.format("G1 X%s Y%s Z%s", dest[0], dest[1] + 9, low);
        mh = new MessageHolder(GCode.class.getSimpleName(), new GCode(gcode, "Move to offset"));
        messageSender.send(mh);

        gcode = String.format("G1 X%s Y%s Z%s", dest[0], dest[1] + 9, mid);
        mh = new MessageHolder(GCode.class.getSimpleName(), new GCode(gcode, "Move Crane up"));
        messageSender.send(mh);

    }
    
    private int[] calculateBoardPosition(int x, int y) {
        int[] pos = new int[2];

        pos[0] = (int) (orgX + (x - 4) * 18 + 9);
        pos[1] = (int) (orgY - (y * 18 + 9));

        return pos;
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
