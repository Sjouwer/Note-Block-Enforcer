package io.github.sjouwer.blockenforcer;

import org.bukkit.Material;

public class Config {
    public static final boolean STOP_NOTE_BLOCK_UPDATES = BlockEnforcer.getPlugin().getConfig().getBoolean("Stop-NoteBlock-Updates");
    public static final boolean STOP_TRIPWIRE_UPDATES = BlockEnforcer.getPlugin().getConfig().getBoolean("Stop-Tripwire-Updates");
    public static final boolean STOP_REDSTONE_UPDATES = BlockEnforcer.getPlugin().getConfig().getBoolean("Stop-Redstone-Updates");
    public static final int MAX_REDSTONE_UPDATES = BlockEnforcer.getPlugin().getConfig().getInt("Max-Redstone-Updates-Per-Tick");
    public static final boolean STOP_DOOR_UPDATES = BlockEnforcer.getPlugin().getConfig().getBoolean("Stop-Door-Updates");
    public static final boolean DISABLE_CHORUS_PLACEMENT_RULES = BlockEnforcer.getPlugin().getConfig().getBoolean("Disable-Plant-Placement-Rules");
    public static final boolean STOP_FALLING_BLOCKS = BlockEnforcer.getPlugin().getConfig().getBoolean("Stop-Falling-Blocks");
    public static final boolean ENABLE_BLOCKSTATE_PICKER = BlockEnforcer.getPlugin().getConfig().getBoolean("Enable-BlockState-Picker");
    public static final boolean DISABLE_PLANT_PLACEMENT_RULES = BlockEnforcer.getPlugin().getConfig().getBoolean("Disable-Plant-Placement-Rules");
    public static final boolean STOP_TURTLE_EGG_UPDATES = BlockEnforcer.getPlugin().getConfig().getBoolean("Stop-Turtle-Egg-Updates");
    public static final boolean OVERRIDE_NOTE_BLOCK_CLICK = BlockEnforcer.getPlugin().getConfig().getBoolean("Override-NoteBlock-Right-Click");
    public static final boolean FIX_TOOL_DESYNC = BlockEnforcer.getPlugin().getConfig().getBoolean("Fix-Tool-Desync");
    public static final boolean ENABLE_STRUCTURE_BLOCKS = BlockEnforcer.getPlugin().getConfig().getBoolean("Enable-Structure-Blocks");
    public static final boolean DISABLE_PISTON_MOVEMENT = BlockEnforcer.getPlugin().getConfig().getBoolean("Disable-Piston-Movement");
    public static final boolean ENABLE_DIRECT_ITEM_MODEL_PLACEMENT = BlockEnforcer.getPlugin().getConfig().getBoolean("Enable-Direct-Item-Model-Placement");
    public static final boolean ENABLE_FLOATING_ITEM_FRAMES = BlockEnforcer.getPlugin().getConfig().getBoolean("Enable-Floating-Item-Frames");

    private static Material pickerTool = Material.BONE;

    static {
        String configTool = BlockEnforcer.getPlugin().getConfig().getString("BlockState-Picker-Tool");
        if (configTool != null) {
            Material material = Material.getMaterial(configTool.toUpperCase());
            if (material != null) {
                pickerTool = material;
            }
        }
    }

    private Config() {
    }

    public static Material getPickerTool() {
        return pickerTool;
    }
}
