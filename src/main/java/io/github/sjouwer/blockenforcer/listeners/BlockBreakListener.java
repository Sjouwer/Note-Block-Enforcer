package io.github.sjouwer.blockenforcer.listeners;

import io.github.sjouwer.blockenforcer.Config;
import io.github.sjouwer.blockenforcer.handlers.RedstoneBlockHandler;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {
        if (Config.STOP_REDSTONE_UPDATES && event.getBlock().getType() == Material.REDSTONE_WIRE) {
            RedstoneBlockHandler.stopRedstoneChangeByRedstone(event.getBlock());
        }
    }
}
