package io.github.sjouwer.noteblockenforcer;

import io.github.sjouwer.noteblockenforcer.events.NoteBlockInteractEvent;
import io.github.sjouwer.noteblockenforcer.events.NoteBlockPhysicsEvent;
import io.github.sjouwer.noteblockenforcer.events.NoteBlockPlaceEvent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class NoteBlockEnforcer extends JavaPlugin {
    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new NoteBlockPlaceEvent(), this);
        Bukkit.getPluginManager().registerEvents(new NoteBlockPhysicsEvent(), this);
        Bukkit.getPluginManager().registerEvents(new NoteBlockInteractEvent(), this);
    }

    @Override
    public void onDisable() {

    }
}
