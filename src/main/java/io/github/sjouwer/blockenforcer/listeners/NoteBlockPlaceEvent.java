package io.github.sjouwer.blockenforcer.listeners;

import io.github.sjouwer.blockenforcer.utils.BlockUtil;
import io.github.sjouwer.blockenforcer.utils.NoteBlockUtil;
import net.minecraft.server.v1_14_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class NoteBlockPlaceEvent implements Listener {
    @EventHandler
    public void forceNoteBlockNBTState(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        ItemStack item = event.getItemInHand();
        if (block.getType() != Material.NOTE_BLOCK || item.getType() != Material.NOTE_BLOCK) {
            return;
        }

        NBTTagCompound blockStateTag = BlockUtil.getBlockStateTag(item);
        if (blockStateTag != null) {
            NoteBlock noteBlock = (NoteBlock) block.getBlockData();
            noteBlock.setInstrument(NoteBlockUtil.getInstrument(blockStateTag.getString("instrument")));
            noteBlock.setNote(new Note(Integer.parseInt(blockStateTag.getString("note"))));
            noteBlock.setPowered(Boolean.parseBoolean(blockStateTag.getString("powered")));
            block.setBlockData(noteBlock);
            block.getState().update(true);
        }
    }
}
