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
        ConfigurationSection section = plugin.getStorage().getControl(RigoxFiles.CHESTS).getConfigurationSection("chests");
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
            if(plugin.getStorage().getControl(RigoxFiles.GAMES).contains("games")) {
                for (String gameName : plugin.getStorage().getControl(RigoxFiles.GAMES).getConfigurationSection("games").getKeys(false)) {
                    if(plugin.getStorage().getControl(RigoxFiles.GAMES).getBoolean("games." + gameName + ".enabled")) {
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
            plugin.getUtils().sendMessage(player, Objects.requireNonNull(plugin.getStorage().getControl(RigoxFiles.MESSAGES).getString("messages.admin.arenaError")).replace("%arena_id%",gameName));
        }
        Game game = getGame(gameName);
        game.join(player);
    }
    public void createGameFiles(String gameName) {
        FileConfiguration gameFiles = plugin.getStorage().getControl(RigoxFiles.GAMES);
        gameFiles.set("games." + gameName + ".enabled", false);
        gameFiles.set("games." + gameName + ".time", 500);
        gameFiles.set("games." + gameName + ".max", 10);
        gameFiles.set("games." + gameName + ".min", 2);
        gameFiles.set("games." + gameName + ".worldTime", 0);
        gameFiles.set("games." + gameName + ".gameType","CLASSIC");
        gameFiles.set("games." + gameName + ".gameSound1",plugin.getStorage().getControl(RigoxFiles.SETTINGS).getString("settings.defaultSounds.sound1"));
        gameFiles.set("games." + gameName + ".gameSound2",plugin.getStorage().getControl(RigoxFiles.SETTINGS).getString("settings.defaultSounds.sound2"));
        gameFiles.set("games." + gameName + ".gameSound3",plugin.getStorage().getControl(RigoxFiles.SETTINGS).getString("settings.defaultSounds.sound3"));
        gameFiles.set("games." + gameName + ".signs", new ArrayList<>());
        plugin.getStorage().save(SaveMode.GAMES_FILES);
    }
    public void setWaiting(String gameName, Location location) {
        try {
            plugin.getStorage().getControl(RigoxFiles.GAMES).set("games." + gameName + ".locations.waiting", location);
            plugin.getStorage().save(SaveMode.GAMES_FILES);
        }catch (Throwable throwable) {
            plugin.getLogs().error("Can't set waiting lobby for game: " + gameName);
            plugin.getLogs().error(throwable);
        }
    }
    public void setSelectedBeast(String gameName, Location location) {
        try {
            plugin.getStorage().getControl(RigoxFiles.GAMES).set("games." + gameName + ".locations.selected-beast", location);
            plugin.getStorage().save(SaveMode.GAMES_FILES);
        }catch (Throwable throwable) {
            plugin.getLogs().error("Can't set selected beast location for game: " + gameName);
            plugin.getLogs().error(throwable);
        }
    }
    public void setBeast(String gameName, Location location) {
        try {
            plugin.getStorage().getControl(RigoxFiles.GAMES).set("games." + gameName + ".locations.beast", location);
            plugin.getStorage().save(SaveMode.GAMES_FILES);
        }catch (Throwable throwable) {
            plugin.getLogs().error("Can't set beast spawn location for game: " + gameName);
            plugin.getLogs().error(throwable);
        }
    }
    public void setRunners(String gameName, Location location) {
        try {
            plugin.getStorage().getControl(RigoxFiles.GAMES).set("games." + gameName + ".locations.runners", location);
            plugin.getStorage().save(SaveMode.GAMES_FILES);
        }catch (Throwable throwable) {
            plugin.getLogs().error("Can't set runners spawn location for game: " + gameName);
            plugin.getLogs().error(throwable);
        }
    }
    public void setMax(String gameName,Integer max) {
        plugin.getStorage().getControl(RigoxFiles.GAMES).set("games." + gameName + ".max", max);
        plugin.getStorage().save(SaveMode.GAMES_FILES);
    }
    public void setMin(String gameName,Integer min) {
        plugin.getStorage().getControl(RigoxFiles.GAMES).set("games." + gameName + ".min", min);
        plugin.getStorage().save(SaveMode.GAMES_FILES);
    }
    public void setMode(String gameName,GameType type) {
        plugin.getStorage().getControl(RigoxFiles.GAMES).set("games." + gameName + ".gameType", type.toString().toUpperCase());
        plugin.getStorage().save(SaveMode.GAMES_FILES);
    }






}
