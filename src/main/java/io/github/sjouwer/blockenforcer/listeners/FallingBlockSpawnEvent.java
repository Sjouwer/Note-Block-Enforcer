package io.github.sjouwer.blockenforcer.listeners;

import io.github.sjouwer.blockenforcer.BlockEnforcer;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class FallingBlockSpawnEvent implements Listener {
    private static final boolean STOP_FALLING_BLOCKS = BlockEnforcer.getPlugin().getConfig().getBoolean("Stop-Falling-Blocks");

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void stopGravityPhysics(EntitySpawnEvent event) {
        if (!STOP_FALLING_BLOCKS) {
            return;
        }

        if (event.getEntityType() == EntityType.FALLING_BLOCK) {
            event.setCancelled(true);
            event.getLocation().getBlock().getState().update(true, false);
        }
    }
}
