package dev.mruniverse.rigoxrftb.rigoxrftb.utils.RigoxScoreboards;

import dev.mruniverse.rigoxrftb.rigoxrftb.RigoxRFTB;
import dev.mruniverse.rigoxrftb.rigoxrftb.enums.Files;
import dev.mruniverse.rigoxrftb.rigoxrftb.enums.RigoxBoard;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class BoardManager {
    private RigoxRFTB plugin;
    public BoardManager(RigoxRFTB main) {
        plugin = main;
    }
    private HashMap<UUID, PlayerManager> players = new HashMap<>();
    private HashMap<UUID, RigoxBoard> playerBoard = new HashMap<>();
    private HashMap<UUID, Player> playerName = new HashMap<>();
    public boolean hasScore(Player player) {
        return players.containsKey(player.getUniqueId());
    }

    public PlayerManager getBoardOfPlayer(Player player) {
        return players.get(player.getUniqueId());
    }

    public void removeScore(Player player) {
        players.remove(player.getUniqueId());
        playerBoard.remove(player.getUniqueId());
        playerName.remove(player.getUniqueId());
    }
    public Player getPlayer(UUID uuid) {
        return playerName.get(uuid);
    }

    public void setScoreboard(RigoxBoard board, Player player) {
        if(!existPlayer(player)) {
            players.put(player.getUniqueId(),new PlayerManager(player));
            playerName.put(player.getUniqueId(),player);
        } else {
            playerBoard.remove(player.getUniqueId());
        }
        playerBoard.put(player.getUniqueId(),board);
        updateScoreboard(board,player);
    }
    public void updateScoreboard(RigoxBoard board,Player player) {
        PlayerManager scoreboard = getBoardOfPlayer(player);
        if(board.equals(RigoxBoard.LOBBY)) {
            String title;
            title = plugin.getUtils().replaceVariables(plugin.getUtils().getTitle(RigoxBoard.LOBBY),player);
            scoreboard.setTitle(title);
            scoreboard.updateLines(plugin.getUtils().getLines(RigoxBoard.LOBBY,player));
        }
    }
    public HashMap<UUID, RigoxBoard> getBoards() {
        return playerBoard;
    }
    public RigoxBoard getBoard(UUID uuid) {
        return playerBoard.get(uuid);
    }
    private boolean existPlayer(Player player) {
        return players.containsKey(player.getUniqueId());
    }

}
