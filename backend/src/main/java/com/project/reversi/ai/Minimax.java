package com.project.reversi.ai;

import com.project.reversi.models.Board;
import com.project.reversi.models.Move;
import com.project.reversi.models.Piece;

import java.util.List;

public class Minimax implements SearchAlgorithm {

    private HeuristicEvaluator evaluator;
    private int maxDepth;

    public Minimax() {
        this.evaluator = new HeuristicEvaluator();
        this.maxDepth = 4;
    }

    @Override
    public Move findBestMove(Board board, Piece player) {
        int bestValue = Integer.MIN_VALUE;
        Move bestMove = null;

        List<Move> moves = board.getValidMoves(player);

        if (moves.isEmpty()) return null;

        for (Move m : moves) {
            Board newBoard = new Board(board.getGrid());

            newBoard.makeMove(m, player);

            int moveValue = minimax(newBoard, maxDepth - 1, false, player);

            if (moveValue > bestValue) {
                bestValue = moveValue;
                bestMove = m;
            }
        }
        return bestMove;
    }

    private int minimax(Board board, int depth, boolean isMax, Piece aiPlayer) {
        if (depth == 0 || board.isGameOver()) {
            return evaluator.evaluate(board, aiPlayer);
        }

        Piece opponent = (aiPlayer == Piece.BLACK) ? Piece.WHITE : Piece.BLACK;
        Piece currentPlayer = isMax ? aiPlayer : opponent;

        List<Move> validMoves = board.getValidMoves(currentPlayer);

        if (validMoves.isEmpty()) {
            return minimax(board, depth - 1, !isMax, aiPlayer);
        }

        if (isMax) {
            int maxEval = Integer.MIN_VALUE;
            for (Move m : validMoves) {
                Board newBoard = new Board(board.getGrid());
                newBoard.makeMove(m, currentPlayer);

                int eval = minimax(newBoard, depth - 1, false, aiPlayer);
                maxEval = Math.max(maxEval, eval);
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Move m : validMoves) {
                Board newBoard = new Board(board.getGrid());
                newBoard.makeMove(m, currentPlayer);

                int eval = minimax(newBoard, depth - 1, true, aiPlayer);
                minEval = Math.min(minEval, eval);
            }
            return minEval;
        }
    }
}