package com.project.reversi.models;

import java.util.ArrayList;

public class Board {
    private final int SIZE = 8;
    private Piece[][] grid;
    private static final int[][] DIRECTIONS = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1},
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
    };
    
    public Board() {
        this.grid = new Piece[SIZE][SIZE];
        initializeBoard();
    }
    
    public Board(Piece[][] grid) {
        this.grid = new Piece[SIZE][SIZE];
        if (grid != null && grid.length == SIZE && grid[0].length == SIZE) {
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    this.grid[i][j] = grid[i][j];
                }
            }
        } else {
            initializeBoard();
        }
    }
    private void initializeBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                grid[i][j] = Piece.EMPTY;
            }
        }
        grid[3][3] = Piece.BLACK;
        grid[3][4] = Piece.WHITE;
        grid[4][3] = Piece.WHITE;
        grid[4][4] = Piece.BLACK;
    }
    public Piece getPiece(int row, int col) {
        return grid[row][col];
    }
    public boolean isValidMove(Move move, Piece player) {
        int r = move.getRow();
        int c = move.getCol();
        if (r < 0 || r >= SIZE || c < 0 || c >= SIZE || grid[r][c] != Piece.EMPTY) {
            return false;
        }
        for (int[] direction : DIRECTIONS) {
            int dr = direction[0];
            int dc = direction[1];
            if (canFlipInDirection(r, c, dr, dc, player)) {
                return true;
            }
        }
        return false;
    }

    private boolean canFlipInDirection(int startR, int startC, int dr, int dc, Piece player) {
        Piece opponent = (player == Piece.BLACK) ? Piece.WHITE : Piece.BLACK;

        int r = startR + dr;
        int c = startC + dc;

        if (r < 0 || r >= SIZE || c < 0 || c >= SIZE || grid[r][c] != opponent) {
            return false;
        }
        r += dr;
        c += dc;

        while (r >= 0 && r < SIZE && c >= 0 && c < SIZE && grid[r][c] == opponent) {
            r += dr;
            c += dc;
        }
        if (r >= 0 && r < SIZE && c >= 0 && c < SIZE && grid[r][c] == player) {
            return true;
        }
        return false;
    }
    public ArrayList<Move> getValidMoves(Piece player) {
        ArrayList<Move> validMoves = new ArrayList<>();
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                Move move = new Move(c, r);
                if (isValidMove(move, player)) {
                    validMoves.add(move);
                }
            }
        }
        return validMoves;
    }
    public void makeMove(Move move, Piece player) {
        int r = move.getRow();
        int c = move.getCol();
        grid[r][c] = player;
        
        for (int[] direction : DIRECTIONS) {
            int dr = direction[0];
            int dc = direction[1];
            if (canFlipInDirection(r, c, dr, dc, player)) {
                flipPiecesInDirection(r, c, dr, dc, player);
            }
        }
    }
    
    private void flipPiecesInDirection(int startR, int startC, int dr, int dc, Piece player) {
        Piece opponent = (player == Piece.BLACK) ? Piece.WHITE : Piece.BLACK;
        
        int r = startR + dr;
        int c = startC + dc;
        
        while (r >= 0 && r < SIZE && c >= 0 && c < SIZE && grid[r][c] == opponent) {
            grid[r][c] = player;
            r += dr;
            c += dc;
        }
    }
    public boolean isGameOver() {
        return getValidMoves(Piece.BLACK).isEmpty() && getValidMoves(Piece.WHITE).isEmpty();
    }
    public int getSize() {
        return SIZE;
    }
    
    public Piece[][] getGrid() {
        Piece[][] copy = new Piece[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                copy[i][j] = this.grid[i][j];
            }
        }
        return copy;
    }
    public int countPieces(Piece pieceType) {
        int count = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (grid[i][j] == pieceType) {
                    count++;
                }
            }
        }
        return count;
    }
}