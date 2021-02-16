package dev.mruniverse.rigoxrftb.core.commands;

import dev.mruniverse.rigoxrftb.core.RigoxRFTB;
import dev.mruniverse.rigoxrftb.core.enums.RigoxFiles;
import dev.mruniverse.rigoxrftb.core.enums.SaveMode;
import dev.mruniverse.rigoxrftb.core.games.GameType;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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
                    plugin.getUtils().sendMessage(player, plugin.getStorage().getControl(RigoxFiles.MESSAGES).getString("messages.others.no-perms").replace("%permission%", permission));
                } catch (Throwable throwable) {
                    plugin.getLogs().error("Can't send permission message to " + sender.getName());
                    plugin.getLogs().error(throwable);
                }
            }
            return player.hasPermission(permission);
        }
        return true;
    }
    @SuppressWarnings({"ConstantConditions"})
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
                    if(args.length == 1) {
                        if(hasPermission(sender,"RigoxRFTB.menu.join")) ((Player)sender).openInventory(plugin.getGameManager().gameMenu.getInventory());
                        return true;
                    }
                    plugin.getGameManager().joinGame((Player)sender,args[1]);
                    //if(hasPermission(sender,"RigoxRFTB.menu.join")) plugin.getGameManager().openMenu((Player)sender);
                    return true;
                }
                plugin.getUtils().sendMessage(sender,"&cThis command only can be used by players.");
                return true;
            }
            if(args[0].equalsIgnoreCase("admin")) {
                if(args.length == 1 || args[1].equalsIgnoreCase("help")) {
                    if(hasPermission(sender,"RigoxRFTB.admin.help")) {
                        plugin.getUtils().sendMessage(sender, "&8» &7/" + command + " &cadmin setlobby &b- &eset lobby location.");
                        plugin.getUtils().sendMessage(sender, "&8» &7/" + command + " &cadmin create (gameName) &b- &eCreate arena.");
                        plugin.getUtils().sendMessage(sender, "&8» &7/" + command + " &cadmin delete (gameName) &b- &eDelete arena.");
                        plugin.getUtils().sendMessage(sender, "&8» &7/" + command + " &cadmin setWaiting (gameName) &b- &eSet waitingLocation.");
                        plugin.getUtils().sendMessage(sender, "&8» &7/" + command + " &cadmin setSelectedBeast (gameName) &b- &eSet selected Beast Location.");
                        plugin.getUtils().sendMessage(sender, "&8» &7/" + command + " &cadmin setBeastSpawn (gameName) &b- &eSet Beast Spawn Location.");
                        plugin.getUtils().sendMessage(sender, "&8» &7/" + command + " &cadmin setRunnerSpawn (gameName) &b- &eSet Runner Spawn Location.");
                        plugin.getUtils().sendMessage(sender, "&8» &7/" + command + " &cadmin setMin (gameName) (min) &b- &eSet min players.");
                        plugin.getUtils().sendMessage(sender, "&8» &7/" + command + " &cadmin setMax (gameName) (max) &b- &eSet max players.");
                        plugin.getUtils().sendMessage(sender, "&8» &7/" + command + " &cadmin setMode (gameName) (gameType) &b- &eSet gameType. &bOPTIONAL");
                        plugin.getUtils().sendMessage(sender, "&8» &7/" + command + " &cadmin addChest (gameName) (chestName) &b- &eAdd a chest to your game &bOPTIONAL");
                        plugin.getUtils().sendMessage(sender, "&8» &7/" + command + " &cadmin delChest (gameName) (chestName) &b- &eRemove a chest from your game &bOPTIONAL");
                        plugin.getUtils().sendMessage(sender, "&8» &7/" + command + " &cadmin addChestLocation (gameName) (chestName) &b- &eAdd Chest location &bOPTIONAL");
                        plugin.getUtils().sendMessage(sender, "&8» &7/" + command + " &cadmin delChestLocation (gameName) (chestName) &b- &eRemove Chest location &bOPTIONAL");
                        plugin.getUtils().sendMessage(sender, "&8» &7/" + command + " &cadmin chestList (gameName) &b- &eSee all chests of an game.");
                        plugin.getUtils().sendMessage(sender, "&8» &7/" + command + " &cadmin enable (gameName) &b- &eEnable game to play.");
                        plugin.getUtils().sendMessage(sender, "&8» &7/" + command + " &cadmin disable (gameName) &b- &eDisable game to config.");
                        plugin.getUtils().sendMessage(sender, "&8» &7/" + command + " &cadmin modes &b- &eShow all modes.");
                        plugin.getUtils().sendMessage(sender, "&8» &7/" + command + " &cadmin list &b- &eShow all games.");
                        plugin.getUtils().sendMessage(sender, "&8» &7/" + command + " &cadmin reload &b- &eReload command.");
                        return true;
                    }
                    return true;
                }
                if(args[1].equalsIgnoreCase("reload")) {
                    if(hasPermission(sender,"RigoxRFTB.admin.reload")) {
                        plugin.getStorage().reloadFile(SaveMode.ALL);
                        plugin.getUtils().sendMessage(sender, "&8» &aReload completed!");
                        plugin.getGameManager().getGameMenu().reloadMenu();
                        plugin.getRunnable().update();
                        if(plugin.getTitleRunnable() != null) {
                            plugin.getTitleRunnable().update();
                        }
                        return true;
                    }
                    return true;
                }
                if(args[1].equalsIgnoreCase("create")) {
                    if(hasPermission(sender,"RigoxRFTB.admin.create")) {
                        if (args.length == 2) {
                            plugin.getUtils().sendMessage(sender, "&7Bad usage.");
                            return true;
                        }
                        if (plugin.getGameManager().getGame(args[2]) == null) {
                            if (!plugin.getStorage().getControl(RigoxFiles.GAMES).contains("games." + args[2])) {
                                plugin.getGameManager().createGameFiles(args[2]);
                                plugin.getUtils().sendMessage(sender, plugin.getStorage().getControl(RigoxFiles.MESSAGES).getString("messages.admin.create").replace("%arena_id%", args[2]));
                                return true;
                            }
                            plugin.getUtils().sendMessage(sender, "&cThis game already exists in &6games.yml");
                            return true;
                        }
                        plugin.getUtils().sendMessage(sender, "&cThis game already exists");
                        return true;
                    }
                }
                if(args[1].equalsIgnoreCase("enable")) {
                    if(hasPermission(sender,"RigoxRFTB.admin.enable")) {
                        if (args.length == 2) {
                            plugin.getUtils().sendMessage(sender,"&7Bad Usage");
                            return true;
                        }
                        if (plugin.getGameManager().getGame(args[2]) == null) {
                            if (plugin.getStorage().getControl(RigoxFiles.GAMES).contains("games." + args[2])) {
                                plugin.getStorage().getControl(RigoxFiles.GAMES).set("games." + args[2] + ".enabled",true);
                                plugin.getGameManager().addGame(args[2]);
                                plugin.getStorage().save(SaveMode.GAMES_FILES);
                                plugin.getUtils().sendMessage(sender,"&aGame &b" + args[2] + "&a enabled.");
                            }
                        }
                    }
                }
                if(args[1].equalsIgnoreCase("disable")) {
                    if(hasPermission(sender,"RigoxRFTB.admin.enable")) {
                        if (args.length == 2) {
                            plugin.getUtils().sendMessage(sender,"&7Bad Usage");
                            return true;
                        }
                        if (plugin.getGameManager().getGame(args[2]) != null) {
                            if (plugin.getStorage().getControl(RigoxFiles.GAMES).contains("games." + args[2])) {
                                plugin.getStorage().getControl(RigoxFiles.GAMES).set("games." + args[2] + ".enabled",false);
                                plugin.getGameManager().delGame(args[2]);
                                plugin.getStorage().save(SaveMode.GAMES_FILES);
                                plugin.getUtils().sendMessage(sender,"&aGame &b" + args[2] + "&a disabled.");
                            }
                        }
                    }
                }
                if(args[1].equalsIgnoreCase("modes")) {
                    if(hasPermission(sender,"RigoxRFTB.admin.modes")) {
                        plugin.getUtils().sendMessage(sender,"&8» &6CLASSIC &7- &6INFECTED &7- &6DOUBLE_BEAST");
                    }
                }
                if(args[1].equalsIgnoreCase("list")) {
                    if(hasPermission(sender,"RigoxRFTB.admin.list")) {
                        if(plugin.getStorage().getControl(RigoxFiles.GAMES).contains("games")) {
                            boolean status;
                            for (String gameName : plugin.getStorage().getControl(RigoxFiles.GAMES).getConfigurationSection("games").getKeys(false)) {
                                status = plugin.getStorage().getControl(RigoxFiles.GAMES).getBoolean("games." + gameName + ".enabled");
                                plugin.getUtils().sendMessage(sender,"&aGame: &b" + gameName + " &aEnabled: &b" + status);
                            }
                        } else {
                            plugin.getLogs().info("You don't have games created yet.");
                        }
                    }
                }
                if(args[1].equalsIgnoreCase("delete")) {
                    if(hasPermission(sender,"RigoxRFTB.admin.delete")) {
                        if (args.length == 2) {
                            plugin.getUtils().sendMessage(sender, "&7Bad usage.");
                            return true;
                        }
                        if (plugin.getGameManager().getGame(args[2]) != null || plugin.getStorage().getControl(RigoxFiles.GAMES).contains("games." + args[2])) {
                            plugin.getUtils().sendMessage(sender, plugin.getStorage().getControl(RigoxFiles.MESSAGES).getString("messages.admin.delete").replace("%arena_id%", args[2]));
                            plugin.getStorage().getControl(RigoxFiles.GAMES).set("games." + args[2], null);
                            return true;
                        }
                        plugin.getUtils().sendMessage(sender, plugin.getStorage().getControl(RigoxFiles.MESSAGES).getString("messages.admin.arenaError").replace("%arena_id%", args[2]));
                        return true;
                    }
                }
                if(args[1].equalsIgnoreCase("setMax")) {
                    if(hasPermission(sender,"RigoxRFTB.admin.setMax")) {
                        if(args.length == 3) {
                            plugin.getUtils().sendMessage(sender,"&7Bad usage");
                            return true;
                        }
                        if (plugin.getStorage().getControl(RigoxFiles.GAMES).contains("games." + args[2])) {
                            plugin.getGameManager().setMax(args[2], Integer.valueOf(args[3]));
                            plugin.getUtils().sendMessage(sender,"&aMax now is &b" + args[3]);
                            return true;
                        }
                        plugin.getUtils().sendMessage(sender, plugin.getStorage().getControl(RigoxFiles.MESSAGES).getString("messages.admin.arenaError").replace("%arena_id%", args[2]));
                        return true;
                    }
                }
                if(args[1].equalsIgnoreCase("addChest")) {
                    if(hasPermission(sender,"RigoxRFTB.admin.addChest")) {
                        if(args.length == 3) {
                            plugin.getUtils().sendMessage(sender,"&7Bad usage");
                            return true;
                        }
                        if (plugin.getStorage().getControl(RigoxFiles.GAMES).contains("games." + args[2])) {
                            if(plugin.getStorage().getControl(RigoxFiles.GAMES).get("games." + args[2] + ".chests") != null) {
                                if(plugin.getStorage().getControl(RigoxFiles.GAMES).getStringList("games." + args[2] + ".chests").contains(args[3])) {
                                    plugin.getUtils().sendMessage(sender,"&cThis chest already exists in game '&e" + args[2] + "&c'");
                                    return true;
                                }
                                List<String> chests = plugin.getStorage().getControl(RigoxFiles.GAMES).getStringList("games." + args[2] + ".chests");
                                chests.add(args[3]);
                                plugin.getUtils().sendMessage(sender,"&aChest &b" + args[3] + " &aadded to game &b" + args[2] + "&a.");
                                plugin.getStorage().getControl(RigoxFiles.GAMES).set("games." + args[2] + ".chests",chests);
                                plugin.getStorage().save(SaveMode.GAMES_FILES);
                                return true;
                            }
                            List<String> chests = new ArrayList<>();
                            chests.add(args[3]);
                            plugin.getUtils().sendMessage(sender,"&aChest &b" + args[3] + " &aadded to game &b" + args[2] + "&a.");
                            plugin.getStorage().getControl(RigoxFiles.GAMES).set("games." + args[2] + ".chests",chests);
                            plugin.getStorage().save(SaveMode.GAMES_FILES);
                            return true;
                        }
                        plugin.getUtils().sendMessage(sender, plugin.getStorage().getControl(RigoxFiles.MESSAGES).getString("messages.admin.arenaError").replace("%arena_id%", args[2]));
                        return true;
                    }
                }
                if(args[1].equalsIgnoreCase("delChest")) {
                    if(hasPermission(sender,"RigoxRFTB.admin.delChest")) {
                        if(args.length == 3) {
                            plugin.getUtils().sendMessage(sender,"&7Bad usage");
                            return true;
                        }
                        if (plugin.getStorage().getControl(RigoxFiles.GAMES).contains("games." + args[2])) {
                            if(plugin.getStorage().getControl(RigoxFiles.GAMES).get("games." + args[2] + ".chests") != null) {
                                if(!plugin.getStorage().getControl(RigoxFiles.GAMES).getStringList("games." + args[2] + ".chests").contains(args[3])) {
                                    plugin.getUtils().sendMessage(sender,"&cThis chest doesn't exists in game '&e" + args[2] + "&c'");
                                    return true;
                                }
                                List<String> chests = plugin.getStorage().getControl(RigoxFiles.GAMES).getStringList("games." + args[2] + ".chests");
                                chests.remove(args[3]);
                                plugin.getUtils().sendMessage(sender,"&aChest &b" + args[3] + " &aremoved from game &b" + args[2] + "&a.");
                                plugin.getStorage().getControl(RigoxFiles.GAMES).set("games." + args[2] + ".chests",chests);
                                plugin.getStorage().save(SaveMode.GAMES_FILES);
                                return true;
                            }
                            if(!plugin.getStorage().getControl(RigoxFiles.GAMES).getStringList("games." + args[2] + ".chests").contains(args[3])) {
                                plugin.getUtils().sendMessage(sender,"&cThis chest doesn't exists in game '&e" + args[2] + "&c'");
                                return true;
                            }
                        }
                        plugin.getUtils().sendMessage(sender, plugin.getStorage().getControl(RigoxFiles.MESSAGES).getString("messages.admin.arenaError").replace("%arena_id%", args[2]));
                        return true;
                    }
                }
                if(args[1].equalsIgnoreCase("setMode")) {
                    if(hasPermission(sender,"RigoxRFTB.admin.setMode")) {
                        if(args.length == 3) {
                            plugin.getUtils().sendMessage(sender,"&7Bad usage");
                            return true;
                        }
                        if (plugin.getStorage().getControl(RigoxFiles.GAMES).contains("games." + args[2])) {
                            GameType gameType = GameType.CLASSIC;
                            if(args[3].equalsIgnoreCase("INFECTED")) gameType = GameType.INFECTED;
                            if(args[3].equalsIgnoreCase("DOUBLE_BEAST")) gameType = GameType.DOUBLE_BEAST;
                            plugin.getGameManager().setMode(args[2], gameType);
                            plugin.getUtils().sendMessage(sender,"&aMode now is &b" + gameType.toString().toUpperCase());
                            return true;
                        }
                        plugin.getUtils().sendMessage(sender, plugin.getStorage().getControl(RigoxFiles.MESSAGES).getString("messages.admin.arenaError").replace("%arena_id%", args[2]));
                        return true;
                    }
                }
                if(args[1].equalsIgnoreCase("setMin")) {
                    if(hasPermission(sender,"RigoxRFTB.admin.setMax")) {
                        if(args.length == 3) {
                            plugin.getUtils().sendMessage(sender,"&7Bad usage");
                            return true;
                        }
                        if (plugin.getStorage().getControl(RigoxFiles.GAMES).contains("games." + args[2])) {
                            plugin.getGameManager().setMin(args[2], Integer.valueOf(args[3]));
                            plugin.getUtils().sendMessage(sender,"&aMin now is &b" + args[3]);
                            return true;
                        }
                        plugin.getUtils().sendMessage(sender, plugin.getStorage().getControl(RigoxFiles.MESSAGES).getString("messages.admin.arenaError").replace("%arena_id%", args[2]));
                        return true;
                    }
                }
                if(sender instanceof Player) {
                    Player player = (Player)sender;
                    if(args[1].equalsIgnoreCase("setlobby")) {
                        if(hasPermission(sender,"RigoxRFTB.admin.setlobby")) {
                            String location = plugin.getUtils().getStringFromLocation(player.getLocation());
                            plugin.getStorage().getControl(RigoxFiles.SETTINGS).set("settings.lobbyLocation",player.getLocation());
                            plugin.getStorage().save(SaveMode.SETTINGS);
                            plugin.getUtils().sendMessage(sender,"&aLocation now is &b" + location + ".");
                        }
                    }
                    if (args[1].equalsIgnoreCase("setWaiting")) {
                        if (hasPermission(sender, "RigoxRFTB.admin.locations")) {
                            if (plugin.getStorage().getControl(RigoxFiles.GAMES).contains("games." + args[2])) {
                                plugin.getGameManager().setWaiting(args[2], player.getLocation());
                                plugin.getUtils().sendMessage(sender, plugin.getStorage().getControl(RigoxFiles.MESSAGES).getString("messages.admin.setWaiting").replace("%arena_id%", args[2]).replace("%spawnType%","Runner").replace("%location%","X: " + player.getLocation().getX() + " Y: " + player.getLocation().getY() + " Z: " + player.getLocation().getZ()));
                                return true;
                            }
                            plugin.getUtils().sendMessage(sender, plugin.getStorage().getControl(RigoxFiles.MESSAGES).getString("messages.admin.arenaError").replace("%arena_id%", args[2]));
                            return true;
                        }
                    }
                    if(args[1].equalsIgnoreCase("addChestLocation")) {
                        if(hasPermission(sender,"RigoxRFTB.admin.addChestLocation")) {
                            if(args.length == 3) {
                                plugin.getUtils().sendMessage(sender,"&7Bad usage");
                                return true;
                            }
                            if (plugin.getStorage().getControl(RigoxFiles.GAMES).contains("games." + args[2])) {
                                String path = "games." + args[2] + ".chests-location." + args[3];
                                String toAdd = plugin.getUtils().getStringFromLocation(player.getTargetBlock(null,5).getLocation());
                                if(falseChest(player.getTargetBlock(null,5).getType())) {
                                    plugin.getUtils().sendMessage(sender,"&cThis block is not a chest.");
                                    return true;
                                }
                                if(plugin.getStorage().getControl(RigoxFiles.GAMES).get("games." + args[2] + ".chests") == null) {
                                    plugin.getUtils().sendMessage(sender,"&cThe chest &f" + args[3] + " &cis not added in chest list of game &f" + args[2]);
                                    return true;
                                }
                                if(!plugin.getStorage().getControl(RigoxFiles.GAMES).getStringList("games." + args[2] + ".chests").contains(args[3])) {
                                    plugin.getUtils().sendMessage(sender,"&cThe chest &f" + args[3] + " &cis not added in chest list of game &f" + args[2]);
                                    return true;
                                }
                                if(plugin.getStorage().getControl(RigoxFiles.GAMES).get(path) != null) {
                                    if(plugin.getStorage().getControl(RigoxFiles.GAMES).getStringList(path).contains(args[3])) {
                                        plugin.getUtils().sendMessage(sender,"&cThis chest location already exists in game '&e" + args[2] + "&c'");
                                        return true;
                                    }
                                    List<String> chests = plugin.getStorage().getControl(RigoxFiles.GAMES).getStringList(path);
                                    chests.add(toAdd);
                                    plugin.getUtils().sendMessage(sender,"&aChest Location added to chest &b" + args[3] + " &ain game&b " + args[2] + "&a.");
                                    plugin.getStorage().getControl(RigoxFiles.GAMES).set(path,chests);
                                    plugin.getStorage().save(SaveMode.GAMES_FILES);
                                    return true;
                                }
                                List<String> chests = new ArrayList<>();
                                chests.add(toAdd);
                                plugin.getUtils().sendMessage(sender,"&aChest Location added to chest &b" + args[3] + " &ain game&b " + args[2] + "&a.");
                                plugin.getStorage().getControl(RigoxFiles.GAMES).set(path,chests);
                                plugin.getStorage().save(SaveMode.GAMES_FILES);
                                return true;
                            }
                            plugin.getUtils().sendMessage(sender, plugin.getStorage().getControl(RigoxFiles.MESSAGES).getString("messages.admin.arenaError").replace("%arena_id%", args[2]));
                            return true;
                        }
                    }
                    if(args[1].equalsIgnoreCase("delChestLocation")) {
                        if(hasPermission(sender,"RigoxRFTB.admin.delChestLocation")) {
                            if(args.length == 3) {
                                plugin.getUtils().sendMessage(sender,"&7Bad usage");
                                return true;
                            }
                            if (plugin.getStorage().getControl(RigoxFiles.GAMES).contains("games." + args[2])) {
                                if(falseChest(player.getTargetBlock(null,5).getType())) {
                                    plugin.getUtils().sendMessage(sender,"&cThis block is not a chest.");
                                    return true;
                                }
                                String path = "games." + args[2] + ".chests-location." + args[3];
                                String toRemove = plugin.getUtils().getStringFromLocation(player.getTargetBlock(null,5).getLocation());
                                if(plugin.getStorage().getControl(RigoxFiles.GAMES).get("games." + args[2] + ".chests") == null) {
                                    plugin.getUtils().sendMessage(sender,"&cThe chest &f" + args[3] + " &cis not added in chest list of game &f" + args[2]);
                                    return true;
                                }
                                if(!plugin.getStorage().getControl(RigoxFiles.GAMES).getStringList("games." + args[2] + ".chests").contains(args[3])) {
                                    plugin.getUtils().sendMessage(sender,"&cThe chest &f" + args[3] + " &cis not added in chest list of game &f" + args[2]);
                                    return true;
                                }
                                if(plugin.getStorage().getControl(RigoxFiles.GAMES).get(path) != null) {
                                    if(!plugin.getStorage().getControl(RigoxFiles.GAMES).getStringList(path).contains(args[3])) {
                                        plugin.getUtils().sendMessage(sender,"&cThis chest location already doesn't exists in game '&e" + args[2] + "&c'");
                                        return true;
                                    }
                                    List<String> chests = plugin.getStorage().getControl(RigoxFiles.GAMES).getStringList(path);
                                    chests.remove(toRemove);
                                    plugin.getUtils().sendMessage(sender,"&aChest Location removed from chest &b" + args[3] + " &ain game&b " + args[2] + "&a.");
                                    plugin.getStorage().getControl(RigoxFiles.GAMES).set(path,chests);
                                    plugin.getStorage().save(SaveMode.GAMES_FILES);
                                    return true;
                                }
                                plugin.getUtils().sendMessage(sender,"&cThis chest location already doesn't exists in game '&e" + args[2] + "&c'");
                                return true;
                            }
                            plugin.getUtils().sendMessage(sender, plugin.getStorage().getControl(RigoxFiles.MESSAGES).getString("messages.admin.arenaError").replace("%arena_id%", args[2]));
                            return true;
                        }
                    }
                    if (args[1].equalsIgnoreCase("setSelectedBeast")) {
                        if (hasPermission(sender, "RigoxRFTB.admin.locations")) {
                            if (plugin.getStorage().getControl(RigoxFiles.GAMES).contains("games." + args[2])) {
                                plugin.getGameManager().setSelectedBeast(args[2], player.getLocation());
                                plugin.getUtils().sendMessage(sender, plugin.getStorage().getControl(RigoxFiles.MESSAGES).getString("messages.admin.setSpawn").replace("%arena_id%", args[2]).replace("%spawnType%","SelectedBeast").replace("%location%","X: " + player.getLocation().getX() + " Y: " + player.getLocation().getY() + " Z: " + player.getLocation().getZ()));
                                return true;
                            }
                            plugin.getUtils().sendMessage(sender, plugin.getStorage().getControl(RigoxFiles.MESSAGES).getString("messages.admin.arenaError").replace("%arena_id%", args[2]));
                            return true;
                        }
                    }
                    if (args[1].equalsIgnoreCase("setBeastSpawn")) {
                        if (hasPermission(sender, "RigoxRFTB.admin.locations")) {
                            if (plugin.getStorage().getControl(RigoxFiles.GAMES).contains("games." + args[2])) {
                                plugin.getGameManager().setBeast(args[2], player.getLocation());
                                plugin.getUtils().sendMessage(sender, plugin.getStorage().getControl(RigoxFiles.MESSAGES).getString("messages.admin.setSpawn").replace("%arena_id%", args[2]).replace("%spawnType%","Beast").replace("%location%","X: " + player.getLocation().getX() + " Y: " + player.getLocation().getY() + " Z: " + player.getLocation().getZ()));
                                return true;
                            }
                            plugin.getUtils().sendMessage(sender, plugin.getStorage().getControl(RigoxFiles.MESSAGES).getString("messages.admin.arenaError").replace("%arena_id%", args[2]));
                            return true;
                        }
                    }
                    if (args[1].equalsIgnoreCase("setRunnerSpawn")) {
                        if (hasPermission(sender, "RigoxRFTB.admin.locations")) {
                            if (plugin.getStorage().getControl(RigoxFiles.GAMES).contains("games." + args[2])) {
                                plugin.getGameManager().setRunners(args[2], player.getLocation());
                                plugin.getUtils().sendMessage(sender, plugin.getStorage().getControl(RigoxFiles.MESSAGES).getString("messages.admin.setSpawn").replace("%arena_id%", args[2]).replace("%spawnType%","Runner").replace("%location%","X: " + player.getLocation().getX() + " Y: " + player.getLocation().getY() + " Z: " + player.getLocation().getZ()));
                                return true;
                            }
                            plugin.getUtils().sendMessage(sender, plugin.getStorage().getControl(RigoxFiles.MESSAGES).getString("messages.admin.arenaError").replace("%arena_id%", args[2]));
                            return true;
                        }
                    }
                } else {
                    plugin.getUtils().sendMessage(sender,"&cThis command only can be executed by a Player.");
                    return true;
                }
            }

        }catch (Throwable throwable) {
            plugin.getLogs().error("Can't execute main command :(");
            plugin.getLogs().error(throwable);
        }
        return true;
    }
    private boolean falseChest(Material evalMaterial) {
        if(evalMaterial.equals(Material.CHEST)) return false;
        if(evalMaterial.equals(Material.TRAPPED_CHEST)) return false;
        return (!evalMaterial.equals(Material.ENDER_CHEST));
    }
}
