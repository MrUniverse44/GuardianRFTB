package dev.mruniverse.rigoxrftb.core.kits;

import dev.mruniverse.rigoxrftb.core.RigoxRFTB;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class KitInfo {
    private final RigoxRFTB plugin;
    private final KitType type;
    private final String name;
    private HashMap<Integer, ItemStack> inventoryItems;
    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private int price;
    public KitInfo(RigoxRFTB main,KitType kitType,String name) {
        this.name = name;
        plugin = main;
        type = kitType;
    }
    public void loadInventory() {

    }
    public void loadArmor() {

    }
    public String getName() {
        return name;
    }
    public KitType getType() {
        return type;
    }
}
