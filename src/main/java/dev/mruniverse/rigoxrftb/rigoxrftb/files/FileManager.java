package dev.mruniverse.rigoxrftb.rigoxrftb.files;

import dev.mruniverse.rigoxrftb.rigoxrftb.RigoxRFTB;
import dev.mruniverse.rigoxrftb.rigoxrftb.enums.Files;
import dev.mruniverse.rigoxrftb.rigoxrftb.enums.SaveMode;
import dev.mruniverse.rigoxrftb.rigoxrftb.utils.Logger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private final RigoxRFTB plugin;
    private FileConfiguration rGames,rMySQL,rMenus,rItems,rMessages,rScoreboard,rSettings;
    private final File dataFolder;
    private File Games;
    private File MySQL;
    private File Menus;
    private File Items;
    private File Messages;
    private File Scoreboard;
    private File Settings;
    public FileManager(RigoxRFTB main) {
        plugin = main;
        dataFolder = main.getDataFolder();
    }
    public void loadFiles() {
        loadFolder(dataFolder,"Main Folder");
        Games = new File(dataFolder,"games.yml");
        MySQL = new File(dataFolder,"mysql.yml");
        Menus = new File(dataFolder, "menus.yml");
        Items = new File(dataFolder, "items.yml");
        Scoreboard = new File(dataFolder, "scoreboards.yml");
        Messages = new File(dataFolder,"messages.yml");
        Settings = new File(dataFolder,"settings.yml");
        loadFile(Games,"games.yml");
        loadFile(MySQL,"mysql.yml");
        loadFile(Menus,"menus.yml");
        loadFile(Items,"items.yml");
        loadFile(Scoreboard,"scoreboards.yml");
        loadFile(Settings,"settings.yml");
        loadFile(Messages,"messages.yml");
    }
    private void loadFile(File fileToLoad,String fileName) {
        boolean result = false;
        if(!fileToLoad.exists()) {
            try {
                result = fileToLoad.createNewFile();
            } catch (IOException exception) {
                Logger.info("The plugin can't load or save configuration files!");
            }
            if(result) {
                Logger.info("File: &b" + fileName + "&f created!");
            }
        }
    }
    public void loadFolder(File folderToLoad, String folderName) {
        boolean result = false;
        if(!folderToLoad.exists()) result = folderToLoad.mkdir();
        if(result) {
            Logger.info("Folder: &b" + folderName + "&f created!");
        }
    }
    public void loadConfiguration() {
        addConfig(Files.SETTINGS,"settings.update-check",true);
        addConfig(Files.SETTINGS,"settings.maxTime",500);
        addConfig(Files.SETTINGS,"settings.lobbyLocation","notSet");
        addConfig(Files.SETTINGS,"settings.options.joinLobbyTeleport",false);
        addConfig(Files.SETTINGS,"settings.lobbyScoreboard-only-in-lobby-world",true);
        addConfig(Files.SETTINGS,"settings.options.pluginChat",true);
        addConfig(Files.SETTINGS,"settings.options.PerWorldTab",true);
        addConfig(Files.SETTINGS,"settings.options.PerWorldChat",true);
        addConfig(Files.SETTINGS,"settings.pointSystem.onRunnerDeath",-4);
        addConfig(Files.SETTINGS,"settings.pointSystem.onBeastDeath",-4);
        addConfig(Files.SETTINGS,"settings.pointSystem.onRunnersWin",8);
        addConfig(Files.SETTINGS,"settings.pointSystem.onBeastWin",4);
        addConfig(Files.SETTINGS,"settings.pointSystem.onKillBeast",4);
        addConfig(Files.SETTINGS,"settings.pointSystem.onBeastKill",1);
        List<String> lists = new ArrayList<>();
        lists.add("/leave");
        lists.add("/quit");
        lists.add("/salir");
        addConfig(Files.SETTINGS,"settings.leaveCMDs",lists);
    }
    public void addConfig(Files fileToAdd,String path,Object value) {
        switch(fileToAdd) {
            case MESSAGES:
                if(!getControl(Files.MESSAGES).contains(path)) {
                    getControl(Files.MESSAGES).set(path,value);
                }
            case ITEMS:
                if(!getControl(Files.ITEMS).contains(path)) {
                    getControl(Files.ITEMS).set(path,value);
                }
            case MYSQL:
                if(!getControl(Files.MYSQL).contains(path)) {
                    getControl(Files.MYSQL).set(path, value);
                }
            case GAMES:
                if(!getControl(Files.GAMES).contains(path)) {
                    getControl(Files.GAMES).set(path,value);
                }
            case MENUS:
                if(!getControl(Files.MENUS).contains(path)) {
                    getControl(Files.MENUS).set(path,value);
                }
            case SCOREBOARD:
                if(!getControl(Files.SCOREBOARD).contains(path)) {
                    getControl(Files.SCOREBOARD).set(path,value);
                }
            default:
                if(!getControl(Files.SETTINGS).contains(path)) {
                    getControl(Files.SETTINGS).set(path,value);
                }
        }
    }
    public FileConfiguration getControl(Files fileToGet) {
        switch (fileToGet) {
            case GAMES:
                if(rGames == null) reloadFile(SaveMode.GAMES_FILES);
                return rGames;
            case MYSQL:
                if(rMySQL == null) reloadFile(SaveMode.MYSQL);
                return rMySQL;
            case MENUS:
                if(rMenus == null) reloadFile(SaveMode.MENUS);
                return rMenus;
            case ITEMS:
                if(rItems == null) reloadFile(SaveMode.ITEMS);
                return rItems;
            case MESSAGES:
                if(rMessages == null) reloadFile(SaveMode.MESSAGES);
                return rMessages;
            case SCOREBOARD:
                if(rScoreboard == null) reloadFile(SaveMode.SCOREBOARDS);
                return rScoreboard;
            default:
                if(rSettings == null) reloadFile(SaveMode.SETTINGS);
                return rSettings;
        }
    }
    public void reloadFile(SaveMode Mode) {
        loadFiles();
        if(Mode.equals(SaveMode.MESSAGES) || Mode.equals(SaveMode.ALL)) {
            rMessages = YamlConfiguration.loadConfiguration(Messages);
        }
        if(Mode.equals(SaveMode.ITEMS) || Mode.equals(SaveMode.ALL)) {
            rItems = YamlConfiguration.loadConfiguration(Items);
        }
        if(Mode.equals(SaveMode.MENUS) || Mode.equals(SaveMode.ALL)) {
            rMenus = YamlConfiguration.loadConfiguration(Menus);
        }
        if(Mode.equals(SaveMode.SETTINGS) || Mode.equals(SaveMode.ALL)) {
            rSettings = YamlConfiguration.loadConfiguration(Settings);
        }
        if(Mode.equals(SaveMode.SCOREBOARDS) || Mode.equals(SaveMode.ALL)) {
            rScoreboard = YamlConfiguration.loadConfiguration(Scoreboard);
        }
        if(Mode.equals(SaveMode.MYSQL) || Mode.equals(SaveMode.ALL)) {
            rMySQL = YamlConfiguration.loadConfiguration(MySQL);
        }
        if(Mode.equals(SaveMode.GAMES_FILES) || Mode.equals(SaveMode.ALL)) {
            rGames = YamlConfiguration.loadConfiguration(Games);
        }
    }
    public void save(SaveMode Mode) {
        try {
            if(Mode.equals(SaveMode.GAMES_FILES) || Mode.equals(SaveMode.ALL)) {
                getControl(Files.GAMES).save(Games);
            }
            if(Mode.equals(SaveMode.MYSQL) || Mode.equals(SaveMode.ALL)) {
                getControl(Files.MYSQL).save(MySQL);
            }
            if(Mode.equals(SaveMode.SCOREBOARDS) || Mode.equals(SaveMode.ALL)) {
                getControl(Files.SCOREBOARD).save(Scoreboard);
            }
            if(Mode.equals(SaveMode.ITEMS) || Mode.equals(SaveMode.ALL)) {
                getControl(Files.ITEMS).save(Items);
            }
            if(Mode.equals(SaveMode.MESSAGES) || Mode.equals(SaveMode.ALL)) {
                getControl(Files.MESSAGES).save(Messages);
            }
            if(Mode.equals(SaveMode.SETTINGS) || Mode.equals(SaveMode.ALL)) {
                getControl(Files.SETTINGS).save(Settings);
            }
        } catch(IOException exception) {
            Logger.info("The plugin can't load or save configuration files! (Spigot Control Issue - Caused by: One plugin is using bad the <getControl() from FileManager.class>)");
        }
    }

}
