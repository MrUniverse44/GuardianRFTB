package dev.mruniverse.rigoxrftb.rigoxrftb.utils;

import dev.mruniverse.rigoxrftb.rigoxrftb.RigoxRFTB;
import dev.mruniverse.rigoxrftb.rigoxrftb.enums.Files;
import dev.mruniverse.rigoxrftb.rigoxrftb.enums.RigoxBoard;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RigoxUtils {
    private final RigoxRFTB plugin;
    private String NameVersion;
    public RigoxUtils(RigoxRFTB main) {
        plugin = main;
        NameVersion = plugin.getServer().getClass().getPackage().getName();
        NameVersion = NameVersion.substring(NameVersion.lastIndexOf(".") + 1);
    }
    public void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (Throwable throwable) {
            plugin.getLogs().error("Can't send packet for " + player.getName() + ".");
            plugin.getLogs().error(throwable);
        }
    }

    public  Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server"
                    + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + name);
        } catch (Throwable throwable) {
            plugin.getLogs().error("Can't load this Minecraft version for Titles :(");
            plugin.getLogs().error(throwable);
        }
        return null;
    }
    public void sendTitle(Player player, int fadeInTime, int showTime, int fadeOutTime, String title, String subtitle) {
        try {
            if(plugin.hasPAPI()) {
                title = PlaceholderAPI.setPlaceholders(player,title);
                subtitle = PlaceholderAPI.setPlaceholders(player,subtitle);
            }
            title = color(title);
            subtitle = color(subtitle);
            Object chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class)
                    .invoke(null, "{\"text\": \"" + title + "\"}");
            Constructor<?> titleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(
                    getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"),
                    int.class, int.class, int.class);
            Object packet = titleConstructor.newInstance(
                    getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null), chatTitle,
                    fadeInTime, showTime, fadeOutTime);

            Object chatsTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class)
                    .invoke(null, "{\"text\": \"" + subtitle + "\"}");
            Constructor<?> stitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(
                    getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"),
                    int.class, int.class, int.class);
            Object spacket = stitleConstructor.newInstance(
                    getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE").get(null), chatsTitle,
                    fadeInTime, showTime, fadeOutTime);

            sendPacket(player, packet);
            sendPacket(player, spacket);
        } catch (Throwable throwable) {
            plugin.getLogs().error("Can't send title for " + player.getName() + ".");
            plugin.getLogs().error(throwable);
        }
    }
    public void sendActionbar(Player player, String message) {
        if (player == null || message == null) return;
        if(plugin.hasPAPI()) { message = PlaceholderAPI.setPlaceholders(player,message); }
        message = color(message);

        //1.10 and up
        if (!NameVersion.startsWith("v1_9_R") && !NameVersion.startsWith("v1_8_R")) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
            return;
        }

        //1.8.x and 1.9.x
        try {
            Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + NameVersion + ".entity.CraftPlayer");
            Object craftPlayer = craftPlayerClass.cast(player);

            Class<?> ppoc = Class.forName("net.minecraft.server." + NameVersion + ".PacketPlayOutChat");
            Class<?> packet = Class.forName("net.minecraft.server." + NameVersion + ".Packet");
            Object packetPlayOutChat;
            Class<?> chat = Class.forName("net.minecraft.server." + NameVersion + (NameVersion.equalsIgnoreCase("v1_8_R1") ? ".ChatSerializer" : ".ChatComponentText"));
            Class<?> chatBaseComponent = Class.forName("net.minecraft.server." + NameVersion + ".IChatBaseComponent");

            Method method = null;
            if (NameVersion.equalsIgnoreCase("v1_8_R1")) method = chat.getDeclaredMethod("a", String.class);

            Object object = NameVersion.equalsIgnoreCase("v1_8_R1") ? chatBaseComponent.cast(method.invoke(chat, "{'text': '" + message + "'}")) : chat.getConstructor(new Class[]{String.class}).newInstance(message);
            packetPlayOutChat = ppoc.getConstructor(new Class[]{chatBaseComponent, Byte.TYPE}).newInstance(object, (byte) 2);

            Method handle = craftPlayerClass.getDeclaredMethod("getHandle");
            Object iCraftPlayer = handle.invoke(craftPlayer);
            Field playerConnectionField = iCraftPlayer.getClass().getDeclaredField("playerConnection");
            Object playerConnection = playerConnectionField.get(iCraftPlayer);
            Method sendPacket = playerConnection.getClass().getDeclaredMethod("sendPacket", packet);
            sendPacket.invoke(playerConnection, packetPlayOutChat);
        } catch (Throwable throwable) {
            plugin.getLogs().error("Can't send action bar for " + player.getName() + " with message: " + message);
            plugin.getLogs().error(throwable);
        }
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
