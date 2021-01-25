package dev.mruniverse.rigoxrftb.core.games;

import dev.mruniverse.rigoxrftb.core.RigoxRFTB;
import dev.mruniverse.rigoxrftb.core.enums.Files;
import dev.mruniverse.rigoxrftb.core.enums.PlayerStatus;
import dev.mruniverse.rigoxrftb.core.enums.RigoxBoard;
import dev.mruniverse.rigoxrftb.core.utils.players.PlayerManager;
import org.bukkit.*;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class Game {
    private final String gameName;
    private final String gamePath;
    public GameType gameType;
    public ArrayList<Player> players;
    public ArrayList<Location> signs;
    public ArrayList<Player> runners;
    public ArrayList<Player> beasts;
    public ArrayList<Player> spectators;

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
    private final RigoxRFTB plugin;

    public GameStatus gameStatus;
    public boolean invincible = true;
    public boolean preparingStage;
    public boolean startingStage;
    public boolean inGameStage;
    public boolean playingStage;
    public boolean endingStage;

    public Game(RigoxRFTB main, String name) {
        this.gameTimer = 0;
        this.gameFile = main.getFiles().getControl(Files.GAMES);
        this.settingsFile = main.getFiles().getControl(Files.SETTINGS);
        this.messagesFile = main.getFiles().getControl(Files.MESSAGES);
        this.plugin = main;
        this.players = new ArrayList<>();
        this.signs = new ArrayList<>();
        this.spectators = new ArrayList<>();
        this.timer = 500;
        this.min = 2;
        this.time = 0;
        this.max = 10;
        this.fakeStarting = 0;
        this.beastLocation = null;
        this.runnersLocation = null;
        this.runners = new ArrayList<>();
        this.beasts = new ArrayList<>();
        this.preparingStage = true;
        this.startingStage = false;
        this.inGameStage = false;
        this.playingStage = false;
        this.endingStage = false;
        this.starting = 30;
        this.ending = 150;
        this.times = 0;
        this.inventoryNumber = -1;
        this.gameName = name;
        this.gamePath = "games." + name + ".";
        try {
            loadGame();
        } catch (Throwable throwable) {
            RigoxRFTB.getInstance().getLogs().error("Can't load arena: " + gameName);
            RigoxRFTB.getInstance().getLogs().error(throwable);
            this.preparingStage = false;
            this.gameStatus = GameStatus.PREPARING;
        }
    }

    private void loadGame() {
        this.timer = gameFile.getInt(gamePath + "time");
        this.worldTime = gameFile.getInt(gamePath + "worldTime");
        this.max = gameFile.getInt(gamePath + "max");
        this.min = gameFile.getInt(gamePath + "min");
        this.gameType = GameType.valueOf(gameFile.getString(gamePath + "gameType"));
        String bL = gameFile.getString(gamePath + "locations.beast");
        if(bL == null) bL = "notSet";
        this.beastLocation = plugin.getUtils().getLocationFromString(bL);
        String sbL = gameFile.getString(gamePath + "locations.selected-beast");
        if(sbL == null) sbL = "notSet";
        this.selectedBeast = plugin.getUtils().getLocationFromString(sbL);
        String rL = gameFile.getString(gamePath + "locations.runners");
        if(rL == null) rL = "notSet";
        this.runnersLocation = plugin.getUtils().getLocationFromString(rL);
        String wL = gameFile.getString(gamePath + "locations.waiting");
        if(wL == null) wL = "notSet";
        this.waiting = plugin.getUtils().getLocationFromString(wL);
        if (this.beastLocation == null || this.runnersLocation == null || this.selectedBeast == null || this.waiting == null) {
            this.preparingStage = false;
            this.gameStatus = GameStatus.PREPARING;
        }
        this.gameStatus = GameStatus.WAITING;
        loadSigns();
        try {
            this.gameSound1 = Sound.valueOf(gameFile.getString(gamePath + "gameSound1"));
            this.gameSound2 = Sound.valueOf(gameFile.getString(gamePath + "gameSound2"));
            this.gameSound3 = Sound.valueOf(gameFile.getString(gamePath + "gameSound3"));
        }catch (Throwable throwable) {
            plugin.getLogs().error("Can't load game Sounds! Please verify if the sound works in your current version!");
        }
    }

    public void loadSigns() {
        this.signs.clear();
        for(String signs : gameFile.getStringList(gamePath + "signs")) {
            Location signLocation = plugin.getUtils().getLocationFromString(signs);
            if(signLocation != null) {
                if (signLocation.getBlock().getState() instanceof Sign) {
                    this.signs.add(signLocation);
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
        for(Location signLocation : this.signs) {
            if(signLocation.getBlock().getState() instanceof Sign) {
                Sign currentSign = (Sign)signLocation.getBlock().getState();
                currentSign.setLine(0,replaceGameVariable(line1));
                currentSign.setLine(1,replaceGameVariable(line2));
                currentSign.setLine(2,replaceGameVariable(line3));
                currentSign.setLine(3,replaceGameVariable(line4));
                currentSign.update();
            }
        }
    }

    public void deathBeast(Player beast) {
        this.beasts.remove(beast);
        if(beasts.size() == 0) {
            winRunners();
        }
    }
    public void deathRunner(Player runner) {
        this.runners.remove(runner);
        if(runners.size() == 0) {
            winBeasts();
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
        if((min - this.players.size()) <= 0 && this.gameStatus.equals(GameStatus.WAITING)) {
            this.gameStatus = GameStatus.STARTING;
            for(Player player : this.players) {
                PlayerManager playerData = plugin.getPlayerData(player.getUniqueId());
                playerData.setBoard(RigoxBoard.STARTING);
            }
        }
        if(this.players.size() < min) {
            return (min - this.players.size());
        }
        if(this.gameStatus.equals(GameStatus.WAITING)) {
            this.gameStatus = GameStatus.STARTING;
            for(Player player : this.players) {
                PlayerManager playerData = plugin.getPlayerData(player.getUniqueId());
                playerData.setBoard(RigoxBoard.STARTING);
            }
        }
        return 0;
    }
    public String getName() {
        return gameName;
    }

    public void join(Player player) {
        if(plugin.getPlayerData(player.getUniqueId()).getGame() != null) {
            plugin.getUtils().sendMessage(player, messagesFile.getString("messages.inGame.already"));
            return;
        }
        if(!this.gameStatus.equals(GameStatus.WAITING) && !this.gameStatus.equals(GameStatus.STARTING)) {
            if(!this.gameStatus.equals(GameStatus.RESTARTING)) {
                plugin.getUtils().sendMessage(player, messagesFile.getString("messages.others.gamePlaying"));
                return;
            }
            plugin.getUtils().sendMessage(player, messagesFile.getString("messages.others.restarting"));
            return;
        }
        if(this.players.size() >= max) {
            plugin.getUtils().sendMessage(player, messagesFile.getString("messages.others.full"));
            return;
        }
        player.getInventory().clear();
        player.setGameMode(GameMode.ADVENTURE);
        player.teleport(this.waiting);
        this.players.add(player);
        this.runners.add(player);
        plugin.getPlayerData(player.getUniqueId()).setGame(this);
        plugin.getPlayerData(player.getUniqueId()).setBoard(RigoxBoard.WAITING);
        plugin.getPlayerData(player.getUniqueId()).setStatus(PlayerStatus.IN_GAME);
        checkPlayers();
        player.setGameMode(GameMode.ADVENTURE);
        player.setFlying(false);
        player.setAllowFlight(false);
        player.setHealth(20.0D);
        player.setFireTicks(0);
        player.getInventory().setItem(plugin.RunnerSlot,plugin.kitRunner);
        player.getInventory().setItem(plugin.exitSlot,plugin.exitItem);
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        String joinMsg = messagesFile.getString("messages.inGame.join");
        if(joinMsg == null) joinMsg = "&7%player% &ehas joined &e(&b%online%&e/&b%max%&e)!";
        for(Player pl : this.players) {
            plugin.getUtils().sendMessage(pl,joinMsg.replace("%player%",player.getName())
            .replace("%online%",this.players.size()+"")
            .replace("%max%",this.max+""));
        }
        updateSigns();
        player.updateInventory();
    }
    public void checkPlayers() {
        this.endingStage = false;
        if (this.players.size() == this.min && !this.startingStage && !gameStatus.equals(GameStatus.STARTING)) {
            this.gameStatus = GameStatus.STARTING;
            this.startingStage = true;
            this.gameTimer = 1;
            for(Player runner : this.runners) {
                plugin.getPlayerData(runner.getUniqueId()).setBoard(RigoxBoard.SELECTING);
            }
            for(Player beast : this.beasts) {
                plugin.getPlayerData(beast.getUniqueId()).setBoard(RigoxBoard.SELECTING);
            }
        }
    }
    public void gameCount(GameCountType gameCountType) {
        try {
            if (gameCountType.equals(GameCountType.START_COUNT)) {
                if (this.starting < -25) {
                    this.gameTimer = 2;
                    return;
                }
                if (this.starting <= 5 && this.starting >= 0)
                    if (gameSound1 != null) {
                        for (Player members : this.players) {
                            members.playSound(members.getLocation(), gameSound1, 1.0F, 0.5F);
                        }
                    }
                if (this.starting <= -20 && this.starting >= -25) {
                    if (gameSound2 != null) {
                        for (Player runner : this.runners) {
                            runner.playSound(runner.getLocation(), gameSound2, 1.0F, 0.8F);
                        }
                    }
                    if (gameSound1 != null) {
                        for (Player beast : this.beasts) {
                            beast.playSound(beast.getLocation(), gameSound1, 1.0F, 0.5F);
                        }
                    }
                }
                if(this.starting == -24 || this.starting == -23 || this.starting == -22 || this.starting == -21 || this.starting == -20) {
                    for(Player player : this.players) {
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
                if (this.starting == -25) {
                    this.starting--;
                    this.invincible = false;
                    this.gameTimer = 2;
                    if (this.beasts.size() <= 0) {
                        winRunners();
                        return;
                    }
                    for (Player beast : this.beasts) {
                        beast.teleport(this.beastLocation);
                        beast.setFoodLevel(20);
                        beast.setHealth(20.0D);
                        beast.setExp(0.0F);
                        beast.setLevel(0);
                        beast.setFireTicks(0);
                        plugin.getPlayerData(beast.getUniqueId()).setBoard(RigoxBoard.PLAYING);
                        plugin.getUtils().sendList(beast,messagesFile.getStringList("messages.inGame.infoList.startInfo"));
                        plugin.getUtils().sendTitle(beast, 0, 20, 10, messagesFile.getString("messages.inGame.others.titles.runnersGo.toBeasts.title"), messagesFile.getString("messages.inGame.others.titles.runnersGo.toBeasts.subtitle"));
                    }
                    if (gameSound3 != null) {
                        for (Player runner : this.players)
                            runner.playSound(runner.getLocation(), gameSound3, 10.0F, 1.0F);
                    }
                }
                if (this.runners.size() < 1) {
                    if (!this.inGameStage) {
                        this.startingStage = false;
                        this.gameStatus = GameStatus.WAITING;
                        this.starting = 30;
                        for (Player player : this.players) {
                            player.teleport(waiting);
                            player.getInventory().clear();
                            player.getInventory().setArmorContents(null);
                            this.beasts = new ArrayList<>();
                            if (!this.runners.contains(player))
                                this.runners.add(player);
                            plugin.getPlayerData(player.getUniqueId()).setBoard(RigoxBoard.WAITING);
                            plugin.getUtils().sendMessage(player, messagesFile.getString("messages.inGame.cantStartGame"));
                        }
                        this.gameTimer = 0;
                        return;
                    }
                    if (!this.playingStage) {
                        for (Player player : this.players) {
                            leave(player);
                            plugin.getUtils().sendMessage(player, messagesFile.getString("messages.inGame.cantStartGame"));
                        }
                        restart();
                    } else {
                        if (this.beasts.size() == 0) {
                            winRunners();
                            this.gameTimer = 0;
                            return;
                        }
                        winBeasts();
                        this.gameTimer = 0;
                        return;
                    }
                }
                if (this.starting == -10) {
                    this.playingStage = true;
                    this.gameStatus = GameStatus.PLAYING;
                    for (Player runner : this.runners) {
                        runner.teleport(runnersLocation);
                        plugin.getPlayerData(runner.getUniqueId()).setBoard(RigoxBoard.PLAYING);
                        plugin.getUtils().sendList(runner,messagesFile.getStringList("messages.inGame.infoList.startInfo"));
                        plugin.getUtils().sendTitle(runner, 0, 20, 10, messagesFile.getString("messages.inGame.others.titles.runnersGo.toRunners.title"), messagesFile.getString("messages.inGame.others.titles.runnersGo.toRunners.subtitle"));
                    }
                    for(Player beast : this.beasts) {
                        plugin.getPlayerData(beast.getUniqueId()).setBoard(RigoxBoard.BEAST_SPAWN);
                    }
                }
                if (this.starting == 0) {
                    this.inGameStage = true;
                    this.gameStatus = GameStatus.IN_GAME;
                    Objects.requireNonNull(this.runnersLocation.getWorld()).setTime(this.worldTime);
                    this.runnersLocation.getWorld().setThundering(false);
                    this.runnersLocation.getWorld().setStorm(false);
                    this.runnersLocation.getWorld().setSpawnFlags(false, false);
                    this.endingStage = false;
                    for (Player player : this.players) {
                        plugin.getPlayerData(player.getUniqueId()).setBoard(RigoxBoard.PLAYING);
                        plugin.getUtils().sendTitle(player, 5, 40, 5, messagesFile.getString("messages.inGame.others.titles.gameStart.title"), messagesFile.getString("messages.inGame.others.titles.gameStart.subtitle"));
                    }

                }
                if (this.starting == -2)
                    selectBeast();
                if (this.starting == -2)
                    for (Player runner : this.runners) {
                        runner.getInventory().clear();
                        runner.getInventory().setArmorContents(null);
                    }
                if (this.starting == 30) {
                    this.endingStage = false;
                    Objects.requireNonNull(waiting.getWorld()).setTime(this.time);
                    waiting.getWorld().setThundering(false);
                    waiting.getWorld().setStorm(false);
                    waiting.getWorld().setDifficulty(Difficulty.PEACEFUL);
                    waiting.getWorld().setSpawnFlags(false, false);
                    for (Player player : this.players) {
                        player.closeInventory();
                        player.setHealth(20.0D);
                    }
                }
                if(this.starting >= 1) {
                    if(this.starting > 1) {
                        String startMsg = messagesFile.getString("messages.inGame.selectingBeast");
                        String seconds = messagesFile.getString("times.seconds");
                        if(startMsg == null) startMsg = "&eThe beast will be selected in &c%time% &e%seconds%!";
                        if(seconds == null) seconds = "seconds";
                        if(this.starting == 15 || this.starting == 10 || this.starting == 5 || this.starting == 4 || this.starting == 3 || this.starting == 2) {
                            for(Player player : this.players) {
                                plugin.getUtils().sendMessage(player,startMsg.replace("%time%",starting+"").replace("%seconds%",seconds));
                            }
                        }
                    }  else {
                        String startMsg = messagesFile.getString("messages.inGame.selectingBeast");
                        String second = messagesFile.getString("times.second");
                        if(startMsg == null) startMsg = "&eThe beast will be selected in &c%time% &e%seconds%!";
                        if(second == null) second = "second";
                        for(Player player : this.players) {
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
                        for(Player player : this.players) {
                            plugin.getUtils().sendMessage(player,startMsg.replace("%time%",perfectTime+"").replace("%seconds%",seconds));
                        }
                    } else if(this.starting == -9){
                        String startMsg = messagesFile.getString("messages.inGame.starting");
                        String second = messagesFile.getString("times.second");
                        if(startMsg == null) startMsg = "&eThe game starts in &c%time% &e%seconds%!";
                        if(second == null) second = "second";
                        for(Player player : this.players) {
                            plugin.getUtils().sendMessage(player,startMsg.replace("%time%",1+"").replace("%seconds%",second));
                        }
                    }
                }
                this.starting--;
                updateSigns();
                return;
            }
            updateSigns();
            if(this.beasts.size() == 0) {
                if(!this.endingStage && !gameStatus.equals(GameStatus.RESTARTING)) {
                    winRunners();
                }
                return;
            }
            if(this.runners.size() == 0) {
                if(!this.endingStage && !gameStatus.equals(GameStatus.RESTARTING)) {
                    winBeasts();
                }
                return;
            }
            if (this.timer == 0) {
                winRunners();
                return;
            }
            this.timer--;
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
            this.runners.remove(nextBeast);
            for (Player announce : this.players) {
                plugin.getUtils().sendMessage(announce, chosenBeast.replace("%player%", nextBeast.getName()));
            }
            nextBeast.getInventory().clear();
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
        NextBeast.getInventory().setItem(plugin.beastSlot,plugin.kitBeast);
        for(Player announce : this.players) {
            plugin.getUtils().sendMessage(announce,chosenBeast.replace("%player%",NextBeast.getName()));
        }
        NextBeast.getInventory().setItem(plugin.exitSlot,plugin.exitItem);
        NextBeast.updateInventory();
        plugin.getItems(GameEquip.BEAST_KIT,NextBeast);
        nextBeast.teleport(selectedBeast);
        changeToStartingBoard();
        this.runners.remove(nextBeast);
        this.times = 0;

    }
    public void changeToStartingBoard() {
        for(Player runner : this.runners) {
            plugin.getPlayerData(runner.getUniqueId()).setBoard(RigoxBoard.STARTING);
        }
        for(Player beast : this.beasts) {
            plugin.getPlayerData(beast.getUniqueId()).setBoard(RigoxBoard.STARTING);
        }
    }
    public void winRunners() {
        this.invincible = true;
        this.gameTimer = 0;
        this.endingStage = true;
        this.gameStatus = GameStatus.RESTARTING;
        try {
            if (this.runners != null) {
                if(this.runners.size() != 0) {
                    for (Player runner : this.runners) {
                        leave(runner);
                        runner.playSound(runner.getLocation(), gameSound3, 3.0F, 1.0F);
                    }
                }
            }
            if (this.spectators != null) {
                if(this.spectators.size() != 0) {
                    for (Player spectator : this.spectators) {
                        leave(spectator);
                        spectator.playSound(spectator.getLocation(), gameSound3, 3.0F, 1.0F);
                    }
                }
            }
            if (this.beasts != null) {
                if(this.beasts.size() != 0) {
                    for (Player beast : this.beasts) {
                        leave(beast);
                        beast.playSound(beast.getLocation(), gameSound1, 3.0F, 1.0F);
                    }
                }
            }
            if (this.players != null) {
                for (Player player : this.players) {
                    leave(player);
                    player.playSound(player.getLocation(), gameSound3, 3.0F, 1.0F);
                }
            }
            restart();
        }catch (Throwable throwable) {
            plugin.getLogs().error("Can't leave players");
            plugin.getLogs().error(throwable);
            restart();
        }
    }
    public void winBeasts() {
        this.invincible = true;
        this.gameTimer = 0;
        this.endingStage = true;
        this.gameStatus = GameStatus.RESTARTING;
        try {
            if (this.runners != null) {
                if(this.runners.size() != 0) {
                    for (Player runner : this.runners) {
                        leave(runner);
                        runner.playSound(runner.getLocation(), gameSound3, 3.0F, 1.0F);
                    }
                }
            }
            if (this.spectators != null) {
                if(this.spectators.size() != 0) {
                    for (Player spectator : this.spectators) {
                        leave(spectator);
                        spectator.playSound(spectator.getLocation(), gameSound3, 3.0F, 1.0F);
                    }
                }
            }
            if (this.beasts != null) {
                if(this.beasts.size() != 0) {
                    for (Player beast : this.beasts) {
                        leave(beast);
                        beast.playSound(beast.getLocation(), gameSound1, 3.0F, 1.0F);
                    }
                }
            }
            if (this.players != null) {
                for (Player player : this.players) {
                    leave(player);
                    player.playSound(player.getLocation(), gameSound3, 3.0F, 1.0F);
                }
            }
            restart();
        }catch (Throwable throwable) {
            plugin.getLogs().error("Can't leave players");
            plugin.getLogs().error(throwable);
            restart();
        }
    }
    public void leave(Player player) {
        this.players.remove(player);
        this.runners.remove(player);
        this.beasts.remove(player);
        this.spectators.remove(player);
        String LST = settingsFile.getString("settings.lobbyLocation");
        if(LST == null) LST = "notSet";
        Location location = plugin.getUtils().getLocationFromString(LST);
        if(location != null) {
            player.teleport(location);
            player.setGameMode(GameMode.ADVENTURE);
        }
        String quitMsg = messagesFile.getString("messages.inGame.quit");
        if(quitMsg == null) quitMsg = "&7%player% &ehas quit!";
        for(Player pl : this.players) {
            plugin.getUtils().sendMessage(pl,quitMsg.replace("%player%",player.getName())
                    .replace("%online%",this.players.size()+"")
                    .replace("%max%",this.max+""));
        }
        plugin.getPlayerData(player.getUniqueId()).setStatus(PlayerStatus.IN_LOBBY);
        plugin.getPlayerData(player.getUniqueId()).setGame(null);
        plugin.getPlayerData(player.getUniqueId()).setBoard(RigoxBoard.LOBBY);
        player.getInventory().clear();
        for(ItemStack item : plugin.getLobbyItems().keySet()) {
            player.getInventory().setItem(plugin.getSlot(item),item);
        }
        player.updateInventory();
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
        this.fakeStarting = 0;
        this.beasts.clear();
        this.playingStage = false;
        this.endingStage = false;
        this.preparingStage = true;
        this.startingStage = false;
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
