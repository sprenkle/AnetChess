/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.imaging;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import net.sprenkle.imageutils.BlackWhite;
import org.apache.commons.math3.stat.regression.SimpleRegression;

/**
 *
 * @author david
 */
public class BoardCalculator {

    ArrayList<Point> detectedPices = new ArrayList<>();
 //   Line horizontal[] = new Line[8];
 //   Line vertical[] = new Line[8];
    
    
    public void parseBI(BufferedImage bi) {

    }

    public void detectCircle(BufferedImage bi) {
        detectedPices = new ArrayList<Point>();

        boolean[][] array = BlackWhite.convert(bi);
        Graphics2D g2 = bi.createGraphics();

        BasicStroke stroke = new BasicStroke(2);
        g2.setStroke(stroke);

        for (int y = 6; y < array[0].length - 6; y++) {
            for (int x = 6; x < array.length - 6; x++) {
                if (detectC(array, x, y, true)) {
                    detectedPices.add(new Point(x, y));
                    markPiece(g2, x, y, true);
                    array[x][y] = true;
                    array[x + 1][y] = true;
                    array[x][y + 1] = true;
                    array[x + 1][y + 1] = true;
                    x += 15;
                }
                if (detectC(array, x, y, false)) {
                    detectedPices.add(new Point(x, y));
                    markPiece(g2, x, y, false);
                    array[x][y] = false;
                    array[x + 1][y] = false;
                    array[x + 1][y] = false;
                    array[x + 1][y + 1] = false;
                    x += 15;
                }
            }
        }
        
//        for(int i = 0 ; i < 8; i++){
//            g2.setColor(Color.GREEN);
//            g2.drawLine(horizontal[i].start.x, horizontal[i].start.y
//                    , horizontal[i].end.x, horizontal[i].end.y);
//            g2.drawLine(vertical[i].start.x, vertical[i].start.y
//                    , vertical[i].end.x, vertical[i].end.y);
//        }
    }

    public void initialLines(BufferedImage bi){
        detectedPices = new ArrayList<Point>();

        boolean[][] array = BlackWhite.convert(bi);
        Graphics2D g2 = bi.createGraphics();

        BasicStroke stroke = new BasicStroke(2);
        g2.setStroke(stroke);

        for (int y = 6; y < array[0].length - 6; y++) {
            for (int x = 6; x < array.length - 6; x++) {
                if (detectC(array, x, y, true)) {
                    detectedPices.add(new Point(x, y));
                    markPiece(g2, x, y, true);
                    array[x][y] = true;
                    array[x + 1][y] = true;
                    array[x][y + 1] = true;
                    array[x + 1][y + 1] = true;
                    x += 15;
                }
                if (detectC(array, x, y, false)) {
                    detectedPices.add(new Point(x, y));
                    markPiece(g2, x, y, false);
                    array[x][y] = false;
                    array[x + 1][y] = false;
                    array[x + 1][y] = false;
                    array[x + 1][y + 1] = false;
                    x += 15;
                }
            }
        }
        markLines(bi, g2);
        
    }
    
    private void markLines(BufferedImage bi,Graphics2D g2) {
        g2.setColor(Color.GREEN);
        System.out.println("Number of pieces are " + detectedPices.size());
        detectedPices.sort((Point a, Point b) -> Integer.compare(a.y, b.y));
        int minY = detectedPices.get(0).y;
        int maxY = detectedPices.get(detectedPices.size()-1).y;
        
        detectedPices.sort((Point a, Point b) -> Integer.compare(a.x, b.x));
        for (int i = 0; i < 8; i++) {
            SimpleRegression simpleRegression = new SimpleRegression();
            for (int j = 0; j < 4; j++) {
                simpleRegression.addData(detectedPices.get(i * 4 + j).x, detectedPices.get(i * 4 + j).y);
            }
            int minX = (int)((0 - simpleRegression.getIntercept())/simpleRegression.getSlope());
            int maxX = (int)((bi.getHeight() - simpleRegression.getIntercept())/simpleRegression.getSlope());
            Point start = new Point(minX,0);
            Point end = new Point(maxX,bi.getHeight());
   //         vertical[i] = new Line(start,end);
            g2.drawLine(minX, 0, maxX, bi.getHeight());
        }

        
        detectedPices.sort((Point a, Point b) -> Integer.compare(a.y, b.y));
        for (int i = 0; i < 4; i++) {
            SimpleRegression simpleRegression = new SimpleRegression();
            for (int j = 0; j < 8; j++) {
                simpleRegression.addData(detectedPices.get(i * 8 + j).x, detectedPices.get(i * 8 + j).y);
            }
            
            minY = (int)(simpleRegression.getIntercept());
            maxY = (int)(bi.getWidth() * simpleRegression.getSlope() + simpleRegression.getIntercept());
            Point start = new Point(0, minY);
            Point end = new Point(bi.getWidth(), maxY);
            if(i < 2){
  //              horizontal[i] = new Line(start,end); 
            }else{
  //              horizontal[i+4] = new Line(start,end); 
            }
            g2.drawLine(start.x, start.y, end.x, end.y);
        }
        
//        double spacingStart = (horizontal[6].start.y - horizontal[1].start.y)/5.0; 
//        double spacingEnd = (horizontal[6].end.y - horizontal[1].end.y)/5.0; 
//        
//        for(int i = 0 ; i < 4; i++){
//            Point start = new Point(horizontal[0].start.x, (int)(horizontal[1].start.y + spacingStart + (spacingStart * i)));
//            Point end = new Point(horizontal[0].end.x, (int)(horizontal[1].end.y + spacingEnd + (spacingEnd * i)));
//            horizontal[i + 2] = new Line(start, end);
//            g2.drawLine(horizontal[i+2].start.x, horizontal[i+2].start.y, horizontal[i+2].end.x, horizontal[i+2].end.y);
//        }

    }

    private void markPiece(Graphics2D g2, int x, int y, boolean piece) {
        if (piece) {
            g2.setColor(Color.RED);
        } else {
            g2.setColor(Color.BLUE);
        }
        g2.drawLine(x - 15, y - 15, x + 15, y - 15);
        g2.drawLine(x + 15, y - 15, x + 15, y + 15);
        g2.drawLine(x + 15, y + 15, x - 15, y + 15);
        g2.drawLine(x - 15, y + 15, x - 15, y - 15);
    }

    private static boolean detectC(boolean[][] array, int startX, int startY, boolean piece) {
        //System.out.format("startX=%s startY=%s\n", startX, startY);
        if (array[startX][startY] == piece) {
            return false;
        }

        // Check up
        int y = startY - 1;
        while (array[startX][y] != piece && y > 0) {
            y--;
        }

        if (startY - y < 5) {
            //System.out.format("startY - y < 5  startY=%s y=%s\n", startY, y);
            return false;
        }
        int t = y;
        int yUp = startY - y;
        // Check down
        y = startY + 1;
        while (array[startX][y] != piece && y < array[0].length - 1) {
            y++;
        }
        if (y - startY < 5) {
            //System.out.format("y - startY < 5  startY=%s y=%s\n", startY, y);
            return false;
        }
        int b = y;
        int yDown = y - startY;
        if (Math.abs(yUp - yDown) > 1) {
            //System.out.format("Math.abs(yUp - yDown) > 3  yUp=%s yDown=%s\n", yUp, yDown);
            return false;
        }

        // Check Left
        int x = startX - 1;
        while (array[x][startY] != piece && x > 0) {
            x--;
        }
        if (startX - x < 5) {
            //System.out.format("startX - x < 5  startX=%s x=%s\n", startX, x);
            return false;
        }
        int l = x;
        int xLeft = startX - x;

        x = startX + 1;
        while (array[x][startY] != piece && x < array.length - 1) {
            x++;
        }
        if (x - startX < 5) {
            //System.out.format("x - startX < 5  startX=%s x=%s\n", startX, x);
            return false;
        }
        int r = x;
        int xRight = x - startX;

        if (Math.abs(xLeft - xRight) > 1) {
            //System.out.format("Math.abs(xLeft - xRight) > 4  xLeft=%s xRight=%s\n", xLeft, xRight);
            return false;
        }

        if (array[l][t] != piece || array[r][t] != piece || array[l][b] != piece || array[r][b] != piece) {
            return false;
        }

        if (array[l + 1][t] != piece || array[r - 1][t] != piece || array[l + 1][b] != piece || array[r - 1][b] != piece) {
            return false;
        }

        if (array[l][t + 1] != piece || array[r][t + 1] != piece || array[l][b - 1] != piece || array[r][b - 1] != piece) {
            return false;
        }

        if (array[l + 1][t + 1] != piece || array[r - 1][t + 1] != piece || array[l + 1][b - 1] != piece || array[r - 1][b - 1] != piece) {
            return false;
        }
        //System.out.format("height=%s Width=%s\n", b - t, r - l);
        if (b - t < 15 || b - t > 26 || r - l < 15 || r - l > 25) {
            return false;
        }

        x = startX + 1;
        y = startY + 1;
        int rs = 0;
        while (array[x][y] != piece && x < array.length - 1 && y < array.length - 1) {
            x++;
            y++;
            rs++;
        }
        // x, y = first non circle square down right

        //    System.out.format("%s %s %s %s\n", x, r, y, b);
        for (int tx = x; tx < r; tx++) {
            for (int ty = y; ty < b; ty++) {
                if (array[x][y] != piece) {
                    return false;
                }
            }
        }

        x = startX - 1;
        y = startY - 1;
        int rs2 = 0;
        while (array[x][y] != piece && x > 0 && y > 0) {
            x--;
            y--;
            rs2++;
        }
        // x, y = first non circle square left up

        for (int tx = x; tx > l; tx--) {
            for (int ty = y; ty > t; ty--) {
                if (array[x][y] != piece) {
                    return false;
                }
            }
        }

        if (Math.abs(rs - rs2) > 2) {
            return false;
        }

        rs = rs + rs2;

        x = startX - 1;
        y = startY + 1;
        int ls = 0;
        while (array[x][y] != piece && x > 0 && y < array.length - 1) {
            x--;
            y++;
            ls++;
        }

        for (int tx = x; tx > startX + x; tx++) {
            for (int ty = startY; ty < startY + y; ty++) {
                if (array[x][y] != piece) {
                    return false;
                }
            }
        }

        x = startX + 1;
        y = startY - 1;
        int ls2 = 0;
        while (array[x][y] != piece && x < array.length - 1 && y > 0) {
            x++;
            y--;
            ls2++;
        }

        if (Math.abs(ls - ls2) > 3) {
            return false;
        }

        ls = ls + ls2;

        if (Math.abs(rs - ls) > 2) {
            return false;
        }

        return true;
    }

}
