/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.test;

import org.apache.commons.math3.stat.regression.SimpleRegression;

/**
 *
 * @author david
 */
public class Regression {
    public static void main(String... arg){
        SimpleRegression sr = new SimpleRegression();
        sr.addData(264, 63);
        sr.addData(563, -63);
        sr.addData(389, 9);
        sr.addData(433, -9);
        sr.addData(266, 63);
        sr.addData(389, 9);
        sr.addData(430, -9);
        sr.addData(558, -63);
        
        System.out.format("X  Slope=%s Y-intercept=%s\n", sr.getSlope(), sr.getIntercept());

        sr = new SimpleRegression();
        sr.addData(69, -135);
        sr.addData(71, -135);
        sr.addData(158, -99);
        sr.addData(200, -81);
        sr.addData(364, -9);
        sr.addData(365, -9);
        sr.addData(367, -9);
        sr.addData(368, -9);
        
        System.out.format("Y  Slope=%s Y-intercept=%s\n", sr.getSlope(), sr.getIntercept());
        
    }
}
