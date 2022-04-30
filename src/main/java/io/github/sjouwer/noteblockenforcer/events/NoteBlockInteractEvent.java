package io.github.sjouwer.noteblockenforcer.events;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_14_R1.EntityPlayer;
import net.minecraft.server.v1_14_R1.EnumHand;
import net.minecraft.server.v1_14_R1.ItemActionContext;
import net.minecraft.server.v1_14_R1.MovingObjectPositionBlock;
import net.minecraft.server.v1_14_R1.EntityHuman;
import net.minecraft.server.v1_14_R1.Vec3D;
import net.minecraft.server.v1_14_R1.BlockPosition;

public class NoteBlockInteractEvent implements Listener {
    @EventHandler
    public void placeBlockInstead(PlayerInteractEvent event) {
        Block clickedBlock = event.getClickedBlock();
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || clickedBlock == null || clickedBlock.getType() != Material.NOTE_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        if (player.isSneaking() && player.getInventory().getItemInMainHand().getType() != Material.AIR) {
            return;
        }

        event.setCancelled(true);

        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }

        EquipmentSlot hand = event.getHand();
        if (hand == null) {
            return;
        }

        int itemAmount = item.getAmount();
        if (player.getGameMode() == GameMode.CREATIVE && itemAmount == 1) {
            item.setAmount(2);
        }

        Block relativeBlock = clickedBlock.getRelative(event.getBlockFace());
        placeItem(player, item, relativeBlock, hand);

        if (player.getGameMode() == GameMode.CREATIVE) {
            item.setAmount(itemAmount);
        }
    }

    private void placeItem(Player player, ItemStack item, Block block, EquipmentSlot slot) {
        EntityPlayer human = ((CraftPlayer) player).getHandle();
        EnumHand hand = slot == EquipmentSlot.OFF_HAND ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
        net.minecraft.server.v1_14_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        MovingObjectPositionBlock mopb = (MovingObjectPositionBlock) genMOPB(player, block);

        nmsItem.placeItem(new ItemActionContext(human, hand, mopb), hand);
    }

    private Object genMOPB(Player player, Block block) {
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
