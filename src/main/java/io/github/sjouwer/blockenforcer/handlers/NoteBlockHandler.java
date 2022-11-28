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
            noteBlock.setInstrument(NoteBlockUtil.getInstrument(blockStateTag.getString("instrument")));
            noteBlock.setNote(new Note(Integer.parseInt(blockStateTag.getString("note"))));
            noteBlock.setPowered(Boolean.parseBoolean(blockStateTag.getString("powered")));
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
