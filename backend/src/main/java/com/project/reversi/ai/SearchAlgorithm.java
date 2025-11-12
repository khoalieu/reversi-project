package com.project.reversi.ai;

import com.project.reversi.models.Board;
import com.project.reversi.models.Move;
import com.project.reversi.models.Piece;

public interface SearchAlgorithm {

    /**
     * Phương thức tìm nước đi tối ưu.
     * * @param board  Trạng thái bàn cờ hiện tại.
     * @param player Người chơi đang cần tìm nước đi (AI).
     * @return Move  Nước đi tốt nhất tìm được.
     */
    Move findBestMove(Board board, Piece player);

}