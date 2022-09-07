package io.github.sjouwer.blockenforcer.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemUtil {
    private ItemUtil() {
    }

    public static void nameItemStack(ItemStack item, String name) {
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(name);
            item.setItemMeta(itemMeta);
        }
    }

    public static void addLore(ItemStack item, String lore) {
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta != null) {
            List<String> loreList;
            if (itemMeta.hasLore()) {
                loreList = itemMeta.getLore();
            }
            else {
                loreList = new ArrayList<>();
            }
            loreList.add(lore);
            itemMeta.setLore(loreList);
            item.setItemMeta(itemMeta);
        }
    }
}
