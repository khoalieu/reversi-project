package com.project.reversi.ai;

import com.project.reversi.models.Board;
import com.project.reversi.models.Move;
import com.project.reversi.models.Piece;

public class AlphaBetaPruning implements SearchAlgorithm {

    private HeuristicEvaluator evaluator;
    private int maxDepth; // Độ sâu tìm kiếm

    public AlphaBetaPruning() {
        this.evaluator = new HeuristicEvaluator();
        this.maxDepth = 6; // Ví dụ độ sâu mặc định
    }

    @Override
    public Move findBestMove(Board board, Piece player) {
        // TODO: Gọi hàm đệ quy minimax với alpha-beta
        return null;
    }

    // Hàm đệ quy (Helper method)
    // private int alphaBeta(...) { ... }
}