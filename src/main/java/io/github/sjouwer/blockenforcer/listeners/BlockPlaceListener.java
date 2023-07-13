package io.github.sjouwer.blockenforcer.listeners;

import io.github.sjouwer.blockenforcer.Config;
import io.github.sjouwer.blockenforcer.handlers.NoteBlockHandler;
import io.github.sjouwer.blockenforcer.handlers.RedstoneBlockHandler;
import io.github.sjouwer.blockenforcer.handlers.TechnicalBlockHandler;
import io.github.sjouwer.blockenforcer.handlers.BiomeHandler;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Fence;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.meta.ItemMeta;

public class BlockPlaceListener implements Listener {

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        Block placedBlock = event.getBlockPlaced();

        switch (placedBlock.getType()) {
            case NOTE_BLOCK:
                NoteBlockHandler.forceNoteBlockNBTState(event.getItemInHand(), placedBlock);
                break;

            case TRIPWIRE_HOOK:
                RedstoneBlockHandler.forceHookNBTState(event.getItemInHand(), placedBlock);
                break;

            case REDSTONE_WIRE:
                RedstoneBlockHandler.forceRedstoneNBTState(event.getItemInHand(), placedBlock);
                break;

            case FLOWER_POT:
                TechnicalBlockHandler.placePottedPlant(event.getItemInHand(), placedBlock);
                break;

            case ICE:
                TechnicalBlockHandler.placeFrostedIce(event.getItemInHand(), placedBlock);
                break;

            case PISTON:
            case STICKY_PISTON:
                TechnicalBlockHandler.placePistonHead(event.getItemInHand(), placedBlock);
                break;

            default:
        }

        ItemMeta meta = event.getItemInHand().getItemMeta();
        if (meta != null && meta.hasLore()) {
            BiomeHandler.changeBiome(event.getBlockPlaced(), meta, event.getPlayer());
        }
    }
}
