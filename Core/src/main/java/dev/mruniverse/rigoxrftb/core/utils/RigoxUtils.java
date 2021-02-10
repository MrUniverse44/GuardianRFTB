package dev.mruniverse.rigoxrftb.core.utils;

import dev.mruniverse.rigoxrftb.core.RigoxRFTB;
import dev.mruniverse.rigoxrftb.core.enums.RigoxFiles;
import dev.mruniverse.rigoxrftb.core.enums.RigoxBoard;
import dev.mruniverse.rigoxrftb.core.games.Game;
import dev.mruniverse.rigoxrftb.core.games.GameTeam;
import dev.mruniverse.rigoxrftb.core.games.GameType;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RigoxUtils {
    private final RigoxRFTB plugin;
    public RigoxUtils(RigoxRFTB main) {
        plugin = main;
    }
    public void sendMessage(Player player,String message) {
        if(plugin.hasPAPI()) {
            message = PlaceholderAPI.setPlaceholders(player,message);
        }
        message = ChatColor.translateAlternateColorCodes('&',message);
        player.sendMessage(message);
    }
    public void sendMessage(CommandSender sender, String message) {
        message = ChatColor.translateAlternateColorCodes('&',message);
        sender.sendMessage(message);
    }
    public void sendTitle(Player player, int fadeInTime, int showTime, int fadeOutTime, String title, String subtitle) {
        try {
            if(plugin.hasPAPI()) {
                if(title != null) {
                    title = PlaceholderAPI.setPlaceholders(player, title);
                }
                if(subtitle != null) {
                    subtitle = PlaceholderAPI.setPlaceholders(player, subtitle);
                }
            }
            plugin.getNMSHandler().sendTitle(player,fadeInTime,showTime,fadeOutTime,title,subtitle);

        } catch (Throwable throwable) {
            plugin.getLogs().error("Can't send title for " + player.getName() + ".");
            plugin.getLogs().error(throwable);
        }
    }
    public void sendGameList(Player player, List<String> list, GameTeam winnerTeam) {
        if(list == null) list = new ArrayList<>();
        if(plugin.getPlayerData(player.getUniqueId()).getGame() != null) {
            String runnerRole = plugin.getStorage().getControl(RigoxFiles.SETTINGS).getString("roles.runners");
            String beastRole = plugin.getStorage().getControl(RigoxFiles.SETTINGS).getString("roles.beasts");
            String wT,lT;
            if(runnerRole == null) runnerRole = "Runners";
            if(beastRole == null) beastRole = "Beasts";
            if(winnerTeam.equals(GameTeam.RUNNERS)) {
                wT = runnerRole;
                lT = beastRole;
            } else {
                wT = beastRole;
                lT = runnerRole;
            }
            if(plugin.getPlayerData(player.getUniqueId()).getGame().getBeasts().contains(player)) {
                for(String line : list) {
                    line = line.replace("<isBeast>","")
                            .replace("<center>","             ")
                            .replace("%gameType%",plugin.getPlayerData(player.getUniqueId()).getGame().getGameType().getType())
                            .replace("%map_name%",plugin.getPlayerData(player.getUniqueId()).getGame().getName())
                            .replace("[bx]","▄")
                            .replace("%winner_team%",wT)
                            .replace("[px]","⚫")
                            .replace("%game%","+5")
                            .replace("%looser_team%",lT);
                    if(!line.contains("<isRunner>")) {
                        sendMessage(player, line);
                    }
                }
            } else {
                for(String line : list) {
                    line = line.replace("<isRunner>","")
                            .replace("<center>","             ")
                            .replace("%gameType%",plugin.getPlayerData(player.getUniqueId()).getGame().getGameType().getType())
                            .replace("%map_name%",plugin.getPlayerData(player.getUniqueId()).getGame().getName())
                            .replace("[bx]","▄")
                            .replace("%winner_team%",wT)
                            .replace("[px]","⚫")
                            .replace("%game%","+5")
                            .replace("%looser_team%",lT);
                    if(!line.contains("<isBeast>")) {
                        sendMessage(player, line);
                    }
                }
            }
        } else {
            for(String line : list) {
                line = line.replace("[bx]","▄")
                        .replace("[px]","⚫");
                sendMessage(player,line);
            }
        }
    }

    public void sendList(Player player,List<String> list) {
        if(list == null) list = new ArrayList<>();
        if(plugin.getPlayerData(player.getUniqueId()).getGame() != null) {
            if(plugin.getPlayerData(player.getUniqueId()).getGame().getBeasts().contains(player)) {
                for(String line : list) {
                    line = line.replace("<isBeast>","")
                            .replace("<center>","             ")
                            .replace("%gameType%",plugin.getPlayerData(player.getUniqueId()).getGame().getGameType().getType())
                            .replace("%map_name%",plugin.getPlayerData(player.getUniqueId()).getGame().getName())
                            .replace("[px]","⚫")
                            .replace("%game%","+5")
                            .replace("[bx]","▄");
                    if(!line.contains("<isRunner>")) {
                        sendMessage(player, line);
                    }
                }
            } else {
                for(String line : list) {
                    line = line.replace("<isRunner>","")
                            .replace("<center>","             ")
                            .replace("%gameType%",plugin.getPlayerData(player.getUniqueId()).getGame().getGameType().getType())
                            .replace("%map_name%",plugin.getPlayerData(player.getUniqueId()).getGame().getName())
                            .replace("[px]","⚫")
                            .replace("%game%","+5")
                            .replace("[bx]","▄");
                    if(!line.contains("<isBeast>")) {
                        sendMessage(player, line);
                    }
                }
            }
        } else {
            for(String line : list) {
                line = line.replace("[bx]","▄");
                sendMessage(player,line);
            }
        }
    }
    public void sendBossBar(Player player, String message) {
        if (player == null || message == null) return;
        if(plugin.hasPAPI()) { message = PlaceholderAPI.setPlaceholders(player,message); }
        message = ChatColor.translateAlternateColorCodes('&',message);
        plugin.getNMSHandler().sendBossBar(player,message);
    }
    public void sendBossBar(Player player, String message, float percentage) {
        if (player == null || message == null) return;
        if(plugin.hasPAPI()) { message = PlaceholderAPI.setPlaceholders(player,message); }
        message = ChatColor.translateAlternateColorCodes('&',message);
        plugin.getNMSHandler().sendBossBar(player,message,percentage);
    }
    public void sendActionbar(Player player, String message) {
        if (player == null || message == null) return;
        if(plugin.hasPAPI()) { message = PlaceholderAPI.setPlaceholders(player,message); }
        message = color(message);
        plugin.getNMSHandler().sendActionBar(player,message);
    }

    public String getTitle(RigoxBoard board) {
        switch (board) {
            case LOBBY:
                if (plugin.getStorage().getControl(RigoxFiles.SCOREBOARD).getString("scoreboards.lobby.title") != null) {
                    return plugin.getStorage().getControl(RigoxFiles.SCOREBOARD).getString("scoreboards.lobby.title");
                }
                return "";
            case WAITING:
            case STARTING:
            case SELECTING:
            case BEAST_SPAWN:
                if (plugin.getStorage().getControl(RigoxFiles.SCOREBOARD).getString("scoreboards.waiting.title") != null) {
                    return plugin.getStorage().getControl(RigoxFiles.SCOREBOARD).getString("scoreboards.waiting.title");
                }
                return "";
            case PLAYING:
                if (plugin.getStorage().getControl(RigoxFiles.SCOREBOARD).getString("scoreboards.playing.title") != null) {
                    return plugin.getStorage().getControl(RigoxFiles.SCOREBOARD).getString("scoreboards.playing.title");
                }
                return "";
            case WIN_BEAST_FOR_BEAST:
                if (plugin.getStorage().getControl(RigoxFiles.SCOREBOARD).getString("scoreboards.beastWin.forBeast.title") != null) {
                    return plugin.getStorage().getControl(RigoxFiles.SCOREBOARD).getString("scoreboards.beastWin.forBeast.title");
                }
                return "";
            case WIN_BEAST_FOR_RUNNERS:
                if (plugin.getStorage().getControl(RigoxFiles.SCOREBOARD).getString("scoreboards.beastWin.forRunners.title") != null) {
                    return plugin.getStorage().getControl(RigoxFiles.SCOREBOARD).getString("scoreboards.beastWin.forRunners.title");
                }
                return "";
            case WIN_RUNNERS_FOR_BEAST:
                if (plugin.getStorage().getControl(RigoxFiles.SCOREBOARD).getString("scoreboards.runnersWin.forBeast.title") != null) {
                    return plugin.getStorage().getControl(RigoxFiles.SCOREBOARD).getString("scoreboards.runnersWin.forBeast.title");
                }
                return "";
            case WIN_RUNNERS_FOR_RUNNERS:
                if (plugin.getStorage().getControl(RigoxFiles.SCOREBOARD).getString("scoreboards.runnersWin.forRunners.title") != null) {
                    return plugin.getStorage().getControl(RigoxFiles.SCOREBOARD).getString("scoreboards.runnersWin.forRunners.title");
                }
                return "";
        }
        return "";
    }
    public String color(String message) {
        return ChatColor.translateAlternateColorCodes('&',message);
    }
    public List<String> getLines(RigoxBoard board,Player player) {
        List<String> lines = new ArrayList<>();
        switch(board) {
            case LOBBY:
            default:
                for (String line : plugin.getStorage().getControl(RigoxFiles.SCOREBOARD).getStringList("scoreboards.lobby.lines")) {
                    line = replaceVariables(line, player);
                    lines.add(line);
                }
                return lines;
            case WAITING:
                for (String line : plugin.getStorage().getControl(RigoxFiles.SCOREBOARD).getStringList("scoreboards.waiting.lines")) {
                    if (!line.contains("<isStarting>") && !line.contains("<isSelecting>") && !line.contains("<BeastAppear>")) {
                        if (line.contains("<isWaiting>")) line = line.replace("<isWaiting>", "");
                        line = replaceVariables(line, player);
                        lines.add(line);
                    }
                }
                return lines;
            case SELECTING:
                for (String line : plugin.getStorage().getControl(RigoxFiles.SCOREBOARD).getStringList("scoreboards.waiting.lines")) {
                    if (!line.contains("<isWaiting>") && !line.contains("<isStarting>") && !line.contains("<BeastAppear>")) {
                        if (line.contains("<isSelecting>")) line = line.replace("<isSelecting>", "");
                        line = replaceVariables(line, player);
                        lines.add(line);
                    }
                }
                return lines;
            case STARTING:
                for (String line : plugin.getStorage().getControl(RigoxFiles.SCOREBOARD).getStringList("scoreboards.waiting.lines")) {
                    if (!line.contains("<isWaiting>") && !line.contains("<isSelecting>") && !line.contains("<BeastAppear>")) {
                        if (line.contains("<isStarting>")) line = line.replace("<isStarting>", "");
                        line = replaceVariables(line, player);
                        lines.add(line);
                    }
                }
                return lines;
            case BEAST_SPAWN:
                for (String line : plugin.getStorage().getControl(RigoxFiles.SCOREBOARD).getStringList("scoreboards.waiting.lines")) {
                    if (!line.contains("<isWaiting>") && !line.contains("<isSelecting>") && !line.contains("<isStarting>")) {
                        if (line.contains("<BeastAppear>")) line = line.replace("<BeastAppear>", "");
                        line = replaceVariables(line, player);
                        lines.add(line);
                    }
                }
                return lines;
            case PLAYING:
                for (String line : plugin.getStorage().getControl(RigoxFiles.SCOREBOARD).getStringList("scoreboards.playing.lines")) {
                    line = replaceVariables(line, player);
                    lines.add(line);
                }
                return lines;
            case WIN_BEAST_FOR_BEAST:
                for (String line : plugin.getStorage().getControl(RigoxFiles.SCOREBOARD).getStringList("scoreboards.beastWin.forBeast.lines")) {
                    line = replaceVariables(line, player);
                    lines.add(line);
                }
                return lines;
            case WIN_BEAST_FOR_RUNNERS:
                for (String line : plugin.getStorage().getControl(RigoxFiles.SCOREBOARD).getStringList("scoreboards.beastWin.forRunners.lines")) {
                    line = replaceVariables(line, player);
                    lines.add(line);
                }
                return lines;
            case WIN_RUNNERS_FOR_BEAST:
                for (String line : plugin.getStorage().getControl(RigoxFiles.SCOREBOARD).getStringList("scoreboards.runnersWin.forBeast.lines")) {
                    line = replaceVariables(line, player);
                    lines.add(line);
                }
                return lines;
            case WIN_RUNNERS_FOR_RUNNERS:
                for (String line : plugin.getStorage().getControl(RigoxFiles.SCOREBOARD).getStringList("scoreboards.runnersWin.forRunners.lines")) {
                    line = replaceVariables(line, player);
                    lines.add(line);
                }
                return lines;
        }
    }

    public String getDateFormat() {
        String dateFormat = plugin.getStorage().getControl(RigoxFiles.SETTINGS).getString("settings.dateFormat");
        if(dateFormat == null) dateFormat = "dd/MM/yyyy";
        return "" + (new SimpleDateFormat(dateFormat).format(Calendar.getInstance().getTime()));

    }
    public String getStringFromLocation(Location location) {
        try {
            World currentWorld = location.getWorld();
            String worldName = "world";
            if(currentWorld != null) worldName = location.getWorld().getName();
            return worldName + "," + location.getX() + "," + location.getY() + "," + location.getZ() + "," + location.getYaw() + "," + location.getPitch();
        }catch (Throwable throwable) {
            plugin.getLogs().error("Can't get String from location " + location.toString());
            plugin.getLogs().error(throwable);
        }
        return null;
    }
    public Location getLocationFromString(String location) {
        if(!location.equalsIgnoreCase("notSet")) {
            String[] loc = location.split(",");
            World w = Bukkit.getWorld(loc[0]);
            if(w != null) {
                double x = Double.parseDouble(loc[1]);
                double y = Double.parseDouble(loc[2]);
                double z = Double.parseDouble(loc[3]);
                float yaw = Float.parseFloat(loc[4]);
                float pitch = Float.parseFloat(loc[5]);
                return new Location(w, x, y, z, yaw, pitch);
            }
            plugin.getLogs().error("Can't get world named: " + loc[0]);
            return null;
        }
        return null;
    }
    public String replaceVariables(String text,Player player) {
        text = text.replace("<player_name>",player.getName());
        if(plugin.getPlayerData(player.getUniqueId()) != null) {
            text = text.replace("<player_coins>", "" + plugin.getPlayerData(player.getUniqueId()).getCoins())
                    .replace("<player_wins>","" + plugin.getPlayerData(player.getUniqueId()).getWins());
        } else {
            text = text.replace("<player_coins>", "0")
                    .replace("<player_wins>", "0");
        }
        text = text.replace("<player_beast_kit>","Not selected")
                .replace("<player_runner_kit>","Not selected")
                .replace("<server_online>",plugin.getServer().getOnlinePlayers().size() + "")
                .replace("<timeFormat>",getDateFormat());

        if(plugin.getPlayerData(player.getUniqueId()) != null) {
            if (plugin.getPlayerData(player.getUniqueId()).getGame() != null) {
                Game playerGame = plugin.getPlayerData(player.getUniqueId()).getGame();
                text = text.replace("<arena_name>",playerGame.getName())
                        .replace("<arena_online>","" + playerGame.getPlayers().size())
                        .replace("<arena_max>","" + playerGame.max)
                        .replace("<arena_need>","" + playerGame.getNeedPlayers())
                        .replace("<arena_time_text>",getSecondsLeft(playerGame.starting))
                        .replace("<arena_beast>",getBeast(playerGame))
                        .replace("<arena_runners>","" + playerGame.getRunners().size())
                        .replace("<arena_mode>",playerGame.getGameType().getType())
                        .replace("<arena_timeLeft>",playerGame.timer + "")
                        .replace("<arena_status>",playerGame.gameStatus.getStatus())
                        .replace("<player_role>",getRole(playerGame,player));
                if(plugin.getPlayerData(player.getUniqueId()).getBoard().equals(RigoxBoard.SELECTING)) {
                    text = text.replace("<arena_time_number>", playerGame.starting + "");
                } else {
                    text = text.replace("<arena_time_number>", playerGame.fakeStarting + "");
                }
            }
        }
        if(plugin.hasPAPI()) { text = PlaceholderAPI.setPlaceholders(player,text); }
        return text;
    }
    private String getRole(Game game,Player player) {
        if(game.getBeasts().contains(player)) {
            return plugin.getStorage().getControl(RigoxFiles.SETTINGS).getString("roles.beast");
        }
        return plugin.getStorage().getControl(RigoxFiles.SETTINGS).getString("roles.runner");
    }
    private String getSecondsLeft(int time) {
        if(time != 1) {
            return plugin.getStorage().getControl(RigoxFiles.SETTINGS).getString("times.seconds");
        }
        return plugin.getStorage().getControl(RigoxFiles.SETTINGS).getString("times.second");
    }
    public Player getRandomBeast(Player player) {
        if(plugin.getPlayerData(player.getUniqueId()).getGame() != null) {
            if(plugin.getPlayerData(player.getUniqueId()).getGame().getBeasts().size() != 0) {
                return plugin.getPlayerData(player.getUniqueId()).getGame().getBeasts().get(0);
            }
            return player;
        }
        return player;
    }
    public boolean isBeast(Player player) {
        if(plugin.getPlayerData(player.getUniqueId()) == null) return false;
        if(plugin.getPlayerData(player.getUniqueId()).getGame() != null) {
            return plugin.getPlayerData(player.getUniqueId()).getGame().getBeasts().contains(player);
        }
        return false;
    }
    private String getBeast(Game game) {
        if(game.getGameType().equals(GameType.DOUBLE_BEAST)) {
            return game.getBeasts().size()+"";
        }
        if(game.getBeasts().size() != 0) {
            return game.getBeasts().get(0).getName();
        }
        return "none";
    }

}

