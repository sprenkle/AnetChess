/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.pieces;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sprenkle.chess.exceptions.InvalidLocationException;

/**
 *
 * @author David
 */
public class PieceLocation {

    private final int x, y;

    public PieceLocation(String move) throws InvalidLocationException {
        int[] loc = ConvertMove(move);
        x = loc[0];
        y = loc[1];
    }

    public PieceLocation(int x, int y) throws InvalidLocationException{
        if(x<0 || x > 7 | y<0 | y>7) {
            throw new InvalidLocationException();
        }
        this.x = x;
        this.y = y;
    }

    

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    private int[] ConvertMove(String move) throws InvalidLocationException {
        int rv[] = new int[2];
        Pattern pattern = Pattern.compile("[abcdefgh][12345678]");
        Matcher matcher = pattern.matcher(move);

        if (matcher.matches()) {
            switch (move.substring(0, 1)) {
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

            rv[0] = Integer.parseInt(move.substring(1, 2));
        } else {
            throw new InvalidLocationException();
        }
        return rv;
    }
}
