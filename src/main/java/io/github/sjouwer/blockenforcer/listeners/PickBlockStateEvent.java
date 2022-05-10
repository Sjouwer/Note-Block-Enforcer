package io.github.sjouwer.blockenforcer.listeners;

import io.github.sjouwer.blockenforcer.BlockEnforcer;
import io.github.sjouwer.blockenforcer.tools.BlockStatePicker;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class PickBlockStateEvent implements Listener {
    private static final boolean ENABLE_BLOCKSTATE_PICKER = BlockEnforcer.getPlugin().getConfig().getBoolean("Enable-BlockState-Picker");
    private static final BlockStatePicker picker = new BlockStatePicker();

    @EventHandler
    public void getBlockStateItem(PlayerInteractEvent event) {
        if (!ENABLE_BLOCKSTATE_PICKER) {
            return;
        }

        Block clickedBlock = event.getClickedBlock();
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || clickedBlock == null) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (player.isSneaking() || player.getGameMode() != GameMode.CREATIVE || item == null || item.getType() != picker.getTool() || event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        event.setCancelled(true);
        picker.pickBlockState(clickedBlock, player);
    }
}
