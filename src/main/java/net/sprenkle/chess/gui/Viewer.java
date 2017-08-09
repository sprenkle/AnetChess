/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.gui;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.swing.ImageIcon;
import net.sprenkle.chess.BoardProperties;
import net.sprenkle.chess.BoardReader;
import net.sprenkle.chess.Player;
import net.sprenkle.chess.RabbitConfiguration;
import net.sprenkle.chess.models.PossiblePiece;
import net.sprenkle.chess.imaging.BlackWhite;
import net.sprenkle.chess.imaging.BoardCalculator;
import net.sprenkle.chess.imaging.ImageUtil;
import net.sprenkle.chess.messages.BoardAtRest;
import net.sprenkle.chess.messages.BoardImage;
import net.sprenkle.chess.messages.ChessImageListenerInterface;
import net.sprenkle.chess.messages.ChessMessageReceiver;
import net.sprenkle.chess.messages.ChessMove;
import net.sprenkle.chess.messages.ChessMoveMsg;
import net.sprenkle.chess.messages.ConfirmedPieceMove;
import net.sprenkle.chess.messages.GCode;
import net.sprenkle.chess.messages.GameInformation;
import net.sprenkle.chess.messages.MessageHandler;
import net.sprenkle.chess.messages.MessageHolder;
import net.sprenkle.chess.messages.RMQChessMessageSender;
import net.sprenkle.chess.messages.PiecePositions;
import net.sprenkle.chess.messages.RMQChessMessageReceiver;
import net.sprenkle.chess.messages.RMQChesssImageReceiver;
import net.sprenkle.chess.messages.RequestImage;
import net.sprenkle.chess.messages.RequestMove;
import net.sprenkle.chess.messages.RequestMovePieces;
import net.sprenkle.chess.messages.RequestPiecePositions;
import net.sprenkle.chess.messages.SetBoardRestPosition;
import net.sprenkle.chess.messages.StartChessGame;
import net.sprenkle.chess.models.DetectedObject;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author david
 */
public class Viewer extends javax.swing.JFrame implements ChessImageListenerInterface {

    static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(BoardCalculator.class.getSimpleName());
    private ArrayList<BufferedImage> imageList = new ArrayList<>();
    private ArrayList<String> imageNameList = new ArrayList<>();
    private ArrayList<String> moveList = new ArrayList<>();
    private int imageIndex = 0;
    private BufferedImage bi;
    private final BoardCalculator boardCalculator;
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
    RMQChessMessageSender messageSender;

    /**
     * Creates new form Viewer
     *
     * @throws java.io.IOException
     * @throws java.util.concurrent.TimeoutException
     */
    public Viewer() throws IOException, TimeoutException {
        initComponents();
        messageSender = new RMQChessMessageSender("Viewer", new RabbitConfiguration());
        boardCalculator = new BoardCalculator(new BoardProperties(), messageSender );
        RMQChesssImageReceiver imageReceiver = new RMQChesssImageReceiver("Viewer");

        imageReceiver.add(
                new MessageHandler<BoardImage>() {
            @Override
            public void handleMessage(BoardImage boardImage) {
                try {
                    boardImage(boardImage);
                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(BoardReader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        ChessMessageReceiver messageReceiver = new RMQChessMessageReceiver("Viewer", true);

        messageReceiver.addMessageHandler(GameInformation.class.getName(), new MessageHandler<GameInformation>() {
            @Override
            public void handleMessage(GameInformation gameInformation) {
                try {
                    messageTxt.setText(gameInformation.getMessage());
                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(BoardReader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        try {
            imageReceiver.initialize(new RabbitConfiguration());
        } catch (Exception ex) {
            Logger.getLogger(Viewer.class.getName()).log(Level.SEVERE, null, ex);
        }
        bi = ImageUtil.loadImage("D:\\git\\Chess\\images\\cc0e2bad-d025-4b7b-bbf8-e4458e30b377.png");
        imageLbl.setIcon(new ImageIcon(bi));

        try (Stream<Path> paths = Files.walk(Paths.get("D:\\git\\Chess\\images\\game2"))) {
            paths
                    .filter(Files::isRegularFile).sorted()
                    .forEach(x -> {
                        try {
                            imageList.add(net.sprenkle.chess.imaging.ImageUtil.loadImage(x.toAbsolutePath().toString()));
                            imageNameList.add(x.toAbsolutePath().toString());
                        } catch (IOException ex) {
                            Logger.getLogger(TestHarness.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
        }

        moveList.add("e7e5");
        moveList.add("d7d6");
        moveList.add("c8h4");
        moveList.add("h4g4");

        imageName.setText(imageNameList.get(imageIndex));
    }

    public void boardImage(BoardImage boardImage) {
        logger.debug("received image");

        bi = boardImage.getBi();
        imageTime = LocalTime.now();

        //ImageUtil.saveJpg(bImageFromConvert, "d:\\chess.jpg");
        if (showPieces.isSelected()) {
            showPieces(bi);
        } else if (initialzieRdo.isSelected()) {
            showInitialized(bi);
        } else if (noneRdo.isSelected()) {
            showNone(bi);
        } else {
            showBlackWhite(bi);
        }
    }

    public void requestMove(RequestMove requestMove) throws Exception {
        if (requestMove.isRobot()) {
            ChessMoveMsg chessMove = new ChessMoveMsg(requestMove.getMoveId(), true, new ChessMove(requestMove.getTurn(), moveList.remove(0)));
            messageSender.send(new MessageHolder<ChessMoveMsg>(chessMove));
        }
    }

    private void showInitialized(BufferedImage boardImage) {
        BufferedImage altBi = ImageUtil.copyBi(boardImage);
        try {
            boardCalculator.initialLines(altBi);
        } catch (Exception e) {
            e.printStackTrace();
        }

        imageLbl.setIcon(new ImageIcon(createRotated(altBi)));
    }

    private void showPieces(BufferedImage boardImage) {
        BufferedImage altBi = ImageUtil.copyBi(boardImage);
        try {
            boardCalculator.detectPieces(altBi, Player.White, boardCalculator.getKnownBoard());
        } catch (Exception e) {
            e.printStackTrace();
        }
        imageLbl.setIcon(new ImageIcon(createRotated(altBi)));
    }

    private void showNone(BufferedImage boardImage) {
        BufferedImage altBi = ImageUtil.copyBi(boardImage);
        try {
            List<DetectedObject> boardMarker = boardCalculator.detectBoardMarker(altBi);
            if (boardMarker.size() >= 1) {
                logger.debug(String.format("Marker y=%s\n", boardMarker.get(0).getY()));
            }
            List<DetectedObject> hook = boardCalculator.detectHook(altBi);
            if (hook.size() >= 1) {
                logger.debug(String.format("hook x=%s\n", hook.get(0).getX()));
            }

            int hookWidth = boardCalculator.getHookWidth(altBi);
            logger.debug(String.format("hook Width=%s\n", hookWidth));

            boardCalculator.showCircles(altBi);

        } catch (Exception e) {
            e.printStackTrace();
        }

        imageLbl.setIcon(new ImageIcon(createRotated(altBi)));
    }

    private void showBlackWhite(BufferedImage boardImage) {
        //ImageUtil.copyBi(boardImage);
        try {
            BufferedImage altBi = BlackWhite.thresholdImage(boardImage, 100);
            imageLbl.setIcon(new ImageIcon(altBi));

            imageLbl.setIcon(new ImageIcon(createRotated(altBi)));
        } catch (Exception e) {
            e.printStackTrace();
        }

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
        startGameBtn = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        SendImage = new javax.swing.JButton();
        prevImage = new javax.swing.JButton();
        nextImage = new javax.swing.JButton();
        imageName = new javax.swing.JLabel();
        debugChk = new javax.swing.JCheckBox();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        gcodetxt = new javax.swing.JTextField();
        messageTxt = new javax.swing.JTextField();

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
        showBlackWhite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showBlackWhiteActionPerformed(evt);
            }
        });

        buttonGroup1.add(initialzieRdo);
        initialzieRdo.setText("initialize");
        initialzieRdo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                initialzieRdoActionPerformed(evt);
            }
        });

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

        startGameBtn.setText("Start Game");
        startGameBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startGameBtnActionPerformed(evt);
            }
        });

        jButton2.setText("Save Image");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        SendImage.setText("Send Image");
        SendImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SendImageActionPerformed(evt);
            }
        });

        prevImage.setText("Prev Image");
        prevImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prevImageActionPerformed(evt);
            }
        });

        nextImage.setText("Next Image");
        nextImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextImageActionPerformed(evt);
            }
        });

        imageName.setText("jLabel3");

        debugChk.setText("Debug");

        jButton3.setText("Slope");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Send GCode");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        gcodetxt.setText("jTextField1");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(imageLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 804, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(initialzieRdo)
                                .addGap(18, 18, 18)
                                .addComponent(showPieces))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(showBlackWhite)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(noneRdo)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(imageName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(debugChk)
                        .addGap(72, 72, 72))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(25, 25, 25)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(getImageBtn)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(imageAdjustTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(adjustImageBtn))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(resetBtn)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(recordPieceLocBtn))))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(gcodeX, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2)
                                .addGap(21, 21, 21)
                                .addComponent(gcodeY, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton1))
                            .addComponent(jButton2)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(SendImage)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(prevImage)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(nextImage))
                            .addComponent(jButton3)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButton4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(gcodetxt)))
                        .addContainerGap(42, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(messageTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 759, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45)
                .addComponent(startGameBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(imageLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 606, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(initialzieRdo)
                            .addComponent(showPieces))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(showBlackWhite)
                            .addComponent(noneRdo))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(imageAdjustTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(adjustImageBtn))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(getImageBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(resetBtn)
                            .addComponent(recordPieceLocBtn))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(gcodeX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)
                            .addComponent(gcodeY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(jButton1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(imageName)
                            .addComponent(debugChk))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(SendImage)
                            .addComponent(prevImage)
                            .addComponent(nextImage))
                        .addGap(28, 28, 28)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton4)
                            .addComponent(gcodetxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton3)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 71, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(startGameBtn)
                    .addComponent(messageTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void showPiecesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showPiecesActionPerformed
        showPieces(bi);
    }//GEN-LAST:event_showPiecesActionPerformed

    private double blackPercent = .35;
    private void adjustImageBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_adjustImageBtnActionPerformed
        int threshHold = Integer.parseInt(imageAdjustTxt.getText());
        boardCalculator.setThreshHold(threshHold);
    }//GEN-LAST:event_adjustImageBtnActionPerformed

    private void getImageBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getImageBtnActionPerformed
        requestImage();
    }//GEN-LAST:event_getImageBtnActionPerformed

    private void resetBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetBtnActionPerformed
        // TODO add your handling code here:
        boardCalculator.setInitialized(false);
    }//GEN-LAST:event_resetBtnActionPerformed

    private void recordPieceLocBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_recordPieceLocBtnActionPerformed
        MessageHolder<GCode> mh = new MessageHolder(new GCode("G1 X0 Y190 Z26", "Testing piece locations"));
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

        boolean[][] array = BlackWhite.convert(bi, 100);
 //       ArrayList<PossiblePiece> pieces = boardCalculator.detectCircles(array, false);
        int position = 0;
//        for (PossiblePiece piece : pieces) {
//            logger.debug(String.format("Piece at %s,%s", piece.x, piece.y));
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

//            movePiece(piece.x, piece.y, position++, 0);
//        }
//        mh = new MessageHolder<GCode>(new GCode(String.format("G1 X%f Y%f Z26", orgX, orgY), "Testing piece locations"));
//        messageSender.send(mh);
//        logger.debug("Done recording pieces");
    }//GEN-LAST:event_recordPieceLocBtnActionPerformed

    private void movePiece(int imageX, int imageY, int boardX, int boardY) {
        int x = (int) (xSlope * imageX + xIntercept + orgX);
        int y = (int) (ySlope * imageY + yIntercept + 190);

        int[] dest = calculateBoardPosition(boardX, boardY);

        String gcode = String.format("G1 X%s Y%s Z%s", x, y + 9, mid);
        MessageHolder<GCode> mh = new MessageHolder<>(new GCode(gcode, "Go to high offset"));
        messageSender.send(mh);

        gcode = String.format("G1 X%s Y%s Z%s", x, y + 9, low);
        mh = new MessageHolder(new GCode(gcode, "Go to Low offset"));
        messageSender.send(mh);

        gcode = String.format("G1 X%s Y%s Z%s", x, y, low);
        mh = new MessageHolder(new GCode(gcode, "Move to center low"));
        messageSender.send(mh);

        gcode = String.format("G1 X%s Y%s Z%s", x, y, high);
        mh = new MessageHolder(new GCode(gcode, "Lift up piece"));
        messageSender.send(mh);

        jog();

        gcode = String.format("G1 X%s Y%s Z%s", dest[0], dest[1], high);
        mh = new MessageHolder(new GCode(gcode, "Move to new position high"));
        messageSender.send(mh);

        gcode = String.format("G1 X%s Y%s Z%s", dest[0], dest[1], low);
        mh = new MessageHolder(new GCode(gcode, "Lower piece"));
        messageSender.send(mh);

        gcode = String.format("G1 X%s Y%s Z%s", dest[0], dest[1] + 9, low);
        mh = new MessageHolder(new GCode(gcode, "Move to offset"));
        messageSender.send(mh);

        gcode = String.format("G1 X%s Y%s Z%s", dest[0], dest[1] + 9, mid);
        mh = new MessageHolder(new GCode(gcode, "Move Crane up"));
        messageSender.send(mh);

    }

    private void sendCommand(String gcode, String comment) {
        MessageHolder mh = new MessageHolder(new GCode(gcode, comment));
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
        showNone(bi);
    }//GEN-LAST:event_noneRdoActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        MessageHolder mh2 = new MessageHolder(new GCode(String.format("G1 X%s Y%s Z54", gcodeX.getText(), gcodeY.getText()), "Testing piece locations"));
        messageSender.send(mh2);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void startGameBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startGameBtnActionPerformed
        imageIndex = 0;
        moveList.clear();
        moveList.add("e7e5");
        moveList.add("d7d6");
        moveList.add("c8h4");
        moveList.add("h4g4");

        MessageHolder mh = new MessageHolder(new StartChessGame(false, true));
        messageSender.send(mh);
    }//GEN-LAST:event_startGameBtnActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        String filename = String.format("D:\\git\\Chess\\images\\board%s.png", UUID.randomUUID());
        ImageUtil.savePng(bi, filename);
// TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void initialzieRdoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_initialzieRdoActionPerformed
        showInitialized(bi);
    }//GEN-LAST:event_initialzieRdoActionPerformed

    private void showBlackWhiteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showBlackWhiteActionPerformed
        showBlackWhite(bi);
    }//GEN-LAST:event_showBlackWhiteActionPerformed

    private void nextImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextImageActionPerformed
        imageIndex++;
        if (imageIndex >= imageNameList.size()) {
            imageIndex = imageNameList.size() - 1;
        }
        imageName.setText(imageNameList.get(imageIndex));
        imageLbl.setIcon(new ImageIcon(imageList.get(imageIndex)));
    }//GEN-LAST:event_nextImageActionPerformed

    private void prevImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prevImageActionPerformed
        imageIndex--;
        if (imageIndex < 0) {
            imageIndex = 0;
        }
        imageName.setText(imageNameList.get(imageIndex));
        imageLbl.setIcon(new ImageIcon(imageList.get(imageIndex)));
    }//GEN-LAST:event_prevImageActionPerformed

    private void SendImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SendImageActionPerformed
        MessageHolder mh = new MessageHolder(new BoardImage(imageList.get(imageIndex)));
        messageSender.send(mh);
    }//GEN-LAST:event_SendImageActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
       // boardCalculator.syncImage(bi);        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        MessageHolder mh = new MessageHolder(new GCode(gcodetxt.getText(), "from viewer"));
        messageSender.send(mh);

    }//GEN-LAST:event_jButton4ActionPerformed

    private void requestImage() {
        MessageHolder mh = new MessageHolder(new RequestImage(UUID.randomUUID()));
        messageSender.send(mh);
    }

    public void requestBoardRestPosition(SetBoardRestPosition boardRestPosition) {
        if (!debugChk.isSelected()) {
            return;
        }
        messageSender.send(new MessageHolder(new BoardAtRest(true)));
    }

    public void requestMovePieces(RequestMovePieces requestMovePieces) {
        if (!debugChk.isSelected()) {
            return;
        }
        logger.debug(String.format("Requesting move %s", requestMovePieces));
        messageSender.send(new MessageHolder(new RequestPiecePositions(requestMovePieces.getChessMove(), requestMovePieces.isCastle(), requestMovePieces.getUuid())));
    }

    public void piecePositions(PiecePositions piecePositions) {
        if (!debugChk.isSelected()) {
            return;
        }
        messageSender.send(new MessageHolder(new ConfirmedPieceMove(true)));
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
    private javax.swing.JButton SendImage;
    private javax.swing.JButton adjustImageBtn;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox debugChk;
    private javax.swing.JTextField gcodeX;
    private javax.swing.JTextField gcodeY;
    private javax.swing.JTextField gcodetxt;
    private javax.swing.JButton getImageBtn;
    private javax.swing.JTextField imageAdjustTxt;
    private javax.swing.JLabel imageLbl;
    private javax.swing.JLabel imageName;
    private javax.swing.JRadioButton initialzieRdo;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField messageTxt;
    private javax.swing.JButton nextImage;
    private javax.swing.JRadioButton noneRdo;
    private javax.swing.JButton prevImage;
    private javax.swing.JButton recordPieceLocBtn;
    private javax.swing.JButton resetBtn;
    private javax.swing.JRadioButton showBlackWhite;
    private javax.swing.JRadioButton showPieces;
    private javax.swing.JButton startGameBtn;
    // End of variables declaration//GEN-END:variables

    @Override
    public void receivedImage(BufferedImage bi) {
        if (!debugChk.isSelected()) {
            return;
        }
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
