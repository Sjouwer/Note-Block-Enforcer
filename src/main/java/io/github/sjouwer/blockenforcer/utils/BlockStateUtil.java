package io.github.sjouwer.blockenforcer.utils;

import net.minecraft.server.v1_14_R1.NBTTagCompound;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class BlockStateUtil {
    private BlockStateUtil() {
    }

    public static NBTTagCompound getBlockStateTag(ItemStack itemStack) {
        net.minecraft.server.v1_14_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        if (nmsStack.hasTag()) {
            NBTTagCompound compound = nmsStack.getTag();
            NBTTagCompound blockStateTag = compound.getCompound("BlockStateTag");
            if (!blockStateTag.isEmpty()) {
                return blockStateTag;
            }
        }

        return null;
    }
}
