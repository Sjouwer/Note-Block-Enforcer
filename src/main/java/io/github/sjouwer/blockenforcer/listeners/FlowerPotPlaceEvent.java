package io.github.sjouwer.blockenforcer.listeners;

import io.github.sjouwer.blockenforcer.utils.FlowerPotUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class FlowerPotPlaceEvent implements Listener {
    @EventHandler
    public void placePottedPlant(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        ItemStack item = event.getItemInHand();
        if (block.getType() != Material.FLOWER_POT || item.getType() != Material.FLOWER_POT) {
            return;
        }

        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) {
            return;
        }

        block.setType(FlowerPotUtil.convertToMaterial(itemMeta.getDisplayName()));
        block.getState().update(true);
    }
}
