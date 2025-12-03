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

    public ReversiGame() {
        this.board = new Board();
        this.currentPlayer = Piece.BLACK;

        this.ai = new AlphaBetaPruning(); 
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
    private void switchTurn() {
        if (this.currentPlayer == Piece.BLACK) {
            this.currentPlayer = Piece.WHITE;
        } else {
            this.currentPlayer = Piece.BLACK;
        }
    }

    public void play(Move playerMove) {

        if (!board.isValidMove(playerMove, currentPlayer)) {
            System.out.println("Nước đi không hợp lệ! Vui lòng chọn ô khác.");
            return;
        }
        board.makeMove(playerMove, currentPlayer);

        switchTurn();
    }
}