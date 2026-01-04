package com.project.reversi;

import com.project.reversi.ai.AlphaBetaPruning;
import com.project.reversi.ai.Minimax;
import com.project.reversi.ai.SearchAlgorithm;
import com.project.reversi.models.Board;
import com.project.reversi.models.Move;
import com.project.reversi.models.Piece;

import java.util.List;
import java.util.Random;

public class Benchmark {

    // Số lần chạy lặp lại để lấy trung bình
    private static final int ITERATIONS = 5;

    public static void main(String[] args) {
        System.out.println("======================================================================");
        System.out.println("   BENCHMARK: MINIMAX vs ALPHA-BETA (Average of " + ITERATIONS + " runs)   ");
        System.out.println("======================================================================");
        System.out.printf("%-8s %-15s %-20s %-15s\n", "Depth", "Algorithm", "Avg Time (ms)", "Avg Memory (KB)");
        System.out.println("----------------------------------------------------------------------");

        // Chạy test từ độ sâu 1 đến 6
        for (int depth = 1; depth <= 6; depth++) {
            runAverageTest("Minimax", new Minimax(), depth);
            runAverageTest("AlphaBeta", new AlphaBetaPruning(), depth);
            System.out.println("----------------------------------------------------------------------");
        }
    }

    private static void runAverageTest(String algoName, SearchAlgorithm ai, int depth) {
        // Cấu hình độ sâu
        if (ai instanceof Minimax) {
            ((Minimax) ai).setMaxDepth(depth);
        } else if (ai instanceof AlphaBetaPruning) {
            ((AlphaBetaPruning) ai).setMaxDepth(depth);
        }

        double totalTime = 0;
        double totalMemory = 0;

        for (int i = 0; i < ITERATIONS; i++) {
            // 1. Tạo bàn cờ mẫu (có chút ngẫu nhiên để tránh Cache)
            Board board = createTestBoard();

            // 2. Chuẩn bị đo bộ nhớ
            Runtime runtime = Runtime.getRuntime();
            runtime.gc(); // Dọn rác
            long startMem = runtime.totalMemory() - runtime.freeMemory();

            // 3. Chuẩn bị đo thời gian
            long startT = System.nanoTime();

            // 4. CHẠY THUẬT TOÁN
            ai.findBestMove(board, Piece.BLACK);

            // 5. Kết thúc đo
            long endT = System.nanoTime();
            long endMem = runtime.totalMemory() - runtime.freeMemory();

            // Cộng dồn
            totalTime += (endT - startT) / 1_000_000.0; // ms
            totalMemory += Math.max(0, endMem - startMem) / 1024.0; // KB
        }

        // Tính trung bình
        double avgTime = totalTime / ITERATIONS;
        double avgMemory = totalMemory / ITERATIONS;

        // In kết quả
        System.out.printf("%-8d %-15s %-20.4f %-15.4f\n", depth, algoName, avgTime, avgMemory);
    }

    // Tạo một bàn cờ mẫu ở giai đoạn giữa game (khoảng 10-15 nước đi ngẫu nhiên)
    // Để tạo độ phức tạp thực tế
    private static Board createTestBoard() {
        Board board = new Board();
        Piece current = Piece.BLACK;
        Random rand = new Random();

        // Đi ngẫu nhiên khoảng 8 nước để vào Midgame
        int movesToPlay = 8;

        for(int i=0; i<movesToPlay; i++) {
            List<Move> moves = board.getValidMoves(current);
            if(!moves.isEmpty()) {
                // Chọn nước đi ngẫu nhiên
                Move randomMove = moves.get(rand.nextInt(moves.size()));
                board.makeMove(randomMove, current);
            }
            // Đổi lượt
            current = (current == Piece.BLACK) ? Piece.WHITE : Piece.BLACK;
        }
        return board;
    }
}