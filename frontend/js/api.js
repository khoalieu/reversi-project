
const API_BASE_URL = "http://localhost:7070";

const API = {
    async getGameState() {
        try {
            const response = await fetch(`${API_BASE_URL}/gameState`);
            if (!response.ok) throw new Error("Network response was not ok");
            return await response.json();
        } catch (error) {
            console.error("Error fetching game state:", error);
            return null;
        }
    },

    async makeMove(row, col) {
        try {
            const response = await fetch(`${API_BASE_URL}/makeMove`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ row, col })
            });

            if (!response.ok) {
                const errorMsg = await response.text();
                throw new Error(errorMsg);
            }
            return await response.json();
        } catch (error) {
            console.error("Error making move:", error);
            alert("Nước đi không hợp lệ hoặc lỗi server!");
            return null;
        }
    },

    async newGame() {
        try {
            const response = await fetch(`${API_BASE_URL}/newGame`, {
                method: "POST"
            });
            return await response.json();
        } catch (error) {
            console.error("Error starting new game:", error);
        }
    },
    async triggerAiMove() {
        try {
            const response = await fetch(`${API_BASE_URL}/aiMove`, {
                method: "POST"
            });
            return await response.json();
        } catch (error) {
            console.error("Error triggering AI move:", error);
            return null;
        }
    }
};