/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess;

/**
 *
 * @author david
 */
public interface BoardController {
    void connect(String portName) throws Exception;
    public void process();
    public void executeGcode(String gcode, String notes);
}
