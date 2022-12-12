package io.github.sjouwer.blockenforcer.handlers;

import io.github.sjouwer.blockenforcer.packets.PacketWorldChunk;
import io.github.sjouwer.blockenforcer.utils.BlockUtil;
import net.minecraft.server.v1_14_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.AnaloguePowerable;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.Powerable;
import org.bukkit.block.data.Rail;
import org.bukkit.block.data.type.Cake;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.RedstoneWire;
import org.bukkit.block.data.type.TripwireHook;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.PressureSensor;

import java.util.ArrayList;
import java.util.List;

public class RedstoneBlockHandler {
    private static final List<Block> updatedRedstone = new ArrayList<>();

    private RedstoneBlockHandler() {
    }

    public static void forcePlaceRedstone(PlayerInteractEvent event) {
        Block placedBlock = BlockPlaceHandler.placeBlock(event);
        if (placedBlock == null) {
            return;
        }

        placedBlock.getState().update(true, true);
        event.setCancelled(true);
    }

    public static void forceRedstoneNBTState(ItemStack item, Block block) {
        if (item.getType() != Material.REDSTONE) {
            return;
        }

        NBTTagCompound blockStateTag = BlockUtil.getBlockStateTag(item);
        if (blockStateTag != null) {
            RedstoneWire redstoneWire = (RedstoneWire) block.getBlockData();

            String note = blockStateTag.getString("power");
            if (!note.isEmpty()) redstoneWire.setPower(Integer.parseInt(note));

            String north = blockStateTag.getString("north").toUpperCase();
            if (!north.isEmpty()) redstoneWire.setFace(BlockFace.NORTH, RedstoneWire.Connection.valueOf(north));

            String east = blockStateTag.getString("east").toUpperCase();
            if (!north.isEmpty()) redstoneWire.setFace(BlockFace.EAST, RedstoneWire.Connection.valueOf(east));

            String south = blockStateTag.getString("south").toUpperCase();
            if (!north.isEmpty()) redstoneWire.setFace(BlockFace.SOUTH, RedstoneWire.Connection.valueOf(south));

            String west = blockStateTag.getString("west").toUpperCase();
            if (!north.isEmpty()) redstoneWire.setFace(BlockFace.WEST, RedstoneWire.Connection.valueOf(west));

            block.setBlockData(redstoneWire);
            block.getState().update(true, false);
        }
    }

    public static void stopTripwireChange(Block block) {
        GeneralBlockHandler.scheduleBlockUpdate(block.getRelative(BlockFace.NORTH), Material.TRIPWIRE);
        GeneralBlockHandler.scheduleBlockUpdate(block.getRelative(BlockFace.EAST), Material.TRIPWIRE);
        GeneralBlockHandler.scheduleBlockUpdate(block.getRelative(BlockFace.SOUTH), Material.TRIPWIRE);
        GeneralBlockHandler.scheduleBlockUpdate(block.getRelative(BlockFace.WEST), Material.TRIPWIRE);
    }

    public static void stopRedstoneChange(Block block) {
        GeneralBlockHandler.scheduleBlockUpdate(block.getRelative(BlockFace.NORTH), Material.REDSTONE_WIRE);
        GeneralBlockHandler.scheduleBlockUpdate(block.getRelative(BlockFace.EAST), Material.REDSTONE_WIRE);
        GeneralBlockHandler.scheduleBlockUpdate(block.getRelative(BlockFace.SOUTH), Material.REDSTONE_WIRE);
        GeneralBlockHandler.scheduleBlockUpdate(block.getRelative(BlockFace.WEST), Material.REDSTONE_WIRE);
        GeneralBlockHandler.scheduleBlockUpdate(block.getRelative(BlockFace.DOWN), Material.REDSTONE_WIRE);
    }

    public static void redstoneUpdateCheck(Block block) {
        updateNeighbourRedstone(block);
        updateNeighbourRedstone(block.getRelative(BlockFace.DOWN));
        updateNeighbourRedstone(block.getRelative(BlockFace.UP));

        updatedRedstone.clear();
    }

    private static void updateNeighbourRedstone(Block block) {
        updateRedstone(block.getRelative(BlockFace.NORTH));
        updateRedstone(block.getRelative(BlockFace.EAST));
        updateRedstone(block.getRelative(BlockFace.SOUTH));
        updateRedstone(block.getRelative(BlockFace.WEST));
        updateRedstone(block.getRelative(BlockFace.DOWN));
    }

    private static void updateRedstone(Block redstone) {
        if (redstone.getType() != Material.REDSTONE_WIRE) {
            return;
        }

        if (updatedRedstone.contains(redstone)) {
            return;
        }

        redstone.getState().update(true, false);
        updatedRedstone.add(redstone);
        updateNeighbourRedstone(redstone);
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

    public static void forcePlacePressurePlate(PlayerInteractEvent event) {
        Block placedBlock = BlockPlaceHandler.placeBlock(event);
        if (placedBlock == null) {
            return;
        }

        NBTTagCompound blockStateTag = BlockUtil.getBlockStateTag(event.getItem());
        if (blockStateTag != null) {
            String powered = blockStateTag.getString("powered");
            if (!powered.isEmpty()) setPowered(placedBlock, Boolean.parseBoolean(powered));
        }

        placedBlock.getState().update(true);
        event.setCancelled(true);
    }

    public static void forcePlaceWeightedPressurePlate(PlayerInteractEvent event) {
        Block placedBlock = BlockPlaceHandler.placeBlock(event);
        if (placedBlock == null) {
            return;
        }

        NBTTagCompound blockStateTag = BlockUtil.getBlockStateTag(event.getItem());
        if (blockStateTag != null) {
            AnaloguePowerable plate = (AnaloguePowerable) placedBlock.getBlockData();

            String power = blockStateTag.getString("power");
            if (!power.isEmpty()) plate.setPower(Integer.parseInt(power));

            placedBlock.setBlockData(plate);
            placedBlock.getState().update(true);
        }

        event.setCancelled(true);
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

    public static void setPowered(Block block, boolean poweredState) {
        if (block.getBlockData() instanceof Powerable) {
            Powerable powerable = (Powerable) block.getBlockData();
            powerable.setPowered(poweredState);
            block.setBlockData(powerable);
        }
    }

    private static void setRailShape(Block block, Rail.Shape shape) {
        if (block.getBlockData() instanceof Rail) {
            Rail rail = (Rail) block.getBlockData();
            rail.setShape(shape);
            block.setBlockData(rail);
        }
    }
}
