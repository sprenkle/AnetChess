/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.gui;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import net.sprenkle.chess.BoardReader;
import net.sprenkle.chess.PossiblePiece;
import net.sprenkle.chess.imaging.BoardCalculator;
import net.sprenkle.chess.messages.BoardImage;
import net.sprenkle.chess.messages.ChessImageListenerInterface;
import net.sprenkle.chess.messages.ChessMessageReceiver;
import net.sprenkle.chess.messages.GCode;
import net.sprenkle.chess.messages.MessageHandler;
import net.sprenkle.chess.messages.MqChessMessageSender;
import net.sprenkle.chess.messages.RabbitMqChessImageReceiver;
import net.sprenkle.chess.messages.RequestBoardStatus;
import net.sprenkle.imageutils.BlackWhite;
import net.sprenkle.imageutils.ImageUtil;
import net.sprenkle.messages.MessageHolder;
import net.sprenkle.messages.images.RequestImage;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author david
 */
public class Viewer extends javax.swing.JFrame implements ChessImageListenerInterface {

    static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(BoardCalculator.class.getSimpleName());

    private BufferedImage bi;
    private static final String EXCHANGE_NAME = "images";
    private static final String IMAGE_UPDATE = "images_update";
    private final Connection connection;
    private final Channel sendChannel;
    private final BoardCalculator boardCalculator = new BoardCalculator();
    private final int Left = 252;
    private final int Right = 576;
    private final int Top = 11;
    private final int Bottom = 340;
    static double xSlope = -0.4262;
    static double ySlope = 0.4271;
    static double xIntercept = 175.376;
    static double yIntercept = -165.4933;
    static double orgX = 94.3;
    static double orgY = 170.4;
    double low = 14;
    double high = 60;
    double mid = 26;
    LocalTime imageTime;
    MqChessMessageSender messageSender;

    /**
     * Creates new form Viewer
     *
     * @throws java.io.IOException
     * @throws java.util.concurrent.TimeoutException
     */
    public Viewer() throws IOException, TimeoutException {
        initComponents();
        messageSender = new MqChessMessageSender("Viewer");

        ChessMessageReceiver messageReceiver = new ChessMessageReceiver("Viewer", true);

        messageReceiver.addMessageHandler(BoardImage.class.getSimpleName(), new MessageHandler<BoardImage>() {
            @Override
            public void handleMessage(BoardImage boardImage) {
                try {
                    boardImage(boardImage);
                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(BoardReader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.1.90");
        connection = factory.newConnection();

        sendChannel = connection.createChannel();
        sendChannel.exchangeDeclare(IMAGE_UPDATE, BuiltinExchangeType.FANOUT);
    }

    public void boardImage(BoardImage boardImage) {
        logger.debug("received image");
                BufferedImage bImageFromConvert = boardImage.GetBi();
                bi = bImageFromConvert;
                imageTime = LocalTime.now();
                //   bImageFromConvert = createRotated(bImageFromConvert);
                //ImageUtil.saveJpg(bImageFromConvert, "d:\\chess.jpg");
                if (showPieces.isSelected()) {
                    try {
                        boardCalculator.detectPieces(bImageFromConvert);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (initialzieRdo.isSelected()) {
                    try {
                        boardCalculator.initialLines(bImageFromConvert);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (noneRdo.isSelected()) {
                    boardCalculator.showCircles(bImageFromConvert);
                } else {
                    try {
                        BlackWhite.convertImage(bImageFromConvert, blackPercent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                //new ImageIcon(bImageFromConvert);
                imageLbl.setIcon(new ImageIcon(bImageFromConvert));

                MessageHolder mh = new MessageHolder(RequestImage.class.getSimpleName(), new RequestImage());
                messageSender.send(mh);
    }


    private static BufferedImage createRotated(BufferedImage image) {
        AffineTransform at = AffineTransform.getRotateInstance(
                Math.PI, image.getWidth() / 2, image.getHeight() / 2.0);
        return createTransformed(image, at);
    }

    private static BufferedImage createTransformed(
            BufferedImage image, AffineTransform at) {
        BufferedImage newImage = new BufferedImage(
                image.getWidth(), image.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
        g.transform(at);
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return newImage;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        imageLbl = new javax.swing.JLabel();
        showPieces = new javax.swing.JRadioButton();
        showBlackWhite = new javax.swing.JRadioButton();
        initialzieRdo = new javax.swing.JRadioButton();
        imageAdjustTxt = new javax.swing.JTextField();
        adjustImageBtn = new javax.swing.JButton();
        getImageBtn = new javax.swing.JButton();
        resetBtn = new javax.swing.JButton();
        recordPieceLocBtn = new javax.swing.JButton();
        noneRdo = new javax.swing.JRadioButton();
        gcodeX = new javax.swing.JTextField();
        gcodeY = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        buttonGroup1.add(showPieces);
        showPieces.setText("Pieces");
        showPieces.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showPiecesActionPerformed(evt);
            }
        });

        buttonGroup1.add(showBlackWhite);
        showBlackWhite.setSelected(true);
        showBlackWhite.setText("BlackWhite");

        buttonGroup1.add(initialzieRdo);
        initialzieRdo.setText("initialize");

        imageAdjustTxt.setText("0 ");

        adjustImageBtn.setText("Adjust Image");
        adjustImageBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                adjustImageBtnActionPerformed(evt);
            }
        });

        getImageBtn.setText("Get Image");
        getImageBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getImageBtnActionPerformed(evt);
            }
        });

        resetBtn.setText("Reset");
        resetBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetBtnActionPerformed(evt);
            }
        });

        recordPieceLocBtn.setText("RecordPieceLocations");
        recordPieceLocBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                recordPieceLocBtnActionPerformed(evt);
            }
        });

        buttonGroup1.add(noneRdo);
        noneRdo.setText("None");
        noneRdo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                noneRdoActionPerformed(evt);
            }
        });

        gcodeX.setToolTipText("");

        jLabel1.setText("X");

        jLabel2.setText("Y");

        jButton1.setText("Send Gcode");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(imageLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 804, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(initialzieRdo)
                                    .addComponent(showBlackWhite)))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(showPieces)))
                        .addGap(0, 193, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(getImageBtn)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(imageAdjustTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(adjustImageBtn))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(resetBtn)
                                .addGap(27, 27, 27)
                                .addComponent(recordPieceLocBtn))
                            .addComponent(noneRdo)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(5, 5, 5)
                                        .addComponent(jLabel1))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(3, 3, 3)
                                        .addComponent(jLabel2)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(gcodeX, javax.swing.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
                                    .addComponent(gcodeY)))
                            .addComponent(jButton1))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(imageLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 606, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(initialzieRdo)
                        .addGap(13, 13, 13)
                        .addComponent(showPieces)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(showBlackWhite)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(noneRdo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(imageAdjustTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(adjustImageBtn))
                        .addGap(26, 26, 26)
                        .addComponent(getImageBtn)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(resetBtn)
                            .addComponent(recordPieceLocBtn))
                        .addGap(31, 31, 31)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(gcodeX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(gcodeY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addGap(18, 18, 18)
                        .addComponent(jButton1)))
                .addGap(0, 82, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void showPiecesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showPiecesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_showPiecesActionPerformed

    private double blackPercent = .35;
    private void adjustImageBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_adjustImageBtnActionPerformed
        blackPercent = Double.parseDouble(imageAdjustTxt.getText());
        requestImage();
    }//GEN-LAST:event_adjustImageBtnActionPerformed

    private void getImageBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getImageBtnActionPerformed
        requestImage();
    }//GEN-LAST:event_getImageBtnActionPerformed

    private void resetBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetBtnActionPerformed
        // TODO add your handling code here:
        boardCalculator.setIsInitialized(false);
    }//GEN-LAST:event_resetBtnActionPerformed

    private void recordPieceLocBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_recordPieceLocBtnActionPerformed
        MessageHolder mh = new MessageHolder(GCode.class.getSimpleName(), new GCode("G1 X0 Y190 Z26", "Testing piece locations"));
        messageSender.send(mh);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Viewer.class.getName()).log(Level.SEVERE, null, ex);
        }
        LocalTime now = LocalTime.now();

        while (now.isAfter(imageTime)) {
            try {
                logger.debug("Sleeping waiting for image");
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(Viewer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        boolean[][] array = BlackWhite.convert(bi);
        ArrayList<PossiblePiece> pieces = boardCalculator.detectCircle(array);
        int position = 0;
        for (PossiblePiece piece : pieces) {
            if (piece.x >= Left && piece.x <= Right && piece.y >= Top && piece.y <= Bottom) {
                logger.debug(String.format("Piece at %s,%s", piece.x, piece.y));
//                int x = (int) (xSlope * piece.x + xIntercept + orgX);
//                int y = (int) (ySlope * piece.y + yIntercept + 190);
//                String gcode = String.format("G1 X%s Y%s Z%s", x, y, mid);
//                mh = new MessageHolder(GCode.class.getSimpleName(), new GCode(gcode, "Testing piece locations"));
//                messageSender.send(mh);
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException ex) {
//                    Logger.getLogger(Viewer.class.getName()).log(Level.SEVERE, null, ex);
//                }

                movePiece(piece.x, piece.y, position++, 0);
            }
        }
        mh = new MessageHolder(GCode.class.getSimpleName(), new GCode(String.format("G1 X%f Y%f Z26", orgX, orgY), "Testing piece locations"));
        messageSender.send(mh);
        logger.debug("Done recording pieces");
    }//GEN-LAST:event_recordPieceLocBtnActionPerformed

    private void movePiece(int imageX, int imageY, int boardX, int boardY) {
        int x = (int) (xSlope * imageX + xIntercept + orgX);
        int y = (int) (ySlope * imageY + yIntercept + 190);

        int[] dest = calculateBoardPosition(boardX, boardY);

        String gcode = String.format("G1 X%s Y%s Z%s", x, y + 9, mid);
        MessageHolder mh = new MessageHolder(GCode.class.getSimpleName(), new GCode(gcode, "Go to high offset"));
        messageSender.send(mh);

        gcode = String.format("G1 X%s Y%s Z%s", x, y + 9, low);
        mh = new MessageHolder(GCode.class.getSimpleName(), new GCode(gcode, "Go to Low offset"));
        messageSender.send(mh);

        gcode = String.format("G1 X%s Y%s Z%s", x, y, low);
        mh = new MessageHolder(GCode.class.getSimpleName(), new GCode(gcode, "Move to center low"));
        messageSender.send(mh);

        gcode = String.format("G1 X%s Y%s Z%s", x, y, high);
        mh = new MessageHolder(GCode.class.getSimpleName(), new GCode(gcode, "Lift up piece"));
        messageSender.send(mh);

        jog();

        gcode = String.format("G1 X%s Y%s Z%s", dest[0], dest[1], high);
        mh = new MessageHolder(GCode.class.getSimpleName(), new GCode(gcode, "Move to new position high"));
        messageSender.send(mh);

        gcode = String.format("G1 X%s Y%s Z%s", dest[0], dest[1], low);
        mh = new MessageHolder(GCode.class.getSimpleName(), new GCode(gcode, "Lower piece"));
        messageSender.send(mh);

        gcode = String.format("G1 X%s Y%s Z%s", dest[0], dest[1] + 9, low);
        mh = new MessageHolder(GCode.class.getSimpleName(), new GCode(gcode, "Move to offset"));
        messageSender.send(mh);

        gcode = String.format("G1 X%s Y%s Z%s", dest[0], dest[1] + 9, mid);
        mh = new MessageHolder(GCode.class.getSimpleName(), new GCode(gcode, "Move Crane up"));
        messageSender.send(mh);

    }

    private void sendCommand(String gcode, String comment) {
        MessageHolder mh = new MessageHolder(GCode.class.getSimpleName(), new GCode(gcode, comment));
        messageSender.send(mh);
    }

    private void jog() {
        sendCommand("G91", "relative");
        sendCommand("G1 X1", "relative");
        sendCommand("G1 X-1", "relative");
        sendCommand("G1 X1", "relative");
        sendCommand("G1 X-1", "relative");
        sendCommand("G1 X1", "relative");
        sendCommand("G1 X-1", "relative");
        sendCommand("G1 X2", "relative");
        sendCommand("G1 X-2", "relative");
        sendCommand("G1 X2", "relative");
        sendCommand("G1 X-2", "relative");
        sendCommand("G1 X3", "relative");
        sendCommand("G1 X-3", "relative");
        sendCommand("G1 X3", "relative");
        sendCommand("G1 X-3", "relative");
        sendCommand("G1 X3", "relative");
        sendCommand("G1 X-3", "relative");
        sendCommand("G1 X3", "relative");
        sendCommand("G1 X-3", "relative");
        sendCommand("G90", "absolute");

    }

    private int[] calculateBoardPosition(int x, int y) {
        int[] pos = new int[2];

        pos[0] = (int) (orgX + (x - 4) * 18 + 9);
        pos[1] = (int) (orgY - (y * 18 + 9));

        return pos;
    }

    private void noneRdoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_noneRdoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_noneRdoActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        MessageHolder mh2 = new MessageHolder(GCode.class.getSimpleName(), new GCode(String.format("G1 X%s Y%s Z26", gcodeX.getText(), gcodeY.getText()), "Testing piece locations"));
        messageSender.send(mh2);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void requestImage() {

        MessageHolder mh = new MessageHolder(RequestImage.class.getSimpleName(), new RequestImage());
        messageSender.send(mh);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Viewer.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Viewer.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Viewer.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Viewer.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        PropertyConfigurator.configure("D:\\git\\Chess\\src\\main\\java\\log4j.properties");

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new Viewer().setVisible(true);
                } catch (IOException ex) {
                    Logger.getLogger(Viewer.class.getName()).log(Level.SEVERE, null, ex);
                } catch (TimeoutException ex) {
                    Logger.getLogger(Viewer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton adjustImageBtn;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JTextField gcodeX;
    private javax.swing.JTextField gcodeY;
    private javax.swing.JButton getImageBtn;
    private javax.swing.JTextField imageAdjustTxt;
    private javax.swing.JLabel imageLbl;
    private javax.swing.JRadioButton initialzieRdo;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JRadioButton noneRdo;
    private javax.swing.JButton recordPieceLocBtn;
    private javax.swing.JButton resetBtn;
    private javax.swing.JRadioButton showBlackWhite;
    private javax.swing.JRadioButton showPieces;
    // End of variables declaration//GEN-END:variables

    @Override
    public void receivedImage(BufferedImage bi) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
