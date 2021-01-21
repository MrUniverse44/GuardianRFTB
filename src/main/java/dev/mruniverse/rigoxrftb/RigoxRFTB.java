package dev.mruniverse.rigoxrftb;

import dev.mruniverse.rigoxrftb.files.FileManager;
import dev.mruniverse.rigoxrftb.listeners.ListenerUtil;
import dev.mruniverse.rigoxrftb.enums.SaveMode;
import dev.mruniverse.rigoxrftb.utils.Logger;
import dev.mruniverse.rigoxrftb.utils.rigoxbossbars.BossManager;
import dev.mruniverse.rigoxrftb.utils.rigoxplayers.PlayerRunnable;
import dev.mruniverse.rigoxrftb.utils.rigoxscoreboards.BoardManager;
import dev.mruniverse.rigoxrftb.utils.RigoxUtils;
import dev.mruniverse.rigoxrftb.utils.rigoxplayers.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public final class RigoxRFTB extends JavaPlugin {
    private FileManager fileManager;
    private boolean hasPAPI = false;
    private static RigoxRFTB instance;
    private Logger logger;
    private RigoxUtils rigoxUtils;
    private ListenerUtil rigoxListeners;
    private BoardManager rigoxScoreboards;
    private BossManager rigoxBoss;
    private final HashMap<UUID, PlayerManager> rigoxPlayers = new HashMap<>();
    @Override
    public void onEnable() {
        instance = this;
        logger = new Logger(this);

        // * Files Setup

        fileManager = new FileManager(this);
        fileManager.loadFiles();
        fileManager.loadConfiguration();
        fileManager.save(SaveMode.ALL);

        if(getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            hasPAPI = true;
        }

        // * Listener Setup

        rigoxListeners = new ListenerUtil(this);
        rigoxListeners.registerListeners();

        // * Utils Setup

        rigoxUtils = new RigoxUtils(this);

        // * BossBar Setup

        rigoxBoss = new BossManager(this);

        // * Scoreboard Setup

        rigoxScoreboards = new BoardManager(this);
        getServer().getScheduler().runTaskTimerAsynchronously(this,new PlayerRunnable(this),0L,20L);
    }

    @Override
    public void onDisable() {
        // disabled
    }
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
    public BossManager getBossBar() { return rigoxBoss; }
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
    public PlayerManager getPlayerData(Player player) {
        return rigoxPlayers.get(player.getUniqueId());
    }
    public HashMap<UUID, PlayerManager> getRigoxPlayers() {
        return rigoxPlayers;
    }
    public PlayerManager getPlayerData(UUID uuid) {
        return rigoxPlayers.get(uuid);
    }
}
