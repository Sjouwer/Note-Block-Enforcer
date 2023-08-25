package io.github.sjouwer.blockenforcer.handlers;

import io.github.sjouwer.blockenforcer.utils.BlockUtil;
import net.minecraft.server.v1_14_R1.NBTTagCompound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Door;
import org.bukkit.event.player.PlayerInteractEvent;

public class DoorHandler {
    private DoorHandler() {
    }

    public static void openDoorWithoutOtherChanges(Block block) {
        if (!(block.getBlockData() instanceof Door)) return;

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

    public static void forcePlaceDoor(BlockData blockData, NBTTagCompound blockStateTag, PlayerInteractEvent event) {
        if (!(blockData instanceof Door) || (blockStateTag == null && event.getBlockFace() == BlockFace.UP)) return;

        Door door = (Door) blockData;

        if (blockStateTag != null) {
            String hinge = blockStateTag.getString("hinge").toUpperCase();
            if (!hinge.isEmpty()) door.setHinge(Door.Hinge.valueOf(hinge));

            String powered = blockStateTag.getString("powered");
            if (!powered.isEmpty()) door.setPowered(Boolean.parseBoolean(powered));
        }

        BlockFace facing = event.getBlockFace();
        if (facing == BlockFace.UP || facing == BlockFace.DOWN) {
            facing = event.getPlayer().getFacing();
        }
        door.setFacing(facing);
        door.setHalf(Bisected.Half.BOTTOM);

        Block placedBlock = BlockPlaceHandler.placeBlock(event, door);
        if (placedBlock == null) return;

        event.setCancelled(true);

        Block doorTopBlock = placedBlock.getRelative(BlockFace.UP, 1);
        if (BlockUtil.isReplaceable(doorTopBlock)) {
            door.setHalf(Bisected.Half.TOP);
            BlockPlaceHandler.placeBlock(event, door, doorTopBlock);
        }
    }
}
