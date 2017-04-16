/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.prototype.servos;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sprenkle.messages.AdjustArm;
import net.sprenkle.messages.ArmPosition;
import net.sprenkle.messages.BoardPosition;
import net.sprenkle.messages.MessageHolder;
import net.sprenkle.messages.MoveBoardAbsolute;
import net.sprenkle.messages.MoveBoardRelative;
import se.hirt.pi.adafruit.pwm.PWMDevice;
import se.hirt.pi.adafruit.pwm.PWMDevice.PWMChannel;

/**
 *
 * @author pi
 */
public class BoardController {

    private static final String EXCHANGE_NAME = "boardcontrol";
    private static final int DEFAULT_START = 500;
    private final int leftStartPosition = 150;
    private final int rightStartPosition = 150;
    private final int width = 250;
    private int x = 143;
    private int y = 95;
    private int lastRd = 203;
    private int lastLd = 154;
    private final int leftLead = 0;
    private final int rightLead = -90;
    private int colPos[][];
    private int rowPos[][];

    private Channel channel;
    private String queueName;
    PWMChannel tableServo;
    PWMChannel leftServo;
    PWMChannel rightServo;

    private int tablePwm;
    private int leftPwm;
    private int rightPwm;

    private int boardPos;

    public BoardController() {
        try {
            colPos = new int[9][];
            for(int i = 0; i < 9; i++) colPos[i] = new int[4];
            colPos[0][0] = 40;
            colPos[0][1] = 50;
            colPos[0][2] = 51;
            colPos[0][3] = 110;
            colPos[1][0] = 50;
            colPos[1][1] = 30;
            colPos[1][2] = 45;
            colPos[1][3] = 110;
            colPos[2][0] = 78;
            colPos[2][1] = 30;
            colPos[2][2] = 83;
            colPos[2][3] = 110;
            colPos[3][0] = 106;
            colPos[3][1] = 30;
            colPos[3][2] = 113;
            colPos[3][3] = 110;
            colPos[4][0] = 138;
            colPos[4][1] = 30;
            colPos[4][2] = 141;
            colPos[4][3] = 105;
            colPos[5][0] = 168;
            colPos[5][1] = 30;
            colPos[5][2] = 173;
            colPos[5][3] = 105;
            colPos[6][0] = 198;
            colPos[6][1] = 30;
            colPos[6][2] = 203;
            colPos[6][3] = 100;
            colPos[7][0] = 228;
            colPos[7][1] = 30;
            colPos[7][2] = 235;
            colPos[7][3] = 90;
            colPos[8][0] = 259;
            colPos[8][1] = 10;
            colPos[8][2] = 267;
            colPos[8][3] = 85;
            
            rowPos = new int[9][];
            for(int i = 0; i < 9; i++) rowPos[i] = new int[2];
            rowPos[0][0] = 0;
            rowPos[0][1] = 0;
            rowPos[1][0] = 490;
            rowPos[1][1] = 470;
            rowPos[2][0] = 450;
            rowPos[2][1] = 435;
            rowPos[3][0] = 415;
            rowPos[3][1] = 400;
            rowPos[4][0] = 380;
            rowPos[4][1] = 365;
            rowPos[5][0] = 345;
            rowPos[5][1] = 330;
            rowPos[6][0] = 320;
            rowPos[6][1] = 300;
            rowPos[7][0] = 280;
            rowPos[7][1] = 270;
            rowPos[8][0] = 250;
            rowPos[8][1] = 235;
            
            
            
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            Connection connection = factory.newConnection();
            channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
            queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, EXCHANGE_NAME, "");

            PWMDevice device = new PWMDevice();
            device.setPWMFreqency(50);
            tableServo = device.getChannel(0);
            tableServo.setPWM(0, DEFAULT_START);
            boardPos = DEFAULT_START;

            device = new PWMDevice();
            device.setPWMFreqency(50);
            leftServo = device.getChannel(1);
            leftServo.setPWM(0, leftStartPosition);

            device = new PWMDevice();
            device.setPWMFreqency(50);
            rightServo = device.getChannel(2);
            rightServo.setPWM(0, rightStartPosition);

        } catch (IOException ex) {
            Logger.getLogger(BoardController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TimeoutException ex) {
            Logger.getLogger(BoardController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static {

    }

    public static void main(String[] argv) throws Exception {
        BoardController boardController = new BoardController();
        boardController.start();
    }

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
                        case "MoveBoardRelative":
                            moveBoardRelative((MoveBoardRelative) mh.getObject());
                            break;
                        case "MoveBoardAbsolute":
                            moveBoardAbsolute((MoveBoardAbsolute) mh.getObject());
                            break;
                        case "ArmPosition":
                            moveArmPosition((ArmPosition) mh.getObject());
                            break;
                        case "AdjustArm":
                            adjustArm((AdjustArm) mh.getObject());
                            break;
                        case "BoardPosition":
                            boardPosition((BoardPosition) mh.getObject());
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

    private void boardPosition(BoardPosition boardPosition){
        int index = boardPosition.getHalf() ? 1 : 0;
        moveBoard(rowPos[boardPosition.getSquare()][index]);
    }
    
    private void adjustArm(AdjustArm adjustArm){
        System.out.format("AdjustArm X=%s Y=%s square=%s down=%s\n", adjustArm.getAdjustX(), adjustArm.getAdjustY(), adjustArm.getSquare(), adjustArm.getDown());
        x = x + adjustArm.getAdjustX();
        y = y + adjustArm.getAdjustY();
        System.out.format("X=%s Y=%s\n", x, y);
        moveX();
    }
    
    private void moveBoardRelative(MoveBoardRelative mbr) throws IOException {
        System.out.format("Move board relative %s\n", mbr.getDistance());
        try {
            tableServo.setPWM(0, mbr.getDistance());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void moveBoardAbsolute(MoveBoardAbsolute mba) throws IOException {
        System.out.format("Move board absolute %s\n", mba.getDistance());
        moveBoard(mba.getDistance());
        
//        try {
//            //boardPos
//            int finalPos = mba.getDistance();
//
//            if (finalPos > boardPos) {
//                for (int i = boardPos; i <= finalPos; i++) {
//                    tableServo.setPWM(0, i);
//                    Thread.sleep(100);
//                }
//            } else {
//                for (int i = boardPos; i >= finalPos; i--) {
//                    tableServo.setPWM(0, i);
//                    Thread.sleep(100);
//                }
//            }
//
//            boardPos = finalPos;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private void moveBoard(int distance){
        try {
            //boardPos
            int finalPos = distance;

            if (finalPos > boardPos) {
                for (int i = boardPos; i <= finalPos; i++) {
                    tableServo.setPWM(0, i);
                    Thread.sleep(100);
                }
            } else {
                for (int i = boardPos; i >= finalPos; i--) {
                    tableServo.setPWM(0, i);
                    Thread.sleep(100);
                }
            }

            boardPos = finalPos;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void moveArmPosition(ArmPosition armPosition) {
        System.out.format("square=%s down=%s\n", armPosition.getArmPosition(), armPosition.isDown());
        int index = armPosition.isDown() ? 2 : 0;
        
        x = colPos[armPosition.getArmPosition()][index];
        y = colPos[armPosition.getArmPosition()][index + 1];

        System.out.format("X=%s Y=%s\n", x, y);
        moveX();
    }

    private void moveX() {
        try {
            System.out.format("leftLead=%s rightLead=%s\n", leftLead, rightLead);

            double initld = Math.sqrt(Math.pow((double) x, 2.0) + Math.pow(y, 2));
            double initrd = Math.sqrt(Math.pow((double) (width - x), 2.0) + Math.pow(y, 2));



            double ld = (Math.sqrt(Math.pow((double) x, 2.0) + Math.pow(y, 2)) + leftLead);
            double rd = width - (Math.sqrt(Math.pow((double) (width - x), 2.0) + Math.pow(y, 2)) + rightLead);



            System.out.format("ld=%s rd=%s\n",ld, rd);
            int incr;

            if(Math.abs(lastLd - (int)ld) > Math.abs(lastRd - rd)){
                incr = Math.abs(lastLd - (int)ld);
            }else{
                incr = Math.abs(lastRd - (int)rd);
            }

            double leftDiff = (ld - (double)lastLd)/incr;
            double rightDiff = (rd - (double)lastRd)/incr;
            
            for(int i = 0; i < incr; i++){
                setLeftPwm((int)(lastLd + (leftDiff * i)));
                setRightPwm((int)(lastRd + (rightDiff * i)));
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(BoardController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            lastLd = (int) ld;
            lastRd = (int) rd;
        } catch (IOException ex) {
            Logger.getLogger(TestHookAndTable.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void setLeftPwm(int value) throws IOException {
       // System.out.format("Set Left Pwm=%s\n", value);
        leftServo.setPWM(0, value);
        leftPwm = value;
    }

    private void setRightPwm(int value) throws IOException {
        //System.out.format("Set Right Pwm=%s\n", value);
        rightServo.setPWM(0, value);
        rightPwm = value;
    }
}
