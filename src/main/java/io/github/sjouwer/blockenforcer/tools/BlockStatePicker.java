package io.github.sjouwer.blockenforcer.tools;

import io.github.sjouwer.blockenforcer.BlockEnforcer;
import io.github.sjouwer.blockenforcer.utils.FlowerPotUtil;
import io.github.sjouwer.blockenforcer.utils.NoteBlockUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.minecraft.server.v1_14_R1.IBlockData;
import net.minecraft.server.v1_14_R1.NBTTagCompound;
import net.minecraft.server.v1_14_R1.NBTTagList;
import net.minecraft.server.v1_14_R1.NBTTagString;
import net.minecraft.server.v1_14_R1.BlockPosition;

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
        else {
            Optional<ItemStack> optionalItem = clickedBlock.getDrops().stream().findFirst();
            pickedItem = optionalItem.orElseGet(() -> convertToStack(clickedMaterial));
        }

        if (pickedItem == null) {
            return;
        }

        pickedItem = addBlockStateNbt(pickedItem, clickedBlock);
        if (pickedItem.getType() == Material.FLOWER_POT || pickedItem.getType() == Material.NOTE_BLOCK) {
            ItemMeta itemMeta = pickedItem.getItemMeta();
            if (itemMeta != null) {
                if (pickedItem.getType() == Material.FLOWER_POT) {
                    itemMeta.setDisplayName(FlowerPotUtil.convertToName(clickedMaterial));
                }
                if (pickedItem.getType() == Material.NOTE_BLOCK && clickedMaterial == Material.NOTE_BLOCK ) {
                    itemMeta.setDisplayName(NoteBlockUtil.getName((NoteBlock) clickedBlock.getBlockData()));
                }

                pickedItem.setItemMeta(itemMeta);
            }
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

    private ItemStack addBlockStateNbt(ItemStack stack, Block block) {
        net.minecraft.server.v1_14_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);
        NBTTagCompound propertiesTag = new NBTTagCompound();
        toNMSBlock(block).getStateMap().forEach((key, value) -> propertiesTag.setString(key.a(), value.toString()));

        if (propertiesTag.isEmpty()) {
            return stack;
        }

        nmsStack.a("BlockStateTag", propertiesTag);

        NBTTagCompound loreTag = new NBTTagCompound();
        NBTTagList nbtList = new NBTTagList();
        nbtList.add(new NBTTagString("\"(+BlockState NBT)\""));
        loreTag.set("Lore", nbtList);
        nmsStack.a("display", loreTag);

        return CraftItemStack.asBukkitCopy(nmsStack);
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
            default: return null;
        }
    }
}
