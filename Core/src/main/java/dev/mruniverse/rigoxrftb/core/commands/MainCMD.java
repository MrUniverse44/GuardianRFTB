package dev.mruniverse.rigoxrftb.core.commands;

import dev.mruniverse.rigoxrftb.core.RigoxRFTB;
import dev.mruniverse.rigoxrftb.core.enums.Files;
import dev.mruniverse.rigoxrftb.core.enums.SaveMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MainCMD implements CommandExecutor {
    private final String command;
    private final RigoxRFTB plugin;
    public MainCMD(String cmd, RigoxRFTB main) {
        this.command = cmd;
        this.plugin = main;
    }
    @SuppressWarnings("ConstantConditions")
    private boolean hasPermission(CommandSender sender, String permission) {
        if(sender instanceof Player) {
            Player player = (Player)sender;
            if (!player.hasPermission(permission)) {
                try {
                    plugin.getUtils().sendMessage(player, plugin.getFiles().getControl(Files.MESSAGES).getString("messages.others.no-perms").replace("%permission%", permission));
                } catch (Throwable throwable) {
                    plugin.getLogs().error("Can't send permission message to " + sender.getName());
                    plugin.getLogs().error(throwable);
                }
            }
            return player.hasPermission(permission);
        }
        return true;
    }
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        try {
            if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
                if (hasPermission(sender, "RigoxRFTB.help")) {
                    plugin.getUtils().sendMessage(sender,"&8» &7/" + command + " join &b- &eOpen Game Selector");
                    plugin.getUtils().sendMessage(sender,"&8» &7/" + command + " leave &b- &eLeave from your current game");
                    if(sender instanceof Player) {
                        if(sender.hasPermission("RigoxRFTB.admin.help")) {
                            plugin.getUtils().sendMessage(sender, "&8» &7/" + command + " &cadmin &b- &eShow admin commands.");
                        }
                    } else {
                        plugin.getUtils().sendMessage(sender, "&8» &7/" + command + " &cadmin &b- &eShow admin commands.");
                    }
                }
                return true;
            }
            if(args[0].equalsIgnoreCase("leave")) {
                if(sender instanceof Player) {
                    Player player = (Player)sender;
                    if (plugin.getPlayerData(player.getUniqueId()).getGame() != null) {
                        plugin.getPlayerData(player.getUniqueId()).getGame().leave(player);
                        return true;
                    }
                    plugin.getUtils().sendMessage(sender, "&cYou aren't playing");
                    return true;
                }
                plugin.getUtils().sendMessage(sender,"&cThis command only can be used by players.");
                return true;
            }
            if(args[0].equalsIgnoreCase("join")) {
                if(sender instanceof Player) {
                    if(hasPermission(sender,"RigoxRFTB.menu.join")) plugin.getGameManager().openMenu((Player)sender);
                    return true;
                }
                plugin.getUtils().sendMessage(sender,"&cThis command only can be used by players.");
                return true;
            }
            if(args[0].equalsIgnoreCase("admin")) {
                if(args.length == 1 || args[1].equalsIgnoreCase("help")) {
                    if(hasPermission(sender,"RigoxRFTB.admin.help")) {
                        plugin.getUtils().sendMessage(sender, "&8» &7/" + command + " &cadmin create (gameName) &b- &eCreate arena.");
                        plugin.getUtils().sendMessage(sender, "&8» &7/" + command + " &cadmin delete (gameName) &b- &eDelete arena.");
                        plugin.getUtils().sendMessage(sender, "&8» &7/" + command + " &cadmin setWaiting (gameName) &b- &eSet waitingLocation.");
                        plugin.getUtils().sendMessage(sender, "&8» &7/" + command + " &cadmin setSelectedBeast (gameName) &b- &eSet selected Beast Location.");
                        plugin.getUtils().sendMessage(sender, "&8» &7/" + command + " &cadmin setBeastSpawn (gameName) &b- &eSet Beast Spawn Location.");
                        plugin.getUtils().sendMessage(sender, "&8» &7/" + command + " &cadmin setRunnerSpawn (gameName) &b- &eSet Runner Spawn Location.");
                        plugin.getUtils().sendMessage(sender, "&8» &7/" + command + " &cadmin setMin (gameName) (min) &b- &eSet min players.");
                        plugin.getUtils().sendMessage(sender, "&8» &7/" + command + " &cadmin setMax (gameName) (max) &b- &eSet max players.");
                        plugin.getUtils().sendMessage(sender, "&8» &7/" + command + " &cadmin setMode (gameName) (gameType) &b- &eSet gameType.");
                        plugin.getUtils().sendMessage(sender, "&8» &7/" + command + " &cadmin enable (gameName) &b- &eEnable game to play.");
                        plugin.getUtils().sendMessage(sender, "&8» &7/" + command + " &cadmin modes &b- &eShow all modes.");
                        plugin.getUtils().sendMessage(sender, "&8» &7/" + command + " &cadmin list &b- &eShow all games.");
                        plugin.getUtils().sendMessage(sender, "&8» &7/" + command + " &cadmin reload &b- &eReload command.");
                        return true;
                    }
                    return true;
                }
                if(args[1].equalsIgnoreCase("reload")) {
                    if(hasPermission(sender,"RigoxRFTB.admin.reload")) {
                        plugin.getFiles().reloadFile(SaveMode.ALL);
                        plugin.getUtils().sendMessage(sender, "&8» &aReload completed!");
                        return true;
                    }
                    return true;
                }
                return true;
            }
        }catch (Throwable throwable) {
            plugin.getLogs().error("Can't execute main command :(");
            plugin.getLogs().error(throwable);
        }
        return true;
    }
}
