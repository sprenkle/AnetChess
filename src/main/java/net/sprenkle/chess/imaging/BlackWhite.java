/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.imaging;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 *
 * @author david
 */
public class BlackWhite {

    public static boolean[][] convert(BufferedImage biIn, int threshHold) {
        BufferedImage bi = BlackWhite.thresholdImage(biIn, threshHold);
        
        boolean[][] rv = new boolean[bi.getWidth()][bi.getHeight()];
        
        for (int w = 0; w < bi.getWidth(); w++) {
            for (int h = 0; h < bi.getHeight(); h++) {
                double gl = grayLevel(bi.getRGB(w, h));
                //System.out.println(gl);
                rv[w][h] = gl > threshHold ;
            }
        }
        return rv;
    }

    public static void toString(boolean array[][]) {
        for (int y = 0; y < array[0].length; y++) {
            StringBuffer sb = new StringBuffer();
            for (int x = 0; x < array.length; x++) {
                if (array[x][y]) {
                    sb.append("1");
                } else {
                    sb.append("0");
                }
            }
            System.out.println(sb.toString());
        }
    }

    public static void convertImage(BufferedImage bi) {
        convertImage(bi, .35);
    }

    public static void convertImage(BufferedImage bi, double percentage) {
        double avg = getAvgGreyLevel(bi);
        avg = avg + avg * percentage;
        for (int w = 0; w < bi.getWidth(); w++) {
            for (int h = 0; h < bi.getHeight(); h++) {
                double gl = grayLevel(bi.getRGB(w, h));
                //System.out.println(gl);
                bi.setRGB(w, h, gl < avg ? 0x000000 : 0xFFFFFF);
            }
        }

    }

    
    /**
 * Converts an image to a binary one based on given threshold
 * @param image the image to convert. Remains untouched.
 * @param threshold the threshold in [0,255]
 * @return a new BufferedImage instance of TYPE_BYTE_GRAY with only 0'S and 255's
 */
public static BufferedImage thresholdImage(BufferedImage image, int threshold) {
    BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
    result.getGraphics().drawImage(image, 0, 0, null);
    WritableRaster raster = result.getRaster();
    int[] pixels = new int[image.getWidth()];
    for (int y = 0; y < image.getHeight(); y++) {
        raster.getPixels(0, y, image.getWidth(), 1, pixels);
        for (int i = 0; i < pixels.length; i++) {
            if (pixels[i] < threshold) pixels[i] = 0;
            else pixels[i] = 255;
        }
        raster.setPixels(0, y, image.getWidth(), 1, pixels);
    }
    return result;
}
    
    public static BufferedImage arrayToImage(boolean[][] array) {
        BufferedImage bi = new BufferedImage(50, 50, BufferedImage.TYPE_BYTE_GRAY);
        for (int x = 0; x < array.length; x++) {
            for (int y = 0; y < array[0].length; y++) {
                bi.setRGB(x, y, array[x][y] ? 0xFFFFFF : 0x000000);
            }
        }
        return bi;
    }

    public static double grayLevel(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = (rgb & 0xFF);
        double grayLevel = (r + g + b) / 3;
        return grayLevel;
    }

    private static double getAvgGreyLevel(BufferedImage bi) {
        double sum = 0;
        int count = 0;

        for (int w = 0; w < bi.getWidth(); w += 5) {
            for (int h = 0; h < bi.getHeight(); h += 5) {
                sum += grayLevel(bi.getRGB(w, h));
                count++;
            }
        }
        return sum / count;
    }

}
