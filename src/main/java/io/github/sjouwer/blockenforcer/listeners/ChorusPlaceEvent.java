package io.github.sjouwer.blockenforcer.listeners;

import io.github.sjouwer.blockenforcer.BlockEnforcer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ChorusPlaceEvent implements Listener {
    private static final boolean DISABLE_CHORUS_PLACEMENT_RULES = BlockEnforcer.getPlugin().getConfig().getBoolean("Disable-Chorus-Placement-Rules");

    @EventHandler
    public void forcePlaceChorusPlant(PlayerInteractEvent event) {
        if (!DISABLE_CHORUS_PLACEMENT_RULES) {
            return;
        }

        Block clickedBlock = event.getClickedBlock();
        ItemStack item = event.getItem();
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || clickedBlock == null || item == null || event.getHand() == null ||
                (item.getType() != Material.CHORUS_PLANT && item.getType() != Material.CHORUS_FLOWER)) {
            return;
        }

        Block relativeBlock = clickedBlock.getRelative(event.getBlockFace());
        Block placementBlock;
        if (isReplaceable(clickedBlock)) {
            placementBlock = clickedBlock;
        }
        else if (isReplaceable(relativeBlock)) {
            placementBlock = relativeBlock;
        }
        else {
            return;
        }

        BlockState replacedBlockState = placementBlock.getState();
        placementBlock.setType(item.getType(), false);
        BlockPlaceEvent blockPlaceEvent = new BlockPlaceEvent(placementBlock, replacedBlockState, clickedBlock, item, event.getPlayer(), true, event.getHand());
        Bukkit.getPluginManager().callEvent(blockPlaceEvent);
        if (blockPlaceEvent.isCancelled()) {
            placementBlock.setBlockData(replacedBlockState.getBlockData(), false);
            return;
        }

        if (item.getType() == Material.CHORUS_PLANT) {
            connectChorusPlants(placementBlock, event.getBlockFace(), event.getPlayer());
        }
        if (item.getType() == Material.CHORUS_FLOWER) {
            updateChorusFacing(placementBlock.getLocation(), null, event.getBlockFace().getOppositeFace());
        }

        event.setCancelled(true);
    }

    private void connectChorusPlants(Block placedBlock, BlockFace placedFace, Player player) {
        Location placedLocation = placedBlock.getLocation();
        MultipleFacing placedChorus = (MultipleFacing) placedBlock.getBlockData();
        placedChorus.setFace(placedFace.getOppositeFace(), true);

        if (player.isSneaking()) {
            updateChorusFacing(placedLocation, placedChorus, placedFace.getOppositeFace());
        }
        else {
            updateChorusFacing(placedLocation, placedChorus, BlockFace.EAST);
            updateChorusFacing(placedLocation, placedChorus, BlockFace.WEST);
            updateChorusFacing(placedLocation, placedChorus, BlockFace.NORTH);
            updateChorusFacing(placedLocation, placedChorus, BlockFace.SOUTH);
            updateChorusFacing(placedLocation, placedChorus, BlockFace.UP);
            updateChorusFacing(placedLocation, placedChorus, BlockFace.DOWN);
        }

        placedBlock.setBlockData(placedChorus);
        placedBlock.getState().update(true, false);
    }

    private void updateChorusFacing(Location placedLocation, MultipleFacing placedChorus, BlockFace blockFace) {
        Block neighborBlock = placedLocation.clone().add(blockFace.getDirection()).getBlock();
        if (neighborBlock.getType() == Material.CHORUS_PLANT) {
            if (placedChorus != null) {
                placedChorus.setFace(blockFace, true);
            }

            MultipleFacing neighborChorus = (MultipleFacing) neighborBlock.getBlockData();
            neighborChorus.setFace(blockFace.getOppositeFace(), true);
            neighborBlock.setBlockData(neighborChorus);
            neighborBlock.getState().update(true, false);
        }
    }

    private boolean isReplaceable(Block block) {
        if (block.isEmpty() || block.isLiquid()) {
            return true;
        }

        switch (block.getType()) {
            case GRASS:
            case TALL_GRASS:
            case FERN:
            case LARGE_FERN:
            case SEAGRASS:
            case TALL_SEAGRASS:
            case DEAD_BUSH:
            case VINE:
            case FIRE:
                return true;
            default:
                return false;
        }
    }
}
