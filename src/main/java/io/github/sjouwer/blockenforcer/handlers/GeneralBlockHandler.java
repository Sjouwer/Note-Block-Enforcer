package io.github.sjouwer.blockenforcer.handlers;

import io.github.sjouwer.blockenforcer.utils.BlockUtil;
import net.minecraft.server.v1_14_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Cake;
import org.bukkit.block.data.type.SeaPickle;
import org.bukkit.block.data.type.Snow;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class GeneralBlockHandler {
    private GeneralBlockHandler() {
    }

    public static void forcePlaceBlock(BlockData blockData, PlayerInteractEvent event) {
        Block placementBlock = BlockPlaceHandler.placeBlock(event, blockData);
        if (placementBlock == null) return;

        event.setCancelled(true);
    }

    public static void forcePlaceCake(BlockData blockData, PlayerInteractEvent event) {
        if (!(blockData instanceof Cake)) return;

        Cake cake = (Cake) blockData;

        NBTTagCompound blockStateTag = BlockUtil.getBlockStateTag(event.getItem());
        if (blockStateTag != null) {
            String bites = blockStateTag.getString("bites");
            if (!bites.isEmpty()) cake.setBites(Integer.parseInt(bites));
        }

        Block placedBlock = BlockPlaceHandler.placeBlock(event, cake);
        if (placedBlock == null) return;

        event.setCancelled(true);
    }

    public static void forcePlaceSeaPickle(BlockData blockData, PlayerInteractEvent event) {
        if (!(blockData instanceof SeaPickle)) return;

        SeaPickle pickle = (SeaPickle) blockData;

        NBTTagCompound blockStateTag = BlockUtil.getBlockStateTag(event.getItem());
        if (blockStateTag != null) {
            String pickles = blockStateTag.getString("pickles");
            if (!pickles.isEmpty()) pickle.setPickles(Integer.parseInt(pickles));

            String waterlogged = blockStateTag.getString("waterlogged");
            if (!waterlogged.isEmpty()) pickle.setWaterlogged(Boolean.parseBoolean(waterlogged));
        }

        Block placedBlock = BlockPlaceHandler.placeBlock(event, pickle);
        if (placedBlock == null) return;

        event.setCancelled(true);
    }

    public static void forcePlaceSnow(BlockData blockData, PlayerInteractEvent event) {
        if (!(blockData instanceof Snow)) return;

        NBTTagCompound blockStateTag = BlockUtil.getBlockStateTag(event.getItem());
        if (blockStateTag == null) {
            boolean layerAdded = false;

            if (event.getClickedBlock().getType() == Material.SNOW && event.getBlockFace() == BlockFace.UP) {
                layerAdded = addSnowLayer(event.getClickedBlock(), event);
            }

            if (!layerAdded && event.getClickedBlock().getRelative(event.getBlockFace()).getType() == Material.SNOW) {
                layerAdded = addSnowLayer(event.getClickedBlock().getRelative(event.getBlockFace()), event);
            }

            if (layerAdded) return;
        }

        Snow snow = (Snow) blockData;

        if (blockStateTag != null) {
            String layers = blockStateTag.getString("layers");
            if (!layers.isEmpty()) snow.setLayers(Integer.parseInt(layers));
        }

        Block placedBlock = BlockPlaceHandler.placeBlock(event, snow);
        if (placedBlock == null) return;

        event.setCancelled(true);
    }

    private static boolean addSnowLayer(Block placementBlock, PlayerInteractEvent event) {
        Snow snow = (Snow) placementBlock.getBlockData();
        if (snow.getLayers() != 8) {
            BlockState replacedBlockState = placementBlock.getState();

            snow.setLayers(snow.getLayers() + 1);
            placementBlock.setBlockData(snow);

            BlockPlaceEvent blockPlaceEvent = new BlockPlaceEvent(placementBlock, replacedBlockState, event.getClickedBlock().getRelative(BlockFace.DOWN), event.getItem(), event.getPlayer(), true, event.getHand());
            Bukkit.getPluginManager().callEvent(blockPlaceEvent);
            if (blockPlaceEvent.isCancelled()) {
                placementBlock.setBlockData(replacedBlockState.getBlockData(), false);
                return false;
            }

            event.setCancelled(true);
            return true;
        }

        return false;
    }
}
