package io.github.sjouwer.blockenforcer.handlers;

import io.github.sjouwer.blockenforcer.utils.FlowerPotUtil;
import io.github.sjouwer.blockenforcer.utils.ItemUtil;
import net.minecraft.server.v1_14_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.type.Piston;
import org.bukkit.block.data.type.PistonHead;
import org.bukkit.block.data.type.TechnicalPiston;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TechnicalBlockHandler {
    private TechnicalBlockHandler() {
    }

    public static void placePottedPlant(ItemStack item, Block block) {
        if (item.getType() != Material.FLOWER_POT) return;

        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) return;

        block.setType(FlowerPotUtil.convertToMaterial(itemMeta.getDisplayName()), false);
        block.getState().update(true, false);
    }

    public static void placeFrostedIce(ItemStack item, Block block) {
        if (item.getType() != Material.ICE) return;

        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null || !itemMeta.getDisplayName().equals("Frosted Ice")) return;

        block.setType(Material.FROSTED_ICE, false);
        NBTTagCompound blockStateTag = ItemUtil.getBlockStateTag(item);
        if (blockStateTag != null) {
            Ageable frostBlock = (Ageable) block.getBlockData();
            String age = blockStateTag.getString("age");
            if (!age.isEmpty()) frostBlock.setAge(Integer.parseInt(age));
            block.setBlockData(frostBlock, false);
        }

        block.getState().update(true, false);
    }

    public static void placePistonHead(ItemStack item, Block block) {
        if (item.getType() != Material.STICKY_PISTON && item.getType() != Material.PISTON) return;

        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null || !itemMeta.getDisplayName().contains("Piston Head")) return;

        String displayName = itemMeta.getDisplayName();
        BlockFace facing = ((Piston) block.getBlockData()).getFacing();
        block.setType(Material.PISTON_HEAD, false);
        PistonHead pistonBlock = (PistonHead) block.getBlockData();
        pistonBlock.setFacing(facing);
        pistonBlock.setShort(displayName.contains("Short"));
        if (displayName.contains("Sticky")) {
            pistonBlock.setType(TechnicalPiston.Type.STICKY);
        }

        block.setBlockData(pistonBlock, false);
        block.getState().update(true, false);
    }
}
