package io.github.sjouwer.blockenforcer.listeners;

import io.github.sjouwer.blockenforcer.BlockEnforcer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;

public class NoteBlockPhysicsEvent implements Listener {
    private static final boolean STOP_NOTE_BLOCK_UPDATES = BlockEnforcer.getPlugin().getConfig().getBoolean("Stop-NoteBlock-Updates");

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void stopNoteBlockPhysics(BlockPhysicsEvent event) {
        if (!STOP_NOTE_BLOCK_UPDATES) {
            return;
        }

        Block block = event.getBlock();
        if (block.getType() == Material.NOTE_BLOCK) {
            event.setCancelled(true);
        }

        Block blockAbove = block.getLocation().add(0, 1, 0).getBlock();
        if (blockAbove.getType() != Material.NOTE_BLOCK) {
            return;
        }

        event.setCancelled(true);
        block.getState().update(true, false);
        while (blockAbove.getType() == Material.NOTE_BLOCK) {
            blockAbove.getState().update(true, true);
            blockAbove = blockAbove.getLocation().add(0, 1, 0).getBlock();
        }
    }
}
