package io.github.sjouwer.blockenforcer.listeners;

import io.github.sjouwer.blockenforcer.Config;
import io.github.sjouwer.blockenforcer.handlers.RedstoneBlockHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreakEvent(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (Config.STOP_REDSTONE_UPDATES && block.getType() == Material.REDSTONE_WIRE) {
            event.setCancelled(true);
            block.setBlockData(Bukkit.createBlockData(Material.AIR), false);
            block.getState().update(true, false);
            RedstoneBlockHandler.updateRedstone(block, true);
        }
    }
}
