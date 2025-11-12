public enum Board {
    private final int SIZE;
    private Piece[][] grid;

    Board(Piece[][] grid) {
        this.grid = new Piece[SIZE][SIZE];
        initializeBoard();
    }
    private void initializeBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                grid[i][j] = Piece.EMPTY
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
    
}