package dev.mruniverse.rigoxrftb.core.files;

import dev.mruniverse.rigoxrftb.core.RigoxRFTB;
import dev.mruniverse.rigoxrftb.core.enums.Files;
import dev.mruniverse.rigoxrftb.core.enums.SaveMode;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class SQL {
    public HashMap<String, Integer> kills = new HashMap<>();
    public HashMap<String, Integer> deaths = new HashMap<>();
    public HashMap<String, Integer> wins = new HashMap<>();
    public HashMap<String, Integer> score = new HashMap<>();
    public HashMap<String, Integer> coins = new HashMap<>();
    public HashMap<String, Integer> levelXP = new HashMap<>();
    private final RigoxRFTB plugin;
    public SQL(RigoxRFTB main) {
        plugin = main;
    }

    public void putData() {
        if (kills.size() != 0)
            for (Map.Entry<String, Integer> k : kills.entrySet())
                plugin.getFiles().getControl(Files.DATA).set("Kills." + k.getKey(), k.getValue());
        if (deaths.size() != 0)
            for (Map.Entry<String, Integer> k : deaths.entrySet())
                plugin.getFiles().getControl(Files.DATA).set("Deaths." + k.getKey(), k.getValue());
        if (wins.size() != 0)
            for (Map.Entry<String, Integer> k : wins.entrySet())
                plugin.getFiles().getControl(Files.DATA).set("Wins." + k.getKey(), k.getValue());
        if (score.size() != 0)
            for (Map.Entry<String, Integer> k : score.entrySet())
                plugin.getFiles().getControl(Files.DATA).set("Score." + k.getKey(), k.getValue());
        if (coins.size() != 0)
            for (Map.Entry<String, Integer> k : coins.entrySet())
                plugin.getFiles().getControl(Files.DATA).set("Coins." + k.getKey(), k.getValue());
        if (levelXP.size() != 0)
            for (Map.Entry<String, Integer> k : levelXP.entrySet())
                plugin.getFiles().getControl(Files.DATA).set("LevelXP." + k.getKey(), k.getValue());
        plugin.getFiles().save(SaveMode.DATA);
    }

    public void loadData() {
        if (plugin.getFiles().getControl(Files.DATA).contains("Kills"))
            for (String str : plugin.getFiles().getContent(Files.DATA,"Kills",true)) {
                int p = plugin.getFiles().getControl(Files.DATA).getInt("Kills." + str);
                kills.put(str.replace("Kills.", ""), p);
            }
        if (plugin.getFiles().getControl(Files.DATA).contains("Deaths"))
            for (String str : plugin.getFiles().getContent(Files.DATA,"Deaths",true)) {
                int p = plugin.getFiles().getControl(Files.DATA).getInt("Deaths." + str);
                deaths.put(str.replace("Deaths.", ""), p);
            }
        if (plugin.getFiles().getControl(Files.DATA).contains("Wins"))
            for (String str : plugin.getFiles().getContent(Files.DATA,"Wins",true)) {
                int p = plugin.getFiles().getControl(Files.DATA).getInt("Wins." + str);
                wins.put(str.replace("Wins.", ""), p);
            }
        if (plugin.getFiles().getControl(Files.DATA).contains("Score"))
            for (String str : plugin.getFiles().getContent(Files.DATA,"Score",true)) {
                int p = plugin.getFiles().getControl(Files.DATA).getInt("Score." + str);
                score.put(str.replace("Score.", ""), p);
            }
        if (plugin.getFiles().getControl(Files.DATA).contains("LevelXP"))
            for (String str : plugin.getFiles().getContent(Files.DATA,"LevelXP",true)) {
                int p = plugin.getFiles().getControl(Files.DATA).getInt("LevelXP." + str);
                levelXP.put(str.replace("LevelXP.", ""), p);
            }
        if (plugin.getFiles().getControl(Files.DATA).contains("Coins"))
            for (String str : plugin.getFiles().getContent(Files.DATA,"Coins",true)) {
                int p = plugin.getFiles().getControl(Files.DATA).getInt("Coins." + str);
                levelXP.put(str.replace("Coins.", ""), p);
            }
    }

    public void createPlayer(Player player) {
        kills.put(player.getUniqueId().toString(), 0);
        deaths.put(player.getUniqueId().toString(), 0);
        wins.put(player.getUniqueId().toString(), 0);
        coins.put(player.getUniqueId().toString(), 0);
        levelXP.put(player.getUniqueId().toString(), 0);
    }
}
