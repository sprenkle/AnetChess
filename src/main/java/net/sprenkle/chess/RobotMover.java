/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess;

import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sprenkle.chess.messages.ChessMessageReceiver;
import net.sprenkle.chess.messages.ChessMessageSender;
import net.sprenkle.chess.messages.ChessMoveMsg;
import net.sprenkle.chess.messages.MessageHandler;
import net.sprenkle.chess.messages.MqChessMessageSender;
import net.sprenkle.chess.messages.RequestMove;
import net.sprenkle.chess.messages.StartGame;
import net.sprenkle.chess.messages.ChessMove;
import net.sprenkle.chess.messages.MessageHolder;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author david Will wait for a request for move for a robot player and respond
 * with the move
 */
public class RobotMover {

    static Logger logger = Logger.getLogger(RobotMover.class.getSimpleName());

    private static final String EXCHANGE_NAME = "CHESS";
    UCIInterface uci;
    ChessMessageSender messageSender;

    public RobotMover(UCIInterface uci, ChessMessageSender messageSender, ChessMessageReceiver messageReceiver) {
        this.uci = uci;
        this.messageSender = messageSender;
        initializeEngine();

        messageReceiver.addMessageHandler(StartGame.class.getName(),new MessageHandler<StartGame>() {
            @Override
            public void handleMessage(StartGame startGame) {
                startGame(startGame);
            }
        });

        messageReceiver.addMessageHandler(RequestMove.class.getName(), new MessageHandler<RequestMove>() {
            @Override
            public void handleMessage(RequestMove requestMove) {
                try {
                    requestMove(requestMove);
                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(RobotMover.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        try {
            messageReceiver.initialize();
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(RobotMover.class.getName()).log(Level.SEVERE, null, ex);
        }
        ;
    }

    private void initializeEngine() {
        uci.sendCommandAndWait("uci", "uciok");
        setOptions();
        uci.sendCommandAndWait("isready", "readyok");

    }

    // Messages that can be recieved
    public void startGame(StartGame startGame) {
        uci.sendCommand("ucinewgame");
        uci.sendCommandAndWait("isready", "readyok");
    }

    Pattern p = Pattern.compile(".*bestmove (\\w\\d\\w\\d).*");

    public void requestMove(RequestMove requestMove) throws Exception {
        if (requestMove.isRobot()) {
            logger.debug(requestMove.toString());
            uci.sendCommand(requestMove.getMoveHistory());
            String move = uci.sendCommandAndWait("go " + getTimeString(), "bestmove");
            logger.info(String.format("Made move -%s-", move));
            Matcher m = p.matcher(move);
            if(m.matches()){
                move = m.group(1);
            }else{
                move = move.substring(move.length() - 4);
            }
            ChessMove chessMove = new ChessMove(requestMove.getTurn(), move);
            ChessMoveMsg chessMoveMsg = new ChessMoveMsg(requestMove.getMoveId(), true, chessMove);
            messageSender.send(new MessageHolder(chessMoveMsg));
            logger.debug(chessMove.toString());
        }
    }

    private String getTimeString() {
        return "wtime 300000 btime 300000 winc 0 binc 0";
    }

    /**
     * *
     * Sets the options of the Engine. I am using Stockfish and hardcoding the
     * options. Work could be done to read these from a file or something. Note:
     * options are everything after "setoption name"
     */
    private void setOptions() {
        String[] options = {"Write Search Log value false",
            "Search Log Filename value SearchLog.txt",
            "Book File value book.bin",
            "Best Book Move value false",
            "Contempt Factor value 0",
            "Mobility (Midgame) value 100",
            "Mobility (Endgame) value 100",
            "Pawn Structure (Midgame) value 100",
            "Pawn Structure (Endgame) value 100",
            "Passed Pawns (Midgame) value 100",
            "Passed Pawns (Endgame) value 100",
            "Space value 100", "Aggressiveness value 100",
            "Cowardice value 100",
            "Min Split Depth value 0",
            "Max Threads per Split Point value 5",
            "Threads value 1",
            "Idle Threads Sleep value false",
            "Hash value 128",
            "Ponder value true",
            "OwnBook value true",
            "MultiPV value 1",
            "Skill Level value 2", // default 2
            "Emergency Move Horizon value 4", // default 40
            "Emergency Base Time value 20", // default 200
            "Emergency Move Time value 10", // default 70
            "Minimum Thinking Time value 1" // default 20
            ,
             "Slow Mover value 1",
            "UCI_Chess960 value false",
            "UCI_AnalyseMode value false"
        };
        for (String option : options) {
            uci.sendCommand("setoption name " + option);
        }
    }

    public static void main(String[] args) throws Exception {
        PropertyConfigurator.configure("D:\\git\\Chess\\src\\main\\java\\log4j.properties");

        new RobotMover(new StockFishUCI(), new MqChessMessageSender("RobotMover"), new ChessMessageReceiver("RobotMover", false));
    }

}
