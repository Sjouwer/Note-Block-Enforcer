package io.github.sjouwer.blockenforcer.handlers;

import io.github.sjouwer.blockenforcer.utils.BlockUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class BlockPlaceHandler {
    private BlockPlaceHandler() {
    }

    public static Block placeBlock(PlayerInteractEvent event) {
        return BlockPlaceHandler.placeBlock(event, null);
    }

    public static Block placeBlock(PlayerInteractEvent event, Block blockOverride) {
        Block clickedBlock = event.getClickedBlock();
        ItemStack item = event.getItem();
        if (clickedBlock == null || item == null || event.getHand() == null) {
            return null;
        }

        Block placementBlock = blockOverride == null ? getPlacementBlock(clickedBlock, event.getBlockFace()) : blockOverride;
        if (placementBlock == null) {
            return null;
        }

        Material blockMaterial = BlockUtil.convertToBlockMaterial(item);
        if (blockMaterial == null) {
            return null;
        }

        BlockState replacedBlockState = placementBlock.getState();
        placementBlock.setType(blockMaterial, false);
        BlockPlaceEvent blockPlaceEvent = new BlockPlaceEvent(placementBlock, replacedBlockState, clickedBlock, item, event.getPlayer(), true, event.getHand());
        Bukkit.getPluginManager().callEvent(blockPlaceEvent);
        if (blockPlaceEvent.isCancelled()) {
            placementBlock.setBlockData(replacedBlockState.getBlockData(), false);
            return null;
        }

        return placementBlock;
    }

    private static Block getPlacementBlock(Block clickedBlock, BlockFace face) {
        if (BlockUtil.isReplaceable(clickedBlock)) {
            return clickedBlock;
        }

        Block relativeBlock = clickedBlock.getRelative(face);
        if (BlockUtil.isReplaceable(relativeBlock)) {
            return relativeBlock;
        }

        return null;
    }
}
