package io.github.sjouwer.blockenforcer.tools;

import io.github.sjouwer.blockenforcer.BlockEnforcer;
import io.github.sjouwer.blockenforcer.utils.BlockUtil;
import io.github.sjouwer.blockenforcer.utils.FlowerPotUtil;
import io.github.sjouwer.blockenforcer.utils.ItemUtil;
import io.github.sjouwer.blockenforcer.utils.NoteBlockUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.block.data.type.PistonHead;
import org.bukkit.block.data.type.TechnicalPiston;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_14_R1.NBTTagCompound;

public class BlockStatePicker {
    private static Material tool = Material.BONE;

    static  {
        String configTool = BlockEnforcer.getPlugin().getConfig().getString("BlockState-Picker-Tool");
        if (configTool != null) {
            Material material = Material.getMaterial(configTool.toUpperCase());
            if (material != null) {
                BlockStatePicker.tool = material;
            }
        }
    }

    private BlockStatePicker() {
    }

    public static Material getTool() {
        return tool;
    }

    public static void provideBlockInfo(Block clickedBlock, Player player) {
        player.sendMessage("----------Block Info----------");
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Block: " + clickedBlock.getType().name().toLowerCase());

        player.sendMessage(ChatColor.AQUA + "States: ");
        BlockUtil.toNMSBlock(clickedBlock).getStateMap().forEach((key, value) ->
                player.sendMessage(ChatColor.AQUA + key.a() + " = " + value.toString()));

        player.sendMessage(ChatColor.GREEN + "Biome: " + clickedBlock.getBiome().name().toLowerCase());
    }

    public static void pickBlockState(Block clickedBlock, Player player) {
        Material clickedMaterial = clickedBlock.getType();
        ItemStack pickedItem;

        if (clickedMaterial == Material.PISTON_HEAD) {
            givePistonHead(clickedBlock, player);
            return;
        }
        else {
            pickedItem = BlockUtil.convertToStack(clickedBlock);
        }

        if (pickedItem == null) {
            return;
        }

        pickedItem = addBlockStateNbt(pickedItem, clickedBlock);
        if (pickedItem.getType() == Material.FLOWER_POT) {
            ItemUtil.nameItemStack(pickedItem, FlowerPotUtil.convertToName(clickedMaterial));
        }
        if (pickedItem.getType() == Material.NOTE_BLOCK && clickedMaterial == Material.NOTE_BLOCK ) {
            ItemUtil.nameItemStack(pickedItem, NoteBlockUtil.getName((NoteBlock) clickedBlock.getBlockData()));
        }
        if (pickedItem.getType() == Material.ICE && clickedMaterial == Material.FROSTED_ICE ) {
            ItemUtil.nameItemStack(pickedItem, "Frosted Ice");
        }

        //Add biome to item when player is sneaking
        if (player.isSneaking()) {
            ItemUtil.addLore(pickedItem, clickedBlock.getBiome().name());
        }

        player.getInventory().setItem(getInventorySlot(player), pickedItem);
        player.updateInventory();
    }

    private static int getInventorySlot(Player player) {
        Inventory inventory = player.getInventory();
        int slot = inventory.firstEmpty();
        if (slot < 0 || slot > 8) {
            slot = inventory.first(tool) + 1;
            if (slot > 8) {
                slot = 0;
            }
        }

        return slot;
    }

    private static void givePistonHead(Block clickedBlock, Player player) {
        ItemStack pickedItem;
        PistonHead pistonHead = ((PistonHead) clickedBlock.getBlockData());
        TechnicalPiston.Type type = pistonHead.getType();
        if (type == TechnicalPiston.Type.STICKY) {
            pickedItem = new ItemStack(Material.STICKY_PISTON);
            ItemUtil.nameItemStack(pickedItem, pistonHead.isShort() ? "Short Sticky Piston Head" : "Sticky Piston Head");
        }
        else {
            pickedItem = new ItemStack(Material.PISTON);
            ItemUtil.nameItemStack(pickedItem, pistonHead.isShort() ? "Short Piston Head" : "Piston Head");
        }

        player.getInventory().setItem(getInventorySlot(player), pickedItem);
        player.updateInventory();
    }

    private static ItemStack addBlockStateNbt(ItemStack stack, Block block) {
        net.minecraft.server.v1_14_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);
        NBTTagCompound propertiesTag = new NBTTagCompound();
        BlockUtil.toNMSBlock(block).getStateMap().forEach((key, value) -> propertiesTag.setString(key.a(), value.toString()));

        if (propertiesTag.isEmpty()) {
            return stack;
        }

        nmsStack.a("BlockStateTag", propertiesTag);
        ItemStack nbtStack = CraftItemStack.asBukkitCopy(nmsStack);
        ItemUtil.addLore(nbtStack, "(+BlockState NBT)");

        return nbtStack;
    }
}
