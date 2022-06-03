package io.github.sjouwer.blockenforcer.tools;

import io.github.sjouwer.blockenforcer.BlockEnforcer;
import io.github.sjouwer.blockenforcer.utils.FlowerPotUtil;
import io.github.sjouwer.blockenforcer.utils.NoteBlockUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.block.data.type.PistonHead;
import org.bukkit.block.data.type.TechnicalPiston;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.minecraft.server.v1_14_R1.IBlockData;
import net.minecraft.server.v1_14_R1.NBTTagCompound;
import net.minecraft.server.v1_14_R1.BlockPosition;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BlockStatePicker {
    private Material tool = Material.BONE;

    public BlockStatePicker() {
        String configTool = BlockEnforcer.getPlugin().getConfig().getString("BlockState-Picker-Tool");
        if (configTool != null) {
            Material material = Material.getMaterial(configTool.toUpperCase());
            if (material != null) {
                tool = material;
            }
        }
    }

    public Material getTool() {
        return tool;
    }

    public void pickBlockState(Block clickedBlock, Player player) {
        Material clickedMaterial = clickedBlock.getType();
        ItemStack pickedItem;
        if (clickedMaterial.isItem()) {
            pickedItem = new ItemStack(clickedMaterial);
        }
        else if (clickedMaterial == Material.PISTON_HEAD) {
            givePistonHead(clickedBlock, player);
            return;
        }
        else {
            Optional<ItemStack> optionalItem = clickedBlock.getDrops().stream().findFirst();
            pickedItem = optionalItem.orElseGet(() -> convertToStack(clickedMaterial));
        }

        if (pickedItem == null) {
            return;
        }

        pickedItem = addBlockStateNbt(pickedItem, clickedBlock);
        if (pickedItem.getType() == Material.FLOWER_POT) {
            nameItemStack(pickedItem, FlowerPotUtil.convertToName(clickedMaterial));
        }
        if (pickedItem.getType() == Material.NOTE_BLOCK && clickedMaterial == Material.NOTE_BLOCK ) {
            nameItemStack(pickedItem, NoteBlockUtil.getName((NoteBlock) clickedBlock.getBlockData()));
        }
        if (pickedItem.getType() == Material.ICE && clickedMaterial == Material.FROSTED_ICE ) {
            nameItemStack(pickedItem, "Frosted Ice");
        }

        //Add biome to item when player is sneaking
        if (player.isSneaking()) {
            addLore(pickedItem, clickedBlock.getBiome().name());
        }

        player.getInventory().setItem(getInventorySlot(player), pickedItem);
        player.updateInventory();
    }

    private int getInventorySlot(Player player) {
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

    private void nameItemStack(ItemStack item, String name) {
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(name);
            item.setItemMeta(itemMeta);
        }
    }

    private void addLore(ItemStack item, String lore) {
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta != null) {
            List<String> loreList;
            if (itemMeta.hasLore()) {
                loreList = itemMeta.getLore();
            }
            else {
                loreList = new ArrayList<>();
            }
            loreList.add(lore);
            itemMeta.setLore(loreList);
            item.setItemMeta(itemMeta);
        }
    }

    private void givePistonHead(Block clickedBlock, Player player) {
        ItemStack pickedItem;
        PistonHead pistonHead = ((PistonHead) clickedBlock.getBlockData());
        TechnicalPiston.Type type = pistonHead.getType();
        if (type == TechnicalPiston.Type.STICKY) {
            pickedItem = new ItemStack(Material.STICKY_PISTON);
            nameItemStack(pickedItem, pistonHead.isShort() ? "Short Sticky Piston Head" : "Sticky Piston Head");
        }
        else {
            pickedItem = new ItemStack(Material.PISTON);
            nameItemStack(pickedItem, pistonHead.isShort() ? "Short Piston Head" : "Piston Head");
        }

        player.getInventory().setItem(getInventorySlot(player), pickedItem);
        player.updateInventory();
    }

    private ItemStack addBlockStateNbt(ItemStack stack, Block block) {
        net.minecraft.server.v1_14_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);
        NBTTagCompound propertiesTag = new NBTTagCompound();
        toNMSBlock(block).getStateMap().forEach((key, value) -> propertiesTag.setString(key.a(), value.toString()));

        if (propertiesTag.isEmpty()) {
            return stack;
        }

        nmsStack.a("BlockStateTag", propertiesTag);
        ItemStack nbtStack = CraftItemStack.asBukkitCopy(nmsStack);
        addLore(nbtStack, "(+BlockState NBT)");

        return nbtStack;
    }

    private IBlockData toNMSBlock(Block block) {
        net.minecraft.server.v1_14_R1.World nmsWorld = ((CraftWorld) block.getWorld()).getHandle().getMinecraftWorld();
        BlockPosition blockPos = new BlockPosition(block.getX(), block.getY(), block.getZ());

        return nmsWorld.getType(blockPos);
    }

    private ItemStack convertToStack(Material material) {
        switch (material) {
            case BRAIN_CORAL_WALL_FAN: return new ItemStack(Material.BRAIN_CORAL_FAN);
            case BUBBLE_CORAL_WALL_FAN: return new ItemStack(Material.BUBBLE_CORAL_FAN);
            case FIRE_CORAL_WALL_FAN: return new ItemStack(Material.FIRE_CORAL_FAN);
            case HORN_CORAL_WALL_FAN: return new ItemStack(Material.HORN_CORAL_FAN);
            case TUBE_CORAL_WALL_FAN: return new ItemStack(Material.TUBE_CORAL_FAN);
            case DEAD_BRAIN_CORAL_WALL_FAN: return new ItemStack(Material.DEAD_BRAIN_CORAL_FAN);
            case DEAD_BUBBLE_CORAL_WALL_FAN: return new ItemStack(Material.DEAD_BUBBLE_CORAL_FAN);
            case DEAD_FIRE_CORAL_WALL_FAN: return new ItemStack(Material.DEAD_FIRE_CORAL_FAN);
            case DEAD_HORN_CORAL_WALL_FAN: return new ItemStack(Material.DEAD_HORN_CORAL_FAN);
            case DEAD_TUBE_CORAL_WALL_FAN: return new ItemStack(Material.DEAD_TUBE_CORAL_FAN);
            case FROSTED_ICE: return new ItemStack(Material.ICE);
            default: return null;
        }
    }
}
