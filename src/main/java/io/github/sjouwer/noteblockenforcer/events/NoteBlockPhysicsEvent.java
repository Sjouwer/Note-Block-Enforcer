package io.github.sjouwer.noteblockenforcer.events;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;

public class NoteBlockPhysicsEvent implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void stopNoteBlockPhysics(BlockPhysicsEvent event) {
        Block blockAbove = event.getBlock().getLocation().add(0, 1, 0).getBlock();
        while (blockAbove.getType() == Material.NOTE_BLOCK) {
            event.setCancelled(true);
            blockAbove.getState().update(true, true);
            blockAbove = blockAbove.getLocation().add(0, 1, 0).getBlock();
        }

        if (event.getBlock().getType() == Material.NOTE_BLOCK) {
            event.setCancelled(true);
        }

        event.getBlock().getState().update(true, false);
    }
}
