package io.github.sjouwer.blockenforcer.handlers;

import io.github.sjouwer.blockenforcer.utils.BlockUtil;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class BlockPlaceHandler {
    private BlockPlaceHandler() {
    }

    public static Block placeBlock(PlayerInteractEvent event, BlockData dataOverride) {
        return BlockPlaceHandler.placeBlock(event, dataOverride, null);
    }

    public static Block placeBlock(PlayerInteractEvent event, BlockData blockData, Block blockOverride) {
        Block clickedBlock = event.getClickedBlock();
        ItemStack item = event.getItem();

        Block placementBlock = blockOverride == null ? getPlacementBlock(clickedBlock, event.getBlockFace()) : blockOverride;
        if (placementBlock == null) return null;

        BlockState replacedBlockState = placementBlock.getState();
        boolean waterLogged = placementBlock.getType() == Material.WATER;

        placementBlock.setBlockData(blockData, false);
        if (placementBlock.getBlockData() instanceof Waterlogged) {
            Waterlogged waterLoggable = ((Waterlogged) placementBlock.getBlockData());
            waterLoggable.setWaterlogged(waterLogged);
            placementBlock.setBlockData(waterLoggable);
        }

        BlockPlaceEvent blockPlaceEvent = new BlockPlaceEvent(placementBlock, replacedBlockState, clickedBlock, item, event.getPlayer(), true, event.getHand());
        Bukkit.getPluginManager().callEvent(blockPlaceEvent);
        if (blockPlaceEvent.isCancelled()) {
            placementBlock.setBlockData(replacedBlockState.getBlockData(), false);
            return null;
        }

        RedstoneBlockHandler.stopRedstoneChange(placementBlock);
        return placementBlock;
    }

    private static Block getPlacementBlock(Block clickedBlock, BlockFace face) {
        if (BlockUtil.isReplaceable(clickedBlock)) return clickedBlock;

        Block relativeBlock = clickedBlock.getRelative(face);
        if (BlockUtil.isReplaceable(relativeBlock)) return relativeBlock;

        return null;
    }

    public static void forcePlayerToPlaceBlock(Player player, ItemStack item, Block block, EquipmentSlot slot) {
        int itemAmount = item.getAmount();
        if (player.getGameMode() == GameMode.CREATIVE && itemAmount == 1) {
            item.setAmount(2);
        }

        EntityPlayer human = ((CraftPlayer) player).getHandle();
        EnumHand hand = slot == EquipmentSlot.OFF_HAND ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
        net.minecraft.server.v1_14_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        MovingObjectPositionBlock mopb = (MovingObjectPositionBlock) genMOPB(player, block);

        nmsItem.placeItem(new ItemActionContext(human, hand, mopb), hand);

        if (player.getGameMode() == GameMode.CREATIVE) {
            item.setAmount(itemAmount);
        }
    }

    private static Object genMOPB(Player player, Block block) {
        Location source = player.getEyeLocation();
        EntityHuman human = ((CraftPlayer) player).getHandle();
        return new MovingObjectPositionBlock(
                new Vec3D(source.getX(), source.getY(), source.getZ()),
                human.getDirection(),
                new BlockPosition(block.getX(), block.getY(), block.getZ()),
                false
        );
    }
}
