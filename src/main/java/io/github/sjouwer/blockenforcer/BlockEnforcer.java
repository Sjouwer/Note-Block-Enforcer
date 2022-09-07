package io.github.sjouwer.blockenforcer;

import io.github.sjouwer.blockenforcer.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockEnforcer extends JavaPlugin {
    private static JavaPlugin plugin;
    public static JavaPlugin getPlugin() {
        return BlockEnforcer.plugin;
    }

    private final FileConfiguration config = getConfig();

    @Override
    public void onEnable() {
        BlockEnforcer.plugin = this;

        config.addDefault("Enable-BlockState-Picker", true);
        config.addDefault("BlockState-Picker-Tool", "bone");
        config.addDefault("Stop-NoteBlock-Updates", true);
        config.addDefault("Fix_WE_Wand_Desync", true);
        config.addDefault("WE-Wand", "wooden_axe");
        config.addDefault("Override-NoteBlock-Right-Click", true);
        config.addDefault("Stop-Falling-Blocks", true);
        config.addDefault("Disable-Plant-Placement-Rules", true);
        config.options().copyDefaults(true);
        saveConfig();

        Bukkit.getPluginManager().registerEvents(new NoteBlockPlaceEvent(), BlockEnforcer.plugin);
        Bukkit.getPluginManager().registerEvents(new NoteBlockPhysicsEvent(), BlockEnforcer.plugin);
        Bukkit.getPluginManager().registerEvents(new NoteBlockInteractEvent(), BlockEnforcer.plugin);
        Bukkit.getPluginManager().registerEvents(new FallingBlockSpawnEvent(), BlockEnforcer.plugin);
        Bukkit.getPluginManager().registerEvents(new PickBlockStateEvent(), BlockEnforcer.plugin);
        Bukkit.getPluginManager().registerEvents(new TechnicalBlockPlaceEvent(), BlockEnforcer.plugin);
        Bukkit.getPluginManager().registerEvents(new ChorusPhysicsEvent(), BlockEnforcer.plugin);
        Bukkit.getPluginManager().registerEvents(new PlantPlaceEvent(), BlockEnforcer.plugin);
        Bukkit.getPluginManager().registerEvents(new BiomeBlockPlaceEvent(), BlockEnforcer.plugin);
    }

    @Override
    public void onDisable() {

    }
}
