package dev.mruniverse.rigoxrftb.core.utils;

import dev.mruniverse.rigoxrftb.core.RigoxRFTB;
import dev.mruniverse.rigoxrftb.core.enums.Files;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class Teams {
    private final RigoxRFTB plugin;
    public Teams(RigoxRFTB main) {
        plugin = main;
    }
    public void registerTeams(Player player) {
        Scoreboard sb = player.getScoreboard();
        Team runners = sb.getTeam("rxRunners");
        if (runners == null)
            runners = sb.registerNewTeam("rxRunners");
        if(plugin.getFiles().getControl(Files.SETTINGS).getBoolean("settings.tags.runners.toggle")) {
            String runnerTag = plugin.getFiles().getControl(Files.SETTINGS).getString("settings.tags.runners.tag");
            if (runnerTag == null) runnerTag = "&b&lRUNNERS";
            runners.setSuffix(" " + ChatColor.translateAlternateColorCodes('&', runnerTag));
        }
        runners.setAllowFriendlyFire(false);
        runners.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        Team beasts = sb.getTeam("rxBeasts");
        if (beasts == null)
            beasts = sb.registerNewTeam("rxBeasts");
        if(plugin.getFiles().getControl(Files.SETTINGS).getBoolean("settings.tags.beasts.toggle")) {
            String beastTag = plugin.getFiles().getControl(Files.SETTINGS).getString("settings.tags.beasts.tag");
            if (beastTag == null) beastTag = "&c&lBEAST";
            beasts.setSuffix(" " + ChatColor.translateAlternateColorCodes('&', beastTag));
        }
        beasts.setAllowFriendlyFire(false);
        beasts.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
    }

    public void loadPlayer(Player loadPlayer) {
        try {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (plugin.getPlayerData(player.getUniqueId()).getGame() != null) {
                    if (!plugin.getPlayerData(player.getUniqueId()).getGame().beasts.contains(player)) {
                        Team beasts = loadPlayer.getScoreboard().getTeam("rxBeasts");
                        beasts.addPlayer(player);
                    } else {
                        Team runners = loadPlayer.getScoreboard().getTeam("rxRunners");
                        runners.addPlayer(player);
                    }
                } else {
                    Team runners = loadPlayer.getScoreboard().getTeam("rxRunners");
                    runners.addPlayer(player);
                }
            }
        }catch (Throwable throwable) {
            plugin.getLogs().error("Can't add players to Beast or Runners team, this error don't affect gameplay.");
            plugin.getLogs().error(throwable);
        }
    }

    public void addLobby(Player player) {
        try {
            for (Player pl : Bukkit.getOnlinePlayers()) {
                Team runners = pl.getScoreboard().getTeam("rxRunners");
                runners.addPlayer(player);
            }
        }catch (Throwable throwable){
            plugin.getLogs().error("Can't add players to Beast or Runners team, this error don't affect gameplay.");
            plugin.getLogs().error(throwable);
        }
    }

    public void addRunner(Player player) {
        try {
            for (Player pl : Bukkit.getOnlinePlayers()) {
                Team runners = pl.getScoreboard().getTeam("rxRunners");
                runners.addPlayer(player);
            }
        }catch (Throwable throwable) {
            plugin.getLogs().error("Can't add players to Beast or Runners team, this error don't affect gameplay.");
            plugin.getLogs().error(throwable);
        }
    }

    public void addBeast(Player player) {
        try {
            for (Player pl : Bukkit.getOnlinePlayers()) {
                Team beasts = pl.getScoreboard().getTeam("rxBeasts");
                beasts.addPlayer(player);
            }
        }catch (Throwable throwable){
            plugin.getLogs().error("Can't add players to Beast or Runners team, this error don't affect gameplay.");
            plugin.getLogs().error(throwable);
        }
    }
}
