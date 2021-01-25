package dev.mruniverse.rigoxrftb.core.files;

import dev.mruniverse.rigoxrftb.core.enums.Files;
import dev.mruniverse.rigoxrftb.core.RigoxRFTB;
import dev.mruniverse.rigoxrftb.core.enums.SaveMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private final RigoxRFTB plugin;
    private FileConfiguration rGames,rMySQL,rMenus,rItems,rMessages,rScoreboard,rSettings;
    private final File dataFolder;
    private File Games;
    private File MySQL;
    private File Menus;
    private File Items;
    private File Messages;
    private File Scoreboard;
    private File Settings;
    public FileManager(RigoxRFTB main) {
        plugin = main;
        dataFolder = main.getDataFolder();
    }
    public void loadFiles() {
        loadFolder(dataFolder,"Main Folder");
        Games = new File(dataFolder,"games.yml");
        MySQL = new File(dataFolder,"mysql.yml");
        Menus = new File(dataFolder, "menus.yml");
        Items = new File(dataFolder, "items.yml");
        Scoreboard = new File(dataFolder, "scoreboards.yml");
        Messages = new File(dataFolder,"messages.yml");
        Settings = new File(dataFolder,"settings.yml");
        loadFile(Games,"games.yml");
        loadFile(MySQL,"mysql.yml");
        loadFile(Menus,"menus.yml");
        loadFile(Items,"items.yml");
        loadFile(Scoreboard,"scoreboards.yml");
        loadFile(Settings,"settings.yml");
        loadFile(Messages,"messages.yml");
    }
    private void loadFile(File fileToLoad,String fileName) {
        boolean result = false;
        if(!fileToLoad.exists()) {
            try {
                result = fileToLoad.createNewFile();
            } catch (Throwable throwable) {
                plugin.getLogs().error("The plugin can't load or save configuration files!");
                plugin.getLogs().error(throwable);
            }
            if(result) {
                plugin.getLogs().info("File: &b" + fileName + "&f created!");
            }
        }
    }
    public void loadFolder(File folderToLoad, String folderName) {
        boolean result = false;
        if(!folderToLoad.exists()) result = folderToLoad.mkdir();
        if(result) {
            plugin.getLogs().info("Folder: &b" + folderName + "&f created!");
        }
    }
    public void loadConfiguration() {
        addConfig(Files.SETTINGS,"settings.update-check",true);
        addConfig(Files.SETTINGS,"settings.maxTime",500);
        addConfig(Files.SETTINGS,"settings.lobbyLocation","notSet");
        addConfig(Files.SETTINGS,"settings.defaultSounds.sound1","BLOCK_NOTE_BLOCK_HARP");
        addConfig(Files.SETTINGS,"settings.defaultSounds.sound2","ENTITY_ENDER_DRAGON_GROWL");
        addConfig(Files.SETTINGS,"settings.defaultSounds.sound3","ENTITY_EXPERIENCE_ORB_PICKUP");
        addConfig(Files.SETTINGS,"settings.options.joinLobbyTeleport",false);
        addConfig(Files.SETTINGS,"settings.options.joinHeal",true);
        addConfig(Files.SETTINGS,"settings.options.joinAdventureGamemode",true);
        addConfig(Files.SETTINGS,"settings.options.clearInventory-onJoin",true);
        addConfig(Files.SETTINGS,"settings.options.hideServerJoinMessage",true);
        addConfig(Files.SETTINGS,"settings.options.hideServerQuitMessage",true);
        addConfig(Files.SETTINGS,"settings.options.lobby-bossBar",true);
        addConfig(Files.SETTINGS,"settings.options.lobby-noDamage",true);
        addConfig(Files.SETTINGS,"settings.options.lobby-noHunger",true);
        addConfig(Files.SETTINGS,"settings.options.lobby-actionBar",true);
        addConfig(Files.SETTINGS,"settings.tags.runners.toggle",true);
        addConfig(Files.SETTINGS,"settings.tags.runners.tag","&b&lRUNNER");
        addConfig(Files.SETTINGS,"settings.tags.beasts.toggle",true);
        addConfig(Files.SETTINGS,"settings.tags.beasts.tag","&c&lBEAST");
        addConfig(Files.SETTINGS,"settings.dateFormat","dd/MM/yyyy");
        addConfig(Files.SETTINGS,"settings.lobbyScoreboard-only-in-lobby-world",true);
        addConfig(Files.SETTINGS,"settings.options.pluginChat",true);
        addConfig(Files.SETTINGS,"settings.options.PerWorldTab",true);
        addConfig(Files.SETTINGS,"settings.options.PerWorldChat",true);
        addConfig(Files.SETTINGS,"settings.pointSystem.onRunnerDeath",-4);
        addConfig(Files.SETTINGS,"settings.pointSystem.onBeastDeath",-4);
        addConfig(Files.SETTINGS,"settings.pointSystem.onRunnersWin",8);
        addConfig(Files.SETTINGS,"settings.pointSystem.onBeastWin",4);
        addConfig(Files.SETTINGS,"settings.pointSystem.onKillBeast",4);
        addConfig(Files.SETTINGS,"settings.pointSystem.onBeastKill",1);
        addConfig(Files.SETTINGS,"settings.signs.line1","&l%arena%");
        addConfig(Files.SETTINGS,"settings.signs.line2","%gameStatus%");
        addConfig(Files.SETTINGS,"settings.signs.line3","%on%/%max%");
        addConfig(Files.SETTINGS,"settings.signs.line4","&nClick to join");
        addConfig(Files.SETTINGS,"settings.gameStatus.preparing","&5Config");
        addConfig(Files.SETTINGS,"settings.gameStatus.waiting","&aWaiting");
        addConfig(Files.SETTINGS,"settings.gameStatus.starting","&eStarting");
        addConfig(Files.SETTINGS,"settings.gameStatus.playing","&cPlaying");
        addConfig(Files.SETTINGS,"settings.gameStatus.InGame","&4InGame");
        addConfig(Files.SETTINGS,"settings.gameStatus.ending","&9Restarting");
        addConfig(Files.SETTINGS,"roles.beast","Beast");
        addConfig(Files.SETTINGS,"roles.runner","Runner");
        addConfig(Files.SETTINGS,"times.minutes","minutes");
        addConfig(Files.SETTINGS,"times.minute","minute");
        addConfig(Files.SETTINGS,"times.seconds","seconds");
        addConfig(Files.SETTINGS,"times.second","second");
        List<String> lore = new ArrayList<>();
        lore.add("&7Here your lore");
        lore.add("&erigox.club");
        addConfig(Files.MYSQL,"mysql.enabled",true);
        addConfig(Files.MYSQL,"mysql.host","localhost");
        addConfig(Files.MYSQL,"mysql.port",3306);
        addConfig(Files.MYSQL,"mysql.username","root");
        addConfig(Files.MYSQL,"mysql.password","password");
        addConfig(Files.MYSQL,"mysql.database","rRFTB");
        addConfig(Files.MYSQL,"mysql.table","rData");
        addConfig(Files.ITEMS,"lobby.gameSelector.toggle",true);
        addConfig(Files.ITEMS,"lobby.gameSelector.item","PAPER");
        addConfig(Files.ITEMS,"lobby.gameSelector.name","&aGame Selector");
        addConfig(Files.ITEMS,"lobby.gameSelector.lore",lore);
        addConfig(Files.ITEMS,"lobby.gameSelector.slot",0);
        addConfig(Files.ITEMS,"lobby.Shop.toggle",true);
        addConfig(Files.ITEMS,"lobby.Shop.item","EMERALD");
        addConfig(Files.ITEMS,"lobby.Shop.name","&aRFTB Shop");
        addConfig(Files.ITEMS,"lobby.Shop.lore",lore);
        addConfig(Files.ITEMS,"lobby.Shop.slot",4);
        addConfig(Files.ITEMS,"lobby.PlayerSettings.toggle",true);
        addConfig(Files.ITEMS,"lobby.PlayerSettings.item","FIREWORK");
        addConfig(Files.ITEMS,"lobby.PlayerSettings.name","&aYour settings");
        addConfig(Files.ITEMS,"lobby.PlayerSettings.lore",lore);
        addConfig(Files.ITEMS,"lobby.PlayerSettings.slot",1);
        addConfig(Files.ITEMS,"lobby.LobbySelector.toggle",true);
        addConfig(Files.ITEMS,"lobby.LobbySelector.item","BEACON");
        addConfig(Files.ITEMS,"lobby.LobbySelector.name","&aLobby Selector");
        addConfig(Files.ITEMS,"lobby.LobbySelector.lore",lore);
        addConfig(Files.ITEMS,"lobby.LobbySelector.slot",7);
        addConfig(Files.ITEMS,"lobby.Exit.toggle",true);
        addConfig(Files.ITEMS,"lobby.Exit.item","BED");
        addConfig(Files.ITEMS,"lobby.Exit.name","&aGo to the hub");
        addConfig(Files.ITEMS,"lobby.Exit.lore",lore);
        addConfig(Files.ITEMS,"lobby.Exit.slot",8);
        addConfig(Files.ITEMS,"InGame.RunnerKit.item","MAP");
        addConfig(Files.ITEMS,"InGame.RunnerKit.name","&aKit Selector (&lRunners&a)");
        addConfig(Files.ITEMS,"InGame.RunnerKit.lore",lore);
        addConfig(Files.ITEMS,"InGame.RunnerKit.slot",0);
        addConfig(Files.ITEMS,"InGame.BeastKit.item","MAP");
        addConfig(Files.ITEMS,"InGame.BeastKit.name","&aKit Selector (&lBeast&a)");
        addConfig(Files.ITEMS,"InGame.BeastKit.lore",lore);
        addConfig(Files.ITEMS,"InGame.BeastKit.slot",0);
        addConfig(Files.ITEMS,"InGame.Exit.item","BED");
        addConfig(Files.ITEMS,"InGame.Exit.name","&aLeave");
        addConfig(Files.ITEMS,"InGame.Exit.lore",lore);
        addConfig(Files.ITEMS,"InGame.Exit.slot",8);
        List<String> lists = new ArrayList<>();
        lists.add("/leave");
        lists.add("/quit");
        lists.add("/salir");
        addConfig(Files.SETTINGS,"settings.leaveCMDs",lists);
        lists = new ArrayList<>();
        addConfig(Files.SCOREBOARD,"scoreboards.lobby.toggle",true);
        addConfig(Files.SCOREBOARD,"scoreboards.lobby.title","&e&lRigoxRFTB");
        lists.add(" ");
        lists.add("&fUser: &b<player_name>");
        lists.add("&fCoins: &b<player_coins>");
        lists.add("&fWins: &b<player_wins>");
        lists.add(" ");
        lists.add("&fBeast Selected Kit:");
        lists.add("&b<player_beast_kit>");
        lists.add("&fRunner Selected Kit:");
        lists.add("&b<player_runner_kit>");
        lists.add(" ");
        lists.add("&fOnline: &b<server_online>");
        lists.add(" ");
        lists.add("&erigox.club");
        addConfig(Files.SCOREBOARD,"scoreboards.lobby.lines",lists);
        lists = new ArrayList<>();
        addConfig(Files.SCOREBOARD,"scoreboards.waiting.title","&e&lRigoxRFTB");
        lists.add("&7<arena_mode> | <timeFormat>");
        lists.add(" ");
        lists.add("&fMap: &a<arena_name>");
        lists.add("&fPlayers: &a<arena_online>/<arena_max>");
        lists.add(" ");
        lists.add("<isWaiting>&fWaiting for");
        lists.add("<isWaiting>&a<arena_need> &fplayers to start");
        lists.add("<isSelecting>&fSelecting beast in &a<arena_time_number>");
        lists.add("<isStarting>&fStarting in <arena_time_number> &f<arena_time_text>.");
        lists.add(" ");
        lists.add("&erigox.club");
        addConfig(Files.SCOREBOARD,"scoreboards.waiting.lines",lists);
        lists = new ArrayList<>();
        addConfig(Files.SCOREBOARD,"scoreboards.playing.title","&e&lRigoxRFTB");
        lists.add("&7<timeFormat>");
        lists.add(" ");
        lists.add("&fRunners Left: &a<arena_runners>");
        lists.add("&fTime Left: &a<arena_timeLeft>");
        lists.add(" ");
        lists.add("&fMap: &a<arena_name>");
        lists.add("&fBeast(s): &a<arena_beast>");
        lists.add("&fYour role: &a<player_role>");
        lists.add(" ");
        lists.add("&erigox.club");
        addConfig(Files.SCOREBOARD,"scoreboards.playing.lines",lists);
        lists = new ArrayList<>();
        addConfig(Files.SCOREBOARD,"scoreboards.beastWin.forBeast.title","&e&lRigoxRFTB");
        lists.add("&7<timeFormat>");
        lists.add(" ");
        lists.add("&fYou are the best beast");
        lists.add("&aWell played&f!");
        lists.add(" ");
        lists.add("&a+<beast_coins_win> &fcoins &bxWin");
        lists.add("&a+<beast_coins_kill> &fcoins &bxKill");
        lists.add(" ");
        lists.add("&erigox.club");
        addConfig(Files.SCOREBOARD,"scoreboards.beastWin.forBeast.lines",lists);
        lists = new ArrayList<>();
        addConfig(Files.SCOREBOARD,"scoreboards.beastWin.forRunners.title","&e&lRigoxRFTB");
        lists.add("&7<timeFormat>");
        lists.add(" ");
        lists.add("&fWell.. you tried");
        lists.add(" ");
        lists.add("&c<runners_coins_death> &fcoins");
        lists.add(" ");
        lists.add("&erigox.club");
        addConfig(Files.SCOREBOARD,"scoreboards.beastWin.forRunners.lines",lists);
        lists = new ArrayList<>();
        addConfig(Files.SCOREBOARD,"scoreboards.runnersWin.forBeast.title","&e&lRigoxRFTB");
        lists.add("&7<timeFormat>");
        lists.add(" ");
        lists.add("&fBad played!");
        lists.add("&cGood luck for next game&f!");
        lists.add(" ");
        lists.add("&c<beast_coins_death> &fcoins");
        lists.add("&a+<beast_coins_kill> &fcoins &bxKill");
        lists.add(" ");
        lists.add("&erigox.club");
        addConfig(Files.SCOREBOARD,"scoreboards.runnersWin.forBeast.lines",lists);
        lists = new ArrayList<>();
        addConfig(Files.SCOREBOARD,"scoreboards.runnersWin.forRunners.title","&e&lRigoxRFTB");
        lists.add("&7<timeFormat>");
        lists.add(" ");
        lists.add("&fRunners Win!");
        lists.add("&aWell played!");
        lists.add(" ");
        lists.add("&a+<runners_coins_win> &fcoins &bxWin");
        lists.add("<isPlayerKiller>&a+<runners_coins_kill> &fcoins only for you.");
        lists.add(" ");
        lists.add("&erigox.club");
        addConfig(Files.SCOREBOARD,"scoreboards.runnersWin.forRunners.lines",lists);
        lists = new ArrayList<>();
        addConfig(Files.MESSAGES,"messages.inGame.join","&7%player% &ehas joined &e(&b%online%&e/&b%max%&e)!");
        addConfig(Files.MESSAGES,"messages.inGame.quit","&7%player% &ehas quit!");
        addConfig(Files.MESSAGES,"messages.inGame.already","&cYou're already in an arena!");
        addConfig(Files.MESSAGES,"messages.inGame.selectingBeast","&eThe beast will be selected in &c%time% &e%seconds%!");
        addConfig(Files.MESSAGES,"messages.inGame.starting","&eThe game starts in &c%time% &e%seconds%!");
        addConfig(Files.MESSAGES,"messages.inGame.beastsAppear","&eThe beasts spawns in &c%time% &e%seconds%!");
        addConfig(Files.MESSAGES,"messages.inGame.chosenBeast","&eThe player &b%player% &enow is a beast!");
        lists.add("&a[bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx]");
        lists.add("<center>&e&lMode: %gameType% - %map_name%");
        lists.add(" ");
        lists.add("<isBeast>&e&lKill all runners!");
        lists.add("<isRunner>&e&lSurvive and Kill the beast!");
        lists.add(" ");
        lists.add("&a[bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx]");
        addConfig(Files.MESSAGES,"messages.inGame.infoList.startInfo",lists);
        lists = new ArrayList<>();
        lists.add("&a[bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx]");
        lists.add("<center>&e&l%gameType% - %map_name%");
        lists.add(" ");
        lists.add("<center>&7%winner_team% &e&lWINNER &7%looser_team%");
        lists.add(" ");
        lists.add("&a[bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx]");
        addConfig(Files.MESSAGES,"messages.inGame.infoList.endInfo",lists);
        lists = new ArrayList<>();
        lists.add("&a[bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx]");
        lists.add("<center>&e&l%gameType% - %map_name%");
        lists.add(" ");
        lists.add(" &7You earned:");
        lists.add("  &f[px] &3%game% Game Experience");
        lists.add("  &f<isInGuild>[px] &2%guild% Guild Experience");
        lists.add(" ");
        lists.add("&a[bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx]");
        addConfig(Files.MESSAGES,"messages.inGame.infoList.rewardSummary",lists);
        addConfig(Files.MESSAGES,"messages.inGame.deathMessages.pvp","&7%victim% was killed by %attacker%");
        addConfig(Files.MESSAGES,"messages.inGame.deathMessages.void","&7%victim% was searching a diamond.");
        addConfig(Files.MESSAGES,"messages.inGame.deathMessages.lava","&7%victim% was on fire!");
        addConfig(Files.MESSAGES,"messages.inGame.deathMessages.bow","&7%attacker% is the best with the bow vs %victim%");
        addConfig(Files.MESSAGES,"messages.inGame.deathMessages.otherCause","&7%victim% died");
        addConfig(Files.MESSAGES,"messages.inGame.cantStartGame","&cThis game can't start, not enough players");
        addConfig(Files.MESSAGES,"messages.inGame.others.winCoins","&6+%winCoins% coins (Win)!");
        addConfig(Files.MESSAGES,"messages.inGame.others.playAgainWin","&a&lYOU WON! &e&lWant to play again? <clickText>");
        addConfig(Files.MESSAGES,"messages.inGame.others.playAgainLoose","&c&lYOU LOOSE! &e&lWant to play again? <clickText>");
        addConfig(Files.MESSAGES,"messages.inGame.others.playAgainClickText","&6&lCLICK HERE");
        addConfig(Files.MESSAGES,"messages.inGame.others.titles.gameStart.title","&a");
        addConfig(Files.MESSAGES,"messages.inGame.others.titles.gameStart.subtitle","&aGame Started!");
        addConfig(Files.MESSAGES,"messages.inGame.others.titles.runnersGo.toRunners.title","&a");
        addConfig(Files.MESSAGES,"messages.inGame.others.titles.runnersGo.toRunners.subtitle","&aYou has been released, GO!");
        addConfig(Files.MESSAGES,"messages.inGame.others.titles.runnersGo.toBeasts.title","&a");
        addConfig(Files.MESSAGES,"messages.inGame.others.titles.runnersGo.toBeasts.subtitle","&aThe runners has been released!");
        addConfig(Files.MESSAGES,"messages.inGame.others.titles.beastsGo.toRunners.title","&a");
        addConfig(Files.MESSAGES,"messages.inGame.others.titles.beastsGo.toRunners.subtitle","&cThe beasts has been released!");
        addConfig(Files.MESSAGES,"messages.inGame.others.titles.beastsGo.toBeasts.title","&a");
        addConfig(Files.MESSAGES,"messages.inGame.others.titles.beastsGo.toBeasts.subtitle","&aYou has been released, GO!");
        addConfig(Files.MESSAGES,"messages.inGame.others.titles.Winner.title","&6&lVICTORY!");
        addConfig(Files.MESSAGES,"messages.inGame.others.titles.Winner.subtitle","&b%winner_name%&f won the game!");
        addConfig(Files.MESSAGES,"messages.inGame.others.titles.Looser.title","&c&lGAME OVER!");
        addConfig(Files.MESSAGES,"messages.inGame.others.titles.Looser.subtitle","&8%attacker_name%&f kill you!");
        addConfig(Files.MESSAGES,"messages.inGame.others.bossBar.toRunners","&bDistance between you and &e&lBEAST &f%beastName%");
        addConfig(Files.MESSAGES,"messages.inGame.others.bossBar.toBeasts","&bRunners with life: &e&l%runners%");
        addConfig(Files.MESSAGES,"messages.lobby.actionBar","&bYou are playing &e&lRFTB &bwith &e&lRigoxRFTB");
        addConfig(Files.MESSAGES,"messages.lobby.bossBar","&bYou are playing on &e&nRigox Network&b.");
        addConfig(Files.MESSAGES,"messages.others.topFormat.top","&b================[&lTOP 10&b]================");
        addConfig(Files.MESSAGES,"messages.others.topFormat.format","&a%number% &6%player% &7- Score: &b%score%");
        addConfig(Files.MESSAGES,"messages.others.topFormat.bot","&b================[&lTOP 10&b]================");
        addConfig(Files.MESSAGES,"messages.others.gameTypes.Classic","Classic");
        addConfig(Files.MESSAGES,"messages.others.gameTypes.Infected","Infected");
        addConfig(Files.MESSAGES,"messages.others.gameTypes.Double","Double Beast");
        addConfig(Files.MESSAGES,"messages.others.no-perms","&cYou need permission &7%permission% &cfor this action.");
        addConfig(Files.MESSAGES,"messages.others.full","&cThis game is full!");
        addConfig(Files.MESSAGES,"messages.others.gamePlaying","&cThis arena is in game.");
        addConfig(Files.MESSAGES,"messages.others.restarting","&cThis game is in restarting mode!");
        addConfig(Files.MESSAGES,"messages.others.playerError","&7%player% &cis not online.");
        addConfig(Files.MESSAGES,"messages.others.customChat.inGame","&a[%player_role%] &7<player_name>&8: &f%message%");
        addConfig(Files.MESSAGES,"messages.others.customChat.spectator","&8[SPECTATOR] &7<player_name>&8: &f%message%");
        addConfig(Files.MESSAGES,"messages.others.customChat.lobby","&7<player_name>&8: &f%message%");
        addConfig(Files.MESSAGES,"messages.admin.create","&aArena &b%arena_id% &acreated correctly!");
        addConfig(Files.MESSAGES,"messages.admin.delete","&aArena &b%arena_id% &aremoved correctly!");
        addConfig(Files.MESSAGES,"messages.admin.arenaError","&7%arena_id% &cdoesn't exists");
        addConfig(Files.MESSAGES,"messages.admin.setWaiting","&aWaiting Lobby now is in &b%location%");
        addConfig(Files.MESSAGES,"messages.admin.setSpawn","&aSpawn-%spawnType% now is in &b%location%");
        addConfig(Files.MESSAGES,"messages.admin.setCenter","&aArena center now is in &b%location%");
        addConfig(Files.MESSAGES,"messages.admin.setCenter","&aArena Beast Presentation now is in &b%location%");
        addConfig(Files.MESSAGES,"messages.admin.setChestArea","&aArena Chest Area now is in &b%location%");
        addConfig(Files.MESSAGES,"messages.admin.setMode","&aArena Mode now is &b%arena_mode%");
        addConfig(Files.MESSAGES,"messages.admin.setCreator","&aArena creator now is &b%arena_creator%");
        addConfig(Files.MESSAGES,"messages.admin.setName","&aArena ID: &b%arena_id% &anow has the name: &b%arena_name%");
        addConfig(Files.MESSAGES,"messages.admin.saveArena","&aArena &b%arena_name%&a(&b%arena_id%&a) was saved and enabled correctly!");
        addConfig(Files.MESSAGES,"messages.admin.editArena","&aNow you can edit arena &b%arena_name%&a(&b%arena_id%&a)");
    }
    public void addConfig(Files fileToAdd,String path,Object value) {
        switch(fileToAdd) {
            case MESSAGES:
                if(!getControl(Files.MESSAGES).contains(path)) {
                    getControl(Files.MESSAGES).set(path,value);
                }
                return;
            case ITEMS:
                if(!getControl(Files.ITEMS).contains(path)) {
                    getControl(Files.ITEMS).set(path,value);
                }
                return;
            case MYSQL:
                if(!getControl(Files.MYSQL).contains(path)) {
                    getControl(Files.MYSQL).set(path, value);
                }
                return;
            case GAMES:
                if(!getControl(Files.GAMES).contains(path)) {
                    getControl(Files.GAMES).set(path,value);
                }
                return;
            case MENUS:
                if(!getControl(Files.MENUS).contains(path)) {
                    getControl(Files.MENUS).set(path,value);
                }
                return;
            case SCOREBOARD:
                if(!getControl(Files.SCOREBOARD).contains(path)) {
                    getControl(Files.SCOREBOARD).set(path,value);
                }
                return;
            default:
                if(!getControl(Files.SETTINGS).contains(path)) {
                    getControl(Files.SETTINGS).set(path,value);
                }
        }
    }
    public FileConfiguration getControl(Files fileToGet) {
        switch (fileToGet) {
            case GAMES:
                if(rGames == null) reloadFile(SaveMode.GAMES_FILES);
                return rGames;
            case MYSQL:
                if(rMySQL == null) reloadFile(SaveMode.MYSQL);
                return rMySQL;
            case MENUS:
                if(rMenus == null) reloadFile(SaveMode.MENUS);
                return rMenus;
            case ITEMS:
                if(rItems == null) reloadFile(SaveMode.ITEMS);
                return rItems;
            case MESSAGES:
                if(rMessages == null) reloadFile(SaveMode.MESSAGES);
                return rMessages;
            case SCOREBOARD:
                if(rScoreboard == null) reloadFile(SaveMode.SCOREBOARDS);
                return rScoreboard;
            default:
                if(rSettings == null) reloadFile(SaveMode.SETTINGS);
                return rSettings;
        }
    }
    public void reloadFile(SaveMode Mode) {
        loadFiles();
        if(Mode.equals(SaveMode.MESSAGES) || Mode.equals(SaveMode.ALL)) {
            rMessages = YamlConfiguration.loadConfiguration(Messages);
        }
        if(Mode.equals(SaveMode.ITEMS) || Mode.equals(SaveMode.ALL)) {
            rItems = YamlConfiguration.loadConfiguration(Items);
        }
        if(Mode.equals(SaveMode.MENUS) || Mode.equals(SaveMode.ALL)) {
            rMenus = YamlConfiguration.loadConfiguration(Menus);
        }
        if(Mode.equals(SaveMode.SETTINGS) || Mode.equals(SaveMode.ALL)) {
            rSettings = YamlConfiguration.loadConfiguration(Settings);
        }
        if(Mode.equals(SaveMode.SCOREBOARDS) || Mode.equals(SaveMode.ALL)) {
            rScoreboard = YamlConfiguration.loadConfiguration(Scoreboard);
        }
        if(Mode.equals(SaveMode.MYSQL) || Mode.equals(SaveMode.ALL)) {
            rMySQL = YamlConfiguration.loadConfiguration(MySQL);
        }
        if(Mode.equals(SaveMode.GAMES_FILES) || Mode.equals(SaveMode.ALL)) {
            rGames = YamlConfiguration.loadConfiguration(Games);
        }
    }
    public void save(SaveMode Mode) {
        try {
            if(Mode.equals(SaveMode.GAMES_FILES) || Mode.equals(SaveMode.ALL)) {
                getControl(Files.GAMES).save(Games);
            }
            if(Mode.equals(SaveMode.MYSQL) || Mode.equals(SaveMode.ALL)) {
                getControl(Files.MYSQL).save(MySQL);
            }
            if(Mode.equals(SaveMode.SCOREBOARDS) || Mode.equals(SaveMode.ALL)) {
                getControl(Files.SCOREBOARD).save(Scoreboard);
            }
            if(Mode.equals(SaveMode.ITEMS) || Mode.equals(SaveMode.ALL)) {
                getControl(Files.ITEMS).save(Items);
            }
            if(Mode.equals(SaveMode.MESSAGES) || Mode.equals(SaveMode.ALL)) {
                getControl(Files.MESSAGES).save(Messages);
            }
            if(Mode.equals(SaveMode.SETTINGS) || Mode.equals(SaveMode.ALL)) {
                getControl(Files.SETTINGS).save(Settings);
            }
        } catch(Throwable throwable) {
            plugin.getLogs().error("The plugin can't load or save configuration files! (Spigot Control Issue - Caused by: One plugin is using bad the <getControl() from FileManager.class>)");
            plugin.getLogs().error(throwable);
        }
    }

}

