package io.github.sjouwer.blockenforcer.handlers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class ChorusPlaceHandler {
    private ChorusPlaceHandler() {
    }

    public static void forcePlaceChorusPlant(PlayerInteractEvent event) {
        if (event.getItem() == null) {
            return;
        }

        Block placedBlock = BlockPlaceHandler.placeBlock(event);
        if (placedBlock == null) {
            return;
        }

        if (event.getItem().getType() == Material.CHORUS_PLANT) {
            connectChorusPlants(placedBlock, event.getBlockFace(), event.getPlayer());
        }
        if (event.getItem().getType() == Material.CHORUS_FLOWER) {
            updateChorusFacing(placedBlock.getLocation(), null, event.getBlockFace().getOppositeFace());
        }

        event.setCancelled(true);
    }

    private static void connectChorusPlants(Block placedBlock, BlockFace placedFace, Player player) {
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

    private static void updateChorusFacing(Location placedLocation, MultipleFacing placedChorus, BlockFace blockFace) {
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
}
