/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.imaging;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author David
 */
public class ChessBoardImage {

    private BoardDetails boardDetails;

    public ChessBoardImage() {
        boardDetails = new BoardDetails();
    }

    public ChessBoardImage(int top, int bottom, int left, int right, double squareWidth, double squareHeight) {
        boardDetails = new BoardDetails(left, right, top, bottom, squareWidth, squareHeight);
    }

    public BoardDetails getBoardDetails() {
        return boardDetails;
    }

    public void CalcCorners(BufferedImage bi) {

        ArrayList<Integer> left = new ArrayList<>();
        ArrayList<Integer> right = new ArrayList<>();
        ArrayList<Integer> bottom = new ArrayList<>();
        ArrayList<Integer> top = new ArrayList<>();

        for (int x = 0; x < bi.getWidth(); x++) {
            BoardLine boardLine = new BoardLine(new Point(x, 0));
            for (int y = 0; y < bi.getHeight(); y++) {
                boardLine.addPixel(bi.getRGB(x, y));
            }
            if (boardLine.foundLine()) {
                top.add(boardLine.getStart());
                bottom.add(boardLine.getEnd());
//                for (int y = boardLine.getStart(); y < boardLine.getEnd(); y++) {
//                    bi.setRGB(x, y, 0xFFFFFF);
//                }
            }
        }

        for (int y = 0; y < bi.getHeight(); y++) {
            BoardLine boardLine = new BoardLine(new Point(0, y));
            for (int x = 0; x < bi.getWidth(); x++) {
                boardLine.addPixel(bi.getRGB(x, y));
            }
            if (boardLine.foundLine()) {
                left.add(boardLine.getStart());
                right.add(boardLine.getEnd());
//                for (int x = boardLine.getStart(); x < boardLine.getEnd(); x++) {
//                    bi.setRGB(x, y, 0xFFFFFF);
//                }
            }
        }

        Collections.sort(left);
        Collections.sort(right);
        Collections.sort(top);
        Collections.sort(bottom);

        if (right.size() > 4) {
            int quarterNum = right.size() / 4;
            boardDetails.setRight(right.get(right.size() - quarterNum));
        }
        if (left.size() > 4) {
            int quarterNum = left.size() / 4;
            boardDetails.setLeft(left.get(quarterNum));
        }

        int quarterNum = bottom.size() / 4;
        boardDetails.setBottom(bottom.get(bottom.size() - quarterNum));
        quarterNum = top.size() / 4;
        boardDetails.setTop(top.get(quarterNum));

        boardDetails.setSquareHeight((double) (boardDetails.getBottom() - boardDetails.getTop()) / 8.0);
        boardDetails.setSquareWidth((double) (boardDetails.getRight() - boardDetails.getLeft()) / 8.0);
    }

    public boolean isDifferent(int x, int y, BufferedImage bi, double[][][] rgb) {
        int xs = boardDetails.getSquare(x, y).x;
        int xl = boardDetails.getSquare(x, y).width + boardDetails.getSquare(x, y).x;
        int ys = boardDetails.getSquare(x, y).y;
        int yl = boardDetails.getSquare(x, y).y + boardDetails.getSquare(x, y).height;
        double[] rgbAvg = new double[3];
        int size = 0;
        int countDiff = 0;
        for (int x1 = xs; x1 < xl; x1++) {
            for (int y1 = ys; y1 < yl; y1++) {
                Color color = new Color(bi.getRGB(x1, y1));

                double diff = abs(color.getRed() - rgb[x][y][0]) + abs(color.getGreen() - rgb[x][y][1]) + abs(color.getBlue() - rgb[x][y][2]);

                if (diff > 60) {
                    countDiff++;
                }
            }
        }

        return countDiff > 5;
    }

    public int[] getAvgSquareLocation(int x, int y) {
        int[] loc = new int[4];
        loc[0] = boardDetails.getSquare(x, y).x;
        loc[1] = boardDetails.getSquare(x, y).y;
        loc[2] = boardDetails.getSquare(x, y).width + boardDetails.getSquare(x, y).x;
        loc[3] = boardDetails.getSquare(x, y).height + boardDetails.getSquare(x, y).y;

        return loc;
    }

    public double[] getAvgSquare(int x, int y, BufferedImage bi) {

        int xs = boardDetails.getSquare(x, y).x;
        int xl = boardDetails.getSquare(x, y).width + boardDetails.getSquare(x, y).x;
        int ys = boardDetails.getSquare(x, y).y;
        int yl = boardDetails.getSquare(x, y).y + boardDetails.getSquare(x, y).height;
        double[] rgbAvg = new double[3];
        int size = 0;

        for (int x1 = xs; x1 < xl; x1++) {
            for (int y1 = ys; y1 < yl; y1++) {
                Color color = new Color(bi.getRGB(x1, y1));

                rgbAvg[0] += color.getRed();
                rgbAvg[1] += color.getGreen();
                rgbAvg[2] += color.getBlue();
                size++;
            }
        }

        return new double[]{rgbAvg[0] / size, rgbAvg[1] / size, rgbAvg[2] / size};
    }

        public int getAvgGreyValue2(int x, int y, BufferedImage bi) {

        int x_s = boardDetails.getSquare(x, y).x;
        int x_l = boardDetails.getSquare(x, y).width + boardDetails.getSquare(x, y).x;
        int y_s = boardDetails.getSquare(x, y).y;
        int y_l = boardDetails.getSquare(x, y).y + boardDetails.getSquare(x, y).height;
        int size = 0;

        int avg = 0;

        double red = 0;
        double green = 0;
        double blue = 0;
        
        for (int x1 = x_s; x1 < x_l; x1++) {
                Color color = new Color(bi.getRGB(x1, 0));
                red += color.getRed();
                green += color.getGreen();
                blue += color.getBlue();
        
                color = new Color(bi.getRGB(x1, y_l));
                red += color.getRed();
                green += color.getGreen();
                blue += color.getBlue();
        }

        
        for (int y1 = y_s + 1; y1 < y_l - 1; y1++) {
               Color color = new Color(bi.getRGB(0, y1));
                red += color.getRed();
                green += color.getGreen();
                blue += color.getBlue();
        
                color = new Color(bi.getRGB(x_l, y1));
                red += color.getRed();
                green += color.getGreen();
                blue += color.getBlue();
        }
        
        red = red/(((x_l * 2) + (y_l * 2)));
        green = green/(((x_l * 2) + (y_l * 2)));
        blue = blue/(((x_l * 2) + (y_l * 2)));

        double cred = 0;
        double cgreen = 0;
        double cblue = 0;
        int centerX = ((x_l - x_s) / 2) - 2;
        int centerY = ((y_l - y_s) / 2) - 2;
        for(int x1=0;x1<4;x1++){
            for(int y1=0;y1<4;y1++){
                Color color = new Color(bi.getRGB(centerX + x1, centerY + y1));
            }
        }
        
        // System.out.println(x + " " + y + " " + greyAvg);
        return avg;
    }

    
    public int getAvgGreyValue(int x, int y, BufferedImage bi) {

        int x_s = boardDetails.getSquare(x, y).x;
        int x_l = boardDetails.getSquare(x, y).width + boardDetails.getSquare(x, y).x;
        int y_s = boardDetails.getSquare(x, y).y;
        int y_l = boardDetails.getSquare(x, y).y + boardDetails.getSquare(x, y).height;
        int size = 0;

        int avg = 0;

        for (int x1 = x_s; x1 < x_l; x1++) {
            for (int y1 = y_s; y1 < y_l; y1++) {
                Color color = new Color(bi.getRGB(x1, y1));
                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();

                avg = avg + red + green + blue;
            }
        }
        // System.out.println(x + " " + y + " " + greyAvg);
        return avg;
    }

    public int getDiffSquare(int x, int y, BufferedImage bi) {
        ArrayList<Integer> pixelRedValues = new ArrayList<>();
        ArrayList<Integer> pixelGreenValues = new ArrayList<>();
        ArrayList<Integer> pixelBlueValues = new ArrayList<>();

        int xs = boardDetails.getSquare(x, y).x;
        int xl = boardDetails.getSquare(x, y).width + boardDetails.getSquare(x, y).x;
        int ys = boardDetails.getSquare(x, y).y;
        int yl = boardDetails.getSquare(x, y).y + boardDetails.getSquare(x, y).height;

        for (int x1 = xs; x1 < xl; x1++) {
            for (int y1 = ys; y1 < yl; y1++) {
                Color color = new Color(bi.getRGB(x1, y1));
                pixelRedValues.add(color.getRed());
                pixelGreenValues.add(color.getGreen());
                pixelBlueValues.add(color.getBlue());
                //             System.out.printf("%d, %d, %d, %d, %d, %d, %d\n", x, y, x1, y1, color.getRed(), color.getGreen(), color.getBlue());
            }
        }

        Collections.sort(pixelRedValues);
        Collections.sort(pixelGreenValues);
        Collections.sort(pixelBlueValues);

        int total = 0;
        for (int i = 0; i < 10; i++) {
            total += abs(pixelRedValues.get(i + 10) - pixelRedValues.get(pixelRedValues.size() - 20 + i))
                    + abs(pixelGreenValues.get(i + 10) - pixelRedValues.get(pixelGreenValues.size() - 20 + i))
                    + abs(pixelBlueValues.get(i + 10) - pixelRedValues.get(pixelBlueValues.size() - 20 + i));
        }

        return total;
    }

}
