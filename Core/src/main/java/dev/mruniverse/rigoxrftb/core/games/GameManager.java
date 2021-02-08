package dev.mruniverse.rigoxrftb.core.games;

import dev.mruniverse.rigoxrftb.core.RigoxRFTB;
import dev.mruniverse.rigoxrftb.core.enums.RigoxFiles;
import dev.mruniverse.rigoxrftb.core.enums.SaveMode;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class GameManager {
    private final ArrayList<Game> games = new ArrayList<>();
    public HashMap<String,GameChests> gameChests = new HashMap<>();
    public GameMenu gameMenu;
    private final RigoxRFTB plugin;
    public GameManager(RigoxRFTB main) {
        plugin = main;
    }
    public void loadChests() {
        ConfigurationSection section = plugin.getFiles().getControl(RigoxFiles.CHESTS).getConfigurationSection("chests");
        if(section == null) return;
        for(String chest : section.getKeys(false)) {
            gameChests.put(chest,new GameChests(plugin,chest));
        }
    }
    public GameChests getGameChest(String chestName) {
        return gameChests.get(chestName);
    }
    public Game getGame(String gameName) {
        if (this.games.size() < 1)
            return null;
        for (Game game : this.games) {
            if (game.getName().equalsIgnoreCase(gameName))
                return game;
        }
        return null;
    }

    public void loadGames() {
        try {
            if(plugin.getFiles().getControl(RigoxFiles.GAMES).contains("games")) {
                for (String gameName : plugin.getFiles().getControl(RigoxFiles.GAMES).getConfigurationSection("games").getKeys(false)) {
                    if(plugin.getFiles().getControl(RigoxFiles.GAMES).getBoolean("games." + gameName + ".enabled")) {
                        Game game = new Game(plugin, gameName);
                        this.games.add(game);
                        plugin.getLogs().debug("Game " + gameName + " loaded!");
                    } else {
                        plugin.getLogs().debug("Game " + gameName + " is not enabled in games.yml, this game wasn't loaded.");
                    }
                }
                plugin.getLogs().info(this.games.size() + " game(s) loaded!");
            } else {
                plugin.getLogs().info("You don't have games created yet.");
            }
            plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin,new GameRunnable(plugin),0L,20L);
            gameMenu = new GameMenu(plugin);
        }catch (Throwable throwable) {
            plugin.getLogs().error("Can't load games plugin games :(");
            plugin.getLogs().error(throwable);
        }
    }
    public GameMenu getGameMenu() {
        return gameMenu;
    }
    public void addGame(String gameName) {
        if(getGame(gameName) != null) {
            return;
        }
        Game game = new Game(plugin,gameName);
        this.games.add(game);
        plugin.getLogs().debug("Game " + gameName + " loaded!");
    }
    public void delGame(String gameName) {
        if(getGame(gameName) != null) {
            this.games.remove(getGame(gameName));
        }
        plugin.getLogs().debug("Game " + gameName + " unloaded!");
    }
    public ArrayList<Game> getGames() {
        return games;
    }

    public Game getGame(Player player) {
        return plugin.getPlayerData(player.getUniqueId()).getGame();
    }

    public boolean existGame(String name) {
        return (getGame(name) != null);
    }

    public boolean isPlaying(Player player) {
        return (getGame(player) != null);
    }

    public void joinGame(Player player,String gameName) {
        if(!existGame(gameName)) {
            plugin.getUtils().sendMessage(player, Objects.requireNonNull(plugin.getFiles().getControl(RigoxFiles.MESSAGES).getString("messages.admin.arenaError")).replace("%arena_id%",gameName));
        }
        Game game = getGame(gameName);
        game.join(player);
    }
    public void createGameFiles(String gameName) {
        FileConfiguration gameFiles = plugin.getFiles().getControl(RigoxFiles.GAMES);
        gameFiles.set("games." + gameName + ".enabled", false);
        gameFiles.set("games." + gameName + ".time", 500);
        gameFiles.set("games." + gameName + ".max", 10);
        gameFiles.set("games." + gameName + ".min", 2);
        gameFiles.set("games." + gameName + ".worldTime", 0);
        gameFiles.set("games." + gameName + ".gameType","CLASSIC");
        gameFiles.set("games." + gameName + ".gameSound1",plugin.getFiles().getControl(RigoxFiles.SETTINGS).getString("settings.defaultSounds.sound1"));
        gameFiles.set("games." + gameName + ".gameSound2",plugin.getFiles().getControl(RigoxFiles.SETTINGS).getString("settings.defaultSounds.sound2"));
        gameFiles.set("games." + gameName + ".gameSound3",plugin.getFiles().getControl(RigoxFiles.SETTINGS).getString("settings.defaultSounds.sound3"));
        gameFiles.set("games." + gameName + ".locations.waiting", "notSet");
        gameFiles.set("games." + gameName + ".locations.selected-beast", "notSet");
        gameFiles.set("games." + gameName + ".locations.beast", "notSet");
        gameFiles.set("games." + gameName + ".locations.runners", "notSet");
        gameFiles.set("games." + gameName + ".signs", new ArrayList<>());
        plugin.getFiles().save(SaveMode.GAMES_FILES);
    }
    public void setWaiting(String gameName, Location location) {
        try {
            String gameLoc = location.getWorld().getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ() + "," + location.getYaw() + "," + location.getPitch();
            plugin.getFiles().getControl(RigoxFiles.GAMES).set("games." + gameName + ".locations.waiting", gameLoc);
            plugin.getFiles().save(SaveMode.GAMES_FILES);
        }catch (Throwable throwable) {
            plugin.getLogs().error("Can't set waiting lobby for game: " + gameName);
            plugin.getLogs().error(throwable);
        }
    }
    public void setSelectedBeast(String gameName, Location location) {
        try {
            String gameLoc = location.getWorld().getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ() + "," + location.getYaw() + "," + location.getPitch();
            plugin.getFiles().getControl(RigoxFiles.GAMES).set("games." + gameName + ".locations.selected-beast", gameLoc);
            plugin.getFiles().save(SaveMode.GAMES_FILES);
        }catch (Throwable throwable) {
            plugin.getLogs().error("Can't set selected beast location for game: " + gameName);
            plugin.getLogs().error(throwable);
        }
    }
    public void setBeast(String gameName, Location location) {
        try {
            String gameLoc = location.getWorld().getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ() + "," + location.getYaw() + "," + location.getPitch();
            plugin.getFiles().getControl(RigoxFiles.GAMES).set("games." + gameName + ".locations.beast", gameLoc);
            plugin.getFiles().save(SaveMode.GAMES_FILES);
        }catch (Throwable throwable) {
            plugin.getLogs().error("Can't set beast spawn location for game: " + gameName);
            plugin.getLogs().error(throwable);
        }
    }
    public void setRunners(String gameName, Location location) {
        try {
            String gameLoc = location.getWorld().getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ() + "," + location.getYaw() + "," + location.getPitch();
            plugin.getFiles().getControl(RigoxFiles.GAMES).set("games." + gameName + ".locations.runners", gameLoc);
            plugin.getFiles().save(SaveMode.GAMES_FILES);
        }catch (Throwable throwable) {
            plugin.getLogs().error("Can't set runners spawn location for game: " + gameName);
            plugin.getLogs().error(throwable);
        }
    }
    public void setMax(String gameName,Integer max) {
        plugin.getFiles().getControl(RigoxFiles.GAMES).set("games." + gameName + ".max", max);
        plugin.getFiles().save(SaveMode.GAMES_FILES);
    }
    public void setMin(String gameName,Integer min) {
        plugin.getFiles().getControl(RigoxFiles.GAMES).set("games." + gameName + ".min", min);
        plugin.getFiles().save(SaveMode.GAMES_FILES);
    }






}
