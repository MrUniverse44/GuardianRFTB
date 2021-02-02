package dev.mruniverse.rigoxrftb.core.utils.players;

import dev.mruniverse.rigoxrftb.core.RigoxRFTB;
import dev.mruniverse.rigoxrftb.core.enums.RigoxFiles;
import dev.mruniverse.rigoxrftb.core.enums.PlayerStatus;
import dev.mruniverse.rigoxrftb.core.enums.RigoxBoard;
import dev.mruniverse.rigoxrftb.core.games.Game;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerManager {
    private PlayerStatus playerStatus;
    private RigoxBoard rigoxBoard;
    private final Player player;
    private final RigoxRFTB plugin;
    private Game currentGame;

    public PlayerManager(Player p, RigoxRFTB main) {
        plugin = main;
        player = p;
        rigoxBoard = RigoxBoard.LOBBY;
        playerStatus = PlayerStatus.IN_LOBBY;
        currentGame = null;
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
        if (plugin.getFiles().getControl(RigoxFiles.MYSQL).getBoolean("mysql.enabled")) {
            String table = plugin.getFiles().getControl(RigoxFiles.MYSQL).getString("mysql.table");
            // integers.add("Kills");
            //            integers.add("Deaths");
            //            integers.add("Score");
            //            integers.add("Wins");
            //            integers.add("Coins");
            //            integers.add("LevelXP");
            //            List<String> strings = new ArrayList<>();
            //            strings.add("Player");
            //            integers.add("Rank");
            return plugin.getData().getInt(table, "Wins", "Player", player.getUniqueId().toString());
        }
        return plugin.getData().getSQL().wins.get(player.getUniqueId().toString());
    }

    public void setWins(int wins) {
        String playerName = this.player.getUniqueId().toString();
        if (plugin.getFiles().getControl(RigoxFiles.MYSQL).getBoolean("mysql.enabled")) {
            registerDefault();
            String table = plugin.getFiles().getControl(RigoxFiles.MYSQL).getString("mysql.table");
            plugin.getData().setInt(table, "Wins", "Player", playerName, wins);
        } else {
            plugin.getData().getSQL().wins.put(playerName, wins);
        }
    }

    public void addWins() {
        setWins(getWins() + 1);
    }

    public int getCoins() {
        if (plugin.getFiles().getControl(RigoxFiles.MYSQL).getBoolean("mysql.enabled")) {
            String table = plugin.getFiles().getControl(RigoxFiles.MYSQL).getString("mysql.table");
            return plugin.getData().getInt(table, "Coins", "Player", player.getUniqueId().toString());
        }
        return plugin.getData().getSQL().coins.get(player.getUniqueId().toString());
    }

    public int getKills() {
        if (plugin.getFiles().getControl(RigoxFiles.MYSQL).getBoolean("mysql.enabled")) {
            String table = plugin.getFiles().getControl(RigoxFiles.MYSQL).getString("mysql.table");
            return plugin.getData().getInt(table, "Kills", "Player", player.getUniqueId().toString());
        }
        return plugin.getData().getSQL().kills.get(player.getUniqueId().toString());
    }

    public void setKills(int kills) {
        String playerName = this.player.getUniqueId().toString();
        if (plugin.getFiles().getControl(RigoxFiles.MYSQL).getBoolean("mysql.enabled")) {
            registerDefault();
            String table = plugin.getFiles().getControl(RigoxFiles.MYSQL).getString("mysql.table");
            plugin.getData().setInt(table, "Kills", "Player", playerName, kills);
        } else {
            plugin.getData().getSQL().kills.put(playerName, kills);
        }
    }

    public void addKills() {
        setKills(getKills() + 1);
    }

    public int getDeaths() {
        if (plugin.getFiles().getControl(RigoxFiles.MYSQL).getBoolean("mysql.enabled")) {
            String table = plugin.getFiles().getControl(RigoxFiles.MYSQL).getString("mysql.table");
            return plugin.getData().getInt(table, "Deaths", "Player", this.player.getUniqueId().toString());
        }
        return plugin.getData().getSQL().deaths.get(this.player.getUniqueId().toString());
    }

    public void setDeaths(int deaths) {
        String playerName = this.player.getUniqueId().toString();
        if (plugin.getFiles().getControl(RigoxFiles.MYSQL).getBoolean("mysql.enabled")) {
            registerDefault();
            String table = plugin.getFiles().getControl(RigoxFiles.MYSQL).getString("mysql.table");
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
        String table = plugin.getFiles().getControl(RigoxFiles.MYSQL).getString("mysql.table");
        if (!plugin.getData().isRegistered(table, "Player", playerName)) {
            List<String> values = new ArrayList<>();
            values.add("Player-" + playerName);
            values.add("Kills-0");
            values.add("Deaths-0");
            values.add("Score-0");
            values.add("Wins-0");
            plugin.getData().register(table, values);
        }
    }
}

