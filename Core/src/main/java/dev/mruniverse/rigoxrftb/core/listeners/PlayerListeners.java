package dev.mruniverse.rigoxrftb.core.listeners;

import dev.mruniverse.rigoxrftb.core.enums.Files;
import dev.mruniverse.rigoxrftb.core.enums.RigoxBoard;
import dev.mruniverse.rigoxrftb.core.RigoxRFTB;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListeners implements Listener {
    private final RigoxRFTB plugin;
    public PlayerListeners(RigoxRFTB main) {
        plugin = main;
        main.getLogs().info("PlayerListener registered!");
    }
    @EventHandler
    public void joinOptions(PlayerJoinEvent event) {
        plugin.addPlayer(event.getPlayer());
        FileConfiguration file = plugin.getFiles().getControl(Files.SETTINGS);
        Player player = event.getPlayer();
        if(file.getBoolean("settings.options.hideServerJoinMessage")) {
            event.setJoinMessage(null);
        }
        if(file.getBoolean("settings.options.joinHeal")) {
            player.setHealth(20.0D);
            player.setFoodLevel(20);
            player.setLevel(0);
            player.setExp(0.0F);
        }
        if(file.getBoolean("settings.options.joinAdventureGamemode")) {
            player.setGameMode(GameMode.ADVENTURE);
        }

    }
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
                            plugin.getScoreboards().setScoreboard(RigoxBoard.LOBBY,event.getPlayer());
                        }
                    }
                }
            } else {
                if(plugin.getFiles().getControl(Files.SCOREBOARD).getBoolean("scoreboards.lobby.toggle")) {
                    plugin.getScoreboards().setScoreboard(RigoxBoard.LOBBY,event.getPlayer());
                }
            }
        } catch (Throwable throwable) {
            plugin.getLogs().error("Can't generate lobby scoreboard for " + event.getPlayer().getName() +"!");
            plugin.getLogs().error(throwable);
        }
    }
    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        plugin.getScoreboards().removeScore(event.getPlayer());
        if(plugin.getFiles().getControl(Files.SETTINGS).getBoolean("settings.options.hideServerQuitMessage")) {
            event.setQuitMessage(null);
        }
        plugin.removePlayer(event.getPlayer());
    }
    @EventHandler
    public void lobbyDamage(EntityDamageEvent event) {
        if(event.getEntity().getType().equals(EntityType.PLAYER)) {
            if (!plugin.getFiles().getControl(Files.SETTINGS).getString("settings.lobbyLocation").equalsIgnoreCase("notSet")) {
                String[] loc = plugin.getFiles().getControl(Files.SETTINGS).getString("settings.lobbyLocation").split(",");
                World w = Bukkit.getWorld(loc[0]);
                if (event.getEntity().getWorld().equals(w)) {
                    event.setCancelled(true);
                }
            }
        }
    }
    @EventHandler
    public void lobbyHunger(FoodLevelChangeEvent event) {
        if(event.getEntity().getType().equals(EntityType.PLAYER)) {
            if (!plugin.getFiles().getControl(Files.SETTINGS).getString("settings.lobbyLocation").equalsIgnoreCase("notSet")) {
                String[] loc = plugin.getFiles().getControl(Files.SETTINGS).getString("settings.lobbyLocation").split(",");
                World w = Bukkit.getWorld(loc[0]);
                if (event.getEntity().getWorld().equals(w)) {
                    event.setFoodLevel(20);
                }
            }
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
                        plugin.getScoreboards().setScoreboard(RigoxBoard.LOBBY,event.getPlayer());
                    }
                    if(file.getBoolean("settings.options.joinHeal")) {
                        event.getPlayer().setHealth(20.0D);
                        event.getPlayer().setLevel(0);
                        event.getPlayer().setFoodLevel(20);
                        event.getPlayer().setExp(0.0F);
                    }
                    if(file.getBoolean("settings.options.lobby-actionBar")) {
                        plugin.getUtils().sendActionbar(event.getPlayer(),plugin.getFiles().getControl(Files.MESSAGES).getString("messages.lobby.actionBar"));
                    }
                    if(file.getBoolean("settings.options.joinAdventureGamemode")) {
                        event.getPlayer().setGameMode(GameMode.ADVENTURE);
                    }
                }
            }
        } catch (Throwable throwable) {
            plugin.getLogs().error("Can't teleport " + event.getPlayer().getName() +" to the lobby!");
            plugin.getLogs().error(throwable);
        }
    }
}