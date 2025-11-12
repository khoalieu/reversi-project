public enum Board {
    private final int SIZE;
    private Piece[][] grid;
    private static final int[][] DIRECTIONS = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1},
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
    };
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
    pubic boolean isValidMove(Move move, Piece player) {
        if (r < 0 || r >= SIZE || c < 0 || c >= SIZE || grid[r][c] != Piece.EMPTY) {
            return false; //nuoc di khong hop le
        }
        for (int[] direction : DIRECTIONS) {
            int dr = direction[0];
            int dc = direction[1];
            if (canFlipInDirection(r, c, dr, dc, player)) {
                return true;
            }
        }
        return false; // Không lật được quân nào ở bất kỳ hướng nào
    }
    }
    private boolean canFlipInDirection(int startR, int startC, int dr, int dc, Piece player) {
        // Xác định quân đối thủ
        Piece opponent = (player == Piece.BLACK) ? Piece.WHITE : Piece.BLACK;

        int r = startR + dr; // Bắt đầu quét từ ô kế bên
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
}