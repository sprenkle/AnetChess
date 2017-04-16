package net.sprenkle.prototype.servos;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.hopding.jrpicam.RPiCamera;
import com.hopding.jrpicam.exceptions.FailedToRunRaspistillException;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import net.sprenkle.messages.MessageHolder;
import net.sprenkle.messages.images.*;

/**
 *
 * @author pi
 */
public class ImageProviderJFrame extends javax.swing.JFrame {

    private final static String EXCHANGE_NAME = "images";
    private static final String IMAGE_UPDATE = "images_update";
    private boolean takeImages = false;
    private RPiCamera piCamera;
    private Channel channel;
    private String queueName;

    /**
     * Creates new form ImageProviderJFrame
     *
     * @throws java.io.IOException
     * @throws java.util.concurrent.TimeoutException
     */
    public ImageProviderJFrame() throws IOException, TimeoutException {
        initComponents();
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        channel = connection.createChannel();

        channel.exchangeDeclare(IMAGE_UPDATE, "fanout");
        queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, IMAGE_UPDATE, "");
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

        try {
            piCamera = new RPiCamera("/home/pi");
            piCamera.setTimeout(2);
            piCamera.setWidth(800);
            piCamera.setHeight(600);
            piCamera.turnOffPreview();
        } catch (FailedToRunRaspistillException ex) {
            Logger.getLogger(ImageProviderJFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

        start();
        new Thread(new Runnable() {
            @Override
            @SuppressWarnings("SleepWhileInLoop")
            public void run() {
                int count = 0;
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ImageProviderJFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    try {
                        if (takeImages) {
                            BufferedImage bi = piCamera.takeBufferedStill();
                            byte[] imageInByte;
                            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                                ImageIO.write(bi, "jpg", baos);
                                baos.flush();
                                imageInByte = baos.toByteArray();
                                channel.basicPublish(EXCHANGE_NAME, "", null, imageInByte);
                                System.out.format("Picture taken %s\n", count++);
                            }
                            // piCamera.takeStill("Apicture.jpg");
                        }
                    } catch (Exception e) {

                    }

                }
            }
        }).start();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        startBtn = new javax.swing.JButton();
        stopBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        startBtn.setText("Start");
        startBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startBtnActionPerformed(evt);
            }
        });

        stopBtn.setText("Stop");
        stopBtn.setEnabled(false);
        stopBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(startBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(stopBtn)
                .addContainerGap(239, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(startBtn)
                    .addComponent(stopBtn))
                .addContainerGap(263, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public void start() throws IOException {
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                    AMQP.BasicProperties properties, byte[] body) throws IOException {
                try {
                    //System.out.println("Have Message");
                    MessageHolder mh = MessageHolder.fromBytes(body);//                    Logger.getLogger(BoardController.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println(mh.getClassName());
                    switch (mh.getClassName()) {
                        case "AdjustImageBlackWhiteThreshold":
                            adjustImageBlackWhiteThreshold((AdjustImageBlackWhiteThreshold) mh.getObject());
                            break;
                        case "RequestImage":
                            requestImage((RequestImage) mh.getObject());
                            break;
                    }
                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                    Logger.getLogger(BoardController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        channel.basicConsume(queueName, true, consumer);
    }

    private void adjustImageBlackWhiteThreshold(AdjustImageBlackWhiteThreshold adjustImageBlack) {

    }

    private int count = 0;
    private void requestImage(RequestImage requestImage) {
        try {
            BufferedImage bi = piCamera.takeBufferedStill();
            byte[] imageInByte;
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                ImageIO.write(bi, "jpg", baos);
                baos.flush();
                imageInByte = baos.toByteArray();
                channel.basicPublish(EXCHANGE_NAME, "", null, imageInByte);
                System.out.format("Picture taken %s\n", count++);
            }
        }   catch (IOException ex) {
            Logger.getLogger(ImageProviderJFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(ImageProviderJFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void startBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startBtnActionPerformed
        stopBtn.setEnabled(true);
        startBtn.setEnabled(false);
        takeImages = true;
    }//GEN-LAST:event_startBtnActionPerformed

    private void stopBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopBtnActionPerformed
        stopBtn.setEnabled(false);
        startBtn.setEnabled(true);
        takeImages = false;
    }//GEN-LAST:event_stopBtnActionPerformed

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
            java.util.logging.Logger.getLogger(ImageProviderJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ImageProviderJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ImageProviderJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ImageProviderJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new ImageProviderJFrame().setVisible(true);
                } catch (IOException ex) {
                    Logger.getLogger(ImageProviderJFrame.class.getName()).log(Level.SEVERE, null, ex);
                } catch (TimeoutException ex) {
                    Logger.getLogger(ImageProviderJFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton startBtn;
    private javax.swing.JButton stopBtn;
    // End of variables declaration//GEN-END:variables
}
