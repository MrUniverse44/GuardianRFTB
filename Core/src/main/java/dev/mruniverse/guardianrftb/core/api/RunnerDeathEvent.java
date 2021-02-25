package dev.mruniverse.guardianrftb.core.api;

import dev.mruniverse.guardianrftb.core.games.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class RunnerDeathEvent extends Event {
    private final Game game;
    private final Player runner;
    private static final HandlerList handlerList = new HandlerList();

    public RunnerDeathEvent(Game rftbGame, Player rftbBeast) {
        runner = rftbBeast;
        game = rftbGame;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
    public Game getCurrentGame() {
        return game;
    }
    public Player getRunner() {
        return runner;
    }
}
