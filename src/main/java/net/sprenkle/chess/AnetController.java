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

public class AnetController implements BoardController{
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(BoardProcessor.class.getSimpleName());
    private String lastString;
    private OutputStream out;
    private InputStream in;

    public AnetController() {
    }

    @Override
    public void connect(String portName) throws Exception {
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

    @Override
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

    @Override
    public void executeGcode(String gcode, String notes) {
        try {
            System.out.format("%s   %s\n", gcode, notes);
            this.out.write(String.format("%s\n", gcode).getBytes());
            //this.out.write("M105\n".getBytes());
            process();
            //System.out.println("Command Sent");
        } catch (IOException ex) {
            Logger.getLogger(BoardProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
