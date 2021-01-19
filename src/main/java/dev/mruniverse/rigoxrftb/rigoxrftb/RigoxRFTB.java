package dev.mruniverse.rigoxrftb.rigoxrftb;

import dev.mruniverse.rigoxrftb.rigoxrftb.enums.SaveMode;
import dev.mruniverse.rigoxrftb.rigoxrftb.files.FileManager;
import dev.mruniverse.rigoxrftb.rigoxrftb.listeners.ListenerUtil;
import dev.mruniverse.rigoxrftb.rigoxrftb.utils.Logger;
import org.bukkit.plugin.java.JavaPlugin;

public final class RigoxRFTB extends JavaPlugin {
    private FileManager fileManager;
    private static RigoxRFTB instance;
    private Logger logger;
    @Override
    public void onEnable() {
        instance = this;
        logger = new Logger(this);

        // * Files Setup

        fileManager = new FileManager(this);
        fileManager.loadFiles();
        fileManager.loadConfiguration();
        fileManager.save(SaveMode.ALL);


        // * Listener Setup

        new ListenerUtil(this);

        // * Game Setup
    }

    @Override
    public void onDisable() {
        // disabled
    }
    public static RigoxRFTB getInstance() {
        return instance;
    }
    public FileManager getFiles() {
        return fileManager;
    }
    public Logger getLogs() { return logger; }
}
