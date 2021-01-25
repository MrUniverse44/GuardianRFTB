package dev.mruniverse.rigoxrftb.core.utils.players;

import dev.mruniverse.rigoxrftb.core.RigoxRFTB;
import dev.mruniverse.rigoxrftb.core.enums.Files;
import dev.mruniverse.rigoxrftb.core.enums.PlayerStatus;
import dev.mruniverse.rigoxrftb.core.enums.RigoxBoard;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class PlayerRunnable extends BukkitRunnable {
    private final RigoxRFTB plugin;
    private boolean bossLb,actionLb;
    private String bossLobby,actionLobby;
    public PlayerRunnable(RigoxRFTB main) {
        plugin = main;
        bossLb = plugin.getFiles().getControl(Files.SETTINGS).getBoolean("settings.options.lobby-bossBar");
        bossLobby = plugin.getFiles().getControl(Files.MESSAGES).getString("messages.lobby.bossBar");
        actionLb = plugin.getFiles().getControl(Files.SETTINGS).getBoolean("settings.options.lobby-actionBar");
        actionLobby = plugin.getFiles().getControl(Files.MESSAGES).getString("messages.lobby.actionBar");
    }
    public void update() {
        bossLb = plugin.getFiles().getControl(Files.SETTINGS).getBoolean("settings.options.lobby-bossBar");
        bossLobby = plugin.getFiles().getControl(Files.MESSAGES).getString("messages.lobby.bossBar");
        actionLb = plugin.getFiles().getControl(Files.SETTINGS).getBoolean("settings.options.lobby-actionBar");
        actionLobby = plugin.getFiles().getControl(Files.MESSAGES).getString("messages.lobby.actionBar");
    }
    @Override
    public void run() {
        for (UUID uuid : plugin.getRigoxPlayers().keySet()) {
            PlayerManager playerManager = plugin.getPlayerData(uuid);
            PlayerStatus playerStatus = playerManager.getStatus();
            Player player = playerManager.getPlayer();
            plugin.getScoreboards().setScoreboard(playerManager.getBoard(),playerManager.getPlayer());
            plugin.getLogs().debug("board: " + playerManager.getBoard().toString() + " of " + playerManager.getPlayer().getName());
            if (playerStatus.equals(PlayerStatus.IN_LOBBY)) {
                if(bossLb) {
                    plugin.getUtils().sendBossBar(player, bossLobby);
                }
                if(actionLb) {
                    plugin.getUtils().sendActionbar(player, actionLobby);
                }
            } else {
                if(playerManager.getBoard().equals(RigoxBoard.WAITING) || playerManager.getBoard().equals(RigoxBoard.STARTING)) {
                    if(bossLb) {
                        plugin.getUtils().sendBossBar(player, bossLobby);
                    }
                }
            }

        }
    }
}
