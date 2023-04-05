package io.github.sjouwer.blockenforcer.listeners;

import io.github.sjouwer.blockenforcer.handlers.NoteBlockHandler;
import io.github.sjouwer.blockenforcer.handlers.RedstoneBlockHandler;
import io.github.sjouwer.blockenforcer.handlers.TechnicalBlockHandler;
import io.github.sjouwer.blockenforcer.handlers.BiomeHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.meta.ItemMeta;

public class BlockPlaceListener implements Listener {

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        switch (event.getBlockPlaced().getType()) {
            case NOTE_BLOCK:
                NoteBlockHandler.forceNoteBlockNBTState(event.getItemInHand(), event.getBlockPlaced());
                break;

            case TRIPWIRE_HOOK:
                RedstoneBlockHandler.forceHookNBTState(event.getItemInHand(), event.getBlockPlaced());
                break;

            case REDSTONE_WIRE:
                RedstoneBlockHandler.forceRedstoneNBTState(event.getItemInHand(), event.getBlockPlaced());
                break;

            case FLOWER_POT:
                TechnicalBlockHandler.placePottedPlant(event.getItemInHand(), event.getBlockPlaced());
                break;

            case ICE:
                TechnicalBlockHandler.placeFrostedIce(event.getItemInHand(), event.getBlockPlaced());
                break;

            case PISTON:
            case STICKY_PISTON:
                TechnicalBlockHandler.placePistonHead(event.getItemInHand(), event.getBlockPlaced());
                break;

            default:
        }

        ItemMeta meta = event.getItemInHand().getItemMeta();
        if (meta != null && meta.hasLore()) {
            BiomeHandler.changeBiome(event.getBlockPlaced(), meta, event.getPlayer());
        }
    }
}
