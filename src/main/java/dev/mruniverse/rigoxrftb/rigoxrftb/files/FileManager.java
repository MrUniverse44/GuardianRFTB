package dev.mruniverse.rigoxrftb.rigoxrftb.files;

import dev.mruniverse.rigoxrftb.rigoxrftb.RigoxRFTB;
import dev.mruniverse.rigoxrftb.rigoxrftb.enums.Files;
import dev.mruniverse.rigoxrftb.rigoxrftb.enums.SaveMode;
import dev.mruniverse.rigoxrftb.rigoxrftb.utils.Logger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class FileManager {
    private final RigoxRFTB plugin;
    private FileConfiguration rGames,rMySQL,rMenus,rItems,rMessages,rScoreboard,rSettings;
    private File dataFolder,Games,MySQL,Menus,Items,Messages,Scoreboard,Settings;
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

}
