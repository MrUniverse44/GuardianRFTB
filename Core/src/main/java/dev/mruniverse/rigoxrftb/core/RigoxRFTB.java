package dev.mruniverse.rigoxrftb.core;

import dev.mruniverse.rigoxrftb.core.enums.ItemFunction;
import dev.mruniverse.rigoxrftb.core.enums.RigoxFiles;
import dev.mruniverse.rigoxrftb.core.enums.NMSenum;
import dev.mruniverse.rigoxrftb.core.enums.SaveMode;
import dev.mruniverse.rigoxrftb.core.files.DataStorage;
import dev.mruniverse.rigoxrftb.core.files.FileStorage;
import dev.mruniverse.rigoxrftb.core.games.GameEquip;
import dev.mruniverse.rigoxrftb.core.games.GameManager;
import dev.mruniverse.rigoxrftb.core.kits.ArmorPart;
import dev.mruniverse.rigoxrftb.core.kits.KitInfo;
import dev.mruniverse.rigoxrftb.core.kits.KitLoader;
import dev.mruniverse.rigoxrftb.core.kits.KitType;
import dev.mruniverse.rigoxrftb.core.listeners.ListenerUtil;
import dev.mruniverse.rigoxrftb.core.nms.NMS;
import dev.mruniverse.rigoxrftb.core.utils.*;
import dev.mruniverse.rigoxrftb.core.utils.players.PlayerManager;
import dev.mruniverse.rigoxrftb.core.utils.players.PlayerRunnable;
import dev.mruniverse.rigoxrftb.core.utils.scoreboards.BoardManager;
import dev.mruniverse.rigoxrftb.core.utils.scoreboards.TitleRunnable;
import dev.mruniverse.rigoxrftb.core.xseries.XEnchantment;
import dev.mruniverse.rigoxrftb.core.xseries.XMaterial;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public final class RigoxRFTB extends JavaPlugin {
    private FileStorage fileStorage;
    private boolean hasPAPI = false;
    private static RigoxRFTB instance;
    private Logger logger;
    private RigoxUtils rigoxUtils;
    private NMS nmsHandler;
    private ListenerUtil rigoxListeners;
    private BoardManager rigoxScoreboards;
    private GameManager rigoxGameManager;
    private DataStorage dataStorage;
    private PlayerRunnable runnable;
    private TitleRunnable titleRunnable = null;
    private KitLoader kitLoader;

    public ItemStack exitItem;
    public ItemStack kitRunner;
    public ItemStack kitBeast;
    public ItemStack beastHelmet;
    public ItemStack beastChestplate;
    public ItemStack beastLeggings;
    public ItemStack beastBoots;

    public Integer exitSlot;
    public Integer RunnerSlot;
    public Integer beastSlot;

    private final HashMap<UUID, PlayerManager> rigoxPlayers = new HashMap<>();
    private final HashMap<ItemStack, Integer> lobbyItems = new HashMap<>();
    private final HashMap<ItemStack, Integer> beastInventory = new HashMap<>();
    private final HashMap<ItemStack, ItemFunction> currentItem = new HashMap<>();

    @Override
    public void onDisable() {
        dataStorage.disableDatabase();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onEnable() {
        instance = this;
        BukkitRunnable runnable = new BukkitRunnable() {
        @Override
        public void run() {
            instance.logger = new Logger(instance);

            // * RigoxFiles Setup

            instance.fileStorage = new FileStorage(instance);
            instance.fileStorage.save(SaveMode.ALL);

            instance.hasPAPI = getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");
            String mode = instance.fileStorage.getControl(RigoxFiles.SETTINGS).getString("settings.pluginMode");
            if(mode == null) mode = "MULTI_ARENA";
            if(!mode.equalsIgnoreCase("BUNGEE_MODE")) {

                // * Listener Setup

                instance.rigoxListeners = new ListenerUtil(instance);
                instance.rigoxListeners.registerListeners();
                getListener().registerCommands();

                // * Utils Setup

                instance.rigoxUtils = new RigoxUtils(instance);
                instance.kitLoader = new KitLoader(instance);

                // * NMS Setup

                nmsSetup();

                // * Game Setup

                instance.rigoxGameManager = new GameManager(instance);
                instance.rigoxGameManager.loadChests();
                instance.rigoxGameManager.loadGames();

                FileConfiguration items = getStorage().getControl(RigoxFiles.ITEMS);
                ConfigurationSection section;
                // * Beast Items
                try {
                    section = items.getConfigurationSection("Playing.BeastInventory");
                    if (section == null) throw new Throwable("Can't found beast inventory section in items.yml");
                    for (String beastDefaultInv : section.getKeys(false)) {
                        String material = items.getString("Playing.BeastInventory." + beastDefaultInv + ".item");
                        if (material == null) material = "BED";
                        Optional<XMaterial> optionalXMaterial = XMaterial.matchXMaterial(material);
                        if (optionalXMaterial.isPresent()) {
                            XMaterial m = optionalXMaterial.get();
                            if (m.parseMaterial() != null) {
                                String itemName = items.getString("Playing.BeastInventory." + beastDefaultInv + ".name");
                                Integer slot = items.getInt("Playing.BeastInventory." + beastDefaultInv + ".slot");
                                List<String> lore = items.getStringList("Playing.BeastInventory." + beastDefaultInv + ".lore");
                                ItemStack item = getItem(m, itemName, lore);
                                if (items.get("Playing.BeastInventory." + beastDefaultInv + ".lore") != null) {
                                    item = getEnchantmentList(item, RigoxFiles.ITEMS, "Playing.BeastInventory." + beastDefaultInv + ".enchantments");
                                }
                                beastInventory.put(item, slot);
                            }
                        } else {
                            getLogs().error("Item: " + material + " doesn't exists.");
                        }
                    }
                } catch (Throwable throwable) {
                    getLogs().error("Can't load beast inventory items");
                    getLogs().error(throwable);
                }
                // * Beast Armor
                try {
                    String ItemMaterial, ItemName;
                    List<String> ItemLore;
                    ItemStack item;
                    XMaterial m;
                    Optional<XMaterial> optionalXMaterial;
                    ItemMaterial = items.getString("Playing.BeastArmor.Helmet.item");
                    ItemName = items.getString("Playing.BeastArmor.Helmet.name");
                    ItemLore = items.getStringList("Playing.BeastArmor.Helmet.lore");
                    if (ItemMaterial == null) ItemMaterial = "DIAMOND_HELMET";
                    optionalXMaterial = XMaterial.matchXMaterial(ItemMaterial);
                    if (optionalXMaterial.isPresent()) {
                        m = optionalXMaterial.get();
                        if (m.parseMaterial() != null) {
                            item = getItem(m, ItemName, ItemLore);
                            if (items.get("Playing.BeastArmor.Helmet.enchantments") != null) {
                                item = getEnchantmentList(item, RigoxFiles.ITEMS, "Playing.BeastArmor.Helmet.enchantments");
                            }
                            beastHelmet = item;
                        }
                    } else {
                        getLogs().error("Item: " + ItemMaterial + " doesn't exists.");
                    }
                    ItemMaterial = items.getString("Playing.BeastArmor.Chestplate.item");
                    ItemName = items.getString("Playing.BeastArmor.Chestplate.name");
                    ItemLore = items.getStringList("Playing.BeastArmor.Chestplate.lore");
                    if (ItemMaterial == null) ItemMaterial = "DIAMOND_CHESTPLATE";
                    optionalXMaterial = XMaterial.matchXMaterial(ItemMaterial);
                    if (optionalXMaterial.isPresent()) {
                        m = optionalXMaterial.get();
                        if (m.parseMaterial() != null) {
                            item = getItem(m, ItemName, ItemLore);
                            if (items.get("Playing.BeastArmor.Helmet.enchantments") != null) {
                                item = getEnchantmentList(item, RigoxFiles.ITEMS, "Playing.BeastArmor.Chestplate.enchantments");
                            }
                            beastChestplate = item;
                        }
                    } else {
                        getLogs().error("Item: " + ItemMaterial + " doesn't exists.");
                    }
                    ItemMaterial = items.getString("Playing.BeastArmor.Leggings.item");
                    ItemName = items.getString("Playing.BeastArmor.Leggings.name");
                    ItemLore = items.getStringList("Playing.BeastArmor.Leggings.lore");
                    if (ItemMaterial == null) ItemMaterial = "DIAMOND_LEGGINGS";
                    optionalXMaterial = XMaterial.matchXMaterial(ItemMaterial);
                    if (optionalXMaterial.isPresent()) {
                        m = optionalXMaterial.get();
                        if (m.parseMaterial() != null) {
                            item = getItem(m, ItemName, ItemLore);
                            if (items.get("Playing.BeastArmor.Helmet.enchantments") != null) {
                                item = getEnchantmentList(item, RigoxFiles.ITEMS, "Playing.BeastArmor.Leggings.enchantments");
                            }
                            beastLeggings = item;
                        }
                    } else {
                        getLogs().error("Item: " + ItemMaterial + " doesn't exists.");
                    }
                    ItemMaterial = items.getString("Playing.BeastArmor.Boots.item");
                    ItemName = items.getString("Playing.BeastArmor.Boots.name");
                    ItemLore = items.getStringList("Playing.BeastArmor.Boots.lore");
                    if (ItemMaterial == null) ItemMaterial = "DIAMOND_BOOTS";
                    optionalXMaterial = XMaterial.matchXMaterial(ItemMaterial);
                    if (optionalXMaterial.isPresent()) {
                        m = optionalXMaterial.get();
                        if (m.parseMaterial() != null) {
                            item = getItem(m, ItemName, ItemLore);
                            if (items.get("Playing.BeastArmor.Helmet.enchantments") != null) {
                                item = getEnchantmentList(item, RigoxFiles.ITEMS, "Playing.BeastArmor.Boots.enchantments");
                            }
                            beastBoots = item;
                        }
                    } else {
                        getLogs().error("Item: " + ItemMaterial + " doesn't exists.");
                    }
                } catch (Throwable throwable) {
                    getLogs().error("Can't load beast Default Armor");
                }

                // * Lobby Items
                try {
                    section = items.getConfigurationSection("lobby");
                    if (section == null) throw new Throwable("Can't found beast inventory section in items.yml");
                    for (String lItems : section.getKeys(false)) {
                        if (items.getBoolean("lobby." + lItems + ".toggle")) {
                            String material = items.getString("lobby." + lItems + ".item");
                            if (material == null) material = "BED";
                            Optional<XMaterial> optionalXMaterial = XMaterial.matchXMaterial(material);
                            XMaterial m;
                            if (optionalXMaterial.isPresent()) {
                                m = optionalXMaterial.get();
                                if (m.parseMaterial() != null) {
                                    String itemName = items.getString("lobby." + lItems + ".name");
                                    Integer slot = items.getInt("lobby." + lItems + ".slot");
                                    List<String> lore = items.getStringList("lobby." + lItems + ".lore");
                                    ItemStack item = getItem(m, itemName, lore);
                                    if (items.get("lobby." + lItems + ".enchantments") != null) {
                                        item = getEnchantmentList(item, RigoxFiles.ITEMS, "lobby." + lItems + ".enchantments");
                                    }
                                    lobbyItems.put(item, slot);
                                    currentItem.put(item, getCurrent(lItems));
                                }
                            } else {
                                getLogs().error("Item: " + material + " doesn't exists.");
                            }
                        }
                    }
                } catch (Throwable throwable) {
                    getLogs().error("Can't get lobby items on startup");
                    getLogs().error(throwable);
                }
                try {
                    String ItemMaterial, ItemName;
                    List<String> ItemLore;
                    int ItemSlot;
                    ItemStack item;
                    Optional<XMaterial> optionalXMaterial;
                    XMaterial m;
                    ItemMaterial = items.getString("InGame.RunnerKit.item");
                    ItemName = items.getString("InGame.RunnerKit.name");
                    ItemLore = items.getStringList("InGame.RunnerKit.lore");
                    ItemSlot = items.getInt("InGame.RunnerKit.slot");
                    if (ItemMaterial == null) ItemMaterial = "PAPER";
                    optionalXMaterial = XMaterial.matchXMaterial(ItemMaterial);
                    if (optionalXMaterial.isPresent()) {
                        m = optionalXMaterial.get();
                        if (m.parseMaterial() != null) {
                            item = getItem(m, ItemName, ItemLore);
                            if (items.get("InGame.RunnerKit.enchantments") != null) {
                                item = getEnchantmentList(item, RigoxFiles.ITEMS, "InGame.RunnerKit.enchantments");
                            }
                            kitRunner = item;
                            RunnerSlot = ItemSlot;
                            currentItem.put(item, ItemFunction.KIT_RUNNERS);
                        }
                    } else {
                        getLogs().error("Item: " + ItemMaterial + " doesn't exists.");
                    }
                    ItemMaterial = items.getString("InGame.BeastKit.item");
                    ItemName = items.getString("InGame.BeastKit.name");
                    ItemLore = items.getStringList("InGame.BeastKit.lore");
                    ItemSlot = items.getInt("InGame.BeastKit.slot");
                    if (ItemMaterial == null) ItemMaterial = "PAPER";
                    optionalXMaterial = XMaterial.matchXMaterial(ItemMaterial);
                    if (optionalXMaterial.isPresent()) {
                        m = optionalXMaterial.get();
                        if (m.parseMaterial() != null) {
                            item = getItem(m, ItemName, ItemLore);
                            if (items.get("InGame.BeastKit.enchantments") != null) {
                                item = getEnchantmentList(item, RigoxFiles.ITEMS, "InGame.BeastKit.enchantments");
                            }
                            kitBeast = item;
                            beastSlot = ItemSlot;
                            currentItem.put(item, ItemFunction.KIT_BEASTS);
                        }
                    } else {
                        getLogs().error("Item: " + ItemMaterial + " doesn't exists.");
                    }
                    ItemMaterial = items.getString("InGame.Exit.item");
                    ItemName = items.getString("InGame.Exit.name");
                    ItemLore = items.getStringList("InGame.Exit.lore");
                    ItemSlot = items.getInt("InGame.Exit.slot");
                    if (ItemMaterial == null) ItemMaterial = "RED_BED";
                    optionalXMaterial = XMaterial.matchXMaterial(ItemMaterial);
                    if (optionalXMaterial.isPresent()) {
                        m = optionalXMaterial.get();
                        if (m.parseMaterial() != null) {
                            item = getItem(m, ItemName, ItemLore);
                            if (items.get("InGame.Exit.enchantments") != null) {
                                item = getEnchantmentList(item, RigoxFiles.ITEMS, "InGame.Exit.enchantments");
                            }
                            exitItem = item;
                            exitSlot = ItemSlot;
                            currentItem.put(item, ItemFunction.EXIT_GAME);
                        }
                    } else {
                        getLogs().error("Item: " + ItemMaterial + " doesn't exists.");
                    }
                } catch (Throwable throwable) {
                    getLogs().error("Can't get game items on startup");
                    getLogs().error(throwable);
                }

                // * Data Storage

                instance.dataStorage = new DataStorage(instance);
                instance.dataStorage.loadDatabase();

                // * bStats

                BukkitMetrics bukkitMetrics = new BukkitMetrics(instance, 10282);
                getLogs().debug(String.format("Spigot metrics has been enabled &7(%s)", bukkitMetrics.isEnabled()));

                // * Rigox Updater

                if (getStorage().getControl(RigoxFiles.SETTINGS).getBoolean("settings.update-check")) {
                    RigoxUpdater updater = new RigoxUpdater(instance, 88817);
                    String updaterResult = updater.getUpdateResult();
                    String versionResult = updater.getVersionResult();
                    switch (updaterResult.toUpperCase()) {
                        case "UPDATED":
                            getLogs().info("&aYou're using latest version of PixelMOTD, You're Awesome!");
                            switch (versionResult.toUpperCase()) {
                                case "RED_PROBLEM":
                                    getLogs().info("&aRigoxRFTB can't connect to WiFi to check plugin version.");
                                    break;
                                case "PRE_ALPHA_VERSION":
                                    getLogs().info("&cYou are Running a &aPre Alpha version&c, it is normal to find several errors, please report these errors so that they can be solved. &eWARNING: &cI (MrUniverse) recommend a Stable version, PreAlpha aren't stable versions!");
                                    break;
                                case "ALPHA_VERSION":
                                    getLogs().info("&bYou are Running a &aAlpha version&b, it is normal to find several errors, please report these errors so that they can be solved.");
                                    break;
                                case "RELEASE":
                                    getLogs().info("&aYou are Running a &bRelease Version&a, this is a stable version, awesome!");
                                    break;
                                case "PRE_RELEASE":
                                    getLogs().info("&aYou are Running a &bPreRelease Version&a, this is a stable version but is not the final version or don't have finished all things of the final version, but is a stable version,awesome!");
                                    break;
                                default:
                                    break;
                            }
                            break;
                        case "NEW_VERSION":
                            getLogs().info("&aA new update is available: &bhttps://www.spigotmc.org/resources/88817/");
                            break;
                        case "BETA_VERSION":
                            getLogs().info("&aYou are Running a Pre-Release version, please report bugs ;)");
                            break;
                        case "RED_PROBLEM":
                            getLogs().info("&aRigoxRFTB can't connect to WiFi to check plugin version.");
                            break;
                        case "ALPHA_VERSION":
                            getLogs().info("&bYou are Running a &aAlpha version&b, it is normal to find several errors, please report these errors so that they can be solved.");
                            break;
                        case "PRE_ALPHA_VERSION":
                            getLogs().info("&cYou are Running a &aPre Alpha version&c, it is normal to find several errors, please report these errors so that they can be solved. &eWARNING: &cI (MrUniverse) recommend a Stable version, PreAlpha aren't stable versions!");
                            break;
                        default:
                            break;
                    }
                }


                // * Scoreboard Setup

                instance.rigoxScoreboards = new BoardManager(instance);

                // * Tasks
                instance.runnable = new PlayerRunnable(instance);
                if (getStorage().getControl(RigoxFiles.SCOREBOARD).getBoolean("scoreboards.animatedTitle.toggle")) {
                    instance.titleRunnable = new TitleRunnable(instance);
                    getServer().getScheduler().runTaskTimerAsynchronously(instance, instance.titleRunnable, 0L, getStorage().getControl(RigoxFiles.SCOREBOARD).getLong("scoreboards.animatedTitle.repeatTime"));
                }
                getServer().getScheduler().runTaskTimerAsynchronously(instance, instance.runnable, 0L, 20L);
            }
        }
        };
        runnable.runTaskLater(this, 1L);
    }
    public ItemStack getItem(XMaterial xItem,String name,List<String> lore) {
        ItemStack itemToReturn = xItem.parseItem();
        if(itemToReturn != null) {
            ItemMeta ReturnMeta = itemToReturn.getItemMeta();
            if(ReturnMeta != null) {
                ReturnMeta.setDisplayName(TextUtilities.recolor(name));
                ReturnMeta.setLore(TextUtilities.recolorLore(lore));
                itemToReturn.setItemMeta(ReturnMeta);
                return itemToReturn;
            }
            return itemToReturn;
        }
        return getNMSHandler().getItemStack(xItem.parseMaterial(), TextUtilities.recolor(name), TextUtilities.recolorLore(lore));
    }
    @SuppressWarnings("ConstantConditions")
    public ItemStack getEnchantmentList(ItemStack item, RigoxFiles fileOfPath, String path) {
        for(String enchants : getStorage().getControl(fileOfPath).getStringList(path)) {
            try {
                item = XEnchantment.addEnchantFromString(item, enchants);
            } catch(Throwable throwable) {
                getLogs().error("Can't add Enchantment: " + enchants);
            }
        }
        return item;
    }
    @SuppressWarnings("unused")
    private World getWorld() {
        if(getServer().getWorlds().size() != 0) {
            return getServer().getWorlds().get(0);
        }
        return null;
    }
    public ItemFunction getCurrent(ItemStack item) {
        return currentItem.get(item);
    }
    private ItemFunction getCurrent(String path) {
        if(path.equalsIgnoreCase("gameSelector")) {
            return ItemFunction.GAME_SELECTOR;
        }
        if(path.equalsIgnoreCase("Shop")) {
            return ItemFunction.SHOP;
        }
        if(path.equalsIgnoreCase("PlayerSettings")) {
            return ItemFunction.PLAYER_SETTINGS;
        }
        if(path.equalsIgnoreCase("LobbySelector")) {
            return ItemFunction.LOBBY_SELECTOR;
        }
        return ItemFunction.EXIT_LOBBY;
    }
    private void nmsSetup() {
        try {
            nmsHandler = (NMS) Class.forName("dev.mruniverse.rigoxrftb.nms." + NMSenum.getCurrent() + ".NMSHandler").getConstructor(new Class[0]).newInstance(new Object[0]);
            getLogs().info("Successfully connected with version: " + NMSenum.getCurrent() + ", the plugin can work correctly. If you found an issue please report to the developer.");
        }catch (Throwable throwable) {
            getLogs().error("Can't initialize NMS, unsupported version: " + NMSenum.getCurrent());
            getLogs().error(throwable);
        }
    }
    public HashMap<ItemStack,Integer> getBeastInventory() { return beastInventory; }
    public HashMap<ItemStack,Integer> getLobbyItems() { return lobbyItems; }
    public KitLoader getKitLoader() { return kitLoader; }
    public int getSlot(ItemStack itemStack) { return lobbyItems.get(itemStack); }
    public NMS getNMSHandler() { return nmsHandler; }
    public boolean hasPAPI() { return hasPAPI; }
    public ListenerUtil getListener() { return rigoxListeners; }
    public static RigoxRFTB getInstance() { return instance; }
    public FileStorage getStorage() { return fileStorage; }
    public GameManager getGameManager() { return rigoxGameManager; }
    public DataStorage getData() { return dataStorage; }
    public Logger getLogs() { return logger; }
    public RigoxUtils getUtils() { return rigoxUtils; }
    public PlayerRunnable getRunnable() { return runnable; }
    public TitleRunnable getTitleRunnable() { return titleRunnable; }
    public BoardManager getScoreboards() { return rigoxScoreboards; }
    public void addPlayer(Player player){
        if(!existPlayer(player)) {
            rigoxPlayers.put(player.getUniqueId(),new PlayerManager(player,this));
        }
    }
    public void getItems(GameEquip gameEquipment, Player player) {
        switch (gameEquipment) {
            case BEAST_KIT:
                String kitID = getPlayerData(player.getUniqueId()).getSelectedKit();
                if(kitID.equalsIgnoreCase("NONE")) return;
                KitInfo kitInfo = getKitLoader().getKitsUsingID(KitType.BEAST).get(kitID);
                if(kitInfo == null) return;
                for(Map.Entry<ItemStack,Integer> data : kitInfo.getInventoryItems().entrySet()) {
                    player.getInventory().setItem(data.getValue(),data.getKey());
                }
                if(kitInfo.getArmor(ArmorPart.HELMET) != null) player.getInventory().setHelmet(kitInfo.getArmor(ArmorPart.HELMET));
                if(kitInfo.getArmor(ArmorPart.CHESTPLATE) != null) player.getInventory().setHelmet(kitInfo.getArmor(ArmorPart.CHESTPLATE));
                if(kitInfo.getArmor(ArmorPart.LEGGINGS) != null) player.getInventory().setHelmet(kitInfo.getArmor(ArmorPart.LEGGINGS));
                if(kitInfo.getArmor(ArmorPart.BOOTS) != null) player.getInventory().setHelmet(kitInfo.getArmor(ArmorPart.BOOTS));
                return;
            case RUNNER_KIT:
            default:
                String runnerID = getPlayerData(player.getUniqueId()).getSelectedKit();
                if(runnerID.equalsIgnoreCase("NONE")) return;
                KitInfo runnerInfo = getKitLoader().getKitsUsingID(KitType.RUNNER).get(runnerID);
                if(runnerInfo == null) return;
                for(Map.Entry<ItemStack,Integer> data : runnerInfo.getInventoryItems().entrySet()) {
                    player.getInventory().setItem(data.getValue(),data.getKey());
                }
                if(runnerInfo.getArmor(ArmorPart.HELMET) != null) player.getInventory().setHelmet(runnerInfo.getArmor(ArmorPart.HELMET));
                if(runnerInfo.getArmor(ArmorPart.CHESTPLATE) != null) player.getInventory().setHelmet(runnerInfo.getArmor(ArmorPart.CHESTPLATE));
                if(runnerInfo.getArmor(ArmorPart.LEGGINGS) != null) player.getInventory().setHelmet(runnerInfo.getArmor(ArmorPart.LEGGINGS));
                if(runnerInfo.getArmor(ArmorPart.BOOTS) != null) player.getInventory().setHelmet(runnerInfo.getArmor(ArmorPart.BOOTS));
        }
    }
    public boolean existPlayer(Player player) { return rigoxPlayers.containsKey(player.getUniqueId()); }
    public void removePlayer(Player player) { rigoxPlayers.remove(player.getUniqueId()); }
    public HashMap<UUID, PlayerManager> getRigoxPlayers() { return rigoxPlayers; }
    public PlayerManager getPlayerData(UUID uuid) { return rigoxPlayers.get(uuid); }
}