package dev.mruniverse.rigoxrftb.core.nms;

import org.bukkit.entity.Player;

public interface NMS {
    void sendTitle(Player player, int fadeIn, int stay, int fadeOut, String title, String subtitle);
    void sendActionBar(Player player, String msg);
    void sendBossBar(Player player, String message);
    void sendBossBar(Player player, String message, Integer percentage);
}
