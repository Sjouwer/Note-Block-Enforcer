package io.github.sjouwer.blockenforcer.utils;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.server.v1_14_R1.BlockPosition;
import net.minecraft.server.v1_14_R1.IBlockData;
import net.minecraft.server.v1_14_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Switch;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class BlockUtil {
    private static final BiMap<Material, Material> materialBiMap = HashBiMap.create();

    static {
        materialBiMap.put(Material.BRAIN_CORAL_WALL_FAN, Material.BRAIN_CORAL_FAN);
        materialBiMap.put(Material.BUBBLE_CORAL_WALL_FAN, Material.BUBBLE_CORAL_FAN);
        materialBiMap.put(Material.FIRE_CORAL_WALL_FAN, Material.FIRE_CORAL_FAN);
        materialBiMap.put(Material.HORN_CORAL_WALL_FAN, Material.HORN_CORAL_FAN);
        materialBiMap.put(Material.TUBE_CORAL_WALL_FAN, Material.TUBE_CORAL_FAN);
        materialBiMap.put(Material.DEAD_BRAIN_CORAL_WALL_FAN, Material.DEAD_BRAIN_CORAL_FAN);
        materialBiMap.put(Material.DEAD_BUBBLE_CORAL_WALL_FAN, Material.DEAD_BUBBLE_CORAL_FAN);
        materialBiMap.put(Material.DEAD_FIRE_CORAL_WALL_FAN, Material.DEAD_FIRE_CORAL_FAN);
        materialBiMap.put(Material.DEAD_HORN_CORAL_WALL_FAN, Material.DEAD_HORN_CORAL_FAN);
        materialBiMap.put(Material.DEAD_TUBE_CORAL_WALL_FAN, Material.DEAD_TUBE_CORAL_FAN);
        materialBiMap.put(Material.FROSTED_ICE, Material.ICE);
        materialBiMap.put(Material.WHEAT, Material.WHEAT_SEEDS);
        materialBiMap.put(Material.BEETROOTS, Material.BEETROOT_SEEDS);
        materialBiMap.put(Material.PUMPKIN_STEM, Material.PUMPKIN_SEEDS);
        materialBiMap.put(Material.MELON_STEM, Material.MELON_SEEDS);
        materialBiMap.put(Material.COCOA, Material.COCOA_BEANS);
        materialBiMap.put(Material.CARROTS, Material.CARROT);
        materialBiMap.put(Material.POTATOES, Material.POTATO);
        materialBiMap.put(Material.SWEET_BERRY_BUSH, Material.SWEET_BERRIES);
        materialBiMap.put(Material.REDSTONE_WIRE, Material.REDSTONE);
        materialBiMap.put(Material.TRIPWIRE, Material.STRING);
        materialBiMap.put(Material.WATER, Material.WATER_BUCKET);
        materialBiMap.put(Material.LAVA, Material.LAVA_BUCKET);
    }

    private BlockUtil() {
    }

    public static IBlockData toNMSBlock(Block block) {
        net.minecraft.server.v1_14_R1.World nmsWorld = ((CraftWorld) block.getWorld()).getHandle().getMinecraftWorld();
        BlockPosition blockPos = new BlockPosition(block.getX(), block.getY(), block.getZ());

        return nmsWorld.getType(blockPos);
    }

    public static ItemStack convertToStack(Block block) {
        ItemStack stack;
        Material blockMaterial = block.getType();

        Material itemMaterial = materialBiMap.getOrDefault(blockMaterial, null);
        if (itemMaterial != null) {
            stack = new ItemStack(itemMaterial);
            return stack;
        }

        if (blockMaterial.isItem()) {
            stack = new ItemStack(blockMaterial);
            return stack;
        }

        Optional<ItemStack> optionalItem = block.getDrops().stream().findFirst();
        stack = optionalItem.orElse(null);
        return stack;
    }

    public static Material convertToBlockMaterial(ItemStack stack, NBTTagCompound blockStateTag) {
        Material material = stack.getType();
        String facing = blockStateTag == null ? "" : blockStateTag.getString("facing");

        //Coral behaviour is Cubed specific
        switch (material) {
            case BRAIN_CORAL_FAN: return Material.BRAIN_CORAL_WALL_FAN;
            case BUBBLE_CORAL_FAN: return facing.isEmpty() ? material : Material.BUBBLE_CORAL_WALL_FAN;
            case FIRE_CORAL_FAN: return Material.FIRE_CORAL_WALL_FAN;
            case HORN_CORAL_FAN: return Material.HORN_CORAL_WALL_FAN;
            case TUBE_CORAL_FAN: return Material.TUBE_CORAL_WALL_FAN;
            case DEAD_BRAIN_CORAL_FAN: return Material.DEAD_BRAIN_CORAL_WALL_FAN;
            case DEAD_BUBBLE_CORAL_FAN: return Material.DEAD_BUBBLE_CORAL_WALL_FAN;
            case DEAD_FIRE_CORAL_FAN: return Material.DEAD_FIRE_CORAL_WALL_FAN;
            case DEAD_HORN_CORAL_FAN: return facing.isEmpty() ? material : Material.DEAD_HORN_CORAL_WALL_FAN;
            case DEAD_TUBE_CORAL_FAN: return Material.DEAD_TUBE_CORAL_WALL_FAN;
            default: return material.isBlock() ? material : materialBiMap.inverse().getOrDefault(material, null);
        }
    }

    public static boolean isReplaceable(Block block) {
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

    public static boolean isInteractable(Block block) {
        Material type = block.getType();
        return type.isInteractable() &&
                type != Material.NOTE_BLOCK &&
                type != Material.CAKE &&
                type != Material.STRUCTURE_BLOCK &&
                type != Material.CAULDRON &&
                !Tag.FENCES.isTagged(type) &&
                !Tag.STAIRS.isTagged(type);
    }

    public static BlockFace rotateClock(BlockFace face) {
        switch (face) {
            case NORTH: return BlockFace.EAST;
            case EAST: return BlockFace.SOUTH;
            case SOUTH: return BlockFace.WEST;
            default: return BlockFace.NORTH;
        }
    }

    public static BlockFace rotateAntiClock(BlockFace face) {
        switch (face) {
            case NORTH: return BlockFace.WEST;
            case WEST: return BlockFace.SOUTH;
            case SOUTH: return BlockFace.EAST;
            default: return BlockFace.NORTH;
        }
    }

    public static Rotation convertToRotation(BlockFace playerFace, BlockFace blockFace) {
        if (blockFace.equals(BlockFace.UP)) {
            switch (playerFace) {
                case EAST: return Rotation.CLOCKWISE;
                case SOUTH: return Rotation.FLIPPED;
                case WEST: return Rotation.COUNTER_CLOCKWISE;
                default: return Rotation.NONE;
            }
        }
        else {
            switch (playerFace) {
                case WEST: return Rotation.COUNTER_CLOCKWISE;
                case NORTH: return Rotation.FLIPPED;
                case EAST: return Rotation.CLOCKWISE;
                default: return Rotation.NONE;
            }
        }
    }

    public static Switch.Face getSwitchFace(BlockFace face) {
        switch (face) {
            case NORTH:
            case EAST:
            case SOUTH:
            case WEST:
                return Switch.Face.WALL;
            case DOWN:
                return Switch.Face.CEILING;
            default:
                return Switch.Face.FLOOR;
        }
    }
}
