package io.github.sjouwer.blockenforcer.utils;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.bukkit.Material;

public class FlowerPotUtil {
    private static final BiMap<String, Material> flowerPotBiMap = HashBiMap.create();

    static {
        flowerPotBiMap.put("Potted Dandelion", Material.POTTED_DANDELION);
        flowerPotBiMap.put("Potted Poppy", Material.POTTED_POPPY);
        flowerPotBiMap.put("Potted Blue Orchid", Material.POTTED_BLUE_ORCHID);
        flowerPotBiMap.put("Potted Allium", Material.POTTED_ALLIUM);
        flowerPotBiMap.put("Potted Azure Bluet", Material.POTTED_AZURE_BLUET);
        flowerPotBiMap.put("Potted Red Tulip", Material.POTTED_RED_TULIP);
        flowerPotBiMap.put("Potted Orange Tulip", Material.POTTED_ORANGE_TULIP);
        flowerPotBiMap.put("Potted White Tulip", Material.POTTED_WHITE_TULIP);
        flowerPotBiMap.put("Potted Pink Tulip", Material.POTTED_PINK_TULIP);
        flowerPotBiMap.put("Potted Oxeye Daisy", Material.POTTED_OXEYE_DAISY);
        flowerPotBiMap.put("Potted Cornflower", Material.POTTED_CORNFLOWER);
        flowerPotBiMap.put("Potted Lily of the Valley", Material.POTTED_LILY_OF_THE_VALLEY);
        flowerPotBiMap.put("Potted Wither Rose", Material.POTTED_WITHER_ROSE);
        flowerPotBiMap.put("Potted Oak Sapling", Material.POTTED_OAK_SAPLING);
        flowerPotBiMap.put("Potted Spruce Sapling", Material.POTTED_SPRUCE_SAPLING);
        flowerPotBiMap.put("Potted Birch Sapling", Material.POTTED_BIRCH_SAPLING);
        flowerPotBiMap.put("Potted Jungle Sapling", Material.POTTED_JUNGLE_SAPLING);
        flowerPotBiMap.put("Potted Acacia Sapling", Material.POTTED_ACACIA_SAPLING);
        flowerPotBiMap.put("Potted Dark Oak Sapling", Material.POTTED_DARK_OAK_SAPLING);
        flowerPotBiMap.put("Potted Red Mushroom", Material.POTTED_RED_MUSHROOM);
        flowerPotBiMap.put("Potted Brown Mushroom", Material.POTTED_BROWN_MUSHROOM);
        flowerPotBiMap.put("Potted Fern", Material.POTTED_FERN);
        flowerPotBiMap.put("Potted Dead Bush", Material.POTTED_DEAD_BUSH);
        flowerPotBiMap.put("Potted Cactus", Material.POTTED_CACTUS);
        flowerPotBiMap.put("Potted Bamboo", Material.POTTED_BAMBOO);
    }

    private FlowerPotUtil() {
    }

    public static Material convertToMaterial(String potName) {
        return flowerPotBiMap.getOrDefault(potName, Material.FLOWER_POT);
    }

    public static String convertToName(Material potMaterial) {
        return flowerPotBiMap.inverse().getOrDefault(potMaterial, "Flower Pot");
    }
}
