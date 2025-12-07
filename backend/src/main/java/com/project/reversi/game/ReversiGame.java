package com.project.reversi.game;

import com.project.reversi.ai.AlphaBetaPruning;
import com.project.reversi.ai.Minimax;
import com.project.reversi.ai.SearchAlgorithm;
import com.project.reversi.models.Board;
import com.project.reversi.models.Move;
import com.project.reversi.models.Piece;

import java.util.HashMap;
import java.util.Map;

public class ReversiGame {
    private Board board;
    private Piece currentPlayer;
    private SearchAlgorithm ai;

    private Piece aiPlayer = Piece.BLACK;

    public ReversiGame() {
        this.board = new Board();
        this.currentPlayer = Piece.WHITE;
        this.ai = new Minimax();
    }


    public void play(Move playerMove) {
        if (!board.isValidMove(playerMove, currentPlayer)) {
            System.out.println("Nước đi không hợp lệ!");
            return;
        }

        board.makeMove(playerMove, currentPlayer);

        processNextTurns();
    }

    private void processNextTurns() {
        boolean processing = true;

        while (processing) {
            if (board.isGameOver()) {
                System.out.println("Game Over!");
                break;
            }

            switchTurn();

            if (board.getValidMoves(currentPlayer).isEmpty()) {
                System.out.println(currentPlayer + " không có nước đi. Mất lượt (Pass)!");
                continue;
            }

            if (isAiTurn(currentPlayer)) {
                System.out.println("AI (" + currentPlayer + ") đang đi...");

                Move bestMove = ai.findBestMove(board, currentPlayer);
                if (bestMove != null) {
                    board.makeMove(bestMove, currentPlayer);
                }
            } else {
                processing = false;
            }
        }
    }

    private boolean isAiTurn(Piece player) {
        return aiPlayer != null && player == aiPlayer;
    }

    private void switchTurn() {
        if (this.currentPlayer == Piece.BLACK) {
            this.currentPlayer = Piece.WHITE;
        } else {
            this.currentPlayer = Piece.BLACK;
        }
    }

    public Map<String, Object> getGameState() {
        Map<String, Object> state = new HashMap<>();
        state.put("board", board.getGrid());
        state.put("currentPlayer", currentPlayer);

        Map<String, Integer> scores = new HashMap<>();
        scores.put("BLACK", board.countPieces(Piece.BLACK));
        scores.put("WHITE", board.countPieces(Piece.WHITE));
        state.put("scores", scores);

        state.put("validMoves", board.getValidMoves(currentPlayer));

        boolean isOver = board.isGameOver();
        state.put("gameOver", isOver);
        if (isOver) {
            int blackScore = scores.get("BLACK");
            int whiteScore = scores.get("WHITE");
            if (blackScore > whiteScore) state.put("winner", "BLACK");
            else if (whiteScore > blackScore) state.put("winner", "WHITE");
            else state.put("winner", "DRAW");
        }
        return state;
    }
}