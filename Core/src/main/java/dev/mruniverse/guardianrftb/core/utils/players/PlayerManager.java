package dev.mruniverse.guardianrftb.core.utils.players;

import dev.mruniverse.guardianrftb.core.GuardianRFTB;
import dev.mruniverse.guardianrftb.core.enums.GuardianFiles;
import dev.mruniverse.guardianrftb.core.games.Game;
import dev.mruniverse.guardianrftb.core.kits.KitMenu;
import dev.mruniverse.guardianrftb.core.kits.KitType;
import dev.mruniverse.guardianrftb.core.enums.PlayerStatus;
import dev.mruniverse.guardianrftb.core.enums.GuardianBoard;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayerManager {
    private PlayerStatus playerStatus;
    private GuardianBoard guardianBoard;
    private final KitMenu beastMenu;
    private final KitMenu runnerMenu;
    private final Player player;
    private final GuardianRFTB plugin;
    private boolean pointStatus;
    private Location lastCheckpoint;
    private Game currentGame;
    private int leaveDelay;

    public PlayerManager(Player p, GuardianRFTB main) {
        plugin = main;
        beastMenu = new KitMenu(main, KitType.BEAST,p);
        runnerMenu = new KitMenu(main, KitType.RUNNER,p);
        player = p;
        leaveDelay = 0;
        pointStatus = false;
        lastCheckpoint = null;
        guardianBoard = GuardianBoard.LOBBY;
        playerStatus = PlayerStatus.IN_LOBBY;
        currentGame = null;
    }
    public KitMenu getKitMenu(KitType kitType) {
        switch (kitType) {
            case BEAST:
                return beastMenu;
            case RUNNER:
            default:
                return runnerMenu;
        }
    }

    public void setLeaveDelay(int delay) { leaveDelay = delay; }
    public void setStatus(PlayerStatus status) {
        playerStatus = status;
    }
    public void setBoard(GuardianBoard board) {
        guardianBoard = board;
    }
    public void setGame(Game game) { currentGame = game; }
    public GuardianBoard getBoard() {
        return guardianBoard;
    }
    public PlayerStatus getStatus() {
        return playerStatus;
    }
    public String getName() {
        return player.getName();
    }
    public Game getGame() { return currentGame; }
    public Player getPlayer() {
        return player;
    }
    public int getLeaveDelay() { return leaveDelay; }
    public int getWins() {
        if (plugin.getStorage().getControl(GuardianFiles.MYSQL).getBoolean("mysql.enabled")) {
            String table = plugin.getStorage().getControl(GuardianFiles.MYSQL).getString("mysql.table");
            return plugin.getData().getInt(table, "Wins", "Player", player.getUniqueId().toString());
        }
        if(plugin.getData().getSQL().wins.get(player.getUniqueId().toString()) != null) {
            return plugin.getData().getSQL().wins.get(player.getUniqueId().toString());
        }
        return 0;
    }

    public void setWins(int wins) {
        String playerName = getID();
        if (plugin.getStorage().getControl(GuardianFiles.MYSQL).getBoolean("mysql.enabled")) {
            registerDefault();
            String table = plugin.getStorage().getControl(GuardianFiles.MYSQL).getString("mysql.table");
            plugin.getData().setInt(table, "Wins", "Player", playerName, wins);
        } else {
            plugin.getData().getSQL().wins.put(playerName, wins);
        }
    }

    public void addWins() {
        setWins(getWins() + 1);
    }

    public boolean getPointStatus() {
        return pointStatus;
    }

    public Location getLastCheckpoint() {
        return lastCheckpoint;
    }

    public void setPointStatus(boolean bol) {
        pointStatus = bol;
    }

    public void setLastCheckpoint(Location location) {
        lastCheckpoint = location;
    }

    public int getCoins() {
        if (plugin.getStorage().getControl(GuardianFiles.MYSQL).getBoolean("mysql.enabled")) {
            registerDefault();
            String table = plugin.getStorage().getControl(GuardianFiles.MYSQL).getString("mysql.table");
            return plugin.getData().getInt(table, "Coins", "Player", player.getUniqueId().toString());
        }
        if(plugin.getData().getSQL().coins.get(player.getUniqueId().toString()) != null) {
            return plugin.getData().getSQL().coins.get(player.getUniqueId().toString());
        }
        return 0;
    }
    public void updateCoins(int addOrRemove) {
        setCoins(getCoins() + addOrRemove);
    }
    public void setCoins(int coinCounter) {
        String playerName = getID();
        if (plugin.getStorage().getControl(GuardianFiles.MYSQL).getBoolean("mysql.enabled")) {
            registerDefault();
            String table = plugin.getStorage().getControl(GuardianFiles.MYSQL).getString("mysql.table");
            plugin.getData().setInt(table, "Coins", "Player", playerName, coinCounter);
        } else {
            plugin.getData().getSQL().coins.put(playerName, coinCounter);
        }
    }

    public int getKills() {
        if (plugin.getStorage().getControl(GuardianFiles.MYSQL).getBoolean("mysql.enabled")) {
            String table = plugin.getStorage().getControl(GuardianFiles.MYSQL).getString("mysql.table");
            return plugin.getData().getInt(table, "Kills", "Player", player.getUniqueId().toString());
        }
        if(plugin.getData().getSQL().kills.get(player.getUniqueId().toString()) != null) {
            return plugin.getData().getSQL().kills.get(player.getUniqueId().toString());
        }
        return 0;
    }

    public void setKills(int kills) {
        String playerName = getID();
        if (plugin.getStorage().getControl(GuardianFiles.MYSQL).getBoolean("mysql.enabled")) {
            registerDefault();
            String table = plugin.getStorage().getControl(GuardianFiles.MYSQL).getString("mysql.table");
            plugin.getData().setInt(table, "Kills", "Player", playerName, kills);
        } else {
            plugin.getData().getSQL().kills.put(playerName, kills);
        }
    }

    public void setSelectedKit(String kitID) {
        String playerName = getID();
        if (plugin.getStorage().getControl(GuardianFiles.MYSQL).getBoolean("mysql.enabled")) {
            registerDefault();
            String table = plugin.getStorage().getControl(GuardianFiles.MYSQL).getString("mysql.table");
            plugin.getData().setString(table, "SelectedKit", "Player", playerName, kitID);
        } else {
            plugin.getData().getSQL().selectedKits.put(playerName, kitID);
        }
    }

    public String getSelectedKit() {
        if (plugin.getStorage().getControl(GuardianFiles.MYSQL).getBoolean("mysql.enabled")) {
            registerDefault();
            String table = plugin.getStorage().getControl(GuardianFiles.MYSQL).getString("mysql.table");
            String kitsBuy = plugin.getData().getString(table,"SelectedKit","Player",getID());
            return kitsBuy.replace(" ","");
        }
        if(plugin.getData().getSQL().kits.get(getID()) != null) {
            String kitsBuy = plugin.getData().getSQL().selectedKits.get(getID());
            return kitsBuy.replace(" ","");
        }
        return "NONE";
    }

    public void addKit(String kitID) {
        if (plugin.getStorage().getControl(GuardianFiles.MYSQL).getBoolean("mysql.enabled")) {
            registerDefault();
            String table = plugin.getStorage().getControl(GuardianFiles.MYSQL).getString("mysql.table");
            String lastResult = plugin.getData().getString(table,"Kits","Player",getID());
            if(!lastResult.equalsIgnoreCase("")) {
                plugin.getData().setString(table, "Kits", "Player", getID(), lastResult + ",K" + kitID);
            } else {
                plugin.getData().setString(table, "Kits", "Player", getID(), "K"+kitID);
            }
        }
        if(plugin.getData().getSQL().kits.get(getID()) != null) {

            String lastResult = plugin.getData().getSQL().kits.get(getID());
            if(!lastResult.equalsIgnoreCase("")) {
                plugin.getData().getSQL().kits.put(getID(), lastResult + ",K" + kitID);
            } else {
                plugin.getData().getSQL().kits.put(getID(), "K" + kitID);
            }
        }
    }

    public List<String> getKits() {
        if (plugin.getStorage().getControl(GuardianFiles.MYSQL).getBoolean("mysql.enabled")) {
            registerDefault();
            String table = plugin.getStorage().getControl(GuardianFiles.MYSQL).getString("mysql.table");
            String kitsBuy = plugin.getData().getString(table,"Kits","Player",getID());
            kitsBuy = kitsBuy.replace(" ","").replace("K","");
            String[] kitShortList = kitsBuy.split(",");
            return Arrays.asList(kitShortList);
        }
        if(plugin.getData().getSQL().kits.get(getID()) != null) {
            registerDefault();
            String kitsBuy = plugin.getData().getSQL().kits.get(getID());
            kitsBuy = kitsBuy.replace(" ","");
            String[] kitShortList = kitsBuy.split(",");
            return Arrays.asList(kitShortList);
        }
        return new ArrayList<>();
    }

    @SuppressWarnings("unused")
    public void addKills() {
        setKills(getKills() + 1);
    }

    public int getDeaths() {
        if (plugin.getStorage().getControl(GuardianFiles.MYSQL).getBoolean("mysql.enabled")) {
            registerDefault();
            String table = plugin.getStorage().getControl(GuardianFiles.MYSQL).getString("mysql.table");
            return plugin.getData().getInt(table, "Deaths", "Player", getID());
        }
        if(plugin.getData().getSQL().deaths.get(getID()) != null) {
            return plugin.getData().getSQL().deaths.get(getID());
        }
        return 0;
    }

    public void setDeaths(int deaths) {
        String playerName = getID();
        if (plugin.getStorage().getControl(GuardianFiles.MYSQL).getBoolean("mysql.enabled")) {
            registerDefault();
            String table = plugin.getStorage().getControl(GuardianFiles.MYSQL).getString("mysql.table");
            plugin.getData().setInt(table, "Deaths", "Player", playerName, deaths);
        } else {
            plugin.getData().getSQL().deaths.put(playerName, deaths);
        }
    }
    
    public String getID() {
        return player.getUniqueId().toString().replace("-","");
    }

    public void addDeaths() {
        registerDefault();
        setDeaths(getDeaths() + 1);
    }


    public void registerDefault() {
        String table = plugin.getStorage().getControl(GuardianFiles.MYSQL).getString("mysql.table");
        if (!plugin.getData().isRegistered(table, "Player", getID())) {
            List<String> values = new ArrayList<>();
            values.add("Player-" + getID());
            values.add("Coins-0");
            values.add("Kits-K" + plugin.getStorage().getControl(GuardianFiles.SETTINGS).getString("settings.defaultKitID"));
            values.add("SelectedKit-NONE");
            values.add("Kills-0");
            values.add("Deaths-0");
            values.add("Score-0");
            values.add("Wins-0");
            plugin.getData().register(table, values);
        }
    }
}

