package com.project.reversi;

import com.project.reversi.game.ReversiGame;
import com.project.reversi.models.Move;

import io.javalin.Javalin;
import io.javalin.http.Context;

public class Main {

    // Khai báo đối tượng Game (duy nhất 1 instance chạy trên server)
    private static ReversiGame game = new ReversiGame();

    public static void main(String[] args) {

        // 1. Khởi tạo Javalin Server
        Javalin app = Javalin.create(config -> {
            // BẬT CORS: Để Frontend (port 5500 hoặc file) gọi được Backend (port 7070)
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(it -> it.anyHost());
            });
        }).start(7070);

        System.out.println("Server đang chạy tại: http://localhost:7070");

        // 2. Định nghĩa các API Endpoints

        // API 1: Lấy trạng thái bàn cờ hiện tại (Frontend gọi cái này liên tục để vẽ)
        app.get("/gameState", ctx -> {
            ctx.json(game.getGameState());
        });

        // API 2: Người chơi thực hiện nước đi
        app.post("/makeMove", ctx -> {
            try {
                Move playerMove = ctx.bodyAsClass(Move.class);
                game.play(playerMove); // Chỉ đánh nước người chơi
                ctx.json(game.getGameState());
            } catch (Exception e) {
                e.printStackTrace();
                ctx.status(400).result("Lỗi");
            }
        });

        // [MỚI] API 4: Yêu cầu AI thực hiện nước đi
        app.post("/aiMove", ctx -> {
            game.playAi(); // Gọi hàm xử lý AI
            ctx.json(game.getGameState()); // Trả về bàn cờ sau khi AI đi
        });

        // API 3: Reset game mới
        app.post("/newGame", ctx -> {
            System.out.println("Khởi tạo game mới...");
            game = new ReversiGame(); // Tạo lại đối tượng game mới tinh
            ctx.json(game.getGameState());
        });
    }
}