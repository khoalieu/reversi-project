package com.project.reversi.game;

import com.project.reversi.ai.AlphaBetaPruning;
import com.project.reversi.ai.SearchAlgorithm;
import com.project.reversi.models.Board;
import com.project.reversi.models.Move;
import com.project.reversi.models.Piece;

import java.util.HashMap;
import java.util.Map;

public class ReversiGame {
    // 1. Các thuộc tính (Attributes) theo sơ đồ lớp
    private Board board;
    private Piece currentPlayer;
    private SearchAlgorithm ai;

    // 2. Hàm khởi tạo (Constructor)
    public ReversiGame() {
        this.board = new Board();           // Tạo bàn cờ mới
        this.currentPlayer = Piece.BLACK;   // Luật Reversi: Quân Đen đi trước
        
        // Khởi tạo AI (chọn AlphaBeta hoặc Minimax tùy bạn cấu hình)
        this.ai = new AlphaBetaPruning(); 
    }
    public Map<String, Object> getGameState() {
        Map<String, Object> state = new HashMap<>();

        // 1. Trả về mảng bàn cờ (Frontend cần grid[][])
        state.put("board", board.getGrid());

        // 2. Trả về người chơi hiện tại ("BLACK" hoặc "WHITE")
        state.put("currentPlayer", currentPlayer);

        // 3. Tính điểm và trả về
        Map<String, Integer> scores = new HashMap<>();
        scores.put("BLACK", board.countPieces(Piece.BLACK));
        scores.put("WHITE", board.countPieces(Piece.WHITE));
        state.put("scores", scores);

        // 4. Trả về danh sách nước đi hợp lệ (để Frontend vẽ gợi ý)
        state.put("validMoves", board.getValidMoves(currentPlayer));

        // 5. Trạng thái kết thúc game
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
    // Đổi lượt đi giữa hai bên
    private void switchTurn() {
        if (this.currentPlayer == Piece.BLACK) {
            this.currentPlayer = Piece.WHITE;
        } else {
            this.currentPlayer = Piece.BLACK;
        }
    }
// Trong file ReversiGame.java

    public void play(Move playerMove) {

        // BƯỚC 1: Kiểm tra tính hợp lệ
        // "Nếu nước đi NÀY của người chơi NÀY trên bàn cờ NÀY là KHÔNG hợp lệ..."
        if (!board.isValidMove(playerMove, currentPlayer)) {
            System.out.println("Nước đi không hợp lệ! Vui lòng chọn ô khác.");
            return; // Dừng hàm lại, không làm gì tiếp theo
        }

        // Nếu hợp lệ thì code tiếp ở dưới...
        board.makeMove(playerMove, currentPlayer);

        // Gọi hàm switchTurn() mà bạn vừa nhắc tới
        switchTurn();
    }
}