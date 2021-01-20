package dev.mruniverse.rigoxrftb.rigoxrftb;

import dev.mruniverse.rigoxrftb.rigoxrftb.enums.SaveMode;
import dev.mruniverse.rigoxrftb.rigoxrftb.files.FileManager;
import dev.mruniverse.rigoxrftb.rigoxrftb.listeners.ListenerUtil;
import dev.mruniverse.rigoxrftb.rigoxrftb.utils.Logger;
import dev.mruniverse.rigoxrftb.rigoxrftb.utils.RigoxBossBar;
import dev.mruniverse.rigoxrftb.rigoxrftb.utils.RigoxUtils;
import org.bukkit.plugin.java.JavaPlugin;

public final class RigoxRFTB extends JavaPlugin {
    private FileManager fileManager;
    private boolean hasPAPI = false;
    private static RigoxRFTB instance;
    private Logger logger;
    private RigoxUtils rigoxUtils;
    private ListenerUtil rigoxListeners;
    private RigoxBossBar rigoxBossBar;
    @Override
    public void onEnable() {
        instance = this;
        logger = new Logger(this);

        // * Utils Setup

        rigoxUtils = new RigoxUtils(this);
        rigoxBossBar = new RigoxBossBar("&7");

        // * Files Setup

        fileManager = new FileManager(this);
        fileManager.loadFiles();
        fileManager.loadConfiguration();
        fileManager.save(SaveMode.ALL);

        if(getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            hasPAPI = true;
        }

        // * Listener Setup

        rigoxListeners = new ListenerUtil(this);
        rigoxListeners.registerListeners();

        // * Game Setup
    }

    @Override
    public void onDisable() {
        // disabled
    }
    public boolean hasPAPI() { return hasPAPI; }
    public ListenerUtil getListener() { return rigoxListeners; }
    public static RigoxRFTB getInstance() {
        return instance;
    }
    public FileManager getFiles() {
        return fileManager;
    }
    public Logger getLogs() { return logger; }
    public RigoxUtils getUtils() { return rigoxUtils; }
    public RigoxBossBar getBar() { return rigoxBossBar; }
}
