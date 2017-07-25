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

/**
 *
 * @author david
 */
public class BoardProperties {

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

    private double imageVertical;
    private double imageHorizontal;

    private double vLine1;
    private double vLine2;
    private double vLine3;
    private double vLine4;
    private double vLine5;
    private double vLine6;
    private double vLine7;
    private double vLine8;

    private double hLine1;
    private double hLine2;
    private double hLine3;
    private double hLine4;
    private double hLine5;
    private double hLine6;
    private double hLine7;
    private double hLine8;
    private double xLine[] = new double[8];
    private double yLine[] = new double[8];

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

            imageVertical = Double.parseDouble(prop.getProperty("imageVertical"));
            imageHorizontal = Double.parseDouble(prop.getProperty("imageHorizontal"));

            vLine1 = Double.parseDouble(prop.getProperty("vLine1"));
            vLine2 = Double.parseDouble(prop.getProperty("vLine2"));
            vLine3 = Double.parseDouble(prop.getProperty("vLine3"));
            vLine4 = Double.parseDouble(prop.getProperty("vLine4"));
            vLine5 = Double.parseDouble(prop.getProperty("vLine5"));
            vLine6 = Double.parseDouble(prop.getProperty("vLine6"));
            vLine7 = Double.parseDouble(prop.getProperty("vLine7"));
            vLine8 = Double.parseDouble(prop.getProperty("vLine8"));
            xLine[0] = vLine1;
            xLine[1] = vLine1;
            xLine[2] = vLine1;
            xLine[3] = vLine1;
            xLine[4] = vLine1;
            xLine[5] = vLine1;
            xLine[6] = vLine1;
            xLine[7] = vLine1;
            
            hLine1 = Double.parseDouble(prop.getProperty("hLine1"));
            hLine2 = Double.parseDouble(prop.getProperty("hLine2"));
            hLine3 = Double.parseDouble(prop.getProperty("hLine3"));
            hLine4 = Double.parseDouble(prop.getProperty("hLine4"));
            hLine5 = Double.parseDouble(prop.getProperty("hLine5"));
            hLine6 = Double.parseDouble(prop.getProperty("hLine6"));
            hLine7 = Double.parseDouble(prop.getProperty("hLine7"));
            hLine8 = Double.parseDouble(prop.getProperty("hLine8"));
            yLine[0] = hLine1;
            yLine[1] = hLine1;
            yLine[2] = hLine1;
            yLine[3] = hLine1;
            yLine[4] = hLine1;
            yLine[5] = hLine1;
            yLine[6] = hLine1;
            yLine[7] = hLine1;

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
    public double getImageVertical() {
        return imageVertical;
    }

    /**
     * @param imageVertical the imageVertical to set
     */
    public void setImageVertical(double imageVertical) {
        this.imageVertical = imageVertical;
    }

    /**
     * @return the imageHorizontal
     */
    public double getImageHorizontal() {
        return imageHorizontal;
    }

    /**
     * @param imageHorizontal the imageHorizontal to set
     */
    public void setImageHorizontal(double imageHorizontal) {
        this.imageHorizontal = imageHorizontal;
    }

    /**
     * @return the vLine1
     */
    public double getvLine1() {
        return vLine1;
    }

    /**
     * @param vLine1 the vLine1 to set
     */
    public void setvLine1(double vLine1) {
        this.vLine1 = vLine1;
    }

    /**
     * @return the vLine2
     */
    public double getvLine2() {
        return vLine2;
    }

    /**
     * @param vLine2 the vLine2 to set
     */
    public void setvLine2(double vLine2) {
        this.vLine2 = vLine2;
    }

    /**
     * @return the vLine3
     */
    public double getvLine3() {
        return vLine3;
    }

    /**
     * @param vLine3 the vLine3 to set
     */
    public void setvLine3(double vLine3) {
        this.vLine3 = vLine3;
    }

    /**
     * @return the vLine4
     */
    public double getvLine4() {
        return vLine4;
    }

    /**
     * @param vLine4 the vLine4 to set
     */
    public void setvLine4(double vLine4) {
        this.vLine4 = vLine4;
    }

    /**
     * @return the vLine5
     */
    public double getvLine5() {
        return vLine5;
    }

    /**
     * @param vLine5 the vLine5 to set
     */
    public void setvLine5(double vLine5) {
        this.vLine5 = vLine5;
    }

    /**
     * @return the vLine6
     */
    public double getvLine6() {
        return vLine6;
    }

    /**
     * @param vLine6 the vLine6 to set
     */
    public void setvLine6(double vLine6) {
        this.vLine6 = vLine6;
    }

    /**
     * @return the vLine7
     */
    public double getvLine7() {
        return vLine7;
    }

    /**
     * @param vLine7 the vLine7 to set
     */
    public void setvLine7(double vLine7) {
        this.vLine7 = vLine7;
    }

    /**
     * @return the vLine8
     */
    public double getvLine8() {
        return vLine8;
    }

    /**
     * @param vLine8 the vLine8 to set
     */
    public void setvLine8(double vLine8) {
        this.vLine8 = vLine8;
    }

    /**
     * @return the hLine1
     */
    public double gethLine1() {
        return hLine1;
    }

    /**
     * @param hLine1 the hLine1 to set
     */
    public void sethLine1(double hLine1) {
        this.hLine1 = hLine1;
    }

    /**
     * @return the hLine2
     */
    public double gethLine2() {
        return hLine2;
    }

    /**
     * @param hLine2 the hLine2 to set
     */
    public void sethLine2(double hLine2) {
        this.hLine2 = hLine2;
    }

    /**
     * @return the hLine3
     */
    public double gethLine3() {
        return hLine3;
    }

    /**
     * @param hLine3 the hLine3 to set
     */
    public void sethLine3(double hLine3) {
        this.hLine3 = hLine3;
    }

    /**
     * @return the hLine4
     */
    public double gethLine4() {
        return hLine4;
    }

    /**
     * @param hLine4 the hLine4 to set
     */
    public void sethLine4(double hLine4) {
        this.hLine4 = hLine4;
    }

    /**
     * @return the hLine5
     */
    public double gethLine5() {
        return hLine5;
    }

    /**
     * @param hLine5 the hLine5 to set
     */
    public void sethLine5(double hLine5) {
        this.hLine5 = hLine5;
    }

    /**
     * @return the hLine6
     */
    public double gethLine6() {
        return hLine6;
    }

    /**
     * @param hLine6 the hLine6 to set
     */
    public void sethLine6(double hLine6) {
        this.hLine6 = hLine6;
    }

    /**
     * @return the hLine7
     */
    public double gethLine7() {
        return hLine7;
    }

    /**
     * @param hLine7 the hLine7 to set
     */
    public void sethLine7(double hLine7) {
        this.hLine7 = hLine7;
    }

    /**
     * @return the hLine8
     */
    public double gethLine8() {
        return hLine8;
    }

    /**
     * @param hLine8 the hLine8 to set
     */
    public void sethLine8(double hLine8) {
        this.hLine8 = hLine8;
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
     * @return the xLine
     */
    public double[] getxLine() {
        return xLine;
    }

    /**
     * @return the yLine
     */
    public double[] getyLine() {
        return yLine;
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
