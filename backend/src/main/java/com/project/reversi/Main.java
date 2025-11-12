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
                // Jackson tự động biến JSON { "row": 2, "col": 3 } thành object Move
                Move playerMove = ctx.bodyAsClass(Move.class);

                System.out.println("Nhận nước đi: " + playerMove.getRow() + ", " + playerMove.getCol());

                // Gọi logic game xử lý (Kiểm tra, đánh, AI đánh lại...)
                game.play(playerMove);

                // Trả về trạng thái mới sau khi đánh
                ctx.json(game.getGameState());
            } catch (Exception e) {
                e.printStackTrace();
                ctx.status(400).result("Dữ liệu nước đi không hợp lệ");
            }
        });

        // API 3: Reset game mới
        app.post("/newGame", ctx -> {
            System.out.println("Khởi tạo game mới...");
            game = new ReversiGame(); // Tạo lại đối tượng game mới tinh
            ctx.json(game.getGameState());
        });
    }
}