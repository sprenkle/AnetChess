/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sprenkle.chess;

import dagger.Module;
import dagger.Provides;
import net.sprenkle.chess.messages.ChessMessageReceiver;
import net.sprenkle.chess.messages.ChessMessageSender;
import net.sprenkle.chess.messages.MqChessMessageSender;

/**
 *
 * @author david
 */
//@Module(library=true)
@Module(
        injects = ChessMessageReceiver.class
)
public class ChessModule {

    @Provides
    ChessMessageSender providesChessMessageSender() {
        return new MqChessMessageSender();
    }

    @Provides
    UCIInterface providesUCIInterface() {
        return new StockFishUCI();
    }

    @Provides
    ChessControllerInterface providesStartGameSender(UCIInterface uciInterface) {
        return new ChessController();
    }

    @Provides
    ChessInterface providesChess(ChessControllerInterface chessEngine, ChessMessageSender chessMessageSender) {
        return new Chess(chessEngine, new ChessState(), chessMessageSender);
    }

    @Provides
    ChessMessageReceiver providesChessMessageReceiver(ChessInterface chessInterface) {
        return new ChessMessageReceiver(chessInterface);
    }

}
