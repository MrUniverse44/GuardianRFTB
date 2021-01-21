package dev.mruniverse.rigoxrftb.core.utils;

import dev.mruniverse.rigoxrftb.core.enums.Files;
import dev.mruniverse.rigoxrftb.core.enums.RigoxBoard;
import dev.mruniverse.rigoxrftb.core.RigoxRFTB;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
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
    public String replaceVariables(String text,Player player) {
        if(text.contains("<player_name>")) text = text.replace("<player_name>",player.getName());
        if(text.contains("<player_coins>")) text = text.replace("<player_coins>","" + 0);
        if(text.contains("<player_wins>")) text = text.replace("<player_wins>","" + 0);
        if(text.contains("<player_beast_kit>")) text = text.replace("<player_beast_kit>","Not selected");
        if(text.contains("<player_runner_kit>")) text = text.replace("<player_runner_kit>","Not selected");
        if(text.contains("<server_online>")) text = text.replace("<server_online>",plugin.getServer().getOnlinePlayers().size() + "");
        if(text.contains("<timeFormat>")) text = text.replace("<timeFormat>",getDateFormat());
        if(plugin.hasPAPI()) { text = PlaceholderAPI.setPlaceholders(player,text);
        }
        return text;
    }

}

