package io.github.sjouwer.blockenforcer.listeners;

import io.github.sjouwer.blockenforcer.BlockEnforcer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;

public class ChorusPhysicsEvent implements Listener {
    private static final boolean DISABLE_CHORUS_PLACEMENT_RULES = BlockEnforcer.getPlugin().getConfig().getBoolean("Disable-Chorus-Placement-Rules");

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void stopChorusUpdates(BlockPhysicsEvent event) {
        if (!DISABLE_CHORUS_PLACEMENT_RULES) {
            return;
        }

        Block block = event.getBlock();
        if (block.getType() == Material.CHORUS_PLANT || block.getType() == Material.CHORUS_FLOWER) {
            event.setCancelled(true);
            block.getState().update(true, false);
        }
    }
}
