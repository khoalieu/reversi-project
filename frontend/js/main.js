document.addEventListener("DOMContentLoaded", () => {
    const boardElement = document.getElementById("board");
    const blackScoreElement = document.getElementById("black-score");
    const whiteScoreElement = document.getElementById("white-score");
    const playerIndicator = document.getElementById("player-indicator");
    const statusElement = document.getElementById("status");
    const newGameBtn = document.getElementById("new-game-btn");

    // Biến lưu trạng thái bàn cờ CŨ để so sánh
    let currentBoardState = [];

    // Khởi tạo bàn cờ rỗng
    function initBoard() {
        boardElement.innerHTML = "";
        currentBoardState = []; // Reset trạng thái
        for (let i = 0; i < 8; i++) {
            let rowArr = [];
            for (let j = 0; j < 8; j++) {
                const cell = document.createElement("div");
                cell.classList.add("cell");
                cell.dataset.row = i;
                cell.dataset.col = j;
                cell.addEventListener("click", () => handleCellClick(i, j));
                boardElement.appendChild(cell);
                rowArr.push("EMPTY");
            }
            currentBoardState.push(rowArr);
        }
    }

    // Xử lý khi người chơi click
    async function handleCellClick(row, col) {
        // 1. Giao diện chờ: Thông báo AI đang nghĩ
        statusElement.textContent = "AI is thinking...";
        statusElement.style.color = "#e67e22"; // Đổi màu chữ cho nổi bật
        boardElement.classList.add("disabled"); // Khóa bàn cờ lại

        // 2. Gọi API (Backend xử lý cực nhanh)
        // Lưu ý: Kết quả trả về đã bao gồm cả nước đi của bạn VÀ của AI
        const newState = await API.makeMove(row, col);

        // 3. Tạo độ trễ giả (0.7 giây) để người dùng cảm thấy AI đang tính
        if (newState) {
            setTimeout(() => {
                updateUI(newState);
                boardElement.classList.remove("disabled"); // Mở khóa bàn cờ
                statusElement.style.color = "#2c3e50"; // Trả lại màu chữ
            }, 700); // 700ms = 0.7 giây
        } else {
            // Nếu lỗi thì mở khóa ngay
            boardElement.classList.remove("disabled");
        }
    }

    function updateUI(gameState) {
        if (!gameState) return;

        const newGrid = gameState.board;
        const cells = document.querySelectorAll(".cell");

        cells.forEach(cell => {
            const r = parseInt(cell.dataset.row);
            const c = parseInt(cell.dataset.col);

            const oldType = currentBoardState[r] ? currentBoardState[r][c] : "EMPTY";
            const newType = newGrid[r][c];

            // Chỉ vẽ lại nếu có sự thay đổi hoặc ô đó đang trống
            if (newType !== "EMPTY") {
                // Xóa quân cũ đi để vẽ lại (để kích hoạt animation)
                cell.innerHTML = "";

                const piece = document.createElement("div");
                piece.classList.add("piece");
                piece.classList.add(newType.toLowerCase()); // "black" hoặc "white"

                // LOGIC QUAN TRỌNG: Quyết định hiệu ứng
                if (oldType === "EMPTY") {
                    // 1. Ô này trước đây trống -> Đây là nước đi mới đặt xuống
                    piece.classList.add("placing");
                } else if (oldType !== newType) {
                    // 2. Ô này trước đây khác màu -> Đây là quân bị lật (Flipping)
                    piece.classList.add("flipping");
                }

                cell.appendChild(piece);
            } else {
                cell.innerHTML = ""; // Nếu là EMPTY thì xóa sạch
            }
        });

        // Cập nhật trạng thái cũ thành trạng thái mới cho lần sau
        currentBoardState = JSON.parse(JSON.stringify(newGrid));

        // Cập nhật điểm số & Lượt đi
        if (gameState.scores) {
            blackScoreElement.textContent = gameState.scores.BLACK;
            whiteScoreElement.textContent = gameState.scores.WHITE;
        }

        const currentPlayer = gameState.currentPlayer;
        playerIndicator.className = `piece ${currentPlayer.toLowerCase()}`;
        statusElement.textContent = `${currentPlayer === "BLACK" ? "Black" : "White"}'s Turn`;

        // Vẽ gợi ý (Hints)
        cells.forEach(c => c.classList.remove("valid-move"));
        if (gameState.validMoves) {
            gameState.validMoves.forEach(move => {
                const index = move.row * 8 + move.col;
                if (cells[index]) cells[index].classList.add("valid-move");
            });
        }

        // Xử lý Game Over
        if (gameState.gameOver) {
            setTimeout(() => {
                // Hiện popup thắng thua (bạn có thể bỏ comment nếu muốn hiện popup)
                // alert(`Game Over! Winner: ${gameState.winner}`);
                statusElement.textContent = `Game Over! Winner: ${gameState.winner}`;
            }, 500);
        }
    }

    newGameBtn.addEventListener("click", async () => {
        statusElement.textContent = "Starting new game...";
        const newState = await API.newGame();
        // Reset biến trạng thái cục bộ về rỗng để animation hoạt động đúng
        for(let i=0; i<8; i++) for(let j=0; j<8; j++) currentBoardState[i][j] = "EMPTY";
        updateUI(newState);
    });

    // Khởi chạy
    initBoard();
    API.getGameState().then(newState => {
        // Lần load đầu tiên không cần animation, cập nhật luôn biến cục bộ
        if(newState) {
            currentBoardState = JSON.parse(JSON.stringify(newState.board));
            updateUI(newState);
        }
    });
});