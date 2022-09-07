package io.github.sjouwer.blockenforcer.listeners;

import io.github.sjouwer.blockenforcer.BlockEnforcer;
import io.github.sjouwer.blockenforcer.handlers.ChorusPlaceHandler;
import io.github.sjouwer.blockenforcer.handlers.PlantPlaceHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlantPlaceEvent implements Listener {
    private static final boolean DISABLE_PLANT_PLACEMENT_RULES = BlockEnforcer.getPlugin().getConfig().getBoolean("Disable-Plant-Placement-Rules");

    @EventHandler
    public void forcePlaceChorusPlant(PlayerInteractEvent event) {
        if (!DISABLE_PLANT_PLACEMENT_RULES) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getClickedBlock() == null || event.getItem() == null || event.getHand() == null) {
            return;
        }

        switch (event.getItem().getType()) {
            case GRASS:
            case FERN:
            case DEAD_BUSH:
            case DANDELION:
            case POPPY:
            case BLUE_ORCHID:
            case ALLIUM:
            case AZURE_BLUET:
            case RED_TULIP:
            case ORANGE_TULIP:
            case WHITE_TULIP:
            case PINK_TULIP:
            case OXEYE_DAISY:
            case CORNFLOWER:
            case LILY_OF_THE_VALLEY:
            case WITHER_ROSE:
            case RED_MUSHROOM:
            case BROWN_MUSHROOM:
            case LILY_PAD:
                PlantPlaceHandler.forcePlacePlant(event);
                break;

            case WHEAT_SEEDS:
            case WHEAT:
            case BEETROOT_SEEDS:
            case MELON_SEEDS:
            case PUMPKIN_SEEDS:
            case POTATO:
            case CARROT:
            case NETHER_WART:
            case CACTUS:
            case SUGAR_CANE:
                PlantPlaceHandler.forcePlaceAgingPlant(event);
                break;

            case TALL_GRASS:
            case LARGE_FERN:
            case SUNFLOWER:
            case LILAC:
            case ROSE_BUSH:
            case PEONY:
                PlantPlaceHandler.forcePlaceDoublePlant(event);
                break;

            case OAK_SAPLING:
            case SPRUCE_SAPLING:
            case BIRCH_SAPLING:
            case JUNGLE_SAPLING:
            case ACACIA_SAPLING:
            case DARK_OAK_SAPLING:
                PlantPlaceHandler.forcePlaceSapling(event);
                break;

            case CHORUS_PLANT:
            case CHORUS_FLOWER:
                ChorusPlaceHandler.forcePlaceChorusPlant(event);
                break;

            case BAMBOO:
                PlantPlaceHandler.forcePlaceBamboo(event);
                break;

            default:
        }

    }
}
