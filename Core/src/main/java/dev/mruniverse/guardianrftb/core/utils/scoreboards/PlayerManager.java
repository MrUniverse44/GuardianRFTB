package dev.mruniverse.guardianrftb.core.utils.scoreboards;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.List;

@SuppressWarnings("deprecation")
public class PlayerManager {
    private Scoreboard scoreboard;
    private Objective sidebar;

    public PlayerManager(Player player) {
        if(Bukkit.getScoreboardManager() == null) return;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.sidebar = this.scoreboard.registerNewObjective("sidebar", "dummy");
        this.sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
        for (int i = 1; i <= 15; i++) {
            Team team = this.scoreboard.registerNewTeam("SLOT_" + i);
            team.addEntry(genEntry(i));
        }
        player.setScoreboard(this.scoreboard);
    }

    public void setTitle(String title) {
        title = ChatColor.translateAlternateColorCodes('&', title);
        this.sidebar.setDisplayName((title.length() > 32) ? title.substring(0, 32) : title);
    }

    public void setSlot(int slot, String text) {
        Team team = this.scoreboard.getTeam("SLOT_" + slot);
        if(team == null) return;
        String entry = genEntry(slot);
        if (!this.scoreboard.getEntries().contains(entry))
            this.sidebar.getScore(entry).setScore(slot);
        text = ChatColor.translateAlternateColorCodes('&', text);
        String pre = getFirstSplit(text);
        String suf = getFirstSplit(ChatColor.getLastColors(pre) + getSecondSplit(text));
        team.setPrefix(pre);
        team.setSuffix(suf);
    }

    public void removeSlot(int slot) {
        String entry = genEntry(slot);
        if (this.scoreboard.getEntries().contains(entry))
            this.scoreboard.resetScores(entry);
    }

    public void updateLines(List<String> list) {
        while (list.size() > 15)
            list.remove(list.size() - 1);
        int slot = list.size();
        if (slot < 15)
            for (int i = slot + 1; i <= 15; i++)
                removeSlot(i);
        for (String line : list) {
            setSlot(slot, line);
            slot--;
        }
    }

    private String genEntry(int slot) {
        return ChatColor.values()[slot].toString();
    }

    private String getFirstSplit(String s) {
        return (s.length() > 16) ? s.substring(0, 16) : s;
    }

    private String getSecondSplit(String s) {
        if (s.length() > 32)
            s = s.substring(0, 32);
        return (s.length() > 16) ? s.substring(16) : "";
    }
}
