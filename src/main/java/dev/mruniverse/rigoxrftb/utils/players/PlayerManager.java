package dev.mruniverse.rigoxrftb.utils.players;

import dev.mruniverse.rigoxrftb.enums.PlayerStatus;
import dev.mruniverse.rigoxrftb.enums.RigoxBoard;
import org.bukkit.entity.Player;

public class PlayerManager {
    private PlayerStatus playerStatus;
    private RigoxBoard rigoxBoard;
    private final Player player;
    public PlayerManager(Player p) {
        player = p;
        rigoxBoard = RigoxBoard.LOBBY;
        playerStatus = PlayerStatus.IN_LOBBY;
    }
    public void setStatus(PlayerStatus status) {
        playerStatus = status;
    }
    public void setBoard(RigoxBoard board) {
        rigoxBoard = board;
    }
    public RigoxBoard getBoard() {
        return rigoxBoard;
    }
    public PlayerStatus getStatus() {
        return playerStatus;
    }
    public String getName() {
        return player.getName();
    }
    public Player getPlayer() {
        return player;
    }
}
