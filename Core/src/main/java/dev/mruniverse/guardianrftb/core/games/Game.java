package dev.mruniverse.guardianrftb.core.games;

import dev.mruniverse.guardianrftb.core.GuardianRFTB;
import dev.mruniverse.guardianrftb.core.api.BeastDeathEvent;
import dev.mruniverse.guardianrftb.core.api.GameEndEvent;
import dev.mruniverse.guardianrftb.core.api.GameStartEvent;
import dev.mruniverse.guardianrftb.core.api.RunnerDeathEvent;
import dev.mruniverse.guardianrftb.core.enums.GuardianFiles;
import dev.mruniverse.guardianrftb.core.enums.PlayerStatus;
import dev.mruniverse.guardianrftb.core.enums.GuardianBoard;
import dev.mruniverse.guardianrftb.core.utils.players.PlayerManager;
import org.bukkit.*;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;

import java.util.*;
@SuppressWarnings("deprecation")
public class Game {
    private final String gameName;
    private final String configGameName;
    private final String gamePath;
    private GameType gameType;
    private final HashMap<String, List<Location>> gameChests;
    private final ArrayList<Player> players;
    private final ArrayList<Location> signs;
    private final ArrayList<Player> runners;
    private ArrayList<Player> beasts;
    private final ArrayList<Player> spectators;
    private final ArrayList<String> gameChestsTypes;
    int times;
    public int gameTimer;
    public int timer;
    public int time;
    public int min;
    public int worldTime;
    public int max;
    public int fakeStarting;
    public int starting;
    public int ending;
    public int inventoryNumber;
    public int menuSlot;
    public Sound gameSound1;
    public Sound gameSound2;
    public Sound gameSound3;

    public Location waiting;
    public Location selectedBeast;
    public Location beastLocation;
    public Location runnersLocation;

    private final FileConfiguration gameFile;
    private final FileConfiguration settingsFile;
    private final FileConfiguration messagesFile;
    private final GuardianRFTB plugin;

    public GameStatus gameStatus;
    public boolean invincible = true;
    public boolean preparingStage;
    public boolean startingStage;
    public boolean selectingStage;
    public boolean inGameStage;
    public boolean playingStage;
    public boolean endingStage;

    public Game(GuardianRFTB main, String configName, String gameName) {
        gameTimer = 0;
        gameFile = main.getStorage().getControl(GuardianFiles.GAMES);
        gameChestsTypes = new ArrayList<>();
        settingsFile = main.getStorage().getControl(GuardianFiles.SETTINGS);
        gameChests = new HashMap<>();
        messagesFile = main.getStorage().getControl(GuardianFiles.MESSAGES);
        plugin = main;
        players = new ArrayList<>();
        signs = new ArrayList<>();
        spectators = new ArrayList<>();
        timer = 500;
        menuSlot = -1;
        min = 2;
        time = 0;
        max = 10;
        fakeStarting = 0;
        beastLocation = null;
        runnersLocation = null;
        runners = new ArrayList<>();
        beasts = new ArrayList<>();
        preparingStage = true;
        selectingStage = false;
        startingStage = false;
        inGameStage = false;
        playingStage = false;
        endingStage = false;
        starting = 30;
        ending = 150;
        times = 0;
        inventoryNumber = -1;
        this.gameName = gameName;
        configGameName = configName;
        gamePath = "games." + configName + ".";
        try {
            loadGame();
        } catch (Throwable throwable) {
            GuardianRFTB.getInstance().getLogs().error("Can't load arena: " + gameName);
            GuardianRFTB.getInstance().getLogs().error(throwable);
            preparingStage = false;
            gameStatus = GameStatus.PREPARING;
        }
    }

    public GameType getGameType() {
        return gameType;
    }

    public HashMap<String, List<Location>> getGameChests() {
        return gameChests;
    }

    public ArrayList<Location> getSigns() {
        return signs;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public ArrayList<Player> getRunners() {
        return runners;
    }

    public ArrayList<Player> getBeasts() {
        return beasts;
    }

    public ArrayList<Player> getSpectators() {
        return spectators;
    }

    public ArrayList<String> getGameChestsTypes() {
        return gameChestsTypes;
    }

    private void loadGame() {
        timer = gameFile.getInt(gamePath + "time");
        worldTime = gameFile.getInt(gamePath + "worldTime");
        max = gameFile.getInt(gamePath + "max");
        min = gameFile.getInt(gamePath + "min");
        if(min == 1) min = 2;
        gameType = GameType.valueOf(gameFile.getString(gamePath + "gameType"));
        String bL = gameFile.getString(gamePath + "locations.beast");
        if(bL == null) bL = "notSet";
        beastLocation = plugin.getUtils().getLocationFromString(bL);
        String sbL = gameFile.getString(gamePath + "locations.selected-beast");
        if(sbL == null) sbL = "notSet";
        selectedBeast = plugin.getUtils().getLocationFromString(sbL);
        String rL = gameFile.getString(gamePath + "locations.runners");
        if(rL == null) rL = "notSet";
        runnersLocation = plugin.getUtils().getLocationFromString(rL);
        String wL = gameFile.getString(gamePath + "locations.waiting");
        if(wL == null) wL = "notSet";
        waiting = plugin.getUtils().getLocationFromString(wL);
        if (beastLocation == null || runnersLocation == null || selectedBeast == null || waiting == null) {
            preparingStage = false;
            gameStatus = GameStatus.PREPARING;
        }
        gameStatus = GameStatus.WAITING;
        loadSigns();
        loadChests();
        try {
            gameSound1 = Sound.valueOf(gameFile.getString(gamePath + "gameSound1"));
            gameSound2 = Sound.valueOf(gameFile.getString(gamePath + "gameSound2"));
            gameSound3 = Sound.valueOf(gameFile.getString(gamePath + "gameSound3"));
        }catch (Throwable throwable) {
            plugin.getLogs().error("Can't load game Sounds! Please verify if the sound works in your current version!");
        }
    }

    public void loadChestType(String chestName) {
        try {
            if(gameFile.get(gamePath + "chests-location." + chestName) != null) {
                List<Location> newLocations = new ArrayList<>();
                for(String locations : gameFile.getStringList(gamePath + "chests-location." + chestName)) {
                    newLocations.add(plugin.getUtils().getLocationFromString(locations));
                }
                gameChests.put(chestName,newLocations);
            }
        }catch (Throwable throwable){
            plugin.getLogs().error("Can't load game Chests");
            plugin.getLogs().error(throwable);
        }
    }

    public void loadChests() {
        try {
            if(gameFile.get(gamePath + "chests") != null) {
                for(String chestType : gameFile.getStringList(gamePath + "chests")) {
                    gameChestsTypes.add(chestType);
                    loadChestType(chestType);
                }
            }
        }catch (Throwable throwable){
            plugin.getLogs().error("Can't load game Chests");
            plugin.getLogs().error(throwable);
        }
    }

    public void loadSigns() {
        signs.clear();
        for(String sign : gameFile.getStringList(gamePath + "signs")) {
            Location signLocation = plugin.getUtils().getLocationFromString(sign);
            if(signLocation != null) {
                if(signLocation.getBlock().getState() instanceof Sign) {
                    signs.add(signLocation);
                }
            }
        }
        updateSigns();
    }

    public void updateSigns() {
        String line1,line2,line3,line4;
        line1 = settingsFile.getString("settings.signs.line1");
        line2 = settingsFile.getString("settings.signs.line2");
        line3 = settingsFile.getString("settings.signs.line3");
        line4 = settingsFile.getString("settings.signs.line4");
        if(line1 == null) line1 = "&l%arena%";
        if(line2 == null) line2 = "%gameStatus%";
        if(line3 == null) line3 = "%on%/%max%";
        if(line4 == null) line4 = "&nClick to join";
        if(plugin.getGameManager().getGameMenu(this.gameType) != null) {
            plugin.getGameManager().getGameMenu(this.gameType).updateSlot(menuSlot, this);
        }
        for(Location signLocation : this.signs) {
            boolean worldLoaded = true;
            World signWorld = signLocation.getWorld();
            if(signWorld != null) {
                if (Bukkit.getWorld(signWorld.getName()) == null) worldLoaded = false;
                if (worldLoaded && signWorld.getChunkAt(signLocation).isLoaded()) {
                    if (signLocation.getBlock().getState() instanceof Sign) {
                        Sign currentSign = (Sign) signLocation.getBlock().getState();
                        currentSign.setLine(0, replaceGameVariable(line1));
                        currentSign.setLine(1, replaceGameVariable(line2));
                        currentSign.setLine(2, replaceGameVariable(line3));
                        currentSign.setLine(3, replaceGameVariable(line4));
                        currentSign.update();
                    }
                }
            }
        }
    }

    public String replaceGameVariable(String message) {
        if(message.contains("%arena%")) message = message.replace("%arena%",gameName);
        if(message.contains("%gameStatus%")) message = message.replace("%gameStatus%",getStatus());
        if(message.contains("%on%")) message = message.replace("%on%",players.size() + "");
        if(message.contains("%max%")) message = message.replace("%max%", max + "");
        return ChatColor.translateAlternateColorCodes('&',message);
    }

    public String getStatus() {
        switch (gameStatus) {
            case WAITING:
                return settingsFile.getString("settings.gameStatus.waiting");
            case PLAYING:
                return settingsFile.getString("settings.gameStatus.playing");
            case STARTING:
                return settingsFile.getString("settings.gameStatus.starting");
            case RESTARTING:
                return settingsFile.getString("settings.gameStatus.ending");
            case IN_GAME:
                return settingsFile.getString("settings.gameStatus.InGame");
            case PREPARING:
            default:
                return settingsFile.getString("settings.gameStatus.preparing");
        }
    }
    public int getNeedPlayers() {
        int RealMin = min;
        if((gameType == GameType.DOUBLE_BEAST || gameType == GameType.ISLAND_OF_THE_BEAST_DOUBLE_BEAST) && min == 2) {
            RealMin = 3;
        }
        if((RealMin - players.size()) <= 0 && gameStatus.equals(GameStatus.WAITING)) {
            gameStatus = GameStatus.STARTING;
            for(Player player : players) {
                PlayerManager playerData = plugin.getPlayerData(player.getUniqueId());
                playerData.setBoard(GuardianBoard.STARTING);
            }
        }
        if(players.size() < RealMin) {
            return (RealMin - players.size());
        }
        if(gameStatus.equals(GameStatus.WAITING)) {
            gameStatus = GameStatus.STARTING;
            for(Player player : players) {
                PlayerManager playerData = plugin.getPlayerData(player.getUniqueId());
                playerData.setBoard(GuardianBoard.STARTING);
            }
        }
        return 0;
    }
    public String getGameName() {
        return configGameName;
    }
    public String getName() {
        return gameName;
    }

    public void join(Player player) {
        if(plugin.getPlayerData(player.getUniqueId()).getGame() != null) {
            plugin.getUtils().sendMessage(player, messagesFile.getString("messages.inGame.already"));
            return;
        }
        if(!gameStatus.equals(GameStatus.WAITING) && !gameStatus.equals(GameStatus.STARTING)) {
            if(!gameStatus.equals(GameStatus.RESTARTING)) {
                plugin.getUtils().sendMessage(player, messagesFile.getString("messages.others.gamePlaying"));
                return;
            }
            plugin.getUtils().sendMessage(player, messagesFile.getString("messages.others.restarting"));
            return;
        }
        if(players.size() >= max) {
            plugin.getUtils().sendMessage(player, messagesFile.getString("messages.others.full"));
            return;
        }
        player.getInventory().clear();
        player.setGameMode(GameMode.ADVENTURE);
        player.teleport(this.waiting);
        plugin.getPlayerData(player.getUniqueId()).setBoard(GuardianBoard.WAITING);
        if(gameStatus.equals(GameStatus.STARTING)) {
            if(selectingStage) {
                plugin.getPlayerData(player.getUniqueId()).setBoard(GuardianBoard.SELECTING);
            } else {
                plugin.getPlayerData(player.getUniqueId()).setBoard(GuardianBoard.STARTING);
            }
        }
        players.add(player);
        runners.add(player);
        plugin.getPlayerData(player.getUniqueId()).setGame(this);
        plugin.getPlayerData(player.getUniqueId()).setStatus(PlayerStatus.IN_GAME);
        checkPlayers();
        player.setGameMode(GameMode.ADVENTURE);
        player.setFlying(false);
        player.setAllowFlight(false);
        player.setHealth(20.0D);
        player.setFireTicks(0);
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
        player.getInventory().setItem(plugin.RunnerSlot,plugin.kitRunner);
        player.getInventory().setItem(plugin.exitSlot,plugin.exitItem);
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        String joinMsg = messagesFile.getString("messages.inGame.join");
        if(joinMsg == null) joinMsg = "&7%player% &ehas joined &e(&b%online%&e/&b%max%&e)!";
        for(Player pl : players) {
            plugin.getUtils().sendMessage(pl,joinMsg.replace("%player%",player.getName())
            .replace("%online%",this.players.size()+"")
            .replace("%max%",this.max+""));
        }
        updateSigns();
        player.updateInventory();
    }
    public void checkPlayers() {
        this.endingStage = false;
        int realMin = min;
        if(min == 2 && gameType.equals(GameType.DOUBLE_BEAST)) {
            realMin = 3;
        }
        if (players.size() == realMin && !startingStage && !gameStatus.equals(GameStatus.STARTING)) {
            gameStatus = GameStatus.STARTING;
            selectingStage = true;
            startingStage = true;
            gameTimer = 1;
            for(Player runner : runners) {
                plugin.getPlayerData(runner.getUniqueId()).setBoard(GuardianBoard.SELECTING);
            }
            for(Player beast : beasts) {
                plugin.getPlayerData(beast.getUniqueId()).setBoard(GuardianBoard.SELECTING);
            }
        }
    }
    public void gameCount(GameCountType gameCountType) {
        try {
            int realMin = min;
            if(min == 2 && gameType.equals(GameType.DOUBLE_BEAST)) {
                realMin = 3;
            }
            if (gameCountType.equals(GameCountType.START_COUNT)) {
                if(gameStatus.equals(GameStatus.STARTING)) {
                    if(players.size() < realMin) {
                        startingStage = false;
                        selectingStage = false;
                        gameTimer = 0;
                        gameStatus = GameStatus.WAITING;
                        for(Player player : players) {
                            beasts.remove(player);
                            plugin.getPlayerData(player.getUniqueId()).setBoard(GuardianBoard.WAITING);
                            player.teleport(waiting);
                            player.getInventory().clear();
                            player.getInventory().setItem(plugin.RunnerSlot,plugin.kitRunner);
                            player.getInventory().setItem(plugin.exitSlot,plugin.exitItem);
                            if(!runners.contains(player)) {
                                runners.add(player);
                            }
                        }
                    }
                }
                if (starting < -25) {
                    gameTimer = 2;
                    return;
                }
                if (starting <= 5 && starting >= 0)
                    if (gameSound1 != null) {
                        for (Player members : players) {
                            members.playSound(members.getLocation(), gameSound1, 1.0F, 0.5F);
                        }
                    }
                if (starting <= -20 && starting >= -25) {
                    if (gameSound2 != null) {
                        for (Player runner : runners) {
                            runner.playSound(runner.getLocation(), gameSound2, 1.0F, 0.8F);
                        }
                    }
                    if (gameSound1 != null) {
                        for (Player beast : beasts) {
                            beast.playSound(beast.getLocation(), gameSound1, 1.0F, 0.5F);
                        }
                    }
                }
                if(starting == -19 || starting == -18 || starting == -17 || starting == -16 || starting == -15) {
                    if(this.starting == -19) fakeStarting = 6;
                    if(this.starting == -18) fakeStarting = 7;
                    if(this.starting == -17) fakeStarting = 8;
                    if(this.starting == -16) fakeStarting = 9;
                    if(this.starting == -15) fakeStarting = 10;
                }
                if(starting == -24 || starting == -23 || starting == -22 || starting == -21 || starting == -20) {
                    for(Player player : players) {
                        int s = 5;
                        fakeStarting = 5;
                        String seconds = messagesFile.getString("times.seconds");
                        String second = messagesFile.getString("times.seconds");
                        String beastSpawn = messagesFile.getString("messages.inGame.beastsAppear");
                        if(beastSpawn == null) beastSpawn = "&eThe beasts spawns in &c%time% &e%seconds%!";
                        if(second == null) second = "second";
                        if(seconds == null) seconds = "seconds";
                        if(this.starting == -21) {
                            fakeStarting = 4;
                            s = 4;
                        }
                        if(this.starting == -22) {
                            fakeStarting = 3;
                            s = 3;
                        }
                        if(this.starting == -23) {
                            fakeStarting = 2;
                            s = 2;
                        }
                        if(this.starting == -24) {
                            fakeStarting = 1;
                            s = 1;
                        }
                        if(s != 1) {
                            plugin.getUtils().sendMessage(player, beastSpawn.replace("%time%", s + "").replace("%seconds%",seconds));
                        } else {
                            plugin.getUtils().sendMessage(player, beastSpawn.replace("%time%", s + "").replace("%seconds%",second));
                        }
                    }
                }
                if (starting == -25) {
                    starting--;
                    invincible = false;
                    gameTimer = 2;
                    if (beasts.size() <= 0) {
                        winRunners();
                        return;
                    }
                    for (Player beast : beasts) {
                        beast.teleport(beastLocation);
                        beast.setFoodLevel(20);
                        beast.setHealth(20.0D);
                        beast.getInventory().clear();
                        giveBeastInv(beast);
                        beast.setExp(0.0F);
                        beast.setLevel(0);
                        beast.setFireTicks(0);
                        plugin.getPlayerData(beast.getUniqueId()).setBoard(GuardianBoard.PLAYING);
                        plugin.getUtils().sendList(beast,messagesFile.getStringList("messages.inGame.infoList.startInfo"));
                        plugin.getUtils().sendTitle(beast, 0, 20, 10, messagesFile.getString("messages.inGame.others.titles.runnersGo.toBeasts.title"), messagesFile.getString("messages.inGame.others.titles.runnersGo.toBeasts.subtitle"));
                    }
                    if (gameSound3 != null) {
                        for (Player runner : players)
                            runner.playSound(runner.getLocation(), gameSound3, 10.0F, 1.0F);
                    }
                }
                if (runners.size() < 1) {
                    if (!inGameStage) {
                        startingStage = false;
                        selectingStage = false;
                        gameStatus = GameStatus.WAITING;
                        starting = 30;
                        for (Player player : players) {
                            player.teleport(waiting);
                            player.getInventory().clear();
                            player.getInventory().setArmorContents(null);
                            beasts = new ArrayList<>();
                            if (!runners.contains(player))
                                runners.add(player);
                            plugin.getPlayerData(player.getUniqueId()).setBoard(GuardianBoard.WAITING);
                            plugin.getUtils().sendMessage(player, messagesFile.getString("messages.inGame.cantStartGame"));
                        }
                        gameTimer = 0;
                        return;
                    }
                    if (!playingStage) {
                        String LST = settingsFile.getString("settings.lobbyLocation");
                        if(LST == null) LST = "notSet";
                        Location location = plugin.getUtils().getLocationFromString(LST);
                        for (Player player : players) {
                            if(location != null) {
                                if(player.isOnline()) {
                                    player.teleport(location);
                                }
                            }
                            player.getInventory().setHelmet(null);
                            player.getInventory().setChestplate(null);
                            player.getInventory().setLeggings(null);
                            player.getInventory().setBoots(null);
                            plugin.getPlayerData(player.getUniqueId()).setStatus(PlayerStatus.IN_LOBBY);
                            plugin.getPlayerData(player.getUniqueId()).setGame(null);
                            plugin.getPlayerData(player.getUniqueId()).setBoard(GuardianBoard.LOBBY);
                            player.getInventory().clear();
                            for (ItemStack item : plugin.getLobbyItems().keySet()) {
                                player.getInventory().setItem(plugin.getSlot(item), item);
                            }
                            player.updateInventory();
                            plugin.getUtils().sendMessage(player, messagesFile.getString("messages.inGame.cantStartGame"));
                        }
                        restart();
                    } else {
                        if (beasts.size() == 0) {
                            winRunners();
                            gameTimer = 0;
                            return;
                        }
                        winBeasts();
                        gameTimer = 0;
                        return;
                    }
                }
                if (starting == -12) {
                    invincible = false;
                }
                if (starting == -10) {
                    playingStage = true;
                    gameStatus = GameStatus.PLAYING;
                    GameStartEvent event = new GameStartEvent(this);
                    plugin.getServer().getPluginManager().callEvent(event);
                    for (Player runner : runners) {
                        runner.teleport(runnersLocation);
                        runner.getInventory().clear();
                        runner.updateInventory();
                        plugin.getPlayerData(runner.getUniqueId()).setBoard(GuardianBoard.PLAYING);
                        plugin.getItems(GameEquip.RUNNER_KIT,runner);
                        plugin.getUtils().sendList(runner,messagesFile.getStringList("messages.inGame.infoList.startInfo"));
                        plugin.getUtils().sendTitle(runner, 0, 20, 10, messagesFile.getString("messages.inGame.others.titles.runnersGo.toRunners.title"), messagesFile.getString("messages.inGame.others.titles.runnersGo.toRunners.subtitle"));
                    }
                    fakeStarting = 10;
                    for(Player beast : beasts) {
                        plugin.getPlayerData(beast.getUniqueId()).setBoard(GuardianBoard.BEAST_SPAWN);
                    }
                }
                if (starting == 0) {
                    inGameStage = true;
                    gameStatus = GameStatus.IN_GAME;
                    Objects.requireNonNull(runnersLocation.getWorld()).setTime(worldTime);
                    runnersLocation.getWorld().setThundering(false);
                    runnersLocation.getWorld().setStorm(false);
                    runnersLocation.getWorld().setSpawnFlags(false, false);
                    endingStage = false;
                    for (Player player : players) {
                        plugin.getPlayerData(player.getUniqueId()).setBoard(GuardianBoard.PLAYING);
                        plugin.getUtils().sendTitle(player, 5, 40, 5, messagesFile.getString("messages.inGame.others.titles.gameStart.title"), messagesFile.getString("messages.inGame.others.titles.gameStart.subtitle"));
                    }

                }
                if (starting == -2)
                    selectBeast();
                if (starting == -2)
                    for (Player runner : runners) {
                        runner.getInventory().clear();
                        runner.getInventory().setArmorContents(null);
                    }
                if (starting == 30) {
                    endingStage = false;
                    Objects.requireNonNull(waiting.getWorld()).setTime(time);
                    waiting.getWorld().setThundering(false);
                    waiting.getWorld().setStorm(false);
                    waiting.getWorld().setDifficulty(Difficulty.PEACEFUL);
                    waiting.getWorld().setSpawnFlags(false, false);
                    for (Player player : players) {
                        player.closeInventory();
                        player.setHealth(20.0D);
                    }
                }
                if(starting >= 1) {
                    if(starting > 1) {
                        String startMsg = messagesFile.getString("messages.inGame.selectingBeast");
                        String seconds = messagesFile.getString("times.seconds");
                        if(startMsg == null) startMsg = "&eThe beast will be selected in &c%time% &e%seconds%!";
                        if(seconds == null) seconds = "seconds";
                        if(starting == 15 || starting == 10 || starting == 5 || starting == 4 || starting == 3 || starting == 2) {
                            for(Player player : players) {
                                plugin.getUtils().sendMessage(player,startMsg.replace("%time%",starting+"").replace("%seconds%",seconds));
                            }
                        }
                    }  else {
                        String startMsg = messagesFile.getString("messages.inGame.selectingBeast");
                        String second = messagesFile.getString("times.second");
                        if(startMsg == null) startMsg = "&eThe beast will be selected in &c%time% &e%seconds%!";
                        if(second == null) second = "second";
                        for(Player player : players) {
                            plugin.getUtils().sendMessage(player,startMsg.replace("%time%",starting+"").replace("%seconds%",second));
                        }
                    }
                }
                if(this.starting <= -5) {
                    if(this.starting > -9) {
                        String startMsg = messagesFile.getString("messages.inGame.starting");
                        String seconds = messagesFile.getString("times.seconds");
                        if(startMsg == null) startMsg = "&eThe game starts in &c%time% &e%seconds%!";
                        if(seconds == null) seconds = "seconds";
                        int perfectTime = 5;
                        fakeStarting = 5;
                        if(starting == -8) {
                            perfectTime = 2;
                            fakeStarting = 2;
                        }
                        if(starting == -7) {
                            perfectTime = 3;
                            fakeStarting = 3;
                        }
                        if(starting == -6) {
                            perfectTime = 4;
                            fakeStarting = 4;
                        }
                        for(Player player : players) {
                            plugin.getUtils().sendMessage(player,startMsg.replace("%time%",perfectTime+"").replace("%seconds%",seconds));
                        }
                    } else if(starting == -9){
                        String startMsg = messagesFile.getString("messages.inGame.starting");
                        String second = messagesFile.getString("times.second");
                        if(startMsg == null) startMsg = "&eThe game starts in &c%time% &e%seconds%!";
                        if(second == null) second = "second";
                        for(Player player : players) {
                            plugin.getUtils().sendMessage(player,startMsg.replace("%time%",1+"").replace("%seconds%",second));
                        }
                    }
                }
                starting--;
                updateSigns();
                return;
            }
            updateSigns();
            if(beasts.size() == 0) {
                if(!endingStage && !gameStatus.equals(GameStatus.RESTARTING)) {
                    winRunners();
                }
                return;
            }
            if(runners.size() == 0) {
                if(!endingStage && !gameStatus.equals(GameStatus.RESTARTING)) {
                    winBeasts();
                }
                return;
            }
            if (timer == 0) {
                winRunners();
                return;
            }
            timer--;
        }catch (Throwable throwable) {
            plugin.getLogs().error("Can't play in " + gameName + ", because an error occurred in the principal Game-Count");
            plugin.getLogs().error(throwable);
        }
    }
    public void selectBeast() {
        String chosenBeast = messagesFile.getString("messages.inGame.chosenBeast");
        if (chosenBeast == null) chosenBeast = "&eThe player &b%player% &enow is a beast!";
        if (this.gameType.equals(GameType.CLASSIC) || this.gameType.equals(GameType.INFECTED)) {
            Random random = new Random();
            this.times++;
            int beast = random.nextInt(this.runners.size());
            Player nextBeast = this.runners.get(beast);
            this.beasts.add(nextBeast);
            plugin.getItems(GameEquip.BEAST_KIT,nextBeast);
            this.runners.remove(nextBeast);
            for (Player announce : this.players) {
                plugin.getUtils().sendMessage(announce, chosenBeast.replace("%player%", nextBeast.getName()));
            }
            nextBeast.getInventory().clear();
            nextBeast.getInventory().setHelmet(null);
            nextBeast.getInventory().setChestplate(null);
            nextBeast.getInventory().setLeggings(null);
            nextBeast.getInventory().setBoots(null);
            nextBeast.getInventory().setItem(plugin.beastSlot, plugin.kitBeast);
            nextBeast.getInventory().setItem(plugin.exitSlot, plugin.exitItem);
            nextBeast.updateInventory();
            nextBeast.teleport(selectedBeast);
            changeToStartingBoard();
            this.times = 0;
            return;
        }
        Random random = new Random();
        int beast = random.nextInt(this.runners.size());
        Player nextBeast = this.runners.get(beast);
        this.beasts.add(nextBeast);
        plugin.getItems(GameEquip.BEAST_KIT, nextBeast);
        nextBeast.teleport(selectedBeast);
        nextBeast.getInventory().clear();
        nextBeast.getInventory().setHelmet(null);
        nextBeast.getInventory().setChestplate(null);
        nextBeast.getInventory().setLeggings(null);
        nextBeast.getInventory().setBoots(null);
        nextBeast.getInventory().setItem(plugin.beastSlot, plugin.kitBeast);
        nextBeast.getInventory().setItem(plugin.exitSlot, plugin.exitItem);
        nextBeast.updateInventory();
        this.runners.remove(nextBeast);
        this.spectators.remove(nextBeast);
        for(Player announce : this.players) {
            plugin.getUtils().sendMessage(announce,chosenBeast.replace("%player%",nextBeast.getName()));
        }
        int Beast = random.nextInt(this.runners.size());
        Player NextBeast = this.runners.get(Beast);
        this.beasts.add(NextBeast);
        this.runners.remove(NextBeast);
        this.spectators.remove(NextBeast);
        NextBeast.getInventory().clear();
        NextBeast.getInventory().setHelmet(null);
        NextBeast.getInventory().setChestplate(null);
        NextBeast.getInventory().setLeggings(null);
        NextBeast.getInventory().setBoots(null);
        NextBeast.getInventory().setItem(plugin.beastSlot,plugin.kitBeast);
        for(Player announce : this.players) {
            plugin.getUtils().sendMessage(announce,chosenBeast.replace("%player%",NextBeast.getName()));
        }
        NextBeast.getInventory().setItem(plugin.exitSlot,plugin.exitItem);
        NextBeast.updateInventory();
        plugin.getItems(GameEquip.BEAST_KIT,NextBeast);
        NextBeast.teleport(selectedBeast);
        changeToStartingBoard();
        this.runners.remove(nextBeast);
        this.times = 0;

    }
    public void changeToStartingBoard() {
        for(Player runner : this.runners) {
            plugin.getPlayerData(runner.getUniqueId()).setBoard(GuardianBoard.STARTING);
        }
        for(Player beast : this.beasts) {
            plugin.getPlayerData(beast.getUniqueId()).setBoard(GuardianBoard.STARTING);
        }
    }
    private int getSlot(ItemStack item) {
        return plugin.getBeastInventory().get(item);
    }
    public void giveBeastInv(Player beast){
        for(ItemStack inventory : plugin.getBeastInventory().keySet()) {
            beast.getInventory().setItem(getSlot(inventory),inventory);
        }
        beast.getInventory().setHelmet(plugin.beastHelmet);
        beast.getInventory().setChestplate(plugin.beastChestplate);
        beast.getInventory().setLeggings(plugin.beastLeggings);
        beast.getInventory().setBoots(plugin.beastBoots);
    }
    public void deathBeast(Player beast) {
        beasts.remove(beast);
        spectators.add(beast);
        plugin.getPlayerData(beast.getUniqueId()).addDeaths();
        beast.setGameMode(GameMode.SPECTATOR);
        for(Player player : players) {
            player.hidePlayer(beast);
        }
        BeastDeathEvent event = new BeastDeathEvent(this,beast);
        plugin.getServer().getPluginManager().callEvent(event);
        if(beasts.size() == 0) {
            plugin.getPlayerData(beast.getUniqueId()).setBoard(GuardianBoard.WIN_RUNNERS_FOR_BEAST);
            winRunners();
        }
    }
    public void deathRunner(Player runner) {
        runners.remove(runner);
        RunnerDeathEvent event = new RunnerDeathEvent(this,runner);
        plugin.getServer().getPluginManager().callEvent(event);
        if(!gameType.equals(GameType.INFECTED)) {
            spectators.add(runner);
            for(Player player : players) {
                player.hidePlayer(runner);
            }
            runner.setGameMode(GameMode.SPECTATOR);
        } else {
            beasts.add(runner);
            runner.getInventory().clear();
            giveBeastInv(runner);
            runner.setGameMode(GameMode.ADVENTURE);
            runner.teleport(beastLocation);
        }
        plugin.getPlayerData(runner.getUniqueId()).addDeaths();
        if(runners.size() == 0) {
            plugin.getPlayerData(runner.getUniqueId()).setBoard(GuardianBoard.WIN_BEAST_FOR_RUNNERS);
            winBeasts();
        }
    }
    public void winRunners() {
        for(Player player : this.players) {
            if(this.spectators.contains(player)) player.setGameMode(GameMode.SPECTATOR);
            if(gameSound3 != null) {
                player.playSound(player.getLocation(),gameSound3, 10.0F, 1.0F);
            }
            plugin.getUtils().sendGameList(player, messagesFile.getStringList("messages.inGame.infoList.endInfo"),GameTeam.RUNNERS);
        }
        for(Player runner : this.runners) {
            plugin.getPlayerData(runner.getUniqueId()).setBoard(GuardianBoard.WIN_RUNNERS_FOR_RUNNERS);
            plugin.getPlayerData(runner.getUniqueId()).addWins();
        }
        for(Player beast : this.beasts) {
            plugin.getPlayerData(beast.getUniqueId()).setBoard(GuardianBoard.WIN_RUNNERS_FOR_BEAST);
        }
        for(Player spectator : this.spectators) {
            plugin.getPlayerData(spectator.getUniqueId()).setBoard(GuardianBoard.WIN_RUNNERS_FOR_BEAST);
        }
        this.invincible = true;
        this.gameTimer = 0;
        this.endingStage = true;
        this.gameStatus = GameStatus.RESTARTING;
        GameEndEvent event = new GameEndEvent(this);
        plugin.getServer().getPluginManager().callEvent(event);
        timerToLobby(GameTeam.RUNNERS);
    }
    public void winBeasts() {
        for(Player player : this.players) {
            if(gameSound3 != null) {
                player.playSound(player.getLocation(),gameSound3, 10.0F, 1.0F);
            }
            plugin.getUtils().sendGameList(player, messagesFile.getStringList("messages.inGame.infoList.endInfo"),GameTeam.BEASTS);
        }
        for(Player beast : this.beasts) {
            plugin.getPlayerData(beast.getUniqueId()).setBoard(GuardianBoard.WIN_BEAST_FOR_BEAST);
            plugin.getPlayerData(beast.getUniqueId()).addWins();
        }
        for(Player runner : this.runners) {
            plugin.getPlayerData(runner.getUniqueId()).setBoard(GuardianBoard.WIN_BEAST_FOR_RUNNERS);
        }
        for(Player spectator : this.spectators) {
            plugin.getPlayerData(spectator.getUniqueId()).setBoard(GuardianBoard.WIN_BEAST_FOR_RUNNERS);
        }
        this.invincible = true;
        this.gameTimer = 0;
        this.endingStage = true;
        this.gameStatus = GameStatus.RESTARTING;
        timerToLobby(GameTeam.BEASTS);
    }
    public void timerToLobby(GameTeam winnerTeam) {
        String LST = settingsFile.getString("settings.lobbyLocation");
        if(LST == null) LST = "notSet";
        Location location = plugin.getUtils().getLocationFromString(LST);
        if (this.players.size() < 1) {
            restart();
            this.gameTimer = 0;
            return;
        }
        if(this.ending == 50) {
            boolean wtm = true;
            if(winnerTeam == GameTeam.BEASTS) wtm = false;
            for(Player player : this.players) {
                plugin.getUtils().rewardInfo(player,messagesFile.getStringList("messages.inGame.infoList.rewardSummary"),wtm);
            }
        }
        if (this.ending >= 50)
            try {
                if (winnerTeam.equals(GameTeam.RUNNERS)) {
                    for (Player pl : this.runners) {
                        firework(pl, timing(this.ending));
                    }
                } else {
                    for (Player pl : this.beasts) {
                        firework(pl, timing(this.ending));
                    }
                }
            }catch (Throwable throwable) {
                plugin.getLogs().error("Can't send fireworks effect on win");
            }
        if (this.ending == 0) {
            for (Player player : this.players) {
                if(location != null) {
                    player.teleport(location);
                }
                player.getInventory().setHelmet(null);
                player.getInventory().setChestplate(null);
                player.getInventory().setLeggings(null);
                player.getInventory().setBoots(null);
                plugin.getPlayerData(player.getUniqueId()).setStatus(PlayerStatus.IN_LOBBY);
                plugin.getPlayerData(player.getUniqueId()).setGame(null);
                plugin.getPlayerData(player.getUniqueId()).setBoard(GuardianBoard.LOBBY);
                player.getInventory().clear();
                for (ItemStack item : plugin.getLobbyItems().keySet()) {
                    player.getInventory().setItem(plugin.getSlot(item), item);
                }
                player.updateInventory();
            }
            restart();
            this.gameTimer = 0;
            return;
        }
        for (Player pl : this.players) {
            for (PotionEffect effect : pl.getActivePotionEffects())
                pl.removePotionEffect(effect.getType());
        }
        this.ending--;
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> this.timerToLobby(winnerTeam),2L);
    }
    public void firework(Player player,boolean firework) {
        if(!firework) return;
        Firework fa = player.getWorld().spawn(player.getLocation(), Firework.class);
        FireworkMeta fam = fa.getFireworkMeta();
        Random r = new Random();
        int fType = r.nextInt(4) + 1;
        FireworkEffect.Type fireworkType = null;
        switch (fType) {
            case 1:
                fireworkType = FireworkEffect.Type.STAR;
                break;
            case 2:
                fireworkType = FireworkEffect.Type.BALL;
                break;
            case 3:
                fireworkType = FireworkEffect.Type.BALL_LARGE;
                break;
            case 4:
                fireworkType = FireworkEffect.Type.CREEPER;
                break;
            case 5:
                fireworkType = FireworkEffect.Type.BURST;
                break;
        }
        int co1 = r.nextInt(10) + 1;
        int co2 = r.nextInt(10) + 1;
        Color c1 = fireColor(co1);
        Color c2 = fireColor(co2);
        FireworkEffect ef = FireworkEffect.builder().flicker(r.nextBoolean()).withColor(c1).withFade(c2).with(fireworkType).trail(r.nextBoolean()).build();
        fam.addEffect(ef);
        fam.setPower(1);
        fa.setFireworkMeta(fam);
    }
    public Color fireColor(int c) {
        switch (c) {
            default:
                return Color.YELLOW;
            case 2:
                return Color.RED;
            case 3:
                return Color.GREEN;
            case 4:
                return Color.BLUE;
            case 5:
                return Color.AQUA;
            case 6:
                return Color.OLIVE;
            case 7:
                return Color.WHITE;
            case 8:
                return Color.ORANGE;
            case 9:
                return Color.TEAL;
            case 10:
                break;
        }
        return Color.LIME;
    }
    public boolean timing(int i) {
        return i % 3 == 0;
    }
    public void leave(Player player) {
        this.players.remove(player);
        this.runners.remove(player);
        this.beasts.remove(player);
        this.spectators.remove(player);
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
        String LST = settingsFile.getString("settings.lobbyLocation");
        if(LST == null) LST = "notSet";
        if(player.isOnline()) {
            Location location = plugin.getUtils().getLocationFromString(LST);
            if (location != null) {
                player.teleport(location);
                player.setGameMode(GameMode.ADVENTURE);
            }
        }
        String quitMsg = messagesFile.getString("messages.inGame.quit");
        if(quitMsg == null) quitMsg = "&7%player% &ehas quit!";
        for(Player pl : this.players) {
            plugin.getUtils().sendMessage(pl,quitMsg.replace("%player%",player.getName())
                    .replace("%online%",this.players.size()+"")
                    .replace("%max%",this.max+""));
        }
        if(player.isOnline()) {
            plugin.getPlayerData(player.getUniqueId()).setStatus(PlayerStatus.IN_LOBBY);
            plugin.getPlayerData(player.getUniqueId()).setGame(null);
            plugin.getPlayerData(player.getUniqueId()).setBoard(GuardianBoard.LOBBY);
            player.getInventory().clear();
            for (ItemStack item : plugin.getLobbyItems().keySet()) {
                player.getInventory().setItem(plugin.getSlot(item), item);
            }
            player.updateInventory();
        }
        updateSigns();
    }
    public void restart() {
        this.players.clear();
        this.spectators.clear();
        this.beasts.clear();
        this.runners.clear();
        this.gameTimer = 0;
        this.timer = 500;
        this.min = 2;
        this.max = 10;
        this.fakeStarting = 5;
        this.beasts.clear();
        this.playingStage = false;
        this.endingStage = false;
        this.preparingStage = true;
        this.startingStage = false;
        this.selectingStage = false;
        this.inGameStage = false;
        this.gameStatus = GameStatus.PREPARING;
        this.starting = 30;
        this.ending = 150;
        loadGame();
        updateSigns();
    }
    @SuppressWarnings("unused")
    public void reload() {
        for(Player player : this.players) {
            leave(player);
        }
        restart();
        loadGame();
    }
}
