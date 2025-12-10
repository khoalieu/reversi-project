package com.project.reversi.game;

import com.project.reversi.ai.AlphaBetaPruning;
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
        // Cấu hình ai đi trước ở đây. Theo luật Othello, Black đi trước.
        this.currentPlayer = Piece.BLACK;
        this.ai = new AlphaBetaPruning();
    }

    // 1. Chỉ xử lý nước đi của người chơi (Human)
    public void play(Move playerMove) {
        // [SỬA LỖI QUAN TRỌNG]
        // Nếu hiện tại đang là lượt của AI, từ chối lệnh đánh của người chơi
        if (isAiTurn(currentPlayer)) {
            System.out.println("Lỗi: Người chơi cố gắng đánh trong lượt của AI!");
            return;
        }

        if (!board.isValidMove(playerMove, currentPlayer)) {
            System.out.println("Nước đi không hợp lệ!");
            return;
        }

        board.makeMove(playerMove, currentPlayer);

        switchTurn();
        checkPassTurn();
    }
    public void playAi() {
        if (board.isGameOver()) return;
        if (isAiTurn(currentPlayer)) {
            System.out.println("AI (" + currentPlayer + ") đang đi...");
            Move bestMove = ai.findBestMove(board, currentPlayer);
            if (bestMove != null) {
                board.makeMove(bestMove, currentPlayer);
            } else {
                System.out.println("AI không có nước đi hợp lệ.");
            }
            switchTurn();
            checkPassTurn();
        }
    }

    private void checkPassTurn() {
        // Nếu game chưa kết thúc mà người hiện tại không có nước đi -> Mất lượt -> Đổi lại
        if (!board.isGameOver() && board.getValidMoves(currentPlayer).isEmpty()) {
            System.out.println(currentPlayer + " không có nước đi. Mất lượt (Pass)!");
            switchTurn();
            if (board.getValidMoves(currentPlayer).isEmpty()) {
                System.out.println("Cả hai đều không có nước đi -> Kết thúc sớm.");
            }
        }
    }

    private boolean isAiTurn(Piece player) {
        return aiPlayer != null && player == aiPlayer;
    }

    private void switchTurn() {
        this.currentPlayer = (this.currentPlayer == Piece.BLACK) ? Piece.WHITE : Piece.BLACK;
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

        state.put("isAiTurn", isAiTurn(currentPlayer));

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