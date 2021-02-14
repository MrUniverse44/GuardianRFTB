package dev.mruniverse.rigoxrftb.core.kits;

import dev.mruniverse.rigoxrftb.core.RigoxRFTB;

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
        plugin.getLogs().info("Kits loaded!");
        loadKits(KitType.RUNNER);

    }
    public void loadKits(KitType kitType) {
        switch (kitType) {
            case RUNNER:
                //load runner kit
                return;
            case BEAST:
                //load beast kit
        }
    }
    public void loadKit(KitType kitType,String kitName) {
        //load kit
    }
    public void unloadKit(KitType kitType,String kitName) {
        //unload kit
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
