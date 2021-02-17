package dev.mruniverse.rigoxrftb.core.kits;

import dev.mruniverse.rigoxrftb.core.RigoxRFTB;
import dev.mruniverse.rigoxrftb.core.enums.RigoxFiles;
import dev.mruniverse.rigoxrftb.core.xseries.XMaterial;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class KitInfo {
    private final RigoxRFTB plugin;
    private final KitType type;
    private final String name;
    private HashMap<ItemStack, Integer> inventoryItems;
    private ItemStack kitItem = null;
    private ItemStack helmet = null;
    private ItemStack chestplate = null;
    private ItemStack leggings = null;
    private ItemStack boots = null;
    private final int price;
    public KitInfo(RigoxRFTB main,KitType kitType,String name) {
        this.name = name;
        inventoryItems = new HashMap<>();
        plugin = main;
        type = kitType;
        loadArmor();
        loadKitItem();
        loadInventory();
        price = plugin.getStorage().getControl(RigoxFiles.KITS).getInt(getPath() + ".KitInfo.price");
    }
    public void loadKitItem() {
        FileConfiguration items = plugin.getStorage().getControl(RigoxFiles.KITS);
        String material = items.getString(getPath() + ".KitInfo.item");
        if(material == null) return;
        if(material.equalsIgnoreCase("DISABLE")) return;
        Optional<XMaterial> optionalXMaterial = XMaterial.matchXMaterial(material);
        if(!optionalXMaterial.isPresent()) return;
        XMaterial m = optionalXMaterial.get();
        if (m.parseMaterial() == null) return;
        String itemName = items.getString(getPath() + ".KitInfo.name");
        List<String> lore = items.getStringList(getPath() + ".KitInfo.lore");
        ItemStack returnItem = plugin.getItem(m,itemName,lore);
        if(items.get(getPath() + ".KitInfo.enchantments") == null) kitItem = returnItem;
        kitItem = plugin.getEnchantmentList(returnItem,RigoxFiles.KITS,getPath() + ".KitInfo.enchantments");
    }
    public void loadInventory() {
        try {
            inventoryItems = new HashMap<>();
            FileConfiguration items = plugin.getStorage().getControl(RigoxFiles.KITS);
            ConfigurationSection section = plugin.getStorage().getControl(RigoxFiles.KITS).getConfigurationSection(getPath() + ".Inventory");
            if (section == null) return;
            for (String itemInfo : section.getKeys(false)) {
                String material = items.getString(getPath() + ".Inventory." + itemInfo + ".item");
                if (material == null) material = "BEDROCK";
                Optional<XMaterial> optionalXMaterial = XMaterial.matchXMaterial(material);
                if (optionalXMaterial.isPresent()) {
                    XMaterial m = optionalXMaterial.get();
                    if (m.parseMaterial() != null) {
                        String itemName = items.getString(getPath() + ".Inventory." + itemInfo + ".name");
                        Integer slot = items.getInt(getPath() + ".Inventory." + itemInfo + ".slot");
                        List<String> lore = items.getStringList(getPath() + ".Inventory." + itemInfo + ".lore");
                        ItemStack item = plugin.getItem(m, itemName, lore);
                        if (items.get(getPath() + ".Inventory." + itemInfo + ".lore") != null) {
                            item = plugin.getEnchantmentList(item, RigoxFiles.KITS, getPath() + ".Inventory." + itemInfo + ".enchantments");
                        }
                        inventoryItems.put(item, slot);
                    }
                } else {
                    plugin.getLogs().error("Item: " + material + " doesn't exists.");
                }
            }
        }catch (Throwable throwable) {
            plugin.getLogs().error("Can't load inventory items of kit: " + name);
        }
    }

    public void loadArmor() {
        helmet = loadPart(ArmorPart.HELMET);
        chestplate = loadPart(ArmorPart.CHESTPLATE);
        leggings = loadPart(ArmorPart.LEGGINGS);
        boots = loadPart(ArmorPart.BOOTS);
    }

    private ItemStack loadPart(ArmorPart armorPart) {
        FileConfiguration items = plugin.getStorage().getControl(RigoxFiles.KITS);
        String material = items.getString(getPath() + ".Armor." + armorPart.getPartName() + ".item");
        if(material == null) return null;
        if(material.equalsIgnoreCase("DISABLE")) return null;
        Optional<XMaterial> optionalXMaterial = XMaterial.matchXMaterial(material);
        if(!optionalXMaterial.isPresent()) return null;
        XMaterial m = optionalXMaterial.get();
        if (m.parseMaterial() == null) return null;
        String itemName = items.getString(getPath() + ".Armor." + armorPart.getPartName() + ".name");
        List<String> lore = items.getStringList(getPath() + ".Armor." + armorPart.getPartName() + ".lore");
        ItemStack returnItem = plugin.getItem(m,itemName,lore);
        if(items.get(getPath() + ".Armor." + armorPart.getPartName() + ".enchantments") == null) return returnItem;
        return plugin.getEnchantmentList(returnItem,RigoxFiles.KITS,getPath() + ".Armor." + armorPart.getPartName() + ".enchantments");
    }
    private String getPath() {
        switch (type) {
            case BEAST:
                return "beastKits." + name;
            case RUNNER:
            default:
                return "runnerKits." + name;
        }
    }

    public HashMap<ItemStack, Integer> getInventoryItems() {
        return inventoryItems;
    }

    public ItemStack getArmor(ArmorPart armorPart) {
        switch (armorPart) {
            case HELMET:
                return helmet;
            case LEGGINGS:
                return leggings;
            case BOOTS:
                return boots;
            default:
            case CHESTPLATE:
                return chestplate;
        }
    }

    public ItemStack getKitItem() {
        return kitItem;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public KitType getType() {
        return type;
    }
}
