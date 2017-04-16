package net.sprenkle.chess.imaging;

import net.sprenkle.chess.states.State;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import net.sprenkle.chess.ImageUtil;

public class ChessPlayDialog  {
    int imageNumber = 0;
    private State state = State.getState(State.STARTSTATE);

    public BufferedImage transform(BufferedImage orig) {
        ColorModel cm = orig.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = orig.copyData(null);
        BufferedImage bi = new BufferedImage(cm, raster, isAlphaPremultiplied, null);
        ImageUtil.savePng(bi, String.format("images\\chessImage%04d.png", imageNumber++));
        state = state.process(bi);
        return bi;
    }

    public ChessPlayDialog() {

//        Webcam webcam = Webcam.getDefault();
//
//        webcam.setViewSize(WebcamResolution.VGA.getSize());
//        webcam.setImageTransformer(this);
//       
//        webcam.open(false);
//
//        JFrame window = new JFrame("Test Transformer");
//
//        WebcamPanel panel = new WebcamPanel(webcam);
//        panel.setFPSDisplayed(true);
//        panel.setFillArea(true);
//
//        window.add(panel);
//        window.pack();
//        window.setVisible(true);
//        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    public static void main(String[] args) {
        new ChessPlayDialog();
    }



}
