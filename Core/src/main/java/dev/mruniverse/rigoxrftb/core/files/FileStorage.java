package dev.mruniverse.rigoxrftb.core.files;

import dev.mruniverse.rigoxrftb.core.RigoxRFTB;
import dev.mruniverse.rigoxrftb.core.enums.RigoxFiles;
import dev.mruniverse.rigoxrftb.core.enums.SaveMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;


public class FileStorage {
    private final RigoxRFTB plugin;
    private FileConfiguration settings,messages,mysql,data,menus,items,games,boards,chests;
    private final File Settings;
    private final File Messages;
    private final File MySQL;
    private final File Data;
    private final File Menus;
    private final File Items;
    private final File Games;
    private final File Boards;
    private final File Chests;
    public FileStorage(RigoxRFTB main) {
        plugin = main;
        Settings = new File(main.getDataFolder(), "settings.yml");
        Messages = new File(main.getDataFolder(), "messages.yml");
        MySQL = new File(main.getDataFolder(), "mysql.yml");
        Data = new File(main.getDataFolder(), "data.yml");
        Menus = new File(main.getDataFolder(), "menus.yml");
        Items = new File(main.getDataFolder(), "items.yml");
        Games = new File(main.getDataFolder(), "games.yml");
        Boards = new File(main.getDataFolder(), "scoreboards.yml");
        Chests = new File(main.getDataFolder(), "chests.yml");
    }

    public File getFile(RigoxFiles fileToGet) {
        switch (fileToGet) {
            case CHESTS:
                return Chests;
            case ITEMS:
                return Items;
            case DATA:
                return Data;
            case GAMES:
                return Games;
            case MENUS:
                return Menus;
            case SCOREBOARD:
                return Boards;
            case MYSQL:
                return MySQL;
            case MESSAGES:
                return Messages;
            case SETTINGS:
            default:
                return Settings;
        }
    }

    /**
     * Creates a config File if it doesn't exists,
     * reloads if specified file exists.
     *
     * @param configName config to create/reload.
     */
    public FileConfiguration loadConfig(String configName) {
        File configFile = new File(plugin.getDataFolder(), configName + ".yml");

        if (!configFile.exists()) {
            saveConfig(configName);
        }

        FileConfiguration cnf = null;
        try {
            cnf = YamlConfiguration.loadConfiguration(configFile);
        } catch (Exception e) {
            plugin.getLogs().warn(String.format("A error occurred while loading the settings file. Error: %s", e));
            e.printStackTrace();
        }

        plugin.getLogs().info(String.format("&7File &e%s.yml &7has been loaded", configName));
        return cnf;
    }

    /**
     * Reload plugin file(s).
     *
     * @param Mode mode of reload.
     */
    public void reloadFile(SaveMode Mode) {
        switch (Mode) {
            case CHESTS:
                chests = YamlConfiguration.loadConfiguration(Chests);
                break;
            case ITEMS:
                items = YamlConfiguration.loadConfiguration(Items);
                break;
            case DATA:
                data = YamlConfiguration.loadConfiguration(Data);
                break;
            case MENUS:
                menus = YamlConfiguration.loadConfiguration(Menus);
                break;
            case MESSAGES:
                messages = YamlConfiguration.loadConfiguration(Messages);
                break;
            case MYSQL:
                mysql = YamlConfiguration.loadConfiguration(MySQL);
                break;
            case SETTINGS:
                settings = YamlConfiguration.loadConfiguration(Settings);
                break;
            case SCOREBOARDS:
                boards = YamlConfiguration.loadConfiguration(Boards);
                break;
            case GAMES_FILES:
                games = YamlConfiguration.loadConfiguration(Games);
                break;
            case ALL:
            default:
                messages = YamlConfiguration.loadConfiguration(Messages);
                data = YamlConfiguration.loadConfiguration(Data);
                items = YamlConfiguration.loadConfiguration(Items);
                chests = YamlConfiguration.loadConfiguration(Chests);
                menus = YamlConfiguration.loadConfiguration(Menus);
                mysql = YamlConfiguration.loadConfiguration(MySQL);
                settings = YamlConfiguration.loadConfiguration(Settings);
                boards = YamlConfiguration.loadConfiguration(Boards);
                games = YamlConfiguration.loadConfiguration(Games);
                break;
        }
    }

    /**
     * Save config File using FileStorage
     *
     * @param fileToSave config to save/create.
     */
    public void save(RigoxFiles fileToSave) {
        switch (fileToSave) {
            case CHESTS:
                saveConfig("chests");
            case ITEMS:
                saveConfig("items");
            case DATA:
                saveConfig("data");
            case GAMES:
                saveConfig("games");
            case MENUS:
                saveConfig("menus");
            case SCOREBOARD:
                saveConfig("scoreboards");
            case MYSQL:
                saveConfig("mysql");
            case MESSAGES:
                saveConfig("messages");
            case SETTINGS:
            default:
                saveConfig("settings");
        }
    }
    /**
     * Save config File Changes & Paths
     *
     * @param configName config to save/create.
     */
    public void saveConfig(String configName) {
        File folderDir = plugin.getDataFolder();
        File file = new File(plugin.getDataFolder(), configName + ".yml");

        if (!folderDir.exists()) {
            boolean createFile = folderDir.mkdir();
            if(createFile) plugin.getLogs().info("&fFile &3" + configName + ".yml &fcreated!");
        }

        if (!file.exists()) {
            try (InputStream in = plugin.getResource(configName + ".yml")) {
                if(in != null) {
                    Files.copy(in, file.toPath());
                }
            } catch (IOException e) {
                plugin.getLogs().warn(String.format("A error occurred while copying the config %s to the plugin data folder. Error: %s", configName, e));
                e.printStackTrace();
            }
        }
    }

    /**
     * Control a file, getControl() will return a FileConfiguration.
     *
     * @param fileToControl config to control.
     */
    public FileConfiguration getControl(RigoxFiles fileToControl) {
        switch (fileToControl) {
            case CHESTS:
                if(chests == null) items = loadConfig("chests");
                return chests;
            case ITEMS:
                if(items == null) items = loadConfig("items");
                return items;
            case DATA:
                if(data == null) data = loadConfig("data");
                return data;
            case GAMES:
                if(games == null) games = loadConfig("games");
                return games;
            case MENUS:
                if(menus == null) menus = loadConfig("menus");
                return menus;
            case SCOREBOARD:
                if(boards == null) boards = loadConfig("scoreboards");
                return boards;
            case MYSQL:
                if(mysql == null) mysql = loadConfig("mysql");
                return mysql;
            case MESSAGES:
                if(messages == null) messages = loadConfig("messages");
                return messages;
            case SETTINGS:
            default:
                if(settings == null) settings = loadConfig("settings");
                return settings;
        }
    }

}
