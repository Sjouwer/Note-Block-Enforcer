package io.github.sjouwer.blockenforcer.listeners;

import io.github.sjouwer.blockenforcer.BlockEnforcer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class NoteBlockPhysicsEvent implements Listener {
    private static final boolean STOP_NOTE_BLOCK_UPDATES = BlockEnforcer.getPlugin().getConfig().getBoolean("Stop-NoteBlock-Updates");
    private static final boolean FIX_WE_WAND_DESYNC = BlockEnforcer.getPlugin().getConfig().getBoolean("Fix_WE_Wand_Desync");
    private static Material weWand = Material.WOODEN_AXE;

    static {
        String configTool = BlockEnforcer.getPlugin().getConfig().getString("WE-Wand");
        if (configTool != null) {
            Material material = Material.getMaterial(configTool.toUpperCase());
            if (material != null) {
                weWand = material;
            }
        }
    }

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
        updateNoteBlocks(blockAbove);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void stopWEWandDesync(PlayerInteractEvent event) {
        if (!FIX_WE_WAND_DESYNC) {
            return;
        }

        if (event.getAction() != Action.LEFT_CLICK_BLOCK || event.getClickedBlock() == null || event.getItem() == null || event.getItem().getType() != weWand) {
            return;
        }

        Block blockAbove = event.getClickedBlock().getLocation().add(0, 1, 0).getBlock();
        updateNoteBlocks(blockAbove);
    }

    private void updateNoteBlocks(Block blockAbove) {
        while (blockAbove.getType() == Material.NOTE_BLOCK) {
            blockAbove.getState().update(true, true);
            blockAbove = blockAbove.getLocation().add(0, 1, 0).getBlock();
        }
    }
}
