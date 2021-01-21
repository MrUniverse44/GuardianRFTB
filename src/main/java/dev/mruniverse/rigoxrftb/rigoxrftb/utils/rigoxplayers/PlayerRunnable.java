package dev.mruniverse.rigoxrftb.rigoxrftb.utils.rigoxplayers;

import dev.mruniverse.rigoxrftb.rigoxrftb.RigoxRFTB;
import dev.mruniverse.rigoxrftb.rigoxrftb.enums.Files;
import dev.mruniverse.rigoxrftb.rigoxrftb.enums.PlayerStatus;
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
            plugin.getScoreboards().setScoreboard(playerManager.getBoard(),playerManager.getPlayer());
            if (playerStatus.equals(PlayerStatus.IN_LOBBY)) {
                if(bossLb) {
                    plugin.getBossBar().setBossBar(playerManager.getPlayer(), bossLobby);
                }
                if(actionLb) {
                    plugin.getUtils().sendActionbar(playerManager.getPlayer(), actionLobby);
                }
            }

        }
    }
}
