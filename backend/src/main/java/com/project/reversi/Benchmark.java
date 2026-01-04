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

    private static final int ITERATIONS = 5;

    public static void main(String[] args) {
        System.out.println("======================================================================");
        System.out.println("   BENCHMARK: MINIMAX vs ALPHA-BETA (Average of " + ITERATIONS + " runs)   ");
        System.out.println("======================================================================");
        System.out.printf("%-8s %-15s %-20s %-15s\n", "Depth", "Algorithm", "Avg Time (ms)", "Avg Memory (KB)");
        System.out.println("----------------------------------------------------------------------");

        for (int depth = 1; depth <= 6; depth++) {
            runAverageTest("Minimax", new Minimax(), depth);
            runAverageTest("AlphaBeta", new AlphaBetaPruning(), depth);
            System.out.println("----------------------------------------------------------------------");
        }
    }

    private static void runAverageTest(String algoName, SearchAlgorithm ai, int depth) {
        if (ai instanceof Minimax) {
            ((Minimax) ai).setMaxDepth(depth);
        } else if (ai instanceof AlphaBetaPruning) {
            ((AlphaBetaPruning) ai).setMaxDepth(depth);
        }

        double totalTime = 0;
        double totalMemory = 0;

        for (int i = 0; i < ITERATIONS; i++) {
            Board board = createTestBoard();

            Runtime runtime = Runtime.getRuntime();
            runtime.gc();
            long startMem = runtime.totalMemory() - runtime.freeMemory();

            long startT = System.nanoTime();

            ai.findBestMove(board, Piece.BLACK);

            long endT = System.nanoTime();
            long endMem = runtime.totalMemory() - runtime.freeMemory();

            totalTime += (endT - startT) / 1_000_000.0; // ms
            totalMemory += Math.max(0, endMem - startMem) / 1024.0; // KB
        }

        double avgTime = totalTime / ITERATIONS;
        double avgMemory = totalMemory / ITERATIONS;

        System.out.printf("%-8d %-15s %-20.4f %-15.4f\n", depth, algoName, avgTime, avgMemory);
    }

    private static Board createTestBoard() {
        Board board = new Board();
        Piece current = Piece.BLACK;
        Random rand = new Random();

        int movesToPlay = 8;

        for(int i=0; i<movesToPlay; i++) {
            List<Move> moves = board.getValidMoves(current);
            if(!moves.isEmpty()) {
                Move randomMove = moves.get(rand.nextInt(moves.size()));
                board.makeMove(randomMove, current);
            }
            current = (current == Piece.BLACK) ? Piece.WHITE : Piece.BLACK;
        }
        return board;
    }
}