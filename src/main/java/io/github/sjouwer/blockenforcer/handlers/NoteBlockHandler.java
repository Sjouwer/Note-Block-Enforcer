package io.github.sjouwer.blockenforcer.handlers;

import io.github.sjouwer.blockenforcer.utils.NoteBlockUtil;
import net.minecraft.server.v1_14_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.event.player.PlayerInteractEvent;

public class NoteBlockHandler {
    private NoteBlockHandler() {
    }

    public static void forcePlaceNoteBlock(BlockData blockData, NBTTagCompound blockStateTag, PlayerInteractEvent event) {
        if (!(blockData instanceof NoteBlock)) return;

        NoteBlock noteBlock = (NoteBlock) blockData;

        if (blockStateTag != null) {
            String instrument = blockStateTag.getString("instrument").toUpperCase();
            if (!instrument.isEmpty()) noteBlock.setInstrument(NoteBlockUtil.getInstrument(instrument));

            String note = blockStateTag.getString("note");
            if (!note.isEmpty()) noteBlock.setNote(new Note(Integer.parseInt(note)));

            String powered = blockStateTag.getString("powered");
            if (!powered.isEmpty()) noteBlock.setPowered(Boolean.parseBoolean(powered));
        }

        Block placedBlock = BlockPlaceHandler.placeBlock(event, noteBlock);
        if (placedBlock == null) return;

        event.setCancelled(true);
    }

    public static void updateAllAboveNoteBlocks(Block blockAbove) {
        while (blockAbove.getType() == Material.NOTE_BLOCK) {
            blockAbove.getState().update(true, false);
            blockAbove = blockAbove.getRelative(BlockFace.UP, 1);
        }
    }
}
