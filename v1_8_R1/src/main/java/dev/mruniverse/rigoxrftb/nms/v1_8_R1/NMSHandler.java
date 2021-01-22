package dev.mruniverse.rigoxrftb.nms.v1_8_R1;

import dev.mruniverse.rigoxrftb.core.nms.NMS;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;

public final class NMSHandler implements NMS {
    private HashMap<Player,EntityWither> bossBar = new HashMap<Player, EntityWither>();
    public void sendTitle(Player player, int fadeIn, int stay, int fadeOut, String title, String subtitle) {
        PlayerConnection pConn = ((CraftPlayer) player).getHandle().playerConnection;
        PacketPlayOutTitle pTitleInfo = new PacketPlayOutTitle(EnumTitleAction.TIMES, null, fadeIn, stay, fadeOut);
        pConn.sendPacket(pTitleInfo);
        if (subtitle != null) {
            subtitle = subtitle.replaceAll("%player%", player.getName());
            subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);
            IChatBaseComponent iComp = ChatSerializer.a("{\"text\": \"" + subtitle + "\"}");
            PacketPlayOutTitle pSubtitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, iComp);
            pConn.sendPacket(pSubtitle);
        }
        if (title != null) {
            title = title.replaceAll("%player%", player.getName());
            title = ChatColor.translateAlternateColorCodes('&', title);
            IChatBaseComponent iComp = ChatSerializer.a("{\"text\": \"" + title + "\"}");
            PacketPlayOutTitle pTitle = new PacketPlayOutTitle(EnumTitleAction.TITLE, iComp);
            pConn.sendPacket(pTitle);
        }
    }

    public void sendActionBar(Player player, String msg) {
        String toBC = ChatColor.translateAlternateColorCodes('&', msg);
        IChatBaseComponent icbc = ChatSerializer.a("{\"text\": \"" + toBC + "\"}");
        PacketPlayOutChat bar = new PacketPlayOutChat(icbc, (byte)2);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(bar);
    }

    public void sendBossBar(Player player, String message) {
        if(!BossHasPlayer(player)) {
            bossBar.put(player,new EntityWither(((CraftWorld)player.getWorld()).getHandle()));
        }
        Location witherLocation = getWitherLocation(player.getLocation());
        getBossBar(player).setCustomName(message);
        getBossBar(player).setHealth(100);
        getBossBar(player).setInvisible(true);
        getBossBar(player).setLocation(witherLocation.getX(), witherLocation.getY(), witherLocation.getZ(), 0, 0);
        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(getBossBar(player));
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
    }

    public void sendBossBar(Player player, String message,float percentage) {
        if(!BossHasPlayer(player)) {
            bossBar.put(player,new EntityWither(((CraftWorld)player.getWorld()).getHandle()));
        }
        if (percentage <= 0) {
            percentage = (float) 0.001;
        }
        Location witherLocation = getWitherLocation(player.getLocation());
        getBossBar(player).setCustomName(message);
        getBossBar(player).setHealth(percentage);
        getBossBar(player).setInvisible(true);
        getBossBar(player).setLocation(witherLocation.getX(), witherLocation.getY(), witherLocation.getZ(), 0, 0);
        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(getBossBar(player));
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
    }
    public void deleteBossBar(Player player) {
        EntityWither wither = bossBar.remove(player);
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(wither.getId());
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
    }
    public boolean BossHasPlayer(Player player) {
        return bossBar.containsKey(player);
    }
    private EntityWither getBossBar(Player player) {
        return bossBar.get(player);
    }
    private Location getWitherLocation(Location playerLocation) {
        return playerLocation.add(playerLocation.getDirection().multiply(60));
    }
}