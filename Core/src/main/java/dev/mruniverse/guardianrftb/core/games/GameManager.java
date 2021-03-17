package dev.mruniverse.guardianrftb.core.games;

import dev.mruniverse.guardianrftb.core.GuardianRFTB;
import dev.mruniverse.guardianrftb.core.enums.GuardianFiles;
import dev.mruniverse.guardianrftb.core.enums.SaveMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class GameManager {
    private final ArrayList<Game> games = new ArrayList<>();
    private final HashMap<World,Game> gamesWorlds = new HashMap<>();
    public HashMap<String,GameChests> gameChests = new HashMap<>();
    public HashMap<GameType,GameMenu> gameMenu = new HashMap<>();
    private final GameMainMenu gameMainMenu;
    private final GuardianRFTB plugin;
    public GameManager(GuardianRFTB plugin) {
        this.plugin = plugin;
        gameMainMenu = new GameMainMenu(plugin);
    }
    public void loadChests() {
        ConfigurationSection section = plugin.getStorage().getControl(GuardianFiles.CHESTS).getConfigurationSection("chests");
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
            if(plugin.getStorage().getControl(GuardianFiles.GAMES).contains("games")) {
                for (String gameName : Objects.requireNonNull(plugin.getStorage().getControl(GuardianFiles.GAMES).getConfigurationSection("games")).getKeys(false)) {
                    if(plugin.getStorage().getControl(GuardianFiles.GAMES).getBoolean("games." + gameName + ".enabled")) {
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
            gameMenu.put(GameType.CLASSIC,new GameMenu(plugin,GameType.CLASSIC));
            gameMenu.put(GameType.DOUBLE_BEAST,new GameMenu(plugin,GameType.DOUBLE_BEAST));
            gameMenu.put(GameType.INFECTED,new GameMenu(plugin,GameType.INFECTED));
            gameMenu.put(GameType.KILLER,new GameMenu(plugin,GameType.KILLER));
            gameMenu.put(GameType.ISLAND_OF_THE_BEAST,new GameMenu(plugin,GameType.ISLAND_OF_THE_BEAST));
            gameMenu.put(GameType.ISLAND_OF_THE_BEAST_DOUBLE_BEAST,new GameMenu(plugin,GameType.ISLAND_OF_THE_BEAST_DOUBLE_BEAST));
            gameMenu.put(GameType.ISLAND_OF_THE_BEAST_KILLER,new GameMenu(plugin,GameType.ISLAND_OF_THE_BEAST_KILLER));
            //gameMenu = new GameMenu(plugin);
            loadGameWorlds();
        }catch (Throwable throwable) {
            plugin.getLogs().error("Can't load games plugin games :(");
            plugin.getLogs().error(throwable);
        }
    }
    public void loadGameWorlds() {
        for(Game game : getGames()) {
            gamesWorlds.put(game.runnersLocation.getWorld(),game);
        }
    }
    public GameMainMenu getGameMainMenu() { return gameMainMenu; }
    public GameMenu getGameMenu(GameType gameType) {
        return gameMenu.get(gameType);
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
    public HashMap<World,Game> getGameWorlds() { return gamesWorlds; }

    public Game getGame(Player player) {
        return plugin.getPlayerData(player.getUniqueId()).getGame();
    }

    public boolean existGame(String name) {
        return (getGame(name) != null);
    }

    //public boolean isPlaying(Player player) {
    //    return (getGame(player) != null);
    //}

    public void joinGame(Player player,String gameName) {
        if(!existGame(gameName)) {
            plugin.getUtils().sendMessage(player, Objects.requireNonNull(plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.admin.arenaError")).replace("%arena_id%",gameName));
        }
        Game game = getGame(gameName);
        game.join(player);
    }
    public void createGameFiles(String gameName) {
        FileConfiguration gameFiles = plugin.getStorage().getControl(GuardianFiles.GAMES);
        gameFiles.set("games." + gameName + ".enabled", false);
        gameFiles.set("games." + gameName + ".time", 500);
        gameFiles.set("games." + gameName + ".disableRain", true);
        gameFiles.set("games." + gameName + ".max", 10);
        gameFiles.set("games." + gameName + ".min", 2);
        gameFiles.set("games." + gameName + ".worldTime", 0);
        gameFiles.set("games." + gameName + ".gameType","CLASSIC");
        gameFiles.set("games." + gameName + ".gameSound1",plugin.getStorage().getControl(GuardianFiles.SETTINGS).getString("settings.defaultSounds.sound1"));
        gameFiles.set("games." + gameName + ".gameSound2",plugin.getStorage().getControl(GuardianFiles.SETTINGS).getString("settings.defaultSounds.sound2"));
        gameFiles.set("games." + gameName + ".gameSound3",plugin.getStorage().getControl(GuardianFiles.SETTINGS).getString("settings.defaultSounds.sound3"));
        gameFiles.set("games." + gameName + ".locations.waiting", "notSet");
        gameFiles.set("games." + gameName + ".locations.selected-beast", "notSet");
        gameFiles.set("games." + gameName + ".locations.beast", "notSet");
        gameFiles.set("games." + gameName + ".locations.runners", "notSet");
        gameFiles.set("games." + gameName + ".signs", new ArrayList<>());
        plugin.getStorage().save(SaveMode.GAMES_FILES);
    }
    public void setWaiting(String gameName, Location location) {
        try {
            String gameLoc = Objects.requireNonNull(location.getWorld()).getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ() + "," + location.getYaw() + "," + location.getPitch();
            plugin.getStorage().getControl(GuardianFiles.GAMES).set("games." + gameName + ".locations.waiting", gameLoc);
            plugin.getStorage().save(SaveMode.GAMES_FILES);
        }catch (Throwable throwable) {
            plugin.getLogs().error("Can't set waiting lobby for game: " + gameName);
            plugin.getLogs().error(throwable);
        }
    }
    public void setSelectedBeast(String gameName, Location location) {
        try {
            String gameLoc = Objects.requireNonNull(location.getWorld()).getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ() + "," + location.getYaw() + "," + location.getPitch();
            plugin.getStorage().getControl(GuardianFiles.GAMES).set("games." + gameName + ".locations.selected-beast", gameLoc);
            plugin.getStorage().save(SaveMode.GAMES_FILES);
        }catch (Throwable throwable) {
            plugin.getLogs().error("Can't set selected beast location for game: " + gameName);
            plugin.getLogs().error(throwable);
        }
    }
    public void setBeast(String gameName, Location location) {
        try {
            String gameLoc = Objects.requireNonNull(location.getWorld()).getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ() + "," + location.getYaw() + "," + location.getPitch();
            plugin.getStorage().getControl(GuardianFiles.GAMES).set("games." + gameName + ".locations.beast", gameLoc);
            plugin.getStorage().save(SaveMode.GAMES_FILES);
        }catch (Throwable throwable) {
            plugin.getLogs().error("Can't set beast spawn location for game: " + gameName);
            plugin.getLogs().error(throwable);
        }
    }
    public void setRunners(String gameName, Location location) {
        try {
            String gameLoc = Objects.requireNonNull(location.getWorld()).getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ() + "," + location.getYaw() + "," + location.getPitch();
            plugin.getStorage().getControl(GuardianFiles.GAMES).set("games." + gameName + ".locations.runners", gameLoc);
            plugin.getStorage().save(SaveMode.GAMES_FILES);
        }catch (Throwable throwable) {
            plugin.getLogs().error("Can't set runners spawn location for game: " + gameName);
            plugin.getLogs().error(throwable);
        }
    }
    public void setMax(String gameName,Integer max) {
        plugin.getStorage().getControl(GuardianFiles.GAMES).set("games." + gameName + ".max", max);
        plugin.getStorage().save(SaveMode.GAMES_FILES);
    }
    public void setMin(String gameName,Integer min) {
        plugin.getStorage().getControl(GuardianFiles.GAMES).set("games." + gameName + ".min", min);
        plugin.getStorage().save(SaveMode.GAMES_FILES);
    }
    public void setMode(String gameName,GameType type) {
        plugin.getStorage().getControl(GuardianFiles.GAMES).set("games." + gameName + ".gameType", type.toString().toUpperCase());
        plugin.getStorage().save(SaveMode.GAMES_FILES);
    }






}
