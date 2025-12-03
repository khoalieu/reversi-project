package com.project.reversi.ai;

import com.project.reversi.models.Board;
import com.project.reversi.models.Move;
import com.project.reversi.models.Piece;

public class AlphaBetaPruning implements SearchAlgorithm {

    private HeuristicEvaluator evaluator;
    private int maxDepth; // Độ sâu tìm kiếm

    public AlphaBetaPruning() {
        this.evaluator = new HeuristicEvaluator();
        this.maxDepth = 6;
    }


    @Override
    public Move findBestMove(Board board, Piece player) {
        return null;
    }
}