package io.github.sjouwer.blockenforcer.handlers;

import io.github.sjouwer.blockenforcer.BlockEnforcer;
import io.github.sjouwer.blockenforcer.utils.BlockUtil;
import net.minecraft.server.v1_14_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Cake;
import org.bukkit.block.data.type.SeaPickle;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

public class GeneralBlockHandler {
    private static final List<Block> scheduledBlocks = new ArrayList<>();

    private GeneralBlockHandler() {
    }

    public static void scheduleBlockUpdate(Block block, Material material) {
        if (block.getType() != material || scheduledBlocks.contains(block)) {
            return;
        }

        scheduledBlocks.add(block);
        BlockData data = block.getBlockData().clone();
        Bukkit.getScheduler().runTaskLater(BlockEnforcer.getPlugin(), () -> {
            if (block.getType() == data.getMaterial()) {
                block.setBlockData(data, false);
                scheduledBlocks.remove(block);
            }
        }, 1L);
    }

    public static void forcePlaceBlock(PlayerInteractEvent event) {
        Block placementBlock = BlockPlaceHandler.placeBlock(event);
        if (placementBlock == null) {
            return;
        }

        event.setCancelled(true);
    }

    public static void forcePlaceCake(PlayerInteractEvent event) {
        Block placedBlock = BlockPlaceHandler.placeBlock(event);
        if (placedBlock == null) {
            return;
        }

        NBTTagCompound blockStateTag = BlockUtil.getBlockStateTag(event.getItem());
        if (blockStateTag != null) {
            Cake cake = (Cake) placedBlock.getBlockData();

            String bites = blockStateTag.getString("bites");
            if (!bites.isEmpty()) cake.setBites(Integer.parseInt(bites));

            placedBlock.setBlockData(cake);
            placedBlock.getState().update(true);
        }

        event.setCancelled(true);
    }

    public static void forcePlaceSeaPickle(PlayerInteractEvent event) {
        Block placedBlock = BlockPlaceHandler.placeBlock(event);
        if (placedBlock == null) {
            return;
        }

        NBTTagCompound blockStateTag = BlockUtil.getBlockStateTag(event.getItem());
        if (blockStateTag != null) {
            SeaPickle pickle = (SeaPickle) placedBlock.getBlockData();

            String pickles = blockStateTag.getString("pickles");
            if (!pickles.isEmpty()) pickle.setPickles(Integer.parseInt(pickles));

            String waterlogged = blockStateTag.getString("waterlogged");
            if (!waterlogged.isEmpty()) pickle.setWaterlogged(Boolean.parseBoolean(waterlogged));

            placedBlock.setBlockData(pickle);
            placedBlock.getState().update(true);
        }

        event.setCancelled(true);
    }
}
