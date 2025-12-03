package com.project.reversi.ai;

import com.project.reversi.models.Board;
import com.project.reversi.models.Piece;

public class HeuristicEvaluator {
    public int evaluate(Board board, Piece player) {
        Piece opponent = (player == Piece.BLACK) ? Piece.WHITE : Piece.BLACK;

        int myCount = board.countPieces(player);

        int oppCount = board.countPieces(opponent);

        return myCount - oppCount;
    }
}