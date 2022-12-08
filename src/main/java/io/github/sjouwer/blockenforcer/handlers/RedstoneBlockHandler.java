package io.github.sjouwer.blockenforcer.handlers;

import io.github.sjouwer.blockenforcer.BlockEnforcer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Door;

public class RedstoneBlockHandler {
    private RedstoneBlockHandler() {
    }

    public static void stopTripwireChange(Block block) {
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

    public static void openDoorWithoutOtherChanges(Block block) {
        Door door = (Door) block.getBlockData();
        door.setOpen(!door.isOpen());
        block.setBlockData(door);
        block.getState().update(true, false);

        boolean isTop = door.getHalf() == Bisected.Half.TOP;
        Block relatedBlock = block.getRelative(isTop ? BlockFace.DOWN : BlockFace.UP);
        if (relatedBlock.getType() != door.getMaterial()) {
            return;
        }

        Door relatedDoor = (Door) relatedBlock.getBlockData();
        if ((isTop && relatedDoor.getHalf() == Bisected.Half.TOP) ||
                (!isTop && relatedDoor.getHalf() == Bisected.Half.BOTTOM)) {
            return;
        }

        relatedDoor.setOpen(door.isOpen());
        relatedBlock.setBlockData(relatedDoor);
        relatedBlock.getState().update(true, false);
    }
}
