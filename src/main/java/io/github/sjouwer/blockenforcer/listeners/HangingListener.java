package io.github.sjouwer.blockenforcer.listeners;

import io.github.sjouwer.blockenforcer.Config;
import io.github.sjouwer.blockenforcer.utils.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class HangingListener implements Listener {
    //ToDo: Needs to be made configurable
    private static final Map<Material, List<String>> itemModelMap = new EnumMap<>(Material.class);

    static {
        List<String> emptyList = new ArrayList<>();

        itemModelMap.put(Material.APPLE, emptyList);
        itemModelMap.put(Material.COOKED_CHICKEN, emptyList);
        itemModelMap.put(Material.EGG, emptyList);
        itemModelMap.put(Material.SALMON, emptyList);
        itemModelMap.put(Material.MUSIC_DISC_11, emptyList);
        itemModelMap.put(Material.MUSIC_DISC_13, emptyList);
        itemModelMap.put(Material.MUSIC_DISC_BLOCKS, emptyList);
        itemModelMap.put(Material.MUSIC_DISC_CAT, emptyList);
        itemModelMap.put(Material.MUSIC_DISC_CHIRP, emptyList);
        itemModelMap.put(Material.MUSIC_DISC_FAR, emptyList);
        itemModelMap.put(Material.MUSIC_DISC_MALL, emptyList);
        itemModelMap.put(Material.MUSIC_DISC_MELLOHI, emptyList);
        itemModelMap.put(Material.MUSIC_DISC_STAL, emptyList);
        itemModelMap.put(Material.MUSIC_DISC_STRAD, emptyList);
        itemModelMap.put(Material.MUSIC_DISC_WAIT, emptyList);
        itemModelMap.put(Material.MUSIC_DISC_WARD, emptyList);
        itemModelMap.put(Material.BREAD, Arrays.asList("white bread crate", "french bread crate", "rye bread crate", "empty crate"));
        itemModelMap.put(Material.POTATO, Arrays.asList("potato crate", "tomato crate", "empty crate"));
        itemModelMap.put(Material.CAKE, Arrays.asList("sweets case", "sweets case 2", "sweets case 3", "sweets case empty", "cupcakes", "doughnuts", "white cake", "white cake slice", "cake slice"));
        itemModelMap.put(Material.OAK_SIGN, Arrays.asList("sign airport", "sign bend to left", "sign bend to right", "sign bike parking", "sign crossroads", "sign cycle path", "sign hump", "sign marina", "sign max speed 30", "sign max speed 50", "sign max speed 70", "sign merge", "sign minimum speed", "sign min speed 20", "sign min speed 30", "sign min speed 50", "sign motorway", "sign no cycling", "sign no entry", "sign no motor vehicles", "sign no overtaking", "sign no parking", "sign no parking alt", "sign no stop", "sign no turn left", "sign no turn right", "sign one way traffic", "sign parking", "sign pedestrian crossing", "sign pedestrian only", "sign road narrows", "sign road narrows left", "sign road narrows right", "sign road works", "sign roundabout", "sign stop", "sign turn left", "sign turn right", "sign two way traffic", "sign wild animals", "sign wild chonk", "sign yield", "sign checkout left", "sign checkout right", "sign exit left", "sign exit right", "sign fire extinguisher", "sign restroom", "sign restroom men", "sign restroom women", "sign we're closed", "sign we're open"));
        itemModelMap.put(Material.END_ROD, Arrays.asList("umbrella", "black", "blue", "green", "red", "white", "gas pump", "gas pump display"));
        itemModelMap.put(Material.PAINTING, Arrays.asList("graffiti 1", "poster environmental", "poster movie", "poster various", "flag asexual", "flag bisexual", "flag gay", "flag lesbian", "flag non-binary", "flag pansexual", "flag trans"));
        itemModelMap.put(Material.DEAD_FIRE_CORAL, Arrays.asList("billiard table1", "billiard table2", "coffee table", "table1", "table2", "table3", "table4"));
        itemModelMap.put(Material.DEAD_BRAIN_CORAL_FAN, Arrays.asList("classroom chair", "billiard table2", "modern chair1 black", "modern chair1 white", "modern chair2 black", "modern chair2 white", "outdoor chair", "seat"));
        itemModelMap.put(Material.DEAD_BUBBLE_CORAL, Arrays.asList("11up bottle", "beer bottle", "cola bottle", "cola pack", "oil and shakers", "jars", "orange bottle", "tall glasses", "wine glass", "two wine glasses", "wine glasses"));
        itemModelMap.put(Material.DEAD_TUBE_CORAL_FAN, Arrays.asList("console"));
        itemModelMap.put(Material.DEAD_HORN_CORAL_FAN, Arrays.asList("mailbox multi"));
        itemModelMap.put(Material.GRINDSTONE, Arrays.asList("big cart wheel", "bike wheel", "boat steering wheel", "cart_wheel", "life ring", "skateboard", "small wooden wheel", "steering wheel"));
        itemModelMap.put(Material.BIRCH_BUTTON, Arrays.asList("black round handle left", "black round handle right", "gold round handle left", "gold round handle right", "silver round handle left", "silver round handle right"));
        itemModelMap.put(Material.FLOWER_POT, Arrays.asList("clay plant pot", "clay plant pots", "tea plants"));
        itemModelMap.put(Material.BREWING_STAND, Arrays.asList("construction barrier"));
        itemModelMap.put(Material.IRON_SWORD, Arrays.asList("cutting board", "knife block"));
        itemModelMap.put(Material.LEATHER, Arrays.asList("faux deer mount", "faux deer mount no antlers", "faux wolf mount"));
        itemModelMap.put(Material.COMPARATOR, Arrays.asList("drawing tablet", "monitor hanging"));
        itemModelMap.put(Material.FLINT_AND_STEEL, Arrays.asList("fire extinguisher", "fire extinguisher in box"));
        itemModelMap.put(Material.HOPPER, Arrays.asList("handbag"));
        itemModelMap.put(Material.FURNACE, Arrays.asList("induction stove top"));
        itemModelMap.put(Material.NETHER_STAR, Arrays.asList("Check palettes!"));
        itemModelMap.put(Material.ACACIA_PRESSURE_PLATE, Arrays.asList("mug rack black", "mug rack glass", "mug rack white", "tea set black white", "tea set black", "tea set white", "teapot black", "teapot white"));
    }

    @EventHandler()
    public void onHangingBreak(HangingBreakEvent event) {
        if (!Config.ENABLE_FLOATING_ITEM_FRAMES) return;

        switch(event.getCause()) {
            case PHYSICS:
            case OBSTRUCTION:
            case EXPLOSION:
                event.setCancelled(true);
                break;
            default:
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onHangingDamage(EntityDamageByEntityEvent event) {
        if (!Config.ENABLE_DIRECT_ITEM_MODEL_PLACEMENT) return;

        if (event.getEntity() instanceof ItemFrame && event.getDamager() instanceof Player) {
            ItemFrame itemFrame = (ItemFrame) event.getEntity();

            Material itemInHand = ((Player) event.getDamager()).getInventory().getItemInMainHand().getType();
            ItemStack itemFrameItem = itemFrame.getItem();
            if (itemInHand.equals(Material.AIR) || isItemModel(itemFrameItem)) {
                itemFrame.remove();
            }
        }
    }

    @EventHandler
    public void onHangingPlace(PlayerInteractEvent event) {
        if (!Config.ENABLE_DIRECT_ITEM_MODEL_PLACEMENT) return;

        if (event.hasBlock() && event.hasItem() && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            ItemStack item = event.getItem();

            if (!isItemModel(item)) return;

            Block block = event.getClickedBlock();
            Player player = event.getPlayer();
            BlockFace face = event.getBlockFace();

            if (ItemUtil.isMusicDisc(item) && block.getType().equals(Material.JUKEBOX) && !player.isSneaking()) return;

            try {
                ItemFrame itemFrame = player.getWorld().spawn(block.getRelative(face).getLocation(), ItemFrame.class, frame -> {
                    frame.setFacingDirection(face);
                    frame.setItem(item);
                });

                HangingPlaceEvent hangingPlaceEvent = new HangingPlaceEvent(itemFrame, player, block, face);
                Bukkit.getPluginManager().callEvent(hangingPlaceEvent);

                if (hangingPlaceEvent.isCancelled()) {
                    itemFrame.remove();
                    return;
                }

                event.setCancelled(true);
            }
            catch (IllegalArgumentException e) {
                //No free face
            }
        }
    }

    private boolean isItemModel(ItemStack item) {
        if (!itemModelMap.containsKey(item.getType())) return false;

        List<String> itemNames = itemModelMap.get(item.getType());
        if (itemNames.isEmpty()) return true;

        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) return false;

        String itemName = itemMeta.getDisplayName().toLowerCase();
        return itemNames.contains(itemName);
    }
}
