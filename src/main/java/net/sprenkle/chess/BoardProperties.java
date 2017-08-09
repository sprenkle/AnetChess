/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import net.sprenkle.chess.imaging.Line;
import net.sprenkle.chess.imaging.Point;

/**
 *
 * @author david
 */
public class BoardProperties {

    /**
     * @return the vLines
     */
    public Line[] getvLines() {
        return vLines;
    }

    /**
     * @return the hLines
     */
    public Line[] gethLines() {
        return hLines;
    }

    private int leftBoard;
    private int rightBoard;
    private int bottomBoard;
    private int topBoard;
    
    private int bottomDetect;
    private int topDetect;
    private int leftDetect;
    private int rightDetect;

    private int bottomHook;
    private int topHook;
    private int leftHook;
    private int rightHook;

    private double pawnHeight;
    private double bishopHeight;
    private double knightHeight;
    private double rookHeight;
    private double queenHeight;
    private double kingHeight;

    private double mid;
    private double high;
    private double rest;

    private int imageHeight;
    private int imageWidth;

    private Line[] vLines;
    private Line[] hLines;

    public BoardProperties() {
        Properties prop = new Properties();
        InputStream input = null;

        try {
            input = new FileInputStream("board.properties");
            // load a properties file
            prop.load(input);

            leftBoard = Integer.parseInt(prop.getProperty("leftBoard"));
            rightBoard = Integer.parseInt(prop.getProperty("rightBoard"));
            bottomBoard = Integer.parseInt(prop.getProperty("bottomBoard"));
            topBoard = Integer.parseInt(prop.getProperty("topBoard"));

            leftDetect = Integer.parseInt(prop.getProperty("leftDetect"));
            rightDetect = Integer.parseInt(prop.getProperty("rightDetect"));
            bottomDetect = Integer.parseInt(prop.getProperty("bottomDetect"));
            topDetect = Integer.parseInt(prop.getProperty("topDetect"));

            leftHook = Integer.parseInt(prop.getProperty("leftHook"));
            rightHook = Integer.parseInt(prop.getProperty("rightHook"));
            bottomHook = Integer.parseInt(prop.getProperty("bottomHook"));
            topHook = Integer.parseInt(prop.getProperty("topHook"));
            
            pawnHeight = Double.parseDouble(prop.getProperty("pawnHeight"));
            bishopHeight = Double.parseDouble(prop.getProperty("bishopHeight"));
            knightHeight = Double.parseDouble(prop.getProperty("knightHeight"));
            rookHeight = Double.parseDouble(prop.getProperty("rookHeight"));
            kingHeight = Double.parseDouble(prop.getProperty("kingHeight"));
            queenHeight = Double.parseDouble(prop.getProperty("queenHeight"));

            mid = Double.parseDouble(prop.getProperty("mid"));
            high = Double.parseDouble(prop.getProperty("high"));
            rest = Double.parseDouble(prop.getProperty("rest"));

            imageHeight = Integer.parseInt(prop.getProperty("imageHeight"));
            imageWidth = Integer.parseInt(prop.getProperty("imageWidth"));
            String[] vls = prop.getProperty("vLine").split(",");
            String[] hls = prop.getProperty("hLine").split(",");

            vLines = new Line[vls.length];
            for(int x=0; x < vls.length; x++){
                vLines[x] = new Line(
                            new Point(Integer.parseInt(vls[x]), 0, Player.Black), 
                            new Point(Integer.parseInt(vls[x]), imageHeight, Player.Black)
                        );
            }

            hLines = new Line[hls.length];
            for(int y=0; y < hls.length; y++){
                hLines[y] = new Line(
                            new Point(0, Integer.parseInt(hls[y]), Player.Black), 
                            new Point(imageWidth, Integer.parseInt(hls[y]), Player.Black)
                        );
            }
            
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void saveParamChanges() {
        try {
            Properties prop = new Properties();
            InputStream input = null;

            input = new FileInputStream("board.properties");
            // load a properties file
            prop.load(input);
        
            Properties props = new Properties();
            props.setProperty("leftBoard", "" + getLeftBoard());
            props.setProperty("rightBoard", "" + getRightBoard());
            props.setProperty("bottomBoard", "" + getBottomBoard());
            props.setProperty("topBoard", "" + getTopBoard());

            props.setProperty("pawnHeight", "" + getPawnHeight());
            props.setProperty("bishopHeight", "" + getBishopHeight());
            props.setProperty("rookHeight", "" + getRookHeight());
            props.setProperty("kingHeight", "" + getKingHeight());

            props.setProperty("mid", "" + getMid());
            props.setProperty("high", "" + getHigh());
            props.setProperty("rest", "" + getRest());

            File f = new File("board.properties");
            OutputStream out = new FileOutputStream(f);
            props.store(out, "Board Properties");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] arg) {
        BoardProperties bp = new BoardProperties();
        //bp.saveParamChanges();
    }

    /**
     * @return the leftBoard
     */
    public int getLeftBoard() {
        return leftBoard;
    }

    /**
     * @param leftBoard the leftBoard to set
     */
    public void setLeftBoard(int leftBoard) {
        this.leftBoard = leftBoard;
    }

    /**
     * @return the rightBoard
     */
    public int getRightBoard() {
        return rightBoard;
    }

    /**
     * @param rightBoard the rightBoard to set
     */
    public void setRightBoard(int rightBoard) {
        this.rightBoard = rightBoard;
    }

    /**
     * @return the bottomBoard
     */
    public int getBottomBoard() {
        return bottomBoard;
    }

    /**
     * @param bottomBoard the bottomBoard to set
     */
    public void setBottomBoard(int bottomBoard) {
        this.bottomBoard = bottomBoard;
    }

    /**
     * @return the topBoard
     */
    public int getTopBoard() {
        return topBoard;
    }

    /**
     * @param topBoard the topBoard to set
     */
    public void setTopBoard(int topBoard) {
        this.topBoard = topBoard;
    }

    /**
     * @return the pawnHeight
     */
    public double getPawnHeight() {
        return pawnHeight;
    }

    /**
     * @param pawnHeight the pawnHeight to set
     */
    public void setPawnHeight(double pawnHeight) {
        this.pawnHeight = pawnHeight;
    }

    /**
     * @return the bishopHeight
     */
    public double getBishopHeight() {
        return bishopHeight;
    }

    /**
     * @param bishopHeight the bishopHeight to set
     */
    public void setBishopHeight(double bishopHeight) {
        this.bishopHeight = bishopHeight;
    }

    /**
     * @return the rookHeight
     */
    public double getRookHeight() {
        return rookHeight;
    }

    /**
     * @param rookHeight the rookHeight to set
     */
    public void setRookHeight(double rookHeight) {
        this.rookHeight = rookHeight;
    }

    /**
     * @return the kingHeight
     */
    public double getKingHeight() {
        return kingHeight;
    }

    /**
     * @param kingHeight the kingHeight to set
     */
    public void setKingHeight(double kingHeight) {
        this.kingHeight = kingHeight;
    }

    /**
     * @return the mid
     */
    public double getMid() {
        return mid;
    }

    /**
     * @param mid the mid to set
     */
    public void setMid(double mid) {
        this.mid = mid;
    }

    /**
     * @return the high
     */
    public double getHigh() {
        return high;
    }

    /**
     * @param high the high to set
     */
    public void setHigh(double high) {
        this.high = high;
    }

    /**
     * @return the rest
     */
    public double getRest() {
        return rest;
    }

    /**
     * @param rest the rest to set
     */
    public void setRest(double rest) {
        this.rest = rest;
    }

    /**
     * @return the imageVertical
     */
    public int getImageHeight() {
        return imageHeight;
    }

    /**
     * @param imageVertical the imageVertical to set
     */
    public void setHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    /**
     * @return the imageHorizontal
     */
    public int getImageHorizontal() {
        return imageWidth;
    }

    /**
     * @param imageHorizontal the imageHorizontal to set
     */
    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }


    /**
     * @return the knightHeight
     */
    public double getKnightHeight() {
        return knightHeight;
    }

    /**
     * @param knightHeight the knightHeight to set
     */
    public void setKnightHeight(double knightHeight) {
        this.knightHeight = knightHeight;
    }

    /**
     * @return the queenHeight
     */
    public double getQueenHeight() {
        return queenHeight;
    }

    /**
     * @param queenHeight the queenHeight to set
     */
    public void setQueenHeight(double queenHeight) {
        this.queenHeight = queenHeight;
    }


    /**
     * @return the bottomDetect
     */
    public int getBottomDetect() {
        return bottomDetect;
    }

    /**
     * @return the topDetect
     */
    public int getTopDetect() {
        return topDetect;
    }

    /**
     * @return the leftDetect
     */
    public int getLeftDetect() {
        return leftDetect;
    }

    /**
     * @return the rightDetect
     */
    public int getRightDetect() {
        return rightDetect;
    }

    /**
     * @return the bottomHook
     */
    public int getBottomHook() {
        return bottomHook;
    }

    /**
     * @return the topHook
     */
    public int getTopHook() {
        return topHook;
    }

    /**
     * @return the leftHook
     */
    public int getLeftHook() {
        return leftHook;
    }

    /**
     * @return the rightHook
     */
    public int getRightHook() {
        return rightHook;
    }
    
   
}
