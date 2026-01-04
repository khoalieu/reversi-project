package com.project.reversi.ai;

import com.project.reversi.models.Board;
import com.project.reversi.models.Move;
import com.project.reversi.models.Piece;

public interface SearchAlgorithm {

    Move findBestMove(Board board, Piece player);

}