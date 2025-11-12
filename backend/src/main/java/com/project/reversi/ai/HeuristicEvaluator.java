package com.project.reversi.ai;

import com.project.reversi.models.Board;
import com.project.reversi.models.Piece;

public class HeuristicEvaluator {

    // Hàm đánh giá chính: Trả về điểm số (càng cao càng tốt cho player)
    public int evaluate(Board board, Piece player) {
        // TODO: Tổng hợp điểm từ Vị trí, Cơ động, Ổn định...
        return 0;
    }

    // Các hàm phụ có thể thêm sau (private)
    // private int evalMobility(...) {}
    // private int evalCorners(...) {}
}