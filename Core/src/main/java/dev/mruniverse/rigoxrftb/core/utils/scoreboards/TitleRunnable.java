package dev.mruniverse.rigoxrftb.core.utils.scoreboards;

import dev.mruniverse.rigoxrftb.core.RigoxRFTB;
import dev.mruniverse.rigoxrftb.core.enums.RigoxFiles;
import dev.mruniverse.rigoxrftb.core.utils.players.PlayerManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;

public class TitleRunnable extends BukkitRunnable {
    private final RigoxRFTB plugin;
    private boolean isEnabled;
    private int showingTitle = 0;
    private List<String> titles;
    public TitleRunnable(RigoxRFTB main) {
        plugin = main;
        titles = main.getStorage().getControl(RigoxFiles.SCOREBOARD).getStringList("scoreboards.animatedTitle.titles");
        isEnabled = main.getStorage().getControl(RigoxFiles.SCOREBOARD).getBoolean("scoreboards.animatedTitle.toggle");
    }
    public void update() {
        titles = plugin.getStorage().getControl(RigoxFiles.SCOREBOARD).getStringList("scoreboards.animatedTitle.titles");
        isEnabled = plugin.getStorage().getControl(RigoxFiles.SCOREBOARD).getBoolean("scoreboards.animatedTitle.toggle");
    }
    @Override
    public void run () {
        if(!isEnabled) cancel();
        for (UUID uuid : plugin.getRigoxPlayers().keySet()) {
            PlayerManager playerManager = plugin.getPlayerData(uuid);
            String currentTitle = titles.get(showingTitle);
            plugin.getScoreboards().setTitle(playerManager.getPlayer(),currentTitle);
            if(showingTitle == (titles.size() - 1)) {
                showingTitle = 0;
            } else {
                showingTitle++;
            }
        }
    }
}