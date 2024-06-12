package io.github.sjouwer.blockenforcer.handlers;

import io.github.sjouwer.blockenforcer.BlockEnforcer;
import io.github.sjouwer.blockenforcer.Config;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Gate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateHandler {
    private static final Map<Block, BlockData> BlocksToChange = new HashMap<>();
    private static final List<Block> updatedRedstone = new ArrayList<>();

    private UpdateHandler() {
    }

    private static void scheduleBlockChangeUpdate(Block blockToChange, Material material) {
        if ((material != null && blockToChange.getType() != material) || BlocksToChange.containsKey(blockToChange)) {
            return;
        }

        BlocksToChange.put(blockToChange, blockToChange.getBlockData().clone());

        if (BlocksToChange.size() == 1) {
            Bukkit.getScheduler().runTaskLater(BlockEnforcer.getPlugin(), () -> {

                BlocksToChange.forEach((block, data) -> {
                    if (block.getType() == data.getMaterial()) {
                        block.setBlockData(data, false);
                        block.getState().update(true, false);
                        if (material == Material.REDSTONE_WIRE) {
                            updatedRedstone.add(block);
                            updateRedstone(block);
                        }
                    }
                });

                BlocksToChange.clear();
            }, 1L);
        }
    }

    private static void updateRedstoneNow(Block block) {
        if (block.getType() != Material.REDSTONE_WIRE || updatedRedstone.contains(block)) {
            return;
        }

        if (updatedRedstone.size() < Config.MAX_REDSTONE_UPDATES) {
            block.getState().update(true, false);
            updatedRedstone.add(block);
            updateRedstone(block);
        }
    }

    public static void stopRedstoneChange(Block block) {
        updatedRedstone.clear();
        stopRedstoneChangeAround(block);
        scheduleBlockChangeUpdate(block.getRelative(BlockFace.DOWN), Material.REDSTONE_WIRE);
        stopRedstoneChangeAround(block.getRelative(BlockFace.DOWN));
        scheduleBlockChangeUpdate(block.getRelative(BlockFace.UP), Material.REDSTONE_WIRE);
        stopRedstoneChangeAround(block.getRelative(BlockFace.UP));
    }

    private static void stopRedstoneChangeAround(Block block) {
        scheduleBlockChangeUpdate(block.getRelative(BlockFace.NORTH), Material.REDSTONE_WIRE);
        scheduleBlockChangeUpdate(block.getRelative(BlockFace.EAST), Material.REDSTONE_WIRE);
        scheduleBlockChangeUpdate(block.getRelative(BlockFace.SOUTH), Material.REDSTONE_WIRE);
        scheduleBlockChangeUpdate(block.getRelative(BlockFace.WEST), Material.REDSTONE_WIRE);
    }

    public static void updateRedstone(Block block, boolean resetUpdates) {
        if (resetUpdates) {
            updatedRedstone.clear();
        }
        updateRedstone(block);
    }

    public static void updateRedstone(Block block) {
        updateRedstoneAround(block);
        updateRedstoneNow(block.getRelative(BlockFace.DOWN));
        updateRedstoneAround(block.getRelative(BlockFace.DOWN));
        updateRedstoneNow(block.getRelative(BlockFace.UP));
        updateRedstoneAround(block.getRelative(BlockFace.UP));
    }

    private static void updateRedstoneAround(Block block) {
        updateRedstoneNow(block.getRelative(BlockFace.NORTH));
        updateRedstoneNow(block.getRelative(BlockFace.EAST));
        updateRedstoneNow(block.getRelative(BlockFace.SOUTH));
        updateRedstoneNow(block.getRelative(BlockFace.WEST));
    }

    public static void stopTripwireChange(Block block) {
        scheduleBlockChangeUpdate(block.getRelative(BlockFace.NORTH), Material.TRIPWIRE);
        scheduleBlockChangeUpdate(block.getRelative(BlockFace.EAST), Material.TRIPWIRE);
        scheduleBlockChangeUpdate(block.getRelative(BlockFace.SOUTH), Material.TRIPWIRE);
        scheduleBlockChangeUpdate(block.getRelative(BlockFace.WEST), Material.TRIPWIRE);
    }

    public static void stopGateChange(Block block) {
        Block relativeBlock = block.getRelative(BlockFace.NORTH);
        if (relativeBlock.getBlockData() instanceof Gate) scheduleBlockChangeUpdate(relativeBlock, null);
        relativeBlock = block.getRelative(BlockFace.EAST);
        if (relativeBlock.getBlockData() instanceof Gate) scheduleBlockChangeUpdate(relativeBlock, null);
        relativeBlock = block.getRelative(BlockFace.SOUTH);
        if (relativeBlock.getBlockData() instanceof Gate) scheduleBlockChangeUpdate(relativeBlock, null);
        relativeBlock = block.getRelative(BlockFace.WEST);
        if (relativeBlock.getBlockData() instanceof Gate) scheduleBlockChangeUpdate(relativeBlock, null);
    }
}
