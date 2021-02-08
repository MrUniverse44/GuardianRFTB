package dev.mruniverse.rigoxrftb.core.games;


import dev.mruniverse.rigoxrftb.core.RigoxRFTB;

public class GameRunnable implements Runnable {
    private final RigoxRFTB plugin;
    public GameRunnable(RigoxRFTB main) {
        plugin = main;
    }
    public void run() {
        for (Game game : plugin.getGameManager().getGames()) {
            if (game.gameTimer == 1) {
                game.gameCount(GameCountType.START_COUNT);
                continue;
            }
            if (game.gameTimer == 2)
                game.gameCount(GameCountType.IN_GAME_COUNT);
        }
    }
}
