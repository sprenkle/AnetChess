/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.gui;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import net.sprenkle.chess.BoardReader;
import net.sprenkle.chess.ImageUtil;
import net.sprenkle.chess.messages.BoardImage;
import net.sprenkle.chess.messages.ChessMessageReceiver;
import net.sprenkle.chess.messages.MessageHandler;
import net.sprenkle.chess.messages.MqChessMessageSender;
import net.sprenkle.messages.MessageHolder;
import net.sprenkle.messages.images.RequestImage;

/**
 *
 * @author david
 */
public class TestHarness extends javax.swing.JFrame {

    ArrayList<BufferedImage> imageList ;    
    ArrayList<MessageHolder> messageList;
    MqChessMessageSender messageSender = new MqChessMessageSender("TestHarness");
    ChessMessageReceiver messageReceiver = new ChessMessageReceiver("TestHarness", true);
    
    /**
     * Creates new form TestHarness
     */
    public TestHarness() throws IOException {
        initComponents();
        initialize();
       
        messageReceiver.addMessageHandler(RequestImage.class.getSimpleName(), new MessageHandler<RequestImage>() {
            @Override
            public void handleMessage(RequestImage requestImage) {
                try {
                    requestImage(requestImage);
                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(BoardReader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
    }

    public void initialize() throws IOException {
        imageList = new ArrayList<BufferedImage>();  
        try (Stream<Path> paths = Files.walk(Paths.get("D:\\git\\Chess\\images\\game2"))) {
            paths
                    .filter(Files::isRegularFile).sorted()
                    .forEach(x -> {
                try {
                    imageList.add(ImageUtil.loadImage(x.toAbsolutePath().toString()));
                } catch (IOException ex) {
                    Logger.getLogger(TestHarness.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            
            messageList = new ArrayList<MessageHolder>();
          //  messageList.add(new MessageHolder("", ))
        }
    }

    public void requestImage(RequestImage requestImage){
        
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        SendImage = new javax.swing.JButton();
        nextImage = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        prevImage = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        SendImage.setText("Send Image");

        nextImage.setText("Next Image");

        jLabel1.setText("jLabel1");

        prevImage.setText("Prev Image");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(SendImage)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(prevImage)
                        .addGap(14, 14, 14)
                        .addComponent(nextImage))
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(253, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SendImage)
                    .addComponent(nextImage)
                    .addComponent(prevImage))
                .addContainerGap(125, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
            java.util.logging.Logger.getLogger(TestHarness.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TestHarness.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TestHarness.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TestHarness.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new TestHarness().setVisible(true);
                } catch (IOException ex) {
                    Logger.getLogger(TestHarness.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton SendImage;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JButton nextImage;
    private javax.swing.JButton prevImage;
    // End of variables declaration//GEN-END:variables
}