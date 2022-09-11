package io.github.sjouwer.blockenforcer.handlers;

import io.github.sjouwer.blockenforcer.packets.PacketWorldChunk;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class BiomeHandler {
    private BiomeHandler() {
    }

    public static void changeBiome(Block block, ItemMeta meta, Player player) {
        List<String> loreList = meta.getLore();
        for (String lore : loreList) {
            try {
                Biome biome = Biome.valueOf(lore);
                if (block.getBiome() == biome) {
                    break;
                }

                block.setBiome(biome);
                PacketWorldChunk.send(block.getChunk(), player);
                return;
            }
            catch (IllegalArgumentException e) {
                //The lore wasn't a biome
            }
        }
    }
}
