package com.project.reversi.models;

public class Move {
    private int row;
    private int col;

    // SỬA QUAN TRỌNG: Đổi thứ tự tham số thành (row, col)
    // Để khớp với cách gọi new Move(r, c) bên Board.java
    public Move(int row, int col) {
        this.row = row;
        this.col = col;
    }

    // Constructor rỗng bắt buộc cho Jackson (khi nhận JSON từ API)
    public Move() {
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    @Override
    public String toString() {
        return "Move{row=" + row + ", col=" + col + "}";
    }
}