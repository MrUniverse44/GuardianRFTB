package dev.mruniverse.rigoxrftb.rigoxrftb.utils.rigoxscoreboards;

import dev.mruniverse.rigoxrftb.rigoxrftb.RigoxRFTB;
import dev.mruniverse.rigoxrftb.rigoxrftb.enums.Files;
import dev.mruniverse.rigoxrftb.rigoxrftb.enums.RigoxBoard;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class BoardRunnable extends BukkitRunnable {
    private final RigoxRFTB plugin;
    private boolean actionBar;
    private String actionMessage;
    public BoardRunnable(RigoxRFTB main) {
        plugin = main;
        actionBar = plugin.getFiles().getControl(Files.SETTINGS).getBoolean("settings.options.lobby-actionBar");
        actionMessage = plugin.getFiles().getControl(Files.MESSAGES).getString("messages.lobby.actionBar");
    }
    public void update() {
        actionBar = plugin.getFiles().getControl(Files.SETTINGS).getBoolean("settings.options.lobby-actionBar");
        actionMessage = plugin.getFiles().getControl(Files.MESSAGES).getString("messages.lobby.actionBar");
    }
    @Override
    public void run() {
        for(UUID uuid : plugin.getScoreboards().getBoards().keySet()) {
            RigoxBoard board = plugin.getScoreboards().getBoard(uuid);
            Player player = plugin.getScoreboards().getPlayer(uuid);
            plugin.getScoreboards().setScoreboard(board,player);
            if(board.equals(RigoxBoard.LOBBY)) {
                if(actionBar) {
                    plugin.getUtils().sendActionbar(player,actionMessage);
                }
            }
        }
    }
}
