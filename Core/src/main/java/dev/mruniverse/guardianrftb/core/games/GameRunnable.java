package dev.mruniverse.guardianrftb.core.games;


import dev.mruniverse.guardianrftb.core.GuardianRFTB;

public class GameRunnable implements Runnable {
    private final GuardianRFTB plugin;
    public GameRunnable(GuardianRFTB main) {
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
