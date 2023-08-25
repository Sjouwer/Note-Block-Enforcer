package io.github.sjouwer.blockenforcer.listeners;

import io.github.sjouwer.blockenforcer.handlers.TechnicalBlockHandler;
import io.github.sjouwer.blockenforcer.handlers.BiomeHandler;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BlockPlaceListener implements Listener {

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        Block placedBlock = event.getBlockPlaced();
        ItemStack handItem = event.getItemInHand();

        switch (placedBlock.getType()) {
            case FLOWER_POT:
                TechnicalBlockHandler.placePottedPlant(handItem, placedBlock);
                break;

            case ICE:
                TechnicalBlockHandler.placeFrostedIce(handItem, placedBlock);
                break;

            case PISTON:
            case STICKY_PISTON:
                TechnicalBlockHandler.placePistonHead(handItem, placedBlock);
                break;

            default:
        }

        ItemMeta meta = event.getItemInHand().getItemMeta();
        if (meta != null && meta.hasLore()) {
            BiomeHandler.changeBiome(placedBlock, meta, event.getPlayer());
        }
    }
}
