package io.github.sjouwer.blockenforcer.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemUtil {
    private static final List<Material> musicDiscs = Arrays.asList(
            Material.MUSIC_DISC_11,
            Material.MUSIC_DISC_13,
            Material.MUSIC_DISC_BLOCKS,
            Material.MUSIC_DISC_CAT,
            Material.MUSIC_DISC_CHIRP,
            Material.MUSIC_DISC_FAR,
            Material.MUSIC_DISC_MALL,
            Material.MUSIC_DISC_MELLOHI,
            Material.MUSIC_DISC_STAL,
            Material.MUSIC_DISC_STRAD,
            Material.MUSIC_DISC_WAIT,
            Material.MUSIC_DISC_WARD
    );

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

    public static boolean isMusicDisc(ItemStack item) {
        return musicDiscs.contains(item.getType());
    }
}
