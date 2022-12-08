package io.github.sjouwer.blockenforcer.handlers;

import io.github.sjouwer.blockenforcer.utils.BlockUtil;
import io.github.sjouwer.blockenforcer.utils.NoteBlockUtil;
import net.minecraft.server.v1_14_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.inventory.ItemStack;

public class NoteBlockHandler {
    private NoteBlockHandler() {
    }

    public static void forceNoteBlockNBTState(ItemStack item, Block block) {
        if (item.getType() != Material.NOTE_BLOCK) {
            return;
        }

        NBTTagCompound blockStateTag = BlockUtil.getBlockStateTag(item);
        if (blockStateTag != null) {
            NoteBlock noteBlock = (NoteBlock) block.getBlockData();

            String instrument = blockStateTag.getString("instrument");
            if (!instrument.isEmpty()) noteBlock.setInstrument(NoteBlockUtil.getInstrument(instrument));

            String note = blockStateTag.getString("note");
            if (!note.isEmpty()) noteBlock.setNote(new Note(Integer.parseInt(note)));

            String powered = blockStateTag.getString("powered");
            if (!powered.isEmpty()) noteBlock.setPowered(Boolean.parseBoolean(powered));

            block.setBlockData(noteBlock);
            block.getState().update(true, false);
        }
    }

    public static void updateAllAboveNoteBlocks(Block blockAbove) {
        while (blockAbove.getType() == Material.NOTE_BLOCK) {
            blockAbove.getState().update(true, false);
            blockAbove = blockAbove.getRelative(BlockFace.UP, 1);
        }
    }
}
