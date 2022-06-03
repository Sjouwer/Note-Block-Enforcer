package io.github.sjouwer.blockenforcer.listeners;

import io.github.sjouwer.blockenforcer.packets.PacketWorldChunk;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class BiomeBlockPlaceEvent implements Listener {
    @EventHandler
    public void changeBiome(BlockPlaceEvent event) {
        ItemMeta meta = event.getItemInHand().getItemMeta();
        if (meta == null || !meta.hasLore()) {
            return;
        }

        Block block = event.getBlockPlaced();
        List<String> loreList = meta.getLore();
        for (String lore : loreList) {
            try {
                Biome biome = Biome.valueOf(lore);
                if (block.getBiome() == biome) {
                    break;
                }

                block.setBiome(biome);
                PacketWorldChunk.send(block.getChunk(), event.getPlayer());
                return;
            }
            catch (IllegalArgumentException e) {
                //The lore wasn't a biome
            }
        }

    }

}
