package io.github.sjouwer.blockenforcer.listeners;

import io.github.sjouwer.blockenforcer.Config;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

public class PistonListener implements Listener {
    @EventHandler
    public void onPistonExtendEvent(BlockPistonExtendEvent event) {
        if (Config.DISABLE_PISTON_MOVEMENT){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPistonRetractEvent(BlockPistonRetractEvent event) {
        if (Config.DISABLE_PISTON_MOVEMENT){
            event.setCancelled(true);
        }
    }
}
