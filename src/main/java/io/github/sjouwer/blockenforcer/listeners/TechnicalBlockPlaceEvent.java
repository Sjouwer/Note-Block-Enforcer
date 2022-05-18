package io.github.sjouwer.blockenforcer.listeners;

import io.github.sjouwer.blockenforcer.utils.BlockStateUtil;
import io.github.sjouwer.blockenforcer.utils.FlowerPotUtil;
import net.minecraft.server.v1_14_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.type.Piston;
import org.bukkit.block.data.type.PistonHead;
import org.bukkit.block.data.type.TechnicalPiston;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TechnicalBlockPlaceEvent implements Listener {
    @EventHandler
    public void placePottedPlant(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        ItemStack item = event.getItemInHand();
        if (block.getType() != Material.FLOWER_POT || item.getType() != Material.FLOWER_POT) {
            return;
        }

        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) {
            return;
        }

        block.setType(FlowerPotUtil.convertToMaterial(itemMeta.getDisplayName()));
        block.getState().update(true);
    }

    @EventHandler
    public void placeFrostedIce(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        ItemStack item = event.getItemInHand();
        if (block.getType() != Material.ICE || item.getType() != Material.ICE) {
            return;
        }

        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null || !itemMeta.getDisplayName().equals("Frosted Ice")) {
            return;
        }

        block.setType(Material.FROSTED_ICE);
        NBTTagCompound blockStateTag = BlockStateUtil.getBlockStateTag(item);
        if (blockStateTag != null) {
            Ageable frostBlock = (Ageable) block.getBlockData();
            frostBlock.setAge(Integer.parseInt(blockStateTag.getString("age")));
            block.setBlockData(frostBlock);
        }

        block.getState().update(true);
    }

    @EventHandler
    public void placePistonHead(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        ItemStack item = event.getItemInHand();
        if ((block.getType() != Material.STICKY_PISTON || item.getType() != Material.STICKY_PISTON) && (block.getType() != Material.PISTON || item.getType() != Material.PISTON)) {
            return;
        }

        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null || !itemMeta.getDisplayName().contains("Piston Head")) {
            return;
        }

        String displayName = itemMeta.getDisplayName();
        BlockFace facing = ((Piston) block.getBlockData()).getFacing();
        block.setType(Material.PISTON_HEAD);
        PistonHead pistonBlock = (PistonHead) block.getBlockData();
        pistonBlock.setFacing(facing);
        pistonBlock.setShort(displayName.contains("Short"));
        if (displayName.contains("Sticky")) {
            pistonBlock.setType(TechnicalPiston.Type.STICKY);
        }

        block.setBlockData(pistonBlock);
        block.getState().update(true);
    }
}
