package dev.mruniverse.guardianrftb.core.listeners;

import dev.mruniverse.guardianrftb.core.GuardianRFTB;
import dev.mruniverse.guardianrftb.core.enums.GuardianFiles;
import dev.mruniverse.guardianrftb.core.enums.ItemFunction;
import dev.mruniverse.guardianrftb.core.enums.GuardianBoard;
import dev.mruniverse.guardianrftb.core.games.Game;
import dev.mruniverse.guardianrftb.core.games.GameStatus;
import dev.mruniverse.guardianrftb.core.games.GameType;
import dev.mruniverse.guardianrftb.core.kits.KitType;
import dev.mruniverse.guardianrftb.core.utils.ShopAction;
import dev.mruniverse.guardianrftb.core.utils.players.PlayerManager;
import dev.mruniverse.guardianrftb.core.enums.SaveMode;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class MainListener implements Listener {
    private final GuardianRFTB plugin;
    public MainListener(GuardianRFTB main) {
        plugin = main;
        main.getLogs().info("PlayerListener registered!");
    }
    @EventHandler
    public void onGameWeather(WeatherChangeEvent event) {
        if(plugin.getGameManager().getGameWorlds().containsKey(event.getWorld())) {
            String gameName = plugin.getGameManager().getGameWorlds().get(event.getWorld()).getName();
            boolean disableRain = true;
            if(plugin.getStorage().getControl(GuardianFiles.GAMES).get("games." + gameName + ".disableRain") != null) disableRain = plugin.getStorage().getControl(GuardianFiles.GAMES).getBoolean("games." + gameName + ".disableRain");
            if(disableRain) {
                event.setCancelled(event.toWeatherState());
            }
        }
    }
    @EventHandler
    public void lobbyWeather(WeatherChangeEvent event) {
        String lC = plugin.getStorage().getControl(GuardianFiles.SETTINGS).getString("settings.lobbyLocation");
        if(lC == null) lC = "notSet";
        if (!lC.equalsIgnoreCase("notSet")) {
            String[] loc = lC.split(",");
            World w = Bukkit.getWorld(loc[0]);
            if(w != event.getWorld()) return;
            boolean disableRain = true;
            if(plugin.getStorage().getControl(GuardianFiles.SETTINGS).get("settings.options.lobby-disableWeather") != null) disableRain = plugin.getStorage().getControl(GuardianFiles.SETTINGS).getBoolean("settings.options.lobby-disableWeather");
            if(disableRain) {
                event.setCancelled(event.toWeatherState());
            }
        }
    }
    @EventHandler
    public void joinOptions(PlayerJoinEvent event) {
        plugin.addPlayer(event.getPlayer());
        FileConfiguration file = plugin.getStorage().getControl(GuardianFiles.SETTINGS);
        Player player = event.getPlayer();
        if (!plugin.getStorage().getControl(GuardianFiles.MYSQL).getBoolean("mysql.enabled") && !plugin.getData().getSQL().deaths.containsKey(player.getUniqueId().toString())) {
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
            PlayerManager pm = plugin.getPlayerData(player.getUniqueId());
            HashMap<ItemStack, Integer> itemToChecks = new HashMap<>(plugin.getLobbyItems());
            itemToChecks.put(plugin.kitRunner,0);
            itemToChecks.put(plugin.kitBeast,0);
            itemToChecks.put(plugin.checkPoint,0);
            itemToChecks.put(plugin.exitItem,8);
            for(ItemStack item : itemToChecks.keySet()) {
                if(event.getItem().getType().equals(item.getType()) && event.getItem().getItemMeta().equals(item.getItemMeta())) {
                    ItemFunction itemAction = plugin.getCurrent(item);
                    event.setCancelled(true);
                    switch(itemAction) {
                        case SHOP:
                            player.openInventory(plugin.getUtils().getShopMenu().getInventory());
                            return;
                        case KIT_BEASTS:
                            player.openInventory(pm.getKitMenu(KitType.BEAST).getInventory());
                            return;
                        case KIT_RUNNERS:
                            player.openInventory(pm.getKitMenu(KitType.RUNNER).getInventory());
                            return;
                        case EXIT_LOBBY:
                            plugin.getUtils().sendMessage(player,"&aSending you to Lobby..");
                            return;
                        case CHECKPOINT:
                            if(pm.getPointStatus() && pm.getLastCheckpoint() != null) {
                                player.teleport(pm.getLastCheckpoint());
                                plugin.getUtils().sendMessage(player, plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.others.checkpoint.use"));
                                pm.setLastCheckpoint(null);
                                pm.setPointStatus(false);
                                plugin.getUtils().consumeItem(player,1,plugin.checkPoint);
                            }
                            return;
                        case GAME_SELECTOR:
                            player.openInventory(plugin.getGameManager().gameMenu.getInventory());
                            return;
                        case LOBBY_SELECTOR:
                        case PLAYER_SETTINGS:
                            plugin.getUtils().sendMessage(player,"&cCurrently in development");
                            return;
                        case EXIT_GAME:
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
    public void onShopMenuClick(InventoryClickEvent event) {
        Player player = (Player)event.getWhoClicked();
        if(plugin.getPlayerData(player.getUniqueId()).getGame() != null) return;
        if(event.getCurrentItem() == null) return;
        if(!event.getInventory().equals(plugin.getUtils().getShopMenu().getInventory())) return;
        HashMap<ItemStack, ShopAction> hash = plugin.getUtils().getShopMenu().getItems();
        ItemStack clickedItem = event.getCurrentItem();
        event.setCancelled(true);
        if(hash.containsKey(clickedItem)) {
            if(hash.get(clickedItem) != ShopAction.CUSTOM && hash.get(clickedItem) != ShopAction.FILL) {
                plugin.getUtils().getShopMenu().execute(player,hash.get(clickedItem));
            }
        }
    }
    @EventHandler
    public void onGameMenuClick(InventoryClickEvent event) {
        Player player = (Player)event.getWhoClicked();
        if(plugin.getPlayerData(player.getUniqueId()).getGame() != null) return;
        if(event.getCurrentItem() == null) return;
        if(!event.getInventory().equals(plugin.getGameManager().gameMenu.getInventory())) return;
        HashMap<ItemStack,String> hash = plugin.getGameManager().gameMenu.getGameItems();
        ItemStack clickedItem = event.getCurrentItem();
        event.setCancelled(true);
        if(hash.containsKey(clickedItem)) {
            plugin.getGameManager().joinGame(player,hash.get(clickedItem));
        }
    }
    @EventHandler
    public void onKitMenuClick(InventoryClickEvent event) {
        Player player = (Player)event.getWhoClicked();
        PlayerManager data = plugin.getPlayerData(player.getUniqueId());
        if(event.getCurrentItem() == null) return;
        if(event.getInventory().equals(data.getKitMenu(KitType.BEAST).getInventory())) {
            HashMap<ItemStack, String> hash = data.getKitMenu(KitType.BEAST).getItems();
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
            if (hash.containsKey(clickedItem)) {
                plugin.getKitLoader().getToSelect(KitType.BEAST, player, hash.get(clickedItem));
            }
            return;
        }
        if(event.getInventory().equals(data.getKitMenu(KitType.RUNNER).getInventory())) {
            HashMap<ItemStack, String> hash = data.getKitMenu(KitType.RUNNER).getItems();
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
            if (hash.containsKey(clickedItem)) {
                plugin.getKitLoader().getToSelect(KitType.RUNNER,player,hash.get(clickedItem));
            }
        }
    }
    @EventHandler
    public void onChestClick(PlayerInteractEvent e) {
        Game game = plugin.getPlayerData(e.getPlayer().getUniqueId()).getGame();
        if(game == null) return;
        if(game.gameStatus == GameStatus.WAITING || game.gameStatus == GameStatus.STARTING || game.gameStatus == GameStatus.RESTARTING) {
            e.setCancelled(true);
        }
        Block b = e.getClickedBlock();
        if (b == null) { return; }
        if(falseChest(b.getType())) { return; }
        if(b.getType() == Material.CHEST) e.setCancelled(true);
        if(b.getType() == Material.TRAPPED_CHEST) e.setCancelled(true);
        if(b.getType() == Material.ENDER_CHEST) e.setCancelled(true);
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
        if(game.getGameChests() == null) return;
        for(String chests : game.getGameChestsTypes()) {
            if(game.getGameChests().get(chests) != null) {
                if (game.getGameChests().get(chests).contains(location)) {
                    openGameChest(player, chests);
                    return;
                }
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
            FileConfiguration file = plugin.getStorage().getControl(GuardianFiles.SETTINGS);
            if (file.getBoolean("settings.lobbyScoreboard-only-in-lobby-world")) {
                String lC = plugin.getStorage().getControl(GuardianFiles.SETTINGS).getString("settings.lobbyLocation");
                if(lC == null ) lC = "notSet";
                if (lC.equalsIgnoreCase("notSet")) {
                    plugin.getLogs().error("-----------------------------");
                    plugin.getLogs().error("Can't show lobby-scoreboard, lobby location is not set");
                    plugin.getLogs().error("-----------------------------");
                } else {
                    Location location = plugin.getUtils().getLocationFromString(lC);
                    if (event.getPlayer().getWorld().equals(location.getWorld())) {
                        if(plugin.getStorage().getControl(GuardianFiles.SCOREBOARD).getBoolean("scoreboards.lobby.toggle")) {
                            plugin.getScoreboards().setScoreboard(GuardianBoard.LOBBY,event.getPlayer());
                        }
                    }
                }
            } else {
                if(plugin.getStorage().getControl(GuardianFiles.SCOREBOARD).getBoolean("scoreboards.lobby.toggle")) {
                    plugin.getScoreboards().setScoreboard(GuardianBoard.LOBBY,event.getPlayer());
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
        if(!plugin.getStorage().getControl(GuardianFiles.SETTINGS).getBoolean("settings.options.pluginChat")) return;
        Player player = event.getPlayer();
        PlayerManager playerManager = plugin.getPlayerData(player.getUniqueId());
        if(playerManager == null || playerManager.getGame() == null) {
            String lC = plugin.getStorage().getControl(GuardianFiles.SETTINGS).getString("settings.lobbyLocation");
            String lobbyChat = plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.others.customChat.lobby");
            if(lobbyChat == null) lobbyChat = "&7<player_name>&8: &f%message%";
            if(lC == null ) lC = "notSet";
            if (lC.equalsIgnoreCase("notSet")) {
                plugin.getLogs().error("-----------------------------");
                plugin.getLogs().error("Can't show lobby-scoreboard, lobby location is not set");
                plugin.getLogs().error("-----------------------------");
            } else {
                plugin.getLogs().debug("CHAT | " + player.getName() + ": " + event.getMessage());
                String[] loc = lC.split(",");
                World w = Bukkit.getWorld(loc[0]);
                if(w == null) return;
                if(player.getWorld() == w) {
                    event.setCancelled(true);
                    for (Player lPlayer : w.getPlayers()) {
                        plugin.getUtils().sendMessage(lPlayer, lobbyChat.replace("<player_name>", player.getName())
                                .replace("%message%", event.getMessage()));
                    }
                }
            }
            return;
        }
        event.setCancelled(true);
        plugin.getLogs().debug("CHAT | " + player.getName() + ": " + event.getMessage());
        Game game = playerManager.getGame();
        if(game.getSpectators().contains(player)) {
            String spectatorChat = plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.others.customChat.spectator");
            if(spectatorChat == null) spectatorChat = "&8[SPECTATOR] &7<player_name>&8: &f%message%";
            for(Player spectator : game.getSpectators()) {
                plugin.getUtils().sendMessage(spectator,spectatorChat.replace("<player_name>",player.getName())
                        .replace("%message%",event.getMessage()));
            }
            return;
        }
        String inGameChat = plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.others.customChat.inGame");
        String playerRole;
        if(inGameChat == null) inGameChat = "&a[%player_role%&a] &7<player_name>&8: &f%message%";
        if(game.getBeasts().contains(player)) {
            playerRole = plugin.getStorage().getControl(GuardianFiles.SETTINGS).getString("roles.beast");
        } else {
            playerRole = plugin.getStorage().getControl(GuardianFiles.SETTINGS).getString("roles.runner");
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
        if(plugin.getStorage().getControl(GuardianFiles.SETTINGS).getBoolean("settings.options.hideServerQuitMessage")) {
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
            if(game.gameStatus.equals(GameStatus.WAITING) || game.gameStatus.equals(GameStatus.STARTING)) {
                player.spigot().respawn();
                player.setGameMode(GameMode.ADVENTURE);
                player.teleport(game.waiting);
                player.setHealth(20);
                player.setFoodLevel(20);
                return;
            }
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
        Game game = plugin.getPlayerData(player.getUniqueId()).getGame();
        if(game.gameStatus == GameStatus.WAITING || game.gameStatus == GameStatus.STARTING || game.invincible) {
            event.setCancelled(true);
            if(event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                player.teleport(game.waiting);
                player.setHealth(20);
                player.setFoodLevel(20);
            }
            return;
        }
        if((player.getHealth() - event.getFinalDamage()) <= 0) {
            event.setCancelled(true);
            player.getInventory().clear();
            player.setHealth(20);
            player.setFoodLevel(20);
            player.getInventory().setBoots(null);
            player.getInventory().setHelmet(null);
            player.getInventory().setChestplate(null);
            player.getInventory().setLeggings(null);
            String deathMessage;
            deathMessage = getDeathMessage(player,event.getCause());
            for(Player inGamePlayer : game.getPlayers()) {
                plugin.getUtils().sendMessage(inGamePlayer,deathMessage);
            }
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
    private String getDeathMessage(Player player, EntityDamageEvent.DamageCause cause) {
        String returnMsg;
        switch (cause) {
            case LAVA:
                returnMsg = plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.inGame.deathMessages.lava");
                if(returnMsg == null) returnMsg = "&7%victim% was on fire!";
                return returnMsg.replace("%victim%",player.getName());
            case VOID:
                returnMsg = plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.inGame.deathMessages.void");
                if(returnMsg == null) returnMsg = "&7%victim% was searching a diamond.";
                return returnMsg.replace("%victim%",player.getName());
            default:
                returnMsg = plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.inGame.deathMessages.otherCause");
                if(returnMsg == null) returnMsg = "&7%victim% died";
                return returnMsg.replace("%victim%",player.getName());
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
            if(!game.getGameType().equals(GameType.INFECTED)) {
                player.setGameMode(GameMode.SPECTATOR);
            }
        }
    }
    @EventHandler
    public void lobbyDamage(EntityDamageEvent event) {
        if(event.getEntity().getType().equals(EntityType.PLAYER)) {
            String lC = plugin.getStorage().getControl(GuardianFiles.SETTINGS).getString("settings.lobbyLocation");
            if(lC == null) lC = "notSet";
            if (!lC.equalsIgnoreCase("notSet")) {
                String[] loc = lC.split(",");
                World w = Bukkit.getWorld(loc[0]);
                if (event.getEntity().getWorld().equals(w)) {
                    event.setCancelled(true);
                    if(plugin.getStorage().getControl(GuardianFiles.SETTINGS).getBoolean("settings.options.lobby-voidSpawnTP")) {
                        if (event.getCause().equals(EntityDamageEvent.DamageCause.VOID)) {
                            Location location = plugin.getUtils().getLocationFromString(lC);
                            event.getEntity().teleport(location);
                        }
                    }
                }
            }
            Player player = (Player)event.getEntity();
            if(plugin.getPlayerData(player.getUniqueId()).getGame() != null) {
                GuardianBoard board = plugin.getPlayerData(player.getUniqueId()).getBoard();
                if(board.equals(GuardianBoard.WAITING) || board.equals(GuardianBoard.STARTING) || board.equals(GuardianBoard.WIN_BEAST_FOR_BEAST) || board.equals(GuardianBoard.WIN_BEAST_FOR_RUNNERS) || board.equals(GuardianBoard.WIN_RUNNERS_FOR_BEAST) || board.equals(GuardianBoard.WIN_RUNNERS_FOR_RUNNERS)) {
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
                PlayerManager mng = plugin.getPlayerData(victim.getUniqueId());
                if(mng.getGame() != null) {
                    Game game = mng.getGame();
                    if(game.invincible) event.setCancelled(true);
                    if(game.getRunners().contains(victim) && game.getRunners().contains(attacker)) event.setCancelled(true);
                    if(game.getBeasts().contains(victim) && game.getBeasts().contains(attacker)) event.setCancelled(true);
                }
            }
        }
    }
    @EventHandler
    public void checkpointAdd(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        PlayerManager data = plugin.getPlayerData(player.getUniqueId());
        if(data.getGame() == null) return;
        if(event.getBlockPlaced().getType() == Material.BEACON) {
            if(!data.getPointStatus()) {
                if(event.isCancelled()) {
                    data.setLastCheckpoint(event.getBlock().getLocation());
                    data.setPointStatus(true);
                    plugin.getUtils().consumeItem(player,1,event.getBlockPlaced().getType());
                }
            } else {
                if (event.isCancelled()) {
                    plugin.getUtils().sendMessage(player, plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.others.checkpoint.already"));
                }
            }
        }
    }
    @EventHandler
    public void GameProjectile(EntityDamageByEntityEvent event) {
        if(event.getEntity().getType().equals(EntityType.PLAYER)) {
            Player victim = (Player)event.getEntity();
            if(event.getDamager() instanceof Arrow) {
                Arrow arrow = (Arrow) event.getDamager();
                Player shooter = (Player) arrow.getShooter();
                if (plugin.getPlayerData(victim.getUniqueId()).getGame() == null) return;
                Game game = plugin.getPlayerData(victim.getUniqueId()).getGame();
                if (game.getRunners().contains(victim) && game.getRunners().contains(shooter)) {
                    event.setCancelled(true);
                }
                if (game.getBeasts().contains(victim) && game.getBeasts().contains(shooter)) {
                    event.setCancelled(true);
                }
                if(!event.isCancelled()) {
                    if((victim.getHealth() - event.getFinalDamage()) <= 0) {
                        String deathMessage;
                        deathMessage = plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.inGame.deathMessages.bow");
                        if(deathMessage == null) deathMessage = "&7%victim% was shot by %attacker%";
                        if(shooter != null) {
                            deathMessage = deathMessage.replace("%victim%", victim.getName()).replace("%attacker%", shooter.getName());
                        } else {
                            deathMessage = deathMessage.replace("%victim%", victim.getName()).replace("%attacker%", "Unknown Player");
                        }
                        for(Player inGamePlayer : game.getPlayers()) {
                            plugin.getUtils().sendMessage(inGamePlayer,deathMessage);
                        }
                    }
                }
            }
        }
    }
    @EventHandler
    public void lobbyDrop(PlayerDropItemEvent event) {
        String lC = plugin.getStorage().getControl(GuardianFiles.SETTINGS).getString("settings.lobbyLocation");
        Player player = event.getPlayer();
        if(lC == null) lC = "notSet";
        if (!lC.equalsIgnoreCase("notSet")) {
            String[] loc = lC.split(",");
            World w = Bukkit.getWorld(loc[0]);
            if (player.getWorld().equals(w)) {
                event.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void gameDrop(PlayerDropItemEvent event) {
        Game game = plugin.getPlayerData(event.getPlayer().getUniqueId()).getGame();
        if(game != null) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void lobbyHunger(FoodLevelChangeEvent event) {
        if(event.getEntity().getType().equals(EntityType.PLAYER)) {
            String lC = plugin.getStorage().getControl(GuardianFiles.SETTINGS).getString("settings.lobbyLocation");
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
        if (!player.hasPermission("GuardianRFTB.admin.signCreate"))
            return;
        try {
            String line1 = event.getLine(0);
            if(line1 == null) return;
            if (line1.equalsIgnoreCase("[RFTB]")) {
                String name = event.getLine(1);
                if(name == null) name = "null";
                final Game game = plugin.getGameManager().getGame(name);
                if (game == null) {
                    String errorMsg = plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.admin.arenaError");
                    if(errorMsg == null) errorMsg = "&c%arena_id% don't exists";
                    errorMsg = errorMsg.replace("%arena_id%", name);
                    plugin.getUtils().sendMessage(player,errorMsg);
                    return;
                }
                List<String> signs = plugin.getStorage().getControl(GuardianFiles.GAMES).getStringList("games." + name + ".signs");
                signs.add(plugin.getUtils().getStringFromLocation(event.getBlock().getLocation()));
                plugin.getStorage().getControl(GuardianFiles.GAMES).set("games." + name + ".signs",signs);
                plugin.getStorage().save(SaveMode.GAMES_FILES);
                game.loadSigns();
            }
        }catch (Throwable throwable) {
            plugin.getLogs().error("Can't create plugin sign");
            plugin.getLogs().error(throwable);
        }
    }
    @SuppressWarnings("deprecation")
    @EventHandler
    public void perWorldTab(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        boolean lobbyWorld = false;
        boolean showInGamePlayers = plugin.getStorage().getControl(GuardianFiles.SETTINGS).getBoolean("settings.options.showInTabLobby-GamePlayers");
        String lC = plugin.getStorage().getControl(GuardianFiles.SETTINGS).getString("settings.lobbyLocation");
        if(lC == null) lC = "notSet";
        if (!lC.equalsIgnoreCase("notSet")) {
            String[] loc = lC.split(",");
            World w = Bukkit.getWorld(loc[0]);
            if(world == w) {
                lobbyWorld = true;
            }
        }
        if(plugin.getStorage().getControl(GuardianFiles.SETTINGS).getBoolean("settings.options.PerWorldTab")) {
            for (Player players : Bukkit.getServer().getOnlinePlayers()) {
                if (players.getWorld() == world) {
                    if(player.getGameMode() != GameMode.SPECTATOR) {
                        players.showPlayer(player);
                    }
                    if(players.getGameMode() != GameMode.SPECTATOR) {
                        player.showPlayer(players);
                    }
                } else {
                    players.hidePlayer(player);
                    if(!lobbyWorld && !showInGamePlayers) {
                        player.hidePlayer(players);
                    }
                }
            }
        }
    }
    @SuppressWarnings("deprecation")
    @EventHandler
    public void onJoinTab(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        boolean lobbyWorld = false;
        boolean showInGamePlayers = plugin.getStorage().getControl(GuardianFiles.SETTINGS).getBoolean("settings.options.showInTabLobby-GamePlayers");
        String lC = plugin.getStorage().getControl(GuardianFiles.SETTINGS).getString("settings.lobbyLocation");
        if(lC == null) lC = "notSet";
        if (!lC.equalsIgnoreCase("notSet")) {
            String[] loc = lC.split(",");
            World w = Bukkit.getWorld(loc[0]);
            if(world == w) {
                lobbyWorld = true;
            }
        }
        if(plugin.getStorage().getControl(GuardianFiles.SETTINGS).getBoolean("settings.options.PerWorldTab")) {
            for (Player players : Bukkit.getServer().getOnlinePlayers()) {
                if (players.getWorld() == world) {
                    if(player.getGameMode() != GameMode.SPECTATOR) {
                        players.showPlayer(player);
                    }
                    if(players.getGameMode() != GameMode.SPECTATOR) {
                        player.showPlayer(players);
                    }
                } else {
                    players.hidePlayer(player);
                    if(!lobbyWorld && !showInGamePlayers) {
                        player.hidePlayer(players);
                    }
                }
            }
        }
    }
    @EventHandler
    public void lobbyClickInventory(InventoryClickEvent event) {
        if(plugin.getStorage().getControl(GuardianFiles.SETTINGS).getBoolean("settings.options.lobby-blockInventoryClick")) {
            FileConfiguration file = plugin.getStorage().getControl(GuardianFiles.SETTINGS);
            String lC = file.getString("settings.lobbyLocation");
            if(lC == null) lC = "notSet";
            if (lC.equalsIgnoreCase("notSet")) {
                plugin.getLogs().error("-----------------------------");
                plugin.getLogs().error("Can't teleport player to lobby location, lobby location is not set");
                plugin.getLogs().error("-----------------------------");
            } else {
                String[] loc = lC.split(",");
                World w = Bukkit.getWorld(loc[0]);
                if(event.getWhoClicked().getWorld() == w) {
                    event.setCancelled(true);
                }
            }
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
            FileConfiguration file = plugin.getStorage().getControl(GuardianFiles.SETTINGS);
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
                    if(plugin.getStorage().getControl(GuardianFiles.SCOREBOARD).getBoolean("scoreboards.lobby.toggle")) {
                        plugin.getScoreboards().setScoreboard(GuardianBoard.LOBBY,event.getPlayer());
                    }
                    if(file.getBoolean("settings.options.joinHeal")) {
                        event.getPlayer().setHealth(20.0D);
                        event.getPlayer().setLevel(0);
                        event.getPlayer().setFoodLevel(20);
                        event.getPlayer().setExp(0.0F);
                    }
                    if(file.getBoolean("settings.options.lobby-actionBar")) {
                        plugin.getUtils().sendActionbar(event.getPlayer(),plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.lobby.actionBar"));
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