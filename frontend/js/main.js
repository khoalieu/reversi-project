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
        if (boardElement.classList.contains("disabled")) {
            return;
        }

        statusElement.textContent = "Processing...";
        boardElement.classList.add("disabled");

        const newState = await API.makeMove(row, col);

        if (newState) {
            // Cập nhật UI cho nước đi của người chơi
            setTimeout(() => {
                updateUI(newState);
            }, 300);

            // [SỬA ĐỔI] Thay vì setTimeout gọi 1 lần, ta dùng hàm đệ quy xử lý chuỗi lượt AI
            if (!newState.gameOver && newState.isAiTurn) {
                // Gọi hàm xử lý lượt AI (chờ 1.3s để người chơi kịp nhìn nước đi vừa đánh)
                setTimeout(() => processAiTurn(), 1300);
            } 
            // Lưu ý: Không cần 'else' unlock ở đây nữa vì updateUI đã lo việc đó
        } else {
            boardElement.classList.remove("disabled");
            statusElement.textContent = "Invalid move!";
        }
    }
    async function processAiTurn() {
        // Gọi API để AI đánh
        const aiState = await API.triggerAiMove();

        if (aiState) {
            updateUI(aiState);

            // [QUAN TRỌNG] Kiểm tra xem sau khi AI đánh, có vẫn là lượt AI không?
            // Trường hợp này xảy ra khi người chơi bị Pass (không có nước đi)
            if (!aiState.gameOver && aiState.isAiTurn) {
                
                // Cập nhật thông báo cho người dùng biết họ bị mất lượt
                statusElement.textContent = "You have no moves! AI plays again...";
                statusElement.style.color = "#c0392b";

                // Gọi đệ quy để AI đánh tiếp sau 1.5s
                setTimeout(() => {
                    processAiTurn();
                }, 1500);
            }
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

            // [SỬA LỖI QUAN TRỌNG]
            // Chỉ mở khóa bàn cờ khi KHÔNG phải lượt AI và game chưa kết thúc.
            // Nếu là lượt AI, bàn cờ phải tiếp tục bị khóa (class 'disabled')
            if (!gameState.isAiTurn && !gameState.gameOver) {
                boardElement.classList.remove("disabled");
            }
            
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