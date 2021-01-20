package dev.mruniverse.rigoxrftb.rigoxrftb.listeners;

import dev.mruniverse.rigoxrftb.rigoxrftb.RigoxRFTB;
import dev.mruniverse.rigoxrftb.rigoxrftb.enums.Files;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class PlayerListeners implements Listener {
    private final RigoxRFTB plugin;
    public PlayerListeners(RigoxRFTB main) {
        plugin = main;
        main.getLogs().info("PlayerListener registered!");
    }
    @SuppressWarnings("deprecation")
    @EventHandler
    public void joinScoreboard(PlayerJoinEvent event) {
        try {
            FileConfiguration file = plugin.getFiles().getControl(Files.SETTINGS);
            if (file.getBoolean("settings.lobbyScoreboard-only-in-lobby-world")) {
                if (file.getString("settings.lobbyLocation").equalsIgnoreCase("notSet")) {
                    plugin.getLogs().error("-----------------------------");
                    plugin.getLogs().error("Can't show lobby-scoreboard, lobby location is not set");
                    plugin.getLogs().error("-----------------------------");
                } else {
                    String[] loc = file.getString("settings.lobbyLocation").split(",");
                    World w = Bukkit.getWorld(loc[0]);
                    if (event.getPlayer().getWorld().equals(w)) {
                        if(plugin.getFiles().getControl(Files.SCOREBOARD).getBoolean("scoreboards.lobby.toggle")) {
                            Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
                            Objective o = sb.registerNewObjective("RigoxRFTB", "Lobby");
                            o.setDisplaySlot(DisplaySlot.SIDEBAR);
                            event.getPlayer().setScoreboard(sb);
                            plugin.getUtils().lobbyBoard(event.getPlayer());
                        }
                    }
                }
            } else {
                if(plugin.getFiles().getControl(Files.SCOREBOARD).getBoolean("scoreboards.lobby.toggle")) {
                    Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
                    Objective o = sb.registerNewObjective("RigoxRFTB", "Lobby");
                    o.setDisplaySlot(DisplaySlot.SIDEBAR);
                    event.getPlayer().setScoreboard(sb);
                    plugin.getUtils().lobbyBoard(event.getPlayer());
                }
            }
        } catch (Throwable throwable) {
            plugin.getLogs().error("Can't generate lobby scoreboard for " + event.getPlayer().getName() +"!");
            plugin.getLogs().error("-------------------------");
            plugin.getLogs().error("Class: " + throwable.getClass().getName() +".class");
            if(throwable.getStackTrace() != null) {
                plugin.getLogs().error("StackTrace: ");
                for(StackTraceElement line : throwable.getStackTrace()) {
                    plugin.getLogs().error("(" + line.getLineNumber() + ") " + line.toString());
                }
            }
            plugin.getLogs().error("-------------------------");
        }
    }
    @EventHandler
    public void joinTeleport(PlayerJoinEvent event) {
        try {
            FileConfiguration file = plugin.getFiles().getControl(Files.SETTINGS);
            if (file.getBoolean("settings.options.joinLobbyTeleport")) {
                if (file.getString("settings.lobbyLocation").equalsIgnoreCase("notSet")) {
                    plugin.getLogs().error("-----------------------------");
                    plugin.getLogs().error("Can't teleport player to lobby location, lobby location is not set");
                    plugin.getLogs().error("-----------------------------");
                } else {
                    String[] loc = file.getString("settings.lobbyLocation").split(",");
                    World w = Bukkit.getWorld(loc[0]);
                    double x = Double.parseDouble(loc[1]);
                    double y = Double.parseDouble(loc[2]);
                    double z = Double.parseDouble(loc[3]);
                    float yaw = Float.parseFloat(loc[4]);
                    float pitch = Float.parseFloat(loc[5]);
                    Location location = new Location(w, x, y, z, yaw, pitch);
                    event.getPlayer().teleport(location);
                    if(plugin.getFiles().getControl(Files.SCOREBOARD).getBoolean("scoreboards.lobby.toggle")) {
                        Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
                        Objective o = sb.registerNewObjective("RigoxRFTB", "Lobby");
                        o.setDisplaySlot(DisplaySlot.SIDEBAR);
                        event.getPlayer().setScoreboard(sb);
                        plugin.getUtils().lobbyBoard(event.getPlayer());
                    }
                }
            }
        } catch (Throwable throwable) {
            plugin.getLogs().error("Can't teleport " + event.getPlayer().getName() +" to the lobby!");
            plugin.getLogs().error("-------------------------");
            plugin.getLogs().error("Class: " + throwable.getClass().getName() +".class");
            if(throwable.getStackTrace() != null) {
                plugin.getLogs().error("StackTrace: ");
                for(StackTraceElement line : throwable.getStackTrace()) {
                    plugin.getLogs().error("(" + line.getLineNumber() + ") " + line.toString());
                }
            }
            plugin.getLogs().error("-------------------------");
        }
    }
}
