package dev.mruniverse.guardianrftb.core.api;

import dev.mruniverse.guardianrftb.core.games.Game;
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
