/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess;

import net.sprenkle.chess.states.BoardReaderState;
import net.sprenkle.chess.models.PossiblePiece;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import net.sprenkle.chess.controllers.PiecePositionsIdentifier;
import net.sprenkle.chess.imaging.BoardCalculator;
import net.sprenkle.chess.messages.BoardAtRest;
import net.sprenkle.chess.messages.BoardStatus;
import net.sprenkle.chess.messages.ChessImageReceiver;
import net.sprenkle.chess.messages.ChessMessageReceiver;
import net.sprenkle.chess.messages.ChessMessageSender;
import net.sprenkle.chess.messages.RMQChessMessageReceiver;
import net.sprenkle.chess.messages.MessageHandler;
import net.sprenkle.chess.messages.RMQChessMessageSender;
import net.sprenkle.chess.messages.RequestSetupAndBoardStatus;
import net.sprenkle.chess.messages.RequestMove;
import net.sprenkle.chess.messages.SetBoardRestPosition;
import net.sprenkle.chess.messages.ChessMoveMsg;
import net.sprenkle.chess.messages.KnownBoardPositions;
import net.sprenkle.chess.messages.PiecePositions;
import net.sprenkle.chess.messages.RequestPiecePositions;
import net.sprenkle.chess.messages.StartChessGame;
import net.sprenkle.chess.messages.ChessMove;
import net.sprenkle.chess.messages.DetectedObjects;
import net.sprenkle.chess.messages.GCode;
import net.sprenkle.chess.messages.GameInformation;
import net.sprenkle.chess.messages.GridObjects;
import net.sprenkle.chess.messages.MessageHolder;
import net.sprenkle.chess.messages.RMQChesssImageReceiver;
import net.sprenkle.chess.messages.RequestGridObjects;
import net.sprenkle.chess.messages.RequestImage;
import net.sprenkle.chess.models.DetectedObject;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author david
 */
public class BoardReader {

    static Logger logger = Logger.getLogger(BoardReader.class.getSimpleName());
    private final ChessMessageSender messageSender;
    private final BoardCalculator boardCalculator;
    private final PiecePositionsIdentifier piecePositionsIdentifier;
    private final BoardReaderState state;
    private RequestMove requestedMove;
    private RequestPiecePositions requestPiecePositions;
    private final BoardProperties boardProperties;

    public BoardReader(BoardReaderState state, ChessMessageSender messageSender,
            ChessMessageReceiver messageReceiver, BoardCalculator boardCalculator,
            PiecePositionsIdentifier piecePositionsIdentifier, ChessImageReceiver imageReceiver, BoardProperties boardProperties) throws Exception {
        this.messageSender = messageSender;
        this.boardCalculator = boardCalculator;
        this.piecePositionsIdentifier = piecePositionsIdentifier;
        this.state = state;
        this.boardProperties = boardProperties;

        messageReceiver.addMessageHandler(RequestMove.class.getName(), new MessageHandler<RequestMove>() {
            @Override
            public void handleMessage(RequestMove requestMove) {
                try {
                    requestMove(requestMove);
                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(BoardReader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        messageReceiver.addMessageHandler(RequestSetupAndBoardStatus.class.getName(), new MessageHandler<RequestSetupAndBoardStatus>() {
            @Override
            public void handleMessage(RequestSetupAndBoardStatus requestBoardStatus) {
                try {
                    requestSetupAndBoardStatus(requestBoardStatus);
                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(BoardReader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        messageReceiver.addMessageHandler(StartChessGame.class.getName(), new MessageHandler<StartChessGame>() {
            @Override
            public void handleMessage(StartChessGame startGame) {
                try {
                    startGame(startGame);
                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(BoardReader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        messageReceiver.addMessageHandler(RequestPiecePositions.class.getName(), new MessageHandler<RequestPiecePositions>() {
            @Override
            public void handleMessage(RequestPiecePositions requestPiecePositions) {
                try {
                    requestPiecePositions(requestPiecePositions);
                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(BoardReader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        messageReceiver.addMessageHandler(KnownBoardPositions.class.getName(), new MessageHandler<KnownBoardPositions>() {
            @Override
            public void handleMessage(KnownBoardPositions knownBoardPositions) {
                try {
                    knownBoardPositions(knownBoardPositions);
                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(BoardReader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        messageReceiver.addMessageHandler(BoardAtRest.class.getName(), new MessageHandler<BoardAtRest>() {
            @Override
            public void handleMessage(BoardAtRest boardAtRest) {
                try {
                    boardAtRest(boardAtRest);
                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(BoardReader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        messageReceiver.addMessageHandler(GridObjects.class.getName(), new MessageHandler<GridObjects>() {
            @Override
            public void handleMessage(GridObjects gridObjects) {
                gridObjects(gridObjects);
            }
        });

        messageReceiver.addMessageHandler(DetectedObjects.class.getName(), new MessageHandler<DetectedObjects>() {
            @Override
            public void handleMessage(DetectedObjects detectedObjects) {
                detectedObjects(detectedObjects);
            }
        });

        messageReceiver.initialize(new RabbitConfiguration());
        imageReceiver.initialize(new RabbitConfiguration());
        state.reset();
    }

    private void boardAtRest(BoardAtRest boardAtRest) {
        if (state.inState(BoardReaderState.SET_REST_POSITION)) {
            state.setState(BoardReaderState.CHECK_FOR_GAME_SETUP);
            requestGridObjects();
        }
    }

    private void detectedObjects(DetectedObjects detectedObjects) {

    }

    private void gridObjects(GridObjects gridObjects) {
        // This state checks to see if all pieces are setup and if so sends board status
        if (state.inState(BoardReaderState.CHECK_FOR_GAME_SETUP)) {
            if (!boardCalculator.verifyStartingPosition(gridObjects.getGridObjects())) {
                return;
            }
            BoardStatus boardStatus = new BoardStatus(true, true, true);
            messageSender.send(new MessageHolder(boardStatus));
            state.reset();
        } else if (state.inState(BoardReaderState.CHECK_FOR_HUMAN_MOVE)) {
            try {
                PossiblePiece[][] lastBoard = boardCalculator.getKnownBoard();
                int[] move = boardCalculator.detectPieces(gridObjects.getGridObjects(), requestedMove.getTurn(), lastBoard);
                if (move != null) {
                    state.reset();
                    ChessMove chessMove = new ChessMove(requestedMove.getTurn(), ChessUtil.convertToMove(move));
                    ChessMoveMsg chessMoveMsg = new ChessMoveMsg(requestedMove.getMoveId(), false, chessMove);
                    messageSender.send(new MessageHolder(chessMoveMsg));
                } else {
                    messageSender.send(new MessageHolder(new GameInformation(String.format("Still looking for %s move.", requestedMove.getTurn()))));
                    requestGridObjects();
                }
            } catch (Exception ex) {
//                logger.error(ex.getLocalizedMessage());
//                messageSender.send(new MessageHolder(new RequestImage(UUID.randomUUID())));
            }
        } else if (state.inState(BoardReaderState.CHECK_FOR_PIECE_POSITIONS)) {
            try {
                PiecePositions piecePositions = piecePositionsIdentifier.processImage(gridObjects.getGridObjects(), boardCalculator, requestPiecePositions);
                if (piecePositions == null) {
                    requestGridObjects();
                    return;
                }
                state.reset();
                messageSender.send(new MessageHolder(piecePositions));
            } catch (Exception ex) {
                java.util.logging.Logger.getLogger(BoardReader.class.getName()).log(Level.SEVERE, null, ex);
                requestGridObjects();
            }

        }
    }

    public void requestBoardRestPosition(SetBoardRestPosition boardRestPosition) {
        state.setState(BoardReaderState.CHECK_FOR_GAME_SETUP);
    }

    public void requestMove(RequestMove requestMove) throws Exception {
        if (!requestMove.isRobot()) {
            requestedMove = requestMove;
            state.setState(BoardReaderState.CHECK_FOR_HUMAN_MOVE);
            logger.debug(String.format("Set state to %s", state.toString()));
            requestGridObjects();
        }
    }

    private void requestGridObjects(){
            messageSender.send(new MessageHolder(new RequestGridObjects(boardProperties.getTopBoard(), boardProperties.getBottomBoard(),
                    boardProperties.getLeftBoard(), boardProperties.getRightBoard(), boardProperties.getvLines(), boardProperties.gethLines())));
    }
    
//    public void boardImage() {
//        BufferedImage bImageFromConvert = boardImage.getBi();
//
////        ArrayList<PossiblePiece> boardMarker = boardCalculator.detectBoardMarker(bImageFromConvert);
////        if (boardMarker.size() != 1 || boardMarker.get(0).y != 53) {
////            sendYBoardAdjust(boardMarker.get(0).y);
////            return;
////        }
//        //  logger.debug(String.format("Marker y=%s\n", boardMarker.get(0).y));
//        if (state.inState(BoardReaderState.CHECK_FOR_HUMAN_MOVE)) {
//            try {
//                PossiblePiece[][] lastBoard = boardCalculator.getKnownBoard();
//                int[] move = boardCalculator.detectPieces(bImageFromConvert, requestedMove.getTurn(), lastBoard);
//                if (move != null) {
//                    logger.debug("Found White move");
//                    state.reset();
//                    ChessMove chessMove = new ChessMove(requestedMove.getTurn(), ChessUtil.convertToMove(move));
//                    ChessMoveMsg chessMoveMsg = new ChessMoveMsg(requestedMove.getMoveId(), false, chessMove);
//                    messageSender.send(new MessageHolder(chessMoveMsg));
//                } else {
//                    messageSender.send(new MessageHolder(new RequestImage(UUID.randomUUID())));
//                }
//            } catch (Exception ex) {
//                logger.error(ex.getLocalizedMessage());
//                messageSender.send(new MessageHolder(new RequestImage(UUID.randomUUID())));
//            }
//        } else if (state.inState(BoardReaderState.CHECK_FOR_PIECE_POSITIONS)) {
//            try {
//                PiecePositions piecePositions = piecePositionsIdentifier.processImage(boardImage, boardCalculator, requestPiecePositions);
//                if (piecePositions == null) {
//                    messageSender.send(new MessageHolder(new RequestImage(UUID.randomUUID())));
//                    return;
//                }
//                state.reset();
//                messageSender.send(new MessageHolder(piecePositions));
//            } catch (Exception ex) {
//                java.util.logging.Logger.getLogger(BoardReader.class.getName()).log(Level.SEVERE, null, ex);
//                messageSender.send(new MessageHolder(new RequestImage(UUID.randomUUID())));
//            }
//
//        } else if (state.inState(BoardReaderState.SET_REST_POSITION)) {
//            List<DetectedObject> marker = boardCalculator.detectBoardMarker(bImageFromConvert);
//            sendYBoardAdjust(marker.get(0).getY());
//
//            state.setState(BoardReaderState.CHECK_FOR_GAME_SETUP);
//            messageSender.send(new MessageHolder(new RequestImage(UUID.randomUUID())));
//        }
//    }
    // Going to implement this, this would read the spot on board and adjust the y axis
//    private void sendYBoardAdjust(int currentY) {
//        double change = (double) (53 - currentY) * -0.48;
//        messageSender.send(new MessageHolder(new GCode("G91", "Relative Positioning")));
//        messageSender.send(new MessageHolder(new GCode(String.format("G1 Y%s", change), "Set to board position 200")));
//        messageSender.send(new MessageHolder(new GCode("G90", "Absolute Positioning")));
//    }
    public void requestSetupAndBoardStatus(RequestSetupAndBoardStatus requestBoardStatus) throws Exception {
        state.setState(BoardReaderState.SET_REST_POSITION);
        // This could get replaced by it adjusting the table position itself or a table adjust message
        messageSender.send(new MessageHolder(new GameInformation(String.format("Putting board in starting position."))));
        messageSender.send(new MessageHolder(new SetBoardRestPosition()));
    }

    public void startGame(StartChessGame startGame) {
        boardCalculator.setInitialized(false);
        state.reset();
    }

    public void requestPiecePositions(RequestPiecePositions requestPiecePositions) {
        state.setState(BoardReaderState.CHECK_FOR_PIECE_POSITIONS);
        this.requestPiecePositions = requestPiecePositions;
        requestGridObjects();
    }

    public void knownBoardPositions(KnownBoardPositions knownBoardPositions) {
        boardCalculator.setKnownBoard(knownBoardPositions.getKnownPostions());
    }

    public static void main(String[] arg) throws Exception {
        PropertyConfigurator.configure("D:\\git\\Chess\\src\\main\\java\\log4j.properties");
        BoardProperties bp = new BoardProperties();
        BoardReader boardReader = new BoardReader(new BoardReaderState(), new RMQChessMessageSender("boardReader", new RabbitConfiguration()), new RMQChessMessageReceiver("BoardReader", true),
                new BoardCalculator(bp, new RMQChessMessageSender("BoardReader", new RabbitConfiguration())), new PiecePositionsIdentifier(bp), new RMQChesssImageReceiver("boardReader"), new BoardProperties());
    }

}
