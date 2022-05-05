package io.github.sjouwer.blockenforcer.listeners;

import io.github.sjouwer.blockenforcer.BlockEnforcer;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_14_R1.IBlockData;
import net.minecraft.server.v1_14_R1.NBTTagCompound;
import net.minecraft.server.v1_14_R1.NBTTagList;
import net.minecraft.server.v1_14_R1.NBTTagString;
import net.minecraft.server.v1_14_R1.BlockPosition;

import java.util.Optional;

public class PickBlockStateEvent implements Listener {
    private static final boolean ENABLE_BLOCKSTATE_PICKER = BlockEnforcer.getPlugin().getConfig().getBoolean("Enable-BlockState-Picker");
    private static Material tool = Material.BONE;

    static {
        String configTool = BlockEnforcer.getPlugin().getConfig().getString("BlockState-Picker-Tool");
        if (configTool != null) {
            Material material = Material.getMaterial(configTool.toUpperCase());
            if (material != null) {
                tool = material;
            }
        }
    }

    @EventHandler
    public void getBlockStateItem(PlayerInteractEvent event) {
        if (!ENABLE_BLOCKSTATE_PICKER) {
            return;
        }

        Block clickedBlock = event.getClickedBlock();
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || clickedBlock == null) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (player.isSneaking() || player.getGameMode() != GameMode.CREATIVE || item == null || item.getType() != tool || event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        event.setCancelled(true);

        ItemStack pickedItem;
        if (clickedBlock.getType().isItem()) {
            pickedItem = new ItemStack(clickedBlock.getType());
        }
        else {
            Optional<ItemStack> optionalItem = clickedBlock.getDrops().stream().findFirst();
            pickedItem = optionalItem.orElseGet(() -> convertToStack(clickedBlock.getType()));
        }

        if (pickedItem != null) {
            Inventory inventory = player.getInventory();
            int emptySlot = inventory.firstEmpty();
            if (emptySlot < 0 || emptySlot > 8) {
                emptySlot = inventory.first(item) + 1;
                if (emptySlot > 8) {
                    emptySlot = 0;
                }
            }
            player.getInventory().setItem(emptySlot, addBlockStateNbt(pickedItem, clickedBlock));
            player.updateInventory();
        }
    }

    private ItemStack addBlockStateNbt(ItemStack stack, Block block) {
        net.minecraft.server.v1_14_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);
        NBTTagCompound propertiesTag = new NBTTagCompound();
        toNMSBlock(block).getStateMap().forEach((key, value) -> propertiesTag.setString(key.a(), value.toString()));
        nmsStack.a("BlockStateTag", propertiesTag);

        NBTTagCompound loreTag = new NBTTagCompound();
        NBTTagList nbtList = new NBTTagList();
        nbtList.add(new NBTTagString("\"(+BlockState NBT)\""));
        loreTag.set("Lore", nbtList);
        nmsStack.a("display", loreTag);

        return CraftItemStack.asBukkitCopy(nmsStack);
    }

    private IBlockData toNMSBlock(Block block) {
        net.minecraft.server.v1_14_R1.World nmsWorld = ((org.bukkit.craftbukkit.v1_14_R1.CraftWorld)block.getWorld()).getHandle().getMinecraftWorld();
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
