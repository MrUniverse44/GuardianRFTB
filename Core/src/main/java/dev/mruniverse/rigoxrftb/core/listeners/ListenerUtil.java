package dev.mruniverse.rigoxrftb.core.listeners;

import dev.mruniverse.rigoxrftb.core.RigoxRFTB;
import dev.mruniverse.rigoxrftb.core.commands.MainCMD;

import java.util.Objects;

@SuppressWarnings("unused")
public class ListenerUtil {
    private final RigoxRFTB plugin;
    public ListenerUtil(RigoxRFTB main){
        plugin = main;
    }
    public void registerListeners() {
        plugin.getLogs().debug("Registering listeners..");
        plugin.getServer().getPluginManager().registerEvents(new MainListener(plugin),plugin);
        plugin.getLogs().debug("Events registered!");
    }
    public void registerCommands() {
        plugin.getLogs().debug("Registering commands..");
        try {
            Objects.requireNonNull(plugin.getCommand("RigoxRFTB")).setExecutor(new MainCMD("RigoxRFTB", plugin));
            Objects.requireNonNull(plugin.getCommand("rRFTB")).setExecutor(new MainCMD("rRFTB", plugin));
            Objects.requireNonNull(plugin.getCommand("rftb")).setExecutor(new MainCMD("rftb", plugin));
        } catch (Throwable throwable) {
            plugin.getLogs().error("Can't register commands.");
        }
    }
    public void stopWorking(int errorCode) {
        plugin.getLogs().error("The plugin stop working, error code: " + errorCode);
        plugin.getServer().getPluginManager().disablePlugin(plugin);
    }
}

