package com.project.reversi.models;

public class  Move {
    private int col;
    private int row;

    public Move(int col, int row) {
        this.col = col;
        this.row = row;
    }
    public Move() {
        // Constructor rỗng bắt buộc cho Jackson
    }
    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }
    @Override
    public String toString() {
        return "(" + row + ", " + col + ")";
    }

}