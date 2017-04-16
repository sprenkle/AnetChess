/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author David
 */
public class Chess {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        FileInputStream fileIn = null;
        FileOutputStream fileOut = null;

        OutputStream procIn = null;
        InputStream procOut = null;

        try {
            fileIn = new FileInputStream("test.txt");
            fileOut = new FileOutputStream("testOut.txt");

            Process process = Runtime.getRuntime().exec("C:/dev/Chess/stockfish-4-win/Windows/stockfish_4_x64_modern.exe");
            procIn = process.getOutputStream();
            procOut = process.getInputStream();

            pipeStream(fileIn, procIn);
            pipeStream(procOut, fileOut);
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
    }




    public static void pipeStream(InputStream input, OutputStream output)
            throws IOException {
        byte buffer[] = new byte[1024];
        int numRead = 0;

        do {
            numRead = input.read(buffer);
            output.write(buffer, 0, numRead);
        } while (input.available() > 0);

        output.flush();
    }
}
