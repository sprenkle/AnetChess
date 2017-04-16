package net.sprenkle.chess.imaging;

import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class ReadBoardDetails  {

    double[][][] ar;
    boolean savedBoard = false;
    ChessBoardImage cbi;

    public BufferedImage transform(BufferedImage orig) {
//        ColorModel cm = orig.getColorModel();
//        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
//        WritableRaster raster = orig.copyData(null);
//        BufferedImage bi = new BufferedImage(cm, raster, isAlphaPremultiplied, null);
//
//        ImageUInt8 gray = ConvertBufferedImage.convertFrom(bi, (ImageUInt8) null);
//        ImageUInt8 edgeImage = new ImageUInt8(gray.width, gray.height);
//
//        // Create a canny edge detector which will dynamically compute the threshold based on maximum edge intensity
//        // It has also been configured to save the trace as a graph.  This is the graph created while performing
//        // hysteresis thresholding.                                          2
//        CannyEdge<ImageUInt8, ImageSInt16> canny = FactoryEdgeDetectors.canny(2, true, true, ImageUInt8.class, ImageSInt16.class);
//
//        // The edge image is actually an optional parameter.  If you don't need it just pass in null
//        canny.process(gray, 0.1f, 0.5f, edgeImage);
//
//        // First get the contour created by canny
//        List<EdgeContour> edgeContours = canny.getContours();
//        // The 'edgeContours' is a tree graph that can be difficult to process.  An alternative is to extract
//        // the contours from the binary image, which will produce a single loop for each connected cluster of pixels.
//        // Note that you are only interested in external contours.
//        List<Contour> contours = BinaryImageOps.contour(edgeImage, 8, null);
//
//        // display the results
//        BufferedImage visualBinary = VisualizeBinaryData.renderBinary(edgeImage, null);
//
//        if (!savedBoard) {
//            cbi = new ChessBoardImage();
//
//            cbi.CalcCorners(visualBinary);
//        }
//
////        double[][][] averages = new double[8][8][3];
//        int[][] diff = new int[8][8];
//        for (int x = 0; x < 8; x++) {
//            for (int y = 0; y < 8; y++) {
//                int[] loc = cbi.getAvgSquareLocation(x, y);
//                diff[x][y] = DetectUtil.getAvgDiff(bi, loc[0], loc[1], loc[2], loc[3]);
////                averages[x][y] = cbi.getAvgSquare(x, y, bi);
//                DetectUtil.displaySquare(cbi.getBoardDetails(), x, y, bi);
//            }
//        }
//
//        if (!savedBoard) {
//            savedBoard = true;
//            BoardDetails bd = cbi.getBoardDetails();
//
//  //          bd.setValues(averages);
//            bd.setDiffValues(diff);
//            FileOutputStream fout;
//            try {
//                fout = new FileOutputStream("C:\\dev\\Chess\\Chess\\boardDetail.ser");
//                ObjectOutputStream oos = new ObjectOutputStream(fout);
//                oos.writeObject(bd);
//                oos.close();
//                fout.close();
//            } catch (Exception ex) {
//                Logger.getLogger(ChessPlayDialog.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }

        return null;
    }

    public ReadBoardDetails() {
    }

    public static void main(String[] args) {
        new ReadBoardDetails();
    }

}
