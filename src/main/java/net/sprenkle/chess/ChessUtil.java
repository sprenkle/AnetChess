/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sprenkle.chess.exceptions.InvalidLocationException;
import net.sprenkle.chess.exceptions.InvalidMoveException;

/**
 *
 * @author David
 */
public class ChessUtil {

    public static String[] ConvertChessMove(String move) throws InvalidMoveException {
        
        Pattern pattern = Pattern.compile("([abcdefgh][12345678])([abcdefgh][12345678])([rknqb]{0,1})");
        Matcher matcher = pattern.matcher(move);
        
        if(!matcher.matches()) throw new InvalidMoveException();
        
        String[] rv = new String[3];
        rv[0] = matcher.group(1);
        rv[1] = matcher.group(2);
        rv[2] = matcher.group(3);
        return rv;
    }

    public static String convertXYtoChessMove(int fromX, int fromY, int toX, int toY){
        StringBuilder sb = new StringBuilder();
        
        sb.append(numberToLetter(fromX));
        sb.append((fromY) + 1);
        sb.append(numberToLetter(toX));
        sb.append((toY) + 1);
        
        return sb.toString();
    }
    
    private static String numberToLetter(int number){
        switch(number){
            case 0: return "a";
            case 1: return "b";
            case 2: return "c";
            case 3: return "d";
            case 4: return "e";
            case 5: return "f";
            case 6: return "g";
            case 7: return "h";
        }
        
        return "";
    }
    
    public static int[] ConvertLocation(String location) throws InvalidLocationException {
        int[] rv = new int[2];
        
        Pattern pattern = Pattern.compile("[abcdefgh][12345678]");
        Matcher matcher = pattern.matcher(location);

        if (matcher.matches()) {
            switch (location.substring(0, 1)) {
                case "a":
                    rv[0] = 0;
                    break;
                case "b":
                    rv[0] = 1;
                    break;
                case "c":
                    rv[0] = 2;
                    break;
                case "d":
                    rv[0] = 3;
                    break;
                case "e":
                    rv[0] = 4;
                    break;
                case "f":
                    rv[0] = 5;
                    break;
                case "g":
                    rv[0] = 6;
                    break;
                case "h":
                    rv[0] = 7;
                    break;
            }

            rv[1] = Integer.parseInt(location.substring(1, 2))-1;
        } else {
            throw new InvalidLocationException();
        }
        
        return rv;
    }

    
        public static int[] convertFromMove(String move) {
        int[] rv = new int[4];

        String from = move.substring(0, 2);
        String to = move.substring(2, 4);

        int[] fromValues = convertSingleMove(from);
        int[] toValues = convertSingleMove(to);

        rv[0] = fromValues[0];
        rv[1] = fromValues[1];
        rv[2] = toValues[0];
        rv[3] = toValues[1];

        return rv;
    }

    private static int[] convertSingleMove(String move) {
        int[] rv = new int[2];

        switch (move.charAt(0)) {
            case 'a':
                rv[0] = 7;
                break;
            case 'b':
                rv[0] = 6;
                break;
            case 'c':
                rv[0] = 5;
                break;
            case 'd':
                rv[0] = 4;
                break;
            case 'e':
                rv[0] = 3;
                break;
            case 'f':
                rv[0] = 2;
                break;
            case 'g':
                rv[0] = 1;
                break;
            case 'h':
                rv[0] = 0;
                break;
        }

        rv[1] = Integer.parseInt(move.substring(1, 2)) - 1;

        return rv;
    }

    public static String convertToMove(int[] move) {

        return String.format("%s%s%s%s", convertAlpha(move[0]), move[1] + 1, convertAlpha(move[2]), move[3] + 1);
    }

    private static String convertAlpha(int value) {
        switch (value) {
            case 0:
                return "h";
            case 1:
                return "g";
            case 2:
                return "f";
            case 3:
                return "e";
            case 4:
                return "d";
            case 5:
                return "c";
            case 6:
                return "b";
            case 7:
                return "a";
        }
        return "Z";
        // --throw new Exception("not valid alpha");
    }

     

    public static void main(String[] arg){
        Random random = new Random();
        for(int i = 0; i < 1;i++){
            System.out.println(random.nextInt(11));
        }
    }
}
