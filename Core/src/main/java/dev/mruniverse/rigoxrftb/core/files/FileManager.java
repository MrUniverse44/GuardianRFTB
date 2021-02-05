package dev.mruniverse.rigoxrftb.core.files;

import dev.mruniverse.rigoxrftb.core.enums.RigoxFiles;
import dev.mruniverse.rigoxrftb.core.RigoxRFTB;
import dev.mruniverse.rigoxrftb.core.enums.SaveMode;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private final RigoxRFTB plugin;
    private FileConfiguration rGames,rMySQL,rMenus,rItems,rMessages,rScoreboard,rSettings,rData,rChests;
    private final File dataFolder;
    private File Games;
    private File MySQL;
    private File Menus;
    private File Items;
    private File Messages;
    private File Scoreboard;
    private File Settings;
    private File Data;
    private File Chests;
    public FileManager(RigoxRFTB main) {
        plugin = main;
        dataFolder = main.getDataFolder();
    }
    public void loadFiles() {
        loadFolder(dataFolder,"Main Folder");
        Games = new File(dataFolder,"games.yml");
        Data = new File(dataFolder,"data.yml");
        Chests = new File(dataFolder,"chests.yml");
        MySQL = new File(dataFolder,"mysql.yml");
        Menus = new File(dataFolder, "menus.yml");
        Items = new File(dataFolder, "items.yml");
        Scoreboard = new File(dataFolder, "scoreboards.yml");
        Messages = new File(dataFolder,"messages.yml");
        Settings = new File(dataFolder,"settings.yml");
        loadFile(Games,"games.yml");
        loadFile(Data,"data.yml");
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
        addConfig(RigoxFiles.SETTINGS,"settings.update-check",true);
        addConfig(RigoxFiles.SETTINGS,"settings.maxTime",500);
        addConfig(RigoxFiles.SETTINGS,"settings.lobbyLocation","notSet");
        if(!Bukkit.getServer().getVersion().contains("1.8")) {
            addConfig(RigoxFiles.SETTINGS, "settings.defaultSounds.sound1", "BLOCK_NOTE_BLOCK_HARP");
            addConfig(RigoxFiles.SETTINGS, "settings.defaultSounds.sound2", "ENTITY_ENDER_DRAGON_GROWL");
            addConfig(RigoxFiles.SETTINGS, "settings.defaultSounds.sound3", "ENTITY_EXPERIENCE_ORB_PICKUP");
        } else {
            addConfig(RigoxFiles.SETTINGS, "settings.defaultSounds.sound1", "NOTE_STICKS");
            addConfig(RigoxFiles.SETTINGS, "settings.defaultSounds.sound2", "ENDERDRAGON_GROWL");
            addConfig(RigoxFiles.SETTINGS, "settings.defaultSounds.sound3", "ORB_PICKUP");
        }
        addConfig(RigoxFiles.SETTINGS,"settings.options.joinLobbyTeleport",false);
        addConfig(RigoxFiles.SETTINGS,"settings.options.joinHeal",true);
        addConfig(RigoxFiles.SETTINGS,"settings.options.joinAdventureGamemode",true);
        addConfig(RigoxFiles.SETTINGS,"settings.options.clearInventory-onJoin",true);
        addConfig(RigoxFiles.SETTINGS,"settings.options.hideServerJoinMessage",true);
        addConfig(RigoxFiles.SETTINGS,"settings.options.hideServerQuitMessage",true);
        addConfig(RigoxFiles.SETTINGS,"settings.options.lobby-bossBar",true);
        addConfig(RigoxFiles.SETTINGS,"settings.options.lobby-noDamage",true);
        addConfig(RigoxFiles.SETTINGS,"settings.options.lobby-noHunger",true);
        addConfig(RigoxFiles.SETTINGS,"settings.options.lobby-actionBar",true);
        addConfig(RigoxFiles.SETTINGS,"settings.tags.runners.toggle",true);
        addConfig(RigoxFiles.SETTINGS,"settings.tags.runners.tag","&b&lRUNNER");
        addConfig(RigoxFiles.SETTINGS,"settings.tags.beasts.toggle",true);
        addConfig(RigoxFiles.SETTINGS,"settings.tags.beasts.tag","&c&lBEAST");
        addConfig(RigoxFiles.SETTINGS,"settings.ShowBeastDistance.toggle",true);
        addConfig(RigoxFiles.SETTINGS,"settings.ShowBeastDistance.Format","BOSSBAR");
        addConfig(RigoxFiles.SETTINGS,"settings.dateFormat","dd/MM/yyyy");
        addConfig(RigoxFiles.SETTINGS,"settings.lobbyScoreboard-only-in-lobby-world",true);
        addConfig(RigoxFiles.SETTINGS,"settings.options.pluginChat",true);
        addConfig(RigoxFiles.SETTINGS,"settings.options.PerWorldTab",true);
        addConfig(RigoxFiles.SETTINGS,"settings.options.PerWorldChat",true);
        addConfig(RigoxFiles.SETTINGS,"settings.pointSystem.onRunnerDeath",-4);
        addConfig(RigoxFiles.SETTINGS,"settings.pointSystem.onBeastDeath",-4);
        addConfig(RigoxFiles.SETTINGS,"settings.pointSystem.onRunnersWin",8);
        addConfig(RigoxFiles.SETTINGS,"settings.pointSystem.onBeastWin",4);
        addConfig(RigoxFiles.SETTINGS,"settings.pointSystem.onKillBeast",4);
        addConfig(RigoxFiles.SETTINGS,"settings.pointSystem.onBeastKill",1);
        addConfig(RigoxFiles.SETTINGS,"settings.signs.line1","&l%arena%");
        addConfig(RigoxFiles.SETTINGS,"settings.signs.line2","%gameStatus%");
        addConfig(RigoxFiles.SETTINGS,"settings.signs.line3","%on%/%max%");
        addConfig(RigoxFiles.SETTINGS,"settings.signs.line4","&nClick to join");
        addConfig(RigoxFiles.SETTINGS,"settings.gameStatus.preparing","&5Config");
        addConfig(RigoxFiles.SETTINGS,"settings.gameStatus.waiting","&aWaiting");
        addConfig(RigoxFiles.SETTINGS,"settings.gameStatus.starting","&eStarting");
        addConfig(RigoxFiles.SETTINGS,"settings.gameStatus.playing","&cPlaying");
        addConfig(RigoxFiles.SETTINGS,"settings.gameStatus.InGame","&4InGame");
        addConfig(RigoxFiles.SETTINGS,"settings.gameStatus.ending","&9Restarting");
        addConfig(RigoxFiles.SETTINGS,"roles.beast","Beast");
        addConfig(RigoxFiles.SETTINGS,"roles.runner","Runner");
        addConfig(RigoxFiles.SETTINGS,"roles.beasts","Beasts");
        addConfig(RigoxFiles.SETTINGS,"roles.runners","Runners");
        addConfig(RigoxFiles.SETTINGS,"times.minutes","minutes");
        addConfig(RigoxFiles.SETTINGS,"times.minute","minute");
        addConfig(RigoxFiles.SETTINGS,"times.seconds","seconds");
        addConfig(RigoxFiles.SETTINGS,"times.second","second");
        List<String> lore = new ArrayList<>();
        lore.add("&7Here your lore");
        lore.add("&erigox.club");
        addConfig(RigoxFiles.MYSQL,"mysql.enabled",true);
        addConfig(RigoxFiles.MYSQL,"mysql.jdbc-url","jdbc:mysql://[host]:[port]/[db]?autoReconnect=true");
        addConfig(RigoxFiles.MYSQL,"mysql.host","localhost");
        addConfig(RigoxFiles.MYSQL,"mysql.port",3306);
        addConfig(RigoxFiles.MYSQL,"mysql.username","root");
        addConfig(RigoxFiles.MYSQL,"mysql.password","password");
        addConfig(RigoxFiles.MYSQL,"mysql.database","rRFTB");
        addConfig(RigoxFiles.MYSQL,"mysql.table","rData");
        addConfig(RigoxFiles.ITEMS,"lobby.gameSelector.toggle",true);
        addConfig(RigoxFiles.ITEMS,"lobby.gameSelector.item","PAPER");
        addConfig(RigoxFiles.ITEMS,"lobby.gameSelector.name","&aGame Selector");
        addConfig(RigoxFiles.ITEMS,"lobby.gameSelector.lore",lore);
        addConfig(RigoxFiles.ITEMS,"lobby.gameSelector.slot",0);
        addConfig(RigoxFiles.ITEMS,"lobby.Shop.toggle",true);
        addConfig(RigoxFiles.ITEMS,"lobby.Shop.item","EMERALD");
        addConfig(RigoxFiles.ITEMS,"lobby.Shop.name","&aRFTB Shop");
        addConfig(RigoxFiles.ITEMS,"lobby.Shop.lore",lore);
        addConfig(RigoxFiles.ITEMS,"lobby.Shop.slot",4);
        addConfig(RigoxFiles.ITEMS,"lobby.PlayerSettings.toggle",true);
        addConfig(RigoxFiles.ITEMS,"lobby.PlayerSettings.item","FIREWORK");
        addConfig(RigoxFiles.ITEMS,"lobby.PlayerSettings.name","&aYour settings");
        addConfig(RigoxFiles.ITEMS,"lobby.PlayerSettings.lore",lore);
        addConfig(RigoxFiles.ITEMS,"lobby.PlayerSettings.slot",1);
        addConfig(RigoxFiles.ITEMS,"lobby.LobbySelector.toggle",true);
        addConfig(RigoxFiles.ITEMS,"lobby.LobbySelector.item","BEACON");
        addConfig(RigoxFiles.ITEMS,"lobby.LobbySelector.name","&aLobby Selector");
        addConfig(RigoxFiles.ITEMS,"lobby.LobbySelector.lore",lore);
        addConfig(RigoxFiles.ITEMS,"lobby.LobbySelector.slot",7);
        addConfig(RigoxFiles.ITEMS,"lobby.Exit.toggle",true);
        addConfig(RigoxFiles.ITEMS,"lobby.Exit.item","BED");
        addConfig(RigoxFiles.ITEMS,"lobby.Exit.name","&aGo to the hub");
        addConfig(RigoxFiles.ITEMS,"lobby.Exit.lore",lore);
        addConfig(RigoxFiles.ITEMS,"lobby.Exit.slot",8);
        addConfig(RigoxFiles.ITEMS,"InGame.RunnerKit.item","PAPER");
        addConfig(RigoxFiles.ITEMS,"InGame.RunnerKit.name","&aKit Selector (&lRunners&a)");
        addConfig(RigoxFiles.ITEMS,"InGame.RunnerKit.lore",lore);
        addConfig(RigoxFiles.ITEMS,"InGame.RunnerKit.slot",0);
        addConfig(RigoxFiles.ITEMS,"InGame.BeastKit.item","PAPER");
        addConfig(RigoxFiles.ITEMS,"InGame.BeastKit.name","&aKit Selector (&lBeast&a)");
        addConfig(RigoxFiles.ITEMS,"InGame.BeastKit.lore",lore);
        addConfig(RigoxFiles.ITEMS,"InGame.BeastKit.slot",0);
        addConfig(RigoxFiles.ITEMS,"InGame.Exit.item","BED");
        addConfig(RigoxFiles.ITEMS,"InGame.Exit.name","&aLeave");
        addConfig(RigoxFiles.ITEMS,"InGame.Exit.lore",lore);
        addConfig(RigoxFiles.ITEMS,"InGame.Exit.slot",8);
        addConfig(RigoxFiles.ITEMS,"Playing.BeastInventory.Sword.item","DIAMOND_SWORD");
        addConfig(RigoxFiles.ITEMS,"Playing.BeastInventory.Sword.name","&eBeast Sword");
        addConfig(RigoxFiles.ITEMS,"Playing.BeastInventory.Sword.lore",lore);
        addConfig(RigoxFiles.ITEMS,"Playing.BeastInventory.Sword.slot",0);
        addConfig(RigoxFiles.ITEMS,"Playing.BeastArmor.Helmet.item","DIAMOND_HELMET");
        addConfig(RigoxFiles.ITEMS,"Playing.BeastArmor.Helmet.name","&eBeast Helmet");
        addConfig(RigoxFiles.ITEMS,"Playing.BeastArmor.Helmet.lore",lore);
        addConfig(RigoxFiles.ITEMS,"Playing.BeastArmor.Chestplate.item","DIAMOND_CHESTPLATE");
        addConfig(RigoxFiles.ITEMS,"Playing.BeastArmor.Chestplate.name","&eBeast Helmet");
        addConfig(RigoxFiles.ITEMS,"Playing.BeastArmor.Chestplate.lore",lore);
        addConfig(RigoxFiles.ITEMS,"Playing.BeastArmor.Leggings.item","DIAMOND_LEGGINGS");
        addConfig(RigoxFiles.ITEMS,"Playing.BeastArmor.Leggings.name","&eBeast Leggings");
        addConfig(RigoxFiles.ITEMS,"Playing.BeastArmor.Leggings.lore",lore);
        addConfig(RigoxFiles.ITEMS,"Playing.BeastArmor.Boots.item","DIAMOND_BOOTS");
        addConfig(RigoxFiles.ITEMS,"Playing.BeastArmor.Boots.name","&eBeast Boots");
        addConfig(RigoxFiles.ITEMS,"Playing.BeastArmor.Boots.lore",lore);
        List<String> lists = new ArrayList<>();
        lists.add("/leave");
        lists.add("/quit");
        lists.add("/salir");
        addConfig(RigoxFiles.SETTINGS,"settings.leaveCMDs",lists);
        lists = new ArrayList<>();
        addConfig(RigoxFiles.SCOREBOARD,"scoreboards.lobby.toggle",true);
        addConfig(RigoxFiles.SCOREBOARD,"scoreboards.lobby.title","&e&lRigoxRFTB");
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
        addConfig(RigoxFiles.SCOREBOARD,"scoreboards.lobby.lines",lists);
        lists = new ArrayList<>();
        addConfig(RigoxFiles.SCOREBOARD,"scoreboards.waiting.title","&e&lRigoxRFTB");
        lists.add("&7<arena_mode> | <timeFormat>");
        lists.add(" ");
        lists.add("&fMap: &a<arena_name>");
        lists.add("&fPlayers: &a<arena_online>/<arena_max>");
        lists.add(" ");
        lists.add("<isWaiting>&fWaiting for");
        lists.add("<isWaiting>&a<arena_need> &fplayers to start");
        lists.add("<isSelecting>&fSelecting beast in &a<arena_time_number>");
        lists.add("<isStarting>&fStarting in &a<arena_time_number> &f<arena_time_text>.");
        lists.add("<BeastAppear>&fYou will spawn in &a<arena_time_number>&f!");
        lists.add(" ");
        lists.add("&erigox.club");
        addConfig(RigoxFiles.SCOREBOARD,"scoreboards.waiting.lines",lists);
        lists = new ArrayList<>();
        addConfig(RigoxFiles.SCOREBOARD,"scoreboards.playing.title","&e&lRigoxRFTB");
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
        addConfig(RigoxFiles.SCOREBOARD,"scoreboards.playing.lines",lists);
        lists = new ArrayList<>();
        addConfig(RigoxFiles.SCOREBOARD,"scoreboards.beastWin.forBeast.title","&e&lRigoxRFTB");
        lists.add("&7<timeFormat>");
        lists.add(" ");
        lists.add("&fYou are the best beast");
        lists.add("&aWell played&f!");
        lists.add(" ");
        lists.add("&erigox.club");
        addConfig(RigoxFiles.SCOREBOARD,"scoreboards.beastWin.forBeast.lines",lists);
        lists = new ArrayList<>();
        addConfig(RigoxFiles.SCOREBOARD,"scoreboards.beastWin.forRunners.title","&e&lRigoxRFTB");
        lists.add("&7<timeFormat>");
        lists.add(" ");
        lists.add("&fWell.. you tried");
        lists.add(" ");
        lists.add("&erigox.club");
        addConfig(RigoxFiles.SCOREBOARD,"scoreboards.beastWin.forRunners.lines",lists);
        lists = new ArrayList<>();
        addConfig(RigoxFiles.SCOREBOARD,"scoreboards.runnersWin.forBeast.title","&e&lRigoxRFTB");
        lists.add("&7<timeFormat>");
        lists.add(" ");
        lists.add("&fBad played!");
        lists.add("&cGood luck for next game&f!");
        lists.add(" ");
        lists.add("&erigox.club");
        addConfig(RigoxFiles.SCOREBOARD,"scoreboards.runnersWin.forBeast.lines",lists);
        lists = new ArrayList<>();
        addConfig(RigoxFiles.SCOREBOARD,"scoreboards.runnersWin.forRunners.title","&e&lRigoxRFTB");
        lists.add("&7<timeFormat>");
        lists.add(" ");
        lists.add("&fRunners Win!");
        lists.add("&aWell played!");
        lists.add(" ");
        lists.add("&erigox.club");
        addConfig(RigoxFiles.SCOREBOARD,"scoreboards.runnersWin.forRunners.lines",lists);
        lists = new ArrayList<>();
        addConfig(RigoxFiles.MESSAGES,"messages.inGame.join","&7%player% &ehas joined &e(&b%online%&e/&b%max%&e)!");
        addConfig(RigoxFiles.MESSAGES,"messages.inGame.quit","&7%player% &ehas quit!");
        addConfig(RigoxFiles.MESSAGES,"messages.inGame.already","&cYou're already in an arena!");
        addConfig(RigoxFiles.MESSAGES,"messages.inGame.selectingBeast","&eThe beast will be selected in &c%time% &e%seconds%!");
        addConfig(RigoxFiles.MESSAGES,"messages.inGame.starting","&eThe game starts in &c%time% &e%seconds%!");
        addConfig(RigoxFiles.MESSAGES,"messages.inGame.beastsAppear","&eThe beasts spawns in &c%time% &e%seconds%!");
        addConfig(RigoxFiles.MESSAGES,"messages.inGame.chosenBeast","&eThe player &b%player% &enow is a beast!");
        lists.add("&a[bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx]");
        lists.add("<center>&e&lMode: %gameType% - %map_name%");
        lists.add(" ");
        lists.add("<isBeast>&e&lKill all runners!");
        lists.add("<isRunner>&e&lSurvive and Kill the beast!");
        lists.add(" ");
        lists.add("&a[bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx]");
        addConfig(RigoxFiles.MESSAGES,"messages.inGame.infoList.startInfo",lists);
        lists = new ArrayList<>();
        lists.add("&a[bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx]");
        lists.add("<center>&e&l%gameType% - %map_name%");
        lists.add(" ");
        lists.add("<center>&a%winner_team% &e&lWINNER &c%looser_team%");
        lists.add(" ");
        lists.add("&a[bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx]");
        addConfig(RigoxFiles.MESSAGES,"messages.inGame.infoList.endInfo",lists);
        lists = new ArrayList<>();
        lists.add("&a[bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx]");
        lists.add("<center>&e&l%gameType% - %map_name%");
        lists.add(" ");
        lists.add(" &7You earned:");
        lists.add("  &f[px] &3%game% Game Experience");
        lists.add(" ");
        lists.add("&a[bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx][bx]");
        addConfig(RigoxFiles.MESSAGES,"messages.inGame.infoList.rewardSummary",lists);
        addConfig(RigoxFiles.MESSAGES,"messages.inGame.deathMessages.pvp","&7%victim% was killed by %attacker%");
        addConfig(RigoxFiles.MESSAGES,"messages.inGame.deathMessages.void","&7%victim% was searching a diamond.");
        addConfig(RigoxFiles.MESSAGES,"messages.inGame.deathMessages.lava","&7%victim% was on fire!");
        addConfig(RigoxFiles.MESSAGES,"messages.inGame.deathMessages.bow","&7%attacker% is the best with the bow vs %victim%");
        addConfig(RigoxFiles.MESSAGES,"messages.inGame.deathMessages.otherCause","&7%victim% died");
        addConfig(RigoxFiles.MESSAGES,"messages.inGame.cantStartGame","&cThis game can't start, not enough players");
        addConfig(RigoxFiles.MESSAGES,"messages.inGame.others.winCoins","&6+%winCoins% coins (Win)!");
        addConfig(RigoxFiles.MESSAGES,"messages.inGame.others.playAgainWin","&a&lYOU WON! &e&lWant to play again? <clickText>");
        addConfig(RigoxFiles.MESSAGES,"messages.inGame.others.playAgainLoose","&c&lYOU LOOSE! &e&lWant to play again? <clickText>");
        addConfig(RigoxFiles.MESSAGES,"messages.inGame.others.playAgainClickText","&6&lCLICK HERE");
        addConfig(RigoxFiles.MESSAGES,"messages.inGame.others.titles.gameStart.title","&a");
        addConfig(RigoxFiles.MESSAGES,"messages.inGame.others.titles.gameStart.subtitle","&aGame Started!");
        addConfig(RigoxFiles.MESSAGES,"messages.inGame.others.titles.runnersGo.toRunners.title","&a");
        addConfig(RigoxFiles.MESSAGES,"messages.inGame.others.titles.runnersGo.toRunners.subtitle","&aYou has been released, GO!");
        addConfig(RigoxFiles.MESSAGES,"messages.inGame.others.titles.runnersGo.toBeasts.title","&a");
        addConfig(RigoxFiles.MESSAGES,"messages.inGame.others.titles.runnersGo.toBeasts.subtitle","&aThe runners has been released!");
        addConfig(RigoxFiles.MESSAGES,"messages.inGame.others.titles.beastsGo.toRunners.title","&a");
        addConfig(RigoxFiles.MESSAGES,"messages.inGame.others.titles.beastsGo.toRunners.subtitle","&cThe beasts has been released!");
        addConfig(RigoxFiles.MESSAGES,"messages.inGame.others.titles.beastsGo.toBeasts.title","&a");
        addConfig(RigoxFiles.MESSAGES,"messages.inGame.others.titles.beastsGo.toBeasts.subtitle","&aYou has been released, GO!");
        addConfig(RigoxFiles.MESSAGES,"messages.inGame.others.titles.Winner.title","&6&lVICTORY!");
        addConfig(RigoxFiles.MESSAGES,"messages.inGame.others.titles.Winner.subtitle","&b%winner_name%&f won the game!");
        addConfig(RigoxFiles.MESSAGES,"messages.inGame.others.titles.Looser.title","&c&lGAME OVER!");
        addConfig(RigoxFiles.MESSAGES,"messages.inGame.others.titles.Looser.subtitle","&8%attacker_name%&f kill you!");
        addConfig(RigoxFiles.MESSAGES,"messages.inGame.others.bossBar.toRunners","&bDistance between you and &e&lBEAST &f%beastName%");
        addConfig(RigoxFiles.MESSAGES,"messages.inGame.others.bossBar.toBeasts","&bRunners with life: &e&l%runners%");
        addConfig(RigoxFiles.MESSAGES,"messages.lobby.actionBar","&bYou are playing &e&lRFTB &bwith &e&lRigoxRFTB");
        addConfig(RigoxFiles.MESSAGES,"messages.lobby.bossBar","&bYou are playing on &e&nRigox Network&b.");
        addConfig(RigoxFiles.MESSAGES,"messages.others.topFormat.top","&b================[&lTOP 10&b]================");
        addConfig(RigoxFiles.MESSAGES,"messages.others.topFormat.format","&a%number% &6%player% &7- Score: &b%score%");
        addConfig(RigoxFiles.MESSAGES,"messages.others.topFormat.bot","&b================[&lTOP 10&b]================");
        addConfig(RigoxFiles.MESSAGES,"messages.others.gameTypes.Classic","Classic");
        addConfig(RigoxFiles.MESSAGES,"messages.others.gameTypes.Infected","Infected");
        addConfig(RigoxFiles.MESSAGES,"messages.others.gameTypes.Double","Double Beast");
        addConfig(RigoxFiles.MESSAGES,"messages.others.no-perms","&cYou need permission &7%permission% &cfor this action.");
        addConfig(RigoxFiles.MESSAGES,"messages.others.full","&cThis game is full!");
        addConfig(RigoxFiles.MESSAGES,"messages.others.gamePlaying","&cThis arena is in game.");
        addConfig(RigoxFiles.MESSAGES,"messages.others.restarting","&cThis game is in restarting mode!");
        addConfig(RigoxFiles.MESSAGES,"messages.others.playerError","&7%player% &cis not online.");
        addConfig(RigoxFiles.MESSAGES,"messages.others.customChat.inGame","&a[%player_role%&a] &7<player_name>&8: &f%message%");
        addConfig(RigoxFiles.MESSAGES,"messages.others.customChat.spectator","&8[SPECTATOR] &7<player_name>&8: &f%message%");
        addConfig(RigoxFiles.MESSAGES,"messages.others.customChat.lobby","&7<player_name>&8: &f%message%");
        addConfig(RigoxFiles.MESSAGES,"messages.admin.create","&aArena &b%arena_id% &acreated correctly!");
        addConfig(RigoxFiles.MESSAGES,"messages.admin.delete","&aArena &b%arena_id% &aremoved correctly!");
        addConfig(RigoxFiles.MESSAGES,"messages.admin.arenaError","&7%arena_id% &cdoesn't exists");
        addConfig(RigoxFiles.MESSAGES,"messages.admin.setWaiting","&aWaiting Lobby now is in &b%location%");
        addConfig(RigoxFiles.MESSAGES,"messages.admin.setSpawn","&aSpawn-%spawnType% now is in &b%location%");
        addConfig(RigoxFiles.MESSAGES,"messages.admin.setCenter","&aArena center now is in &b%location%");
        addConfig(RigoxFiles.MESSAGES,"messages.admin.setCenter","&aArena Beast Presentation now is in &b%location%");
        addConfig(RigoxFiles.MESSAGES,"messages.admin.setChestArea","&aArena Chest Area now is in &b%location%");
        addConfig(RigoxFiles.MESSAGES,"messages.admin.setMode","&aArena Mode now is &b%arena_mode%");
        addConfig(RigoxFiles.MESSAGES,"messages.admin.setCreator","&aArena creator now is &b%arena_creator%");
        addConfig(RigoxFiles.MESSAGES,"messages.admin.setName","&aArena ID: &b%arena_id% &anow has the name: &b%arena_name%");
        addConfig(RigoxFiles.MESSAGES,"messages.admin.saveArena","&aArena &b%arena_name%&a(&b%arena_id%&a) was saved and enabled correctly!");
        addConfig(RigoxFiles.MESSAGES,"messages.admin.editArena","&aNow you can edit arena &b%arena_name%&a(&b%arena_id%&a)");
        List<String> menuList = new ArrayList<>();
        addConfig(RigoxFiles.MENUS, "menus.shop.inventoryName","&8Shop Menu");
        addConfig(RigoxFiles.MENUS, "menus.shop.inventoryRows",6);
        if(!plugin.getServer().getVersion().contains("1.8")) {
            menuList.add("ENTITY_EXPERIENCE_ORB_PICKUP");
        } else {
            menuList.add("ORB_PICKUP");
        }
        addConfig(RigoxFiles.MENUS, "menus.shop.inventorySounds",menuList);
        menuList = new ArrayList<>();
        addConfig(RigoxFiles.MENUS, "menus.shop.items.KitRunner.name", "&aKits: Runner");
        addConfig(RigoxFiles.MENUS, "menus.shop.items.KitRunner.slot", 21);
        menuList.add("&8RigoxRFTB");
        menuList.add("&eClick to open kit runners menu");
        addConfig(RigoxFiles.MENUS, "menus.shop.items.KitRunner.lore", menuList);
        addConfig(RigoxFiles.MENUS, "menus.shop.items.KitRunner.item", "MAP");
        menuList = new ArrayList<>();
        menuList.add("&8RigoxRFTB");
        menuList.add("&eClick to open Craft Coins menu");
        addConfig(RigoxFiles.MENUS, "menus.shop.items.Craft.name", "&aCraft Coins");
        addConfig(RigoxFiles.MENUS, "menus.shop.items.Craft.slot", 22);
        addConfig(RigoxFiles.MENUS, "menus.shop.items.Craft.lore", menuList);
        addConfig(RigoxFiles.MENUS, "menus.shop.items.Craft.item", "BREWING_STAND");
        addConfig(RigoxFiles.MENUS, "menus.shop.items.KitBeast.name", "&aKits: Beast");
        addConfig(RigoxFiles.MENUS, "menus.shop.items.KitBeast.slot", 23);
        menuList = new ArrayList<>();
        menuList.add("&8RigoxRFTB");
        menuList.add("&eClick to open kit beasts menu");
        addConfig(RigoxFiles.MENUS, "menus.shop.items.KitBeast.lore",menuList);
        addConfig(RigoxFiles.MENUS, "menus.shop.items.KitBeast.item","FIREWORK");
        addConfig(RigoxFiles.MENUS, "menus.shop.items.Boost.name","&aGame Booster");
        addConfig(RigoxFiles.MENUS, "menus.shop.items.Boost.slot",31);
        menuList = new ArrayList<>();
        menuList.add("&8RigoxRFTB");
        menuList.add("&eClick to open boost menu");
        addConfig(RigoxFiles.MENUS, "menus.shop.items.Boost.lore",menuList);
        addConfig(RigoxFiles.MENUS, "menus.shop.items.Boost.item","EXPERIENCE_BOTTLE");
        menuList = new ArrayList<>();
        addConfig(RigoxFiles.MENUS, "menus.game.inventoryName","&8Game Menu");
        addConfig(RigoxFiles.MENUS, "menus.game.inventoryRows",3);
        if(!plugin.getServer().getVersion().contains("1.8")) {
            menuList.add("ENTITY_EXPERIENCE_ORB_PICKUP");
        } else {
            menuList.add("ORB_PICKUP");
        }
        addConfig(RigoxFiles.MENUS, "menus.game.inventorySounds",menuList);
        addConfig(RigoxFiles.MENUS, "menus.game.item-status.waiting","STAINED_CLAY:5");
        addConfig(RigoxFiles.MENUS, "menus.game.item-status.starting","STAINED_CLAY:4");
        addConfig(RigoxFiles.MENUS, "menus.game.item-status.playing","STAINED_CLAY:14");
        addConfig(RigoxFiles.MENUS, "menus.game.item-status.ending","STAINED_CLAY:11");
        addConfig(RigoxFiles.MENUS, "menus.game.item.name","&e&nMap: %map_name%");
        menuList = new ArrayList<>();
        menuList.add(" ");
        menuList.add("&7Status: &b%map_status%");
        menuList.add("&7Mode: &b%map_mode%");
        menuList.add("&7Players: &b%map_on%/%map_max%");
        menuList.add(" ");
        menuList.add("&erigox.club");
        addConfig(RigoxFiles.MENUS, "menus.game.item.lore",menuList);
        menuList = new ArrayList<>();
        menuList.add("&8RigoxRFTB &7F-1");
        menuList.add("&erigox.club");
        if(!getControl(RigoxFiles.MENUS).contains("fill-inventory.shop")) {
            addConfig(RigoxFiles.MENUS, "fill-inventory.shop.fillOne.item", "STAINED_GLASS_PANE:5");
            addConfig(RigoxFiles.MENUS, "fill-inventory.shop.fillOne.name", "&aRFTB Shop");
            addConfig(RigoxFiles.MENUS, "fill-inventory.shop.fillOne.lore", menuList);
            addConfig(RigoxFiles.MENUS, "fill-inventory.shop.fillOne.list.type", "ONLY");
            List<Integer> menuValues = new ArrayList<>();
            menuValues.add(0);
            menuValues.add(1);
            menuValues.add(7);
            menuValues.add(8);
            menuValues.add(9);
            menuValues.add(17);
            menuValues.add(36);
            menuValues.add(44);
            menuValues.add(45);
            menuValues.add(46);
            menuValues.add(52);
            menuValues.add(53);
            addConfig(RigoxFiles.MENUS, "fill-inventory.shop.fillOne.list.values", menuValues);
            addConfig(RigoxFiles.MENUS, "fill-inventory.shop.fillTwo.item", "STAINED_GLASS_PANE:15");
            addConfig(RigoxFiles.MENUS, "fill-inventory.shop.fillTwo.name", "&aRFTB Shop");
            menuList = new ArrayList<>();
            menuList.add("&8RigoxRFTB &7F-2");
            menuList.add("&erigox.club");
            addConfig(RigoxFiles.MENUS, "fill-inventory.shop.fillTwo.lore", menuList);
            addConfig(RigoxFiles.MENUS, "fill-inventory.shop.fillTwo.list.type", "IGNORE");
            menuValues = new ArrayList<>();
            menuValues.add(0);
            menuValues.add(1);
            menuValues.add(7);
            menuValues.add(8);
            menuValues.add(9);
            menuValues.add(17);
            menuValues.add(36);
            menuValues.add(44);
            menuValues.add(45);
            menuValues.add(46);
            menuValues.add(52);
            menuValues.add(53);
            menuValues.add(21);
            menuValues.add(22);
            menuValues.add(23);
            menuValues.add(31);
            addConfig(RigoxFiles.MENUS, "fill-inventory.shop.fillTwo.list.values", menuValues);
        }
        menuList = new ArrayList<>();
        if(plugin.getFiles().getControl(RigoxFiles.CHESTS).get("chests") == null) {
            addConfig(RigoxFiles.CHESTS, "chests.armor.inventoryName", "&8Armor Items");
            addConfig(RigoxFiles.CHESTS, "chests.armor.inventoryRows", 6);
            if (!plugin.getServer().getVersion().contains("1.8")) {
                menuList.add("ENTITY_EXPERIENCE_ORB_PICKUP");
            } else {
                menuList.add("ORB_PICKUP");
            }
            addConfig(RigoxFiles.CHESTS, "chests.armor.inventorySounds", menuList);
            menuList = new ArrayList<>();
            List<String> enchants = new ArrayList<>();
            enchants.add("UNBREAKING, 10");
            enchants.add("PROTECTION, 5");
            menuList.add("&8RigoxRFTB");
            addConfig(RigoxFiles.CHESTS, "chests.armor.items.helmet.name", "&aDiamond Helmet");
            addConfig(RigoxFiles.CHESTS, "chests.armor.items.helmet.slot", 21);
            addConfig(RigoxFiles.CHESTS, "chests.armor.items.helmet.lore", menuList);
            addConfig(RigoxFiles.CHESTS, "chests.armor.items.helmet.item", "DIAMOND_HELMET");
            addConfig(RigoxFiles.CHESTS, "chests.armor.items.helmet.enchantments", enchants);
            addConfig(RigoxFiles.CHESTS, "chests.armor.items.chestplate.name", "&aDiamond Chestplate");
            addConfig(RigoxFiles.CHESTS, "chests.armor.items.chestplate.slot", 22);
            addConfig(RigoxFiles.CHESTS, "chests.armor.items.chestplate.lore", menuList);
            addConfig(RigoxFiles.CHESTS, "chests.armor.items.chestplate.item", "DIAMOND_CHESTPLATE");
            enchants = new ArrayList<>();
            enchants.add("UNBREAKING, 2");
            enchants.add("PROTECTION, 4");
            addConfig(RigoxFiles.CHESTS, "chests.armor.items.chestplate.enchantments", enchants);
            addConfig(RigoxFiles.CHESTS, "chests.armor.items.leggings.name", "&aDiamond Leggings");
            addConfig(RigoxFiles.CHESTS, "chests.armor.items.leggings.slot", 23);
            addConfig(RigoxFiles.CHESTS, "chests.armor.items.leggings.lore", menuList);
            addConfig(RigoxFiles.CHESTS, "chests.armor.items.leggings.item", "DIAMOND_LEGGINGS");
            enchants = new ArrayList<>();
            enchants.add("UNBREAKING, 3");
            enchants.add("PROTECTION, 2");
            addConfig(RigoxFiles.CHESTS, "chests.armor.items.leggings.enchantments", enchants);
            addConfig(RigoxFiles.CHESTS, "chests.armor.items.boots.name", "&aDiamond Boots");
            addConfig(RigoxFiles.CHESTS, "chests.armor.items.boots.slot", 31);
            addConfig(RigoxFiles.CHESTS, "chests.armor.items.boots.lore", menuList);
            addConfig(RigoxFiles.CHESTS, "chests.armor.items.boots.item", "DIAMOND_BOOTS");
            enchants = new ArrayList<>();
            enchants.add("UNBREAKING, 3");
            enchants.add("PROTECTION, 1");
            addConfig(RigoxFiles.CHESTS, "chests.armor.items.boots.enchantments", enchants);
            menuList = new ArrayList<>();
            addConfig(RigoxFiles.CHESTS, "chests.attack.inventoryName", "&8Attack Items");
            addConfig(RigoxFiles.CHESTS, "chests.attack.inventoryRows", 6);
            if (!plugin.getServer().getVersion().contains("1.8")) {
                menuList.add("ENTITY_EXPERIENCE_ORB_PICKUP");
            } else {
                menuList.add("ORB_PICKUP");
            }
            addConfig(RigoxFiles.CHESTS, "chests.attack.inventorySounds", menuList);
            addConfig(RigoxFiles.CHESTS, "chests.attack.items.sword.name", "&aDiamond Sword");
            addConfig(RigoxFiles.CHESTS, "chests.attack.items.sword.slot", 21);
            menuList = new ArrayList<>();
            menuList.add("&7RigoxRFTB");
            addConfig(RigoxFiles.CHESTS, "chests.attack.items.sword.lore", menuList);
            addConfig(RigoxFiles.CHESTS, "chests.attack.items.sword.item", "DIAMOND_SWORD");
            enchants = new ArrayList<>();
            enchants.add("FIRE_ASPECT, 2");
            enchants.add("SHARPNESS, 3");
            addConfig(RigoxFiles.CHESTS, "chests.attack.items.sword.enchantments", enchants);
            addConfig(RigoxFiles.CHESTS, "chests.attack.items.bow.name", "&aHardcore Bow");
            addConfig(RigoxFiles.CHESTS, "chests.attack.items.bow.slot", 22);
            addConfig(RigoxFiles.CHESTS, "chests.attack.items.bow.lore", menuList);
            addConfig(RigoxFiles.CHESTS, "chests.attack.items.bow.item", "BOW");
            enchants = new ArrayList<>();
            enchants.add("FLAME, 1");
            enchants.add("INFINITY, 1");
            addConfig(RigoxFiles.CHESTS, "chests.attack.items.bow.enchantments", enchants);
            addConfig(RigoxFiles.CHESTS, "chests.attack.items.arrow.name", "&aHardcore Arrow");
            addConfig(RigoxFiles.CHESTS, "chests.attack.items.arrow.slot", 23);
            addConfig(RigoxFiles.CHESTS, "chests.attack.items.arrow.lore", menuList);
            enchants = new ArrayList<>();
            enchants.add("KB, 1");
            addConfig(RigoxFiles.CHESTS, "chests.attack.items.arrow.item", "ARROW");
            addConfig(RigoxFiles.CHESTS, "chests.attack.items.arrow.enchantments", enchants);
            addConfig(RigoxFiles.CHESTS, "chests.attack.items.potion.name", "&aHardcore Potion");
            addConfig(RigoxFiles.CHESTS, "chests.attack.items.potion.slot", 31);
            addConfig(RigoxFiles.CHESTS, "chests.attack.items.potion.lore", menuList);
            addConfig(RigoxFiles.CHESTS, "chests.attack.items.potion.item", "POTION");
        }
    }
    public void addConfig(RigoxFiles fileToAdd, String path, Object value) {
        switch(fileToAdd) {
            case DATA:
                if(!getControl(RigoxFiles.DATA).contains(path)) {
                    getControl(RigoxFiles.DATA).set(path,value);
                }
                return;
            case CHESTS:
                if(!getControl(RigoxFiles.CHESTS).contains(path)) {
                    getControl(RigoxFiles.CHESTS).set(path,value);
                }
                return;
            case MESSAGES:
                if(!getControl(RigoxFiles.MESSAGES).contains(path)) {
                    getControl(RigoxFiles.MESSAGES).set(path,value);
                }
                return;
            case ITEMS:
                if(!getControl(RigoxFiles.ITEMS).contains(path)) {
                    getControl(RigoxFiles.ITEMS).set(path,value);
                }
                return;
            case MYSQL:
                if(!getControl(RigoxFiles.MYSQL).contains(path)) {
                    getControl(RigoxFiles.MYSQL).set(path, value);
                }
                return;
            case GAMES:
                if(!getControl(RigoxFiles.GAMES).contains(path)) {
                    getControl(RigoxFiles.GAMES).set(path,value);
                }
                return;
            case MENUS:
                if(!getControl(RigoxFiles.MENUS).contains(path)) {
                    getControl(RigoxFiles.MENUS).set(path,value);
                }
                return;
            case SCOREBOARD:
                if(!getControl(RigoxFiles.SCOREBOARD).contains(path)) {
                    getControl(RigoxFiles.SCOREBOARD).set(path,value);
                }
                return;
            default:
                if(!getControl(RigoxFiles.SETTINGS).contains(path)) {
                    getControl(RigoxFiles.SETTINGS).set(path,value);
                }
        }
    }
    public FileConfiguration getControl(RigoxFiles fileToGet) {
        switch (fileToGet) {
            case GAMES:
                if(rGames == null) reloadFile(SaveMode.GAMES_FILES);
                return rGames;
            case DATA:
                if(rData == null) reloadFile(SaveMode.DATA);
                return rGames;
            case CHESTS:
                if(rChests == null) reloadFile(SaveMode.CHESTS);
                return rChests;
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
        if(Mode.equals(SaveMode.DATA) || Mode.equals(SaveMode.ALL)) {
            rData = YamlConfiguration.loadConfiguration(Data);
        }
        if(Mode.equals(SaveMode.CHESTS) || Mode.equals(SaveMode.ALL)) {
            rChests = YamlConfiguration.loadConfiguration(Chests);
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
    public List<String> getContent(RigoxFiles file, String path, boolean getKeys) {
        List<String> rx = new ArrayList<>();
        ConfigurationSection section = getControl(file).getConfigurationSection(path);
        if(section == null) return rx;
        rx.addAll(section.getKeys(getKeys));
        return rx;
    }
    public void save(SaveMode Mode) {
        try {
            if(Mode.equals(SaveMode.GAMES_FILES) || Mode.equals(SaveMode.ALL)) {
                getControl(RigoxFiles.GAMES).save(Games);
            }
            if(Mode.equals(SaveMode.MYSQL) || Mode.equals(SaveMode.ALL)) {
                getControl(RigoxFiles.MYSQL).save(MySQL);
            }
            if(Mode.equals(SaveMode.DATA) || Mode.equals(SaveMode.ALL)) {
                getControl(RigoxFiles.DATA).save(Data);
            }
            if(Mode.equals(SaveMode.CHESTS) || Mode.equals(SaveMode.ALL)) {
                getControl(RigoxFiles.CHESTS).save(Chests);
            }
            if(Mode.equals(SaveMode.SCOREBOARDS) || Mode.equals(SaveMode.ALL)) {
                getControl(RigoxFiles.SCOREBOARD).save(Scoreboard);
            }
            if(Mode.equals(SaveMode.MENUS) || Mode.equals(SaveMode.ALL)) {
                getControl(RigoxFiles.MENUS).save(Menus);
            }
            if(Mode.equals(SaveMode.ITEMS) || Mode.equals(SaveMode.ALL)) {
                getControl(RigoxFiles.ITEMS).save(Items);
            }
            if(Mode.equals(SaveMode.MESSAGES) || Mode.equals(SaveMode.ALL)) {
                getControl(RigoxFiles.MESSAGES).save(Messages);
            }
            if(Mode.equals(SaveMode.SETTINGS) || Mode.equals(SaveMode.ALL)) {
                getControl(RigoxFiles.SETTINGS).save(Settings);
            }
        } catch(Throwable throwable) {
            plugin.getLogs().error("The plugin can't load or save configuration files! (Spigot Control Issue - Caused by: One plugin is using bad the <getControl() from FileManager.class>)");
            plugin.getLogs().error(throwable);
        }
    }

}

