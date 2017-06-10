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
import net.sprenkle.chess.messages.BoardStatus;
import net.sprenkle.chess.messages.ChessMessageReceiver;
import net.sprenkle.chess.messages.MessageHandler;
import net.sprenkle.chess.messages.MqChessMessageSender;
import net.sprenkle.chess.messages.SetBoardRestPosition;
import net.sprenkle.messages.MessageHolder;
import org.apache.log4j.PropertyConfigurator;

public class AnetBoardController {

    static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(AnetBoardController.class.getSimpleName());
    private final MqChessMessageSender messageSender;
    boolean ok = false;
    String lastString;
    OutputStream out;
    InputStream in;

    public AnetBoardController(MqChessMessageSender messageSender, ChessMessageReceiver messageReceiver) {
        this.messageSender = messageSender;

        messageReceiver.addMessageHandler(SetBoardRestPosition.class.getSimpleName(), new MessageHandler<SetBoardRestPosition>() {
            @Override
            public void handleMessage(SetBoardRestPosition requestBoardRestPosition) {
                requestBoardRestPosition(requestBoardRestPosition);
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

    double bottom = 25.5;
    double top = 170.5;
    double left = 22.5;
    double right = 167.6;
    double low = 14;
    double high = 60;
    double mid = 26;

    boolean homed = false;

    String getPosition(double x, double y, double z, boolean middle) {
        double xSquareWidth = (right - left) / 8;
        double calcX = (xSquareWidth) * (x - 1) + left + xSquareWidth * 0.5;
        double ySquareWidth = (top - bottom) / 8;
        double calcY = (ySquareWidth) * (y - 1) + bottom + (middle ? ySquareWidth * 0.5 : ySquareWidth);

        return String.format("X%f Y%f Z%f", calcX, calcY, z);
    }

    public void startCommands() {
        try {
            int c = 0;
            byte input;
            StringBuffer sb = new StringBuffer();
            while ((c = System.in.read()) > -1) {
                if (c == '\n') {
                    if (!homed) {
                        sendCommand("G28", "Home");
                        sendCommand("G90", "Absolute Positioning");
                        sendCommand(String.format("G1 X0 Y0 Z%f", mid), "Bring up hook.");
                        homed = true;
                    }

//                    movePiece(5,2,5,4);
//                    movePiece(5,7,5,5);
//                    movePiece(2,1,3,3);
//                    movePiece(2,8,3,6);
//                    movePiece(4,2,4,3);
//                    movePiece(6,8,3,4);
                    movePiece(5, 3, 5, 2);
                    movePiece(5, 5, 5, 7);
                    movePiece(3, 3, 2, 1);
                    movePiece(3, 6, 2, 8);
                    movePiece(4, 3, 4, 2);
                    movePiece(3, 4, 6, 7);

                    moveDone();
                } else {
                    sb.append((char) c);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void movePiece(int fromX, int fromY, int toX, int toY) {
        String command = String.format("G1 %s I-0.536 J0.47", getPosition(fromX, fromY, mid, false));
        sendCommand(command, "Move to 2,1 side up");

        command = String.format("G1 %s I-0.536 J0.47", getPosition(fromX, fromY, low, false));
        sendCommand(command, "Move to 2,1 side down");

        command = String.format("G1 %s I-0.536 J0.47", getPosition(fromX, fromY, low, true));
        sendCommand(command, "Move to 2,1 mid down");

        command = String.format("G1 %s I-0.536 J0.47", getPosition(fromX, fromY, high, true));
        sendCommand(command, "Move to 2, 1 mid up");

        jog();

        command = String.format("G1 %s I-0.536 J0.47", getPosition(toX, toY, high, true));
        sendCommand(command, "Move to 3,3 mid up");

        command = String.format("G1 %s I-0.536 J0.47", getPosition(toX, toY, low, true));
        sendCommand(command, "Move to 3,3 mid down");

        command = String.format("G1 %s I-0.536 J0.47", getPosition(toX, toY, low, false));
        sendCommand(command, "Move to 3, 3 side down");

        command = String.format("G1 %s I-0.536 J0.47", getPosition(toX, toY, mid, false));
        sendCommand(command, "Move to 3,3 sid up Done");

    }

    private void moveDone() {
        sendCommand(String.format("G1 X%s Y%s\n", left, top), "Move back");
    }

    private void jog() {
        sendCommand("G91", "relative");
        sendCommand("G1 X1", "relative");
        sendCommand("G1 X-1", "relative");
        sendCommand("G1 X1", "relative");
        sendCommand("G1 X-1", "relative");
        sendCommand("G1 X1", "relative");
        sendCommand("G1 X-1", "relative");
        sendCommand("G90", "absolute");

    }

    private void sendCommand(String command, String notes) {
        try {
            System.out.format("%s   %s\n", command, notes);
            ok = false;
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

        AnetBoardController anetBoardController = new AnetBoardController(new MqChessMessageSender("AnetBoardController"), new ChessMessageReceiver("AnetBoardController"));
    }

    public void requestBoardRestPosition(SetBoardRestPosition boardRestPosition) {
        if (!homed) {
            sendCommand("G28", "Home");
            sendCommand("G90", "Absolute Positioning");
            sendCommand(String.format("G1 X0 Y0 Z%f", mid), "Bring up hook.");
            homed = true;
        }
        sendCommand(String.format("G1 Z%f", mid), "Set not to hit");
        sendCommand(String.format("G1 X0 Y190 Z%f", mid), "Bring to Rest");
        messageSender.send(new MessageHolder(BoardStatus.class.getSimpleName(), new BoardStatus(false, false, true)));

    }
}
