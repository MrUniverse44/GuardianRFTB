package dev.mruniverse.rigoxrftb.utils.bossbars;

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

    public PlayerBoard getBossOfPlayer(Player player) {
        return players.get(player.getUniqueId());
    }

    public void removeBossBar(Player player) {
        players.remove(player.getUniqueId());
    }

    public void setBossBar(Player player,String message) {
        if(!existPlayer(player)) {
            players.put(player.getUniqueId(), new PlayerBoard(player, message));
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
