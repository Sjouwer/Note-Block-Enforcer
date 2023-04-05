package io.github.sjouwer.blockenforcer.handlers;

import io.github.sjouwer.blockenforcer.utils.BlockUtil;
import net.minecraft.server.v1_14_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Bamboo;
import org.bukkit.block.data.type.Bamboo.Leaves;
import org.bukkit.block.data.type.Sapling;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlantHandler {
    private PlantHandler() {
    }

    public static void forcePlaceDoublePlant(PlayerInteractEvent event) {
        Block placedBlock = BlockPlaceHandler.placeBlock(event);
        if (placedBlock == null) {
            return;
        }

        event.setCancelled(true);

        NBTTagCompound blockStateTag = BlockUtil.getBlockStateTag(event.getItem());
        if (blockStateTag != null) {
            String half = blockStateTag.getString("half");
            if (!half.isEmpty()) setPlantHalf(placedBlock, stringToHalf(half));
            return;
        }

        Block plantTopBlock = placedBlock.getRelative(BlockFace.UP, 1);
        if (BlockUtil.isReplaceable(plantTopBlock)) {
            placedBlock = BlockPlaceHandler.placeBlock(event, plantTopBlock);
            if (placedBlock == null) {
                return;
            }
            setPlantHalf(placedBlock, Bisected.Half.TOP);
        }
    }

    private static void setPlantHalf(Block block, Bisected.Half half) {
        if (block.getBlockData() instanceof Bisected) {
            Bisected halfPlant = (Bisected) block.getBlockData();
            halfPlant.setHalf(half);
            block.setBlockData(halfPlant);
            block.getState().update(true, false);
        }
    }

    public static Bisected.Half stringToHalf(String string) {
        if (string.equalsIgnoreCase("upper") || string.equalsIgnoreCase("top")) {
            return Bisected.Half.TOP;
        }
        return Bisected.Half.BOTTOM;
    }

    public static void forcePlaceAgingPlant(PlayerInteractEvent event) {
        Block placedBlock = BlockPlaceHandler.placeBlock(event);
        if (placedBlock == null) {
            return;
        }

        int age = getPlantAge(event.getItem());
        setPlantAge(placedBlock, age);
        event.setCancelled(true);
    }

    private static int getPlantAge(ItemStack stack) {
        if (stack.getType() == Material.WHEAT) {
            return 7;
        }

        NBTTagCompound blockStateTag = BlockUtil.getBlockStateTag(stack);
        if (blockStateTag != null){
            String age = blockStateTag.getString("age");
            if (!age.isEmpty()) return Integer.parseInt(age);
        }

        return 0;
    }

    private static void setPlantAge(Block block, int age) {
        if (block.getBlockData() instanceof Ageable) {
            Ageable plant = (Ageable) block.getBlockData();
            plant.setAge(age);
            block.setBlockData(plant);
            block.getState().update(true, false);
        }
    }

    public static void forcePlaceSapling(PlayerInteractEvent event) {
        Block placedBlock = BlockPlaceHandler.placeBlock(event);
        if (placedBlock == null) {
            return;
        }

        NBTTagCompound blockStateTag = BlockUtil.getBlockStateTag(event.getItem());
        if (blockStateTag != null) {
            Sapling sapling = (Sapling) placedBlock.getBlockData();

            String stage = blockStateTag.getString("stage");
            if (!stage.isEmpty()) sapling.setStage(Integer.parseInt(stage));

            placedBlock.setBlockData(sapling);
            placedBlock.getState().update(true, false);
        }

        event.setCancelled(true);
    }

    public static void forcePlaceBamboo(PlayerInteractEvent event) {
        Block placedBlock = BlockPlaceHandler.placeBlock(event);
        if (placedBlock == null) {
            return;
        }

        NBTTagCompound blockStateTag = BlockUtil.getBlockStateTag(event.getItem());
        if (blockStateTag != null) {
            Bamboo bamboo = (Bamboo) placedBlock.getBlockData();

            String age = blockStateTag.getString("age");
            if (!age.isEmpty()) bamboo.setAge(Integer.parseInt(age));

            String stage = blockStateTag.getString("stage");
            if (!stage.isEmpty()) bamboo.setStage(Integer.parseInt(stage));

            String leaves = blockStateTag.getString("leaves");
            if (!leaves.isEmpty()) bamboo.setLeaves(stringToLeaves(leaves));

            placedBlock.setBlockData(bamboo);
            placedBlock.getState().update(true, false);
        }

        event.setCancelled(true);
    }

    private static Leaves stringToLeaves(String string) {
        switch (string) {
            case "small": return Leaves.SMALL;
            case "large": return Leaves.LARGE;
            default: return Leaves.NONE;
        }
    }
}
