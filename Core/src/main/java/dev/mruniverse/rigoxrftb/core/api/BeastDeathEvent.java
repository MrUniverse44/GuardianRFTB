package dev.mruniverse.rigoxrftb.core.api;

import dev.mruniverse.rigoxrftb.core.games.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class BeastDeathEvent extends Event {
    private final Game game;
    private final Player beast;
    private static final HandlerList handlerList = new HandlerList();

    public BeastDeathEvent(Game rftbGame, Player rftbBeast) {
        beast = rftbBeast;
        game = rftbGame;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
    public Game getCurrentGame() {
        return game;
    }
    public Player getBeast() {
        return beast;
    }
}
