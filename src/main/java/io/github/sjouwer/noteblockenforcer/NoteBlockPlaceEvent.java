package io.github.sjouwer.noteblockenforcer;

import net.minecraft.server.v1_14_R1.NBTTagCompound;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class NoteBlockPlaceEvent implements Listener
{
    @EventHandler
    public void forceNoteBlockNBTState(BlockPlaceEvent event)
    {
        Block block = event.getBlockPlaced();
        if(!(block.getType().equals(Material.NOTE_BLOCK))) {
            return;
        }

        ItemStack item = event.getItemInHand();
        if (!item.getType().equals(Material.NOTE_BLOCK)) {
            return;
        }

        net.minecraft.server.v1_14_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
        if (nmsStack.hasTag()) {
            NBTTagCompound compound = nmsStack.getTag();
            NBTTagCompound blockStateTag = compound.getCompound("BlockStateTag");
            if (!blockStateTag.isEmpty()) {
                NoteBlock nBlock = (NoteBlock) block.getBlockData();
                nBlock.setInstrument(getInstrument(blockStateTag.getString("instrument")));
                nBlock.setNote(new Note(Integer.parseInt(blockStateTag.getString("note"))));
                nBlock.setPowered(Boolean.parseBoolean(blockStateTag.getString("powered")));
                block.setBlockData(nBlock);
                block.getState().update(true);
            }
        }
    }

    public Instrument getInstrument(String nbtInstrument) {
        switch (nbtInstrument) {
            case "BASEDRUM": return Instrument.BASS_DRUM;
            case "SNARE": return Instrument.SNARE_DRUM;
            case "HAT": return Instrument.STICKS;
            case "BASS": return Instrument.BASS_GUITAR;
            case "FLUTE": return Instrument.FLUTE;
            case "BELL": return Instrument.BELL;
            case "GUITAR": return Instrument.GUITAR;
            case "CHIME": return Instrument.CHIME;
            case "XYLOPHONE": return Instrument.XYLOPHONE;
            case "IRON_XYLOPHONE": return Instrument.IRON_XYLOPHONE;
            case "COW_BELL": return Instrument.COW_BELL;
            case "DIDGERIDOO": return Instrument.DIDGERIDOO;
            case "BIT": return Instrument.BIT;
            case "BANJO": return Instrument.BANJO;
            case "PLING": return Instrument.PLING;
            default: return Instrument.PIANO;
        }
    }
}
