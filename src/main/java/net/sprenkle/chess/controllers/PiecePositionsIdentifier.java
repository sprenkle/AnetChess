/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess.controllers;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import net.sprenkle.chess.BoardProperties;
import net.sprenkle.chess.BoardReader;
import net.sprenkle.chess.ChessUtil;
import net.sprenkle.chess.PieceMove;
import net.sprenkle.chess.models.PossiblePiece;
import net.sprenkle.chess.imaging.BoardCalculator;
import net.sprenkle.chess.messages.BoardImage;
import net.sprenkle.chess.messages.PiecePositions;
import net.sprenkle.chess.messages.RequestPiecePositions;
import net.sprenkle.chess.models.GridObject;
import org.apache.log4j.Logger;

/**
 *
 * @author david
 */
public class PiecePositionsIdentifier {

    private final double xSlope = -0.43132853294113493;
    private final double ySlope = 0.4266753267702424;
    private final double xIntercept = 271.5168316065272;
    private final double yIntercept = 1.4780612193119822;
    private final double orgX = 95.4;
    private final double orgY = 193;
    private int captured = 0;
    private final double high = 95;
    private final double mid = 54;
    private final double pawnPickup;
    private final double knightPickup;
    private final double bishopPickup;
    private final double rookPickup;
    private final double queenPickup;
    private final double kingPickup;

    static Logger logger = Logger.getLogger(BoardReader.class.getSimpleName());

    public PiecePositionsIdentifier(BoardProperties boardProperties) {
        pawnPickup = boardProperties.getPawnHeight();
        knightPickup = boardProperties.getKnightHeight();
        bishopPickup = boardProperties.getBishopHeight();
        rookPickup = boardProperties.getRookHeight();
        queenPickup = boardProperties.getQueenHeight();
        kingPickup = boardProperties.getKingHeight();
    }

    public PiecePositions processImage(List<GridObject> gridObjects, BoardCalculator boardCalculator, RequestPiecePositions requestPiecePositions) throws Exception {
        PossiblePiece[][] lastBoard = boardCalculator.getKnownBoard(); // needs to be before detect piece
        if(!boardCalculator.verifyPiecePositions(gridObjects, lastBoard)) return null;
        
        //boardCalculator.detectPieces(gridObjects, requestPiecePositions.getChessMove().getTurn(), lastBoard);
        
        
        logger.debug(String.format("received %s", requestPiecePositions.getChessMove().getMove()));
        int[] moves = ChessUtil.convertFromMove(requestPiecePositions.getChessMove().getMove());
        logger.debug(String.format("Converted to %s,%s  %s,%s", moves[0], moves[1], moves[2], moves[3]));
        PossiblePiece fromPiece = lastBoard[moves[0]][moves[1]];
        PossiblePiece toPiece = lastBoard[moves[2]][moves[3]];
        logger.debug(String.format("fromPiece x=%s, y=%s row=%s col=%s", fromPiece.x, fromPiece.y, fromPiece.row, fromPiece.col));
        double[] from = new double[2];
        logger.info(String.format("from Piece image x=%s, y=%s", from[0], from[1]));
        from = calculateBoardPosition(moves[0], moves[1], lastBoard[fromPiece.col][fromPiece.row]);
//        from = calculateBoardPosition(moves[0], moves[1]);
        double[] to = calculateBoardPosition(moves[2], moves[3]);
        List<PieceMove> moveList = new ArrayList<>();

        // Check for a castle
        if (requestPiecePositions.getCastle()) {
            logger.info(String.format("Castling"));
            int rookFromCol = moves[2] == 1 ? 0 : 7;
            int rookToCol = moves[2] == 1 ? 2 : 5;

            double[] fromCastle = calculateBoardPosition(rookFromCol, moves[1]);
            double[] toCastle = calculateBoardPosition(rookToCol, moves[1]);

            moveList.add(new PieceMove(fromCastle, toCastle, getPiecePickupHeight(PossiblePiece.ROOK), false));
        } else if (lastBoard[moves[2]][moves[3]] != null) {
            double[] capture = new double[2];
            capture[0] = -25;
            capture[1] = captured++ * 24 + 10;
            logger.info(String.format("Capture Piece x=%s, y=%s", capture[0], capture[1]));
            moveList.add(new PieceMove(to, capture, getPiecePickupHeight(toPiece.objectId), false));
        } else {
            logger.info(String.format("No capture"));
        }

        moveList.add(new PieceMove(from, to, getPiecePickupHeight(fromPiece.objectId), false));

        PiecePositions piecePositions = new PiecePositions(moveList, mid, high, requestPiecePositions.getUuid());
        return piecePositions;
    }

    public double[] calculateBoardPosition(int x, int y, PossiblePiece piece) {
        double[] pos = calculateBoardPosition(x, y);

        pos[0] +=  piece.xOffFactor * 0.4308;
        pos[1] -=  piece.yOffFactor * 0.4308;
        
        return pos;
    }

    public double[] calculateBoardPosition(int x, int y) {
        double[] pos = new double[2];

        pos[0] = orgX + ((3 - x) * 24) + 12;
        pos[1] = orgY - ((7 - y) * 24 + 12);

        return pos;
    }

    
    public double getPiecePickupHeight(int rank) {
        switch (rank) {
            case 0:
                return pawnPickup; // pawn
            case 1:
                return knightPickup; // knight
            case 2:
                return bishopPickup; // bishop 
            case 3:
                return rookPickup; // rook
            case 4:
                return queenPickup; // queen
            case 5:
                return kingPickup; // king 
        }
        return 100;
    }
    
    
    public double[] getPieceOffset(PossiblePiece piece){
        return null;
    }
}
