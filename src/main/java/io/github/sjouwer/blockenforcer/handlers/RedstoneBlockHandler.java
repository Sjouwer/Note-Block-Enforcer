package io.github.sjouwer.blockenforcer.handlers;

import io.github.sjouwer.blockenforcer.utils.BlockUtil;
import net.minecraft.server.v1_14_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.*;
import org.bukkit.block.data.type.*;
import org.bukkit.event.player.PlayerInteractEvent;

public class RedstoneBlockHandler {
    private RedstoneBlockHandler() {
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
            BlockFace facing = event.getBlockFace();
            if (facing == BlockFace.UP || facing == BlockFace.DOWN) {
                facing = event.getPlayer().getFacing().getOppositeFace();
            }

            String power = blockStateTag.getString("power");
            int powerLvl = power.isEmpty() ? 0 : Integer.parseInt(power);

            //The rotation of the pressure plate models depends on the power lvl in the Cubed pack
            powerLvl = blockData.getMaterial() == Material.LIGHT_WEIGHTED_PRESSURE_PLATE ?
                    getLightPressurePlateRotation(facing, powerLvl) :
                    getHeavyPressurePlateRotation(facing, powerLvl);

            plate.setPower(powerLvl);
        }

        Block placedBlock = BlockPlaceHandler.placeBlock(event, plate);
        if (placedBlock == null) return;

        event.setCancelled(true);
    }

    public static int getLightPressurePlateRotation(BlockFace facing, int powerLvl) {
        if (powerLvl > 0 && powerLvl < 5) {
            switch (facing) {
                case WEST: return 2;
                case SOUTH: return 1;
                case EAST: return 4;
                default: return 3;
            }
        }

        if (powerLvl > 4 && powerLvl < 9) {
            switch (facing) {
                case WEST: return 6;
                case SOUTH: return 7;
                case EAST: return 8;
                default: return 5;
            }
        }

        if (powerLvl > 8 && powerLvl < 13) {
            switch (facing) {
                case WEST: return 10;
                case SOUTH: return 11;
                case EAST: return 12;
                default: return 9;
            }
        }

        if (powerLvl > 12 && powerLvl < 15) {
            switch (facing) {
                case WEST:
                case EAST: return 14;
                default: return 13;
            }
        }

        return powerLvl;
    }

    public static int getHeavyPressurePlateRotation(BlockFace facing, int powerLvl) {
        if (powerLvl > 0 && powerLvl < 5) {
            switch (facing) {
                case WEST: return 2;
                case SOUTH: return 3;
                case EAST: return 4;
                default: return 1;
            }
        }

        if (powerLvl > 4 && powerLvl < 9) {
            switch (facing) {
                case WEST: return 6;
                case SOUTH: return 7;
                case EAST: return 8;
                default: return 5;
            }
        }

        return powerLvl;
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

        BlockFace facing = event.getBlockFace();
        if (facing == BlockFace.UP || facing == BlockFace.DOWN) {
            facing = event.getPlayer().getFacing().getOppositeFace();
        }

        if (blockStateTag != null) {
            String powered = blockStateTag.getString("powered");
            if (!powered.isEmpty()) comparator.setPowered(Boolean.parseBoolean(powered));

            String mode = blockStateTag.getString("mode").toUpperCase();
            if (!mode.isEmpty()) comparator.setMode(Comparator.Mode.valueOf(mode));

            //This is the park bench in the Cubed pack, which is rotated 180, needs to be corrected
            if (!comparator.isPowered() && comparator.getMode() == Comparator.Mode.SUBTRACT) {
                facing = facing.getOppositeFace();
            }
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

    public static void forcePlaceButton(BlockData blockData, NBTTagCompound blockStateTag, PlayerInteractEvent event) {
        if (!(blockData instanceof Switch)) return;

        Switch button = (Switch) blockData;

        BlockFace facing = event.getBlockFace();
        Switch.Face switchFace = BlockUtil.getSwitchFace(facing);
        button.setFace(switchFace);

        if (blockStateTag != null) {
            String powered = blockStateTag.getString("powered");
            if (!powered.isEmpty()) button.setPowered(Boolean.parseBoolean(powered));

            //(Dark Oak Button) Cubed pack uses a keyboard for floor & ceiling and a monitor for the wall
            if (button.isPowered() && blockData.getMaterial() == Material.DARK_OAK_BUTTON) {
                String face = blockStateTag.getString("face").toUpperCase();
                if (!face.isEmpty()) button.setFace(Switch.Face.valueOf(face));
            }
        }

        if (facing == BlockFace.UP || facing == BlockFace.DOWN) {
            //(Acacia Button) Cubed pack uses a mouse, rotate to make it face the right way
            if (button.isPowered() && blockData.getMaterial() == Material.ACACIA_BUTTON) {
                facing = BlockUtil.rotateClock(event.getPlayer().getFacing());
            } else {
                facing = event.getPlayer().getFacing().getOppositeFace();
            }
        }
        button.setFacing(facing);

        Block placedBlock = BlockPlaceHandler.placeBlock(event, button);
        if (placedBlock == null) return;

        event.setCancelled(true);
    }
}
