package dev.mruniverse.rigoxrftb.utils.rigoxbossbars;

import dev.mruniverse.rigoxrftb.RigoxRFTB;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class BossManager {
    private RigoxRFTB plugin;
    public BossManager(RigoxRFTB main) {
        plugin = main;
    }
    private HashMap<UUID, PlayerBoard> players = new HashMap<>();
    private HashMap<UUID, Player> playerName = new HashMap<>();
    public boolean hasScore(Player player) {
        return players.containsKey(player.getUniqueId());
    }

    public PlayerBoard getBossOfPlayer(Player player) {
        return players.get(player.getUniqueId());
    }

    public void removeScore(Player player) {
        players.remove(player.getUniqueId());
        playerName.remove(player.getUniqueId());
    }
    public Player getPlayer(UUID uuid) {
        return playerName.get(uuid);
    }

    public void setBossBar(Player player,String message) {
        if(!existPlayer(player)) {
            players.put(player.getUniqueId(), new PlayerBoard(player, message));
            playerName.put(player.getUniqueId(), player);
        }
        updateBossBar(player, message);
    }
    public void updateBossBar(Player player,String message) {
        PlayerBoard bossBar = getBossOfPlayer(player);
        bossBar.setPercentage(100);

    }
    private boolean existPlayer(Player player) {
        return players.containsKey(player.getUniqueId());
    }
}
