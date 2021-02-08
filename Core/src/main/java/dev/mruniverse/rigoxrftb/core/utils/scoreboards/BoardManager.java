package dev.mruniverse.rigoxrftb.core.utils.scoreboards;

import dev.mruniverse.rigoxrftb.core.RigoxRFTB;
import dev.mruniverse.rigoxrftb.core.enums.RigoxBoard;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class BoardManager {
    private final RigoxRFTB plugin;
    public BoardManager(RigoxRFTB main) {
        plugin = main;
    }
    private final HashMap<UUID, PlayerManager> players = new HashMap<>();

    public PlayerManager getBoardOfPlayer(Player player) {
        return players.get(player.getUniqueId());
    }

    public void removeScore(Player player) {
        players.remove(player.getUniqueId());
    }

    public void setScoreboard(RigoxBoard board, Player player) {
        if(!existPlayer(player)) {
            players.put(player.getUniqueId(), new PlayerManager(player));
        }
        updateScoreboard(board,player);
    }
    public void updateScoreboard(RigoxBoard board,Player player) {
        PlayerManager scoreboard = getBoardOfPlayer(player);
        String title;
        title = plugin.getUtils().replaceVariables(plugin.getUtils().getTitle(board),player);
        scoreboard.setTitle(title);
        scoreboard.updateLines(plugin.getUtils().getLines(board,player));
    }
    private boolean existPlayer(Player player) {
        return players.containsKey(player.getUniqueId());
    }

}
