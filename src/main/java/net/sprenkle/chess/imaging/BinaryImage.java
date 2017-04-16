/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.imaging;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import static java.lang.Math.abs;

/**
 *
 * @author David
 */
public class BinaryImage {

    public static BufferedImage convert(BufferedImage orig) {
        ColorModel cm = orig.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = orig.copyData(null);
        boolean isWhite = false;
        boolean lastColWhite = false;
        BufferedImage bi = new BufferedImage(cm, raster, isAlphaPremultiplied, null);
        for (int y = 1; y < bi.getHeight() - 1; y++) {
            for (int x = 1; x < bi.getWidth() - 1; x++) {
                if (x > 1) {
                    Color col = new Color(orig.getRGB(x, y));
                    Color colB = new Color(orig.getRGB(x - 1, y));
                    Color colF = new Color(orig.getRGB(x + 1, y));
                    if (isDifferent(colB,col,colF)) {
                        isWhite = !isWhite;
                    }

                    if (isWhite) {
                        bi.setRGB(x, y, 0x0ffffff);
                    } else {
                        bi.setRGB(x, y, 0x0000000);
                    }
                } else {
                    if (y == 1) {
                        isWhite = false;
                        lastColWhite = false;
                    } else {
                        Color col = new Color(orig.getRGB(x, y - 1));
                        Color colB = new Color(orig.getRGB(x, y));
                        Color colF = new Color(orig.getRGB(x, y + 1));

                        if (isDifferent(colB,col,colF)) {
                            lastColWhite = !lastColWhite;
                        }
                        isWhite = lastColWhite;
                    }

                    if (isWhite) {
                        bi.setRGB(x, y, 0x0ffffff);
                    } else {
                        bi.setRGB(x, y, 0x0000000);
                    }
                }
            }
        }
        
        return bi;
    }
    
    public static boolean isDifferent(Color colB, Color col, Color colF){
        return (double)pixelDiff(col, colB) * 1.05 < (double)pixelDiff(col, colF);
    }

    public static int pixelDiff(Color col1, Color col2) {
        return abs(col1.getRed() - col2.getRGB()) + abs(col1.getGreen() - col2.getGreen()) + abs(col1.getBlue() - col2.getBlue());
    }
}
