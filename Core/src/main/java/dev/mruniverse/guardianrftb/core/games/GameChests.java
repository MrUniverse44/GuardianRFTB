package dev.mruniverse.guardianrftb.core.games;

import dev.mruniverse.guardianrftb.core.GuardianRFTB;
import dev.mruniverse.guardianrftb.core.enums.GuardianFiles;
import dev.mruniverse.guardianrftb.core.utils.TextUtilities;
import dev.mruniverse.guardianrftb.core.xseries.XMaterial;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class GameChests {
    private final GuardianRFTB plugin;
    private String name;
    private final String chestID;
    private final HashMap<ItemStack, Integer> chestItems = new HashMap<>();
    private Inventory chestInventory;
    public GameChests(GuardianRFTB main, String chestName) {
        plugin = main;
        chestID = chestName;
        createInv();
        loadItems();
    }
    private void createInv() {
        String invName = plugin.getStorage().getControl(GuardianFiles.CHESTS).getString("chests." + chestID + ".inventoryName");

        if(invName == null) invName = "&8Chest";

        invName = ChatColor.translateAlternateColorCodes('&',invName);

        int rows = getRows(plugin.getStorage().getControl(GuardianFiles.CHESTS).getInt("chests.armor.inventoryRows"));

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
    private void pasteItems() {
        chestInventory.clear();
        for(ItemStack item : chestItems.keySet()) {
            chestInventory.setItem(getSlot(item),item);
        }
    }
    private void loadItems() {
        FileConfiguration loadConfig = plugin.getStorage().getControl(GuardianFiles.CHESTS);
        String path = "chests." + chestID + ".items.";
        try {
            ConfigurationSection section = loadConfig.getConfigurationSection("chests." + chestID + ".items");
            if(section == null) throw new Throwable("Can't found beast inventory section in chests.yml");
            for (String item : section.getKeys(false)) {
                String material = loadConfig.getString(path + item + ".item");
                if(material == null) material = "BEDROCK";
                Optional<XMaterial> optionalXMaterial = XMaterial.matchXMaterial(material);
                XMaterial m;
                if(optionalXMaterial.isPresent()) {
                    m = optionalXMaterial.get();
                    if (m.parseItem() != null) {
                        String itemName = loadConfig.getString(path + item + ".name");
                        Integer slot = loadConfig.getInt(path + item + ".slot");
                        List<String> lore = loadConfig.getStringList(path + item + ".lore");
                        if(itemName == null) itemName = "&e<Unknown Name>";
                        ItemStack itLoad = m.parseItem();
                        if(itLoad != null) {
                            ItemMeta ReturnMeta = itLoad.getItemMeta();
                            if(ReturnMeta != null) {
                                ReturnMeta.setDisplayName(TextUtilities.recolor(itemName));
                                ReturnMeta.setLore(TextUtilities.recolorLore(lore));
                                itLoad.setItemMeta(ReturnMeta);
                            }
                        } else {
                            itLoad = plugin.getNMSHandler().getItemStack(m.parseMaterial(), TextUtilities.recolor(itemName), TextUtilities.recolorLore(lore));
                        }
                        if(loadConfig.get(path + item + ".enchantments") != null) {
                            itLoad = plugin.getEnchantmentList(itLoad, GuardianFiles.CHESTS,path + item + ".enchantments");
                        }
                        chestItems.put(itLoad,slot);
                    }
                } else {
                    plugin.getLogs().error("Item: " + material + " doesn't exists.");
                }
            }
            pasteItems();
        } catch (Throwable throwable) {
            plugin.getLogs().error("Can't get chests items on startup");
            plugin.getLogs().error(throwable);
        }
    }
    public int getSlot(ItemStack item) { return chestItems.get(item); }
    public Inventory getInventory() {
        pasteItems();
        return chestInventory;
    }
    public void setName(String newName) { name = newName; }
    public String getName() { return name; }


}
