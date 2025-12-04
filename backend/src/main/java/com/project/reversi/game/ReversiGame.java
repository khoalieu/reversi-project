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

    // Cấu hình: Phe nào là AI? (Để null nếu chơi 2 người)
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

            // Đổi sang phe tiếp theo
            switchTurn();

            // --- KIỂM TRA: Phe này có nước đi không? ---
            if (board.getValidMoves(currentPlayer).isEmpty()) {
                System.out.println(currentPlayer + " không có nước đi. Mất lượt (Pass)!");

                // Nếu phe này không đi được, vòng lặp sẽ chạy tiếp (continue)
                // -> Lại gọi switchTurn() ở đầu vòng lặp -> Trả lượt về cho phe kia.
                // (Logic này đúng cho cả 2 Người hoặc Người vs Máy)

                // *Check phụ*: Nếu đổi lại mà phe kia cũng tịt ngòi nốt -> Game Over
                // (Sẽ được bắt ở đầu vòng lặp tiếp theo nhờ hàm board.isGameOver)
                continue;
            }

            // --- KIỂM TRA: Phe này là AI hay Người? ---
            if (isAiTurn(currentPlayer)) {
                // *** LƯỢT CỦA AI ***
                System.out.println("AI (" + currentPlayer + ") đang đi...");

                Move bestMove = ai.findBestMove(board, currentPlayer);
                if (bestMove != null) {
                    board.makeMove(bestMove, currentPlayer);
                }
                // Sau khi AI đánh xong, vòng lặp tiếp tục chạy (continue)
                // để đổi lượt sang người chơi (hoặc xử lý tiếp nếu người chơi bị cấm)
            } else {
                // *** LƯỢT CỦA NGƯỜI (HUMAN) ***
                // Nếu đến lượt người và họ CÓ nước đi (đã check isEmpty ở trên)
                // -> DỪNG vòng lặp, trả về trạng thái để Frontend chờ user click.
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