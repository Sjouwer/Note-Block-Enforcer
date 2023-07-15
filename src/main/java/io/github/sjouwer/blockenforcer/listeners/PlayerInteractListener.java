package io.github.sjouwer.blockenforcer.listeners;

import io.github.sjouwer.blockenforcer.Config;
import io.github.sjouwer.blockenforcer.handlers.*;
import io.github.sjouwer.blockenforcer.tools.BlockStatePicker;
import io.github.sjouwer.blockenforcer.utils.BlockUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Door;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

@SuppressWarnings("deprecation")
public class PlayerInteractListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;

        tripwireUpdateCheck(event);
        turtleEggBreakingCheck(event);

        if (event.getHand() == null) return;

        statePickerCheck(event);
        doorUpdateCheck(event);

        if (isInteractable(event.getClickedBlock()) && !event.getPlayer().isSneaking()) return;

        blockPlaceCheck(event);
        plantPlaceCheck(event);
        noteBlockClickCheck(event);
    }

    private boolean isInteractable(Block block) {
        Material type = block.getType();
        return type.isInteractable() &&
                type != Material.NOTE_BLOCK &&
                type != Material.CAKE &&
                !Tag.FENCES.isTagged(type) &&
                !Tag.STAIRS.isTagged(type);
    }

    private void doorUpdateCheck(PlayerInteractEvent event) {
        if (Config.STOP_DOOR_UPDATES &&
                !event.isCancelled() &&
                event.getAction() == Action.RIGHT_CLICK_BLOCK &&
                event.getClickedBlock().getBlockData() instanceof Door &&
                event.getHand() == EquipmentSlot.HAND &&
                (event.getItem() == null || !event.getPlayer().isSneaking())) {

            RedstoneBlockHandler.openDoorWithoutOtherChanges(event.getClickedBlock());
            event.setCancelled(true);
        }
    }

    private void tripwireUpdateCheck(PlayerInteractEvent event) {
        if (Config.STOP_TRIPWIRE_UPDATES &&
                event.getAction() == Action.PHYSICAL &&
                event.getClickedBlock().getType() == Material.TRIPWIRE) {

            event.setCancelled(true);
        }
    }

    private void turtleEggBreakingCheck(PlayerInteractEvent event) {
        if (Config.STOP_TURTLE_EGG_UPDATES &&
                event.getAction() == Action.PHYSICAL &&
                event.getClickedBlock().getType() == Material.TURTLE_EGG) {

            event.setCancelled(true);
        }
    }

    private void noteBlockClickCheck(PlayerInteractEvent event) {
        if (event.isCancelled() ||
                !Config.OVERRIDE_NOTE_BLOCK_CLICK ||
                event.getClickedBlock().getType() != Material.NOTE_BLOCK ||
                event.getAction() != Action.RIGHT_CLICK_BLOCK ||
                (event.getItem() != null && event.getPlayer().isSneaking())) {
            return;
        }

        event.setCancelled(true);

        if (event.getItem() == null) return;
        BlockPlaceHandler.forcePlayerToPlaceBlock(event.getPlayer(), event.getItem(), event.getClickedBlock().getRelative(event.getBlockFace()), event.getHand());
    }

    private void statePickerCheck(PlayerInteractEvent event) {
        if (Config.ENABLE_BLOCKSTATE_PICKER &&
                event.getItem() != null &&
                event.getItem().getType() == Config.getPickerTool() &&
                event.getHand() == EquipmentSlot.HAND) {
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

    private void blockPlaceCheck(PlayerInteractEvent event) {
        if (event.isCancelled() || event.getItem() == null || event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Material blockMaterial = BlockUtil.convertToBlockMaterial(event.getItem());
        if (blockMaterial == null) return;

        BlockData blockData = Bukkit.createBlockData(blockMaterial);
        switch (blockMaterial) {
            case RAIL:
                RedstoneBlockHandler.forcePlaceRail(blockData, event);
                break;

            case ACTIVATOR_RAIL:
            case DETECTOR_RAIL:
            case POWERED_RAIL:
                RedstoneBlockHandler.forcePlaceRedstoneRail(blockData, event);
                break;

            case REDSTONE_WIRE:
                RedstoneBlockHandler.forcePlaceRedstone(blockData, event);
                break;

            case CAKE:
                GeneralBlockHandler.forcePlaceCake(blockData, event);
                break;

            case SEA_PICKLE:
                GeneralBlockHandler.forcePlaceSeaPickle(blockData, event);
                break;

            case SNOW:
                GeneralBlockHandler.forcePlaceSnow(blockData, event);
                break;

            case ACACIA_PRESSURE_PLATE:
            case BIRCH_PRESSURE_PLATE:
            case DARK_OAK_PRESSURE_PLATE:
            case JUNGLE_PRESSURE_PLATE:
            case OAK_PRESSURE_PLATE:
            case SPRUCE_PRESSURE_PLATE:
            case STONE_PRESSURE_PLATE:
                RedstoneBlockHandler.forcePlacePowerable(blockData, event);
                break;

            case HEAVY_WEIGHTED_PRESSURE_PLATE:
            case LIGHT_WEIGHTED_PRESSURE_PLATE:
                RedstoneBlockHandler.forcePlaceWeightedPressurePlate(blockData, event);
                break;

            case REPEATER:
                RedstoneBlockHandler.forcePlaceRepeater(blockData, event);
                break;

            case COMPARATOR:
                RedstoneBlockHandler.forcePlaceComparator(blockData, event);
                break;

            case TRIPWIRE_HOOK:
                RedstoneBlockHandler.forcePlaceTripwireHook(blockData, event);
                break;

            case NOTE_BLOCK:
                NoteBlockHandler.forcePlaceNoteBlock(blockData, event);
                break;

            default:
        }
    }

    private void plantPlaceCheck(PlayerInteractEvent event) {
        if (!Config.DISABLE_PLANT_PLACEMENT_RULES || event.isCancelled() || event.getItem() == null || event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Material blockMaterial = BlockUtil.convertToBlockMaterial(event.getItem());
        if (blockMaterial == null) return;

        BlockData blockData = Bukkit.createBlockData(blockMaterial);
        switch (blockMaterial) {
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
                GeneralBlockHandler.forcePlaceBlock(blockData, event);
                break;

            case WHEAT:
            case BEETROOTS:
            case PUMPKIN_STEM:
            case MELON_STEM:
            case CARROTS:
            case POTATOES:
            case NETHER_WART:
            case CACTUS:
            case SUGAR_CANE:
                PlantHandler.forcePlaceAgingPlant(blockData, event);
                break;

            case TALL_GRASS:
            case LARGE_FERN:
            case SUNFLOWER:
            case LILAC:
            case ROSE_BUSH:
            case PEONY:
                PlantHandler.forcePlaceDoublePlant(blockData, event);
                break;

            case OAK_SAPLING:
            case SPRUCE_SAPLING:
            case BIRCH_SAPLING:
            case JUNGLE_SAPLING:
            case ACACIA_SAPLING:
            case DARK_OAK_SAPLING:
                PlantHandler.forcePlaceSapling(blockData, event);
                break;

            case CHORUS_PLANT:
            case CHORUS_FLOWER:
                ChorusHandler.forcePlaceChorusPlant(blockData, event);
                break;

            case BAMBOO:
                PlantHandler.forcePlaceBamboo(blockData, event);
                break;

            default:
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWEWandDesync(PlayerInteractEvent event) {
        if (
                event.getAction() == Action.LEFT_CLICK_BLOCK &&
                event.getClickedBlock() != null &&
                event.getItem() != null &&
                (
                        (Config.FIX_WE_WAND_DESYNC && event.getItem().getType() == Config.getWEWand()) ||
                        (Config.ENABLE_BLOCKSTATE_PICKER && event.getItem().getType() == Config.getPickerTool())
                )
            )
        {
            Block block = event.getClickedBlock();
            Block blockAbove = block.getRelative(BlockFace.UP, 1);

            NoteBlockHandler.updateAllAboveNoteBlocks(blockAbove);
            RedstoneBlockHandler.updateRedstone(event.getClickedBlock(), true);
        }
    }
}
