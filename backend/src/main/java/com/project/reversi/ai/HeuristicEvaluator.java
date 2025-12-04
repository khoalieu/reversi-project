package com.project.reversi.ai;

import com.project.reversi.models.Board;
import com.project.reversi.models.Piece;

public class HeuristicEvaluator {
    private static final int[][] POSITION_WEIGHTS = {
            {100, -20, 10, 5, 5, 10, -20, 100},
            {-20, -50, -2, -2, -2, -2, -50, -20},
            {10, -2, -1, -1, -1, -1, -2, 10},
            {5, -2, -1, -1, -1, -1, -2, 5},
            {5, -2, -1, -1, -1, -1, -2, 5},
            {10, -2, -1, -1, -1, -1, -2, 10},
            {-20, -50, -2, -2, -2, -2, -50, -20},
            {100, -20, 10, 5, 5, 10, -20, 100}
    };

    public int evaluate(Board board, Piece player) {
        return evalPosition(board, player);
    }


    private int evalPosition(Board board, Piece player) {
        int score = 0;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece piece = board.getPiece(r, c);
                if (piece == player) {
                    score += POSITION_WEIGHTS[r][c];
                } else if (piece != null) {
                    score -= POSITION_WEIGHTS[r][c];
                }
            }
        }
        return score;
    }

    private int evalGreedy(Board board, Piece player) {
        Piece opponent = (player == Piece.BLACK) ? Piece.WHITE : Piece.BLACK;
        return board.countPieces(player) - board.countPieces(opponent);
    }

    private int evalMobility(Board board, Piece player) {
        int MyMoves = board.getValidMoves(player).size();
        Piece opponent = (player == Piece.BLACK) ? Piece.WHITE : Piece.BLACK;
        int OpponentMoves = board.getValidMoves(opponent).size();
        if (MyMoves + OpponentMoves != 0) {
            return 100 * (MyMoves - OpponentMoves) / (MyMoves + OpponentMoves);
        }
        return 0;
    }

    private int evalStability(Board board, Piece player) {
        return 0;
    }
}