/* This code is the intellectual property of Darrel Riekhof and David Sprenkle.
 * Copyright(c) 2003, All right reserved.
 *
 * Original Author:      Darrel Riekhof
 * Source File:          $RCSfile: ImageUtil.java,v $
 * Created:              2004 July 30
 * Revision:             $Revision: 1.3 $
 * Last Modified By:     $Author: atweb $
 * Last Modified Date:   $Date: 2008/05/19 00:10:48 $
 */
package net.sprenkle.chess;

//------------------------------------------------------------------------------
// Imports
//------------------------------------------------------------------------------
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Image Utility Class
 */
public class ImageUtil {

    //----------------------------------------------------------------------------
    // Data
    //----------------------------------------------------------------------------
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    /**
     * Private constructor, just static methods in this class, don't want anyone
     * creating instances.
     */
    private ImageUtil() {
    }

    public static String getErrorMsg(Exception e) {
        StringBuffer sb = new StringBuffer();

        StackTraceElement[] ste = e.getStackTrace();
        for (StackTraceElement s : ste) {
            sb.append(s.toString());
            sb.append("\n");
        }
        
        return sb.toString();
    }

    //----------------------------------------------------------------------------
    // Public Methods
    //----------------------------------------------------------------------------
    /**
     * Determine if the 2 given BufferedImages are equal to eachother,
     * Pixel by Pixel.
     */
    public static boolean isEqual(BufferedImage bi1, BufferedImage bi2) {
        boolean equal = true;
        boolean stop = false;
        if (bi1.getWidth() == bi2.getWidth() && bi1.getHeight() == bi2.getHeight()) {
            for (int x = 0; x < bi1.getWidth() && !stop; x++) {
                for (int y = 0; y < bi1.getHeight() && !stop; y++) {
                    if (bi1.getRGB(x, y) != bi2.getRGB(x, y)) {
                        equal = false;
                        stop = true;
                    }
                }
            }
        } else {
            equal = false;
        }
        return equal;
    }

    /**
     * Determine if big image contains smaller image.
     * @return - coordinates of top left pixel in big image that starts matching
     *           the small image, or -1, -1 if not found.
     */
    public static int[] findImageMatch(BufferedImage biBig, BufferedImage biSml) {
        int[] coords = new int[]{-1, -1};
        boolean stop = false;
        for (int ybig = 0; ybig <= biBig.getHeight() - biSml.getHeight() && !stop; ybig++) {
            for (int xbig = 0; xbig <= biBig.getWidth() - biSml.getWidth() && !stop; xbig++) {
                boolean match = ImageUtil.isMatch(xbig, ybig, biBig, biSml);
                if (match) {
                    coords[0] = xbig;
                    coords[1] = ybig;
                    stop = true;
                }
            }
        }
        return coords;
    }
    
        /**
     * Determine if the big image matches the small image from the given starting pixel.
     * Pixel by Pixel.
     */
    public static boolean isMatch(int xbig, int ybig, BufferedImage biBig, BufferedImage biSml) {
        boolean equal = true;
        boolean stop = false;
        for (int x = 0; x < biSml.getWidth() && !stop; x++) {
            for (int y = 0; y < biSml.getHeight() && !stop; y++) {
                if (biSml.getRGB(x, y) != biBig.getRGB(x + xbig, y + ybig)) {
                    equal = false;
                    stop = true;
                }
            }
        }
        return equal;
    }

    /**
     * Will determine if the image is onscreen given the rectagle
     * @param rec
     * @param image
     * @return 
     */
    public static boolean isOnScreen(Robot robot , Rectangle rec, BufferedImage image) {
        boolean stop = false;
        for (int ybig = 0; ybig <= rec.getHeight() - image.getHeight() && !stop; ybig++) {
            for (int xbig = 0; xbig <= rec.getWidth() - image.getWidth() && !stop; xbig++) {
                boolean match = ImageUtil.isScreenMatch(xbig, ybig, robot, image);
                if (match) {
                    return true;
                }
            }
        }
        return false;
    }
    
       /**
     * Determine if the big image matches the small image from the given starting pixel.
     * Pixel by Pixel.
     */
    public static boolean isScreenMatch(int xbig, int ybig, Robot biBig, BufferedImage biSml) {
        boolean equal = true;
        boolean stop = false;
        for (int x = 0; x < biSml.getWidth() && !stop; x++) {
            for (int y = 0; y < biSml.getHeight() && !stop; y++) {
                if (biSml.getRGB(x, y) != biBig.getPixelColor(x + xbig, y + ybig).getRGB()) {
                    equal = false;
                    stop = true;
                }
            }
        }
        return equal;
    }
    


    /**
     * Save a BufferedImage to a JPEG file.
     */
    public static void saveJpg(BufferedImage bi, String filename) {
        try {
            ImageIO.write(bi, "jpg", new File(filename));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Save a BufferedImage to a Bitmap file.
     */
    public static void saveBmp(BufferedImage bi, String filename) {
        try {
            ImageIO.write(bi, "bmp", new File(filename));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public static void savePng(BufferedImage bi, String filename){
        savePng(bi, new File(filename));
    }
    /**
     * Save a BufferedImage to a png file.
     */
    public static void savePng(BufferedImage bi, File filename) {
        try {
            ImageIO.write(bi, "png", filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Load an image file.
     * Supports most image formats.
     */
    public static BufferedImage loadImage(String filename) throws IOException {
        BufferedImage bi = null;
        File f = new File(filename);
        bi = ImageIO.read(f);
        return bi;
    }

    /**
     * Loads all the images in a directory
     */
    public static ArrayList<BufferedImage> loadAllImages(String filename) {
        ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();

        try {
            File f = new File(filename);
            File[] imagesFiles = f.listFiles(new ImageFilter());

            for (int i = 0; i < imagesFiles.length; i++) {
                images.add(loadImage(imagesFiles[i].getAbsolutePath()));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return images;
    }

    /**
     * Found the code for method at http://www.wocode.com/WOCode/Files/CLIImageUtil.java
     * Using it to convert a java.awt.image to a BufferedImage.
     *
     * @deprecated - find a better way.
     */
    public static BufferedImage imageFittedToSize(Image inImage, int maxWidth, int maxHeight) {
        int origHeight = inImage.getHeight(null);
        int origWidth = inImage.getWidth(null);
        double heightScale = (double) maxHeight / (double) origHeight;
        double widthScale = (double) maxWidth / (double) origWidth;
        //which of the two dimensions requires MORE scaling, indicated
        //by a lower value. For instance, if the heightScale is .8 but the
        //widthScale is .5, then scaling by .5 will insure both height and
        //width are within bounds.
        double scale = widthScale < heightScale ? widthScale : heightScale;
        // Determine size of new image. Assuming we are scaling, one
        // of them should equal it's maximum possible. But we don't GROW the image
        // we only reduce it if neccesary.
        int outputWidth;
        int outputHeight;
        if (scale < 1.0) {
            outputWidth = (int) (scale * origWidth);
            outputHeight = (int) (scale * origHeight);
        } else {
            outputWidth = origWidth;
            outputHeight = origHeight;
        }
        // Create an image buffer to paint on
        BufferedImage outImage = new BufferedImage(outputWidth, outputHeight, BufferedImage.TYPE_INT_RGB);
        // Set the scale.
        AffineTransform tx = new AffineTransform();
        // If the image is smaller than the desired image size don't bother scaling.
        if (scale < 1.0) {
            tx.scale(scale, scale);
        }
        // Paint image.
        Graphics2D g2d = outImage.createGraphics();
        g2d.drawImage(inImage, tx, null);
        g2d.dispose();
        return outImage;
    }

    /**
     * Sum all the rgb values in given image to a long.
     * Useful for computing a somewhat unique hash for images.
     */
    public static long sumRgb(BufferedImage bi) {
        long hash = 0;
        for (int h = 0; h < bi.getHeight(); h++) {
            for (int w = 0; w < bi.getWidth(); w++) {
                int rgb = bi.getRGB(w, h);
                hash = hash + rgb;
            }
        }
        return hash;
    }

    /**
     * Make a copy of a buffered image.
     */
    public static BufferedImage copyBi(BufferedImage bi) {
        BufferedImage copy = new BufferedImage(bi.getWidth(null), bi.getHeight(null), BufferedImage.TYPE_INT_RGB);
        copy.getGraphics().drawImage(bi, 0, 0, null);
        return copy;
    }

    private static class ImageFilter implements FileFilter {

        @Override
        public boolean accept(File pathname) {
            String ext = pathname.getName().toLowerCase();
            if (ext.endsWith("bmp") || ext.endsWith("jpg") || ext.endsWith("png")) {
                return true;
            } else {
                return false;
            }
        }
    }

    public static void main(String[] args) {
        String[] readFormats = ImageIO.getReaderMIMETypes();
        String[] writeFormats = ImageIO.getWriterMIMETypes();
        //System.out.println("Readers: " +
        ///  Arrays.asList(readFormats));
        //System.out.println("Writers: " +
        //  Arrays.asList(writeFormats));
        try {
            Robot robot = new Robot();

            ImageUtil.savePng(robot.createScreenCapture(new Rectangle(0, 0, 1600, 1200)), "/stuff2/test.png");
            ImageUtil.saveBmp(robot.createScreenCapture(new Rectangle(0, 0, 1600, 1200)), "/stuff2/test.bmp");
        } catch (Exception e) {
        }
    }
}
