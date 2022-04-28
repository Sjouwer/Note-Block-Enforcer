package io.github.sjouwer.noteblockenforcer.events;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class NoteBlockInteractEvent implements Listener {
    @EventHandler
    public void placeNoteBlockInstead(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.NOTE_BLOCK) {
            ItemStack item = event.getItem();
            Block relativeBlock = event.getClickedBlock().getRelative(event.getBlockFace());
            if (isPlaceable(relativeBlock) && item != null && item.getType().isBlock() && event.getHand() != null) {
                BlockState replacedBlockState = relativeBlock.getState();
                relativeBlock.setType(item.getType());

                BlockPlaceEvent blockPlaceEvent = new BlockPlaceEvent(relativeBlock, replacedBlockState, event.getClickedBlock(), item, event.getPlayer(), true, event.getHand());
                Bukkit.getPluginManager().callEvent(blockPlaceEvent);
                if (blockPlaceEvent.isCancelled()) {
                    relativeBlock.setBlockData(replacedBlockState.getBlockData());
                }
            }
            event.setCancelled(true);
        }
    }

    private boolean isPlaceable(Block block) {
        if (block.isEmpty() || block.isLiquid()) {
            return true;
        }

        switch (block.getType()) {
            case GRASS:
            case TALL_GRASS:
            case FERN:
            case LARGE_FERN:
            case SEAGRASS:
            case TALL_SEAGRASS:
            case DEAD_BUSH:
            case VINE:
            case FIRE:
                return true;
            default:
                return false;
        }
    }
}
