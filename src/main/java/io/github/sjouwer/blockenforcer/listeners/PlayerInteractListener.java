package io.github.sjouwer.blockenforcer.listeners;

import io.github.sjouwer.blockenforcer.BlockEnforcer;
import io.github.sjouwer.blockenforcer.handlers.BlockPlaceHandler;
import io.github.sjouwer.blockenforcer.handlers.ChorusHandler;
import io.github.sjouwer.blockenforcer.handlers.NoteBlockHandler;
import io.github.sjouwer.blockenforcer.handlers.PlantHandler;
import io.github.sjouwer.blockenforcer.tools.BlockStatePicker;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class PlayerInteractListener implements Listener {
    private static final boolean ENABLE_BLOCKSTATE_PICKER = BlockEnforcer.getPlugin().getConfig().getBoolean("Enable-BlockState-Picker");
    private static final boolean DISABLE_PLANT_PLACEMENT_RULES = BlockEnforcer.getPlugin().getConfig().getBoolean("Disable-Plant-Placement-Rules");
    private static final boolean OVERRIDE_NOTE_BLOCK_CLICK = BlockEnforcer.getPlugin().getConfig().getBoolean("Override-NoteBlock-Right-Click");
    private static final boolean FIX_WE_WAND_DESYNC = BlockEnforcer.getPlugin().getConfig().getBoolean("Fix_WE_Wand_Desync");
    private static Material weWand = Material.WOODEN_AXE;

    static {
        String configWand = BlockEnforcer.getPlugin().getConfig().getString("WE-Wand");
        if (configWand != null) {
            Material material = Material.getMaterial(configWand.toUpperCase());
            if (material != null) {
                weWand = material;
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null || event.getHand() == null) {
            return;
        }

        noteBlockClickCheck(event);

        if (event.getItem() == null) {
            return;
        }

        statePickerCheck(event);
        plantPlaceCheck(event);
    }

    private void noteBlockClickCheck(PlayerInteractEvent event) {
        if (OVERRIDE_NOTE_BLOCK_CLICK &&
                event.getClickedBlock().getType() == Material.NOTE_BLOCK &&
                event.getAction() == Action.RIGHT_CLICK_BLOCK &&
                (event.getItem() == null || !event.getPlayer().isSneaking())
        ) {
            event.setCancelled(true);
            if (event.getItem() != null) {
                BlockPlaceHandler.forcePlayerToPlaceBlock(event.getPlayer(), event.getItem(), event.getClickedBlock().getRelative(event.getBlockFace()), event.getHand());
            }
        }
    }

    private void statePickerCheck(PlayerInteractEvent event) {
        if (ENABLE_BLOCKSTATE_PICKER &&
                event.getItem().getType() == BlockStatePicker.getTool() &&
                event.getHand() == EquipmentSlot.HAND
        ) {
            event.setCancelled(true);
            if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                BlockStatePicker.provideBlockInfo(event.getClickedBlock(), event.getPlayer());
                return;
            }

            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                BlockStatePicker.pickBlockState(event.getClickedBlock(), event.getPlayer());
            }
        }
    }

    private void plantPlaceCheck(PlayerInteractEvent event) {
        if (DISABLE_PLANT_PLACEMENT_RULES && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
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
                    PlantHandler.forcePlacePlant(event);
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
                    PlantHandler.forcePlaceAgingPlant(event);
                    break;

                case TALL_GRASS:
                case LARGE_FERN:
                case SUNFLOWER:
                case LILAC:
                case ROSE_BUSH:
                case PEONY:
                    PlantHandler.forcePlaceDoublePlant(event);
                    break;

                case OAK_SAPLING:
                case SPRUCE_SAPLING:
                case BIRCH_SAPLING:
                case JUNGLE_SAPLING:
                case ACACIA_SAPLING:
                case DARK_OAK_SAPLING:
                    PlantHandler.forcePlaceSapling(event);
                    break;

                case CHORUS_PLANT:
                case CHORUS_FLOWER:
                    ChorusHandler.forcePlaceChorusPlant(event);
                    break;

                case BAMBOO:
                    PlantHandler.forcePlaceBamboo(event);
                    break;

                default:
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onWEWandDesync(PlayerInteractEvent event) {
        if (FIX_WE_WAND_DESYNC &&
                event.getAction() == Action.LEFT_CLICK_BLOCK &&
                event.getClickedBlock() != null &&
                event.getItem() != null &&
                event.getItem().getType() == weWand
        ) {
            Block blockAbove = event.getClickedBlock().getRelative(BlockFace.UP, 1);
            NoteBlockHandler.updateAllAboveNoteBlocks(blockAbove);
        }
    }
}
