package dev.mruniverse.guardianrftb.core.utils;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class TextUtilities {
    public static String recolor(String message) {
        return ChatColor.translateAlternateColorCodes('&',message);
    }
    public static List<String> recolorLore(List<String> loreToRecolor) {
        List<String> recolored = new ArrayList<>();
        for(String color : loreToRecolor) {
            recolored.add(ChatColor.translateAlternateColorCodes('&',color));
        }
        return recolored;
    }
}
