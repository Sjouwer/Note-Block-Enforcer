package io.github.sjouwer.blockenforcer.handlers;

import io.github.sjouwer.blockenforcer.utils.BlockUtil;
import net.minecraft.server.v1_14_R1.NBTTagCompound;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Cake;
import org.bukkit.block.data.type.SeaPickle;
import org.bukkit.event.player.PlayerInteractEvent;

public class GeneralBlockHandler {
    private GeneralBlockHandler() {
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
