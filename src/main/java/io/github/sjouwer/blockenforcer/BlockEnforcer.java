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
        config.addDefault("Stop-Tripwire-Updates", true);
        config.addDefault("Stop-Redstone-Updates", true);
        config.addDefault("Max-Redstone-Updates-Per-Tick", 1000);
        config.addDefault("Stop-Door-Updates", true);
        config.addDefault("Stop-Turtle-Egg-Updates", true);
        config.addDefault("Fix-Tool-Desync", true);
        config.addDefault("Override-NoteBlock-Right-Click", true);
        config.addDefault("Stop-Falling-Blocks", true);
        config.addDefault("Disable-Plant-Placement-Rules", true);
        config.addDefault("Enable-Structure-Blocks", true);
        config.addDefault("Disable-Piston-Movement", true);
        config.addDefault("Enable-Direct-Item-Model-Placement", true);
        config.addDefault("Enable-Floating-Item-Frames", true);
        config.options().copyDefaults(true);
        saveConfig();

        Bukkit.getPluginManager().registerEvents(new BlockBreakListener(), BlockEnforcer.plugin);
        Bukkit.getPluginManager().registerEvents(new BlockPhysicsListener(), BlockEnforcer.plugin);
        Bukkit.getPluginManager().registerEvents(new BlockPlaceListener(), BlockEnforcer.plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(), BlockEnforcer.plugin);
        Bukkit.getPluginManager().registerEvents(new PistonListener(), BlockEnforcer.plugin);
        Bukkit.getPluginManager().registerEvents(new HangingListener(), BlockEnforcer.plugin);
    }

    @Override
    public void onDisable() {

    }
}
