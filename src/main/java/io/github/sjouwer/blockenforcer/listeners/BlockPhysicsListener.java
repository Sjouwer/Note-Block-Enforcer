package io.github.sjouwer.blockenforcer.listeners;

import io.github.sjouwer.blockenforcer.Config;
import io.github.sjouwer.blockenforcer.handlers.NoteBlockHandler;
import io.github.sjouwer.blockenforcer.handlers.UpdateHandler;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Fence;
import org.bukkit.block.data.type.Gate;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

public class BlockPhysicsListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        Block block = event.getBlock();
        Material material = block.getType();
        boolean cancel = false;

        if (Config.DISABLE_CHORUS_PLACEMENT_RULES &&
                (material == Material.CHORUS_PLANT || material == Material.CHORUS_FLOWER)) {
            cancel = true;
        }

        if (Config.STOP_NOTE_BLOCK_UPDATES) {
            if (material == Material.NOTE_BLOCK) {
                cancel = true;
            }

            Block blockAbove = block.getRelative(BlockFace.UP, 1);
            if (!blockAbove.equals(event.getSourceBlock()) && blockAbove.getType() == Material.NOTE_BLOCK) {
                NoteBlockHandler.updateAllAboveNoteBlocks(block);
                cancel = true;
            }
        }

        if (Config.STOP_DOOR_UPDATES &&
                block.getBlockData() instanceof Door ||
                block.getBlockData() instanceof TrapDoor ||
                block.getBlockData() instanceof Gate) {
            cancel = true;
        }

        if (Config.STOP_DOOR_UPDATES && block.getBlockData() instanceof Fence) {
            UpdateHandler.stopGateChange(block);
        }

        if (Config.STOP_TRIPWIRE_UPDATES) {
            if (material == Material.TRIPWIRE) {
                cancel = true;
            }
            else if(event.getSourceBlock() == block) {
                UpdateHandler.stopTripwireChange(block);
            }
        }

        if (Config.STOP_REDSTONE_UPDATES) {
            if (material == Material.REDSTONE_WIRE) {
                cancel = true;
            }

            if (event.getSourceBlock() == block) {
                UpdateHandler.stopRedstoneChange(block);
            }
        }

        if (cancel) {
            event.setCancelled(true);
            block.getState().update(true, false);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onGravityPhysics(EntitySpawnEvent event) {
        if (Config.STOP_FALLING_BLOCKS &&
                event.getEntityType() == EntityType.FALLING_BLOCK) {
            event.setCancelled(true);
            event.getLocation().getBlock().getState().update(true, false);
        }
    }
}
