package io.github.sjouwer.blockenforcer.listeners;

import io.github.sjouwer.blockenforcer.BlockEnforcer;
import io.github.sjouwer.blockenforcer.handlers.NoteBlockHandler;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

public class BlockPhysicsListener implements Listener {
    private static final boolean STOP_NOTE_BLOCK_UPDATES = BlockEnforcer.getPlugin().getConfig().getBoolean("Stop-NoteBlock-Updates");
    private static final boolean DISABLE_CHORUS_PLACEMENT_RULES = BlockEnforcer.getPlugin().getConfig().getBoolean("Disable-Chorus-Placement-Rules");
    private static final boolean STOP_FALLING_BLOCKS = BlockEnforcer.getPlugin().getConfig().getBoolean("Stop-Falling-Blocks");

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        Block block = event.getBlock();
        boolean cancel = false;

        if (DISABLE_CHORUS_PLACEMENT_RULES &&
                (block.getType() == Material.CHORUS_PLANT || block.getType() == Material.CHORUS_FLOWER)) {
            cancel = true;
        }

        if (STOP_NOTE_BLOCK_UPDATES) {
            if (block.getType() == Material.NOTE_BLOCK) {
                cancel = true;
            }

            Block blockAbove = block.getLocation().add(0, 1, 0).getBlock();
            if (blockAbove.getType() == Material.NOTE_BLOCK) {
                NoteBlockHandler.updateAllAboveNoteBlocks(blockAbove);
                cancel = true;
            }
        }

        if (cancel) {
            event.setCancelled(true);
            block.getState().update(true, false);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onGravityPhysics(EntitySpawnEvent event) {
        if (STOP_FALLING_BLOCKS &&
                event.getEntityType() == EntityType.FALLING_BLOCK) {
            event.setCancelled(true);
            event.getLocation().getBlock().getState().update(true, false);
        }
    }
}
