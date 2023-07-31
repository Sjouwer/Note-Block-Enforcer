package io.github.sjouwer.blockenforcer.handlers;

import io.github.sjouwer.blockenforcer.BlockEnforcer;
import io.github.sjouwer.blockenforcer.Config;
import net.minecraft.server.v1_14_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.*;
import org.bukkit.block.data.type.*;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RedstoneBlockHandler {
    private static final Map<Block, BlockData> BlocksToChange = new HashMap<>();
    private static final List<Block> updatedRedstone = new ArrayList<>();
    private static final List<Block> redstoneToUpdate = new ArrayList<>();
    private static int currentUpdateCount = 0;

    private RedstoneBlockHandler() {
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

    private static void scheduleRedstoneUpdate(Block blockToUpdate) {
        if (blockToUpdate.getType() != Material.REDSTONE_WIRE || updatedRedstone.contains(blockToUpdate) || redstoneToUpdate.contains(blockToUpdate)) {
            return;
        }

        redstoneToUpdate.add(blockToUpdate);

        if (redstoneToUpdate.size() == 1) {
            Bukkit.getScheduler().runTaskLater(BlockEnforcer.getPlugin(), () -> {
                currentUpdateCount = 0;
                List<Block> blocks = new ArrayList<>(redstoneToUpdate);
                redstoneToUpdate.clear();
                for (Block block : blocks) {
                    updateRedstoneNow(block);
                }
            }, 1L);
        }
    }

    private static void updateRedstoneNow(Block block) {
        if (block.getType() != Material.REDSTONE_WIRE || updatedRedstone.contains(block) || redstoneToUpdate.contains(block)) {
            return;
        }

        if (currentUpdateCount < Config.MAX_REDSTONE_UPDATES) {
            currentUpdateCount++;
            block.getState().update(true, false);
            updatedRedstone.add(block);
            updateRedstone(block);
        }
        else {
            scheduleRedstoneUpdate(block);
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

    public static void forcePlaceRedstone(BlockData blockData, NBTTagCompound blockStateTag, PlayerInteractEvent event) {
        if (!(blockData instanceof RedstoneWire)) return;

        RedstoneWire redstoneWire = (RedstoneWire) blockData;

        if (blockStateTag != null) {
            String power = blockStateTag.getString("power");
            if (!power.isEmpty()) redstoneWire.setPower(Integer.parseInt(power));

            String north = blockStateTag.getString("north").toUpperCase();
            if (!north.isEmpty()) redstoneWire.setFace(BlockFace.NORTH, RedstoneWire.Connection.valueOf(north));

            String east = blockStateTag.getString("east").toUpperCase();
            if (!north.isEmpty()) redstoneWire.setFace(BlockFace.EAST, RedstoneWire.Connection.valueOf(east));

            String south = blockStateTag.getString("south").toUpperCase();
            if (!north.isEmpty()) redstoneWire.setFace(BlockFace.SOUTH, RedstoneWire.Connection.valueOf(south));

            String west = blockStateTag.getString("west").toUpperCase();
            if (!north.isEmpty()) redstoneWire.setFace(BlockFace.WEST, RedstoneWire.Connection.valueOf(west));
        }

        Block placedBlock = BlockPlaceHandler.placeBlock(event, redstoneWire);
        if (placedBlock == null) return;

        event.setCancelled(true);
    }

    public static void forcePlacePowerable(BlockData blockData, NBTTagCompound blockStateTag, PlayerInteractEvent event) {
        if (!(blockData instanceof Powerable)) return;

        Powerable powerable = (Powerable) blockData;

        if (blockStateTag != null) {
            String powered = blockStateTag.getString("powered");
            if (!powered.isEmpty()) powerable.setPowered(Boolean.parseBoolean(powered));
        }

        Block placedBlock = BlockPlaceHandler.placeBlock(event, powerable);
        if (placedBlock == null) return;

        event.setCancelled(true);
    }

    public static void forcePlaceWeightedPressurePlate(BlockData blockData, NBTTagCompound blockStateTag, PlayerInteractEvent event) {
        if (!(blockData instanceof AnaloguePowerable)) return;

        AnaloguePowerable plate = (AnaloguePowerable) blockData;

        if (blockStateTag != null) {
            String power = blockStateTag.getString("power");
            if (!power.isEmpty()) plate.setPower(Integer.parseInt(power));
        }

        Block placedBlock = BlockPlaceHandler.placeBlock(event, plate);
        if (placedBlock == null) return;

        event.setCancelled(true);
    }

    public static void forcePlaceRepeater(BlockData blockData, NBTTagCompound blockStateTag, PlayerInteractEvent event) {
        if (!(blockData instanceof Repeater)) return;

        Repeater repeater = (Repeater) blockData;

        if (blockStateTag != null) {
            String powered = blockStateTag.getString("powered");
            if (!powered.isEmpty()) repeater.setPowered(Boolean.parseBoolean(powered));

            String locked = blockStateTag.getString("locked");
            if (!locked.isEmpty()) repeater.setLocked(Boolean.parseBoolean(locked));

            String delay = blockStateTag.getString("delay");
            if (!delay.isEmpty()) repeater.setDelay(Integer.parseInt(delay));
        }

        BlockFace facing = event.getBlockFace();
        if (facing == BlockFace.UP || facing == BlockFace.DOWN) {
            facing = event.getPlayer().getFacing().getOppositeFace();
        }
        repeater.setFacing(facing);

        Block placedBlock = BlockPlaceHandler.placeBlock(event, repeater);
        if (placedBlock == null) return;

        event.setCancelled(true);
    }

    public static void forcePlaceComparator(BlockData blockData, NBTTagCompound blockStateTag, PlayerInteractEvent event) {
        if (!(blockData instanceof Comparator)) return;

        Comparator comparator = (Comparator) blockData;

        if (blockStateTag != null) {
            String powered = blockStateTag.getString("powered");
            if (!powered.isEmpty()) comparator.setPowered(Boolean.parseBoolean(powered));

            String mode = blockStateTag.getString("mode").toUpperCase();
            if (!mode.isEmpty()) comparator.setMode(Comparator.Mode.valueOf(mode));
        }

        BlockFace facing = event.getBlockFace();
        if (facing == BlockFace.UP || facing == BlockFace.DOWN) {
            facing = event.getPlayer().getFacing().getOppositeFace();
        }
        comparator.setFacing(facing);

        Block placedBlock = BlockPlaceHandler.placeBlock(event, comparator);
        if (placedBlock == null) return;

        event.setCancelled(true);
    }

    public static void forcePlaceRail(BlockData blockData, NBTTagCompound blockStateTag, PlayerInteractEvent event) {
        if (!(blockData instanceof Rail)) return;

        if (blockStateTag == null) return;

        Rail rail = (Rail) blockData;

        String shape = blockStateTag.getString("shape").toUpperCase();
        if (!shape.isEmpty()) rail.setShape(Rail.Shape.valueOf(shape));

        Block placedBlock = BlockPlaceHandler.placeBlock(event, rail);
        if (placedBlock == null) return;

        event.setCancelled(true);
    }

    public static void forcePlaceRedstoneRail(BlockData blockData, NBTTagCompound blockStateTag, PlayerInteractEvent event) {
        if (!(blockData instanceof RedstoneRail)) return;

        if (blockStateTag == null) return;

        RedstoneRail redstoneRail = (RedstoneRail) blockData;

        String shape = blockStateTag.getString("shape").toUpperCase();
        if (!shape.isEmpty()) redstoneRail.setShape(Rail.Shape.valueOf(shape));

        String powered = blockStateTag.getString("powered");
        if (!powered.isEmpty()) redstoneRail.setPowered(Boolean.parseBoolean(powered));

        Block placedBlock = BlockPlaceHandler.placeBlock(event, redstoneRail);
        if (placedBlock == null) return;

        event.setCancelled(true);
    }

    public static void forcePlaceTripwireHook(BlockData blockData, NBTTagCompound blockStateTag, PlayerInteractEvent event) {
        if (!(blockData instanceof TripwireHook)) return;

        TripwireHook hook = (TripwireHook) blockData;

        if (blockStateTag != null) {
            String attached = blockStateTag.getString("attached");
            if (!attached.isEmpty()) hook.setAttached(Boolean.parseBoolean(attached));

            String powered = blockStateTag.getString("powered");
            if (!powered.isEmpty()) hook.setPowered(Boolean.parseBoolean(powered));
        }

        BlockFace facing = event.getBlockFace();
        if (facing == BlockFace.UP || facing == BlockFace.DOWN) {
            facing = event.getPlayer().getFacing().getOppositeFace();
        }
        hook.setFacing(facing);

        Block placedBlock = BlockPlaceHandler.placeBlock(event, hook);
        if (placedBlock == null) return;

        event.setCancelled(true);
    }
}
