package io.github.sjouwer.blockenforcer.handlers;

import io.github.sjouwer.blockenforcer.BlockEnforcer;
import io.github.sjouwer.blockenforcer.utils.BlockUtil;
import net.minecraft.server.v1_14_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Powerable;
import org.bukkit.block.data.Rail;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.TripwireHook;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class RedstoneBlockHandler {
    private RedstoneBlockHandler() {
    }

    public static void setPowered(Block block, boolean poweredState) {
        if (block.getBlockData() instanceof Powerable) {
            Powerable powerable = (Powerable) block.getBlockData();
            powerable.setPowered(poweredState);
            block.setBlockData(powerable);
        }
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

    public static void forceHookNBTState(ItemStack item, Block block) {
        if (item.getType() != Material.TRIPWIRE_HOOK) {
            return;
        }

        NBTTagCompound blockStateTag = BlockUtil.getBlockStateTag(item);
        if (blockStateTag != null) {
            TripwireHook hook = (TripwireHook) block.getBlockData();

            String attached = blockStateTag.getString("attached");
            if (!attached.isEmpty()) hook.setAttached(Boolean.parseBoolean(attached));

            String facing = blockStateTag.getString("facing").toUpperCase();
            if (!facing.isEmpty()) hook.setFacing(BlockFace.valueOf(facing));

            String powered = blockStateTag.getString("powered");
            if (!powered.isEmpty()) hook.setPowered(Boolean.parseBoolean(powered));

            block.setBlockData(hook);
            block.getState().update(true, false);
        }
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

    public static void forcePlaceRail(PlayerInteractEvent event) {
        NBTTagCompound blockStateTag = BlockUtil.getBlockStateTag(event.getItem());
        if (blockStateTag == null) {
            return;
        }

        Block placedBlock = BlockPlaceHandler.placeBlock(event);
        if (placedBlock == null) {
            return;
        }

        String powered = blockStateTag.getString("powered");
        if (!powered.isEmpty()) setPowered(placedBlock, Boolean.parseBoolean(powered));

        String shape = blockStateTag.getString("shape").toUpperCase();
        if (!shape.isEmpty()) setRailShape(placedBlock, Rail.Shape.valueOf(shape));

        placedBlock.getState().update(true, false);
        event.setCancelled(true);
    }

    private static void setRailShape(Block block, Rail.Shape shape) {
        if (block.getBlockData() instanceof Rail) {
            Rail rail = (Rail) block.getBlockData();
            rail.setShape(shape);
            block.setBlockData(rail);
        }
    }
}
