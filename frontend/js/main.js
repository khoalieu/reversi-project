document.addEventListener("DOMContentLoaded", () => {
    const boardElement = document.getElementById("board");
    const blackScoreElement = document.getElementById("black-score");
    const whiteScoreElement = document.getElementById("white-score");
    const playerIndicator = document.getElementById("player-indicator");
    const statusElement = document.getElementById("status");
    const newGameBtn = document.getElementById("new-game-btn");
    const hintBtn = document.getElementById("hint-btn");
    const aiMoveBtn = document.getElementById("ai-move-btn");

    let currentBoardState = [];

    // Khởi tạo
    function initBoard() {
        boardElement.innerHTML = "";
        currentBoardState = [];
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

    async function handleCellClick(row, col) {
        // [THÊM] Nếu bàn cờ đang bị khóa (disabled) thì không làm gì cả
        if (boardElement.classList.contains("disabled")) {
            return;
        }

        statusElement.textContent = "Processing...";
        boardElement.classList.add("disabled"); // Khóa bàn cờ ngay lập tức

        const newState = await API.makeMove(row, col);

        // ... (phần code xử lý sau đó giữ nguyên như câu trả lời trước) ...
        if (newState) {
            setTimeout(() => {
                updateUI(newState); // updateUI sẽ gọi updateInfoText -> ẩn gợi ý AI
            }, 300);

            // Logic gọi AI sau 1s (như code cũ)
            if (!newState.gameOver && newState.isAiTurn) {
                setTimeout(async () => {
                    const aiState = await API.triggerAiMove();
                    if (aiState) updateUI(aiState);
                }, 1300); // 300ms vẽ + 1000ms delay suy nghĩ
            } else {
                // Nếu không phải lượt AI (ví dụ AI bị Pass, hoặc game over), mở khóa lại
                boardElement.classList.remove("disabled");
            }
        } else {
            boardElement.classList.remove("disabled");
            statusElement.textContent = "Invalid move!";
        }
    }

    function updateUI(gameState) {
        if (!gameState) return;

        const newGrid = gameState.board;
        const cells = document.querySelectorAll(".cell");

        // --- PHA 1: CHỈ ĐẶT QUÂN MỚI (Placing) ---
        cells.forEach(cell => {
            const r = parseInt(cell.dataset.row);
            const c = parseInt(cell.dataset.col);

            const oldType = currentBoardState[r] ? currentBoardState[r][c] : "EMPTY";
            const newType = newGrid[r][c];

            // Nếu ô trước đây trống và giờ có quân -> Đây là quân mới đặt
            if (oldType === "EMPTY" && newType !== "EMPTY") {
                cell.innerHTML = "";
                const piece = document.createElement("div");
                piece.classList.add("piece", newType.toLowerCase(), "placing");
                cell.appendChild(piece);
            }
            // Nếu ô này cần lật -> Giữ nguyên màu cũ (chưa lật vội)
            else if (oldType !== "EMPTY" && oldType !== newType) {
                if (cell.firstChild) {
                    cell.firstChild.className = `piece ${oldType.toLowerCase()}`;
                }
            }
        });

        // --- PHA 2: LẬT QUÂN (Flipping) ---
        // Đợi 500ms sau khi đặt quân mới bắt đầu lật
        setTimeout(() => {
            cells.forEach(cell => {
                const r = parseInt(cell.dataset.row);
                const c = parseInt(cell.dataset.col);

                const oldType = currentBoardState[r] ? currentBoardState[r][c] : "EMPTY";
                const newType = newGrid[r][c];

                // Tìm các quân cần lật và thực hiện hiệu ứng
                if (oldType !== "EMPTY" && oldType !== newType) {
                    cell.innerHTML = "";
                    const piece = document.createElement("div");
                    piece.classList.add("piece", newType.toLowerCase(), "flipping");
                    cell.appendChild(piece);
                }
            });

            // Hoàn tất cập nhật
            currentBoardState = JSON.parse(JSON.stringify(newGrid));
            boardElement.classList.remove("disabled");
            statusElement.style.color = "#2c3e50";

            updateInfoText(gameState);

        }, 500); // Độ trễ 0.5s
    }

    function updateInfoText(gameState) {
        if (gameState.scores) {
            blackScoreElement.textContent = gameState.scores.BLACK;
            whiteScoreElement.textContent = gameState.scores.WHITE;
        }

        const currentPlayer = gameState.currentPlayer;
        playerIndicator.className = `piece ${currentPlayer.toLowerCase()}`;

        // Cập nhật thông báo trạng thái
        if (gameState.gameOver) {
            statusElement.textContent = `Game Over! Winner: ${gameState.winner}`;
        } else if (gameState.isAiTurn) {
            statusElement.textContent = "AI is thinking..."; // Thông báo AI đang nghĩ
            statusElement.style.color = "#e67e22";
        } else {
            statusElement.textContent = "Your Turn";
            statusElement.style.color = "#2c3e50";
        }

        // Xóa class valid-move cũ
        const cells = document.querySelectorAll(".cell");
        cells.forEach(c => c.classList.remove("valid-move"));

        // [SỬA LỖI HIỂN THỊ]
        // Chỉ hiển thị gợi ý (chấm vàng) nếu KHÔNG phải lượt AI và game chưa kết thúc
        if (gameState.validMoves && !gameState.isAiTurn && !gameState.gameOver) {
            gameState.validMoves.forEach(move => {
                const index = move.row * 8 + move.col;
                if (cells[index]) cells[index].classList.add("valid-move");
            });
        }

        if (gameState.gameOver) {
            setTimeout(() => {
                alert(`Game Over! Winner: ${gameState.winner}`);
            }, 500);
        }
    }

    newGameBtn.addEventListener("click", async () => {
        statusElement.textContent = "Starting new game...";
        const newState = await API.newGame();
        // Reset trạng thái
        for(let i=0; i<8; i++) for(let j=0; j<8; j++) {
            if(currentBoardState[i]) currentBoardState[i][j] = "EMPTY";
        }

        if(newState) {
            currentBoardState = JSON.parse(JSON.stringify(newState.board));
            // Vẽ ngay lập tức (không hiệu ứng) cho New Game
            const cells = document.querySelectorAll(".cell");
            cells.forEach(cell => {
                cell.innerHTML = "";
                const r = parseInt(cell.dataset.row);
                const c = parseInt(cell.dataset.col);
                const type = newState.board[r][c];
                if (type !== "EMPTY") {
                    const piece = document.createElement("div");
                    piece.classList.add("piece", type.toLowerCase());
                    cell.appendChild(piece);
                }
            });
            updateInfoText(newState);
        }
    });

    hintBtn.addEventListener("click", () => {
        const validCells = document.querySelectorAll(".valid-move");
        validCells.forEach(cell => {
            cell.classList.add("hint");
            setTimeout(() => cell.classList.remove("hint"), 1000);
        });
    });

    initBoard();
    API.getGameState().then(newState => {
        if(newState) {
            currentBoardState = JSON.parse(JSON.stringify(newState.board));
            const cells = document.querySelectorAll(".cell");
            cells.forEach(cell => {
                const r = parseInt(cell.dataset.row);
                const c = parseInt(cell.dataset.col);
                const type = newState.board[r][c];
                if (type !== "EMPTY") {
                    const p = document.createElement("div");
                    p.classList.add("piece", type.toLowerCase());
                    cell.appendChild(p);
                }
            });
            updateInfoText(newState);
        }
    });
});