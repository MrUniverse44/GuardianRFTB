package dev.mruniverse.rigoxrftb.rigoxrftb;

import dev.mruniverse.rigoxrftb.rigoxrftb.files.FileManager;
import dev.mruniverse.rigoxrftb.rigoxrftb.listeners.ListenerUtil;
import org.bukkit.plugin.java.JavaPlugin;

public final class RigoxRFTB extends JavaPlugin {
    private FileManager fileManager;
    private static RigoxRFTB instance;
    @Override
    public void onEnable() {
        instance = this;

        fileManager = new FileManager(this);

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
}
