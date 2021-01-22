package dev.mruniverse.rigoxrftb.core;

import dev.mruniverse.rigoxrftb.core.enums.Files;
import dev.mruniverse.rigoxrftb.core.enums.NMSenum;
import dev.mruniverse.rigoxrftb.core.files.FileManager;
import dev.mruniverse.rigoxrftb.core.listeners.ListenerUtil;
import dev.mruniverse.rigoxrftb.core.enums.SaveMode;
import dev.mruniverse.rigoxrftb.core.nms.NMS;
import dev.mruniverse.rigoxrftb.core.utils.Logger;
import dev.mruniverse.rigoxrftb.core.utils.players.PlayerRunnable;
import dev.mruniverse.rigoxrftb.core.utils.scoreboards.BoardManager;
import dev.mruniverse.rigoxrftb.core.utils.RigoxUtils;
import dev.mruniverse.rigoxrftb.core.utils.players.PlayerManager;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public final class RigoxRFTB extends JavaPlugin {
    private FileManager fileManager;
    private boolean hasPAPI = false;
    private static RigoxRFTB instance;
    private Logger logger;
    private RigoxUtils rigoxUtils;
    private NMS nmsHandler;
    private ListenerUtil rigoxListeners;
    private BoardManager rigoxScoreboards;
    private final HashMap<UUID, PlayerManager> rigoxPlayers = new HashMap<>();
    private final HashMap<ItemStack, Integer> lobbyItems = new HashMap<>();
    private final HashMap<ItemStack, List<String>> lobbyItemCommands = new HashMap<>();
    @Override
    public void onEnable() {
        instance = this;
        logger = new Logger(this);

        // * Files Setup

        fileManager = new FileManager(this);
        fileManager.loadFiles();
        fileManager.loadConfiguration();
        fileManager.save(SaveMode.ALL);

        hasPAPI = getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");

        // * Listener Setup

        rigoxListeners = new ListenerUtil(this);
        rigoxListeners.registerListeners();

        // * Utils Setup

        rigoxUtils = new RigoxUtils(this);

        // * NMS Setup

        nmsSetup();
        try {
            FileConfiguration items = getFiles().getControl(Files.ITEMS);
            for (String lItems : items.getConfigurationSection("lobby").getKeys(false)) {
                if(items.getBoolean("lobby." + lItems + ".toggle")) {
                    String material = items.getString("lobby." + lItems + ".item");
                    if(material == null) material = "BED";
                    if(Material.getMaterial(material) != null) {
                        String itemName = items.getString("lobby." + lItems + ".name");
                        Integer slot = items.getInt("lobby." + lItems + ".slot");
                        List<String> lore = items.getStringList("lobby." + lItems + ".lore");
                        List<String> actions = items.getStringList("lobby." + lItems + ".actions");
                        ItemStack item = getNMSHandler().getItemStack(Material.getMaterial(material), itemName, lore);
                        lobbyItems.put(item, slot);
                        lobbyItemCommands.put(item, actions);
                    }
                }
            }
        } catch (Throwable throwable) {
            getLogs().error("Can't get lobby items on startup");
            getLogs().error(throwable);
        }

        // * Scoreboard Setup

        rigoxScoreboards = new BoardManager(this);

        // * Tasks

        getServer().getScheduler().runTaskTimerAsynchronously(this,new PlayerRunnable(this),0L,20L);
    }
    private void nmsSetup() {
        try {
            nmsHandler = (NMS) Class.forName("dev.mruniverse.rigoxrftb.nms." + NMSenum.getCurrent() + ".NMSHandler").getConstructor(new Class[0]).newInstance(new Object[0]);
            getLogs().info("Successfully connected with version: " + NMSenum.getCurrent() + ", the plugin can work correctly. If you found an issue please report to the developer.");
        }catch (Throwable throwable) {
            getLogs().error("Can't initialize NMS, unsupported version: " + NMSenum.getCurrent());
            getLogs().error(throwable);
        }
    }
    public HashMap<ItemStack,Integer> getLobbyItems() { return lobbyItems; }
    public HashMap<ItemStack,List<String>> getLobbyItemCommands() { return lobbyItemCommands; }
    public List<String> getCommands(ItemStack itemStack) { return lobbyItemCommands.get(itemStack); }
    public int getSlot(ItemStack itemStack) { return lobbyItems.get(itemStack); }
    public NMS getNMSHandler() { return nmsHandler; }
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
    public BoardManager getScoreboards() { return rigoxScoreboards; }
    public void addPlayer(Player player){
        if(!existPlayer(player)) {
            rigoxPlayers.put(player.getUniqueId(),new PlayerManager(player));
        }
    }
    public boolean existPlayer(Player player) {
        return rigoxPlayers.containsKey(player.getUniqueId());
    }
    public void removePlayer(Player player) {
        rigoxPlayers.remove(player.getUniqueId());
    }
    public HashMap<UUID, PlayerManager> getRigoxPlayers() {
        return rigoxPlayers;
    }
    public PlayerManager getPlayerData(UUID uuid) {
        return rigoxPlayers.get(uuid);
    }
}