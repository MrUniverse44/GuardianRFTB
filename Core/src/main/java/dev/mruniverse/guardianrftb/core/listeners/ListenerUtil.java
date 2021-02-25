package dev.mruniverse.guardianrftb.core.listeners;

import dev.mruniverse.guardianrftb.core.GuardianRFTB;
import dev.mruniverse.guardianrftb.core.commands.MainCMD;

import java.util.Objects;

@SuppressWarnings("unused")
public class ListenerUtil {
    private final GuardianRFTB plugin;
    public ListenerUtil(GuardianRFTB main){
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
            Objects.requireNonNull(plugin.getCommand("GuardianRFTB")).setExecutor(new MainCMD("GuardianRFTB", plugin));
            Objects.requireNonNull(plugin.getCommand("gRFTB")).setExecutor(new MainCMD("rRFTB", plugin));
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

