package com.project.reversi.ai;

import com.project.reversi.models.Board;
import com.project.reversi.models.Piece;

public class HeuristicEvaluator {
    // Bảng trọng số vị trí (Góc = 100, Cạnh góc X = -20...)
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
    
    // kiểm tra các hướng xung quanh
    private static final int[] DR = {-1, -1, -1, 0, 0, 1, 1, 1};
    private static final int[] DC = {-1, 0, 1, -1, 1, -1, 0, 1};

    public int evaluate(Board board, Piece player) {
        // Tính điểm theo các tiêu chí khác nhau
        int positionScore = evalPosition(board, player);
        int mobilityScore = evalMobility(board, player);
        int frontierScore = evalFrontier(board, player);
        int discScore = evalPieceDifference(board, player);
        int totalPieces = board.countPieces(Piece.BLACK) + board.countPieces(Piece.WHITE);
        int parityScore = evalParity(board);
        int wPos, wMob, wFront, wDisc, wParity;

        if (totalPieces <= 20) {
            // Khai cuộc: Tập trung vị trí & cơ động, hạn chế ăn quân
            wPos = 10;
            wMob = 5;
            wFront = 2;
            wDisc = -2; // Âm để khuyến khích ít quân ("Evaporation Strategy")
            wParity = 0;
        } else if (totalPieces <= 45) {
            // Trung cuộc: Cân bằng, chú trọng cấu trúc biên
            wPos = 8;
            wMob = 5;
            wFront = 4;
            wDisc = 2;
            wParity = 2; // Bắt đầu để ý chẵn lẻ một chút

        } else {
            // Tàn cuộc: Ăn quân là tất cả
            wPos = 2;
            wMob = 1;
            wFront = 1;
            wDisc = 20; // Đẩy lên cực cao để AI tham lam nhất có thể
            wParity = 10;

        }

        // 4. Tổng hợp
        return (wPos * positionScore)
                + (wMob * mobilityScore)
                + (wFront * frontierScore)
                + (wDisc * discScore)
                + (wParity * parityScore);
    }

    private int evalPosition(Board board, Piece player) {
        int score = 0;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece piece = board.getPiece(r, c);
                if (piece == player) {
                    score += POSITION_WEIGHTS[r][c];
                } else if (piece != null && piece != Piece.EMPTY) {
                    score -= POSITION_WEIGHTS[r][c];
                }
            }
        }
        return score;
    }  
    // Tính chênh lệch số lượng nước đi (Mobility)
    private int evalMobility(Board board, Piece player) {
        int myMoves = board.getValidMoves(player).size();
        Piece opponent = (player == Piece.BLACK) ? Piece.WHITE : Piece.BLACK;
        int opponentMoves = board.getValidMoves(opponent).size();
        
        if (myMoves + opponentMoves != 0) {
            return 100 * (myMoves - opponentMoves) / (myMoves + opponentMoves);
        }
        return 0;
    }
    // Tính điểm quân biên
    // Quân biên là quân nằm cạnh ô trống -> Dễ bị lật -> Càng ít càng tốt
    private int evalFrontier(Board board, Piece player) {
        int myFrontier = 0;
        int oppFrontier = 0;
        Piece opponent = (player == Piece.BLACK) ? Piece.WHITE : Piece.BLACK;

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece piece = board.getPiece(r, c);
                if (piece != Piece.EMPTY) {
                    boolean isFrontier = false;
                    for (int i = 0; i < 8; i++) {
                        int nr = r + DR[i];
                        int nc = c + DC[i];
                        if (nr >= 0 && nr < 8 && nc >= 0 && nc < 8 && board.getPiece(nr, nc) == Piece.EMPTY) {
                            isFrontier = true; break;
                        }
                    }
                    if (isFrontier) {
                        if (piece == player) myFrontier++;
                        else oppFrontier++;
                    }
                }
            }
        }
        if (myFrontier + oppFrontier != 0) {
            return 100 * (oppFrontier - myFrontier) / (myFrontier + oppFrontier);
        }
        return 0;
    }
    private int evalPieceDifference(Board board, Piece player) {
        Piece opponent = (player == Piece.BLACK) ? Piece.WHITE : Piece.BLACK;
        int myPieces = board.countPieces(player);
        int oppPieces = board.countPieces(opponent);

        if (myPieces + oppPieces != 0) {
            return 100 * (myPieces - oppPieces) / (myPieces + oppPieces);
        }
        return 0;
    }
    // 5. Chiến lược Chẵn Lẻ (Parity Strategy)

    private int evalParity(Board board) {
        int[][] quadrants = {
                {0, 0, 3, 3}, // StartRow, StartCol, EndRow, EndCol
                {0, 4, 3, 7},
                {4, 0, 7, 3},
                {4, 4, 7, 7}
        };
        int parityBonus = 0;
        for (int[] q : quadrants) {
            int emptyCount = 0;
            for (int r = q[0]; r <= q[2]; r++) {
                for (int c = q[1]; c <= q[3]; c++) {
                    if (board.getPiece(r, c) == Piece.EMPTY) {
                        emptyCount++;
                    }
                }
            }
            if (emptyCount % 2 != 0) {
                parityBonus += 1;
            }
        }
        return parityBonus * 25;
    }
}