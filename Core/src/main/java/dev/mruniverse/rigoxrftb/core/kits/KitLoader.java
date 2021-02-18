package dev.mruniverse.rigoxrftb.core.kits;

import dev.mruniverse.rigoxrftb.core.RigoxRFTB;
import dev.mruniverse.rigoxrftb.core.enums.RigoxFiles;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class KitLoader {
    private final RigoxRFTB plugin;
    private HashMap<String,KitInfo> beastKits;
    private HashMap<String,KitInfo> runnerKits;
    public KitLoader(RigoxRFTB main) {
        plugin = main;
        beastKits = new HashMap<>();
        runnerKits = new HashMap<>();
        loadKits(KitType.BEAST);
        plugin.getLogs().info(beastKits.keySet().size() + " Beast(s) Kit(s) loaded!");
        loadKits(KitType.RUNNER);
        plugin.getLogs().info(runnerKits.keySet().size() + " Runner(s) Kit(s) loaded!");

    }
    public void loadKits(KitType kitType) {
        switch (kitType) {
            case RUNNER:
                for(String kit : plugin.getStorage().getContent(RigoxFiles.KITS,"runnerKits",false)) {
                    loadKit(KitType.RUNNER,kit);
                }
                return;
            case BEAST:
                for(String kit : plugin.getStorage().getContent(RigoxFiles.KITS,"beastKits",false)) {
                    loadKit(KitType.RUNNER,kit);
                }
        }
    }
    public void getToSelect(KitType kitType, Player player,String kitName) {

    }
    public void updateKits() {
        beastKits = new HashMap<>();
        runnerKits = new HashMap<>();
        loadKits(KitType.BEAST);
        plugin.getLogs().info(beastKits.keySet().size() + " Beast(s) Kit(s) loaded!");
        loadKits(KitType.RUNNER);
        plugin.getLogs().info(runnerKits.keySet().size() + " Runner(s) Kit(s) loaded!");
    }
    public void loadKit(KitType kitType,String kitName) {
        KitInfo kitInfo = new KitInfo(plugin,kitType,kitName);
        switch (kitType) {
            case RUNNER:
                runnerKits.put(kitName,kitInfo);
                return;
            case BEAST:
            default:
                beastKits.put(kitName,kitInfo);
        }
    }
    public void unloadKit(KitType kitType,String kitName) {
        switch (kitType) {
            case RUNNER:
                runnerKits.remove(kitName);
                return;
            case BEAST:
                beastKits.remove(kitName);
        }
    }
    public HashMap<String,KitInfo> getKits(KitType kitType) {
        switch (kitType) {
            case BEAST:
                return beastKits;
            case RUNNER:
            default:
                return runnerKits;
        }
    }
}
