package dev.mruniverse.rigoxrftb.core.kits;

import dev.mruniverse.rigoxrftb.core.RigoxRFTB;
import dev.mruniverse.rigoxrftb.core.enums.RigoxFiles;
import dev.mruniverse.rigoxrftb.core.games.Game;
import dev.mruniverse.rigoxrftb.core.utils.TextUtilities;
import dev.mruniverse.rigoxrftb.core.xseries.XMaterial;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class KitMenu {

    private final RigoxRFTB plugin;
    private final KitType mode;
    private final Player player;
    private String name;
    private Inventory chestInventory;
    public KitMenu(RigoxRFTB main,KitType kitMode,Player player) {
        plugin = main;
        mode = kitMode;
        this.player = player;
        createInv();
    }
    private void createInv() {
        String invName = plugin.getStorage().getControl(RigoxFiles.MENUS).getString("menus.kits.inventoryName");

        if(invName == null) invName = "&8Kits";

        invName = ChatColor.translateAlternateColorCodes('&',invName);

        int rows = getRows(plugin.getStorage().getControl(RigoxFiles.MENUS).getInt("menus.kits.inventoryRows"));

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
    public HashMap<ItemStack,String> getItems() {
        HashMap<ItemStack,String> kits = new HashMap<>();
        String blockedMaterial = plugin.getStorage().getControl(RigoxFiles.MENUS).getString("menus.kits.blocked-item.item");
        String blockedName = plugin.getStorage().getControl(RigoxFiles.MENUS).getString("menus.kits.blocked-item.name");
        List<String> blockedLore = plugin.getStorage().getControl(RigoxFiles.MENUS).getStringList("menus.kits.blocked-item.lore");
        if(blockedMaterial == null) blockedMaterial = "STAINED_GLASS_PANE:14";
        if(blockedName == null) blockedName = "&c&nKit: %kit_name%";
        for(Map.Entry<String, KitInfo> kitData : plugin.getKitLoader().getKits(mode).entrySet()) {
            if(plugin.getPlayerData(player.getUniqueId()).getKits().contains(kitData.getValue().getID())) {
                ItemStack kitItem = kitData.getValue().getKitItem();
                kits.put(kitItem,kitData.getKey());
            } else {
                ItemStack item = getItem(blockedMaterial,getKitName(blockedName,kitData.getValue()),getLore(blockedLore,kitData.getValue()));
                if(item != null) {
                    kits.put(item,kitData.getKey());
                }
            }
        }
        return kits;
    }
    private void pasteItems() {
        chestInventory.clear();
        int slot = 0;
        int maxSlot = chestInventory.getSize();
        String blockedMaterial = plugin.getStorage().getControl(RigoxFiles.MENUS).getString("menus.kits.blocked-item.item");
        String blockedName = plugin.getStorage().getControl(RigoxFiles.MENUS).getString("menus.kits.blocked-item.name");
        List<String> blockedLore = plugin.getStorage().getControl(RigoxFiles.MENUS).getStringList("menus.kits.blocked-item.lore");
        if(blockedMaterial == null) blockedMaterial = "STAINED_GLASS_PANE:14";
        if(blockedName == null) blockedName = "&c&nKit: %kit_name%";
        for(Map.Entry<String, KitInfo> kitData : plugin.getKitLoader().getKits(mode).entrySet()) {
            if(slot != maxSlot) {
                if(plugin.getPlayerData(player.getUniqueId()).getKits().contains(kitData.getValue().getID())) {
                    ItemStack kitItem = kitData.getValue().getKitItem();
                    chestInventory.setItem(slot, kitItem);
                } else {
                    ItemStack item = getItem(blockedMaterial,getKitName(blockedName,kitData.getValue()),getLore(blockedLore,kitData.getValue()));
                    if(item != null) {
                        chestInventory.setItem(slot, item);
                    }
                }
            }
            slot++;
        }
    }
    public String getKitName(String name,KitInfo kitInfo) {
        name = name.replace("%kit_name%",kitInfo.getName())
                .replace("%name%",kitInfo.getName())
                .replace("%kit_price%",kitInfo.getPrice() + "")
                .replace("%price%",kitInfo.getPrice() + "");
        return name;
    }
    public List<String> getLore(List<String> lore, KitInfo kitInfo) {
        List<String> newLore = new ArrayList<>();
        for(String line : lore) {
            newLore.add(line.replace("%kit_name%",kitInfo.getName()).replace("%price%",kitInfo.getPrice() + ""));
        }
        return newLore;
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
        pasteItems();
        return chestInventory;
    }
    public void setName(String newName) { name = newName; }
    public String getName() { return name; }
}
