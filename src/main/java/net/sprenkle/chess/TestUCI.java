/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sprenkle.chess;

/**
 *
 * @author David
 */
public class TestUCI implements UCIInterface{
            private String move = "";

        public TestUCI() {
        }

        @Override
        public String sendCommandAndWait(String command, String waitForString) {
            if ("uci".equals(command)) {
                return "uciok";
            }
            if ("isready".equals(command)) {
                return "readyok";
            }
            if (command.contains("go")) {
                String move = "bestmove " + this.move + " ponder";
                System.out.println(command + "  " + move);
                return move;
            }
            return "";
        }

        @Override
        public void sendCommand(String command) {
            // do nothing
        }

        /**
         * @return the move
         */
        public String getMove() {
            return move;
        }

        /**
         * @param move the move to set
         */
        public void setMove(String move) {
            this.move = move;
        }

}
