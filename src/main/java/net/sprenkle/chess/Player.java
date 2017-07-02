/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess;

import java.io.Serializable;

/**
 *
 * @author david
 */
public class Player implements Serializable {

    public static Player White = new Player(true);
    public static Player Black = new Player(false);

    private final String color;

    private Player(boolean white) {
        color = white ? "White" : "Black";
    }

    public String getColor() {
        return color;
    }

    @Override
    public String toString() {
        return color;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!Player.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        final Player other = (Player) obj;
        return this.getColor().equals(other.getColor());
    }
}
