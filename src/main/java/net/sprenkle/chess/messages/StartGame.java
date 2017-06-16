/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.messages;

import java.io.Serializable;

/**
 *
 * @author david
 */
public class StartGame implements Serializable {
    private String gameId;
    private String whiteName;
    private String blackName;
    private boolean whiteRobot;
    private boolean blackRobot;
    private int whiteLevel;
    private int blackLevel;

    public StartGame(boolean whiteRobot, boolean blackRobot){
        this.whiteRobot = whiteRobot;
        this.blackRobot = blackRobot;
    }
    
    /**
     * @return the gameId
     */
    public String getGameId() {
        return gameId;
    }

    /**
     * @param gameId the gameId to set
     */
    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    /**
     * @return the whiteName
     */
    public String getWhiteName() {
        return whiteName;
    }

    /**
     * @param whiteName the whiteName to set
     */
    public void setWhiteName(String whiteName) {
        this.whiteName = whiteName;
    }

    /**
     * @return the blackName
     */
    public String getBlackName() {
        return blackName;
    }

    /**
     * @param blackName the blackName to set
     */
    public void setBlackName(String blackName) {
        this.blackName = blackName;
    }

    /**
     * @return the whiteRobot
     */
    public boolean isWhiteRobot() {
        return whiteRobot;
    }

    /**
     * @param whiteRobot the whiteRobot to set
     */
    public void setWhiteRobot(boolean whiteRobot) {
        this.whiteRobot = whiteRobot;
    }

    /**
     * @return the blackRobot
     */
    public boolean isBlackRobot() {
        return blackRobot;
    }

    /**
     * @param blackRobot the blackRobot to set
     */
    public void setBlackRobot(boolean blackRobot) {
        this.blackRobot = blackRobot;
    }

    /**
     * @return the whiteLevel
     */
    public int getWhiteLevel() {
        return whiteLevel;
    }

    /**
     * @param whiteLevel the whiteLevel to set
     */
    public void setWhiteLevel(int whiteLevel) {
        this.whiteLevel = whiteLevel;
    }

    /**
     * @return the blackLevel
     */
    public int getBlackLevel() {
        return blackLevel;
    }

    /**
     * @param blackLevel the blackLevel to set
     */
    public void setBlackLevel(int blackLevel) {
        this.blackLevel = blackLevel;
    }
    
    public String toString(){
        return String.format("StartGame White robot=%s Black robot=%s", whiteRobot, blackRobot);
    }
}
