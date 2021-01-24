package dev.mruniverse.rigoxrftb.core.listeners;

import dev.mruniverse.rigoxrftb.core.RigoxRFTB;
import dev.mruniverse.rigoxrftb.core.commands.MainCMD;

import java.util.Objects;

public class ListenerUtil {
    private RigoxRFTB plugin;
    public ListenerUtil(RigoxRFTB main){
        plugin = main;
    }
    public void registerListeners() {
        plugin.getLogs().info("Registering listeners..");
        plugin.getServer().getPluginManager().registerEvents(new PlayerListeners(plugin),plugin);
        plugin.getLogs().info("Events registered!");
    }
    public void registerCommands() {
        plugin.getLogs().info("Registering commands..");
        try {
            Objects.requireNonNull(plugin.getCommand("RigoxRFTB")).setExecutor(new MainCMD("RigoxRFTB", plugin));
            Objects.requireNonNull(plugin.getCommand("rRFTB")).setExecutor(new MainCMD("rRFTB", plugin));
            Objects.requireNonNull(plugin.getCommand("rftb")).setExecutor(new MainCMD("rftb", plugin));
            plugin.getLogs().info("Command: RigoxRFTB, registered!");
        } catch (Throwable throwable) {
            plugin.getLogs().error("Can't register commands.");
        }
    }
    public void stopWorking(int errorCode) {
        plugin.getLogs().info("The plugin stop working, error code: " + errorCode);
        plugin.getServer().getPluginManager().disablePlugin(plugin);
    }
}

