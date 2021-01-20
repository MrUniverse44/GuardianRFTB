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
    private static HashMap<UUID, PlayerManager> players = new HashMap<>();
    public boolean hasScore(Player player) {
        return players.containsKey(player.getUniqueId());
    }

    public PlayerManager getBoardOfPlayer(Player player) {
        return players.get(player.getUniqueId());
    }

    public PlayerManager removeScore(Player player) {
        return players.remove(player.getUniqueId());
    }

    public void setScoreboard(RigoxBoard board, Player player) {
        if(!existPlayer(player)) {
            players.put(player.getUniqueId(),new PlayerManager(player));
        }
        updateScoreboard(board,player);
    }
    public void updateScoreboard(RigoxBoard board,Player player) {
        PlayerManager scoreboard = getBoardOfPlayer(player);
        if(board.equals(RigoxBoard.LOBBY)) {
            String title;
            title = plugin.getUtils().replaceVariables(plugin.getUtils().getTitle(RigoxBoard.LOBBY),player);
            scoreboard.setTitle(title);

        }
    }
    private boolean existPlayer(Player player) {
        return players.containsKey(player.getUniqueId());
    }

}
