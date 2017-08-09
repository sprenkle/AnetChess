/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.states;

/**
 *
 * @author david
 */
public class BoardReaderState extends State{
    public static String CHECK_FOR_GAME_SETUP = "checkForGameSetup";
    public static String CHECK_FOR_HUMAN_MOVE = "checkForHumanMove";
    public static String SET_REST_POSITION = "checkForRestPosition";
    public static String CHECK_FOR_PIECE_POSITIONS = "checkForPiecePositions";
    public static String SET_BOARD_POSITION = "setBoardPosition";
}
