package dev.mruniverse.rigoxrftb.core.utils.players;

import dev.mruniverse.rigoxrftb.core.RigoxRFTB;
import dev.mruniverse.rigoxrftb.core.enums.RigoxFiles;
import dev.mruniverse.rigoxrftb.core.enums.PlayerStatus;
import dev.mruniverse.rigoxrftb.core.enums.RigoxBoard;
import dev.mruniverse.rigoxrftb.core.games.Game;
import dev.mruniverse.rigoxrftb.core.kits.KitMenu;
import dev.mruniverse.rigoxrftb.core.kits.KitType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayerManager {
    private PlayerStatus playerStatus;
    private RigoxBoard rigoxBoard;
    private final KitMenu beastMenu;
    private final KitMenu runnerMenu;
    private final Player player;
    private final RigoxRFTB plugin;
    private Game currentGame;

    public PlayerManager(Player p, RigoxRFTB main) {
        plugin = main;
        beastMenu = new KitMenu(main, KitType.BEAST,p);
        runnerMenu = new KitMenu(main, KitType.RUNNER,p);
        player = p;
        rigoxBoard = RigoxBoard.LOBBY;
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
    public void setStatus(PlayerStatus status) {
        playerStatus = status;
    }
    public void setBoard(RigoxBoard board) {
        rigoxBoard = board;
    }
    public void setGame(Game game) { currentGame = game; }
    public RigoxBoard getBoard() {
        return rigoxBoard;
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
    public int getWins() {
        if (plugin.getStorage().getControl(RigoxFiles.MYSQL).getBoolean("mysql.enabled")) {
            String table = plugin.getStorage().getControl(RigoxFiles.MYSQL).getString("mysql.table");
            return plugin.getData().getInt(table, "Wins", "Player", player.getUniqueId().toString());
        }
        if(plugin.getData().getSQL().wins.get(player.getUniqueId().toString()) != null) {
            return plugin.getData().getSQL().wins.get(player.getUniqueId().toString());
        }
        return 0;
    }

    public void setWins(int wins) {
        String playerName = this.player.getUniqueId().toString();
        if (plugin.getStorage().getControl(RigoxFiles.MYSQL).getBoolean("mysql.enabled")) {
            registerDefault();
            String table = plugin.getStorage().getControl(RigoxFiles.MYSQL).getString("mysql.table");
            plugin.getData().setInt(table, "Wins", "Player", playerName, wins);
        } else {
            plugin.getData().getSQL().wins.put(playerName, wins);
        }
    }

    public void addWins() {
        setWins(getWins() + 1);
    }

    public int getCoins() {
        if (plugin.getStorage().getControl(RigoxFiles.MYSQL).getBoolean("mysql.enabled")) {
            String table = plugin.getStorage().getControl(RigoxFiles.MYSQL).getString("mysql.table");
            return plugin.getData().getInt(table, "Coins", "Player", player.getUniqueId().toString());
        }
        if(plugin.getData().getSQL().coins.get(player.getUniqueId().toString()) != null) {
            return plugin.getData().getSQL().coins.get(player.getUniqueId().toString());
        }
        return 0;
    }
    public void setCoins(int coinCounter) {
        String playerName = this.player.getUniqueId().toString();
        if (plugin.getStorage().getControl(RigoxFiles.MYSQL).getBoolean("mysql.enabled")) {
            registerDefault();
            String table = plugin.getStorage().getControl(RigoxFiles.MYSQL).getString("mysql.table");
            plugin.getData().setInt(table, "Coins", "Player", playerName, coinCounter);
        } else {
            plugin.getData().getSQL().coins.put(playerName, coinCounter);
        }
    }

    public int getKills() {
        if (plugin.getStorage().getControl(RigoxFiles.MYSQL).getBoolean("mysql.enabled")) {
            String table = plugin.getStorage().getControl(RigoxFiles.MYSQL).getString("mysql.table");
            return plugin.getData().getInt(table, "Kills", "Player", player.getUniqueId().toString());
        }
        if(plugin.getData().getSQL().kills.get(player.getUniqueId().toString()) != null) {
            return plugin.getData().getSQL().kills.get(player.getUniqueId().toString());
        }
        return 0;
    }

    public void setKills(int kills) {
        String playerName = this.player.getUniqueId().toString();
        if (plugin.getStorage().getControl(RigoxFiles.MYSQL).getBoolean("mysql.enabled")) {
            registerDefault();
            String table = plugin.getStorage().getControl(RigoxFiles.MYSQL).getString("mysql.table");
            plugin.getData().setInt(table, "Kills", "Player", playerName, kills);
        } else {
            plugin.getData().getSQL().kills.put(playerName, kills);
        }
    }

    public void setSelectedKit(String kitID) {
        String playerName = this.player.getUniqueId().toString();
        if (plugin.getStorage().getControl(RigoxFiles.MYSQL).getBoolean("mysql.enabled")) {
            registerDefault();
            String table = plugin.getStorage().getControl(RigoxFiles.MYSQL).getString("mysql.table");
            plugin.getData().setString(table, "SelectedKit", "Player", playerName, kitID);
        } else {
            plugin.getData().getSQL().selectedKits.put(playerName, kitID);
        }
    }

    public String getSelectedKit() {
        if (plugin.getStorage().getControl(RigoxFiles.MYSQL).getBoolean("mysql.enabled")) {
            String table = plugin.getStorage().getControl(RigoxFiles.MYSQL).getString("mysql.table");
            String kitsBuy = plugin.getData().getString(table,"SelectedKit","Player",this.player.getUniqueId().toString());
            return kitsBuy.replace(" ","");
        }
        if(plugin.getData().getSQL().kits.get(this.player.getUniqueId().toString()) != null) {
            String kitsBuy = plugin.getData().getSQL().selectedKits.get(this.player.getUniqueId().toString());
            return kitsBuy.replace(" ","");
        }
        return "NONE";
    }

    public void addKit(String kitID) {
        if (plugin.getStorage().getControl(RigoxFiles.MYSQL).getBoolean("mysql.enabled")) {
            String table = plugin.getStorage().getControl(RigoxFiles.MYSQL).getString("mysql.table");
            String lastResult = plugin.getData().getString(table,"Kits","Player",this.player.getUniqueId().toString());
            plugin.getData().setString(table, "Kits", "Player", this.player.getUniqueId().toString(),lastResult + "," + kitID);
        }
        if(plugin.getData().getSQL().kits.get(this.player.getUniqueId().toString()) != null) {
            String lastResult = plugin.getData().getSQL().kits.get(this.player.getUniqueId().toString());
            plugin.getData().getSQL().kits.put(this.player.getUniqueId().toString(),lastResult + "," + kitID);
        }
    }

    public List<String> getKits() {
        if (plugin.getStorage().getControl(RigoxFiles.MYSQL).getBoolean("mysql.enabled")) {
            String table = plugin.getStorage().getControl(RigoxFiles.MYSQL).getString("mysql.table");
            String kitsBuy = plugin.getData().getString(table,"Kits","Player",this.player.getUniqueId().toString());
            kitsBuy = kitsBuy.replace(" ","");
            String[] kitShortList = kitsBuy.split(",");
            return Arrays.asList(kitShortList);
        }
        if(plugin.getData().getSQL().kits.get(this.player.getUniqueId().toString()) != null) {
            String kitsBuy = plugin.getData().getSQL().kits.get(this.player.getUniqueId().toString());
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
        if (plugin.getStorage().getControl(RigoxFiles.MYSQL).getBoolean("mysql.enabled")) {
            String table = plugin.getStorage().getControl(RigoxFiles.MYSQL).getString("mysql.table");
            return plugin.getData().getInt(table, "Deaths", "Player", this.player.getUniqueId().toString());
        }
        if(plugin.getData().getSQL().deaths.get(this.player.getUniqueId().toString()) != null) {
            return plugin.getData().getSQL().deaths.get(this.player.getUniqueId().toString());
        }
        return 0;
    }

    public void setDeaths(int deaths) {
        String playerName = this.player.getUniqueId().toString();
        if (plugin.getStorage().getControl(RigoxFiles.MYSQL).getBoolean("mysql.enabled")) {
            registerDefault();
            String table = plugin.getStorage().getControl(RigoxFiles.MYSQL).getString("mysql.table");
            plugin.getData().setInt(table, "Deaths", "Player", playerName, deaths);
        } else {
            plugin.getData().getSQL().deaths.put(playerName, deaths);
        }
    }

    public void addDeaths() {
        setDeaths(getDeaths() + 1);
    }


    public void registerDefault() {
        String playerName = player.getUniqueId().toString();
        String table = plugin.getStorage().getControl(RigoxFiles.MYSQL).getString("mysql.table");
        if (!plugin.getData().isRegistered(table, "Player", playerName)) {
            List<String> values = new ArrayList<>();
            values.add("Player-" + playerName);
            values.add("Coins-0");
            values.add("Kits-" + plugin.getStorage().getControl(RigoxFiles.SETTINGS).getString("settings.defaultKitID"));
            values.add("SelectedKit-NONE");
            values.add("Kills-0");
            values.add("Deaths-0");
            values.add("Score-0");
            values.add("Wins-0");
            plugin.getData().register(table, values);
        }
    }
}

