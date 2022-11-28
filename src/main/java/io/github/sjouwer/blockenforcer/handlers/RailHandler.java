package io.github.sjouwer.blockenforcer.handlers;

import io.github.sjouwer.blockenforcer.utils.BlockUtil;
import net.minecraft.server.v1_14_R1.NBTTagCompound;
import org.bukkit.block.Block;
import org.bukkit.block.data.Powerable;
import org.bukkit.block.data.Rail;
import org.bukkit.event.player.PlayerInteractEvent;

public class RailHandler {
    private RailHandler() {
    }

    public static void forcePlaceRail(PlayerInteractEvent event, boolean canBePowered) {
        NBTTagCompound blockStateTag = BlockUtil.getBlockStateTag(event.getItem());
        if (blockStateTag == null) {
            return;
        }

        Block placedBlock = BlockPlaceHandler.placeBlock(event);
        if (placedBlock == null) {
            return;
        }

        if (canBePowered) {
            setRailPower(placedBlock, blockStateTag);
        }

        setRailShape(placedBlock, blockStateTag);
        placedBlock.getState().update(true);
        event.setCancelled(true);
    }

    private static void setRailPower(Block placedBlock, NBTTagCompound blockStateTag) {
        Powerable rail = (Powerable) placedBlock.getBlockData();
        rail.setPowered(Boolean.parseBoolean(blockStateTag.getString("powered")));
        placedBlock.setBlockData(rail);
    }

    private static void setRailShape(Block placedBlock, NBTTagCompound blockStateTag) {
        Rail rail = (Rail) placedBlock.getBlockData();
        rail.setShape(Rail.Shape.valueOf(blockStateTag.getString("shape").toUpperCase()));
        placedBlock.setBlockData(rail);
    }
}
