package dev.mruniverse.rigoxrftb.core.kits;

import dev.mruniverse.rigoxrftb.core.RigoxRFTB;
import dev.mruniverse.rigoxrftb.core.enums.RigoxFiles;
import dev.mruniverse.rigoxrftb.core.utils.players.PlayerManager;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class KitLoader {
    private final RigoxRFTB plugin;
    private HashMap<String,KitInfo> beastKits;
    private HashMap<String,KitInfo> beastKitsUsingID;
    private HashMap<String,KitInfo> runnerKits;
    private HashMap<String,KitInfo> runnerKitsUsingID;
    public KitLoader(RigoxRFTB main) {
        plugin = main;
        beastKits = new HashMap<>();
        runnerKits = new HashMap<>();
        beastKitsUsingID = new HashMap<>();
        runnerKitsUsingID = new HashMap<>();
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
        PlayerManager data = plugin.getPlayerData(player.getUniqueId());
        KitInfo kitInfo = getKits(kitType).get(kitName);
        if(kitInfo == null) return;
        String kitID = kitInfo.getID();
        switch (kitType) {
            case BEAST:
                if(data.getKits().contains(kitID)) {
                    data.setSelectedKit(kitID);
                    String selected = plugin.getStorage().getControl(RigoxFiles.MESSAGES).getString("messages.inGame.kits.selectedKit");
                    if(selected == null) selected = "&aNow you selected kit &b%kit_name%";
                    selected = selected.replace("%kit_name%",kitName)
                            .replace("%name%",kitName)
                            .replace("%price%",kitInfo.getPrice() + "")
                            .replace("%kit_price%",kitInfo.getPrice() + "");
                    plugin.getUtils().sendMessage(player,selected);
                } else {

                }
            default:
            case RUNNER:
                if(data.getKits().contains(kitID)) {
                    data.setSelectedKit(kitID);
                    String selected = plugin.getStorage().getControl(RigoxFiles.MESSAGES).getString("messages.inGame.kits.selectedKit");
                    if(selected == null) selected = "&aNow you selected kit &b%kit_name%";
                    selected = selected.replace("%kit_name%",kitName)
                            .replace("%name%",kitName)
                            .replace("%price%",kitInfo.getPrice() + "")
                            .replace("%kit_price%",kitInfo.getPrice() + "");
                    plugin.getUtils().sendMessage(player,selected);
                } else {
                    if(data.getCoins() >= kitInfo.getPrice()) {
                        int coins = data.getCoins() - kitInfo.getPrice();
                        data.setCoins(coins);
                        data.addKit(kitID);
                        data.setSelectedKit(kitID);
                        String buyKit = plugin.getStorage().getControl(RigoxFiles.MESSAGES).getString("messages.inGame.kits.buyKit");
                        if(buyKit == null) buyKit = "&aNow you have the kit &b%kit_name% &a(&3-%price%&a)";
                        buyKit = buyKit.replace("%kit_name%",kitName)
                                .replace("%name%",kitName)
                                .replace("%price%",kitInfo.getPrice() + "")
                                .replace("%kit_price%",kitInfo.getPrice() + "");
                        String selected = plugin.getStorage().getControl(RigoxFiles.MESSAGES).getString("messages.inGame.kits.selectedKit");
                        if(selected == null) selected = "&aNow you selected kit &b%kit_name%";
                        selected = selected.replace("%kit_name%",kitName)
                                .replace("%name%",kitName)
                                .replace("%price%",kitInfo.getPrice() + "")
                                .replace("%kit_price%",kitInfo.getPrice() + "");
                        plugin.getUtils().sendMessage(player,buyKit);
                        plugin.getUtils().sendMessage(player,selected);
                    } else {
                        String cantBuy = plugin.getStorage().getControl(RigoxFiles.MESSAGES).getString("messages.inGame.kits.selectedKit");
                        if(cantBuy == null) cantBuy = "&aNow you selected kit &b%kit_name%";
                        cantBuy = cantBuy.replace("%kit_name%",kitName)
                                .replace("%name%",kitName)
                                .replace("%price%",kitInfo.getPrice() + "")
                                .replace("%kit_price%",kitInfo.getPrice() + "");
                        plugin.getUtils().sendMessage(player,cantBuy);
                    }
                }
        }
    }
    public void updateKits() {
        beastKits = new HashMap<>();
        runnerKits = new HashMap<>();
        beastKitsUsingID = new HashMap<>();
        runnerKitsUsingID = new HashMap<>();
        loadKits(KitType.BEAST);
        plugin.getLogs().info(beastKits.keySet().size() + " Beast(s) Kit(s) loaded!");
        loadKits(KitType.RUNNER);
        plugin.getLogs().info(runnerKits.keySet().size() + " Runner(s) Kit(s) loaded!");
    }
    public void loadKit(KitType kitType,String kitName) {
        KitInfo kitInfo = new KitInfo(plugin,kitType,kitName);
        switch (kitType) {
            case RUNNER:
                runnerKitsUsingID.put(kitInfo.getID(),kitInfo);
                runnerKits.put(kitName,kitInfo);
                return;
            case BEAST:
            default:
                beastKitsUsingID.put(kitInfo.getID(),kitInfo);
                beastKits.put(kitName,kitInfo);
        }
    }
    public void unloadKit(KitType kitType,String kitName) {
        switch (kitType) {
            case RUNNER:
                if(beastKits.get(kitName) != null) runnerKitsUsingID.remove(beastKits.get(kitName).getID());
                runnerKits.remove(kitName);
                return;
            case BEAST:
                if(beastKits.get(kitName) != null) beastKitsUsingID.remove(beastKits.get(kitName).getID());
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
    public HashMap<String,KitInfo> getKitsUsingID(KitType kitType) {
        switch (kitType) {
            case BEAST:
                return beastKitsUsingID;
            case RUNNER:
            default:
                return runnerKitsUsingID;
        }
    }
}
