package dev.mruniverse.rigoxrftb.core.listeners;

import dev.mruniverse.rigoxrftb.core.enums.ItemFunction;
import dev.mruniverse.rigoxrftb.core.enums.RigoxFiles;
import dev.mruniverse.rigoxrftb.core.enums.RigoxBoard;
import dev.mruniverse.rigoxrftb.core.RigoxRFTB;
import dev.mruniverse.rigoxrftb.core.enums.SaveMode;
import dev.mruniverse.rigoxrftb.core.games.Game;
import dev.mruniverse.rigoxrftb.core.games.GameStatus;
import dev.mruniverse.rigoxrftb.core.games.GameType;
import dev.mruniverse.rigoxrftb.core.utils.players.PlayerManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class MainListener implements Listener {
    private final RigoxRFTB plugin;
    public MainListener(RigoxRFTB main) {
        plugin = main;
        main.getLogs().info("PlayerListener registered!");
    }
    @EventHandler
    public void joinOptions(PlayerJoinEvent event) {
        plugin.addPlayer(event.getPlayer());
        FileConfiguration file = plugin.getFiles().getControl(RigoxFiles.SETTINGS);
        Player player = event.getPlayer();
        if (!plugin.getFiles().getControl(RigoxFiles.MYSQL).getBoolean("mysql.enabled") && !plugin.getData().getSQL().deaths.containsKey(player.getUniqueId().toString())) {
            plugin.getData().getSQL().createPlayer(player);
        }
        if(file.getBoolean("settings.options.hideServerJoinMessage")) {
            event.setJoinMessage(null);
        }
        if(file.getBoolean("settings.options.joinHeal")) {
            player.setHealth(20.0D);
            player.setFoodLevel(20);
            player.setLevel(0);
            player.setExp(0.0F);
        }
        if(file.getBoolean("settings.options.joinAdventureGamemode")) {
            player.setGameMode(GameMode.ADVENTURE);
        }
        if(file.getBoolean("settings.options.clearInventory-onJoin")) {
            player.getInventory().clear();
        }
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
        for(ItemStack item : plugin.getLobbyItems().keySet()) {
            player.getInventory().setItem(plugin.getSlot(item),item);
        }
    }
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getItem() != null) {
            if(event.getItem() == null) return;
            if(event.getItem().getItemMeta() == null) return;
            if(event.getItem().getType().equals(plugin.exitItem.getType()) && event.getItem().getItemMeta().equals(plugin.exitItem.getItemMeta())) {
                event.setCancelled(true);
                plugin.getPlayerData(event.getPlayer().getUniqueId()).getGame().leave(player);
                return;
            }

            for(ItemStack item : plugin.getLobbyItems().keySet()) {
                if(event.getItem().getType().equals(item.getType()) && event.getItem().getItemMeta().equals(item.getItemMeta())) {
                    ItemFunction itemAction = plugin.getCurrent(item);
                    event.setCancelled(true);
                    switch(itemAction) {
                        case SHOP:
                            plugin.getUtils().sendMessage(player,"&cShop currently is in development");
                            return;
                        case KIT_BEASTS:
                            plugin.getUtils().sendMessage(player,"&cBeast Kits currently in development");
                            return;
                        case KIT_RUNNERS:
                            plugin.getUtils().sendMessage(player,"&cRunners Kits currently in development");
                            return;
                        case EXIT_LOBBY:
                            plugin.getUtils().sendMessage(player,"&aSending you to Lobby..");
                            return;
                        case GAME_SELECTOR:
                            player.openInventory(plugin.getGameManager().gameMenu.getInventory());
                            return;
                        case LOBBY_SELECTOR:
                        case PLAYER_SETTINGS:
                            plugin.getUtils().sendMessage(player,"&cCurrently in development");
                            return;
                        default:
                            if(plugin.getPlayerData(event.getPlayer().getUniqueId()).getGame() != null) {
                                plugin.getPlayerData(event.getPlayer().getUniqueId()).getGame().leave(player);
                                return;
                            }
                    }
                }
            }
        }
    }
    @EventHandler
    public void onGameMenuClick(InventoryClickEvent event) {
        Player player = (Player)event.getWhoClicked();
        if(plugin.getPlayerData(player.getUniqueId()).getGame() != null) return;
        if(!event.getInventory().equals(plugin.getGameManager().gameMenu.getInventory())) return;
        HashMap<ItemStack,String> hash = plugin.getGameManager().gameMenu.getGameItems();
        ItemStack clickedItem = event.getCurrentItem();
        if(hash.containsKey(event.getCurrentItem())) {
            plugin.getGameManager().joinGame(player,hash.get(clickedItem));
        }
    }
    @EventHandler
    public void onChestClick(PlayerInteractEvent e) {
        Game game = plugin.getPlayerData(e.getPlayer().getUniqueId()).getGame();
        if(game == null) return;
        if(game.gameStatus.equals(GameStatus.WAITING) || game.gameStatus.equals(GameStatus.STARTING) || game.gameStatus.equals(GameStatus.RESTARTING)) {
            e.setCancelled(true);
        }
        Block b = e.getClickedBlock();
        if (b == null) { return; }
        if(falseChest(b.getType())) { return; }
        e.setCancelled(true);
        Chest chest = (Chest)e.getClickedBlock().getState();
        InventoryHolder holder = chest.getInventory().getHolder();
        if (holder instanceof DoubleChest) {
            DoubleChest doubleChest = ((DoubleChest) holder);
            Chest leftChest = (Chest) doubleChest.getLeftSide();
            if (leftChest != null) {
                if (isGameChest(e.getPlayer(), leftChest.getLocation())) return;
            }
            Chest rightChest = (Chest) doubleChest.getRightSide();
            if (rightChest != null) { if (isGameChest(e.getPlayer(), rightChest.getLocation())) return; }
            return;
        }
        checkGameChest(e.getPlayer(),b.getLocation());
    }
    private void openGameChest(Player player,String chestName) {
        player.openInventory(plugin.getGameManager().getGameChest(chestName).getInventory());
    }
    private void checkGameChest(Player player,Location location) {
        Game game = plugin.getPlayerData(player.getUniqueId()).getGame();
        if(game == null) return;
        for(String chests : game.getGameChestsTypes()) {
            if(game.getGameChests().get(chests).contains(location)) {
                openGameChest(player,chests);
                return;
            }
        }
    }
    private boolean isGameChest(Player player,Location location) {
        Game game = plugin.getPlayerData(player.getUniqueId()).getGame();
        if(game == null) return false;
        for(String chests : game.getGameChestsTypes()) {
            if(game.getGameChests().get(chests).contains(location)) {
                openGameChest(player,chests);
                return true;
            }
        }
        return false;
    }
    private boolean falseChest(Material evalMaterial) {
        if(evalMaterial.equals(Material.CHEST)) return false;
        if(evalMaterial.equals(Material.TRAPPED_CHEST)) return false;
        return (!evalMaterial.equals(Material.ENDER_CHEST));
    }
    @EventHandler
    public void joinScoreboard(PlayerJoinEvent event) {
        try {
            FileConfiguration file = plugin.getFiles().getControl(RigoxFiles.SETTINGS);
            if (file.getBoolean("settings.lobbyScoreboard-only-in-lobby-world")) {
                String lC = file.getString("settings.lobbyLocation");
                if(lC == null ) lC = "notSet";
                if (lC.equalsIgnoreCase("notSet")) {
                    plugin.getLogs().error("-----------------------------");
                    plugin.getLogs().error("Can't show lobby-scoreboard, lobby location is not set");
                    plugin.getLogs().error("-----------------------------");
                } else {
                    String[] loc = lC.split(",");
                    World w = Bukkit.getWorld(loc[0]);
                    if (event.getPlayer().getWorld().equals(w)) {
                        if(plugin.getFiles().getControl(RigoxFiles.SCOREBOARD).getBoolean("scoreboards.lobby.toggle")) {
                            plugin.getScoreboards().setScoreboard(RigoxBoard.LOBBY,event.getPlayer());
                        }
                    }
                }
            } else {
                if(plugin.getFiles().getControl(RigoxFiles.SCOREBOARD).getBoolean("scoreboards.lobby.toggle")) {
                    plugin.getScoreboards().setScoreboard(RigoxBoard.LOBBY,event.getPlayer());
                }
            }
        } catch (Throwable throwable) {
            plugin.getLogs().error("Can't generate lobby scoreboard for " + event.getPlayer().getName() +"!");
            plugin.getLogs().error(throwable);
        }
    }
    @EventHandler
    public void pluginChat(AsyncPlayerChatEvent event) {
        if(event.isCancelled()) return;
        if(!plugin.getFiles().getControl(RigoxFiles.SETTINGS).getBoolean("settings.options.pluginChat")) return;
        Player player = event.getPlayer();
        PlayerManager playerManager = plugin.getPlayerData(player.getUniqueId());
        if(playerManager == null || playerManager.getGame() == null) {
            String lC = plugin.getFiles().getControl(RigoxFiles.SETTINGS).getString("settings.lobbyLocation");
            String lobbyChat = plugin.getFiles().getControl(RigoxFiles.MESSAGES).getString("messages.others.customChat.lobby");
            if(lobbyChat == null) lobbyChat = "&7<player_name>&8: &f%message%";
            if(lC == null ) lC = "notSet";
            if (lC.equalsIgnoreCase("notSet")) {
                plugin.getLogs().error("-----------------------------");
                plugin.getLogs().error("Can't show lobby-scoreboard, lobby location is not set");
                plugin.getLogs().error("-----------------------------");
            } else {
                event.setCancelled(true);
                plugin.getLogs().debug("CHAT | " + player.getName() + ": " + event.getMessage());
                String[] loc = lC.split(",");
                World w = Bukkit.getWorld(loc[0]);
                if(w == null) return;
                for(Player lPlayer : w.getPlayers()) {
                    plugin.getUtils().sendMessage(lPlayer,lobbyChat.replace("<player_name>",player.getName())
                    .replace("%message%",event.getMessage()));
                }
            }
            return;
        }
        event.setCancelled(true);
        plugin.getLogs().debug("CHAT | " + player.getName() + ": " + event.getMessage());
        Game game = playerManager.getGame();
        if(game.getSpectators().contains(player)) {
            String spectatorChat = plugin.getFiles().getControl(RigoxFiles.MESSAGES).getString("messages.others.customChat.spectator");
            if(spectatorChat == null) spectatorChat = "&8[SPECTATOR] &7<player_name>&8: &f%message%";
            for(Player spectator : game.getSpectators()) {
                plugin.getUtils().sendMessage(spectator,spectatorChat.replace("<player_name>",player.getName())
                        .replace("%message%",event.getMessage()));
            }
            return;
        }
        String inGameChat = plugin.getFiles().getControl(RigoxFiles.MESSAGES).getString("messages.others.customChat.inGame");
        String playerRole;
        if(inGameChat == null) inGameChat = "&a[%player_role%&a] &7<player_name>&8: &f%message%";
        if(game.getBeasts().contains(player)) {
            playerRole = plugin.getFiles().getControl(RigoxFiles.SETTINGS).getString("roles.beast");
        } else {
            playerRole = plugin.getFiles().getControl(RigoxFiles.SETTINGS).getString("roles.runner");
        }
        if(playerRole == null) playerRole = "Runner";
        for(Player spectator : game.getPlayers()) {
            plugin.getUtils().sendMessage(spectator,inGameChat.replace("<player_name>",player.getName())
                    .replace("%message%",event.getMessage()).replace("%player_role%",playerRole));
        }
    }
    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if(plugin.getPlayerData(event.getPlayer().getUniqueId()).getGame() != null) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if(plugin.getPlayerData(event.getPlayer().getUniqueId()).getGame() != null) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        plugin.getScoreboards().removeScore(event.getPlayer());
        if(plugin.getPlayerData(event.getPlayer().getUniqueId()).getGame() != null) {
            plugin.getPlayerData(event.getPlayer().getUniqueId()).getGame().leave(event.getPlayer());
        }
        plugin.removePlayer(event.getPlayer());
        plugin.getNMSHandler().deleteBossBar(event.getPlayer());
        if(plugin.getFiles().getControl(RigoxFiles.SETTINGS).getBoolean("settings.options.hideServerQuitMessage")) {
            event.setQuitMessage(null);
        }
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void inGameDeath(PlayerDeathEvent event) {
        final Player player = event.getEntity();
        if(plugin.getPlayerData(player.getUniqueId()).getGame() != null) {
            Game game = plugin.getPlayerData(player.getUniqueId()).getGame();
            event.getDrops().clear();
            event.setDeathMessage(null);
            event.setDroppedExp(0);
            if(game.getBeasts().contains(player)) {
                player.spigot().respawn();
                player.setGameMode(GameMode.SPECTATOR);
                game.deathBeast(player);
                player.teleport(game.beastLocation);
                player.setGameMode(GameMode.SPECTATOR);
            } else {
                player.spigot().respawn();
                game.deathRunner(player);
                if(!game.getGameType().equals(GameType.INFECTED)) {
                    player.teleport(game.runnersLocation);
                    player.setGameMode(GameMode.SPECTATOR);
                }
            }
            player.setGameMode(GameMode.SPECTATOR);
        }
    }
    @EventHandler
    public void inGameDamage(EntityDamageEvent event) {
        if(!event.getEntity().getType().equals(EntityType.PLAYER)) return;
        Player player = (Player)event.getEntity();
        if(plugin.getPlayerData(player.getUniqueId()) == null) return;
        if(plugin.getPlayerData(player.getUniqueId()).getGame() == null) return;
        if((player.getHealth() - event.getFinalDamage()) <= 0) {
            event.setCancelled(true);
            Game game = plugin.getPlayerData(player.getUniqueId()).getGame();
            player.getInventory().clear();
            player.setHealth(20);
            player.setFoodLevel(20);
            player.getInventory().setBoots(null);
            player.getInventory().setHelmet(null);
            player.getInventory().setChestplate(null);
            player.getInventory().setLeggings(null);

            if(game.getBeasts().contains(player)) {
                player.setGameMode(GameMode.SPECTATOR);
                game.deathBeast(player);
                player.teleport(game.beastLocation);
            } else {
                game.deathRunner(player);
                if(!game.getGameType().equals(GameType.INFECTED)) {
                    player.setGameMode(GameMode.SPECTATOR);
                    player.teleport(game.runnersLocation);
                }
            }


        }
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void onDeathRespawn(PlayerRespawnEvent event) {
        final Player player = event.getPlayer();
        Game game = plugin.getPlayerData(player.getUniqueId()).getGame();
        if(game != null) {
            if(game.getBeasts().contains(player)) {
                player.teleport(game.beastLocation);
            } else {
                player.teleport(game.runnersLocation);
            }
            player.setGameMode(GameMode.SPECTATOR);
        }
    }
    @EventHandler
    public void lobbyDamage(EntityDamageEvent event) {
        if(event.getEntity().getType().equals(EntityType.PLAYER)) {
            String lC = plugin.getFiles().getControl(RigoxFiles.SETTINGS).getString("settings.lobbyLocation");
            if(lC == null) lC = "notSet";
            if (!lC.equalsIgnoreCase("notSet")) {
                String[] loc = lC.split(",");
                World w = Bukkit.getWorld(loc[0]);
                if (event.getEntity().getWorld().equals(w)) {
                    event.setCancelled(true);
                    if(plugin.getFiles().getControl(RigoxFiles.SETTINGS).getBoolean("settings.options.lobby-voidSpawnTP")) {
                        if (event.getCause().equals(EntityDamageEvent.DamageCause.VOID)) {
                            Location location = plugin.getUtils().getLocationFromString(lC);
                            event.getEntity().teleport(location);
                        }
                    }
                }
            }
            Player player = (Player)event.getEntity();
            if(plugin.getPlayerData(player.getUniqueId()).getGame() != null) {
                RigoxBoard board = plugin.getPlayerData(player.getUniqueId()).getBoard();
                if(board.equals(RigoxBoard.WAITING) || board.equals(RigoxBoard.STARTING) || board.equals(RigoxBoard.WIN_BEAST_FOR_BEAST) || board.equals(RigoxBoard.WIN_BEAST_FOR_RUNNERS) || board.equals(RigoxBoard.WIN_RUNNERS_FOR_BEAST) || board.equals(RigoxBoard.WIN_RUNNERS_FOR_RUNNERS)) {
                    event.setCancelled(true);
                } else {
                    if(event.getCause().equals(EntityDamageEvent.DamageCause.FALL) || plugin.getPlayerData(player.getUniqueId()).getGame().getSpectators().contains(player)) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
    @EventHandler
    public void inGameDamage(EntityDamageByEntityEvent event) {
        if(event.getEntity().getType().equals(EntityType.PLAYER)) {
            if(event.getDamager().getType().equals(EntityType.PLAYER)) {
                Player victim = (Player)event.getEntity();
                Player attacker = (Player)event.getDamager();
                if(plugin.getPlayerData(victim.getUniqueId()).getGame() != null) {
                    if(plugin.getPlayerData(attacker.getUniqueId()).getGame() == null) {
                        event.setCancelled(true);
                    } else {
                        Game game = plugin.getPlayerData(victim.getUniqueId()).getGame();
                        if(game.getRunners().contains(victim) && game.getRunners().contains(attacker)) {
                            event.setCancelled(true);
                        }
                    }
                }

            }
        }
    }
    @EventHandler
    public void lobbyHunger(FoodLevelChangeEvent event) {
        if(event.getEntity().getType().equals(EntityType.PLAYER)) {
            String lC = plugin.getFiles().getControl(RigoxFiles.SETTINGS).getString("settings.lobbyLocation");
            if(lC == null) lC = "notSet";
            if (!lC.equalsIgnoreCase("notSet")) {
                String[] loc = lC.split(",");
                World w = Bukkit.getWorld(loc[0]);
                if (event.getEntity().getWorld().equals(w)) {
                    event.setFoodLevel(20);
                }
            }
            Player player = (Player)event.getEntity();
            if(plugin.getPlayerData(player.getUniqueId()).getGame() != null) {
                event.setFoodLevel(20);
            }
        }
    }
    @EventHandler
    public void onSignCreate(SignChangeEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("RigoxRFTB.admin.signCreate"))
            return;
        try {
            String line1 = event.getLine(0);
            if(line1 == null) return;
            if (line1.equalsIgnoreCase("[RFTB]")) {
                String name = event.getLine(1);
                if(name == null) name = "null";
                final Game game = plugin.getGameManager().getGame(name);
                if (game == null) {
                    String errorMsg = plugin.getFiles().getControl(RigoxFiles.MESSAGES).getString("messages.admin.arenaError");
                    if(errorMsg == null) errorMsg = "&c%arena_id% don't exists";
                    errorMsg = errorMsg.replace("%arena_id%", name);
                    plugin.getUtils().sendMessage(player,errorMsg);
                    return;
                }
                List<String> signs = plugin.getFiles().getControl(RigoxFiles.GAMES).getStringList("games." + name + ".signs");
                signs.add(plugin.getUtils().getStringFromLocation(event.getBlock().getLocation()));
                plugin.getFiles().getControl(RigoxFiles.GAMES).set("games." + name + ".signs",signs);
                plugin.getFiles().save(SaveMode.GAMES_FILES);
                game.loadSigns();
            }
        }catch (Throwable throwable) {
            plugin.getLogs().error("Can't create plugin sign");
            plugin.getLogs().error(throwable);
        }
    }
    @EventHandler
    public void SignInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        Player player = e.getPlayer();
        try {
            Block block = e.getClickedBlock();
            if(block == null) return;
            if (block.getState() instanceof Sign) {
                for (Game game : plugin.getGameManager().getGames()) {
                    if (game.getSigns().contains(e.getClickedBlock().getLocation())) {
                        game.join(player);
                        return;
                    }
                }
            }
        }catch (Throwable ignored) {}
    }
    @EventHandler
    public void joinTeleport(PlayerJoinEvent event) {
        try {
            FileConfiguration file = plugin.getFiles().getControl(RigoxFiles.SETTINGS);
            if (file.getBoolean("settings.options.joinLobbyTeleport")) {
                String lC = file.getString("settings.lobbyLocation");
                if(lC == null) lC = "notSet";
                if (lC.equalsIgnoreCase("notSet")) {
                    plugin.getLogs().error("-----------------------------");
                    plugin.getLogs().error("Can't teleport player to lobby location, lobby location is not set");
                    plugin.getLogs().error("-----------------------------");
                } else {
                    String[] loc = lC.split(",");
                    World w = Bukkit.getWorld(loc[0]);
                    double x = Double.parseDouble(loc[1]);
                    double y = Double.parseDouble(loc[2]);
                    double z = Double.parseDouble(loc[3]);
                    float yaw = Float.parseFloat(loc[4]);
                    float pitch = Float.parseFloat(loc[5]);
                    Location location = new Location(w, x, y, z, yaw, pitch);
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,() -> {
                        try {
                            event.getPlayer().teleport(location);
                        } catch (Exception ex) {
                            plugin.getLogs().error("Can't teleport player to lobby on join");
                        }
                    });
                    if(plugin.getFiles().getControl(RigoxFiles.SCOREBOARD).getBoolean("scoreboards.lobby.toggle")) {
                        plugin.getScoreboards().setScoreboard(RigoxBoard.LOBBY,event.getPlayer());
                    }
                    if(file.getBoolean("settings.options.joinHeal")) {
                        event.getPlayer().setHealth(20.0D);
                        event.getPlayer().setLevel(0);
                        event.getPlayer().setFoodLevel(20);
                        event.getPlayer().setExp(0.0F);
                    }
                    if(file.getBoolean("settings.options.lobby-actionBar")) {
                        plugin.getUtils().sendActionbar(event.getPlayer(),plugin.getFiles().getControl(RigoxFiles.MESSAGES).getString("messages.lobby.actionBar"));
                    }
                    if(file.getBoolean("settings.options.joinAdventureGamemode")) {
                        event.getPlayer().setGameMode(GameMode.ADVENTURE);
                    }
                }
            }
        } catch (Throwable throwable) {
            plugin.getLogs().error("Can't teleport " + event.getPlayer().getName() +" to the lobby!");
            plugin.getLogs().error(throwable);
        }
    }
}