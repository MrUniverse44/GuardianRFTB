package dev.mruniverse.guardianrftb.core.files;

import dev.mruniverse.guardianrftb.core.GuardianRFTB;
import dev.mruniverse.guardianrftb.core.enums.GuardianFiles;
import dev.mruniverse.guardianrftb.core.enums.SaveMode;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;


public class FileStorage {
    private final GuardianRFTB plugin;
    private FileConfiguration settings,messages,mysql,data,menus,items,games,boards,chests,kits;
    private final File rxSettings;
    private final File rxMessages;
    private final File rxMySQL;
    private final File rxData;
    private final File rxMenus;
    private final File rxItems;
    private final File rxGames;
    private final File rxBoards;
    private final File rxChests;
    private final File rxKits;
    public FileStorage(GuardianRFTB main) {
        plugin = main;
        rxSettings = new File(main.getDataFolder(), "settings.yml");
        rxMessages = new File(main.getDataFolder(), "messages.yml");
        rxMySQL = new File(main.getDataFolder(), "mysql.yml");
        rxData = new File(main.getDataFolder(), "data.yml");
        rxMenus = new File(main.getDataFolder(), "menus.yml");
        rxItems = new File(main.getDataFolder(), "items.yml");
        rxGames = new File(main.getDataFolder(), "games.yml");
        rxBoards = new File(main.getDataFolder(), "scoreboards.yml");
        rxChests = new File(main.getDataFolder(), "chests.yml");
        rxKits = new File(main.getDataFolder(), "kits.yml");
        settings = loadConfig("settings");
        menus = loadConfig("menus");
        messages = loadConfig("messages");
        items = loadConfig("items");
        mysql = loadConfig("mysql");
        data = loadConfig("data");
        games = loadConfig("games");
        boards = loadConfig("scoreboards");
        chests = loadConfig("chests");
        kits = loadConfig("kits");
    }

    public File getFile(GuardianFiles fileToGet) {
        switch (fileToGet) {
            case CHESTS:
                return rxChests;
            case ITEMS:
                return rxItems;
            case DATA:
                return rxData;
            case GAMES:
                return rxGames;
            case MENUS:
                return rxMenus;
            case SCOREBOARD:
                return rxBoards;
            case MYSQL:
                return rxMySQL;
            case MESSAGES:
                return rxMessages;
            case KITS:
                return rxKits;
            case SETTINGS:
            default:
                return rxSettings;
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
     * Creates a config File if it doesn't exists,
     * reloads if specified file exists.
     *
     * @param rigoxFile config to create/reload.
     */
    public FileConfiguration loadConfig(File rigoxFile) {
        if (!rigoxFile.exists()) {
            saveConfig(rigoxFile);
        }

        FileConfiguration cnf = null;
        try {
            cnf = YamlConfiguration.loadConfiguration(rigoxFile);
        } catch (Exception e) {
            plugin.getLogs().warn(String.format("A error occurred while loading the settings file. Error: %s", e));
            e.printStackTrace();
        }

        plugin.getLogs().info(String.format("&7File &e%s &7has been loaded", rigoxFile.getName()));
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
                chests = YamlConfiguration.loadConfiguration(rxChests);
                break;
            case ITEMS:
                items = YamlConfiguration.loadConfiguration(rxItems);
                break;
            case DATA:
                data = YamlConfiguration.loadConfiguration(rxData);
                break;
            case MENUS:
                menus = YamlConfiguration.loadConfiguration(rxMenus);
                break;
            case MESSAGES:
                messages = YamlConfiguration.loadConfiguration(rxMessages);
                break;
            case KITS:
                kits = YamlConfiguration.loadConfiguration(rxKits);
                break;
            case MYSQL:
                mysql = YamlConfiguration.loadConfiguration(rxMySQL);
                break;
            case SETTINGS:
                settings = YamlConfiguration.loadConfiguration(rxSettings);
                break;
            case SCOREBOARDS:
                boards = YamlConfiguration.loadConfiguration(rxBoards);
                break;
            case GAMES_FILES:
                games = YamlConfiguration.loadConfiguration(rxGames);
                break;
            case ALL:
            default:
                messages = YamlConfiguration.loadConfiguration(rxMessages);
                data = YamlConfiguration.loadConfiguration(rxData);
                items = YamlConfiguration.loadConfiguration(rxItems);
                chests = YamlConfiguration.loadConfiguration(rxChests);
                kits = YamlConfiguration.loadConfiguration(rxKits);
                menus = YamlConfiguration.loadConfiguration(rxMenus);
                mysql = YamlConfiguration.loadConfiguration(rxMySQL);
                settings = YamlConfiguration.loadConfiguration(rxSettings);
                boards = YamlConfiguration.loadConfiguration(rxBoards);
                games = YamlConfiguration.loadConfiguration(rxGames);
                break;
        }
    }

    /**
     * Save config File using FileStorage
     *
     * @param fileToSave config to save/create with saveMode.
     */
    public void save(SaveMode fileToSave) {
        try {
            switch (fileToSave) {
                case CHESTS:
                    getControl(GuardianFiles.CHESTS).save(rxChests);
                    break;
                case ITEMS:
                    getControl(GuardianFiles.ITEMS).save(rxItems);
                    break;
                case DATA:
                    getControl(GuardianFiles.DATA).save(rxData);
                    break;
                case KITS:
                    getControl(GuardianFiles.KITS).save(rxKits);
                    break;
                case GAMES_FILES:
                    getControl(GuardianFiles.GAMES).save(rxGames);
                    break;
                case MENUS:
                    getControl(GuardianFiles.MENUS).save(rxMenus);
                    break;
                case SCOREBOARDS:
                    getControl(GuardianFiles.SCOREBOARD).save(rxBoards);
                    break;
                case MYSQL:
                    getControl(GuardianFiles.MYSQL).save(rxMySQL);
                    break;
                case MESSAGES:
                    getControl(GuardianFiles.MESSAGES).save(rxMessages);
                    break;
                case SETTINGS:
                    getControl(GuardianFiles.SETTINGS).save(rxSettings);
                    break;
                case ALL:
                default:
                    getControl(GuardianFiles.SETTINGS).save(rxSettings);
                    getControl(GuardianFiles.CHESTS).save(rxChests);
                    getControl(GuardianFiles.DATA).save(rxData);
                    getControl(GuardianFiles.KITS).save(rxKits);
                    getControl(GuardianFiles.GAMES).save(rxGames);
                    getControl(GuardianFiles.SCOREBOARD).save(rxBoards);
                    getControl(GuardianFiles.ITEMS).save(rxItems);
                    getControl(GuardianFiles.MENUS).save(rxMenus);
                    getControl(GuardianFiles.MYSQL).save(rxMySQL);
                    getControl(GuardianFiles.MESSAGES).save(rxMessages);
                    break;
            }
        } catch (Throwable throwable) {
            plugin.getLogs().error("Can't save a file!");

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
            if(createFile) plugin.getLogs().info("&7Folder created!");
        }

        if (!file.exists()) {
            try (InputStream in = plugin.getResource(configName + ".yml")) {
                if(in != null) {
                     Files.copy(in, file.toPath());
                }
            } catch (Throwable throwable) {
                plugin.getLogs().error(String.format("A error occurred while copying the config %s to the plugin data folder. Error: %s", configName, throwable));
                plugin.getLogs().error(throwable);
            }
        }
    }
    /**
     * Save config File Changes & Paths
     *
     * @param fileToSave config to save/create.
     */
    public void saveConfig(File fileToSave) {
        if (!fileToSave.getParentFile().exists()) {
            boolean createFile = fileToSave.mkdir();
            if(createFile) plugin.getLogs().info("&7Folder created!!");
        }

        if (!fileToSave.exists()) {
            plugin.getLogs().debug(fileToSave.getName());
            try (InputStream in = plugin.getResource(fileToSave.getName() + ".yml")) {
                if(in != null) {
                    Files.copy(in, fileToSave.toPath());
                }
            } catch (Throwable throwable) {
                plugin.getLogs().error(String.format("A error occurred while copying the config %s to the plugin data folder. Error: %s", fileToSave.getName(), throwable));
                plugin.getLogs().error(throwable);
            }
        }
    }

    /**
     * Control a file, getControl() will return a FileConfiguration.
     *
     * @param fileToControl config to control.
     */
    public FileConfiguration getControl(GuardianFiles fileToControl) {
        switch (fileToControl) {
            case CHESTS:
                if(chests == null) items = loadConfig(rxChests);
                return chests;
            case ITEMS:
                if(items == null) items = loadConfig(rxItems);
                return items;
            case DATA:
                if(data == null) data = loadConfig(rxData);
                return data;
            case KITS:
                if(kits == null) kits = loadConfig(rxKits);
                return kits;
            case GAMES:
                if(games == null) games = loadConfig(rxGames);
                return games;
            case MENUS:
                if(menus == null) menus = loadConfig(rxMenus);
                return menus;
            case SCOREBOARD:
                if(boards == null) boards = loadConfig(rxBoards);
                return boards;
            case MYSQL:
                if(mysql == null) mysql = loadConfig(rxMySQL);
                return mysql;
            case MESSAGES:
                if(messages == null) messages = loadConfig(rxMessages);
                return messages;
            case SETTINGS:
            default:
                if(settings == null) settings = loadConfig(rxSettings);
                return settings;
        }
    }

    public List<String> getContent(GuardianFiles file, String path, boolean getKeys) {
        List<String> rx = new ArrayList<>();
        ConfigurationSection section = getControl(file).getConfigurationSection(path);
        if(section == null) return rx;
        rx.addAll(section.getKeys(getKeys));
        return rx;
    }

}
