// js/main.js

document.addEventListener("DOMContentLoaded", () => {
    const boardElement = document.getElementById("board");
    const blackScoreElement = document.getElementById("black-score");
    const whiteScoreElement = document.getElementById("white-score");
    const playerIndicator = document.getElementById("player-indicator");
    const statusElement = document.getElementById("status");
    const newGameBtn = document.getElementById("new-game-btn");

    // Biến lưu trạng thái game cục bộ để so sánh thay đổi (cho animation)
    let currentBoardState = [];

    // Khởi tạo bàn cờ rỗng
    function initBoard() {
        boardElement.innerHTML = ""; // Xóa bàn cờ cũ
        for (let i = 0; i < 8; i++) {
            for (let j = 0; j < 8; j++) {
                const cell = document.createElement("div");
                cell.classList.add("cell");
                cell.dataset.row = i;
                cell.dataset.col = j;

                // Sự kiện click vào ô
                cell.addEventListener("click", () => handleCellClick(i, j));

                boardElement.appendChild(cell);
            }
        }
    }

    // Xử lý khi người chơi click vào ô
    async function handleCellClick(row, col) {
        // Gọi API gửi nước đi
        const newState = await API.makeMove(row, col);
        if (newState) {
            updateUI(newState);
        }
    }

    // Cập nhật giao diện dựa trên dữ liệu từ Server
    function updateUI(gameState) {
        if (!gameState) return;

        // 1. Cập nhật bàn cờ và quân cờ
        const grid = gameState.board; // Giả sử server trả về { board: [[...]], ... }
        const cells = document.querySelectorAll(".cell");

        cells.forEach(cell => {
            const r = parseInt(cell.dataset.row);
            const c = parseInt(cell.dataset.col);
            const pieceType = grid[r][c]; // "BLACK", "WHITE", "EMPTY"

            // Xóa quân cờ cũ (nếu có)
            cell.innerHTML = "";

            if (pieceType !== "EMPTY") {
                const piece = document.createElement("div");
                piece.classList.add("piece");
                piece.classList.add(pieceType.toLowerCase()); // "black" hoặc "white"

                // Hiệu ứng đặt quân mới (nếu ô này trước đó trống)
                if (!currentBoardState[r] || currentBoardState[r][c] === "EMPTY") {
                    piece.classList.add("placing");
                }

                cell.appendChild(piece);
            }
        });

        // Lưu lại trạng thái để so sánh lần sau
        currentBoardState = JSON.parse(JSON.stringify(grid));

        // 2. Cập nhật điểm số
        // Giả sử server trả về { scores: { BLACK: 2, WHITE: 2 }, ... }
        if (gameState.scores) {
            blackScoreElement.textContent = gameState.scores.BLACK;
            whiteScoreElement.textContent = gameState.scores.WHITE;
        }

        // 3. Cập nhật lượt đi
        // Giả sử server trả về { currentPlayer: "WHITE", ... }
        const currentPlayer = gameState.currentPlayer; // "BLACK" hoặc "WHITE"
        playerIndicator.className = `piece ${currentPlayer.toLowerCase()}`;
        statusElement.textContent = `${currentPlayer === "BLACK" ? "Black" : "White"}'s Turn`;

        // 4. Hiển thị gợi ý nước đi (nếu có)
        // Xóa gợi ý cũ
        cells.forEach(c => c.classList.remove("valid-move"));

        if (gameState.validMoves) {
            gameState.validMoves.forEach(move => {
                // Tìm ô tương ứng và thêm class valid-move
                // move.row, move.col
                const index = move.row * 8 + move.col;
                if (cells[index]) {
                    cells[index].classList.add("valid-move");
                }
            });
        }

        // 5. Kiểm tra Game Over
        if (gameState.gameOver) {
            statusElement.textContent = `Game Over! Winner: ${gameState.winner}`;
            // Caó thể hiện popup #game-over ở đây
            // ...
        }
    }

    // Nút New Game
    newGameBtn.addEventListener("click", async () => {
        const newState = await API.newGame();
        updateUI(newState);
    });

    // Khởi chạy lần đầu
    initBoard();
    API.getGameState().then(updateUI);
});// Logic hiển thị, sự kiện click, v.v.
