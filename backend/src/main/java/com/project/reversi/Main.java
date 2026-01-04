package com.project.reversi;

import com.project.reversi.game.ReversiGame;
import com.project.reversi.models.Move;

import io.javalin.Javalin;

public class Main {

    private static ReversiGame game = new ReversiGame();

    public static void main(String[] args) {

        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(it -> it.anyHost());
            });
        }).start(7070);

        System.out.println("Server đang chạy tại: http://localhost:7070");


        app.get("/gameState", ctx -> {
            ctx.json(game.getGameState());
        });

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

        app.post("/aiMove", ctx -> {
            game.playAi();
            ctx.json(game.getGameState());
        });

        app.post("/newGame", ctx -> {
            System.out.println("Khởi tạo game mới...");
            game = new ReversiGame();
            ctx.json(game.getGameState());
        });
    }
}