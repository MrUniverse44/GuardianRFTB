package dev.mruniverse.rigoxrftb.core.utils;

import dev.mruniverse.rigoxrftb.core.enums.Files;
import dev.mruniverse.rigoxrftb.core.enums.RigoxBoard;
import dev.mruniverse.rigoxrftb.core.RigoxRFTB;
import dev.mruniverse.rigoxrftb.core.games.Game;
import dev.mruniverse.rigoxrftb.core.games.GameStatus;
import dev.mruniverse.rigoxrftb.core.games.GameType;
import me.clip.placeholderapi.PlaceholderAPI;
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
    public void sendList(Player player,List<String> list) {
        if(list == null) list = new ArrayList<>();
        if(plugin.getPlayerData(player.getUniqueId()).getGame() != null) {
            if(plugin.getPlayerData(player.getUniqueId()).getGame().beasts.contains(player)) {
                for(String line : list) {
                    line = line.replace("<isBeast>","")
                            .replace("<center>","             ")
                            .replace("%gameType%",plugin.getPlayerData(player.getUniqueId()).getGame().gameType.toString())
                            .replace("%map_name%",plugin.getPlayerData(player.getUniqueId()).getGame().getName())
                            .replace("[bx]","▄");
                    if(!line.contains("<isRunner>")) {
                        sendMessage(player, line);
                    }
                }
            } else {
                for(String line : list) {
                    line = line.replace("<isRunner>","")
                            .replace("<center>","             ")
                            .replace("%gameType%",plugin.getPlayerData(player.getUniqueId()).getGame().gameType.toString())
                            .replace("%map_name%",plugin.getPlayerData(player.getUniqueId()).getGame().getName())
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
    public void sendActionbar(Player player, String message) {
        if (player == null || message == null) return;
        if(plugin.hasPAPI()) { message = PlaceholderAPI.setPlaceholders(player,message); }
        message = color(message);
        plugin.getNMSHandler().sendActionBar(player,message);
    }

    public String getTitle(RigoxBoard board) {
        if(board.equals(RigoxBoard.LOBBY)) {
            if (plugin.getFiles().getControl(Files.SCOREBOARD).getString("scoreboards.lobby.title") != null) {
                return plugin.getFiles().getControl(Files.SCOREBOARD).getString("scoreboards.lobby.title");
            }
            return "";
        }
        if(board.equals(RigoxBoard.WAITING) || board.equals(RigoxBoard.STARTING)) {
            if (plugin.getFiles().getControl(Files.SCOREBOARD).getString("scoreboards.waiting.title") != null) {
                return plugin.getFiles().getControl(Files.SCOREBOARD).getString("scoreboards.waiting.title");
            }
            return "";
        }
        if(board.equals(RigoxBoard.PLAYING)) {
            if (plugin.getFiles().getControl(Files.SCOREBOARD).getString("scoreboards.playing.title") != null) {
                return plugin.getFiles().getControl(Files.SCOREBOARD).getString("scoreboards.playing.title");
            }
            return "";
        }
        if(board.equals(RigoxBoard.WIN_BEAST_FOR_BEAST)) {
            if (plugin.getFiles().getControl(Files.SCOREBOARD).getString("scoreboards.beastWin.forBeast.title") != null) {
                return plugin.getFiles().getControl(Files.SCOREBOARD).getString("scoreboards.beastWin.forBeast.title");
            }
            return "";
        }
        if(board.equals(RigoxBoard.WIN_BEAST_FOR_RUNNERS)) {
            if (plugin.getFiles().getControl(Files.SCOREBOARD).getString("scoreboards.beastWin.forRunners.title") != null) {
                return plugin.getFiles().getControl(Files.SCOREBOARD).getString("scoreboards.beastWin.forRunners.title");
            }
            return "";
        }
        if(board.equals(RigoxBoard.WIN_RUNNERS_FOR_BEAST)) {
            if (plugin.getFiles().getControl(Files.SCOREBOARD).getString("scoreboards.runnersWin.forBeast.title") != null) {
                return plugin.getFiles().getControl(Files.SCOREBOARD).getString("scoreboards.runnersWin.forBeast.title");
            }
            return "";
        }
        if(board.equals(RigoxBoard.WIN_RUNNERS_FOR_RUNNERS)) {
            if (plugin.getFiles().getControl(Files.SCOREBOARD).getString("scoreboards.runnersWin.forRunners.title") != null) {
                return plugin.getFiles().getControl(Files.SCOREBOARD).getString("scoreboards.runnersWin.forRunners.title");
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
        if(board.equals(RigoxBoard.LOBBY)) {
            for(String line : plugin.getFiles().getControl(Files.SCOREBOARD).getStringList("scoreboards.lobby.lines")) {
                line = replaceVariables(line,player);
                lines.add(line);
            }
            return lines;
        }
        if(board.equals(RigoxBoard.WAITING)) {
            for(String line : plugin.getFiles().getControl(Files.SCOREBOARD).getStringList("scoreboards.waiting.lines")) {
                if(!line.contains("<isStarting>")) {
                    if(line.contains("<isWaiting>")) line = line.replace("<isWaiting>","");
                    line = replaceVariables(line, player);
                    lines.add(line);
                }
            }
            return lines;
        }
        if(board.equals(RigoxBoard.STARTING)) {
            for(String line : plugin.getFiles().getControl(Files.SCOREBOARD).getStringList("scoreboards.waiting.lines")) {
                if(!line.contains("<isWaiting>")) {
                    if(line.contains("<isStarting>")) line = line.replace("<isStarting>","");
                    line = replaceVariables(line, player);
                    lines.add(line);
                }
            }
            return lines;
        }
        if(board.equals(RigoxBoard.PLAYING)) {
            for(String line : plugin.getFiles().getControl(Files.SCOREBOARD).getStringList("scoreboards.playing.lines")) {
                line = replaceVariables(line,player);
                lines.add(line);
            }
            return lines;
        }
        if(board.equals(RigoxBoard.WIN_BEAST_FOR_BEAST)) {
            for(String line : plugin.getFiles().getControl(Files.SCOREBOARD).getStringList("scoreboards.beastWin.forBeast.lines")) {
                line = replaceVariables(line,player);
                lines.add(line);
            }
            return lines;
        }
        if(board.equals(RigoxBoard.WIN_BEAST_FOR_RUNNERS)) {
            for(String line : plugin.getFiles().getControl(Files.SCOREBOARD).getStringList("scoreboards.beastWin.forRunners.lines")) {
                line = replaceVariables(line,player);
                lines.add(line);
            }
            return lines;
        }
        if(board.equals(RigoxBoard.WIN_RUNNERS_FOR_BEAST)) {
            for(String line : plugin.getFiles().getControl(Files.SCOREBOARD).getStringList("scoreboards.runnersWin.forBeast.lines")) {
                line = replaceVariables(line,player);
                lines.add(line);
            }
            return lines;
        }
        if(board.equals(RigoxBoard.WIN_RUNNERS_FOR_RUNNERS)) {
            for(String line : plugin.getFiles().getControl(Files.SCOREBOARD).getStringList("scoreboards.runnersWin.forRunners.lines")) {
                line = replaceVariables(line,player);
                lines.add(line);
            }
            return lines;
        }
        return new ArrayList<>();
    }
    public String getDateFormat() {
        String dateFormat = plugin.getFiles().getControl(Files.SETTINGS).getString("settings.dateFormat");
        if(dateFormat == null) dateFormat = "dd/MM/yyyy";
        return "" + (new SimpleDateFormat(dateFormat).format(Calendar.getInstance().getTime()));

    }
    public String getStringFromLocation(Location location) {
        try {
            return location.getWorld().getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ() + "," + location.getYaw() + "," + location.getPitch();
        }catch (Throwable throwable) {
            plugin.getLogs().error("Can't get String from location " + location.toString());
            plugin.getLogs().error(throwable);
        }
        return null;
    }
    public Location getLocationFromString(String location) {
        if(!location.equalsIgnoreCase("notSet")) {
            String[] loc = location.split(",");
            World w = plugin.getServer().getWorld(loc[0]);
            double x = Double.parseDouble(loc[1]);
            double y = Double.parseDouble(loc[2]);
            double z = Double.parseDouble(loc[3]);
            float yaw = Float.parseFloat(loc[4]);
            float pitch = Float.parseFloat(loc[5]);
            return new Location(w, x, y, z, yaw, pitch);
        }
        return null;
    }
    public String replaceVariables(String text,Player player) {
        if(text.contains("<player_name>")) text = text.replace("<player_name>",player.getName());
        if(text.contains("<player_coins>")) text = text.replace("<player_coins>","" + 0);
        if(text.contains("<player_wins>")) text = text.replace("<player_wins>","" + 0);
        if(text.contains("<player_beast_kit>")) text = text.replace("<player_beast_kit>","Not selected");
        if(text.contains("<player_runner_kit>")) text = text.replace("<player_runner_kit>","Not selected");
        if(text.contains("<server_online>")) text = text.replace("<server_online>",plugin.getServer().getOnlinePlayers().size() + "");
        if(text.contains("<timeFormat>")) text = text.replace("<timeFormat>",getDateFormat());
        if(plugin.getPlayerData(player.getUniqueId()) != null) {
            if (plugin.getPlayerData(player.getUniqueId()).getGame() != null) {
                Game playerGame = plugin.getPlayerData(player.getUniqueId()).getGame();
                if (text.contains("<arena_name>")) text = text.replace("<arena_name>", playerGame.getName());
                if (text.contains("<arena_online>"))
                    text = text.replace("<arena_online>", playerGame.players.size() + "");
                if (text.contains("<arena_max>")) text = text.replace("<arena_max>", playerGame.max + "");
                if (text.contains("<arena_need>")) {
                    if(playerGame.getNeedPlayers() != 0) {
                        text = text.replace("<arena_need>", playerGame.getNeedPlayers() + "");
                    } else {
                        if(playerGame.gameStatus.equals(GameStatus.WAITING)) {
                            playerGame.gameStatus = GameStatus.STARTING;
                            for(Player pl : playerGame.players) {
                                plugin.getPlayerData(pl.getUniqueId()).setBoard(RigoxBoard.STARTING);
                            }
                        }
                    }
                }
                if (text.contains("<arena_time_number>"))
                    text = text.replace("<arena_time_number>", playerGame.starting + "");
                if (text.contains("<arena_time_text>"))
                    text = text.replace("<arena_time_text>", getSecondsLeft(playerGame.starting));
                if (text.contains("<arena_beast>")) text = text.replace("<arena_beast>", getBeast(playerGame));
                if (text.contains("<arena_runners>"))
                    text = text.replace("<arena_runners>", playerGame.runners.size() + "");
                if (text.contains("<arena_mode>")) text = text.replace("<arena_mode>", playerGame.gameType.name());
                if (text.contains("<arena_timeLeft>")) text = text.replace("<arena_timeLeft>", playerGame.timer + "");
                if (text.contains("<player_role>")) text = text.replace("<player_role>", getRole(playerGame, player));
            }
        }
        if(plugin.hasPAPI()) { text = PlaceholderAPI.setPlaceholders(player,text);
        }
        return text;
    }
    private String getRole(Game game,Player player) {
        if(game.beasts.contains(player)) {
            return plugin.getFiles().getControl(Files.SETTINGS).getString("roles.beast");
        }
        return plugin.getFiles().getControl(Files.SETTINGS).getString("roles.runner");
    }
    private String getSecondsLeft(int time) {
        if(time != 1) {
            return plugin.getFiles().getControl(Files.SETTINGS).getString("times.seconds");
        }
        return plugin.getFiles().getControl(Files.SETTINGS).getString("times.second");
    }
    private String getBeast(Game game) {
        if(game.gameType.equals(GameType.DOUBLE_BEAST)) {
            return game.beasts.size()+"";
        }
        if(game.beasts.size() != 0) {
            return game.beasts.get(1).getName();
        }
        return "none";
    }

}

