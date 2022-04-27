package io.github.sjouwer.noteblockenforcer;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class NoteBlockEnforcer extends JavaPlugin {
    private static JavaPlugin plugin;
    public static JavaPlugin getPlugin() {
        return NoteBlockEnforcer.plugin;
    }

    @Override
    public void onEnable() {
        NoteBlockEnforcer.plugin = this;

        Bukkit.getPluginManager().registerEvents(new NoteBlockPlaceEvent(), NoteBlockEnforcer.getPlugin());
    }

    @Override
    public void onDisable() {

    }
}
