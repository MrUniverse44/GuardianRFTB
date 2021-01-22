package dev.mruniverse.rigoxrftb.nms.v1_16_R1;

import dev.mruniverse.rigoxrftb.core.nms.NMS;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_16_R1.*;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class NMSHandler implements NMS {
    public void sendTitle(Player player, int fadeIn, int stay, int fadeOut, String title, String subtitle) {
        PlayerConnection pConn = (((CraftPlayer)player).getHandle()).playerConnection;
        PacketPlayOutTitle pTitleInfo = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TIMES, null, fadeIn, stay, fadeOut);
        pConn.sendPacket(pTitleInfo);
        if (subtitle != null) {
            subtitle = subtitle.replaceAll("%player%", player.getName());
            subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);
            IChatMutableComponent iChatMutableComponent = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + subtitle + "\"}");
            PacketPlayOutTitle pSubtitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, iChatMutableComponent);
            pConn.sendPacket(pSubtitle);
        }
        if (title != null) {
            title = title.replaceAll("%player%", player.getName());
            title = ChatColor.translateAlternateColorCodes('&', title);
            IChatMutableComponent iChatMutableComponent = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + title + "\"}");
            PacketPlayOutTitle pTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, iChatMutableComponent);
            pConn.sendPacket(pTitle);
        }
    }

    public void sendActionBar(Player player, String msg) {
        String toBC = ChatColor.translateAlternateColorCodes('&', msg);
        IChatMutableComponent iChatMutableComponent = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + toBC + "\"}");
        PacketPlayOutTitle bar = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.ACTIONBAR, iChatMutableComponent);
        (((CraftPlayer)player).getHandle()).playerConnection.sendPacket(bar);
    }
    public void sendBossBar(Player player, String message) {

    }

    public void sendBossBar(Player player, String message, float percentage) {

    }

    public void deleteBossBar(Player player) {

    }

    public boolean BossHasPlayer(Player player) {
        return false;
    }
}
