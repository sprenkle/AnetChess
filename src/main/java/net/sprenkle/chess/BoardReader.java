/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess;

import java.awt.image.BufferedImage;
import java.util.logging.Level;
import net.sprenkle.chess.controllers.PiecePositionsIdentifier;
import net.sprenkle.chess.imaging.BoardCalculator;
import net.sprenkle.chess.messages.BoardAtRest;
import net.sprenkle.chess.messages.BoardImage;
import net.sprenkle.chess.messages.BoardStatus;
import net.sprenkle.chess.messages.ChessMessageReceiver;
import net.sprenkle.chess.messages.MessageHandler;
import net.sprenkle.chess.messages.MqChessMessageSender;
import net.sprenkle.chess.messages.RequestBoardStatus;
import net.sprenkle.chess.messages.RequestMove;
import net.sprenkle.chess.messages.SetBoardRestPosition;
import net.sprenkle.chess.messages.ChessMoveMsg;
import net.sprenkle.chess.messages.KnownBoardPositions;
import net.sprenkle.chess.messages.PiecePositions;
import net.sprenkle.chess.messages.RequestPiecePositions;
import net.sprenkle.chess.messages.StartGame;
import net.sprenkle.messages.MessageHolder;
import net.sprenkle.messages.images.RequestImage;
import net.sprenkle.chess.messages.ChessMove;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author david
 */
public class BoardReader {
    static Logger logger = Logger.getLogger(BoardReader.class.getSimpleName());
    private final MqChessMessageSender messageSender;
    private final BoardCalculator boardCalculator;
    private final PiecePositionsIdentifier piecePositionsIdentifier;
    private final BoardReaderState state;
    private RequestMove requestedMove;
    private RequestPiecePositions requestPiecePositions;

    public BoardReader(BoardReaderState state, MqChessMessageSender messageSender, 
            ChessMessageReceiver messageReceiver, BoardCalculator boardCalculator,
            PiecePositionsIdentifier piecePositionsIdentifier) throws Exception {
        this.messageSender = messageSender;
        this.boardCalculator = boardCalculator;
        this.piecePositionsIdentifier = piecePositionsIdentifier;
        this.state = state;
        
        messageReceiver.addMessageHandler(RequestMove.class.getSimpleName(), new MessageHandler<RequestMove>() {
            @Override
            public void handleMessage(RequestMove requestMove) {
                try {
                    requestMove(requestMove);
                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(BoardReader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        messageReceiver.addMessageHandler(RequestBoardStatus.class.getSimpleName(), new MessageHandler<RequestBoardStatus>() {
            @Override
            public void handleMessage(RequestBoardStatus requestBoardStatus) {
                try {
                    requestBoardStatus(requestBoardStatus);
                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(BoardReader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        messageReceiver.addMessageHandler(BoardImage.class.getSimpleName(), new MessageHandler<BoardImage>() {
            @Override
            public void handleMessage(BoardImage boardImage) {
                try {
                    boardImage(boardImage);
                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(BoardReader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        messageReceiver.addMessageHandler(BoardAtRest.class.getSimpleName(), new MessageHandler<BoardAtRest>() {
            @Override
            public void handleMessage(BoardAtRest boardAtRest) {
                try {
                    boardAtRest(boardAtRest);
                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(BoardReader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        messageReceiver.addMessageHandler(StartGame.class.getSimpleName(), new MessageHandler<StartGame>() {
            @Override
            public void handleMessage(StartGame startGame) {
                try {
                    startGame(startGame);
                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(BoardReader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        messageReceiver.addMessageHandler(RequestPiecePositions.class.getSimpleName(), new MessageHandler<RequestPiecePositions>() {
            @Override
            public void handleMessage(RequestPiecePositions requestPiecePositions) {
                try {
                    requestPiecePositions(requestPiecePositions);
                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(BoardReader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        messageReceiver.addMessageHandler(KnownBoardPositions.class.getSimpleName(), new MessageHandler<KnownBoardPositions>() {
            @Override
            public void handleMessage(KnownBoardPositions knownBoardPositions) {
                try {
                    knownBoardPositions(knownBoardPositions);
                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(BoardReader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        messageReceiver.initialize();
        state.reset();
    }

    public static void main(String[] arg) throws Exception {
        PropertyConfigurator.configure("D:\\git\\Chess\\src\\main\\java\\log4j.properties");
        BoardProperties bp = new BoardProperties();
        BoardReader boardReader = new BoardReader(new BoardReaderState(), new MqChessMessageSender("boardReader"), new ChessMessageReceiver("BoardReader", true), 
                new BoardCalculator(bp), new PiecePositionsIdentifier(bp));
    }

    public void requestMove(RequestMove requestMove) throws Exception {
        if (!requestMove.isRobot()) {
            requestedMove = requestMove;
            state.setHumanMove();
            logger.debug(String.format("Set state to %s", state.toString()));
            messageSender.send(new MessageHolder(RequestImage.class.getSimpleName(), new RequestImage()));
        }
    }

    public void boardImage(BoardImage boardImage) {
        BufferedImage bImageFromConvert = boardImage.GetBi();
        //logger.debug("Received Image");
        if (state.inState(BoardReaderState.CHECK_FOR_GAME_SETUP)) {
            boardCalculator.setInitialized(false);
            boardCalculator.initialLines(bImageFromConvert);
            if (boardCalculator.isInitialized()) {
                BoardStatus boardStatus = new BoardStatus(true, true, true);
                logger.debug(String.format("Sent %s", boardStatus.toString()));
                messageSender.send(new MessageHolder(BoardStatus.class.getSimpleName(), boardStatus));
                state.reset();
            } else {
                messageSender.send(new MessageHolder(RequestImage.class.getSimpleName(), new RequestImage()));
            }
        } else if (state.inState(BoardReaderState.CHECK_FOR_HUMAN_MOVE)) {
            try {
                PossiblePiece[][] lastBoard = boardCalculator.getKnownBoard(); 
                int[] move = boardCalculator.detectPieces(bImageFromConvert, requestedMove.getTurn(),lastBoard);
                if (move != null) {
                    logger.debug("Found White move");
                    state.reset();
                    ChessMove chessMove = new ChessMove(requestedMove.getTurn(), ChessUtil.convertToMove(move));
                    ChessMoveMsg chessMoveMsg = new ChessMoveMsg(requestedMove.getMoveId(), false, chessMove);
                    messageSender.send(new MessageHolder(ChessMoveMsg.class.getSimpleName(), chessMoveMsg));
                } else {
                    messageSender.send(new MessageHolder(RequestImage.class.getSimpleName(), new RequestImage()));
                }
            } catch (Exception ex) {
                logger.error(ex.getLocalizedMessage());
                messageSender.send(new MessageHolder(RequestImage.class.getSimpleName(), new RequestImage()));
            }
        } else if (state.inState(BoardReaderState.CHECK_FOR_PIECE_POSITIONS)) {
            try {
                PiecePositions piecePositions = piecePositionsIdentifier.processImage(boardImage, boardCalculator, requestPiecePositions);
                state.reset();
                messageSender.send(new MessageHolder(PiecePositions.class.getSimpleName(), piecePositions));
            } catch (Exception ex) {
                java.util.logging.Logger.getLogger(BoardReader.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    public void boardAtRest(BoardAtRest boardAtRest) {
        if (state.inState(BoardReaderState.CHECK_FOR_REST_POSITION) && boardAtRest.IsAtRest()) {
            state.setGameSetup();
            messageSender.send(new MessageHolder(RequestImage.class.getSimpleName(), new RequestImage()));
        }
    }

    public void requestBoardStatus(RequestBoardStatus requestBoardStatus) throws Exception {
        state.setRestPositino();
        messageSender.send(new MessageHolder(SetBoardRestPosition.class.getSimpleName(), new SetBoardRestPosition()));
    }

    public void startGame(StartGame startGame) {
        boardCalculator.setInitialized(false);
        state.reset();
    }

    public void requestPiecePositions(RequestPiecePositions requestPiecePositions) {
        state.setPiecePosition();
        this.requestPiecePositions = requestPiecePositions;
        messageSender.send(new MessageHolder(RequestImage.class.getSimpleName(), new RequestImage()));
    }

    public void knownBoardPositions(KnownBoardPositions knownBoardPositions) {
        boardCalculator.setKnownBoard(knownBoardPositions.getKnownPostions());
    }

}
