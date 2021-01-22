package dev.mruniverse.rigoxrftb.core.utils.players;

import dev.mruniverse.rigoxrftb.core.enums.PlayerStatus;
import dev.mruniverse.rigoxrftb.core.enums.RigoxBoard;
import dev.mruniverse.rigoxrftb.core.games.Game;
import org.bukkit.entity.Player;

public class PlayerManager {
    private PlayerStatus playerStatus;
    private RigoxBoard rigoxBoard;
    private final Player player;
    private Game currentGame;
    public PlayerManager(Player p) {
        player = p;
        rigoxBoard = RigoxBoard.LOBBY;
        playerStatus = PlayerStatus.IN_LOBBY;
        currentGame = null;
    }
    public void setStatus(PlayerStatus status) {
        playerStatus = status;
    }
    public void setBoard(RigoxBoard board) {
        rigoxBoard = board;
    }
    public void setGame(Game game) { currentGame = game; }
    public RigoxBoard getBoard() {
        return rigoxBoard;
    }
    public PlayerStatus getStatus() {
        return playerStatus;
    }
    public String getName() {
        return player.getName();
    }
    public Game getGame() { return currentGame; }
    public Player getPlayer() {
        return player;
    }
}

