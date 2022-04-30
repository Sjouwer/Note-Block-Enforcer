package io.github.sjouwer.noteblockenforcer;

import io.github.sjouwer.noteblockenforcer.events.NoteBlockInteractEvent;
import io.github.sjouwer.noteblockenforcer.events.NoteBlockPhysicsEvent;
import io.github.sjouwer.noteblockenforcer.events.NoteBlockPlaceEvent;
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
        Bukkit.getPluginManager().registerEvents(new NoteBlockPlaceEvent(), NoteBlockEnforcer.plugin);
        Bukkit.getPluginManager().registerEvents(new NoteBlockPhysicsEvent(), NoteBlockEnforcer.plugin);
        Bukkit.getPluginManager().registerEvents(new NoteBlockInteractEvent(), NoteBlockEnforcer.plugin);
    }

    @Override
    public void onDisable() {

    }
}
