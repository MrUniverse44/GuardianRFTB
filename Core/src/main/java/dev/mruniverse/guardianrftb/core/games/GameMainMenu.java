package dev.mruniverse.guardianrftb.core.games;

import dev.mruniverse.guardianrftb.core.enums.GuardianFiles;
import dev.mruniverse.guardianrftb.core.enums.MainAction;
import dev.mruniverse.guardianrftb.core.enums.SaveMode;
import dev.mruniverse.guardianrftb.core.utils.TextUtilities;
import dev.mruniverse.guardianrftb.core.xseries.XMaterial;
import dev.mruniverse.guardianrftb.core.GuardianRFTB;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class GameMainMenu {
    private final GuardianRFTB plugin;
    private String name;
    private HashMap<ItemStack,Integer> gameItems;
    private HashMap<ItemStack, MainAction> gameAction;
    private HashMap<Integer,ItemStack> fills;
    private Inventory chestInventory;
    private GameType openGameType;
    private boolean directGameType;
    public GameMainMenu(GuardianRFTB main) {
        plugin = main;
        gameItems = new HashMap<>();
        gameAction = new HashMap<>();
        fills = new HashMap<>();
        directGameType = getDirectStatus();
        openGameType = getGameType();
        createInv();
        loadItems();
    }
    public void updateInv(){
        gameItems = new HashMap<>();
        gameAction = new HashMap<>();
        fills = new HashMap<>();
        createInv();
        loadItems();
    }
    private void createInv() {
        String invName = plugin.getStorage().getControl(GuardianFiles.MENUS).getString("menus.gameMain.inventoryName");

        if(invName == null) invName = "&8Game Selector";

        invName = ChatColor.translateAlternateColorCodes('&',invName);

        int rows = getRows(plugin.getStorage().getControl(GuardianFiles.MENUS).getInt("menus.gameMain.inventoryRows"));

        chestInventory = plugin.getServer().createInventory(null,rows,invName);
    }
    private int getRows(int small) {
        if(small == 1) return 9;
        if(small == 2) return 18;
        if(small == 3) return 27;
        if(small == 4) return 36;
        if(small == 5) return 46;
        return 54;
    }
    private void addOnlyFill(ItemStack itemFill,String fillPath) {
        for(int slot : plugin.getStorage().getControl(GuardianFiles.MENUS).getIntegerList(MainAction.FILL.getPath() + "." + fillPath + ".list.values")) {
            fills.put(slot,itemFill);
        }
    }
    private void addIgnoreFill(ItemStack itemFill,String fillPath) {
        List<Integer> ignoreSlots = plugin.getStorage().getControl(GuardianFiles.MENUS).getIntegerList(MainAction.FILL.getPath() + "." + fillPath + ".list.values");
        for(int i = 0; i < chestInventory.getSize(); i++) {
            if(!ignoreSlots.contains(i)) fills.put(i,itemFill);
        }
    }
    public void execute(Player player,MainAction action) {
        switch (action) {
            case CLASSIC:
                player.openInventory(plugin.getGameManager().getGameMenu(GameType.CLASSIC).getInventory());
                return;
            case KILLER:
                player.openInventory(plugin.getGameManager().getGameMenu(GameType.KILLER).getInventory());
                return;
            case ISLAND_OF_THE_BEAST:
                player.openInventory(plugin.getGameManager().getGameMenu(GameType.ISLAND_OF_THE_BEAST).getInventory());
                return;
            case ISLAND_OF_THE_BEAST_DOUBLE_BEAST:
                player.openInventory(plugin.getGameManager().getGameMenu(GameType.ISLAND_OF_THE_BEAST_DOUBLE_BEAST).getInventory());
                return;
            case ISLAND_OF_THE_BEAST_KILLER:
                player.openInventory(plugin.getGameManager().getGameMenu(GameType.ISLAND_OF_THE_BEAST_KILLER).getInventory());
                return;
            case DOUBLE_BEAST:
                player.openInventory(plugin.getGameManager().getGameMenu(GameType.DOUBLE_BEAST).getInventory());
                return;
            case INFECTED:
                player.openInventory(plugin.getGameManager().getGameMenu(GameType.INFECTED).getInventory());
                return;
            default:
                plugin.getUtils().sendMessage(player,"&cSoon..");
        }
    }
    private void loadFills() {
        FileConfiguration menu = plugin.getStorage().getControl(GuardianFiles.MENUS);
        for(String fills : plugin.getStorage().getContent(GuardianFiles.MENUS,MainAction.FILL.getPath(),false)) {
            String name = menu.getString(MainAction.FILL.getPath() + "." + fills + ".name");
            String material = menu.getString(MainAction.FILL.getPath() + "." + fills + ".item");
            List<String> lore = menu.getStringList(MainAction.FILL.getPath() + "." + fills + ".lore");
            ItemStack item = getItem(material,name,lore);
            if(menu.get(MainAction.FILL.getPath() + "." + fills + ".list.type") == null) return;
            String check = menu.getString(MainAction.FILL.getPath() + "." + fills + ".list.type");
            gameAction.put(item,MainAction.FILL);
            if(check == null) check = "ONLY";
            if(check.equalsIgnoreCase("ONLY")) {
                addOnlyFill(item,fills);
            } else {
                addIgnoreFill(item,fills);
            }
        }
    }
    private void loadItems() {
        FileConfiguration menu = plugin.getStorage().getControl(GuardianFiles.MENUS);
        for(MainAction mainAction : MainAction.values()) {
            if(mainAction != MainAction.FILL && mainAction != MainAction.CUSTOM) {
                if(getMinigameStatus(menu,GuardianFiles.MENUS,SaveMode.MENUS,mainAction.getPath() + ".toggle")) {
                    String name = menu.getString(mainAction.getPath() + ".name");
                    String material = menu.getString(mainAction.getPath() + ".item");
                    List<String> lore = menu.getStringList(mainAction.getPath() + ".lore");
                    int slot = menu.getInt(mainAction.getPath() + ".slot");
                    ItemStack item = getItem(material, name, lore);
                    gameItems.put(item, slot);
                    gameAction.put(item, mainAction);
                }
            }
        }
        loadFills();
    }
    private boolean getMinigameStatus(FileConfiguration configuration, GuardianFiles CurrentGuardianFile, SaveMode CurrentSaveMode, String path) {
        if(configuration.contains(path)) {
            return configuration.getBoolean(path);
        } else {
            plugin.getStorage().getControl(CurrentGuardianFile).set(path,true);
            plugin.getStorage().save(CurrentSaveMode);
        }
        return true;
    }
    private boolean getDirectStatus() {
        FileConfiguration menu = plugin.getStorage().getControl(GuardianFiles.MENUS);
        if(!menu.contains("menus.gameMain.directOpen.toggle")) {
            plugin.getStorage().getControl(GuardianFiles.MENUS).set("menus.gameMain.directOpen.toggle",false);
            plugin.getStorage().save(SaveMode.MENUS);
        }
        return false;
    }
    private GameType getGameType() {
        FileConfiguration menu = plugin.getStorage().getControl(GuardianFiles.MENUS);
        if(!menu.contains("menus.gameMain.directOpen.gameType")) {
            plugin.getStorage().getControl(GuardianFiles.MENUS).set("menus.gameMain.directOpen.gameType",GameType.CLASSIC.toString().toUpperCase());
            plugin.getStorage().save(SaveMode.MENUS);
        } else {
            return GameType.valueOf(menu.getString("menus.gameMain.directOpen.gameType"));
        }
        return GameType.CLASSIC;
    }
    private void pasteItems() {
        chestInventory.clear();
        for(Map.Entry<ItemStack,Integer> data : gameItems.entrySet()) {
            chestInventory.setItem(data.getValue(),data.getKey());
        }
        for(Map.Entry<Integer,ItemStack> fills : fills.entrySet()) {
            chestInventory.setItem(fills.getKey(),fills.getValue());
        }
    }
    public HashMap<ItemStack,MainAction> getItems() {
        return gameAction;
    }
    private ItemStack getItem(String material,String itemName,List<String> itemLore) {
        Optional<XMaterial> optionalXMaterial = XMaterial.matchXMaterial(material);
        XMaterial m;
        if(optionalXMaterial.isPresent()) {
            m = optionalXMaterial.get();
            if (m.parseItem() != null) {
                ItemStack itemToReturn = m.parseItem();
                if(itemToReturn != null) {
                    ItemMeta ReturnMeta = itemToReturn.getItemMeta();
                    if(ReturnMeta != null) {
                        ReturnMeta.setDisplayName(TextUtilities.recolor(itemName));
                        ReturnMeta.setLore(TextUtilities.recolorLore(itemLore));
                        itemToReturn.setItemMeta(ReturnMeta);
                        return itemToReturn;
                    }
                    return itemToReturn;
                }
                return plugin.getNMSHandler().getItemStack(m.parseMaterial(), TextUtilities.recolor(itemName), TextUtilities.recolorLore(itemLore));
            }
            return null;
        }
        plugin.getLogs().error("Item: " + material + " doesn't exists.");
        return null;
    }
    public Inventory getInventory() {
        if(directGameType) {
            return plugin.getGameManager().getGameMenu(openGameType).getInventory();
        }
        pasteItems();
        return chestInventory;
    }
    public void setName(String newName) { name = newName; }
    public String getName() { return name; }
}

