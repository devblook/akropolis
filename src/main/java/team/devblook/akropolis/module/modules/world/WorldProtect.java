/*
 * This file is part of Akropolis
 *
 * Copyright (c) 2024 DevBlook Team and others
 *
 * Akropolis free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Akropolis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Akropolis. If not, see <http://www.gnu.org/licenses/>.
 */

package team.devblook.akropolis.module.modules.world;

import com.cryptomorin.xseries.XMaterial;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import team.devblook.akropolis.AkropolisPlugin;
import team.devblook.akropolis.Permissions;
import team.devblook.akropolis.config.ConfigType;
import team.devblook.akropolis.config.Message;
import team.devblook.akropolis.cooldown.CooldownType;
import team.devblook.akropolis.module.Module;
import team.devblook.akropolis.module.ModuleType;
import team.devblook.akropolis.module.modules.hologram.Hologram;

import java.util.Set;

@SuppressWarnings({"deprecation", "ConstantConditions"})
public class WorldProtect extends Module {
    private boolean hungerLoss;
    private boolean fallDamage;
    private boolean weatherChange;
    private boolean deathMessage;
    private boolean fireSpread;
    private boolean leafDecay;
    private boolean mobSpawning;
    private boolean blockBurn;
    private boolean voidDeath;
    private boolean itemDrop;
    private boolean itemPickup;
    private boolean blockBreak;
    private boolean blockPlace;
    private boolean blockInteract;
    private boolean playerPvP;
    private boolean playerDrowning;
    private boolean fireDamage;

    private static final Set<Material> INTERACTABLE;

    static {
        INTERACTABLE = Set.of(XMaterial.ANVIL.parseMaterial(),
                XMaterial.BLACK_BED.parseMaterial(), XMaterial.BLUE_BED.parseMaterial(),
                XMaterial.BROWN_BED.parseMaterial(), XMaterial.CYAN_BED.parseMaterial(),
                XMaterial.GRAY_BED.parseMaterial(), XMaterial.GREEN_BED.parseMaterial(),
                XMaterial.LIGHT_BLUE_BED.parseMaterial(), XMaterial.LIME_BED.parseMaterial(),
                XMaterial.MAGENTA_BED.parseMaterial(), XMaterial.ORANGE_BED.parseMaterial(),
                XMaterial.PINK_BED.parseMaterial(), XMaterial.PURPLE_BED.parseMaterial(),
                XMaterial.RED_BED.parseMaterial(), XMaterial.WHITE_BED.parseMaterial(),
                XMaterial.YELLOW_BED.parseMaterial(), XMaterial.BELL.parseMaterial(),
                XMaterial.BLAST_FURNACE.parseMaterial(), XMaterial.BREWING_STAND.parseMaterial(),
                XMaterial.ACACIA_BUTTON.parseMaterial(), XMaterial.BAMBOO_BUTTON.parseMaterial(),
                XMaterial.BIRCH_BUTTON.parseMaterial(), XMaterial.CHERRY_BUTTON.parseMaterial(),
                XMaterial.CRIMSON_BUTTON.parseMaterial(), XMaterial.DARK_OAK_BUTTON.parseMaterial(),
                XMaterial.JUNGLE_BUTTON.parseMaterial(), XMaterial.MANGROVE_BUTTON.parseMaterial(),
                XMaterial.OAK_BUTTON.parseMaterial(), XMaterial.POLISHED_BLACKSTONE_BUTTON.parseMaterial(),
                XMaterial.SPRUCE_BUTTON.parseMaterial(), XMaterial.STONE_BUTTON.parseMaterial(),
                XMaterial.WARPED_BUTTON.parseMaterial(), XMaterial.CARTOGRAPHY_TABLE.parseMaterial(),
                XMaterial.CAULDRON.parseMaterial(), XMaterial.CHEST.parseMaterial(),
                XMaterial.TRAPPED_CHEST.parseMaterial(), XMaterial.DAYLIGHT_DETECTOR.parseMaterial(),
                XMaterial.CHEST_MINECART.parseMaterial(), XMaterial.COMMAND_BLOCK_MINECART.parseMaterial(),
                XMaterial.FURNACE_MINECART.parseMaterial(), XMaterial.HOPPER_MINECART.parseMaterial(),
                XMaterial.TNT_MINECART.parseMaterial(), XMaterial.COMMAND_BLOCK.parseMaterial(),
                XMaterial.COMPOSTER.parseMaterial(), XMaterial.CRAFTING_TABLE.parseMaterial(),
                XMaterial.ACACIA_DOOR.parseMaterial(), XMaterial.BAMBOO_DOOR.parseMaterial(),
                XMaterial.BIRCH_DOOR.parseMaterial(), XMaterial.CHERRY_DOOR.parseMaterial(),
                XMaterial.COPPER_DOOR.parseMaterial(), XMaterial.CRIMSON_DOOR.parseMaterial(),
                XMaterial.DARK_OAK_DOOR.parseMaterial(), XMaterial.EXPOSED_COPPER_DOOR.parseMaterial(),
                XMaterial.IRON_DOOR.parseMaterial(), XMaterial.JUNGLE_DOOR.parseMaterial(),
                XMaterial.MANGROVE_DOOR.parseMaterial(), XMaterial.OAK_DOOR.parseMaterial(),
                XMaterial.OXIDIZED_COPPER_DOOR.parseMaterial(), XMaterial.SPRUCE_DOOR.parseMaterial(),
                XMaterial.WARPED_DOOR.parseMaterial(), XMaterial.WAXED_COPPER_DOOR.parseMaterial(),
                XMaterial.WAXED_EXPOSED_COPPER_DOOR.parseMaterial(), XMaterial.WAXED_OXIDIZED_COPPER_DOOR.parseMaterial(),
                XMaterial.WAXED_WEATHERED_COPPER_DOOR.parseMaterial(), XMaterial.WEATHERED_COPPER_DOOR.parseMaterial(),
                XMaterial.ENCHANTING_TABLE.parseMaterial(), XMaterial.END_PORTAL_FRAME.parseMaterial(),
                XMaterial.ACACIA_FENCE_GATE.parseMaterial(), XMaterial.BAMBOO_FENCE_GATE.parseMaterial(),
                XMaterial.BIRCH_FENCE_GATE.parseMaterial(), XMaterial.CHERRY_FENCE_GATE.parseMaterial(),
                XMaterial.CRIMSON_FENCE_GATE.parseMaterial(), XMaterial.DARK_OAK_FENCE_GATE.parseMaterial(),
                XMaterial.JUNGLE_FENCE_GATE.parseMaterial(), XMaterial.MANGROVE_FENCE_GATE.parseMaterial(),
                XMaterial.OAK_FENCE_GATE.parseMaterial(), XMaterial.SPRUCE_FENCE_GATE.parseMaterial(),
                XMaterial.WARPED_FENCE_GATE.parseMaterial(), XMaterial.GRINDSTONE.parseMaterial(),
                XMaterial.ITEM_FRAME.parseMaterial(), XMaterial.JUKEBOX.parseMaterial(),
                XMaterial.LECTERN.parseMaterial(), XMaterial.LEVER.parseMaterial(),
                XMaterial.LODESTONE.parseMaterial(), XMaterial.LOOM.parseMaterial(),
                XMaterial.NOTE_BLOCK.parseMaterial(), XMaterial.ACACIA_PRESSURE_PLATE.parseMaterial(),
                XMaterial.BAMBOO_PRESSURE_PLATE.parseMaterial(), XMaterial.BIRCH_PRESSURE_PLATE.parseMaterial(),
                XMaterial.CHERRY_PRESSURE_PLATE.parseMaterial(), XMaterial.CRIMSON_PRESSURE_PLATE.parseMaterial(),
                XMaterial.DARK_OAK_PRESSURE_PLATE.parseMaterial(), XMaterial.HEAVY_WEIGHTED_PRESSURE_PLATE.parseMaterial(),
                XMaterial.JUNGLE_PRESSURE_PLATE.parseMaterial(), XMaterial.LIGHT_WEIGHTED_PRESSURE_PLATE.parseMaterial(),
                XMaterial.MANGROVE_PRESSURE_PLATE.parseMaterial(), XMaterial.OAK_PRESSURE_PLATE.parseMaterial(),
                XMaterial.POLISHED_BLACKSTONE_PRESSURE_PLATE.parseMaterial(),
                XMaterial.SPRUCE_PRESSURE_PLATE.parseMaterial(),
                XMaterial.STONE_PRESSURE_PLATE.parseMaterial(), XMaterial.WARPED_PRESSURE_PLATE.parseMaterial(),
                XMaterial.PUMPKIN.parseMaterial(), XMaterial.RESPAWN_ANCHOR.parseMaterial(),
                XMaterial.SMITHING_TABLE.parseMaterial(), XMaterial.SMOKER.parseMaterial(),
                XMaterial.STONECUTTER.parseMaterial(), XMaterial.TNT.parseMaterial(),
                XMaterial.ACACIA_TRAPDOOR.parseMaterial(), XMaterial.BAMBOO_TRAPDOOR.parseMaterial(),
                XMaterial.BIRCH_TRAPDOOR.parseMaterial(), XMaterial.CHERRY_TRAPDOOR.parseMaterial(),
                XMaterial.COPPER_TRAPDOOR.parseMaterial(), XMaterial.CRIMSON_TRAPDOOR.parseMaterial(),
                XMaterial.DARK_OAK_TRAPDOOR.parseMaterial(), XMaterial.EXPOSED_COPPER_TRAPDOOR.parseMaterial(),
                XMaterial.IRON_TRAPDOOR.parseMaterial(), XMaterial.JUNGLE_TRAPDOOR.parseMaterial(),
                XMaterial.MANGROVE_TRAPDOOR.parseMaterial(), XMaterial.OAK_TRAPDOOR.parseMaterial(),
                XMaterial.OXIDIZED_COPPER_TRAPDOOR.parseMaterial(), XMaterial.SPRUCE_TRAPDOOR.parseMaterial(),
                XMaterial.WARPED_TRAPDOOR.parseMaterial(), XMaterial.WAXED_COPPER_TRAPDOOR.parseMaterial(),
                XMaterial.WAXED_EXPOSED_COPPER_TRAPDOOR.parseMaterial(),
                XMaterial.WAXED_OXIDIZED_COPPER_TRAPDOOR.parseMaterial(),
                XMaterial.WAXED_WEATHERED_COPPER_TRAPDOOR.parseMaterial(),
                XMaterial.WEATHERED_COPPER_TRAPDOOR.parseMaterial(),
                XMaterial.FLOWER_POT.parseMaterial(), XMaterial.PAINTING.parseMaterial(), XMaterial.BEACON.parseMaterial(),
                XMaterial.DISPENSER.parseMaterial(), XMaterial.HOPPER.parseMaterial(), XMaterial.DROPPER.parseMaterial(),
                XMaterial.ENDER_CHEST.parseMaterial(), XMaterial.COMPARATOR.parseMaterial(),
                XMaterial.ACACIA_SIGN.parseMaterial(), XMaterial.ACACIA_WALL_HANGING_SIGN.parseMaterial(),
                XMaterial.ACACIA_WALL_SIGN.parseMaterial(), XMaterial.BAMBOO_HANGING_SIGN.parseMaterial(),
                XMaterial.BAMBOO_SIGN.parseMaterial(), XMaterial.BAMBOO_WALL_HANGING_SIGN.parseMaterial(),
                XMaterial.BAMBOO_WALL_SIGN.parseMaterial(), XMaterial.BIRCH_HANGING_SIGN.parseMaterial(),
                XMaterial.BIRCH_SIGN.parseMaterial(), XMaterial.BIRCH_WALL_HANGING_SIGN.parseMaterial(),
                XMaterial.BIRCH_WALL_SIGN.parseMaterial(), XMaterial.CHERRY_HANGING_SIGN.parseMaterial(),
                XMaterial.CHERRY_SIGN.parseMaterial(), XMaterial.CHERRY_WALL_HANGING_SIGN.parseMaterial(),
                XMaterial.CHERRY_WALL_SIGN.parseMaterial(), XMaterial.CRIMSON_HANGING_SIGN.parseMaterial(),
                XMaterial.CRIMSON_SIGN.parseMaterial(), XMaterial.CRIMSON_WALL_HANGING_SIGN.parseMaterial(),
                XMaterial.CRIMSON_WALL_SIGN.parseMaterial(), XMaterial.DARK_OAK_HANGING_SIGN.parseMaterial(),
                XMaterial.DARK_OAK_SIGN.parseMaterial(), XMaterial.DARK_OAK_WALL_HANGING_SIGN.parseMaterial(),
                XMaterial.DARK_OAK_WALL_SIGN.parseMaterial(), XMaterial.JUNGLE_HANGING_SIGN.parseMaterial(),
                XMaterial.JUNGLE_SIGN.parseMaterial(), XMaterial.JUNGLE_WALL_HANGING_SIGN.parseMaterial(),
                XMaterial.JUNGLE_WALL_SIGN.parseMaterial(), XMaterial.MANGROVE_HANGING_SIGN.parseMaterial(),
                XMaterial.MANGROVE_SIGN.parseMaterial(), XMaterial.MANGROVE_WALL_HANGING_SIGN.parseMaterial(),
                XMaterial.MANGROVE_WALL_SIGN.parseMaterial(), XMaterial.OAK_HANGING_SIGN.parseMaterial(),
                XMaterial.OAK_SIGN.parseMaterial(), XMaterial.OAK_WALL_HANGING_SIGN.parseMaterial(),
                XMaterial.OAK_WALL_SIGN.parseMaterial(), XMaterial.SPRUCE_HANGING_SIGN.parseMaterial(),
                XMaterial.SPRUCE_SIGN.parseMaterial(), XMaterial.SPRUCE_WALL_HANGING_SIGN.parseMaterial(),
                XMaterial.SPRUCE_WALL_SIGN.parseMaterial(), XMaterial.WARPED_HANGING_SIGN.parseMaterial(),
                XMaterial.WARPED_SIGN.parseMaterial(), XMaterial.WARPED_WALL_HANGING_SIGN.parseMaterial(),
                XMaterial.WARPED_WALL_SIGN.parseMaterial());
    }

    public WorldProtect(AkropolisPlugin plugin) {
        super(plugin, ModuleType.WORLD_PROTECT);
    }

    @Override
    public void onEnable() {
        FileConfiguration config = getConfig(ConfigType.SETTINGS);
        hungerLoss = config.getBoolean("world_settings.disable_hunger_loss");
        fallDamage = config.getBoolean("world_settings.disable_fall_damage");
        playerPvP = config.getBoolean("world_settings.disable_player_pvp");
        voidDeath = config.getBoolean("world_settings.disable_void_death");
        weatherChange = config.getBoolean("world_settings.disable_weather_change");
        deathMessage = config.getBoolean("world_settings.disable_death_message");
        mobSpawning = config.getBoolean("world_settings.disable_mob_spawning");
        itemDrop = config.getBoolean("world_settings.disable_item_drop");
        itemPickup = config.getBoolean("world_settings.disable_item_pickup");
        blockBreak = config.getBoolean("world_settings.disable_block_break");
        blockPlace = config.getBoolean("world_settings.disable_block_place");
        blockInteract = config.getBoolean("world_settings.disable_block_interact");
        blockBurn = config.getBoolean("world_settings.disable_block_burn");
        fireSpread = config.getBoolean("world_settings.disable_block_fire_spread");
        leafDecay = config.getBoolean("world_settings.disable_block_leaf_decay");
        playerDrowning = config.getBoolean("world_settings.disable_drowning");
        fireDamage = config.getBoolean("world_settings.disable_fire_damage");
    }

    @Override
    public void onDisable() {
        // TODO: Refactor to follow Liskov Substitution principle.
    }

    @EventHandler
    public void onArmorStandInteract(PlayerArmorStandManipulateEvent event) {
        for (Hologram entry : getPlugin().getHologramManager().getHolograms()) {
            for (ArmorStand stand : entry.getStands()) {
                if (stand.equals(event.getRightClicked())) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!blockBreak || event.isCancelled())
            return;

        Player player = event.getPlayer();
        if (inDisabledWorld(player.getLocation()))
            return;
        if (player.hasPermission(Permissions.EVENT_BLOCK_BREAK.getPermission()))
            return;

        event.setCancelled(true);

        if (tryCooldown(player.getUniqueId(), CooldownType.BLOCK_BREAK, 3)) {
            Component message = Message.EVENT_BLOCK_BREAK.toComponent();

            if (message != Component.empty()) player.sendMessage(message);
        }
    }

    @EventHandler
    public void onBlockPlace(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        if (!blockPlace || event.isCancelled())
            return;

        Player player = event.getPlayer();

        if (inDisabledWorld(player.getLocation()))
            return;

        ItemStack item = event.getItem();

        if (item == null) return;

        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();

        if (container.has(NamespacedKey.minecraft("hotbar-item"), PersistentDataType.STRING)) {
            event.setCancelled(true);
            return;
        }

        if (player.hasPermission(Permissions.EVENT_BLOCK_PLACE.getPermission()))
            return;

        event.setCancelled(true);

        if (tryCooldown(event.getPlayer().getUniqueId(), CooldownType.BLOCK_PLACE, 3)) {
            Component message = Message.EVENT_BLOCK_PLACE.toComponent();

            if (message != Component.empty()) player.sendMessage(message);
        }
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        if (!blockBurn)
            return;

        if (inDisabledWorld(event.getBlock().getLocation()))
            return;

        event.setCancelled(true);
    }

    // Prevent destroying of item frame/paintings
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDestroy(HangingBreakByEntityEvent event) {
        if (!blockBreak || inDisabledWorld(event.getEntity().getLocation()))
            return;

        Entity entity = event.getEntity();
        Entity player = event.getRemover();

        if (entity instanceof Painting || entity instanceof ItemFrame && player instanceof Player) {
            if (player.hasPermission(Permissions.EVENT_BLOCK_BREAK.getPermission()))
                return;

            event.setCancelled(true);

            if (tryCooldown(player.getUniqueId(), CooldownType.BLOCK_BREAK, 3)) {
                Component message = Message.EVENT_BLOCK_BREAK.toComponent();

                if (message != Component.empty()) player.sendMessage(message);
            }
        }
    }

    // Prevent items being rotated in item frame
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        if (!blockInteract || inDisabledWorld(event.getRightClicked().getLocation()))
            return;

        Entity entity = event.getRightClicked();
        Entity player = event.getPlayer();

        if (player.hasPermission(Permissions.EVENT_BLOCK_INTERACT.getPermission()))
            return;

        if (entity instanceof ItemFrame) {
            event.setCancelled(true);

            if (tryCooldown(player.getUniqueId(), CooldownType.BLOCK_INTERACT, 3)) {
                Component message = Message.EVENT_BLOCK_INTERACT.toComponent();

                if (message != Component.empty()) player.sendMessage(message);
            }
        }
    }

    // Prevent items being taken from item frames
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!blockInteract || inDisabledWorld(event.getEntity().getLocation()))
            return;

        Entity entity = event.getEntity();
        Entity damager = event.getDamager();

        if (entity instanceof ItemFrame && damager instanceof Player player) {

            if (player.hasPermission(Permissions.EVENT_BLOCK_INTERACT.getPermission()))
                return;

            event.setCancelled(true);

            if (tryCooldown(player.getUniqueId(), CooldownType.BLOCK_INTERACT, 3)) {
                Component message = Message.EVENT_BLOCK_INTERACT.toComponent();

                if (message != Component.empty()) player.sendMessage(message);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockInteract(PlayerInteractEvent event) {
        if (!blockInteract || inDisabledWorld(event.getPlayer().getLocation()))
            return;

        Player player = event.getPlayer();

        if (player.hasPermission(Permissions.EVENT_BLOCK_INTERACT.getPermission()))
            return;

        Block block = event.getClickedBlock();

        if (block == null)
            return;

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (INTERACTABLE.contains(block.getType()) || block.getType().toString().contains("POTTED")) {
                event.setCancelled(true);

                if (tryCooldown(player.getUniqueId(), CooldownType.BLOCK_INTERACT, 3)) {
                    Component message = Message.EVENT_BLOCK_INTERACT.toComponent();
                    if (message != Component.empty()) player.sendMessage(message);
                }
            }
        } else if (event.getAction() == Action.PHYSICAL && block.getType() == XMaterial.FARMLAND.parseMaterial()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player))
            return;

        if (inDisabledWorld(player.getLocation()))
            return;

        if (fallDamage && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setCancelled(true);
        } else if (playerDrowning && event.getCause() == EntityDamageEvent.DamageCause.DROWNING) {
            event.setCancelled(true);
        } else if (fireDamage && (event.getCause() == EntityDamageEvent.DamageCause.FIRE
                || event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK
                || event.getCause() == EntityDamageEvent.DamageCause.LAVA)) {
            event.setCancelled(true);
        } else if (voidDeath && event.getCause() == EntityDamageEvent.DamageCause.VOID) {
            player.setFallDistance(0.0F);

            Location location = ((LobbySpawn) getPlugin().getModuleManager().getModule(ModuleType.LOBBY)).getLocation();

            if (location == null)
                return;

            Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), () -> player.teleportAsync(location), 3L);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFireSpread(BlockIgniteEvent event) {
        if (!fireSpread)
            return;

        if (inDisabledWorld(event.getBlock().getLocation()))
            return;

        if (event.getCause() == BlockIgniteEvent.IgniteCause.SPREAD)
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onFoodChange(FoodLevelChangeEvent event) {
        if (!hungerLoss)
            return;

        if (!(event.getEntity() instanceof Player player))
            return;

        if (inDisabledWorld(player.getLocation()))
            return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDropEvent(PlayerDropItemEvent event) {
        if (!itemDrop)
            return;

        Player player = event.getPlayer();

        if (inDisabledWorld(player.getLocation()))
            return;

        if (player.hasPermission(Permissions.EVENT_ITEM_DROP.getPermission()))
            return;

        event.setCancelled(true);

        if (tryCooldown(player.getUniqueId(), CooldownType.ITEM_DROP, 3)) {
            Component message = Message.EVENT_ITEM_DROP.toComponent();

            if (message != Component.empty()) player.sendMessage(message);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerPickupEvent(PlayerPickupItemEvent event) {
        if (!itemPickup)
            return;

        Player player = event.getPlayer();

        if (inDisabledWorld(player.getLocation()))
            return;

        if (player.hasPermission(Permissions.EVENT_ITEM_PICKUP.getPermission()))
            return;

        event.setCancelled(true);

        if (tryCooldown(player.getUniqueId(), CooldownType.ITEM_PICKUP, 3)) {
            Component message = Message.EVENT_ITEM_PICKUP.toComponent();

            if (message != Component.empty()) player.sendMessage(message);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLeafDecay(LeavesDecayEvent event) {
        if (!leafDecay)
            return;

        if (inDisabledWorld(event.getBlock().getLocation()))
            return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (!mobSpawning)
            return;

        if (inDisabledWorld(event.getEntity().getLocation()))
            return;

        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM)
            return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWeatherChange(WeatherChangeEvent event) {
        if (inDisabledWorld(event.getWorld()))
            return;

        if (!weatherChange)
            return;

        event.setCancelled(event.toWeatherState());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!deathMessage)
            return;

        if (inDisabledWorld(event.getEntity().getLocation()))
            return;

        event.setDeathMessage(null);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!playerPvP)
            return;

        if (!(event.getEntity() instanceof Player player))
            return;

        if (inDisabledWorld(player.getLocation()))
            return;

        if (event.getDamager().hasPermission(Permissions.EVENT_PLAYER_PVP.getPermission()))
            return;

        event.setCancelled(true);

        if (tryCooldown(player.getUniqueId(), CooldownType.PLAYER_PVP, 3)) {
            Component message = Message.EVENT_PLAYER_PVP.toComponent();

            if (message != Component.empty()) event.getDamager().sendMessage(message);
        }
    }
}
