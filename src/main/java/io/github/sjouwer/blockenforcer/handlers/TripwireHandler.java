package io.github.sjouwer.blockenforcer.handlers;

import io.github.sjouwer.blockenforcer.BlockEnforcer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;

public class TripwireHandler {
    private TripwireHandler() {
    }

    public static void stopChange(Block block) {
        scheduleTripwireUpdate(block.getRelative(BlockFace.NORTH));
        scheduleTripwireUpdate(block.getRelative(BlockFace.EAST));
        scheduleTripwireUpdate(block.getRelative(BlockFace.SOUTH));
        scheduleTripwireUpdate(block.getRelative(BlockFace.WEST));
    }

    public static void scheduleTripwireUpdate(Block block) {
        if (block.getType() != Material.TRIPWIRE) {
            return;
        }

        BlockData data = block.getBlockData().clone();
        Bukkit.getScheduler().runTaskLater(BlockEnforcer.getPlugin(), () -> block.setBlockData(data, false), 1L);
    }
}
