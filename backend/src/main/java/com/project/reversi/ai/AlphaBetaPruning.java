package com.project.reversi.ai;

import com.project.reversi.models.Board;
import com.project.reversi.models.Move;
import com.project.reversi.models.Piece;
import java.util.List;

public class AlphaBetaPruning implements SearchAlgorithm {

    private HeuristicEvaluator evaluator;
    private int maxDepth;

    public AlphaBetaPruning() {
        this.evaluator = new HeuristicEvaluator();
        //thay đổi độ sâu tìm kiếm
        this.maxDepth = 6;
    }

    @Override
    public Move findBestMove(Board board, Piece player) {
        List<Move> validMoves = board.getValidMoves(player);
        if (validMoves.isEmpty()) return null;

        Move bestMove = null;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        int bestValue = Integer.MIN_VALUE;

        for (Move move : validMoves) {
            Board newBoard = new Board(board.getGrid());
            newBoard.makeMove(move, player);
            // Gọi đệ quy: đến lượt đối thủ (isMax = false)
            int value = alphaBeta(newBoard, maxDepth - 1, alpha, beta, false, player);

            if (value > bestValue) {
                bestValue = value;
                bestMove = move;
            }
            // Cập nhật Alpha cho node MAX (gốc)
            alpha = Math.max(alpha, bestValue);
        }
        return bestMove;
    }

    private int alphaBeta(Board board, int depth, int alpha, int beta, boolean isMax, Piece player) {
        if (depth == 0 || board.isGameOver()) {
            return evaluator.evaluate(board, player);
        }

        Piece opponent = (player == Piece.BLACK) ? Piece.WHITE : Piece.BLACK;
        Piece currentPlayer = isMax ? player : opponent;
        List<Move> moves = board.getValidMoves(currentPlayer);

        // Xử lý mất lượt (Pass): vẫn tiếp tục tìm kiếm ở độ sâu tiếp theo
        if (moves.isEmpty()) {
            return alphaBeta(board, depth - 1, alpha, beta, !isMax, player);
        }

        if (isMax) {
            int maxEval = Integer.MIN_VALUE;
            for (Move move : moves) {
                Board newBoard = new Board(board.getGrid());
                newBoard.makeMove(move, currentPlayer);
                
                int eval = alphaBeta(newBoard, depth - 1, alpha, beta, false, player);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                
                // Cắt tỉa Beta
                if (beta <= alpha) break; 
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Move move : moves) {
                Board newBoard = new Board(board.getGrid());
                newBoard.makeMove(move, currentPlayer);
                
                int eval = alphaBeta(newBoard, depth - 1, alpha, beta, true, player);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                
                // Cắt tỉa Alpha
                if (beta <= alpha) break;
            }
            return minEval;
        }
    }
    public void setMaxDepth(int depth) {
        this.maxDepth = depth;
    }
}