/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author David
 */
public class StockFishUCI implements UCIInterface {

    private Scanner stdin;
    private BufferedWriter bw;

    public StockFishUCI() {
        try {
            Process process = Runtime.getRuntime().exec("C:\\Users\\david\\Documents\\NetBeansProjects\\Chess\\stockFish\\Windows\\stockfish_8_x64");
            InputStream procOut = process.getInputStream();

            DataOutputStream out = new DataOutputStream(process.getOutputStream());
            bw = new BufferedWriter(new OutputStreamWriter(out));
            stdin = new Scanner(procOut);

            //   (new Thread(this)).start();
        } catch (IOException ioe) {
            System.out.println(ioe);
        }

    }

    @Override
    public String sendCommandAndWait(String command, String waitForString) {
        send(command);
        while (true) {
            String line;
            while (stdin.hasNextLine()) {
                line = stdin.nextLine();
               // System.out.println("<- " + line);
                if (line.contains(waitForString)) {
                    return line;
                }
            }
        }

    }

    @Override
    public void sendCommand(String command) {
        send(command);
    }

    private void send(String line) {
        try {
         //   System.out.println("-> " + line);
            bw.write(line + "\n");
            bw.flush();
        } catch (IOException ex) {
            Logger.getLogger(ChessEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
